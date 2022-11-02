package se.lantz.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import se.lantz.gui.exports.PublishWorker;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameDetails;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class ExportManager
{
  private List<GameListData> gamesList = new ArrayList<>();
  private List<GameDetails> gameDetailsList = new ArrayList<>();
  private List<GameView> gameViewList = new ArrayList<>();
  private Map<GameView, List<GameDetails>> gameDetailsForViewsMap = new HashMap<>();
  private File targetDir;
  private MainViewModel uiModel;
  private boolean deleteBeforeExport = false;

  private boolean gameViewMode = true;

  public ExportManager(MainViewModel uiModel)
  {
    this.uiModel = uiModel;
  }

  public void setGamesToExport(List<GameListData> gamesList)
  {
    this.gamesList = gamesList;
    gameViewMode = false;
  }

  public void setGameViewsToExport(List<GameView> gameViewList)
  {
    this.gameViewList = gameViewList;
    gameViewMode = true;
  }

  public void setTargetDirectory(File targetDir)
  {
    this.targetDir = targetDir;
  }

  public void createDirectoriesBeforeExport()
  {
    try
    {
      Files.createDirectories(targetDir.toPath());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not create " + targetDir.toPath());
    }
  }

  public void deleteBeforeExport(PublishWorker worker)
  {
    Path targetDirPath = this.targetDir.toPath();
    if (gameViewMode)
    {
      //Delete only subfolders
      try
      {
        if (Files.exists(targetDirPath))
        {
          File[] directories = targetDir.listFiles(File::isDirectory);
          for (File dir : directories)
          {
            worker.publishMessage("Deleting " + dir.getName() + "...");
            //Delete entire folder
            Files.walk(dir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
          }
        }
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not delete target folder");
      }
    }
    else
    {
      //Delete entire target folder
      try
      {
        if (Files.exists(targetDirPath))
        {
          worker.publishMessage("Deleting " + this.targetDir.getName() + "...");
          //Delete entire folder
          Files.walk(targetDirPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not delete target folder");
      }
    }
  }

  public void readFromDb(PublishWorker worker)
  {
    if (gameViewMode)
    {
      for (GameView gameView : gameViewList)
      {
        gameDetailsForViewsMap.put(gameView, uiModel.readGameDetailsForGameView(worker, gameView));
      }
    }
    else
    {
      gameDetailsList = uiModel.readGameDetailsForExport(worker, gamesList);
    }
  }

  public void createGameInfoFiles(PublishWorker worker, boolean fileLoader)
  {
    if (gameViewMode)
    {
      for (GameView gameView : gameViewList)
      {
        Path targetPath = targetDir.toPath().resolve(gameView.getName());
        if (!fileLoader)
        {
          targetPath = targetDir.toPath().resolve(gameView.getName().replace(" ", "_"));
        }
        try
        {
          Files.createDirectories(targetPath);
        }
        catch (IOException e)
        {
          ExceptionHandler.handleException(e, "Could not create " + targetPath);
        }
        for (GameDetails gameDetails : gameDetailsForViewsMap.get(gameView))
        {
          uiModel.exportGameInfoFile(gameDetails, targetPath.toFile(), worker, fileLoader);
        }
      }
    }
    else
    {
      for (GameDetails gameDetails : gameDetailsList)
      {
        uiModel.exportGameInfoFile(gameDetails, targetDir, worker, fileLoader);
      }
    }
  }

  public boolean isDeleteBeforeExport()
  {
    return deleteBeforeExport;
  }

  public void setDeleteBeforeExport(boolean delete)
  {
    this.deleteBeforeExport = delete;
  }

  public void copyFilesForCarousel(PublishWorker worker)
  {
    if (gameViewMode)
    {
      for (GameView gameView : gameViewList)
      {
        Path targetPath = targetDir.toPath().resolve(gameView.getName().replace(" ", "_"));
        copyFilesForCarousel(worker, targetPath, gameDetailsForViewsMap.get(gameView));
      }
    }
    else
    {
      copyFilesForCarousel(worker, targetDir.toPath(), gameDetailsList);
    }
  }

  private void copyFilesForCarousel(PublishWorker worker, Path targetPath, List<GameDetails> gameDetailsList)
  {
    try
    {
      Path targetCoverPath = targetPath.resolve("covers");
      Files.createDirectories(targetCoverPath);
      Path targetScreenPath = targetPath.resolve("screens");
      Files.createDirectories(targetScreenPath);
      Path targetGamePath = targetPath.resolve("games");
      Files.createDirectories(targetGamePath);
    }
    catch (IOException e)
    {
      worker.publishMessage("ERROR: Could not create directories for covers, screens and games, " + e.getMessage());
      ExceptionHandler.handleException(e, " Could not create directories for covers, screens and games");
      return;
    }
    for (GameDetails gameDetails : gameDetailsList)
    {
      Path coverPath = Paths.get("./covers/" + gameDetails.getCover());
      Path targetCoverPath = targetPath.resolve("covers/" + gameDetails.getCover());

      Path screens1Path = Paths.get("./screens/" + gameDetails.getScreen1());
      Path targetScreen1Path = targetPath.resolve("screens/" + gameDetails.getScreen1());
      Path screens2Path = Paths.get("./screens/" + gameDetails.getScreen2());
      Path targetScreen2Path = targetPath.resolve("screens/" + gameDetails.getScreen2());

      Path gamePath = Paths.get("./games/" + gameDetails.getGame());
      Path targetGamePath = targetPath.resolve("games/" + gameDetails.getGame());

      try
      {
        if (!gameDetails.getCover().isEmpty())
        {
          worker.publishMessage("Copying cover from " + coverPath.toString());
          Files.copy(coverPath, targetCoverPath, StandardCopyOption.REPLACE_EXISTING);
        }
        if (!gameDetails.getScreen1().isEmpty())
        {
          worker.publishMessage("Copying screenshot from " + screens1Path.toString());
          Files.copy(screens1Path, targetScreen1Path, StandardCopyOption.REPLACE_EXISTING);
        }
        if (!gameDetails.getScreen2().isEmpty())
        {
          worker.publishMessage("Copying screenshot from " + screens2Path.toString());
          Files.copy(screens2Path, targetScreen2Path, StandardCopyOption.REPLACE_EXISTING);
        }
        if (!gameDetails.getGame().isEmpty())
        {
          worker.publishMessage("Copying game file from " + gamePath.toString());
          Files.copy(gamePath, targetGamePath, StandardCopyOption.REPLACE_EXISTING);
        }
      }
      catch (IOException e)
      {
        worker.publishMessage("ERROR: Could not copy files for " + gameDetails.getTitle() + ", " + e.getMessage());
        ExceptionHandler.handleException(e, "Could NOT copy files for: " + gameDetails.getTitle());
      }
    }
  }

  public void copyGamesForFileLoader(PublishWorker worker)
  {
    if (gameViewMode)
    {
      for (GameView gameView : gameViewList)
      {
        Path targetPath = targetDir.toPath().resolve(gameView.getName());
        copyGamesForFileLoader(worker, targetPath, gameDetailsForViewsMap.get(gameView));
      }
    }
    else
    {
      copyGamesForFileLoader(worker, targetDir.toPath(), gameDetailsList);
    }

  }

  private void copyGamesForFileLoader(PublishWorker worker, Path targetPath, List<GameDetails> gameDetailsList)
  {
    try
    {
      Files.createDirectories(targetPath);
    }
    catch (IOException e)
    {
      worker.publishMessage("ERROR: Could not create directory for games, " + e.getMessage());
      ExceptionHandler.handleException(e, " Could not create directory for games");
      return;
    }
    for (GameDetails gameDetails : gameDetailsList)
    {
      String gameName = gameDetails.getGame();

      Path gamePath = Paths.get("./games/" + gameName);
      String extension = FilenameUtils.getExtension(gameName);
      boolean zipped = false;
      if (extension.equalsIgnoreCase("gz"))
      {
        gameName = FilenameUtils.removeExtension(gameName);
        //Get next extension
        extension = FilenameUtils.getExtension(gameName);
        if (extension.equalsIgnoreCase("vsf"))
        {
          //Rename to prg so that it works in the file loader
          extension = "prg";
        }
        zipped = true;
      }
      String gameFileNameWithoutExtension = FileManager.generateFileNameFromTitleForFileLoader(gameDetails.getTitle(),
                                                                               gameDetails.getDuplicateIndex());
      
      Path targetGamePath = targetPath
        .resolve(gameFileNameWithoutExtension + "." + extension);
      if (FileManager.hasExtraDisks(gameDetails))
      {
        targetGamePath  = targetPath
          .resolve(gameFileNameWithoutExtension + " (disk 1)." + extension);
      }
      

      try
      {
        if (!gameDetails.getGame().isEmpty())
        {
          worker.publishMessage("Copying game file from " + gamePath.toString() + " to " + targetGamePath);

          if (zipped)
          {
            FileManager.decompressGzip(gamePath, targetGamePath);
          }
          else
          {
            Files.copy(gamePath, targetGamePath, StandardCopyOption.REPLACE_EXISTING);
          }
        }
      }
      catch (IOException e)
      {
        worker.publishMessage("ERROR: Could not copy files for " + gameDetails.getTitle() + ", " + e.getMessage());
        ExceptionHandler.handleException(e, "Could NOT copy files for: " + gameDetails.getTitle());
      }
      //Extra disks
      copyExtraDiskForFileLoader(targetPath, gameFileNameWithoutExtension, gameDetails.getDisk2(), 2, worker);
      copyExtraDiskForFileLoader(targetPath, gameFileNameWithoutExtension, gameDetails.getDisk3(), 3, worker);
      copyExtraDiskForFileLoader(targetPath, gameFileNameWithoutExtension, gameDetails.getDisk4(), 4, worker);
      copyExtraDiskForFileLoader(targetPath, gameFileNameWithoutExtension, gameDetails.getDisk5(), 5, worker);
      copyExtraDiskForFileLoader(targetPath, gameFileNameWithoutExtension, gameDetails.getDisk6(), 6, worker);
    }
  }
  
  private void copyExtraDiskForFileLoader(Path targetPath, String gameFileNameWithoutExtension, String extraDiskName, int diskIndex, PublishWorker worker)
  {
    if (extraDiskName != null && !extraDiskName.isEmpty())
    {
      String extension = FilenameUtils.getExtension(extraDiskName);
      
      Path targetGamePath = targetPath
        .resolve(gameFileNameWithoutExtension + " (disk " + diskIndex + ")_CD." + extension);
      
      Path extraDiskPath = Paths.get("./extradisks/" + extraDiskName);
      
      try
      {
        
        worker.publishMessage("Copying extra disk file from " + extraDiskPath.toString() + " to " + targetGamePath);        
        Files.copy(extraDiskPath, targetGamePath, StandardCopyOption.REPLACE_EXISTING);    
      }
      catch (IOException e)
      {
        worker.publishMessage("ERROR: Could not copy files for " + gameFileNameWithoutExtension + ", " + e.getMessage());
        ExceptionHandler.handleException(e, "Could NOT copy files for: " + gameFileNameWithoutExtension);
      }
    }
  }

  public void clearAfterImport()
  {
    gamesList.clear();
    gameDetailsList.clear();
    gameDetailsForViewsMap.clear();
    targetDir = null;
    deleteBeforeExport = false;
  }
}
