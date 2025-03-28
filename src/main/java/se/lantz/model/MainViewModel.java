package se.lantz.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.db.DbConnector;
import se.lantz.gui.exports.PublishWorker;
import se.lantz.manager.ImportManager;
import se.lantz.manager.SavedStatesManager;
import se.lantz.model.data.GameDetails;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;
import se.lantz.scraper.MobyGamesScraper;
import se.lantz.scraper.Scraper;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;
import se.lantz.util.TextComponentSupport;

public class MainViewModel extends AbstractModel
{
  public static final String DB_TAB_ORDER = "dbTabOrder";

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
  private SavedStatesModel stateModel = new SavedStatesModel();

  private FileManager fileManager = new FileManager(this);
  private SavedStatesManager stateManager;

  private String currentGameId = "";
  private GameDetails currentGameDetails = null;
  private PropertyChangeListener requiredFieldsListener;

  private GameListData selectedData;

  Scraper scraper = new MobyGamesScraper();

  private int numberOfFavoritesViews = 10;

  private boolean notifyGameListChange = true;
  private boolean notifyGameSelected = true;

  private List<String> availableDatabases = new ArrayList<>();
  private String selectedDatabase = "Main";
  
  private List<GameView> currentGameViewList;

  public MainViewModel()
  {
    //Read available databases and preferences for them
    try (Stream<Path> stream = Files.list(Paths.get("./databases")))
    {
      availableDatabases = stream.filter(file -> Files.isDirectory(file)).map(Path::getFileName).map(Path::toString)
        .collect(Collectors.toList());
    }
    catch (IOException ex)
    {
      ExceptionHandler.handleException(ex, "Could not read databases");
    }

    if (availableDatabases.isEmpty())
    {
      try
      {
        //We need at least one database. Create a "MainDb" folder with an empty db
        addTab("MainDb");
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not create MainDb");
      }
    }
    else
    {
      //Read preferences for tab order
      Properties configuredProperties = FileManager.getConfiguredProperties();
      String tabOrder = (String) configuredProperties.get(DB_TAB_ORDER);
      if (tabOrder != null)
      {
        List<String> preferencesOrder = Arrays.asList(tabOrder.split("\\s*,\\s*"));

        Collections.sort(availableDatabases, Comparator.comparing(item -> preferencesOrder.indexOf(item)));
      }

      selectedDatabase = availableDatabases.get(0);
      FileManager.setCurrentDbFolder(selectedDatabase);
      DbConnector.setCurrentDbFolder(selectedDatabase);
    }
  }

  /**
   * Used from export dialog
   * 
   * @param currentDatabase The current DB in the main instance of the model
   */
  public MainViewModel(String currentDatabase)
  {
    selectedDatabase = currentDatabase;
  }

  public void updateDbTabPreferences(String prefValue)
  {
    Properties configuredProperties = FileManager.getConfiguredProperties();
    configuredProperties.put(DB_TAB_ORDER, prefValue);
  }

  public void renameTab(String oldName, String newName) throws IOException
  {
    Path source = Paths.get("./databases" + "/" + oldName);
    Files.move(source, source.resolveSibling(newName));
    selectedDatabase = newName;
    availableDatabases.set(availableDatabases.indexOf(oldName), newName);
    FileManager.setCurrentDbFolder(selectedDatabase);
    DbConnector.setCurrentDbFolder(selectedDatabase);
  }

  public void deleteTab(String dbName) throws IOException
  {
    availableDatabases.remove(dbName);

    Path source = Paths.get("./databases" + "/" + dbName);

    Files.walk(source).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
  }

  public void addTab(String name) throws IOException
  {
    FileManager.createNewDb(name);
    availableDatabases.add(name);
  }

  public void setSavedStatesManager(SavedStatesManager savedStatesManager)
  {
    this.stateManager = savedStatesManager;
  }

  public SavedStatesManager getSavedStatesManager()
  {
    return this.stateManager;
  }

  public void initialize()
  {
    logger.debug("Creating DBConnector...");
    //Pass the selected db here
    dbConnector = new DbConnector(availableDatabases);
    DbConnector.setCurrentDbFolder(selectedDatabase);
    logger.debug("...done.");
    setupGameViews(false);

    joy1Model.setPrimaryChangeListener(e -> joy2Model
      .setPrimaryWithoutListenerNotification(!Boolean.valueOf(e.getActionCommand())));

    joy2Model.setPrimaryChangeListener(e -> joy1Model
      .setPrimaryWithoutListenerNotification(!Boolean.valueOf(e.getActionCommand())));

    resetDataChangedAfterInit();
    fileManager.setDbConnector(dbConnector);
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
    stateModel.disableChangeNotification(disable);
  }

