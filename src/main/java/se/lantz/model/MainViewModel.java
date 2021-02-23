package se.lantz.model;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
import se.lantz.model.data.ScraperFields;
import se.lantz.scraper.MobyGamesScraper;
import se.lantz.scraper.Scraper;
import se.lantz.util.FileManager;
import se.lantz.util.TextComponentSupport;

public class MainViewModel extends AbstractModel
{
  private static final Logger logger = LoggerFactory.getLogger(MainViewModel.class);

  DbConnector dbConnector;
  DefaultComboBoxModel<GameView> gameViewModel = new DefaultComboBoxModel<>();
  GameListModel gameListModel = new GameListModel();

  private GameView selectedGameView;
  private int allGamesCount = 0;
  private int favoritesCount = 0;
  private GameView allGameView;
  private GameView favoritesView;

  private InfoModel infoModel = new InfoModel();
  private JoystickModel joy1Model = new JoystickModel(true);
  private JoystickModel joy2Model = new JoystickModel(false);
  private SystemModel systemModel = new SystemModel();

  private FileManager fileManager = new FileManager(this);

  private String currentGameId = "";
  private GameDetails currentGameDetails = null;
  private PropertyChangeListener requiredFieldsListener;

  private GameListData selectedData;

  Scraper scraper = new MobyGamesScraper();

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

