package se.lantz.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameDetails;
import se.lantz.model.data.GameListData;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class ExportManager
{
  private static final Logger logger = LoggerFactory.getLogger(ExportManager.class);
  private List<GameListData> gamesList;
  private List<GameDetails> gameDetailsList;
  private File targetDir;
  private MainViewModel uiModel;
  private boolean favFormat = false;

  public ExportManager(MainViewModel uiModel)
  {
    this.uiModel = uiModel;
  }

  public void setGamesToExport(List<GameListData> gamesList)
  {
    this.gamesList = gamesList;
  }

  public void setTargerDirectory(File targetDir)
  {
    Path targetDirPath = targetDir.toPath().resolve("games");
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
    gameDetailsList = uiModel.readGameDetailsForExport(infoBuilder, gamesList);
  }

  public void createGameInfoFiles(StringBuilder infoBuilder)
  {
    for (GameDetails gameDetails : gameDetailsList)
    {
      uiModel.exportGameInfoFile(gameDetails, targetDir, this.favFormat, infoBuilder);
    }
  }

  public void setExportFormat(boolean favFormat)
  {
    this.favFormat = favFormat;
  }

  public void copyFiles(StringBuilder infoBuilder)
  {
    if (!favFormat)
    {
      try
      {
        Path targetCoverPath = targetDir.toPath().resolve("covers");
        Files.createDirectories(targetCoverPath);
        Path targetScreenPath = targetDir.toPath().resolve("screens");
        Files.createDirectories(targetScreenPath);
        Path targetGamePath = targetDir.toPath().resolve("games");
        Files.createDirectories(targetGamePath);
      }
      catch (IOException e)
      {
        infoBuilder
          .append("ERROR: Could not create directories for covers, screens and games, " + e.getMessage() + "\n");
        ExceptionHandler.handleException(e, " Could not create directories for covers, screens and games");
        return;
      }
    }

    for (GameDetails gameDetails : gameDetailsList)
    {
      Path coverPath = Paths.get("./covers/" + gameDetails.getCover());
      String coverDir = favFormat ? FileManager.generateFileNameFromTitle(gameDetails.getTitle()) + "/" : "covers/";
      Path targetCoverPath = targetDir.toPath().resolve(coverDir + gameDetails.getCover());

      String screensDir = favFormat ? FileManager.generateFileNameFromTitle(gameDetails.getTitle()) + "/" : "screens/";
      Path screens1Path = Paths.get("./screens/" + gameDetails.getScreen1());
      Path targetScreen1Path = targetDir.toPath().resolve(screensDir + gameDetails.getScreen1());

      Path screens2Path = Paths.get("./screens/" + gameDetails.getScreen2());
      Path targetScreen2Path = targetDir.toPath().resolve(screensDir + gameDetails.getScreen2());

      String gamesDir = favFormat ? FileManager.generateFileNameFromTitle(gameDetails.getTitle()) + "/" : "games/";
      Path gamePath = Paths.get("./games/" + gameDetails.getGame());
      Path targetGamePath = targetDir.toPath().resolve(gamesDir + gameDetails.getGame());

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

  public void clearAfterImport()
  {
    gamesList.clear();
    gameDetailsList.clear();
    targetDir = null;
    favFormat = false;
  }
}