  protected void notifyChangeForAllModels()
  {
    infoModel.notifyChange();
    joy1Model.notifyChange();
    joy2Model.notifyChange();
    systemModel.notifyChange();
    stateModel.notifyChange();
  }

  private void setupGameViews(boolean updateGameCount)
  {
    notifyGameListChange = false;
    notifyGameSelected = false;
    numberOfFavoritesViews = FileManager.getConfiguredNumberOfFavorites();
    //Setup game views
    allGameView = new GameView(GameView.ALL_GAMES_ID);

    allGameView.setName("All Games");
    allGameView.setSqlQuery("");
    selectedGameView = allGameView;

    //Add favorites views
    favorites1View = new GameView(GameView.FAVORITES_ID);
    favorites1View.setName(FileManager.getConfiguredFavGameViewName(1));
    favorites1View.setSqlQuery("WHERE Favorite = 1");

    gameViewModel.addElement(allGameView);
    gameViewModel.addElement(favorites1View);

    if (numberOfFavoritesViews > 1)
    {
      favorites2View = new GameView(GameView.FAVORITES_2_ID);
      favorites2View.setName(FileManager.getConfiguredFavGameViewName(2));
      favorites2View.setSqlQuery("WHERE Favorite = 2");
      gameViewModel.addElement(favorites2View);
    }
    if (numberOfFavoritesViews > 2)
    {
      favorites3View = new GameView(GameView.FAVORITES_3_ID);
      favorites3View.setName(FileManager.getConfiguredFavGameViewName(3));
      favorites3View.setSqlQuery("WHERE Favorite = 3");
      gameViewModel.addElement(favorites3View);
    }
    if (numberOfFavoritesViews > 3)
    {
      favorites4View = new GameView(GameView.FAVORITES_4_ID);
      favorites4View.setName(FileManager.getConfiguredFavGameViewName(4));
      favorites4View.setSqlQuery("WHERE Favorite = 4");
      gameViewModel.addElement(favorites4View);
    }
    if (numberOfFavoritesViews > 4)
    {
      favorites5View = new GameView(GameView.FAVORITES_5_ID);
      favorites5View.setName(FileManager.getConfiguredFavGameViewName(5));
      favorites5View.setSqlQuery("WHERE Favorite = 5");
      gameViewModel.addElement(favorites5View);
    }
    if (numberOfFavoritesViews > 5)
    {
      favorites6View = new GameView(GameView.FAVORITES_6_ID);
      favorites6View.setName(FileManager.getConfiguredFavGameViewName(6));
      favorites6View.setSqlQuery("WHERE Favorite = 6");
      gameViewModel.addElement(favorites6View);
    }
    if (numberOfFavoritesViews > 6)
    {
      favorites7View = new GameView(GameView.FAVORITES_7_ID);
      favorites7View.setName(FileManager.getConfiguredFavGameViewName(7));
      favorites7View.setSqlQuery("WHERE Favorite = 7");
      gameViewModel.addElement(favorites7View);
    }
    if (numberOfFavoritesViews > 7)
    {
      favorites8View = new GameView(GameView.FAVORITES_8_ID);
      favorites8View.setName(FileManager.getConfiguredFavGameViewName(8));
      favorites8View.setSqlQuery("WHERE Favorite = 8");
      gameViewModel.addElement(favorites8View);
    }
    if (numberOfFavoritesViews > 8)
    {
      favorites9View = new GameView(GameView.FAVORITES_9_ID);
      favorites9View.setName(FileManager.getConfiguredFavGameViewName(9));
      favorites9View.setSqlQuery("WHERE Favorite = 9");
      gameViewModel.addElement(favorites9View);
    }
    if (numberOfFavoritesViews > 9)
    {
      favorites10View = new GameView(GameView.FAVORITES_10_ID);
      favorites10View.setName(FileManager.getConfiguredFavGameViewName(10));
      favorites10View.setSqlQuery("WHERE Favorite = 10");
      gameViewModel.addElement(favorites10View);
    }
    
    //Select the last favorites to get count on all (not sure why that works...)
    gameViewModel.setSelectedItem(gameViewModel.getElementAt(gameViewModel.getSize() - 1));

    currentGameViewList = dbConnector.loadGameViews();
    Collections.sort(currentGameViewList);

    for (GameView gameView : currentGameViewList)
    {
      gameViewModel.addElement(gameView);
      if (updateGameCount || gameView.getGameCount() < 0)
      {
        //Select each gameview to load all games so that the game count is shown directly (may be a performance issue?)
        gameViewModel.setSelectedItem(gameView);
      }
    }

    //Do with invokeLater since it's used when selecting a game also
    SwingUtilities.invokeLater(() -> notifyGameSelected = true);
    //Finish by selecting all games view again
    gameViewModel.setSelectedItem(allGameView);
    notifyGameListChange = true;
  }

