package se.lantz.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
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
  private boolean deleteBeforeExport = false;

  public ExportManager(MainViewModel uiModel)
  {
    this.uiModel = uiModel;
  }

  public void setGamesToExport(List<GameListData> gamesList)
  {
    this.gamesList = gamesList;
  }

  public void setTargetDirectory(File targetDir, boolean delete, boolean gamesDir)
  {
    this.deleteBeforeExport = delete;
    Path targetDirPath = targetDir.toPath();

    if (delete)
    {
      try
      {
        if (Files.exists(targetDirPath))
        {
          if (Files.exists(targetDirPath.resolve("games").resolve("games")))
          {
            //Delete entire games folder
            Files.walk(targetDirPath.resolve("games")).sorted(Comparator.reverseOrder()).map(Path::toFile)
              .forEach(File::delete);
          }
          else
          {
            //Delete covers, screens, and games folders and all tsg files
            if (Files.exists(targetDirPath.resolve("covers")))
            {
              Files.walk(targetDirPath.resolve("covers")).sorted(Comparator.reverseOrder()).map(Path::toFile)
                .forEach(File::delete);
            }
            if (Files.exists(targetDirPath.resolve("screens")))
            {
              Files.walk(targetDirPath.resolve("screens")).sorted(Comparator.reverseOrder()).map(Path::toFile)
                .forEach(File::delete);
            }
            if (Files.exists(targetDirPath.resolve("games")))
            {
              Files.walk(targetDirPath.resolve("games")).sorted(Comparator.reverseOrder()).map(Path::toFile)
                .forEach(File::delete);
            }

            Files.walk(targetDirPath, 1).filter(p -> p.toString().endsWith(".tsg")).map(Path::toFile)
              .forEach(File::delete);
          }
        }
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not delete games folder");
      }
    }

    if (gamesDir)
    {
      targetDirPath = targetDirPath.resolve("games");
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
    gameDetailsList = uiModel.readGameDetailsForExport(infoBuilder, gamesList);
  }

  public void createGameInfoFiles(StringBuilder infoBuilder, boolean fileLoader)
  {
    for (GameDetails gameDetails : gameDetailsList)
    {
      uiModel.exportGameInfoFile(gameDetails, targetDir, infoBuilder, fileLoader);
    }
  }

  public boolean isDeleteBeforeExport()
  {
    return deleteBeforeExport;
  }

  public void copyFiles(StringBuilder infoBuilder)
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
      infoBuilder.append("ERROR: Could not create directories for covers, screens and games, " + e.getMessage() + "\n");
      ExceptionHandler.handleException(e, " Could not create directories for covers, screens and games");
      return;
    }
    for (GameDetails gameDetails : gameDetailsList)
    {
      Path coverPath = Paths.get("./covers/" + gameDetails.getCover());
      Path targetCoverPath = targetDir.toPath().resolve("covers/" + gameDetails.getCover());

      Path screens1Path = Paths.get("./screens/" + gameDetails.getScreen1());
      Path targetScreen1Path = targetDir.toPath().resolve("screens/" + gameDetails.getScreen1());
      Path screens2Path = Paths.get("./screens/" + gameDetails.getScreen2());
      Path targetScreen2Path = targetDir.toPath().resolve("screens/" + gameDetails.getScreen2());

      Path gamePath = Paths.get("./games/" + gameDetails.getGame());
      Path targetGamePath = targetDir.toPath().resolve("games/" + gameDetails.getGame());

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
    try
    {
      Path targetGamePath = targetDir.toPath();
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
      Path gamePath = Paths.get("./games/" + gameDetails.getGame());
      
      //TODO: Unzip if needed, rename vsf files to prg according to Spannernick
      
      Path targetGamePath = targetDir.toPath().resolve(gameDetails.getGame());

      try
      {
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
    deleteBeforeExport = false;
  }
}