    resetDataChangedAfterInit();
  }
  
  public DbConnector getDbConnector()
  {
    return this.dbConnector;
  }

  @Override
  public void disableChangeNotification(boolean disable)
  {
    super.disableChangeNotification(disable);
    infoModel.disableChangeNotification(disable);
    joy1Model.disableChangeNotification(disable);
    joy2Model.disableChangeNotification(disable);
    systemModel.disableChangeNotification(disable);
  }

  protected void notifyChangeForAllModels()
  {
    infoModel.notifyChange();
    joy1Model.notifyChange();
    joy2Model.notifyChange();
    systemModel.notifyChange();
  }

  private void setupGameViews()
  {
    //Setup game views
    allGameView = new GameView(GameView.ALL_GAMES_ID);
    
    allGameView.setName("All Games");
    allGameView.setSqlQuery("");   
    selectedGameView = allGameView;
    
    //Add favorites view
    favoritesView = new GameView(GameView.FAVORITES_ID);
    favoritesView.setName("Favorites");
    favoritesView.setSqlQuery(" WHERE Favorite = 1");
    
    gameViewModel.addElement(allGameView);
    gameViewModel.addElement(favoritesView);

    List<GameView> gameViewList = dbConnector.loadGameViews();
    Collections.sort(gameViewList);
    for (GameView gameView : gameViewList)
    {
      gameViewModel.addElement(gameView);
    }
  }

  public void reloadGameViews()
  {
    this.disableChangeNotification(true);
    gameViewModel.removeAllElements();
    setupGameViews();
    this.disableChangeNotification(false);
  }

  public ListModel<GameListData> getGameListModel()
  {
    return gameListModel;
  }

  public void readGameDetails(GameListData selectedData)
  {
    this.selectedData = selectedData;
    currentGameId = selectedData.getGameId();
    if (selectedData.getGameId().isEmpty())
    {
      //Create a default GameDetails and return
      currentGameDetails = new GameDetails();
    }
    else
    {
      // Read from db, update all models after that.
      currentGameDetails = dbConnector.getGameDetails(selectedData.getGameId());
    }
    disableChangeNotification(true);

    // Map to models
    infoModel.setTitleFromDb(currentGameDetails.getTitle());

    infoModel.setTitle(currentGameDetails.getTitle());
    infoModel.setDescription(currentGameDetails.getDescription());
    infoModel.setDescriptionDe(currentGameDetails.getDescriptionDe());
    infoModel.setDescriptionFr(currentGameDetails.getDescriptionFr());
    infoModel.setDescriptionEs(currentGameDetails.getDescriptionEs());
    infoModel.setDescriptionIt(currentGameDetails.getDescriptionIt());
    infoModel.setYear(currentGameDetails.getYear());
    infoModel.setAuthor(currentGameDetails.getAuthor());
    infoModel.setGenre(currentGameDetails.getGenre());
    infoModel.setComposer(currentGameDetails.getComposer());
    infoModel.setGamesFile(currentGameDetails.getGame());
    infoModel.setCoverFile(currentGameDetails.getCover());
    infoModel.setScreens1File(currentGameDetails.getScreen1());
    infoModel.setScreens2File(currentGameDetails.getScreen2());
    infoModel.setDuplicateIndex(currentGameDetails.getDuplicateIndex());
    //Reset and images that where added previously
    infoModel.resetImagesAndOldFileNames();
    joy1Model.setConfigStringFromDb(currentGameDetails.getJoy1());
    joy2Model.setConfigStringFromDb(currentGameDetails.getJoy2());
    systemModel.setConfigStringFromDb(currentGameDetails.getSystem());
    systemModel.setVerticalShift(currentGameDetails.getVerticalShift());

    //Set empty title to trigger a change
    if (selectedData.getGameId().isEmpty())
    {
      infoModel.setTitle("");
    }
    disableChangeNotification(false);
    notifyChangeForAllModels();
    //Do not reset unsaved for new entry
    if (!selectedData.getGameId().isEmpty())
    {
      resetDataChanged();
    }
    //Reset The undo managers for a new game
    TextComponentSupport.clearUndoManagers();
  }

  public StringBuilder importGameInfo(List<String> rowValues, ImportManager.Options option, boolean addAsFavorite)
  {
    return dbConnector.importRowsInGameInfoTable(rowValues, option, addAsFavorite);
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

  public void exportGameInfoFile(GameDetails gameDetails, File targetDir, StringBuilder infoBuilder)
  {
    fileManager.exportGameInfoFile(gameDetails, targetDir, infoBuilder);
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
    resetDataChanged();
  }

  public void setSelectedGameView(GameView gameView)
  {
    this.selectedGameView = gameView;
    if (gameView != null)
    {
      this.disableChangeNotification(true);
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
        //Update favorites count
        favoritesCount = 0;
        for (GameListData gameListData : gamesList)
        {
          if (gameListData.isFavorite())
          {
            favoritesCount++;
          }
        }
        favoritesView.setGameCount(favoritesCount);
      }
      this.disableChangeNotification(false);
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

  @Override
  public void resetDataChanged()
  {
    infoModel.resetDataChanged();
    joy1Model.resetDataChanged();
    joy2Model.resetDataChanged();
    systemModel.resetDataChanged();
  }

  @Override
  public void resetDataChangedAfterInit()
  {
    infoModel.resetDataChangedAfterInit();
    joy1Model.resetDataChangedAfterInit();
    joy2Model.resetDataChangedAfterInit();
    systemModel.resetDataChangedAfterInit();
  }

  /**
   * 
   * @return true if save was successful, false if not.
   */
  public boolean saveData()
  {
    if (isDataChanged())
    {
      if (infoModel.isNewGame() || infoModel.isTitleChanged())
      {
        //Update duplicate index 
        infoModel.setDuplicateIndex(dbConnector.getGameDuplicateIndexToUse(infoModel.getTitle()));
      }
      
      if (!validateRequiredFields().isEmpty())
      {
        //Validate that all required fields are set here!!
        requiredFieldsListener.propertyChange(new PropertyChangeEvent(this, "missing", null, validateRequiredFields()));
        return false;
      }

      //Update with empty cover and/or screen before saving
      infoModel.updateCoverAndScreensIfEmpty(getSystemModel().isC64());
      
      //Update all file names to match title
      infoModel.updateFileNames();
      //Create game details
      GameDetails updatedGame = new GameDetails();
      updatedGame.setDuplicateIndex(infoModel.getDuplicateIndex());
      updatedGame.setTitle(infoModel.getTitle().replace("\"", "\"\""));
      updatedGame.setAuthor(infoModel.getAuthor().replace("\"", "\"\""));
      updatedGame.setYear(infoModel.getYear());
      updatedGame.setComposer(infoModel.getComposer().replace("\"", "\"\""));
      updatedGame.setGenre(infoModel.getGenre());
      updatedGame.setDescription(infoModel.getDescription().replace("\"", "\"\""));
      updatedGame.setDescriptionDe(infoModel.getDescriptionDe().replace("\"", "\"\""));
      updatedGame.setDescriptionFr(infoModel.getDescriptionFr().replace("\"", "\"\""));
      updatedGame.setDescriptionEs(infoModel.getDescriptionEs().replace("\"", "\"\""));
      updatedGame.setDescriptionIt(infoModel.getDescriptionIt().replace("\"", "\"\""));
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
      gameListModel.notifyChange();

      fileManager.saveFiles();
      //Reset and images that where added previously
      infoModel.resetImagesAndOldFileNames();
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
      FileManager.deleteFilesForGame(currentGameDetails);
      dbConnector.deleteGame(currentGameId);
      //Update all games count, will be reset if its All that is loaded
      allGamesCount--;
      allGameView.setGameCount(allGamesCount);
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
  
  public void deleteAllGamesInCurrentView()
  {
    //First delete all covers, screens and games 
    for (int i = 0; i < getGameListModel().getSize(); i++)
    {
      GameListData currentData = getGameListModel().getElementAt(i);   
      if (!currentData.getTitle().contains("THEC64"))
      {
        GameDetails details = dbConnector.getGameDetails(currentData.getGameId());
        FileManager.deleteFilesForGame(details);
        allGamesCount--;
        allGameView.setGameCount(allGamesCount);
      }
    }
    dbConnector.deleteAllGamesInView(getSelectedGameView());
    //Reload the current view
    reloadCurrentGameView();
  }

  public void deleteGameView(GameView view)
  {
    if (view.getGameViewId() > GameView.ALL_GAMES_ID)
    {
      dbConnector.deleteView(view);
      reloadGameViews();
    }
  }
  
  public void clearFavorites()
  {
    dbConnector.clearFavorites();
    //Reload the current view
    reloadCurrentGameView();
  }

  private List<String> validateRequiredFields()
  {
    List<String> missingFields = new ArrayList<>();
    if (infoModel.getTitle().isEmpty())
    {
      missingFields.add("Game title");
    }
    boolean missingScreen1 = false;
    if (infoModel.getScreens1File().isEmpty() && infoModel.getScreen1Image() == null)
    {
      missingScreen1 = true;
    }
    if (missingScreen1 && infoModel.getScreens2File().isEmpty() && infoModel.getScreen2Image() == null)
    {
      missingFields.add("At least one screenshot");
    }
    if (infoModel.getGamesFile().isEmpty())
    {
      missingFields.add("Game file");
    }
    return missingFields;
  }

  public void addNewGameListData()
  {
    gameListModel.addElement(new GameListData("New Game", "", 0));
    selectedGameView.setGameCount(gameListModel.getSize());
    //Update all games count 
    allGamesCount++;
    allGameView.setGameCount(allGamesCount);
  }

  public void removeNewGameListData()
  {
    if (gameListModel.get(gameListModel.getSize() - 1).getGameId().isEmpty())
    {
      gameListModel.remove(gameListModel.getSize() - 1);
      selectedGameView.setGameCount(gameListModel.getSize());
      //Update all games count 
      allGamesCount--;
      allGameView.setGameCount(allGamesCount);
      resetDataChanged();
    }
  }

  public void toggleFavorite(GameListData data)
  {
    if (data != null && !data.getGameId().isEmpty())
    {
      dbConnector.toggleFavorite(data.getGameId(), data.getFavorite());
      data.toggleFavorite();
      if (data.isFavorite())
      {
        favoritesView.setGameCount(++favoritesCount);
      }
      else
      {
        favoritesView.setGameCount(--favoritesCount);
      }
      gameListModel.notifyChange();
    }
  }

  public void runGameInVice()
  {
    fileManager.runVice(true);
  }
  
  public void runVice()
  {
    fileManager.runVice(false);
  }
}