  public void reloadGameViews(boolean updateGameCount)
  {
    gameViewModel.removeAllElements();
    setupGameViews(updateGameCount);
  }

  public GameListModel getGameListModel()
  {
    return gameListModel;
  }

  public void readGameDetails(GameListData selectedData)
  {
    this.selectedData = selectedData;
    currentGameId = selectedData.getGameId();
    disableChangeNotification(true);

    //Clear some properties first
    infoModel.resetImagesAndOldFileNames();
    infoModel.setTitleFromDb("");

    if (selectedData.getGameId().isEmpty())
    {
      //Create a default GameDetails
      currentGameDetails = new GameDetails();
      infoModel.setTitle("");
      if (selectedData.isInfoSlot())
      {
        generateInfoSlot(currentGameDetails);
      }
    }
    else
    {
      // Read from db, update all models after that.
      currentGameDetails = dbConnector.getGameDetails(selectedData.getGameId());
      infoModel.setTitleFromDb(currentGameDetails.getTitle());
    }

    // Map to models
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
    infoModel.setDisk2File(currentGameDetails.getDisk2());
    infoModel.setDisk3File(currentGameDetails.getDisk3());
    infoModel.setDisk4File(currentGameDetails.getDisk4());
    infoModel.setDisk5File(currentGameDetails.getDisk5());
    infoModel.setDisk6File(currentGameDetails.getDisk6());
    joy1Model.setConfigStringFromDb(currentGameDetails.getJoy1());
    joy2Model.setConfigStringFromDb(currentGameDetails.getJoy2());
    systemModel.setConfigStringFromDb(currentGameDetails.getSystem());
    systemModel.setVerticalShift(currentGameDetails.getVerticalShift());
    //Read available saved states
    stateManager.readSavedStates();

    disableChangeNotification(false);
    notifyChangeForAllModels();
    //Do not reset unsaved for new entry
    if (!selectedData.getGameId().isEmpty())
    {
      resetDataChanged();
    }
    //Reset The undo managers for a new game
    TextComponentSupport.clearUndoManagers();

    if (selectedData.isInfoSlot() && selectedData.getGameId().isEmpty())
    {
      //Trigger a save directly when adding a info slot
      saveData();
    }
    if (notifyGameSelected)
    {
      //Notify that a new game has been selected
      notifyChange("gameSelected", null, "");
    }
  }

  public StringBuilder importGameInfo(List<String> rowValues,
                                      ImportManager.Options option,
                                      int addAsFavorite,
                                      String viewTag,
                                      int gameViewId)
  {
    return dbConnector.importRowsInGameInfoTable(rowValues, option, addAsFavorite, viewTag, gameViewId);
  }

  public void cleanupAfterImport()
  {
    dbConnector.cleanupAfterImport();
  }

  public List<GameDetails> readGameDetailsForExport(PublishWorker worker, List<GameListData> gamesList)
  {
    List<GameDetails> returnList = new ArrayList<>();
    for (GameListData game : gamesList)
    {
      worker.publishMessage("Fetching information for " + game.getTitle());
      returnList.add(dbConnector.getGameDetails(game.getGameId()));
    }
    return returnList;
  }

  public List<GameDetails> readGameDetailsForGameView(PublishWorker worker, GameView gameView)
  {
    List<GameListData> gamesList = dbConnector.fetchGamesByView(gameView);
    return readGameDetailsForExport(worker, gamesList);
  }

  public int getCurrentGameViewGameCount()
  {
    return dbConnector.fetchGamesByView(getSelectedGameView()).size();
  }

