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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameDetails;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class ExportManager
{
  private static final Logger logger = LoggerFactory.getLogger(ExportManager.class);
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

  public void setTargetDirectory(File targetDir, boolean delete)
  {
    this.deleteBeforeExport = delete;
    Path targetDirPath = targetDir.toPath();

    if (delete)
    {
      //Delete target folder first
      try
      {
        if (Files.exists(targetDirPath))
        {
          //Delete entire folder
          Files.walk(targetDirPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not delete target folder");
      }
    }

    this.targetDir = targetDirPath.toFile();
    try
    {
      Files.createDirectories(targetDirPath);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not create " + targetDirPath);
    }
  }

  public void readFromDb(StringBuilder infoBuilder)
  {
    if (gameViewMode)
    {
      for (GameView gameView : gameViewList)
      {
        gameDetailsForViewsMap.put(gameView, uiModel.readGameDetailsForGameView(infoBuilder, gameView));
      }
    }
    else
    {
      gameDetailsList = uiModel.readGameDetailsForExport(infoBuilder, gamesList);
    }
  }

  public void createGameInfoFiles(StringBuilder infoBuilder, boolean fileLoader)
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
          uiModel.exportGameInfoFile(gameDetails, targetPath.toFile(), infoBuilder, fileLoader);
        }
      }
    }
    else
    {
      for (GameDetails gameDetails : gameDetailsList)
      {
        uiModel.exportGameInfoFile(gameDetails, targetDir, infoBuilder, fileLoader);
      }
    }
  }

  public boolean isDeleteBeforeExport()
  {
    return deleteBeforeExport;
  }

  public void copyFilesForCarousel(StringBuilder infoBuilder)
  {
    if (gameViewMode)
    {
      for (GameView gameView : gameViewList)
      {
        Path targetPath = targetDir.toPath().resolve(gameView.getName().replace(" ", "_"));
        copyFilesForCarousel(infoBuilder, targetPath, gameDetailsForViewsMap.get(gameView));
      }
    }
    else
    {
      copyFilesForCarousel(infoBuilder, targetDir.toPath(), gameDetailsList);
    }
  }

  private void copyFilesForCarousel(StringBuilder infoBuilder, Path targetPath, List<GameDetails> gameDetailsList)
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
      infoBuilder.append("ERROR: Could not create directories for covers, screens and games, " + e.getMessage() + "\n");
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
          infoBuilder.append("Copying cover from ");
          infoBuilder.append(coverPath.toString());
          infoBuilder.append("\n");
          Files.copy(coverPath, targetCoverPath, StandardCopyOption.REPLACE_EXISTING);
        }
        if (!gameDetails.getScreen1().isEmpty())
        {
          infoBuilder.append("Copying screenshot from ");
          infoBuilder.append(screens1Path.toString());
          infoBuilder.append("\n");
          Files.copy(screens1Path, targetScreen1Path, StandardCopyOption.REPLACE_EXISTING);
        }
        if (!gameDetails.getScreen2().isEmpty())
        {
          infoBuilder.append("Copying screenshot from ");
          infoBuilder.append(screens2Path.toString());
          infoBuilder.append("\n");
          Files.copy(screens2Path, targetScreen2Path, StandardCopyOption.REPLACE_EXISTING);
        }
        if (!gameDetails.getGame().isEmpty())
        {
          infoBuilder.append("Copying game file from ");
          infoBuilder.append(gamePath.toString());
          infoBuilder.append("\n");
          Files.copy(gamePath, targetGamePath, StandardCopyOption.REPLACE_EXISTING);
        }
      }
      catch (IOException e)
      {
        infoBuilder.append("ERROR: Could not copy files for " + gameDetails.getTitle() + ", " + e.getMessage() + "\n");
        ExceptionHandler.handleException(e, "Could NOT copy files for: " + gameDetails.getTitle());
      }
    }
  }

  public void copyGamesForFileLoader(StringBuilder infoBuilder)
  {
    if (gameViewMode)
    {
      for (GameView gameView : gameViewList)
      {
        Path targetPath = targetDir.toPath().resolve(gameView.getName());
        copyGamesForFileLoader(infoBuilder, targetPath, gameDetailsForViewsMap.get(gameView));
      }
    }
    else
    {
      copyGamesForFileLoader(infoBuilder, targetDir.toPath(), gameDetailsList);
    }

  }

  private void copyGamesForFileLoader(StringBuilder infoBuilder, Path targetPath, List<GameDetails> gameDetailsList)
  {
    try
    {
      Path targetGamePath = targetPath;
      Files.createDirectories(targetGamePath);
    }
    catch (IOException e)
    {
      infoBuilder.append("ERROR: Could not create directory for games, " + e.getMessage() + "\n");
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
      Path targetGamePath = targetPath
        .resolve(FileManager.generateFileNameFromTitleForFileLoader(gameDetails.getTitle(),
                                                                    gameDetails.getDuplicateIndex()) +
          "." + extension);

      try
      {
        if (!gameDetails.getGame().isEmpty())
        {
          infoBuilder.append("Copying game file from ");
          infoBuilder.append(gamePath.toString());
          infoBuilder.append(" to ");
          infoBuilder.append(targetGamePath);
          infoBuilder.append("\n");
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
        infoBuilder.append("ERROR: Could not copy files for " + gameDetails.getTitle() + ", " + e.getMessage() + "\n");
        ExceptionHandler.handleException(e, "Could NOT copy files for: " + gameDetails.getTitle());
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
