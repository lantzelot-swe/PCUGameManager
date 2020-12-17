package se.lantz.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.db.DbConnector;
import se.lantz.manager.ImportManager;
import se.lantz.model.data.GameDetails;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;
import se.lantz.util.FileManager;

public class MainViewModel extends AbstractModel
{
  private static final Logger logger = LoggerFactory.getLogger(MainViewModel.class);

  DbConnector dbConnector;
  DefaultComboBoxModel<GameView> gameViewModel = new DefaultComboBoxModel<>();
  GameListModel gameListModel = new GameListModel();

  private GameView selectedGameView;
  private int allGamesCount = 0;

  private InfoModel infoModel = new InfoModel();
  private JoystickModel joy1Model = new JoystickModel(true);
  private JoystickModel joy2Model = new JoystickModel(false);
  private SystemModel systemModel = new SystemModel();

  private FileManager fileManager = new FileManager(infoModel);

  private String currentGameId = "";

  private PropertyChangeListener duplicateListener;

  private PropertyChangeListener requiredFieldsListener;

  private GameListData selectedData;

  public void initialize()
  {
    logger.debug("Creating DBConnector...");
    dbConnector = new DbConnector();
    logger.debug("...done.");
    setupGameViews();

    joy1Model.setPrimaryChangeListener(e -> joy2Model
      .setPrimaryWithoutListenerNotification(!Boolean.valueOf(e.getActionCommand())));

    joy2Model.setPrimaryChangeListener(e -> joy1Model
      .setPrimaryWithoutListenerNotification(!Boolean.valueOf(e.getActionCommand())));
  }
  
  private void setupGameViews()
  {
    //Setup game views
    selectedGameView = new GameView(GameView.ALL_GAMES_ID);
    selectedGameView.setName("All Games");
    selectedGameView.setSqlQuery("");
    gameViewModel.addElement(selectedGameView);
    for (GameView gameView : dbConnector.loadGameViews())
    {
      gameViewModel.addElement(gameView);
    }
  }
  
  public void reloadGameViews()
  {
    gameViewModel.removeAllElements();
    setupGameViews();
    reloadCurrentGameView();
  }

  public ListModel<GameListData> getGameListModel()
  {
    return gameListModel;
  }

  public void readGameDetails(GameListData selectedData)
  {
    this.selectedData = selectedData;
    currentGameId = selectedData.getGameId();
    GameDetails details = null;
    if (selectedData.getGameId().isEmpty())
    {
      //Create a default GameDetails and return
      details = new GameDetails();
    }
    else
    {
      // Read from db, update all models after that.
      details = dbConnector.getGameDetails(selectedData.getGameId());
    }
    // Map to models
    infoModel.setTitleFromDb(details.getTitle());

    infoModel.setTitle(details.getTitle());
    infoModel.setDescription(details.getDescription());
    infoModel.setYear(details.getYear());
    infoModel.setAuthor(details.getAuthor());
    infoModel.setGenre(details.getGenre());
    infoModel.setComposer(details.getComposer());
    infoModel.setGamesFile(details.getGame());
    infoModel.setCoverFile(details.getCover());
    infoModel.setScreens1File(details.getScreen1());
    infoModel.setScreens2File(details.getScreen2());
    //Reset and images that where added previously
    infoModel.resetImages();

    infoModel.resetDataChanged();

    joy1Model.setConfigStringFromDb(details.getJoy1());
    joy1Model.resetDataChanged();
    joy2Model.setConfigStringFromDb(details.getJoy2());
    joy2Model.resetDataChanged();
    systemModel.setConfigStringFromDb(details.getSystem());
    systemModel.setVerticalShift(details.getVerticalShift());
    systemModel.resetDataChanged();
    //Set empty title to trigger a change
    if (selectedData.getGameId().isEmpty())
    {
      infoModel.setTitle("");
    }
  }

  public StringBuilder importGameInfo(List<String> rowValues, ImportManager.Options option)
  {
    return dbConnector.importRowsInGameInfoTable(rowValues, option);
  }

  public List<GameDetails> readGameDetailsForExport(StringBuilder infoBuilder, List<GameListData> gamesList)
  {
    List<GameDetails> returnList = new ArrayList<>();
    for (GameListData game : gamesList)
    {
      infoBuilder.append("Fetching information for " + game.getTitle() + "\n");
      returnList.add(dbConnector.getGameDetails(game.getGameId()));
    }
    return returnList;
  }

  public void exportGameInfoFile(GameDetails gameDetails, File targetDir, boolean favFormat, StringBuilder infoBuilder)
  {
    fileManager.exportGameInfoFile(gameDetails, targetDir, favFormat, infoBuilder);
  }

  public InfoModel getInfoModel()
  {
    return infoModel;
  }

  public JoystickModel getJoy1Model()
  {
    return joy1Model;
  }

  public JoystickModel getJoy2Model()
  {
    return joy2Model;
  }

  public SystemModel getSystemModel()
  {
    return systemModel;
  }

  public void reloadCurrentGameView()
  {
    setSelectedGameView(getSelectedGameView());
  }

  public void setSelectedGameView(GameView gameView)
  {
    this.selectedGameView = gameView;
    if (gameView != null)
    {
      logger.debug("Fetching games for view {}...", gameView);
      gameListModel.clear();
      List<GameListData> gamesList = dbConnector.fetchGamesByView(gameView);
      for (GameListData gameListData : gamesList)
      {
        gameListModel.addElement(gameListData);
      }
      gameView.setGameCount(gamesList.size());
      if (gameView.getGameViewId() == GameView.ALL_GAMES_ID)
      {
        this.allGamesCount = gamesList.size();
      }
      logger.debug("...done.");
    }
  }

