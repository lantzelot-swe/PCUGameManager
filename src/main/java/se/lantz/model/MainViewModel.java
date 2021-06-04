package se.lantz.model;

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
  private int favorites1Count = 0;
  private int favorites2Count = 0;
  private int favorites3Count = 0;
  private int favorites4Count = 0;
  private int favorites5Count = 0;
  private int favorites6Count = 0;
  private int favorites7Count = 0;
  private int favorites8Count = 0;
  private int favorites9Count = 0;
  private int favorites10Count = 0;
  private GameView allGameView;
  private GameView favorites1View;
  private GameView favorites2View;
  private GameView favorites3View;
  private GameView favorites4View;
  private GameView favorites5View;
  private GameView favorites6View;
  private GameView favorites7View;
  private GameView favorites8View;
  private GameView favorites9View;
  private GameView favorites10View;

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

    //Add favorites views
    favorites1View = new GameView(GameView.FAVORITES_ID);
    favorites1View.setName("Favorites 1");
    favorites1View.setSqlQuery(" WHERE Favorite = 1");

    favorites2View = new GameView(GameView.FAVORITES_2_ID);
    favorites2View.setName("Favorites 2");
    favorites2View.setSqlQuery(" WHERE Favorite = 2");

    favorites3View = new GameView(GameView.FAVORITES_3_ID);
    favorites3View.setName("Favorites 3");
    favorites3View.setSqlQuery(" WHERE Favorite = 3");

    favorites4View = new GameView(GameView.FAVORITES_4_ID);
    favorites4View.setName("Favorites 4");
    favorites4View.setSqlQuery(" WHERE Favorite = 4");

    favorites5View = new GameView(GameView.FAVORITES_5_ID);
    favorites5View.setName("Favorites 5");
    favorites5View.setSqlQuery(" WHERE Favorite = 5");

    favorites6View = new GameView(GameView.FAVORITES_6_ID);
    favorites6View.setName("Favorites 6");
    favorites6View.setSqlQuery(" WHERE Favorite = 6");

    favorites7View = new GameView(GameView.FAVORITES_7_ID);
    favorites7View.setName("Favorites 7");
    favorites7View.setSqlQuery(" WHERE Favorite = 7");

    favorites8View = new GameView(GameView.FAVORITES_8_ID);
    favorites8View.setName("Favorites 8");
    favorites8View.setSqlQuery(" WHERE Favorite = 8");

    favorites9View = new GameView(GameView.FAVORITES_9_ID);
    favorites9View.setName("Favorites 9");
    favorites9View.setSqlQuery(" WHERE Favorite = 9");

    favorites10View = new GameView(GameView.FAVORITES_10_ID);
    favorites10View.setName("Favorites 10");
    favorites10View.setSqlQuery(" WHERE Favorite = 10");

    gameViewModel.addElement(allGameView);
    gameViewModel.addElement(favorites1View);
    gameViewModel.addElement(favorites2View);
    gameViewModel.addElement(favorites3View);
    gameViewModel.addElement(favorites4View);
    gameViewModel.addElement(favorites5View);
    gameViewModel.addElement(favorites6View);
    gameViewModel.addElement(favorites7View);
    gameViewModel.addElement(favorites8View);
    gameViewModel.addElement(favorites9View);
    gameViewModel.addElement(favorites10View);

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
    infoModel.setViewTag(currentGameDetails.getViewTag());
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

  public StringBuilder importGameInfo(List<String> rowValues,
                                      ImportManager.Options option,
                                      int addAsFavorite,
                                      String viewTag)
  {
    return dbConnector.importRowsInGameInfoTable(rowValues, option, addAsFavorite, viewTag);
  }

  public void cleanupAfterImport()
  {
    dbConnector.cleanupAfterImport();
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
  
  public List<GameDetails> readGameDetailsForGameView(StringBuilder infoBuilder, GameView gameView)
  {
    List<GameListData> gamesList = dbConnector.fetchGamesByView(gameView);
    return readGameDetailsForExport(infoBuilder, gamesList);
  }

  public void exportGameInfoFile(GameDetails gameDetails, File targetDir, StringBuilder infoBuilder, boolean fileLoader)
  {
    fileManager.exportGameInfoFile(gameDetails, targetDir, infoBuilder, fileLoader);
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

      long start = System.currentTimeMillis();
      List<GameListData> gamesList = dbConnector.fetchGamesByView(gameView);
      logger.debug("Fetched all games from db in " + (System.currentTimeMillis() - start) + " ms");
      gameListModel.addAllGames(gamesList);
      gameView.setGameCount(gamesList.size());
      if (gameView.getGameViewId() == GameView.ALL_GAMES_ID)
      {
        this.allGamesCount = gamesList.size();
        //Update favorites count
        favorites1Count = 0;
        favorites2Count = 0;
        favorites3Count = 0;
        favorites4Count = 0;
        favorites5Count = 0;
        favorites6Count = 0;
        favorites7Count = 0;
        favorites8Count = 0;
        favorites9Count = 0;
        favorites10Count = 0;
        for (GameListData gameListData : gamesList)
        {
          if (gameListData.isFavorite())
          {
            switch (gameListData.getFavoriteNumber())
            {
            case 1:
              favorites1Count++;
              break;
            case 2:
              favorites2Count++;
              break;
            case 3:
              favorites3Count++;
              break;
            case 4:
              favorites4Count++;
              break;
            case 5:
              favorites5Count++;
              break;
            case 6:
              favorites6Count++;
              break;
            case 7:
              favorites7Count++;
              break;
            case 8:
              favorites8Count++;
              break;
            case 9:
              favorites9Count++;
              break;
            case 10:
              favorites10Count++;
              break;
            default:
              break;
            }
          }
        }
        favorites1View.setGameCount(favorites1Count);
        favorites2View.setGameCount(favorites2Count);
        favorites3View.setGameCount(favorites3Count);
        favorites4View.setGameCount(favorites4Count);
        favorites5View.setGameCount(favorites5Count);
        favorites6View.setGameCount(favorites6Count);
        favorites7View.setGameCount(favorites7Count);
        favorites8View.setGameCount(favorites8Count);
        favorites9View.setGameCount(favorites9Count);
        favorites10View.setGameCount(favorites10Count);
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
      updatedGame.setViewTag(infoModel.getViewTag());

      if (currentGameId.isEmpty())
      {
        //Create new entry in Db
        int rowId = dbConnector.createNewGame(updatedGame);
        currentGameId = Integer.toString(rowId);
        selectedData.setGameId(currentGameId);
        if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_ID)
        {
          toggleFavorite(selectedData);
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_2_ID)
        {
          toggleFavorite2(selectedData);
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_3_ID)
        {
          toggleFavorite3(selectedData);
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_4_ID)
        {
          toggleFavorite4(selectedData);
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_5_ID)
        {
          toggleFavorite5(selectedData);
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_6_ID)
        {
          toggleFavorite6(selectedData);
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_7_ID)
        {
          toggleFavorite7(selectedData);
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_8_ID)
        {
          toggleFavorite8(selectedData);
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_9_ID)
        {
          toggleFavorite9(selectedData);
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_10_ID)
        {
          toggleFavorite10(selectedData);
        }
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
      if (!currentData.getTitle().contains("THEC64") || currentData.getTitle().contains("VIC20"))
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

  public void clearFavorites(int number)
  {
    dbConnector.clearFavorites(number);
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

  private void reduceFavoriteCount(int previousFavorite)
  {
    switch (previousFavorite)
    {
    case 1:
      favorites1View.setGameCount(--favorites1Count);
      break;
    case 2:
      favorites2View.setGameCount(--favorites2Count);
      break;
    case 3:
      favorites3View.setGameCount(--favorites3Count);
      break;
    case 4:
      favorites4View.setGameCount(--favorites4Count);
      break;
    case 5:
      favorites5View.setGameCount(--favorites5Count);
      break;
    case 6:
      favorites6View.setGameCount(--favorites6Count);
      break;
    case 7:
      favorites7View.setGameCount(--favorites7Count);
      break;
    case 8:
      favorites8View.setGameCount(--favorites8Count);
      break;
    case 9:
      favorites9View.setGameCount(--favorites9Count);
      break;
    case 10:
      favorites10View.setGameCount(--favorites10Count);
      break;
    default:
      break;
    }
  }

  public void toggleFavorite(GameListData data)
  {
    favorites1Count = toggleFavorite(data, 1, favorites1Count, favorites1View);
  }

  public void toggleFavorite2(GameListData data)
  {
    favorites2Count = toggleFavorite(data, 2, favorites2Count, favorites2View);
  }

  public void toggleFavorite3(GameListData data)
  {
    favorites3Count = toggleFavorite(data, 3, favorites3Count, favorites3View);
  }

  public void toggleFavorite4(GameListData data)
  {
    favorites4Count = toggleFavorite(data, 4, favorites4Count, favorites4View);
  }

  public void toggleFavorite5(GameListData data)
  {
    favorites5Count = toggleFavorite(data, 5, favorites5Count, favorites5View);
  }

  public void toggleFavorite6(GameListData data)
  {
    favorites6Count = toggleFavorite(data, 6, favorites6Count, favorites6View);
  }

  public void toggleFavorite7(GameListData data)
  {
    favorites7Count = toggleFavorite(data, 7, favorites7Count, favorites7View);
  }

  public void toggleFavorite8(GameListData data)
  {
    favorites8Count = toggleFavorite(data, 8, favorites8Count, favorites8View);
  }

  public void toggleFavorite9(GameListData data)
  {
    favorites9Count = toggleFavorite(data, 9, favorites9Count, favorites9View);
  }

  public void toggleFavorite10(GameListData data)
  {
    favorites10Count = toggleFavorite(data, 10, favorites10Count, favorites10View);
  }

  private int toggleFavorite(GameListData data, int favoritesNumber, int favoritesCount, GameView favoritesView)
  {
    if (data != null && !data.getGameId().isEmpty())
    {
      int previousFavorite = data.getFavorite();
      dbConnector.toggleFavorite(data.getGameId(), previousFavorite, favoritesNumber);
      data.toggleFavorite(favoritesNumber);
      if (data.isFavorite())
      {
        favoritesView.setGameCount(++favoritesCount);
        reduceFavoriteCount(previousFavorite);
      }
      else
      {
        favoritesView.setGameCount(--favoritesCount);
      }
      gameListModel.notifyChange();
    }
    return favoritesCount;
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
