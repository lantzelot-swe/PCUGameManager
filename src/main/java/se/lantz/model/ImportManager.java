package se.lantz.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.util.ExceptionHandler;

public class ImportManager
{
  public enum Options
  {
    SKIP, OVERWRITE;
  };

  private static final Logger logger = LoggerFactory.getLogger(ImportManager.class);

  Path srcParentFolder;
  Path srcCoversFolder;
  Path srcGamesFolder;
  Path srcScreensFolder;

  Map<Path, List<String>> gameInfoFilesMap = new HashMap<>();
  List<String> dbRowDataList = new ArrayList<>();

  private MainViewModel uiModel;
  private Options selectedOption;

  public ImportManager(MainViewModel uiModel)
  {
    this.uiModel = uiModel;
  }

  public void setSelectedOption(Options option)
  {
    this.selectedOption = option;
  }

  public boolean checkSelectedFolder(Path folder)
  {

    logger.debug("Selected folder: {}", folder.toString());

    srcParentFolder = folder.resolve("games");
    srcCoversFolder = srcParentFolder.resolve("covers");
    srcGamesFolder = srcParentFolder.resolve("games");
    srcScreensFolder = srcParentFolder.resolve("screens");

    logger.debug("parent folder: " + srcParentFolder);
    logger.debug("covers folder: " + srcCoversFolder);
    logger.debug("games folder: " + srcGamesFolder);
    logger.debug("screens folder: " + srcScreensFolder);

    // Verify that subfolders are available
    if (Files.exists(srcParentFolder, LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(srcCoversFolder, LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(srcGamesFolder, LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(srcScreensFolder, LinkOption.NOFOLLOW_LINKS))
    {
      logger.debug("A valid directory!");

      return true;
    }
    else
    {
      logger.debug("An ivalid directory!");
      return false;
    }
  }

  public void readGameInfoFiles(StringBuilder infoBuilder)
  {
    gameInfoFilesMap.clear();

    try (Stream<Path> filePathStream = Files.walk(srcParentFolder, 1))
    {
      filePathStream.forEach(filePath -> {
        if (Files.isRegularFile(filePath))
        {
          infoBuilder.append("Reading game info from ");
          infoBuilder.append(filePath);
          infoBuilder.append("\n");
          gameInfoFilesMap.put(filePath, readFileInList(filePath));
        }
      });
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not read gameInfo files");
    }
  }

  public void convertIntoDbRows()
  {
    // Construct a List of comma separated strings with all info correctly added.
    gameInfoFilesMap.values().stream().forEach(fileLines -> extractInfoIntoRowString(fileLines));
  }

  public StringBuilder insertRowsIntoDb()
  {
    return uiModel.importGameInfo(dbRowDataList, selectedOption);
  }

  private void extractInfoIntoRowString(List<String> fileLines)
  {
    String title = "";
    String year = "";
    String author = "";
    String composer = "";
    String genre = "";
    String description = "";
    String gamefile = "";
    String coverfile = "";
    String screen1file = "";
    String screen2file = "";
    String joy1config = "";
    String joy2config = "";
    String advanced = "";

    for (String line : fileLines)
    {
      if (line.startsWith("T:"))
      {
        title = line.substring(2);
      }
      else if (line.startsWith("Y:"))
      {
        year = line.substring(2);
      }
      else if (line.startsWith("A:"))
      {
        author = line.substring(2);
      }
      else if (line.startsWith("M:"))
      {
        composer = line.substring(2);
      }
      else if (line.startsWith("E:"))
      {
        genre = line.substring(2);
      }
      else if (line.startsWith("D:en"))
      {
        description = line.replaceAll("\"", "\"\"").substring(5);
      }
      else if (line.startsWith("F:"))
      {
        gamefile = line.substring(2);
        if (gamefile.lastIndexOf("/") > -1)
        {
          gamefile = gamefile.substring(gamefile.lastIndexOf("/")+1);
        }
      }
      else if (line.startsWith("C:"))
      {
        coverfile = line.substring(2);
        if (coverfile.lastIndexOf("/") > -1)
        {
          coverfile = coverfile.substring(coverfile.lastIndexOf("/")+1);
        }
      }
      else if (line.startsWith("G:"))
      {
        if (screen1file.isEmpty())
        {
          screen1file = line.substring(2);
          if (screen1file.lastIndexOf("/") > -1)
          {
            screen1file = screen1file.substring(screen1file.lastIndexOf("/")+1);
          }
        }
        else
        {
          screen2file = line.substring(2);
          if (screen2file.lastIndexOf("/") > -1)
          {
            screen2file = screen2file.substring(screen2file.lastIndexOf("/")+1);
          }
        }
      }
      else if (line.startsWith("J:1"))
      {
        joy1config = line;
      }
      else if (line.startsWith("J:2"))
      {
        joy2config = line;
      }
      else if (line.startsWith("X:"))
      {
        advanced = line.substring(2);
      }
    }
    // Construct a data row
    List<String> list = Arrays.asList(title,
                                      year,
                                      author,
                                      composer,
                                      genre,
                                      description,
                                      gamefile,
                                      coverfile,
                                      screen1file,
                                      screen2file,
                                      joy1config,
                                      joy2config,
                                      advanced);
    String result = String.join("\",\"", list);
    result = "\"" + result + "\"";
    dbRowDataList.add(result);
  }

  private List<String> readFileInList(Path filePath)
  {
    List<String> lines = Collections.emptyList();
    try
    {
      lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
    }

    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not read files in list");
    }
    return lines;
  }

  public StringBuilder copyFiles()
  {
    StringBuilder infoBuilder = new StringBuilder();
    //Copy with the existing file names. At export convert to a name that works with the maxi tool if needed.
    dbRowDataList.stream().forEach(dbRowData -> copyFiles(dbRowData, infoBuilder));
    return infoBuilder;
  }

  public void copyFiles(String dbRowData, StringBuilder infoBuilder)
  {
      String coverName = "";
      String screen1Name = "";
      String screen2Name = "";
      String gameName = "";
      
      String[] splittedForPaths = dbRowData.split("\",\"");
    
      gameName = splittedForPaths[6];
      coverName = splittedForPaths[7];
      screen1Name = splittedForPaths[8];
      screen2Name = splittedForPaths[9];
      //Copy!
      Path coverPath = srcCoversFolder.resolve(coverName);
      Path targetPath = Paths.get("./covers/" + coverName);
      
      Path screens1Path = srcScreensFolder.resolve(screen1Name);
      Path targetScreen1Path = Paths.get("./screens/" + screen1Name);
      
      Path screens2Path = srcScreensFolder.resolve(screen2Name);
      Path targetScreen2Path = Paths.get("./screens/" + screen2Name);
      
      Path gamePath = srcGamesFolder.resolve(gameName);
      Path targetGamePath = Paths.get("./games/" + gameName);
      
      try
      {
        infoBuilder.append("Copying cover from ");
        infoBuilder.append(coverPath.toString());
        infoBuilder.append("\n");
        logger.debug("RowData = {}", dbRowData);
        Files.copy(coverPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        infoBuilder.append("Copying screenshot from ");
        infoBuilder.append(screens1Path.toString());
        infoBuilder.append("\n");
        Files.copy(screens1Path, targetScreen1Path, StandardCopyOption.REPLACE_EXISTING);
        
        infoBuilder.append("Copying screenshot from ");
        infoBuilder.append(screens2Path.toString());
        infoBuilder.append("\n");
        Files.copy(screens2Path, targetScreen2Path, StandardCopyOption.REPLACE_EXISTING);
        
        infoBuilder.append("Copying game file from ");
        infoBuilder.append(gamePath.toString());
        infoBuilder.append("\n");
        Files.copy(gamePath, targetGamePath, StandardCopyOption.REPLACE_EXISTING);
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not copy files");
      }
  }
  
  public void clearAfterImport()
  {
    dbRowDataList.clear();
    gameInfoFilesMap.clear();
  }
}