  public List<GameDetails> readGameDetailsForCarouselPreview()
  {
    List<GameDetails> returnList = new ArrayList<>();
    GameListData current = this.selectedData;
    List<GameListData> gamesInView = dbConnector.fetchGamesByView(getSelectedGameView());
    int selectedGameIndex = gamesInView.indexOf(current);

    if (selectedGameIndex < 0 || gamesInView.size() < 10)
    {
      return returnList;
    }

    int start = selectedGameIndex - 4;
    int end = selectedGameIndex + 6;

    List<GameListData> subList = new ArrayList<>(gamesInView
      .subList(start < 0 ? 0 : start, end > (gamesInView.size() - 1) ? gamesInView.size() : end));

    //Add to beginning if negative
    for (int i = 0; i < -start; i++)
    {
      int listIndex = gamesInView.size() - 1 - i;
      subList.add(0, gamesInView.get(listIndex));
    }

    //Add at end if larger than list size
    for (int i = 0; i < (end - gamesInView.size()); i++)
    {
      subList.add(gamesInView.get(i));
    }

    for (GameListData game : subList)
    {
      //find the selected position in the list and return 4 before and 5 after
      returnList.add(dbConnector.getGameDetails(game.getGameId()));
    }
    return returnList;
  }

  public GameDetails getNextGameDetailsWhenScrolling(boolean scrollingRight)
  {
    GameListData current = this.selectedData;
    List<GameListData> gamesInView = dbConnector.fetchGamesByView(getSelectedGameView());
    int selectedGameIndex = gamesInView.indexOf(current);
    int indexToAdd = 0;
    if (scrollingRight)
    {
      indexToAdd = selectedGameIndex + 5;
      if (indexToAdd > gamesInView.size() - 1)
      {
        indexToAdd = indexToAdd - gamesInView.size();
      }
    }
    else
    {
      indexToAdd = selectedGameIndex - 4;
      if (indexToAdd < 0)
      {
        indexToAdd = gamesInView.size() - 1 + indexToAdd;
      }
    }
    return dbConnector.getGameDetails(gamesInView.get(indexToAdd).getGameId());
  }

  public void exportGameInfoFile(GameDetails gameDetails, File targetDir, PublishWorker worker, boolean fileLoader)
  {
    fileManager.exportGameInfoFile(gameDetails, targetDir, worker, fileLoader);
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

  public SavedStatesModel getSavedStatesModel()
  {
    return stateModel;
  }

  public void reloadCurrentGameView()
  {
    setSelectedGameView(getSelectedGameView());
    //Update the available saves states map also
    this.stateManager.readSavedStatesAndUpdateMap();
    resetDataChanged();
  }

  public void setSelectedGameView(GameView gameView)
  {
    if (gameView == null)
    {
      //Use all games view if null is passed here
      gameView = allGameView;
    }
    this.selectedGameView = gameView;

    this.disableChangeNotification(true);
    logger.debug("Fetching games for view {}...", gameView);

    long start = System.currentTimeMillis();
    List<GameListData> gamesList = dbConnector.fetchGamesByView(gameView);
    logger.debug("Fetched all games from db in " + (System.currentTimeMillis() - start) + " ms");
    gameListModel.addAllGames(gamesList);
    gameView.setGameCount(gamesList.size());
    gameView.setFileCount(getFileCount(gamesList));
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
      //Fetch all entries to be able to update counts with infoslots also
      gamesList = dbConnector.fetchAllGamesForGameCount();
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
      if (favorites2View != null)
      {
        favorites2View.setGameCount(favorites2Count);
      }
      if (favorites3View != null)
      {
        favorites3View.setGameCount(favorites3Count);
      }
      if (favorites4View != null)
      {
        favorites4View.setGameCount(favorites4Count);
      }
      if (favorites5View != null)
      {
        favorites5View.setGameCount(favorites5Count);
      }
      if (favorites6View != null)
      {
        favorites6View.setGameCount(favorites6Count);
      }
      if (favorites7View != null)
      {
        favorites7View.setGameCount(favorites7Count);
      }
      if (favorites8View != null)
      {
        favorites8View.setGameCount(favorites8Count);
      }
      if (favorites9View != null)
      {
        favorites9View.setGameCount(favorites9Count);
      }
      if (favorites10View != null)
      {
        favorites10View.setGameCount(favorites10Count);
      }
    }
    this.disableChangeNotification(false);
    if (notifyGameListChange)
    {
      this.notifyChange("selectedGamelistView", null, null);
    }
    logger.debug("...done.");
  }