  public int getAllGamesCount()
  {
    return allGamesCount;
  }

  public GameView getSelectedGameView()
  {
    return this.selectedGameView;
  }

  public DefaultComboBoxModel<GameView> getGameViewModel()
  {
    return gameViewModel;
  }

  public void saveGameView(GameView gameView)
  {
    dbConnector.saveGameView(gameView);
  }

  public void addSaveChangeListener(PropertyChangeListener saveChangeListener)
  {
    getInfoModel().addPropertyChangeListener(saveChangeListener);
    getJoy1Model().addPropertyChangeListener(saveChangeListener);
    getJoy2Model().addPropertyChangeListener(saveChangeListener);
    getSystemModel().addPropertyChangeListener(saveChangeListener);
  }

  public void addDuplicateGameListener(PropertyChangeListener duplicateListener)
  {
    this.duplicateListener = duplicateListener;
  }

  public void addRequireFieldsListener(PropertyChangeListener requiredFieldsListener)
  {
    this.requiredFieldsListener = requiredFieldsListener;
  }

  @Override
  public boolean isDataChanged()
  {
    return infoModel.isDataChanged() || joy1Model.isDataChanged() || joy2Model.isDataChanged() ||
      systemModel.isDataChanged();
  }

  /**
   * 
   * @return true if save was successful, false if not.
   */
  public boolean saveData()
  {
    if (isDataChanged())
    {
      if ((infoModel.isNewGame() || infoModel.isTitleChanged()) && dbConnector.isGameInDb(infoModel.getTitle()))
      {
        duplicateListener.propertyChange(new PropertyChangeEvent(this, "duplicate", null, infoModel.getTitle()));
        return false;
      }

      if (!validateRequiredFields().isEmpty())
      {
        //Validate that all required fields are set here!!
        requiredFieldsListener.propertyChange(new PropertyChangeEvent(this, "missing", null, validateRequiredFields()));
        return false;
      }

      //Update all file names to match title
      infoModel.updateFileNames();
      //Create game details
      GameDetails updatedGame = new GameDetails();
      updatedGame.setTitle(infoModel.getTitle().replace("\"", "\"\""));
      updatedGame.setAuthor(infoModel.getAuthor().replace("\"", "\"\""));
      updatedGame.setYear(infoModel.getYear());
      updatedGame.setComposer(infoModel.getComposer().replace("\"", "\"\""));
      updatedGame.setGenre(infoModel.getGenre());
      updatedGame.setDescription(infoModel.getDescription().replace("\"", "\"\""));
      updatedGame.setGame(infoModel.getGamesFile());
      updatedGame.setCover(infoModel.getCoverFile());
      updatedGame.setScreen1(infoModel.getScreens1File());
      updatedGame.setScreen2(infoModel.getScreens2File());
      updatedGame.setJoy1(joy1Model.getConfigString());
      updatedGame.setJoy2(joy2Model.getConfigString());
      updatedGame.setSystem(systemModel.getConfigString());
      updatedGame.setVerticalShift(systemModel.getVerticalShift());

      if (currentGameId.isEmpty())
      {
        //Create new entry in Db
        int rowId = dbConnector.createNewGame(updatedGame);
        currentGameId = Integer.toString(rowId);
        selectedData.setGameId(currentGameId);
      }
      else
      {
        //Update with currentGameId
        dbConnector.saveGame(currentGameId, updatedGame);
      }
      selectedData.setTitle(updatedGame.getTitle());
      gameListModel.notifySave();

      fileManager.saveFiles();
      //Notify of any changes to covers/screens etc
      infoModel.notifyChange();
      //Reset all models
      infoModel.resetDataChanged();
      joy1Model.resetDataChanged();
      joy2Model.resetDataChanged();
      systemModel.resetDataChanged();

      //Update db title once done
      infoModel.setTitleFromDb(infoModel.getTitle());
      return true;
    }
    return false;
  }

  public boolean isNewGameSelected()
  {
    return currentGameId.isEmpty();
  }

  public void deleteCurrentGame()
  {
    if (isNewGameSelected())
    {
      removeNewGameListData();
    }
    else
    {
      dbConnector.deleteGame(currentGameId);
      //Reload the current view
      reloadCurrentGameView();
    }
  }

  public void deleteAllGames()
  {
    dbConnector.deleteAllGames();
    //Reload the current view
    reloadCurrentGameView();
  }

  private List<String> validateRequiredFields()
  {
    List<String> missingFields = new ArrayList<>();
    if (infoModel.getTitle().isEmpty())
    {
      missingFields.add("Title");
    }
    if (infoModel.getCoverFile().isEmpty() && infoModel.getCoverImage() == null)
    {
      missingFields.add("Cover");
    }
    if (infoModel.getScreens1File().isEmpty() && infoModel.getScreen1Image() == null)
    {
      missingFields.add("Screenshot 1");
    }
    if (infoModel.getScreens2File().isEmpty() && infoModel.getScreen2Image() == null)
    {
      missingFields.add("Screenshot 2");
    }
    if (infoModel.getGamesFile().isEmpty())
    {
      missingFields.add("Game file");
    }
    return missingFields;
  }

  public void addNewGameListData()
  {
    gameListModel.addElement(new GameListData("New Game", ""));
  }

  public void removeNewGameListData()
  {
    if (gameListModel.get(gameListModel.getSize() - 1).getGameId().isEmpty())
    {
      gameListModel.remove(gameListModel.getSize() - 1);
    }
  }
}
