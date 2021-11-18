package se.lantz.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.MainViewModel;
import se.lantz.model.SavedStatesModel;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class SavedStatesManager
{
  private static final Logger logger = LoggerFactory.getLogger(SavedStatesManager.class);
  
  public static final String SAVES = "./saves/";

  private static final String MTA0 = "0.mta";
  private static final String MTA1 = "1.mta";
  private static final String MTA2 = "2.mta";
  private static final String MTA3 = "3.mta";

  private static final String PNG0 = "0.png";
  private static final String PNG1 = "1.png";
  private static final String PNG2 = "2.png";
  private static final String PNG3 = "3.png";

  public static final String VSZ0 = "0.vsz";
  public static final String VSZ1 = "1.vsz";
  public static final String VSZ2 = "2.vsz";
  public static final String VSZ3 = "3.vsz";

  private SavedStatesModel savedStatesModel;
  private MainViewModel model;

  private File exportDir;
  private File importDir;

  private boolean exportOverwrite;
  private boolean importOverwrite;

  private int noFilesCopied = 0;

  /**
   * Map holding available saved states with fileName (subfolder) as key and number of saved states available as value
   * (0-4).
   */
  private Map<String, Integer> savedStatesMap = new HashMap<>();

  public SavedStatesManager(MainViewModel model)
  {
    this.model = model;
    this.savedStatesModel = model.getSavedStatesModel();
    readSavedStatesAndUpdateMap();
  }

  public void saveSavedStates()
  {
    //If the game has been renamed, make sure to rename the saves folder also
    if (model.getInfoModel().isTitleChanged())
    {
      String oldFileName = model.getInfoModel().getOldGamesFile();
      File oldSaveFolder = new File(SAVES + oldFileName);
      if (oldSaveFolder.exists())
      {
        //Rename old folder to new name
        oldSaveFolder.renameTo(new File(SAVES + model.getInfoModel().getGamesFile()));
      }
    }

    String fileName = model.getInfoModel().getGamesFile();
    Path saveFolder = new File(SAVES + fileName).toPath();
    int numberofSaves = 0;
    //Check which ones are available
    Path mta0Path = saveFolder.resolve(MTA0);
    Path vsz0Path = saveFolder.resolve(VSZ0);
    Path png0Path = saveFolder.resolve(PNG0);
    if (Files.exists(mta0Path) || savedStatesModel.getState1Path() != null)
    {
      storePlayTime(mta0Path, savedStatesModel.getState1time());
      copyVsfFile(vsz0Path, savedStatesModel.getState1Path());
      copyPngFile(png0Path, savedStatesModel.getState1PngImage());
      numberofSaves++;
    }
    Path mta1Path = saveFolder.resolve(MTA1);
    Path vsz1Path = saveFolder.resolve(VSZ1);
    Path png1Path = saveFolder.resolve(PNG1);
    if (Files.exists(mta1Path) || savedStatesModel.getState2Path() != null)
    {
      storePlayTime(mta1Path, savedStatesModel.getState2time());
      copyVsfFile(vsz1Path, savedStatesModel.getState2Path());
      copyPngFile(png1Path, savedStatesModel.getState2PngImage());
      numberofSaves++;
    }
    Path mta2Path = saveFolder.resolve(MTA2);
    Path vsz2Path = saveFolder.resolve(VSZ2);
    Path png2Path = saveFolder.resolve(PNG2);
    if (Files.exists(mta2Path) || savedStatesModel.getState3Path() != null)
    {
      storePlayTime(mta2Path, savedStatesModel.getState3time());
      copyVsfFile(vsz2Path, savedStatesModel.getState3Path());
      copyPngFile(png2Path, savedStatesModel.getState3PngImage());
      numberofSaves++;
    }
    Path mta3Path = saveFolder.resolve(MTA3);
    Path vsz3Path = saveFolder.resolve(VSZ3);
    Path png3Path = saveFolder.resolve(PNG3);
    if (Files.exists(mta3Path) || savedStatesModel.getState4Path() != null)
    {
      storePlayTime(mta3Path, savedStatesModel.getState4time());
      copyVsfFile(vsz3Path, savedStatesModel.getState4Path());
      copyPngFile(png3Path, savedStatesModel.getState4PngImage());
      numberofSaves++;
    }
    //Update current map also
    savedStatesMap.put(fileName, numberofSaves);
  }

  public void readSavedStates()
  {
    savedStatesModel.resetProperties();
    //Read from state directory, update model
    String fileName = model.getInfoModel().getGamesFile();
    if (!fileName.isEmpty())
    {
      //Check if folder is available
      Path saveFolder = new File(SAVES + fileName).toPath();
      if (Files.exists(saveFolder))
      {
        //Check which ones are available
        Path mta0Path = saveFolder.resolve(MTA0);
        if (Files.exists(mta0Path))
        {
          //Update model
          savedStatesModel.setState1File(saveFolder.resolve(VSZ0).toFile().getName());
          savedStatesModel.setState1PngFile(saveFolder.resolve(PNG0).toFile().getName());
          savedStatesModel.setState1time(readPlayTime(mta0Path));
        }
        Path mta1Path = saveFolder.resolve(MTA1);
        if (Files.exists(mta1Path))
        {
          //Update model
          savedStatesModel.setState2File(saveFolder.resolve(VSZ1).toFile().getName());
          savedStatesModel.setState2PngFile(saveFolder.resolve(PNG1).toFile().getName());
          savedStatesModel.setState2time(readPlayTime(mta1Path));
        }
        Path mta2Path = saveFolder.resolve(MTA2);
        if (Files.exists(mta2Path))
        {
          //Update model
          savedStatesModel.setState3File(saveFolder.resolve(VSZ2).toFile().getName());
          savedStatesModel.setState3PngFile(saveFolder.resolve(PNG2).toFile().getName());
          savedStatesModel.setState3time(readPlayTime(mta2Path));
        }
        Path mta3Path = saveFolder.resolve(MTA3);
        if (Files.exists(mta3Path))
        {
          //Update model
          savedStatesModel.setState4File(saveFolder.resolve(VSZ3).toFile().getName());
          savedStatesModel.setState4PngFile(saveFolder.resolve(PNG3).toFile().getName());
          savedStatesModel.setState4time(readPlayTime(mta3Path));
        }
      }
    }
  }

  private String readPlayTime(Path mtaFilePath)
  {
    String returnValue = "";
    try
    {
      byte[] fileContent = Files.readAllBytes(mtaFilePath);

      //First 4 bytes represents play time
      byte[] timeArray = new byte[] { fileContent[0], fileContent[1], fileContent[2], fileContent[3] };
      //The value seems to be in milliseconds
      int milliSeconds = ByteBuffer.wrap(timeArray).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
      logger.debug("24 bit value Little endian x= " + milliSeconds);
      returnValue = convertSecondToHHMMString(milliSeconds);
      logger.debug("Converted string = " + returnValue);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not read play time from " + mtaFilePath);
    }
    return returnValue;
  }

  private void storePlayTime(Path mtaFilePath, String playTime)
  {
    try
    {
      if (!mtaFilePath.toFile().exists())
      {
        FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/se/lantz/template.mta"), mtaFilePath.toFile());
      }

      String[] timeparts = playTime.split(":");
      int millis =
        (Integer.parseInt(timeparts[0]) * 3600 + Integer.parseInt(timeparts[1]) * 60 + Integer.parseInt(timeparts[2])) *
          1000;
      byte[] fileContent = Files.readAllBytes(mtaFilePath);
      //Replace the first 4 bytes with the correct values
      ByteBuffer b = ByteBuffer.allocate(4);
      b.order(ByteOrder.LITTLE_ENDIAN);
      b.putInt(millis);
      byte[] result = b.array();
      fileContent[0] = result[0];
      fileContent[1] = result[1];
      fileContent[2] = result[2];
      fileContent[3] = result[3];

      Files.write(mtaFilePath, fileContent);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not write play time to " + mtaFilePath);
    }
  }

  private String convertSecondToHHMMString(int secondtTime)
  {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    df.setTimeZone(tz);
    String time = df.format(new Date(secondtTime));
    return time;
  }

  private void copyVsfFile(Path target, Path source)
  {
    if (source != null)
    {
      try
      {
        FileManager.compressGzip(source, target);
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not compress vsf file");
      }
    }
  }

  private void copyPngFile(Path target, BufferedImage image)
  {
    if (image != null)
    {
      try
      {
        ImageIO.write(image, "png", target.toFile());
      }
      catch (IOException e1)
      {
        ExceptionHandler.logException(e1, "Could not store screenshot");
      }
    }
  }

  public void setExportDirectory(File exportDir)
  {
    this.exportDir = exportDir;
  }

  public void setExportOverwrite(boolean exportOverwrite)
  {
    this.exportOverwrite = exportOverwrite;
  }

  public boolean isExportOverwrite()
  {
    return this.exportOverwrite;
  }

  public void setImportDirectory(File importDir)
  {
    this.importDir = importDir;
  }

  public void setImportOverwrite(boolean importOverwrite)
  {
    this.importOverwrite = importOverwrite;
  }

  public boolean isImportOverwrite()
  {
    return this.importOverwrite;
  }

  public void exportSavedStates(StringBuilder infoBuilder)
  {
    noFilesCopied = 0;
    File saveFolder = new File(SAVES);
    try (Stream<Path> stream = Files.walk(saveFolder.toPath().toAbsolutePath()))
    {
      stream.forEachOrdered(sourcePath -> {
        try
        {
          //Ignore first folder
          if (sourcePath.equals(saveFolder.toPath().toAbsolutePath()))
          {
            return;
          }
          if (!isValidSaveStatePath(sourcePath))
          {
            infoBuilder.append("Skipping " + sourcePath + " (not a valid save state file)\n");
            return;
          }
          Path destinationPath =
            exportDir.toPath().resolve(saveFolder.toPath().toAbsolutePath().relativize(sourcePath));
          //Ignore already existing directories: Files.copy() throws DirectoryNotEmptyException for them
          if (destinationPath.toFile().exists() && destinationPath.toFile().isDirectory())
          {
            return;
          }
          if (!this.exportOverwrite && destinationPath.toFile().exists())
          {
            infoBuilder.append("Skipping " + sourcePath + " (already exists)\n");
          }
          else
          {
            infoBuilder.append("Copying " + sourcePath + "\n");
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            if (!sourcePath.toFile().isDirectory())
            {
              noFilesCopied++;
            }
          }
        }
        catch (Exception e)
        {
          infoBuilder.append("Could not copy from " + sourcePath.toString() + "\n");
          ExceptionHandler.logException(e, "Could not copy from " + sourcePath.toString());
        }
      });
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not export saved states folder.");
    }
  }

  public void importSavedStates(StringBuilder infoBuilder)
  {
    noFilesCopied = 0;
    File saveFolder = new File(SAVES);
    try (Stream<Path> stream = Files.walk(importDir.toPath().toAbsolutePath()))
    {
      stream.forEachOrdered(sourcePath -> {
        try
        {
          //Ignore first folder or any files that are not save state files
          if (sourcePath.equals(importDir.toPath().toAbsolutePath()))
          {
            return;
          }
          if (!isValidSaveStatePath(sourcePath))
          {
            infoBuilder.append("Skipping " + sourcePath + " (not a valid save state file)\n");
            return;
          }
          Path destinationPath =
            saveFolder.toPath().resolve(importDir.toPath().toAbsolutePath().relativize(sourcePath));
          //Ignore already existing directories: Files.copy() throws DirectoryNotEmptyException for them
          if (destinationPath.toFile().exists() && destinationPath.toFile().isDirectory())
          {
            return;
          }
          if (!this.importOverwrite && destinationPath.toFile().exists())
          {
            infoBuilder.append("Skipping " + sourcePath + " (already exists)\n");
          }
          else
          {
            infoBuilder.append("Copying " + sourcePath + "\n");
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            if (!sourcePath.toFile().isDirectory())
            {
              noFilesCopied++;
            }
          }
        }
        catch (Exception e)
        {
          infoBuilder.append("Could not copy from " + sourcePath.toString() + "\n");
          ExceptionHandler.logException(e, "Could not copy from " + sourcePath.toString());
        }
      });
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not import to saved folder.");
    }
    //Update saved states map
    readSavedStatesAndUpdateMap();
    //Update model list after import
    model.getGameListModel().notifyChange();
  }

  private boolean isValidSaveStatePath(Path path)
  {
    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{mta,png,vsz}");
    return matcher.matches(path) || path.toFile().isDirectory();
  }

  public int getNumberOfFilesCopied()
  {
    return noFilesCopied;
  }

  private void readSavedStatesAndUpdateMap()
  {
    savedStatesMap.clear();
    //Read all files in the saves folder
    File saveFolder = new File(SAVES);
    try (Stream<Path> stream = Files.walk(saveFolder.toPath().toAbsolutePath(), 1))
    {
      stream.forEachOrdered(sourcePath -> {
        try
        {
          //Ignore first folder
          if (sourcePath.equals(saveFolder.toPath().toAbsolutePath()))
          {
            return;
          }

          //Check which files are available
          Path save1 = sourcePath.resolve(MTA0);
          Path save2 = sourcePath.resolve(MTA1);
          Path save3 = sourcePath.resolve(MTA2);
          Path save4 = sourcePath.resolve(MTA3);
          int savesAvailable = 0;
          if (save1.toFile().exists())
          {
            savesAvailable++;
          }
          if (save2.toFile().exists())
          {
            savesAvailable++;
          }
          if (save3.toFile().exists())
          {
            savesAvailable++;
          }
          if (save4.toFile().exists())
          {
            savesAvailable++;
          }
          //Add to map
          savedStatesMap.put(sourcePath.toFile().getName(), savesAvailable);
        }
        catch (Exception e)
        {
          ExceptionHandler.logException(e, "Could not check available saved states for " + sourcePath.toString());
        }
      });
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not construct savedStates Map");
    }
  }

  public int getNumberOfSavedStatesForGame(String gameFileName)
  {
    return savedStatesMap.get(gameFileName) != null ? savedStatesMap.get(gameFileName) : 0;
  }
}