  public int getFileCount(List<GameListData> gamesList)
  {
    int fileCount = 0;
    for (GameListData gameListData : gamesList)
    {
      fileCount = fileCount + gameListData.getFileCount();
    }
    return fileCount;
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

  public void createAndUpdateGameViewForImportedGBGames(String mainGameView)
  {
    dbConnector.createAndUpdateGameViewForImportedGBGames(mainGameView);
  }

  public void addSaveChangeListener(PropertyChangeListener saveChangeListener)
  {
    getInfoModel().addPropertyChangeListener(saveChangeListener);
    getJoy1Model().addPropertyChangeListener(saveChangeListener);
    getJoy2Model().addPropertyChangeListener(saveChangeListener);
    getSystemModel().addPropertyChangeListener(saveChangeListener);
    getSavedStatesModel().addPropertyChangeListener(saveChangeListener);
  }

  public void addRequiredFieldsListener(PropertyChangeListener requiredFieldsListener)
  {
    this.requiredFieldsListener = requiredFieldsListener;
  }

  public boolean isInfoSlotAvailableForCurrentView()
  {
    for (int i = 0; i < this.gameListModel.size(); i++)
    {
      if (this.gameListModel.elementAt(i).isInfoSlot())
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isDataChanged()
  {
    return infoModel.isDataChanged() || joy1Model.isDataChanged() || joy2Model.isDataChanged() ||
      systemModel.isDataChanged() || stateModel.isDataChanged();
  }

  @Override
  public void resetDataChanged()
  {
    infoModel.resetDataChanged();
    joy1Model.resetDataChanged();
    joy2Model.resetDataChanged();
    systemModel.resetDataChanged();
    stateModel.resetDataChanged();
  }

  @Override
  public void resetDataChangedAfterInit()
  {
    infoModel.resetDataChangedAfterInit();
    joy1Model.resetDataChangedAfterInit();
    joy2Model.resetDataChangedAfterInit();
    systemModel.resetDataChangedAfterInit();
    stateModel.resetDataChangedAfterInit();
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
        infoModel.setDuplicateIndex(dbConnector.getGameDuplicateIndexToUse(infoModel.getTitle(),
                                                                           this.selectedData.getGameId()));
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
      //If no genre selected use "adventure".
      updatedGame.setGenre(infoModel.getGenre().isEmpty() ? "adventure" : infoModel.getGenre());
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
      updatedGame.setDisk2(infoModel.getDisk2File());
      updatedGame.setDisk3(infoModel.getDisk3File());
      updatedGame.setDisk4(infoModel.getDisk4File());
      updatedGame.setDisk5(infoModel.getDisk5File());
      updatedGame.setDisk6(infoModel.getDisk6File());

      if (currentGameId.isEmpty())
      {
        //Create new entry in Db
        int rowId = dbConnector.createNewGame(updatedGame);
        currentGameId = Integer.toString(rowId);
        selectedData.setGameId(currentGameId);
        if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_ID)
        {
          toggleFavorite(Arrays.asList(selectedData));
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_2_ID)
        {
          toggleFavorite2(Arrays.asList(selectedData));
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_3_ID)
        {
          toggleFavorite3(Arrays.asList(selectedData));
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_4_ID)
        {
          toggleFavorite4(Arrays.asList(selectedData));
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_5_ID)
        {
          toggleFavorite5(Arrays.asList(selectedData));
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_6_ID)
        {
          toggleFavorite6(Arrays.asList(selectedData));
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_7_ID)
        {
          toggleFavorite7(Arrays.asList(selectedData));
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_8_ID)
        {
          toggleFavorite8(Arrays.asList(selectedData));
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_9_ID)
        {
          toggleFavorite9(Arrays.asList(selectedData));
        }
        else if (getSelectedGameView().getGameViewId() == GameView.FAVORITES_10_ID)
        {
          toggleFavorite10(Arrays.asList(selectedData));
        }
      }
      else
      {
        //Update with currentGameId
        dbConnector.saveGame(currentGameId, updatedGame);
      }
      selectedData.setTitle(updatedGame.getTitle());
      selectedData.setGameFileName(updatedGame.getGame());
      gameListModel.notifyChange();

      fileManager.saveFiles();
      stateManager.saveSavedStates();

      //Reset and images that where added previously
      infoModel.resetImagesAndOldFileNames();
      //Reset all models
      infoModel.resetDataChanged();
      joy1Model.resetDataChanged();
      joy2Model.resetDataChanged();
      systemModel.resetDataChanged();
      stateModel.resetDataChanged();

      //Update db title once done
      infoModel.setTitleFromDb(infoModel.getTitle());
      //Update currentGameDetails
      this.currentGameDetails = dbConnector.getGameDetails(selectedData.getGameId());
      //Update data in list model
      //      this.gameListModel.updateSavedGame(selectedData);
      this.notifyChange("gameSaved");
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
    }
  }

  public void deleteGames(List<GameListData> selectedGameListData)
  {
    //First delete all covers, screens and games 
    for (GameListData gameListData : selectedGameListData)
    {
      GameDetails details = dbConnector.getGameDetails(gameListData.getGameId());
      FileManager.deleteFilesForGame(details);
      allGamesCount--;
      allGameView.setGameCount(allGamesCount);
    }
    dbConnector.deleteGames(selectedGameListData);
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
      GameDetails details = dbConnector.getGameDetails(currentData.getGameId());
      FileManager.deleteFilesForGame(details);
      allGamesCount--;
      allGameView.setGameCount(allGamesCount);
    }
    dbConnector.deleteAllGamesInView(getSelectedGameView());
    //Reload the current view
    reloadCurrentGameView();
  }

