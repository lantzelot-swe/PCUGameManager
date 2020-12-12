package se.lantz.manager;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameDetails;
import se.lantz.model.data.GameListData;

public class ExportManager
{
  private static final Logger logger = LoggerFactory.getLogger(ExportManager.class);
  private List<GameListData> gamesList;
  private List<GameDetails> gameDetailsList;
  private File targetDir;
  private MainViewModel uiModel;
  
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
    this.targetDir = targetDir;
  }
  
  public void readFromDb(StringBuilder infoBuilder)
  {
    gameDetailsList = uiModel.readGameDetailsForExport(infoBuilder, gamesList);
  }
  
  public void createGameInfoFiles(StringBuilder infoBuilder)
  {
    for (GameDetails gameDetails : gameDetailsList)
    {
      uiModel.exportGame(gameDetails, targetDir, infoBuilder);
    }
  }

}
