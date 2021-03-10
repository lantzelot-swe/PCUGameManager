package se.lantz.manager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import se.lantz.model.MainViewModel;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class ImportManager
{
  /**
   * The size of each chunk when importing games
   */
  public static final int DB_ROW_CHUNK_SIZE = 50;

  public enum Options
  {
    SKIP, OVERWRITE, ADD;
  }

  private static final Logger logger = LoggerFactory.getLogger(ImportManager.class);

  Path srcParentFolder;
  Path srcCoversFolder;
  Path srcGamesFolder;
  Path srcScreensFolder;

  Map<Path, List<String>> gameInfoFilesMap = new HashMap<>();
  List<String> dbRowDataList = new ArrayList<>();

  private MainViewModel uiModel;
  private Options selectedOption;
  private boolean addAsFavorite = false;

  public ImportManager(MainViewModel uiModel)
  {
    this.uiModel = uiModel;
  }

  public void setSelectedOption(Options option)
  {
    this.selectedOption = option;
  }

  public void setAddAsFavorite(boolean favorite)
  {
    this.addAsFavorite = favorite;
  }

  public void setSelectedFolder(Path folder)
  {
    //Assume games subdirectory
    srcParentFolder = folder.resolve("games");
    srcCoversFolder = srcParentFolder.resolve("covers");
    srcGamesFolder = srcParentFolder.resolve("games");
    srcScreensFolder = srcParentFolder.resolve("screens");

    if (!(Files.exists(srcParentFolder, LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(srcCoversFolder, LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(srcGamesFolder, LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(srcScreensFolder, LinkOption.NOFOLLOW_LINKS)))
    {
      //No games subdirectory
      srcParentFolder = folder;
      srcCoversFolder = folder.resolve("covers");
      srcGamesFolder = folder.resolve("games");
      srcScreensFolder = folder.resolve("screens");
    }
  }

  public void readGameInfoFiles(StringBuilder infoBuilder)
  {
    gameInfoFilesMap.clear();

    try (Stream<Path> filePathStream = Files.walk(srcParentFolder, 1))
    {
      filePathStream.forEach(filePath -> readGameInfoFile(filePath, infoBuilder));
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not read gameInfo files");
    }
  }

  private void readGameInfoFile(Path filePath, StringBuilder infoBuilder)
  {
    if (Files.isRegularFile(filePath) && getFileExtension(filePath).equalsIgnoreCase("tsg"))
    {
      infoBuilder.append("Reading game info from ");
      infoBuilder.append(filePath);
      infoBuilder.append("\n");
      List<String> result = readFileInList(filePath, infoBuilder);
      if (result.size() > 0)
      {
        gameInfoFilesMap.put(filePath, result);
      }
      else
      {
        infoBuilder.append("Skipping file " + filePath.toString() + "\n");
      }
    }
  }

  private String getFileExtension(Path path)
  {
    String fileName = path.toString();
    int dotIndex = fileName.lastIndexOf('.');
    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
  }

  public void convertIntoDbRows(StringBuilder infoBuilder)
  {
    // Construct a List of comma separated strings with all info correctly added.
    gameInfoFilesMap.values().stream().forEach(list -> extractInfoIntoRowString(list, infoBuilder));
  }

  public StringBuilder insertRowsIntoDb(List<String> rowList)
  {
    return uiModel.importGameInfo(rowList, selectedOption, addAsFavorite);
  }

  private void extractInfoIntoRowString(List<String> fileLines, StringBuilder infoBuilder)
  {
    String title = "";
    String year = "";
    String author = "";
    String composer = "";
    String genre = "";
    String description = "";
    String description_de = "";
    String description_fr = "";
    String description_es = "";
    String description_it = "";
    String gamefile = "";
    String coverfile = "";
    String screen1file = "";
    String screen2file = "";
    String joy1config = "";
    String joy2config = "";
    String advanced = "";
    String verticalShift = "0";
    //Add info about old file names so that they can be copied properly
    String oldGameFile = "";
    String oldCoverFile = "";
    String oldScreen1File = "";
    String oldScreen2File = "";

    try
    {
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
        else if (line.startsWith("D:en:"))
        {
          description = line.replace("\"", "\"\"").substring(5);
        }
        else if (line.startsWith("D:de:"))
        {
          description_de = line.replace("\"", "\"\"").substring(5);
        }
        else if (line.startsWith("D:fr:"))
        {
          description_fr = line.replace("\"", "\"\"").substring(5);
        }
        else if (line.startsWith("D:es:"))
        {
          description_es = line.replace("\"", "\"\"").substring(5);
        }
        else if (line.startsWith("D:it:"))
        {
          description_it = line.replace("\"", "\"\"").substring(5);
        }
        else if (line.startsWith("F:"))
        {
          oldGameFile = line.substring(2);
          if (oldGameFile.lastIndexOf("/") > -1)
          {
            oldGameFile = oldGameFile.substring(oldGameFile.lastIndexOf("/") + 1);
          }
        }
        else if (line.startsWith("C:"))
        {
          oldCoverFile = line.substring(2);
          if (oldCoverFile.lastIndexOf("/") > -1)
          {
            oldCoverFile = oldCoverFile.substring(oldCoverFile.lastIndexOf("/") + 1);
          }
        }
        else if (line.startsWith("G:"))
        {
          if (oldScreen1File.isEmpty())
          {
            oldScreen1File = line.substring(2);
            if (oldScreen1File.lastIndexOf("/") > -1)
            {
              oldScreen1File = oldScreen1File.substring(oldScreen1File.lastIndexOf("/") + 1);
            }
          }
          else
          {
            oldScreen2File = line.substring(2);
            if (oldScreen2File.lastIndexOf("/") > -1)
            {
              oldScreen2File = oldScreen2File.substring(oldScreen2File.lastIndexOf("/") + 1);
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
          //The maxi game tool sets the flag for accurate disk to "truedrive". This is not correct according
          //to the The64 manual. Convert to the correct "accuratedisk"
          if (advanced.contains("truedrive"))
          {
            advanced = advanced.replace("truedrive", "accuratedisk");
          }
        }
        else if (line.startsWith("V:"))
        {
          verticalShift = line.substring(2);
        }
      }

      //Check if other languages are the same as english. If that's the case don't import it, leave it empty.
      if (description_de.equals(description))
      {
        description_de = "";
      }
      if (description_fr.equals(description))
      {
        description_fr = "";
      }
      if (description_es.equals(description))
      {
        description_es = "";
      }
      if (description_it.equals(description))
      {
        description_it = "";
      }
      //Generate proper names for files
      int duplicateIndex = getDuplicateIndexForImportedGame(title);
      String fileName = FileManager.generateFileNameFromTitle(title, duplicateIndex);
      coverfile = fileName + "-cover.png";
      screen1file = fileName + "-00.png";
      screen2file = fileName + "-01.png";
      String fileEnding = oldGameFile.substring(oldGameFile.indexOf("."));
      gamefile = fileName + fileEnding;

      // Construct a data row
      addToDbRowList(title,
                     year,
                     author,
                     composer,
                     genre,
                     description,
                     description_de,
                     description_fr,
                     description_es,
                     description_it,
                     gamefile,
                     coverfile,
                     screen1file,
                     screen2file,
                     joy1config,
                     joy2config,
                     advanced,
                     verticalShift,
                     oldCoverFile,
                     oldScreen1File,
                     oldScreen2File,
                     oldGameFile,
                     duplicateIndex);
    }
    catch (Exception e)
    {
      infoBuilder.append("ERROR: Could not read info file for \"" + title + "\"\n");
      logger.error("IMPORT: Could not read info file for " + title, e);
    }
  }

  public void addFromGamebaseImporter(String title,
                                      String year,
                                      String author,
                                      String composer,
                                      String genre,
                                      String gamefile,
                                      String coverfile,
                                      String screen1file,
                                      String screen2file,
                                      String joy1config,
                                      String joy2config,
                                      String advanced,
                                      boolean isC64)
  {
    //Generate proper names for files
    int duplicateIndex = getDuplicateIndexForImportedGame(title);
    String fileName = FileManager.generateFileNameFromTitle(title, duplicateIndex);
    String newCoverfile = fileName + "-cover.png";
    String newScreen1file = fileName + "-00.png";
    String newScreen2file = fileName + "-01.png";
    //Ignore first "." when finding file ending
    String strippedGameFile = gamefile.substring(1);
    String fileEnding = strippedGameFile.substring(strippedGameFile.indexOf("."));
    if (!isC64 && fileEnding.contains(".crt"))
    {
      //A Vic-20 cartridge. Add the flag indicating the cartridge type to the name
      fileEnding = strippedGameFile.substring(strippedGameFile.indexOf("-"));
    }
    String newGamefile = fileName + fileEnding;
    addToDbRowList(title,
                   year,
                   author,
                   composer,
                   genre,
                   "",
                   "",
                   "",
                   "",
                   "",
                   newGamefile,
                   newCoverfile,
                   newScreen1file,
                   newScreen2file,
                   joy1config,
                   joy2config,
                   advanced,
                   "0",
                   coverfile,
                   screen1file,
                   screen2file,
                   gamefile,
                   duplicateIndex);
  }

  private void addToDbRowList(String title,
                              String year,
                              String author,
                              String composer,
                              String genre,
                              String description,
                              String description_de,
                              String description_fr,
                              String description_es,
                              String description_it,
                              String gamefile,
                              String coverfile,
                              String screen1file,
                              String screen2file,
                              String joy1config,
                              String joy2config,
                              String advanced,
                              String verticalShift,
                              String oldCoverFile,
                              String oldScreen1File,
                              String oldScreen2File,
                              String oldGameFile,
                              int duplicateIndex)
  {
    List<String> list = Arrays.asList(title,
                                      year,
                                      author,
                                      composer,
                                      genre,
                                      description,
                                      description_de,
                                      description_fr,
                                      description_es,
                                      description_it,
                                      gamefile,
                                      coverfile,
                                      screen1file,
                                      screen2file,
                                      joy1config,
                                      joy2config,
                                      advanced,
                                      verticalShift,
                                      oldCoverFile,
                                      oldScreen1File,
                                      oldScreen2File,
                                      oldGameFile);
    String result = String.join("\",\"", list);
    //Add duplicateIndex so that it can be added properly when importing
    result = "\"" + result + "\"," + Integer.toString(duplicateIndex);
    dbRowDataList.add(result);
  }

  private int getDuplicateIndexForImportedGame(String title)
  {
    int duplicateIndex = 0;
    if (selectedOption == Options.ADD)
    {
      duplicateIndex = uiModel.getDbConnector().getGameDuplicateIndexToUse(title);
    }
    //Check any duplicates in added rows, always use this otherwise duplicates uses same names.
    duplicateIndex = duplicateIndex + getDbRowDuplicate(title);
    return duplicateIndex;
  }

  private int getDbRowDuplicate(String title)
  {
    int returnValue = 0;
    for (String dbRow : dbRowDataList)
    {
      if (dbRow.startsWith("\"" + title + "\","))
      {
        returnValue++;
      }
    }
    return returnValue;
  }

  private List<String> readFileInList(Path filePath, StringBuilder infoBuilder)
  {
    List<String> lines = Collections.emptyList();
    try
    {
      lines = Files.readAllLines(filePath, StandardCharsets.ISO_8859_1);
    }
    catch (IOException e)
    {
      infoBuilder.append("ERROR: Could not read file: filepath=" + filePath.toString() + ", " + e.getMessage() + "\n");
      ExceptionHandler.handleException(e, "Could not read file: filepath=" + filePath.toString());
    }
    return lines;
  }

  public List<List<String>> getDbRowReadChunks()
  {
    return Lists.partition(dbRowDataList, DB_ROW_CHUNK_SIZE);
  }

  public StringBuilder copyFiles(boolean gamebaseImport, List<String> rowList)
  {
    StringBuilder infoBuilder = new StringBuilder();
    //Copy with the existing file names. At export convert to a name that works with the maxi tool if needed.
    rowList.stream().forEach(dbRowData -> copyFiles(dbRowData, infoBuilder, gamebaseImport));
    return infoBuilder;
  }

  public void copyFiles(String dbRowData, StringBuilder infoBuilder, boolean gamebaseImport)
  {
    String coverName = "";
    String screen1Name = "";
    String screen2Name = "";
    String gameName = "";
    String oldCoverName = "";
    String oldScreen1Name = "";
    String oldScreen2Name = "";
    String oldGameName = "";

    String[] splittedForPaths = dbRowData.split("\",\"");

    gameName = splittedForPaths[10];
    coverName = splittedForPaths[11];
    screen1Name = splittedForPaths[12];
    screen2Name = splittedForPaths[13];
    //Old names  
    oldCoverName = splittedForPaths[18];
    oldScreen1Name = splittedForPaths[19];
    oldScreen2Name = splittedForPaths[20];
    oldGameName = splittedForPaths[21].split("\"")[0];

    Path coverPath = srcCoversFolder.resolve(oldCoverName);
    Path targetCoverPath = Paths.get("./covers/" + coverName);

    Path screens1Path = srcScreensFolder.resolve(oldScreen1Name);
    Path targetScreen1Path = Paths.get("./screens/" + screen1Name);

    Path screens2Path = srcScreensFolder.resolve(oldScreen2Name);
    Path targetScreen2Path = Paths.get("./screens/" + screen2Name);

    Path gamePath = srcGamesFolder.resolve(oldGameName);
    if (gamebaseImport)
    {
      //When importing from gamebase use current folder
      gamePath = new File(oldGameName).toPath();
    }

    Path targetGamePath = Paths.get("./games/" + gameName);

    try
    {
      logger.debug("RowData = {}", dbRowData);

      if (!oldCoverName.isEmpty())
      {
        infoBuilder.append("Copying cover from ");
        infoBuilder.append(coverPath.toString());
        infoBuilder.append(" to ");
        infoBuilder.append(targetCoverPath.toString());
        infoBuilder.append("\n");
        Files.copy(coverPath, targetCoverPath, StandardCopyOption.REPLACE_EXISTING);
        if (gamebaseImport)
        {
          FileManager.scaleCoverImageAndSave(targetCoverPath);
        }
      }
      else
      {
        //Use missing cover since none available
        try
        {
          ImageIO.write(FileManager.emptyC64Cover, "png", targetCoverPath.toFile());
        }
        catch (IOException e)
        {
          ExceptionHandler.handleException(e, "Could not store cover");
        }
      }
      if (!oldScreen1Name.isEmpty())
      {
        infoBuilder.append("Copying screenshot from ");
        infoBuilder.append(screens1Path.toString());
        infoBuilder.append(" to ");
        infoBuilder.append(targetScreen1Path.toString());
        infoBuilder.append("\n");
        Files.copy(screens1Path, targetScreen1Path, StandardCopyOption.REPLACE_EXISTING);
        if (gamebaseImport)
        {
          FileManager.scaleScreenshotImageAndSave(targetScreen1Path);
        }
      }
      if (!oldScreen2Name.isEmpty())
      {
        infoBuilder.append("Copying screenshot from ");
        infoBuilder.append(screens2Path.toString());
        infoBuilder.append(" to ");
        infoBuilder.append(targetScreen2Path.toString());
        infoBuilder.append("\n");
        Files.copy(screens2Path, targetScreen2Path, StandardCopyOption.REPLACE_EXISTING);
        if (gamebaseImport)
        {
          FileManager.scaleScreenshotImageAndSave(targetScreen2Path);
        }
      }
      if (!oldGameName.isEmpty())
      {
        infoBuilder.append("Copying game file from ");
        infoBuilder.append(gamePath.toString());
        infoBuilder.append(" to ");
        infoBuilder.append(targetGamePath.toString());
        infoBuilder.append("\n");
        Files.copy(gamePath, targetGamePath, StandardCopyOption.REPLACE_EXISTING);
      }
    }
    catch (IOException e)
    {
      infoBuilder.append("ERROR: Could not copy files for " + gameName + ", " + e.getMessage() + "\n");
      ExceptionHandler.handleException(e, "Could NOT copy files for: " + gameName);
    }
  }

  public int clearAfterImport()
  {
    int size = dbRowDataList.size();
    dbRowDataList.clear();
    gameInfoFilesMap.clear();
    uiModel.cleanupAfterImport();
    return size;
  }
}