  public void deleteAllGameListViews()
  {
    dbConnector.deleteAllGameListViews();
  }

  public void deleteGameView(GameView view)
  {
    if (view.getGameViewId() > GameView.ALL_GAMES_ID)
    {
      dbConnector.deleteView(view);
      gameViewModel.removeElement(view);
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
    if (infoModel.getGamesFile().isEmpty() && !systemModel.isBasic())
    {
      missingFields.add("Game file");
    }
    return missingFields;
  }

  public void addNewGameListData()
  {
    gameListModel.addElement(new GameListData("New Game", "", "", 0, false));
    selectedGameView.setGameCount(gameListModel.getSize());
    //Update all games count 
    allGamesCount++;
    allGameView.setGameCount(allGamesCount);
  }

  public void addNewInfoSlotData()
  {
    int favorite = selectedGameView.getGameViewId() < -1 ? selectedGameView.getGameViewId() : 0;

    gameListModel.addElement(new GameListData("New Info Slot", "", "", favorite, true));
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
      favorites2Count--;
      if (favorites2View != null)
      {
        favorites2View.setGameCount(favorites2Count);
      }
      break;
    case 3:
      favorites3Count--;
      if (favorites3View != null)
      {
        favorites3View.setGameCount(favorites3Count);
      }
      break;
    case 4:
      favorites4Count--;
      if (favorites4View != null)
      {
        favorites4View.setGameCount(favorites4Count);
      }
      break;
    case 5:
      favorites5Count--;
      if (favorites5View != null)
      {
        favorites5View.setGameCount(favorites5Count);
      }
      break;
    case 6:
      favorites6Count--;
      if (favorites6View != null)
      {
        favorites6View.setGameCount(favorites6Count);
      }
      break;
    case 7:
      favorites7Count--;
      if (favorites7View != null)
      {
        favorites7View.setGameCount(favorites7Count);
      }
      break;
    case 8:
      favorites8Count--;
      if (favorites8View != null)
      {
        favorites8View.setGameCount(favorites8Count);
      }
      break;
    case 9:
      favorites9Count--;
      if (favorites9View != null)
      {
        favorites9View.setGameCount(favorites9Count);
      }
      break;
    case 10:
      favorites10Count--;
      if (favorites10View != null)
      {
        favorites10View.setGameCount(favorites10Count);
      }
      break;
    default:
      break;
    }
  }

  public void toggleFavorite(List<GameListData> data)
  {
    favorites1Count = toggleFavorite(data, 1, favorites1Count, favorites1View);
  }

  public void toggleFavorite2(List<GameListData> data)
  {
    favorites2Count = toggleFavorite(data, 2, favorites2Count, favorites2View);
  }

  public void toggleFavorite3(List<GameListData> data)
  {
    favorites3Count = toggleFavorite(data, 3, favorites3Count, favorites3View);
  }

  public void toggleFavorite4(List<GameListData> data)
  {
    favorites4Count = toggleFavorite(data, 4, favorites4Count, favorites4View);
  }

  public void toggleFavorite5(List<GameListData> data)
  {
    favorites5Count = toggleFavorite(data, 5, favorites5Count, favorites5View);
  }

