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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.MainWindow;
import se.lantz.gui.exports.PublishWorker;
import se.lantz.gui.menu.InsetsMenuItem;
import se.lantz.model.MainViewModel;
import se.lantz.model.PreferencesModel;
import se.lantz.model.SavedStatesModel;
import se.lantz.model.SavedStatesModel.SAVESTATE;
import se.lantz.model.data.GameValidationDetails;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class SavedStatesManager
{
  private static final Logger logger = LoggerFactory.getLogger(SavedStatesManager.class);

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
  private File fixDir;

  private boolean exportOverwrite;
  private boolean importOverwrite;

  private int noFilesCopied = 0;
  private int noSavedStatesFixed = 0;

  /**
   * Map holding available saved states with fileName (subfolder) as key and number of saved states available as value
   * (0-4).
   */
  private Map<String, Integer> savedStatesMap = new HashMap<>();

  private InsetsMenuItem palNtscFixMenuItem;

  public SavedStatesManager(MainViewModel model, InsetsMenuItem palNtscFixMenuItem)
  {
    this.model = model;
    this.palNtscFixMenuItem = palNtscFixMenuItem;
    this.savedStatesModel = model.getSavedStatesModel();
    readSavedStatesAndUpdateMap();
  }

  public void saveSavedStates()
  {
    //First, check if any saved states has been deleted. Delete the corresponding files.
    if (savedStatesModel.isState1Deleted())
    {
      deleteSavedState(SAVESTATE.Save0);
    }
    if (savedStatesModel.isState2Deleted())
    {
      deleteSavedState(SAVESTATE.Save1);
    }
    if (savedStatesModel.isState3Deleted())
    {
      deleteSavedState(SAVESTATE.Save2);
    }
    if (savedStatesModel.isState4Deleted())
    {
      deleteSavedState(SAVESTATE.Save3);
    }

    //If the game has been renamed, make sure to rename the saves folder also
    String oldFileName = model.getInfoModel().getOldGamesFile();
    String newFileName = model.getInfoModel().getGamesFile();
    File oldSaveFolder = new File(FileManager.SAVES + getGameFolderName(oldFileName, model.getInfoModel().getTitle()));
    if (!oldFileName.equals(newFileName) && oldSaveFolder.exists())
    {
      //Rename old folder to new name
      oldSaveFolder.renameTo(new File(FileManager.SAVES +
        getGameFolderName(model.getInfoModel().getGamesFile(), model.getInfoModel().getTitle())));
    }

    String fileName = model.getInfoModel().getGamesFile();
    String gameFolderName = getGameFolderName(fileName, model.getInfoModel().getTitle());

    Path saveFolder = new File(FileManager.SAVES + gameFolderName).toPath();
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
    savedStatesMap.put(gameFolderName.toUpperCase(), numberofSaves);
  }

  public void readSavedStates()
  {
    savedStatesModel.resetProperties();
    //Read from state directory, update model
    String fileName = getGameFolderName(model.getInfoModel().getGamesFile(), model.getInfoModel().getTitle());
    if (!fileName.isEmpty())
    {
      fileName = fileName.trim();
      //Check if folder is available
      Path saveFolder = new File(FileManager.SAVES + fileName).toPath();
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

  public void setFixDirectory(File fixDir)
  {
    this.fixDir = fixDir;
  }

  public void setImportOverwrite(boolean importOverwrite)
  {
    this.importOverwrite = importOverwrite;
  }

  public boolean isImportOverwrite()
  {
    return this.importOverwrite;
  }

  private void deleteSavedState(SAVESTATE state)
  {
    String fileName = getGameFolderName(model.getInfoModel().getGamesFile(), model.getInfoModel().getTitle());
    Path saveFolder = new File(FileManager.SAVES + fileName).toPath();
    try
    {
      switch (state)
      {
      case Save0:
        Path mta0Path = saveFolder.resolve(MTA0);
        Path vsz0Path = saveFolder.resolve(VSZ0);
        Path png0Path = saveFolder.resolve(PNG0);
        Files.deleteIfExists(mta0Path);
        Files.deleteIfExists(vsz0Path);
        Files.deleteIfExists(png0Path);
        break;
      case Save1:
        Path mta1Path = saveFolder.resolve(MTA1);
        Path vsz1Path = saveFolder.resolve(VSZ1);
        Path png1Path = saveFolder.resolve(PNG1);
        Files.deleteIfExists(mta1Path);
        Files.deleteIfExists(vsz1Path);
        Files.deleteIfExists(png1Path);
        break;
      case Save2:
        Path mta2Path = saveFolder.resolve(MTA2);
        Path vsz2Path = saveFolder.resolve(VSZ2);
        Path png2Path = saveFolder.resolve(PNG2);
        Files.deleteIfExists(mta2Path);
        Files.deleteIfExists(vsz2Path);
        Files.deleteIfExists(png2Path);
        break;
      case Save3:
        Path mta3Path = saveFolder.resolve(MTA3);
        Path vsz3Path = saveFolder.resolve(VSZ3);
        Path png3Path = saveFolder.resolve(PNG3);
        Files.deleteIfExists(mta3Path);
        Files.deleteIfExists(vsz3Path);
        Files.deleteIfExists(png3Path);
        break;
      default:
        break;
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not delete saved state files.");
    }
  }

  public void exportSavedStates(PublishWorker worker)
  {
    noFilesCopied = 0;
    File saveFolder = new File(FileManager.SAVES);
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
            worker.publishMessage("Skipping " + sourcePath + " (not a valid save state file)");
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
            worker.publishMessage("Skipping " + sourcePath + " (already exists)");
          }
          else
          {
            worker.publishMessage("Copying " + sourcePath);
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            if (!sourcePath.toFile().isDirectory())
            {
              noFilesCopied++;
            }
          }
        }
        catch (Exception e)
        {
          worker.publishMessage("Could not copy from " + sourcePath.toString());
          ExceptionHandler.logException(e, "Could not copy from " + sourcePath.toString());
        }
      });
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not export saved states folder.");
    }
  }

  public void importSavedStates(PublishWorker worker)
  {
    noFilesCopied = 0;
    File saveFolder = new File(FileManager.SAVES);
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
            worker.publishMessage("Skipping " + sourcePath + " (not a valid save state file)");
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
            worker.publishMessage("Skipping " + sourcePath + " (already exists)");
          }
          else
          {
            worker.publishMessage("Copying " + sourcePath);
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            if (!sourcePath.toFile().isDirectory())
            {
              noFilesCopied++;
            }
          }
        }
        catch (Exception e)
        {
          worker.publishMessage("Could not copy from " + sourcePath.toString());
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

  public void fixCorruptSavedStates(PublishWorker worker)
  {
    noSavedStatesFixed = 0;

    try (Stream<Path> stream = Files.walk(fixDir.toPath().toAbsolutePath()))
    {
      stream.forEachOrdered(sourcePath -> {
        try
        {
          if (!isValidSaveStateMtaFilePath(sourcePath))
          {
            return;
          }

          worker.publishMessage("Fixing " + sourcePath);
          //Read the mta file and keep track of the time
          String playTime = readPlayTime(sourcePath);
          //Copy the template file 
          FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/se/lantz/template.mta"),
                                          sourcePath.toFile());
          //Write the time from the old file
          storePlayTime(sourcePath, playTime);
          noSavedStatesFixed++;
        }
        catch (Exception e)
        {
          worker.publishMessage("Could not fix " + sourcePath.toString());
          ExceptionHandler.logException(e, "Could not fix " + sourcePath.toString());
        }
      });
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not fix saved states files.");
    }
  }

  private boolean isValidSaveStatePath(Path path)
  {
    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{mta,png,vsz}");
    return matcher.matches(path) || path.toFile().isDirectory();
  }

  private boolean isValidSaveStateMtaFilePath(Path path)
  {
    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{mta}");
    return matcher.matches(path);
  }

  public int getNumberOfFilesCopied()
  {
    return noFilesCopied;
  }

  public int getNumberOfFixedSavedStates()
  {
    return noSavedStatesFixed;
  }

  public void readSavedStatesAndUpdateMap()
  {
    savedStatesMap.clear();
    //Read all files in the saves folder
    File saveFolder = new File(FileManager.SAVES);
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
          savedStatesMap.put(sourcePath.toFile().getName().toUpperCase(), savesAvailable);
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

  public int getNumberOfSavedStatesForGame(String gameFileName, String title)
  {
    String fileName = getGameFolderName(gameFileName, title).toUpperCase();
    return savedStatesMap.get(fileName) == null ? 0 : savedStatesMap.get(fileName);
  }

  public void checkEnablementOfPalNtscMenuItem(boolean check)
  {
    boolean palNtscItemEnabled = false;

    if (check)
    {
      //Check if current game has a 0.vsf file and the current game file is a snapshot
      String fileName = model.getInfoModel().getGamesFile();
      if (!fileName.isEmpty() && fileName.contains(".vsf"))
      {
        //Check if folder is available
        Path saveFolder =
          new File(FileManager.SAVES + getGameFolderName(fileName, model.getInfoModel().getTitle())).toPath();
        if (Files.exists(saveFolder))
        {
          //Check which ones are available
          Path vsz0Path = saveFolder.resolve(VSZ0);
          if (Files.exists(vsz0Path))
          {
            palNtscItemEnabled = true;
          }
        }
      }
    }
    palNtscFixMenuItem.setEnabled(palNtscItemEnabled);
  }

  public boolean swapGameFileAndSavedState()
  {
    String gamesFile = model.getInfoModel().getGamesFile();
    Path gameFilePath = new File(FileManager.GAMES + gamesFile).toPath();
    Path firstSavedStatePath =
      new File(FileManager.SAVES + getGameFolderName(gamesFile, model.getInfoModel().getTitle())).toPath()
        .resolve(VSZ0);

    Path tempFilePath = new File(FileManager.GAMES + "temp.gz").toPath();
    try
    {
      Files.copy(gameFilePath, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
      Files.copy(firstSavedStatePath, gameFilePath, StandardCopyOption.REPLACE_EXISTING);
      Files.copy(tempFilePath, firstSavedStatePath, StandardCopyOption.REPLACE_EXISTING);
      Files.delete(tempFilePath);
      if (model.getSystemModel().isPal())
      {
        model.getSystemModel().setNtsc(true);
      }
      else
      {
        model.getSystemModel().setPal(true);
      }
      model.saveData();
      return true;
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not swap game file and first saved state.");
    }
    return false;
  }

  public static String getGameFolderName(String fileName, String gameTitle)
  {
    String returnValue = "";
    switch (FileManager.getConfiguredSavedStatesCarouselVersion())
    {
    case PreferencesModel.CAROUSEL_132:
      returnValue = fileName;
      break;
    case PreferencesModel.CAROUSEL_152:
      if (fileName.indexOf(".") > -1)
      {
        returnValue = fileName.substring(0, fileName.indexOf("."));
      }
      break;
    case PreferencesModel.FILE_LOADER:
      if (fileName.length() > 0)
      {
        returnValue = FileManager.generateSavedStatesFolderNameForFileLoader(gameTitle, 0);
      }
      break;
    default:
      break;
    }
    return returnValue;
  }

  public void convertToCarousel152Version()
  {
    File saveFolder = new File(FileManager.SAVES);
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

          File originalDir = sourcePath.toFile();
          String newName = originalDir.getName();
          if (newName.indexOf(".") > -1)
          {
            newName = newName.substring(0, newName.indexOf("."));
          }
          File newDir = new File(originalDir.getParent() + "\\" + newName);
          if (!newName.equals(originalDir.getName()) && !newDir.exists())
          {
            //TODO: what if same name exists already, merge or just ignore?
            originalDir.renameTo(newDir);
          }
        }
        catch (Exception e)
        {
          ExceptionHandler.logException(e, "Could not convert available saved states for " + sourcePath.toString());
        }
      });
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not convert saved states");
    }
    //Collect all that has 1.5.2 version already
    try (Stream<Path> stream = Files.walk(saveFolder.toPath().toAbsolutePath(), 1))
    {
      List<Path> existingSaveFolders = stream.filter(sourcePath -> {
        File originalDir = sourcePath.toFile();
        String newName = originalDir.getName();
        if (newName.indexOf(".") > -1)
        {
          newName = newName.substring(0, newName.indexOf("."));
          File newDir = new File(originalDir.getParent() + "\\" + newName);

          return newDir.exists();
        }
        return false;
      }).collect(Collectors.toList());

      if (!existingSaveFolders.isEmpty())
      {
        int result = JOptionPane
          .showConfirmDialog(MainWindow.getInstance(),
                             String
                               .format("There are %s games where a 1.5.2 version of the saved states already exists, do you want to overwrite them with the 1.3.2 version?",
                                       existingSaveFolders.size()),
                             "Exisiting saved states",
                             JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION)
        {
          existingSaveFolders.stream().forEach(sourcePath -> {
            File originalDir = sourcePath.toFile();
            String newName = originalDir.getName().substring(0, originalDir.getName().indexOf("."));
            File newDir = new File(originalDir.getParent() + "\\" + newName);
            try
            {
              FileUtils.deleteDirectory(newDir);
              originalDir.renameTo(newDir);
            }
            catch (IOException e)
            {
              ExceptionHandler.handleException(e, "Could not delete and replace dir");
            }
          });
        }
      }
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not convert saved states");
    }
  }

  public void copyFromCarouselToFileLoader()
  {
    //1. look through all folders and try to find a game in the db that matches the file name.
    //2. for all that matches, get the title and copy the existing folder to a folder named as the title 
    File saveFolder = new File(FileManager.SAVES);
    try (Stream<Path> stream = Files.walk(saveFolder.toPath().toAbsolutePath(), 1))
    {
      List<GameValidationDetails> allGamesDetailsList = this.model.getDbConnector().fetchAllGamesForDbValdation();

      List<Path> filteredPathList = getMatchingCarousel152FoldersThatCanBeCopied(stream, allGamesDetailsList);
      //Copy for all 
      filteredPathList.stream().forEachOrdered(sourcePath -> {
        try
        {
          File originalDir = sourcePath.toFile();
          String fileName = originalDir.getName();

          GameValidationDetails gameDetails = allGamesDetailsList.stream()
            .filter(game -> fileName.equals(get152VersionFileName(game.getGame()))).findAny().orElse(null);

          if (gameDetails != null)
          {
            File newDir = new File(originalDir.getParent() + "\\" +
              FileManager.generateSavedStatesFolderNameForFileLoader(gameDetails.getTitle(), 0));
            copyDirectory(sourcePath.toString(), newDir.toPath().toString());
          }
        }
        catch (Exception e)
        {
          ExceptionHandler.logException(e, "Could not copy available saved states for " + sourcePath.toString());
        }
      });
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not copy saved states");
    }
  }

  private void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException
  {
    Files.walk(Paths.get(sourceDirectoryLocation)).forEach(source -> {
      Path destination =
        Paths.get(destinationDirectoryLocation, source.toString().substring(sourceDirectoryLocation.length()));
      try
      {
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
      }
      catch (IOException e)
      {
        ExceptionHandler.logException(e, "Could not copy available saved states from " + sourceDirectoryLocation);
      }
    });
  }

  private boolean is152VersionFolderName(Path folderPath)
  {
    String folderName = folderPath.toFile().getName();
    String newName = folderName;
    if (folderName.indexOf(".") > -1)
    {
      newName = folderName.substring(0, folderName.indexOf("."));
    }
    return newName.equalsIgnoreCase(folderName);
  }

  private String get152VersionFileName(String fileName)
  {
    String newName = fileName;
    if (fileName.indexOf(".") > -1)
    {
      newName = fileName.substring(0, fileName.indexOf("."));
    }
    return newName;
  }

  public int checkFor132SavedStates()
  {
    File saveFolder = new File(FileManager.SAVES);
    long returnValue = 0;
    try (Stream<Path> stream = Files.walk(saveFolder.toPath().toAbsolutePath(), 1))
    {
      returnValue = stream.filter(sourcePath -> {
        //Ignore first folder
        if (sourcePath.equals(saveFolder.toPath().toAbsolutePath()))
        {
          return false;
        }
        File originalDir = sourcePath.toFile();
        String newName = originalDir.getName();
        if (newName.indexOf(".") > -1)
        {
          return true;
        }
        return false;
      }).count();
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not check saved states using carousel 1.3.2 format");
    }
    return (int) returnValue;
  }

  public int checkForSavedStatesToCopyToFileLoader()
  {
    //1. look through all folders and try to find a game in the db that matches the file name.
    //2. for all that matches, get the title and check so that no folder exists already 
    File saveFolder = new File(FileManager.SAVES);
    try (Stream<Path> stream = Files.walk(saveFolder.toPath().toAbsolutePath(), 1))
    {
      List<GameValidationDetails> allGamesDetailsList = this.model.getDbConnector().fetchAllGamesForDbValdation();
      List<Path> filteredPathList = getMatchingCarousel152FoldersThatCanBeCopied(stream, allGamesDetailsList);
      return filteredPathList.size();
    }
    catch (IOException e1)
    {
      ExceptionHandler.handleException(e1, "Could not check saved states for File Loader");
    }
    return 0;
  }

  private List<Path> getMatchingCarousel152FoldersThatCanBeCopied(Stream<Path> stream,
                                                                  List<GameValidationDetails> allGamesDetailsList)
  {
    List<Path> filteredPathList = stream.filter(sourcePath -> is152VersionFolderName(sourcePath)).filter(sourcePath -> {
      File originalDir = sourcePath.toFile();
      String fileName = originalDir.getName();
      GameValidationDetails gameDetails = allGamesDetailsList.stream()
        .filter(game -> fileName.equals(get152VersionFileName(game.getGame()))).findAny().orElse(null);
      if (gameDetails != null)
      {
        File newDir = new File(originalDir.getParent() + "\\" +
          FileManager.generateSavedStatesFolderNameForFileLoader(gameDetails.getTitle(), 0));
        return !newDir.exists();
      }
      return false;
    }).collect(Collectors.toList());
    return filteredPathList;
  }
}