  public void toggleFavorite6(List<GameListData> data)
  {
    favorites6Count = toggleFavorite(data, 6, favorites6Count, favorites6View);
  }

  public void toggleFavorite7(List<GameListData> data)
  {
    favorites7Count = toggleFavorite(data, 7, favorites7Count, favorites7View);
  }

  public void toggleFavorite8(List<GameListData> data)
  {
    favorites8Count = toggleFavorite(data, 8, favorites8Count, favorites8View);
  }

  public void toggleFavorite9(List<GameListData> data)
  {
    favorites9Count = toggleFavorite(data, 9, favorites9Count, favorites9View);
  }

  public void toggleFavorite10(List<GameListData> data)
  {
    favorites10Count = toggleFavorite(data, 10, favorites10Count, favorites10View);
  }

  public void setViewTag(GameListData data, String viewTag)
  {
    if (data != null && !data.getGameId().isEmpty())
    {
      dbConnector.setViewTag(data.getGameId(), viewTag);
      gameListModel.notifyChange();
    }
  }

  public void setPrimaryJoystick(boolean port1)
  {
    //Enough to toggle on joy 1 model?
    getJoy1Model().setPrimary(port1);
    //Save the current game
    saveData();
  }

  private int toggleFavorite(List<GameListData> data, int favoritesNumber, int favoritesCount, GameView favoritesView)
  {
    for (GameListData gameListData : data)
    {
      if (!gameListData.isInfoSlot() && !gameListData.getGameId().isEmpty())
      {
        int previousFavorite = gameListData.getFavorite();
        dbConnector.toggleFavorite(gameListData.getGameId(), previousFavorite, favoritesNumber);
        gameListData.toggleFavorite(favoritesNumber);
        if (gameListData.isFavorite())
        {
          favoritesView.setGameCount(++favoritesCount);
          reduceFavoriteCount(previousFavorite);
        }
        else
        {
          favoritesView.setGameCount(--favoritesCount);
        }

      }
    }
    if (!data.isEmpty())
    {
      gameListModel.notifyChange();

      if (((GameView) gameViewModel.getSelectedItem()).isFavorite())
      {
        this.reloadCurrentGameView();
      }
    }
    return favoritesCount;
  }

  public void runGameInVice()
  {
    runGameInVice(true);
  }
  public void runGameInVice(boolean fromMainWindow)
  {
    fileManager.runGameInVice(fromMainWindow);
  }

  public void runVice()
  {
    fileManager.runViceWithoutGame();
  }

  public void runSnapshotInVice(SavedStatesModel.SAVESTATE saveState)
  {
    fileManager.runSnapshotInVice(saveState);
  }

  public void checkEnablementOfPalNtscMenuItem(boolean check)
  {
    stateManager.checkEnablementOfPalNtscMenuItem(check);
  }

  public void resetJoystickConfigsForCurrentView()
  {
    dbConnector.resetJoystickConfigsForView(getSelectedGameView());
  }

  public void updatePrimaryJoystickPort(List<String> gameIdList, boolean port1)
  {
    dbConnector.updatePrimaryJoystickPort(gameIdList, port1);
  }

  public void enableAccurateDiskForAllGamesInCurrentView()
  {
    dbConnector.setAccurateDiskForView(getSelectedGameView(), true);
  }

  public void disableAccurateDiskForAllGamesInCurrentView()
  {
    dbConnector.setAccurateDiskForView(getSelectedGameView(), false);
  }

  private void generateInfoSlot(GameDetails gameDetails)
  {
    String gameViewTitle = this.selectedGameView.getName();
    String newTitle = "0 <----- " + gameViewTitle + " -----> 0";
    if (newTitle.length() > 35)
    {
      //Trim the title a bit, by removing "--" until just one left
      newTitle = newTitle.replace("--", "-");
      if (newTitle.length() > 35)
      {
        newTitle = newTitle.replace("--", "-");
        if (newTitle.length() > 35)
        {
          newTitle = newTitle.replace("--", "-");
        }
      }
    }
    PreferencesModel prefModel = new PreferencesModel();

    gameDetails.setTitle(newTitle);
    gameDetails.setComposer(prefModel.getComposer());
    gameDetails.setAuthor(prefModel.getAuthor());
    gameDetails.setGenre(prefModel.getGenre());
    gameDetails.setYear(prefModel.getYear());

    gameDetails.setDescription(prefModel.getDescription());
    gameDetails.setDescriptionDe(prefModel.getDescriptionDe());
    gameDetails.setDescriptionFr(prefModel.getDescriptionFr());
    gameDetails.setDescriptionEs(prefModel.getDescriptionEs());
    gameDetails.setDescriptionIt(prefModel.getDescriptionIt());

    gameDetails.setViewTag("GIS:" + this.selectedGameView.getGameViewId());
    gameDetails.setSystem("64,pal,driveicon,accuratedisk,sid6581,basic");
    gameDetails.setJoy1("J:1*:,,,,,,,,,,,,,,");
    gameDetails.setJoy2("J:2:,,,,,,,,,,,,,,");

    infoModel.resetOldFileNames();
    //Cover image
    infoModel.setCoverImage(FileManager.getInfoSlotCover(this.selectedGameView.getGameViewId(),
                                                         this.selectedGameView.getName()));
    //Screen images
    List<String> screenShotFileNames = new ArrayList<>();
    if (this.selectedGameView.getGameCount() > 15)
    {
      ArrayList<Integer> indexList = new ArrayList<Integer>();
      for (int i = 0; i < selectedGameView.getGameCount(); i++)
      {
        indexList.add(i);
      }

      Collections.shuffle(indexList);

      List<String> gameIdList = new ArrayList<>();
      int index = 0;
      while (gameIdList.size() < 32 && index < indexList.size())
      {
        GameListData randomGame = this.gameListModel.get(indexList.get(index));
        if (!randomGame.isInfoSlot())
        {
          gameIdList.add(randomGame.getGameId());
        }
        index++;
      }
      //Only read from db if there are more than 15 games in the current view
      if (gameIdList.size() > 15)
      {
        //Get a screenshot names for the game from db
        screenShotFileNames = this.dbConnector.getScreenShotNames(gameIdList);
        Collections.shuffle(screenShotFileNames);
      }
    }

    BufferedImage[] images = FileManager.getInfoSlotScreenImage(this.selectedGameView, screenShotFileNames);
    writeGameViewTextOnScreen(images[0], Color.yellow);
    infoModel.setScreen1Image(images[0]);
    writeGameViewTextOnScreen(images[1], Color.red);
    infoModel.setScreen2Image(images[1]);
  }

  public void writeGameViewTextOnScreen(BufferedImage image, Color color)
  {
    String title = this.selectedGameView.getName().toUpperCase();
    BufferedImage infoSlotTextBox = FileManager.getInfoSlotTextBox();
    Graphics2D g = image.createGraphics();
    int imgWidth = image.getWidth();
    int imgHeight = image.getHeight();
    g = image.createGraphics();

    g.drawImage(infoSlotTextBox, imgWidth / 2 - infoSlotTextBox.getWidth() / 2, 55, null);

    g.setFont(new Font("C64 Pro", 0, 0).deriveFont(10f));

    TextLayout textLayout = new TextLayout(title, g.getFont(), g.getFontRenderContext());
    double textHeight = textLayout.getBounds().getHeight();
    double textWidth = textLayout.getBounds().getWidth();
    if (textWidth > 170)
    {
      //Draw a black rectangle first
      g.setColor(Color.black);
      g.fillRect(imgWidth / 2 - (int) textWidth / 2 - 5, imgHeight / 2 - 11, (int) textWidth + 10, 21);
    }

    g.setColor(color);
    g.drawString(title, imgWidth / 2 - (int) textWidth / 2, imgHeight / 2 + (int) textHeight / 2);
    g.dispose();
  }

  public GameDetails getCurrentGameDetails()
  {
    return this.currentGameDetails;
  }

  public List<String> getAvailableDatabases()
  {
    return availableDatabases;
  }

  public void setCurrentDatabase(String database)
  {
    //Save the game count for the current gamelist views when switching database
    saveCurrentGamelistViews();
    
    selectedDatabase = database;
    //Update all
    FileManager.setCurrentDbFolder(selectedDatabase);
    DbConnector.setCurrentDbFolder(selectedDatabase);
    stateManager.readSavedStatesAndUpdateMap();
    reloadGameViews(false);
    this.notifyChange("databaseSelected", null, database);
  }
  
  public void saveCurrentGamelistViews()
  {
    dbConnector.saveCurrentGamelistViews(currentGameViewList);
  }

  public String getCurrentDatabase()
  {
    return selectedDatabase;
  }
}
