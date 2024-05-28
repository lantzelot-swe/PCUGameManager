package se.lantz.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.manager.ImportManager;
import se.lantz.model.data.GameDetails;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameValidationDetails;
import se.lantz.model.data.GameView;
import se.lantz.model.data.ViewFilter;
import se.lantz.util.DbConstants;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;
import se.lantz.util.GameListDataComparator;

public class DbConnector
{
  public static String DB_FILE_NAME = "pcusb.db";
  public static String DB_FILE = "";
  private static final String COMMA = "\",\"";
  // @J-
  private static final String GAMEINFO_SQL =
    "CREATE TABLE gameinfo (\r\n" + 
    "    Title         STRING  NOT NULL,\r\n" + 
    "    Year          INTEGER,\r\n" + 
    "    Author        STRING,\r\n" + 
    "    Composer      STRING,\r\n" + 
    "    Genre         STRING,\r\n" + 
    "    Description   STRING,\r\n" + 
    "    Gamefile      STRING,\r\n" + 
    "    Coverfile     STRING,\r\n" + 
    "    Screen1file   STRING,\r\n" + 
    "    Screen2file   STRING,\r\n" + 
    "    Joy1config    STRING,\r\n" + 
    "    Joy2config    STRING,\r\n" + 
    "    System        STRING,\r\n" + 
    "    VerticalShift INTEGER,\r\n" + 
    "    Favorite      INTEGER NOT NULL\r\n" + 
    "                          DEFAULT (0) \r\n" + 
    ");";
  private static final String GAMEVIEW_SQL =
    "CREATE TABLE gameview (\r\n" + 
    "    viewId INTEGER PRIMARY KEY,\r\n" + 
    "    name   STRING\r\n" + 
    ");";
  private static final String VIEWFILTER_SQL =
    "CREATE TABLE viewfilter (\r\n" + 
    "    gameview    INTEGER REFERENCES gameview (viewId),\r\n" + 
    "    field       STRING,\r\n" + 
    "    operator    STRING,\r\n" + 
    "    fieldData   STRING,\r\n" + 
    "    andCriteria BOOLEAN NOT NULL\r\n" + 
    "                        DEFAULT (true) \r\n" + 
    ");";
  // @J+
  private static final Logger logger = LoggerFactory.getLogger(DbConnector.class);
  private List<String> columnList = new ArrayList<>();
  /**
   * Map keeping track of duplicate indexes when importing several games at once.
   */
  Map<String, Integer> duplicateMap = new HashMap<>();
  List<String> addedRowsList = new ArrayList<>();

  public DbConnector(List<String> dbFolders)
  {
    columnList.add(DbConstants.TITLE);
    columnList.add(DbConstants.YEAR);
    columnList.add(DbConstants.AUTHOR);
    columnList.add(DbConstants.COMPOSER);
    columnList.add(DbConstants.GENRE);
    columnList.add(DbConstants.DESC);
    columnList.add(DbConstants.DESC_DE);
    columnList.add(DbConstants.DESC_FR);
    columnList.add(DbConstants.DESC_ES);
    columnList.add(DbConstants.DESC_IT);
    columnList.add(DbConstants.GAME);
    columnList.add(DbConstants.COVER);
    columnList.add(DbConstants.SCREEN1);
    columnList.add(DbConstants.SCREEN2);
    columnList.add(DbConstants.JOY1);
    columnList.add(DbConstants.JOY2);
    columnList.add(DbConstants.SYSTEM);
    columnList.add(DbConstants.VERTICALSHIFT);
    columnList.add(DbConstants.FAVORITE);
    columnList.add(DbConstants.VIEW_TAG);
    columnList.add(DbConstants.DISK_2);
    columnList.add(DbConstants.DISK_3);
    columnList.add(DbConstants.DISK_4);
    columnList.add(DbConstants.DISK_5);
    columnList.add(DbConstants.DISK_6);

    for (String dbFolder : dbFolders)
    {
      setCurrentDbFolder(dbFolder);
      createDbIfMissing(dbFolder);
    }
  }

  public void createDbIfMissing(String folderName)
  {
    //Check if databases file exists, if not create an empty db.
    File dbFile = new File("./" + DB_FILE);
    if (!dbFile.exists())
    {
      createNewDb();
      logger.debug("Database {} missing, new db created.", folderName);
    }
    //To be backwards compatible with 1.0 db, update if missing
    addLanguageAndDuplicateColumnsIfMissing();
    //To be backwards compatible with 2.8.2 db, update if missing
    addDiskColumnsIfMissing();
  }

  public static void setCurrentDbFolder(String dbFolder)
  {
    DB_FILE = "./databases/" + dbFolder + "/" + DB_FILE_NAME;
  }

  private void createNewDb()
  {
    try (Connection conn = this.connect(); PreparedStatement gameInfostmt = conn.prepareStatement(GAMEINFO_SQL);
      PreparedStatement gameViewstmt = conn.prepareStatement(GAMEVIEW_SQL);
      PreparedStatement viewFilterstmt = conn.prepareStatement(VIEWFILTER_SQL))
    {
      gameInfostmt.executeUpdate();
      gameViewstmt.executeUpdate();
      viewFilterstmt.executeUpdate();
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not cretate db tables");
    }
  }

  public void validateMissingColumnsAfterRestore()
  {
    addLanguageAndDuplicateColumnsIfMissing();
    addDiskColumnsIfMissing();
  }

  private void addLanguageAndDuplicateColumnsIfMissing()
  {
    String tableInfoSql = "PRAGMA table_info(gameinfo)";
    String addDeSql = "ALTER TABLE gameinfo ADD COLUMN Description_de STRING;";
    String addFrSql = "ALTER TABLE gameinfo ADD COLUMN Description_fr STRING;";
    String addEsSql = "ALTER TABLE gameinfo ADD COLUMN Description_es STRING;";
    String addItSql = "ALTER TABLE gameinfo ADD COLUMN Description_it STRING;";
    String addDuplicateSql = "ALTER TABLE gameinfo ADD COLUMN Duplicate INTEGER DEFAULT 0;";
    String addViewTagSql = "ALTER TABLE gameinfo ADD COLUMN Viewtag STRING;";

    try (Connection conn = this.connect(); PreparedStatement stmnt = conn.prepareStatement(tableInfoSql);
      ResultSet rs = stmnt.executeQuery(); Statement addDestmnt = conn.createStatement();
      Statement addFrstmnt = conn.createStatement(); Statement addEsstmnt = conn.createStatement();
      Statement addItstmnt = conn.createStatement(); Statement addDuplicatestmnt = conn.createStatement();
      Statement addViewtagstmnt = conn.createStatement())
    {
      boolean columnsAvailable = false;
      boolean duplicateAvailable = false;
      boolean viewTagAvailable = false;
      while (rs.next())
      {
        //Check if one of the language columns are available
        if (rs.getString("Name").equals("Description_de"))
        {
          columnsAvailable = true;
        }
        if (rs.getString("Name").equals("Duplicate"))
        {
          duplicateAvailable = true;
        }
        if (rs.getString("Name").equals("Viewtag"))
        {
          viewTagAvailable = true;
        }
      }

      if (!columnsAvailable)
      {
        logger.debug("Language columns are missing in the database, adding columns.");
        addDestmnt.executeUpdate(addDeSql);
        addFrstmnt.executeUpdate(addFrSql);
        addEsstmnt.executeUpdate(addEsSql);
        addItstmnt.executeUpdate(addItSql);
        logger.debug("Language columns added.");
      }
      if (!duplicateAvailable)
      {
        logger.debug("Duplicate column is missing in the database, adding column.");
        addDuplicatestmnt.executeUpdate(addDuplicateSql);
        logger.debug("Duplicate column added.");
      }
      if (!viewTagAvailable)
      {
        logger.debug("Viewtag column is missing in the database, adding column.");
        addViewtagstmnt.executeUpdate(addViewTagSql);
        logger.debug("Viewtag column added.");
      }
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not update db for language, duplicate columns and view tag");
    }
  }

  private void addDiskColumnsIfMissing()
  {
    String tableInfoSql = "PRAGMA table_info(gameinfo)";
    String addDisk2Sql = "ALTER TABLE gameinfo ADD COLUMN Disk2 STRING;";
    String addDisk3Sql = "ALTER TABLE gameinfo ADD COLUMN Disk3 STRING;";
    String addDisk4Sql = "ALTER TABLE gameinfo ADD COLUMN Disk4 STRING;";
    String addDisk5Sql = "ALTER TABLE gameinfo ADD COLUMN Disk5 STRING;";
    String addDisk6Sql = "ALTER TABLE gameinfo ADD COLUMN Disk6 STRING;";

    try (Connection conn = this.connect(); PreparedStatement stmnt = conn.prepareStatement(tableInfoSql);
      ResultSet rs = stmnt.executeQuery(); Statement addDisk2stmnt = conn.createStatement();
      Statement addDisk3stmnt = conn.createStatement(); Statement addDisk4stmnt = conn.createStatement();
      Statement addDisk5stmnt = conn.createStatement(); Statement addDisk6stmnt = conn.createStatement())
    {
      boolean disk2Available = false;
      boolean disk3Available = false;
      boolean disk4Available = false;
      boolean disk5Available = false;
      boolean disk6Available = false;
      while (rs.next())
      {
        //Check if one of the language columns are available
        if (rs.getString("Name").equals("Disk2"))
        {
          disk2Available = true;
        }
        if (rs.getString("Name").equals("Disk3"))
        {
          disk3Available = true;
        }
        if (rs.getString("Name").equals("Disk4"))
        {
          disk4Available = true;
        }
        if (rs.getString("Name").equals("Disk5"))
        {
          disk5Available = true;
        }
        if (rs.getString("Name").equals("Disk6"))
        {
          disk6Available = true;
        }
      }

      if (!disk2Available)
      {
        logger.debug("Disk2 column is missing in the database, adding column.");
        addDisk2stmnt.executeUpdate(addDisk2Sql);
        logger.debug("Disk2 columns added.");
      }
      if (!disk3Available)
      {
        logger.debug("Disk3 column is missing in the database, adding column.");
        addDisk3stmnt.executeUpdate(addDisk3Sql);
        logger.debug("Disk3 column added.");
      }
      if (!disk4Available)
      {
        logger.debug("Disk4 column is missing in the database, adding column.");
        addDisk4stmnt.executeUpdate(addDisk4Sql);
        logger.debug("Disk4 column added.");
      }
      if (!disk5Available)
      {
        logger.debug("Disk5 column is missing in the database, adding column.");
        addDisk5stmnt.executeUpdate(addDisk5Sql);
        logger.debug("Disk5 column added.");
      }
      if (!disk6Available)
      {
        logger.debug("Disk6 column is missing in the database, adding column.");
        addDisk5stmnt.executeUpdate(addDisk6Sql);
        logger.debug("Disk6 column added.");
      }
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not update db for Disk columns");
    }
  }

  /**
   * Connect to the database.
   */
  public Connection connect()
  {
    Connection connection = null;
    try
    {
      // db parameters
      String url = "jdbc:sqlite:" + DB_FILE;
      // create a connection to the database
      connection = DriverManager.getConnection(url);

      logger.debug("Connection to SQLite has been established.");

    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not connect to db.");
    }
    return connection;
  }

  public List<GameListData> fetchGamesByView(GameView view)
  {
    List<GameListData> returnList = new ArrayList<>();

    //Construct SQL
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder
      .append("SELECT title, composer, year, author, gamefile, rowid, favorite, viewtag, system, disk2, disk3, disk4, disk5, disk6 FROM gameinfo ");
    sqlBuilder.append(view.getSqlQuery());
    sqlBuilder.append(" ORDER BY title COLLATE NOCASE ASC");

    logger.debug("Generated View SQL: {}", sqlBuilder);
    try (Connection conn = this.connect(); Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sqlBuilder.toString()))
    {
      // loop through the result set
      while (rs.next())
      {
        String viewTag = rs.getString("Viewtag");
        GameListData data = new GameListData(rs.getString("Title"),
                                             rs.getString("GameFile"),
                                             Integer.toString(rs.getInt("rowid")),
                                             rs.getInt("Favorite"),
                                             viewTag != null && viewTag.contains("GIS:"));

        //For filtering
        data.setComposer(rs.getString("Composer"));
        data.setAuthor(rs.getString("Author"));
        data.setYear(rs.getInt("Year"));
        data.setViewTag(rs.getString("Viewtag"));
        data.setSystem(rs.getString("System"));

        if (data.isInfoSlot() && !viewTag.equalsIgnoreCase("GIS:" + view.getGameViewId()))
        {
          //Ignore all info slots not created for this specific view
        }
        else
        {
          //Update file count
          int fileCount = 1;
          fileCount = updateDiskCount(rs.getString("disk2"), fileCount);
          fileCount = updateDiskCount(rs.getString("disk3"), fileCount);
          fileCount = updateDiskCount(rs.getString("disk4"), fileCount);
          fileCount = updateDiskCount(rs.getString("disk5"), fileCount);
          fileCount = updateDiskCount(rs.getString("disk6"), fileCount);
          data.setFileCount(fileCount);
          returnList.add(data);
        }
      }
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not fetch games by view = " + view.getName());
    }
    //Sort again since "NOCASE ASC" doesn't seem completely reliable
    Collections.sort(returnList, new GameListDataComparator());
    return returnList;
  }

  public void createAndUpdateGameViewForImportedGBGames(String mainViewTag)
  {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT rowid, disk2, disk3, disk4, disk5, disk6 FROM gameinfo WHERE Viewtag LIKE \"");
    sqlBuilder.append(mainViewTag);
    sqlBuilder.append("\" ORDER BY title COLLATE NOCASE ASC");

    //Map containing gameId and diskCount for each game
    LinkedHashMap<Integer, Integer> sortedGameMap = new LinkedHashMap<>();

    logger.debug("Generated View SQL: {}", sqlBuilder);
    try (Connection conn = this.connect(); Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sqlBuilder.toString()))
    {
      // loop through the result set
      while (rs.next())
      {
        int additionalDiskCount = 0;
        additionalDiskCount = updateDiskCount(rs.getString("disk2"), additionalDiskCount);
        additionalDiskCount = updateDiskCount(rs.getString("disk3"), additionalDiskCount);
        additionalDiskCount = updateDiskCount(rs.getString("disk4"), additionalDiskCount);
        additionalDiskCount = updateDiskCount(rs.getString("disk5"), additionalDiskCount);
        additionalDiskCount = updateDiskCount(rs.getString("disk6"), additionalDiskCount);
        sortedGameMap.put(rs.getInt("rowId"), additionalDiskCount);
      }
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not fetch all games for updating game views during gb import");
    }
    int viewNumber = 1;
    int i = 0;
    int additionalDiskCount = 0;

    int firstGameInView = -1;
    int lastGameInView = -1;
    //Loop through and update gameView for games 
    for (Integer gameId : sortedGameMap.keySet())
    {
      if (i == 0)
      {
        firstGameInView = gameId;
      }
      additionalDiskCount = additionalDiskCount + sortedGameMap.get(gameId);
      if (((i + additionalDiskCount) / 250) > (viewNumber - 1))
      {
        //Rename previous game view before creating a new one
        renameGameView(mainViewTag, viewNumber, firstGameInView, lastGameInView);

        viewNumber++;
        //Create new game view
        String name = mainViewTag + "/" + viewNumber;
        ViewFilter filter = new ViewFilter(DbConstants.VIEW_TAG, ViewFilter.EQUALS_TEXT, name, true);
        GameView newView = new GameView(0);
        newView.setViewFilters(Arrays.asList(filter));
        newView.setName(name.replaceAll("_", " "));
        saveGameView(newView);
        firstGameInView = gameId;
      }
      else
      {
        lastGameInView = gameId;
      }
      if (viewNumber > 1)
      {
        //Tag with right game tag
        setViewTag(gameId.toString(), mainViewTag + "/" + viewNumber);
      }
      i++;
    }
    //Rename the last view also
    if (viewNumber > 1)
    {
      renameGameView(mainViewTag, viewNumber, firstGameInView, lastGameInView);
    }
  }

  private void renameGameView(String originalViewName, int viewNumber, int firstGameId, int lastGameId)
  {
    String firstGameTitle = "";
    String lastGameTitle = "";
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT title FROM gameinfo WHERE rowId IN (");
    sqlBuilder.append(firstGameId);
    sqlBuilder.append(", ");
    sqlBuilder.append(lastGameId);
    sqlBuilder.append(") ORDER BY title COLLATE NOCASE ASC");

    logger.debug("Generated View SQL: {}", sqlBuilder);
    try (Connection conn = this.connect(); Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sqlBuilder.toString()))
    {
      rs.next();
      firstGameTitle = rs.getString("title");
      rs.next();
      lastGameTitle = rs.getString("title");
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not fetch title by id");
    }

    String oldViewName = viewNumber > 1 ? originalViewName + "/" + viewNumber : originalViewName;

    //Always use "0" as for first view (sorting is a bit random...)
    String firstGameLetters = viewNumber == 1 ? "0" : getBeginLetters(firstGameTitle);
    String newViewName = originalViewName + "/" + firstGameLetters + "-" + getBeginLetters(lastGameTitle);

    sqlBuilder = new StringBuilder();
    sqlBuilder.append("");

    sqlBuilder.append("UPDATE gameview SET name = \"");
    sqlBuilder.append(newViewName);
    sqlBuilder.append("\" WHERE name = \"");
    sqlBuilder.append(oldViewName);
    sqlBuilder.append("\";");
    logger.debug("Generated update view SQL: {}", sqlBuilder);
    try (Connection conn = this.connect();
      PreparedStatement gameViewstmt = conn.prepareStatement(sqlBuilder.toString()))
    {
      gameViewstmt.executeUpdate();
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not update gameview name during gb import");
    }
  }

  private String getBeginLetters(String title)
  {
    if (title.length() > 1)
    {
      return title.substring(0, 1).toUpperCase() + title.substring(1, 2).toLowerCase();
    }
    return title.substring(0, 1).toUpperCase();
  }

  private int updateDiskCount(String disk, int fileCount)
  {
    if (disk != null && !disk.isEmpty())
    {
      fileCount++;
    }
    return fileCount;
  }

  public List<GameListData> fetchAllGamesForGameCount()
  {
    List<GameListData> returnList = new ArrayList<>();

    //Construct SQL
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT favorite FROM gameinfo ORDER BY title COLLATE NOCASE ASC");

    logger.debug("Generated View SQL: {}", sqlBuilder);
    try (Connection conn = this.connect(); Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sqlBuilder.toString()))
    {
      // loop through the result set
      while (rs.next())
      {
        GameListData data = new GameListData("", "", "", rs.getInt("Favorite"), false);
        returnList.add(data);
      }
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not fetch all games for count");
    }
    return returnList;
  }

  public List<GameValidationDetails> fetchAllGamesForDbValdation()
  {
    List<GameValidationDetails> returnList = new ArrayList<>();
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder
      .append("SELECT title, gamefile, Coverfile, Screen1file, Screen2file, System FROM gameinfo ORDER BY title COLLATE NOCASE ASC");
    try (Connection conn = this.connect(); Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sqlBuilder.toString()))
    {
      // loop through the result set
      while (rs.next())
      {
        GameValidationDetails data = new GameValidationDetails();
        data.setTitle(rs.getString("Title"));
        data.setGame(rs.getString("GameFile"));
        data.setCover(rs.getString("Coverfile"));
        data.setScreen1(rs.getString("Screen1file"));
        data.setScreen2(rs.getString("Screen2file"));
        data.setSystem(rs.getString("System"));
        returnList.add(data);
      }
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not fetch games for db validation");
    }
    return returnList;
  }

  public List<GameView> loadGameViews()
  {
    List<GameView> viewList = new ArrayList<>();

    String selectSql = "SELECT * FROM gameview";
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(selectSql))
    {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        GameView gameView = new GameView(rs.getInt("viewId"));
        gameView.setName(rs.getString("name"));
        //Fetch all filters       
        List<ViewFilter> viewFilters = new ArrayList<>();

        String filterSql = "SELECT * FROM viewfilter WHERE gameview = " + gameView.getGameViewId();
        try (PreparedStatement filterstmt = conn.prepareStatement(filterSql))
        {
          ResultSet filterRs = filterstmt.executeQuery();
          while (filterRs.next())
          {
            viewFilters.add(new ViewFilter(filterRs.getString("field"),
                                           filterRs.getString("operator"),
                                           filterRs.getString("fieldData"),
                                           filterRs.getBoolean("andCriteria")));
          }
        }
        catch (SQLException e)
        {
          ExceptionHandler.handleException(e, "Could not load view filters for GameView = " + gameView.getName());
        }
        gameView.setViewFilters(viewFilters);
        viewList.add(gameView);
      }
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not load game views.");
    }
    return viewList;
  }

  /**
   * Creates new db entry for a GameView with gameViewId == 0, or updates an existing entry with the new data.
   * 
   * @param view The GameView to save in the db.
   */
  public void saveGameView(GameView view)
  {
    if (view.getGameViewId() < 0)
    {
      logger.debug("Will not do anything with all games view");
      return;
    }
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("");
    if (view.getGameViewId() > 0)
    {
      sqlBuilder.append("UPDATE gameview SET name = \"");
      sqlBuilder.append(view.getName());
      sqlBuilder.append("\" WHERE viewId = ");
      sqlBuilder.append(view.getGameViewId());
      sqlBuilder.append(";");
    }
    else
    {
      sqlBuilder.append("INSERT INTO gameview (name) VALUES (\"");
      sqlBuilder.append(view.getName());
      sqlBuilder.append("\");");
    }
    String gameViewsql = sqlBuilder.toString();
    logger.debug("gameViewsql:\n{}", gameViewsql);

    String deleteViewFiltersql = "DELETE FROM viewfilter WHERE gameview = ?";
    logger.debug("deleteViewFiltersql:\n{}", deleteViewFiltersql);

    StringBuilder insertFilterBuilder = new StringBuilder();
    insertFilterBuilder.append("INSERT INTO viewfilter (gameview,field,operator,fielddata,andCriteria) VALUES ");
    int filterIndex = 0;
    for (ViewFilter filter : view.getViewFilters())
    {
      filterIndex++;
      insertFilterBuilder.append("(");
      insertFilterBuilder.append(" ? ");
      insertFilterBuilder.append(",\"");
      insertFilterBuilder.append(filter.getField());
      insertFilterBuilder.append(COMMA);
      insertFilterBuilder.append(filter.getOperator());
      insertFilterBuilder.append(COMMA);
      insertFilterBuilder.append(filter.getFilterData());
      insertFilterBuilder.append("\",");
      insertFilterBuilder.append(filter.isAndOperator());
      insertFilterBuilder.append(")");
      if (filterIndex < view.getViewFilters().size())
      {
        insertFilterBuilder.append(",");
      }
    }
    String insertSql = insertFilterBuilder.toString();
    logger.debug("insertSql:\n{}", insertSql);

    try (Connection conn = this.connect(); PreparedStatement gameViewstmt = conn.prepareStatement(gameViewsql);
      PreparedStatement deleteFromViewFilterspl = conn.prepareStatement(deleteViewFiltersql);
      PreparedStatement insertViewFilterstmnt = conn.prepareStatement(insertSql))
    {
      gameViewstmt.executeUpdate();
      int viewId = view.getGameViewId();
      if (viewId == 0)
      {
        ResultSet result = gameViewstmt.getGeneratedKeys();
        if (result.next())
        {
          view.setGameViewId(result.getInt(1));
          viewId = view.getGameViewId();
        }
        else
        {
          logger.error("Unexpected result of insert");
          return;
        }
      }
      deleteFromViewFilterspl.setInt(1, viewId);
      deleteFromViewFilterspl.executeUpdate();

      if (!view.getViewFilters().isEmpty())
      {
        for (int i = 1; i <= filterIndex; i++)
        {
          insertViewFilterstmnt.setInt(i, viewId);
        }
        insertViewFilterstmnt.executeUpdate();
      }
      //Make sure SQL statement is updated correctly for new views
      view.setViewFilters(view.getViewFilters());
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not save game view.");
      return;
    }
  }

  public StringBuilder importRowsInGameInfoTable(List<String> rowValues,
                                                 ImportManager.Options option,
                                                 int addAsFavorite,
                                                 String viewTag,
                                                 int gameViewId)
  {
    StringBuilder returnBuilder = new StringBuilder();
    switch (option)
    {
    case SKIP:
      skipExistingAndInsertMissingIntoGameInfoTable(rowValues, returnBuilder, addAsFavorite, viewTag, gameViewId);
      break;
    case OVERWRITE:
      overwriteExistingAndInsertMissingIntoGameInfoTable(rowValues, returnBuilder, addAsFavorite, viewTag, gameViewId);
      break;
    case ADD:
      insertAllIntoGameInfoTable(rowValues, returnBuilder, addAsFavorite, viewTag, gameViewId);
      break;
    default:
      break;
    }
    return returnBuilder;
  }

  private void overwriteExistingAndInsertMissingIntoGameInfoTable(List<String> rowValues,
                                                                  StringBuilder infoBuilder,
                                                                  int addAsFavorite,
                                                                  String viewTag,
                                                                  int gameViewId)
  {
    List<String> existingRowValues = new ArrayList<>();
    List<String> newRowValues = new ArrayList<>();
    //Check which are already available and sort them out of rowValues
    for (String rowValue : rowValues)
    {
      String[] splittedRowValue = rowValue.split(COMMA);
      StringBuilder sqlBuilder = new StringBuilder();
      sqlBuilder.append("SELECT COUNT(*) FROM gameinfo WHERE title = ");
      sqlBuilder.append(splittedRowValue[0]);
      sqlBuilder.append("\" COLLATE NOCASE;");
      String sql = sqlBuilder.toString();
      infoBuilder.append("Checking game ");
      infoBuilder.append(splittedRowValue[0].substring(1));
      infoBuilder.append(": ");
      logger.debug("Checking game: {}", sql);

      try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
      {
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        if (rs.getInt(1) == 0)
        {
          infoBuilder.append("Not available, adding to db\n");
          logger.debug("Game: {} is not available, adding to db", splittedRowValue[0]);
          newRowValues.add(rowValue);
        }
        else
        {
          infoBuilder.append("Already available, overwriting\n");
          logger.debug("Game: {} is already available, overwriting.", splittedRowValue[0]);
          existingRowValues.add(rowValue);
        }
      }
      catch (SQLException e)
      {
        infoBuilder.append("Could not insert games in db.\n");
        ExceptionHandler.handleException(e, "Could not insert games in db.");
      }
    }

    if (existingRowValues.size() > 0)
    {
      infoBuilder.append("Replacing ");
      infoBuilder.append(existingRowValues.size());
      infoBuilder.append(" games in the db\n");
      updateAllInGameInfoTable(existingRowValues, addAsFavorite, viewTag);
    }

    if (newRowValues.size() > 0)
    {
      insertAllIntoGameInfoTable(newRowValues, infoBuilder, addAsFavorite, viewTag, gameViewId);
    }
    else
    {
      infoBuilder.append("No games added.\n");
    }
  }

  private void skipExistingAndInsertMissingIntoGameInfoTable(List<String> rowValues,
                                                             StringBuilder infoBuilder,
                                                             int addAsFavorite,
                                                             String viewTag,
                                                             int gameViewId)
  {
    List<String> newRowValues = new ArrayList<>();
    //Check which are already available and sort them out of rowValues
    for (String rowValue : rowValues)
    {
      String[] splittedRowValue = rowValue.split(COMMA);
      String title = splittedRowValue[0];
      StringBuilder sqlBuilder = new StringBuilder();
      sqlBuilder.append("SELECT COUNT(*) FROM gameinfo WHERE title = ");
      sqlBuilder.append(title);
      sqlBuilder.append("\" COLLATE NOCASE;");
      String sql = sqlBuilder.toString();
      infoBuilder.append("Checking game ");
      infoBuilder.append(title.substring(1));
      infoBuilder.append(": ");
      logger.debug("Checking game: {}", sql);

      if (addedRowsList.contains(rowValue.substring(0, rowValue.indexOf(",")).toLowerCase()))
      {
        //Add game, another one has been added with the same title, no one was available in the db at that point.
        infoBuilder.append("Not available, adding to db\n");
        logger.debug("Game: {} is not available, adding to db", title);
        newRowValues.add(rowValue);
        continue;
      }
      //Check db
      try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
      {
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        if (rs.getInt(1) == 0)
        {
          infoBuilder.append("Not available, adding to db\n");
          logger.debug("Game: {} is not available, adding to db", title);
          newRowValues.add(rowValue);
        }
        else
        {
          infoBuilder.append("Already available, skipping\n");
          logger.debug("Game: {} is already available, skipping.", title);
        }
      }
      catch (SQLException e)
      {
        infoBuilder.append("Could not insert games in db.\n");
        ExceptionHandler.handleException(e, "Could not insert games in db.");
      }
    }

    infoBuilder.append("Skipping ");
    infoBuilder.append(rowValues.size() - newRowValues.size());
    infoBuilder.append(" games.\n");
    if (newRowValues.size() > 0)
    {
      insertAllIntoGameInfoTable(newRowValues, infoBuilder, addAsFavorite, viewTag, gameViewId);
    }
    else
    {
      infoBuilder.append("No games added.\n");
    }
    //Replace content of rowValues with the ones that was added. Only look at these in the next step of the import
    rowValues.clear();
    rowValues.addAll(newRowValues);
  }

  private void insertAllIntoGameInfoTable(List<String> rowValues,
                                          StringBuilder infoBuilder,
                                          int addAsFavorite,
                                          String viewTag,
                                          int gameViewId)
  {
    infoBuilder.append("Adding ");
    infoBuilder.append(rowValues.size());
    infoBuilder.append(" games to the db\n");

    if (rowValues.isEmpty())
    {
      return;
    }

    StringBuilder st = new StringBuilder();
    st.append("INSERT INTO gameinfo (");
    for (String column : columnList)
    {
      st.append(column);
      st.append(",");
    }
    st.append(DbConstants.DUPLICATE_INDEX);
    st.append(") VALUES (");

    for (String rowData : rowValues)
    {
      //Check old gamename, if empty add a view tag for it.
      String oldGameName = getOldGameName(rowData);
      //Strip  rowData from new filenames
      String strippedRowData = stripRowDataFromOldFileNamesAndDisks(rowData);
      String duplicateIndex = rowData.substring(rowData.lastIndexOf(",") + 1);
      st.append(strippedRowData);
      if (addAsFavorite > 0)
      {
        st.append("," + addAsFavorite);
      }
      else
      {
        st.append(",0");
      }
      st.append(",");

      if (getSystemInfo(rowData).contains("basic") && gameViewId != 0)
      {
        //An infoslot, append GIS:
        st.append("\"GIS:" + gameViewId + "\",");
      }
      else if (oldGameName.isEmpty() && !getSystemInfo(rowData).contains("basic"))
      {
        st.append("\"missing");
        if (!viewTag.isEmpty())
        {
          st.append(", " + viewTag);
        }
        st.append("\",");
      }
      else
      {
        //Append viewtag
        st.append("\"" + viewTag + "\",");
      }

      //disk2
      st.append("\"" + getDisk2FileName(rowData) + "\",");
      //disk3
      st.append("\"" + getDisk3FileName(rowData) + "\",");
      //disk4
      st.append("\"" + getDisk4FileName(rowData) + "\",");
      //disk5
      st.append("\"" + getDisk5FileName(rowData) + "\",");
      //disk6
      st.append("\"" + getDisk6FileName(rowData) + "\",");

      st.append(duplicateIndex);
      st.append("),(");
    }
    st.delete(st.length() - 3, st.length());
    st.append(");");
    String sql = st.toString();
    logger.debug("Generated INSERT String:\n{}", sql);

    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      int value = pstmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
      //Add game title to keep track of added games
      rowValues.stream().forEach(row -> addedRowsList.add(row.substring(0, row.indexOf(',')).toLowerCase()));
    }
    catch (SQLException e)
    {
      infoBuilder.append("Could not insert games in db.\n");
      ExceptionHandler.handleException(e, "Could not insert games in db.");
    }
  }

  private String stripRowDataFromOldFileNamesAndDisks(String rowData)
  {
    String[] splittedRowData = rowData.split(COMMA);
    List<String> strippedDataList = new ArrayList<>();
    //Remove last 14 entries (old names and disks)
    for (int i = 0; i < splittedRowData.length - 14; i++)
    {
      strippedDataList.add(splittedRowData[i]);
    }
    return String.join("\",\"", strippedDataList) + "\"";
  }

  private String getDisk2FileName(String rowData)
  {
    String[] splittedRowData = rowData.split(COMMA);
    return splittedRowData[22].split("\"")[0];
  }

  private String getDisk3FileName(String rowData)
  {
    String[] splittedRowData = rowData.split(COMMA);
    return splittedRowData[24].split("\"")[0];
  }

  private String getDisk4FileName(String rowData)
  {
    String[] splittedRowData = rowData.split(COMMA);
    return splittedRowData[26].split("\"")[0];
  }

  private String getDisk5FileName(String rowData)
  {
    String[] splittedRowData = rowData.split(COMMA);
    return splittedRowData[28].split("\"")[0];
  }

  private String getDisk6FileName(String rowData)
  {
    String[] splittedRowData = rowData.split(COMMA);
    return splittedRowData[30].split("\"")[0];
  }

  private String getOldGameName(String rowData)
  {
    String[] splittedRowData = rowData.split(COMMA);
    return splittedRowData[21].split("\"")[0];
  }

  private String getSystemInfo(String rowData)
  {
    String[] splittedRowData = rowData.split(COMMA);
    return splittedRowData[16].split("\"")[0];
  }

  private void updateAllInGameInfoTable(List<String> rowValues, int addAsFavorite, String viewTag)
  {
    for (String rowValue : rowValues)
    {
      List<String> splittedRowValueList = Arrays.asList(rowValue.split(COMMA));
      String title = splittedRowValueList.get(0);
      StringBuilder sqlBuilder = new StringBuilder();
      sqlBuilder.append("UPDATE gameinfo SET ");
      //Loop from 1 (year) to verticalshift, exclude favorite from loop
      for (int i = 1; i < columnList.size() - 2; i++)
      {
        sqlBuilder.append(columnList.get(i));
        sqlBuilder.append(" = ");
        if (i > 1 && i < columnList.size() - 3)
        {
          sqlBuilder.append("\"");
        }

        sqlBuilder.append(splittedRowValueList.get(i));
        if (i < columnList.size() - 3)
        {
          if (i == 1)
          {
            sqlBuilder.append(",");
          }
          else
          {
            sqlBuilder.append("\",");
          }
        }
      }
      if (addAsFavorite > 0)
      {
        sqlBuilder.append(",Favorite = " + addAsFavorite);
      }
      else
      {
        sqlBuilder.append(",Favorite = 0");
      }
      if (!viewTag.isEmpty())
      {
        sqlBuilder.append(",Viewtag = \"" + viewTag + "\"");
      }

      sqlBuilder.append(" WHERE title = ");
      sqlBuilder.append(title);
      sqlBuilder.append("\" AND Duplicate = ");
      //Keep track of multiple duplicate numbers, increase for each
      int duplicateNumber = 0;
      if (duplicateMap.containsKey(title))
      {
        duplicateNumber = duplicateMap.get(title);
      }
      duplicateMap.put(title, duplicateNumber + 1);
      sqlBuilder.append(duplicateNumber);
      sqlBuilder.append(" COLLATE NOCASE;");
      String sql = sqlBuilder.toString();
      logger.debug("Generated UPDATE String:\n{}", sql);
      try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
      {
        int value = pstmt.executeUpdate();
        logger.debug("Executed successfully, value = {}", value);
      }
      catch (SQLException e)
      {
        ExceptionHandler.handleException(e, "Could not insert games in db.");
      }
    }
  }

  /**
   * Returns GameDetails for the currently selected game
   * 
   * @param gameId The id of the current game
   * @return A GameDetails object.
   */
  public GameDetails getGameDetails(String gameId)
  {
    String sql = "SELECT * FROM gameinfo WHERE rowid = " + gameId;
    logger.debug("Generated SELECT String:\n{}", sql);
    GameDetails returnValue = new GameDetails();
    returnValue.setGameId(gameId);
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      ResultSet rs = pstmt.executeQuery();
      returnValue.setTitle(rs.getString(DbConstants.TITLE));
      returnValue.setYear(rs.getInt(DbConstants.YEAR));
      returnValue.setAuthor(rs.getString(DbConstants.AUTHOR));
      returnValue.setComposer(rs.getString(DbConstants.COMPOSER));
      returnValue.setGenre(rs.getString(DbConstants.GENRE));
      returnValue.setDescription(rs.getString(DbConstants.DESC));
      returnValue.setDescriptionDe(rs.getString(DbConstants.DESC_DE));
      returnValue.setDescriptionFr(rs.getString(DbConstants.DESC_FR));
      returnValue.setDescriptionEs(rs.getString(DbConstants.DESC_ES));
      returnValue.setDescriptionIt(rs.getString(DbConstants.DESC_IT));
      returnValue.setGame(rs.getString(DbConstants.GAME));
      returnValue.setCover(rs.getString(DbConstants.COVER));
      returnValue.setScreen1(rs.getString(DbConstants.SCREEN1));
      returnValue.setScreen2(rs.getString(DbConstants.SCREEN2));
      returnValue.setJoy1(rs.getString(DbConstants.JOY1));
      returnValue.setJoy2(rs.getString(DbConstants.JOY2));
      returnValue.setSystem(rs.getString(DbConstants.SYSTEM));
      returnValue.setVerticalShift(rs.getInt(DbConstants.VERTICALSHIFT));
      returnValue.setDuplicateIndex(rs.getInt(DbConstants.DUPLICATE_INDEX));
      returnValue.setViewTag(rs.getString(DbConstants.VIEW_TAG));
      returnValue.setDisk2(rs.getString(DbConstants.DISK_2));
      returnValue.setDisk3(rs.getString(DbConstants.DISK_3));
      returnValue.setDisk4(rs.getString(DbConstants.DISK_4));
      returnValue.setDisk5(rs.getString(DbConstants.DISK_5));
      returnValue.setDisk6(rs.getString(DbConstants.DISK_6));
      logger.debug("SELECT Executed successfully");
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not fetch game details.");
    }
    return returnValue;
  }

  public List<String> getScreenShotNames(List<String> gameIds)
  {
    //Returns 32 screenshot file names used for creating info slot screens.
    List<String> returnList = new ArrayList<>();
    Random screenIndexRandom = new Random();
    String gameIdsString = gameIds.stream().collect(Collectors.joining(","));

    String sql = "SELECT Screen1File, Screen2File FROM gameinfo WHERE rowid IN (" + gameIdsString + ");";
    logger.debug("Generated SELECT String:\n{}", sql);

    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        if (gameIds.size() < 32)
        {
          //Add both screens
          returnList.add(rs.getString(DbConstants.SCREEN1));
          returnList.add(rs.getString(DbConstants.SCREEN2));
        }
        else
        {
          //Randomize which screen to pick
          if (screenIndexRandom.nextInt(2) == 0)
          {
            returnList.add(rs.getString(DbConstants.SCREEN1));
          }
          else
          {
            returnList.add(rs.getString(DbConstants.SCREEN2));
          }
        }
      }
      logger.debug("SELECT Executed successfully");
      if (returnList.size() > 32)
      {
        Collections.shuffle(returnList);
        returnList = returnList.subList(0, 31);
      }
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not fetch game details.");
    }
    return returnList;
  }

  public int getGameDuplicateIndexToUse(String title, String gameId)
  {
    //This is only called when the title changes for an existing game or when adding a new game 
    //Use generated name, that is what decides if a duplicate index needs to be used.
    //Look at the coverFile column since all covers have the same name ending.
    String fileName = FileManager.generateFileNameFromTitle(title, 0);

    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT rowId, Duplicate FROM gameinfo WHERE Coverfile GLOB \'");
    sqlBuilder.append(fileName);
    sqlBuilder.append("-cover*\' OR CoverFile GLOB \'");
    sqlBuilder.append(fileName);
    //Use only 0-5 here to filter out games where title may end with a year, e.g 97. 59 duplicates should be enough...
    sqlBuilder.append("[0-5][0-9]-cover*\';");
    String sql = sqlBuilder.toString();
    logger.debug("Checking if game is in db already: {}", sql);
    int returnIndex = 0;
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        int rowId = rs.getInt("rowId");
        //Ignore the current game if the query matches it
        if (gameId.isEmpty() || rowId != Integer.parseInt(gameId))
        {
          //Increase one to the available index since it's the one supposed to be used.
          returnIndex = Math.max(returnIndex, rs.getInt(DbConstants.DUPLICATE_INDEX) + 1);
        }
      }
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not check for duplicate in db.");
    }
    return returnIndex;
  }

  public int createNewGame(GameDetails details)
  {
    int rowId = 0;
    StringBuilder st = new StringBuilder();
    st.append("INSERT INTO gameinfo (");
    for (String column : columnList)
    {
      st.append(column);
      st.append(",");
    }
    st.append(DbConstants.DUPLICATE_INDEX);
    //    st.delete(st.length() - 1, st.length());
    st.append(") VALUES (\"");

    st.append(details.getTitle());
    st.append("\",");
    st.append(details.getYear());
    st.append(",\"");
    st.append(details.getAuthor());
    st.append(COMMA);
    st.append(details.getComposer());
    st.append(COMMA);
    st.append(details.getGenre());
    st.append(COMMA);
    st.append(details.getDescription());
    st.append(COMMA);
    st.append(details.getDescriptionDe());
    st.append(COMMA);
    st.append(details.getDescriptionFr());
    st.append(COMMA);
    st.append(details.getDescriptionEs());
    st.append(COMMA);
    st.append(details.getDescriptionIt());
    st.append(COMMA);
    st.append(details.getGame());
    st.append(COMMA);
    st.append(details.getCover());
    st.append(COMMA);
    st.append(details.getScreen1());
    st.append(COMMA);
    st.append(details.getScreen2());
    st.append(COMMA);
    st.append(details.getJoy1());
    st.append(COMMA);
    st.append(details.getJoy2());
    st.append(COMMA);
    st.append(details.getSystem());
    st.append("\",");
    st.append(details.getVerticalShift());
    st.append(",0,\"");
    st.append(details.getViewTag());
    st.append(COMMA);
    st.append(details.getDisk2());
    st.append(COMMA);
    st.append(details.getDisk3());
    st.append(COMMA);
    st.append(details.getDisk4());
    st.append(COMMA);
    st.append(details.getDisk5());
    st.append(COMMA);
    st.append(details.getDisk6());
    st.append("\",");
    st.append(details.getDuplicateIndex());
    st.append(");");

    String sql = st.toString();
    logger.debug("Generated INSERT String:\n{}", sql);
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      int value = pstmt.executeUpdate();
      ResultSet result = pstmt.getGeneratedKeys();
      if (result.next())
      {
        rowId = result.getInt(1);
      }
      else
      {
        logger.error("Unexpected result of insert");
        return -1;
      }
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not insert games in db.");
    }
    return rowId;
  }

  public void saveGame(String rowId, GameDetails details)
  {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("UPDATE gameinfo SET ");

    sqlBuilder.append(DbConstants.TITLE);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getTitle());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.YEAR);
    sqlBuilder.append("=");
    sqlBuilder.append(details.getYear());
    sqlBuilder.append(",");
    sqlBuilder.append(DbConstants.AUTHOR);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getAuthor());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.COMPOSER);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getComposer());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.GENRE);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getGenre());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DESC);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDescription());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DESC_DE);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDescriptionDe());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DESC_FR);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDescriptionFr());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DESC_ES);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDescriptionEs());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DESC_IT);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDescriptionIt());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.GAME);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getGame());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.COVER);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getCover());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.SCREEN1);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getScreen1());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.SCREEN2);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getScreen2());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.JOY1);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getJoy1());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.JOY2);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getJoy2());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.SYSTEM);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getSystem());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.VIEW_TAG);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getViewTag());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DISK_2);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDisk2());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DISK_3);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDisk3());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DISK_4);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDisk4());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DISK_5);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDisk5());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.DISK_6);
    sqlBuilder.append("=\"");
    sqlBuilder.append(details.getDisk6());
    sqlBuilder.append("\",");
    sqlBuilder.append(DbConstants.VERTICALSHIFT);
    sqlBuilder.append("=");
    sqlBuilder.append(details.getVerticalShift());
    sqlBuilder.append(",");
    sqlBuilder.append(DbConstants.DUPLICATE_INDEX);
    sqlBuilder.append("=");
    sqlBuilder.append(details.getDuplicateIndex());
    sqlBuilder.append(" WHERE rowId = ");
    sqlBuilder.append(rowId);
    sqlBuilder.append(";");

    String sql = sqlBuilder.toString();
    logger.debug("Generated UPDATE String:\n{}", sql);
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      int value = pstmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not insert games in db.");
    }
  }

  public void deleteGame(String rowId)
  {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("DELETE FROM gameinfo WHERE rowId = ");
    sqlBuilder.append(rowId);
    sqlBuilder.append(";");
    String sql = sqlBuilder.toString();
    logger.debug("Generated DELETE String:\n{}", sql);
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      int value = pstmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not delete game in db.");
    }
  }

  public void deleteAllGames()
  {
    String sql = "DELETE FROM gameinfo";
    logger.debug("Generated DELETE String:\n{}", sql);
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      int value = pstmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not delete games in db.");
    }
  }

  public void deleteAllGamesInView(GameView view)
  {
    String sql = "DELETE FROM gameinfo " + view.getSqlQuery();
    logger.debug("Generated DELETE String:\n{}", sql);
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      int value = pstmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not delete games in db.");
    }
  }

  public void deleteGames(List<GameListData> selectedGameListData)
  {
    List<String> idList = selectedGameListData.stream().map(data -> data.getGameId()).collect(Collectors.toList());
    String idsString = String.join(",", idList);

    String sql = "DELETE FROM gameinfo where rowId IN (" + idsString + ");";
    logger.debug("Generated DELETE String:\n{}", sql);
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
    {
      int value = pstmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not delete games in db.");
    }
  }

  public void deleteAllGameListViews()
  {
    List<GameView> gameViewList = loadGameViews();
    for (GameView gameView : gameViewList)
    {
      deleteView(gameView);
    }
  }

  public void deleteView(GameView view)
  {
    String viewFilterSql = "DELETE FROM viewfilter WHERE gameview = " + view.getGameViewId();
    String gameViewSql = "DELETE FROM gameview WHERE viewId = " + view.getGameViewId();
    String infoSlotSql = "DELETE FROM gameinfo WHERE viewTag LIKE 'GIS:" + view.getGameViewId() + "'";
    try (Connection conn = this.connect(); PreparedStatement viewFilterstmt = conn.prepareStatement(viewFilterSql);
      PreparedStatement gameViewStmt = conn.prepareStatement(gameViewSql);
      PreparedStatement infoSlotStmt = conn.prepareStatement(infoSlotSql))
    {
      int value = viewFilterstmt.executeUpdate();
      logger.debug("{} Executed successfully, value = {}", viewFilterSql, value);
      value = gameViewStmt.executeUpdate();
      logger.debug("{} Executed successfully, value = {}", gameViewSql, value);
      value = infoSlotStmt.executeUpdate();
      logger.debug("{} Executed successfully, value = {}", infoSlotSql, value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not delete gameview or viewfilter in db.");
    }
  }

  public void toggleFavorite(String gameId, int currentFavoriteValue, int newFavorite)
  {
    int newValue = currentFavoriteValue == newFavorite ? 0 : newFavorite;
    String sql = "UPDATE gameinfo SET Favorite = " + newValue + " WHERE rowId = " + gameId + ";";
    try (Connection conn = this.connect(); PreparedStatement favoritestmt = conn.prepareStatement(sql))
    {
      int value = favoritestmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not update favorite value in db.");
    }
  }

  public void clearFavorites(int favoriteNumber)
  {
    String sql =
      "UPDATE gameinfo SET Favorite = 0 where Favorite = " + favoriteNumber + " AND ViewTag NOT LIKE '%GIS:%';";
    try (Connection conn = this.connect(); PreparedStatement favoritestmt = conn.prepareStatement(sql))
    {
      int value = favoritestmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not clear favorite values in db.");
    }
  }

  public void setViewTag(String gameId, String viewTag)
  {
    String sql = "UPDATE gameinfo SET Viewtag = \"" + viewTag + "\" WHERE rowId = " + gameId + ";";
    try (Connection conn = this.connect(); PreparedStatement favoritestmt = conn.prepareStatement(sql))
    {
      int value = favoritestmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not update Viewtag value in db.");
    }
  }

  public void cleanupAfterImport()
  {
    addedRowsList.clear();
    duplicateMap.clear();
  }

  public List<String> fixDescriptions()
  {
    List<String> fixedGames = new ArrayList<>();
    String sql =
      "SELECT title FROM gameinfo where description LIKE '%\n%' OR description_de LIKE '%\n%' OR description_es LIKE '%\n%' OR description_fr LIKE '%\n%' OR description_it LIKE '%\n%'";
    logger.debug("Generated SELECT String:\n{}", sql);

    String replaceDescSql =
      "UPDATE gameinfo SET " + DbConstants.DESC + " = REPLACE(" + DbConstants.DESC + ", '\n', ' ');";
    String replaceDescDESql =
      "UPDATE gameinfo SET " + DbConstants.DESC_DE + " = REPLACE(" + DbConstants.DESC_DE + ", '\n', ' ');";
    String replaceDescESSql =
      "UPDATE gameinfo SET " + DbConstants.DESC_ES + " = REPLACE(" + DbConstants.DESC_ES + ", '\n', ' ');";
    String replaceDescFRSql =
      "UPDATE gameinfo SET " + DbConstants.DESC_FR + " = REPLACE(" + DbConstants.DESC_FR + ", '\n', ' ');";
    String replaceDescITSql =
      "UPDATE gameinfo SET " + DbConstants.DESC_IT + " = REPLACE(" + DbConstants.DESC_IT + ", '\n', ' ');";

    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql);
      PreparedStatement replaceDescPstmt = conn.prepareStatement(replaceDescSql);
      PreparedStatement replaceDescDEPstmt = conn.prepareStatement(replaceDescDESql);
      PreparedStatement replaceDescESPstmt = conn.prepareStatement(replaceDescESSql);
      PreparedStatement replaceDescFRPstmt = conn.prepareStatement(replaceDescFRSql);
      PreparedStatement replaceDescITPstmt = conn.prepareStatement(replaceDescITSql);)
    {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        String title = rs.getString("Title");
        fixedGames.add(title);
      }
      int value = replaceDescPstmt.executeUpdate();
      logger.debug("Fix description Executed successfully, value = {}", value);
      value = replaceDescDEPstmt.executeUpdate();
      logger.debug("Fix description_de Executed successfully, value = {}", value);
      value = replaceDescESPstmt.executeUpdate();
      logger.debug("Fix description_es Executed successfully, value = {}", value);
      value = replaceDescFRPstmt.executeUpdate();
      logger.debug("Fix description_fr Executed successfully, value = {}", value);
      value = replaceDescITPstmt.executeUpdate();
      logger.debug("Fix description_it Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not fix descriptions in db.");
    }
    return fixedGames;
  }

  public void resetJoystickConfigsForView(GameView view)
  {
    String joyConfig = FileManager.getConfiguredJoystickConfig();
    //Get only the mappings
    joyConfig = joyConfig.substring(joyConfig.lastIndexOf(":") + 1);

    String joy1Default = "J:1*:" + joyConfig;
    String joy1NotDefault = "J:1:" + joyConfig;
    String joy2Default = "J:2*:" + joyConfig;
    String joy2NotDefault = "J:2:" + joyConfig;

    StringBuilder joy1sqlBuilder = new StringBuilder();
    StringBuilder joy2sqlBuilder = new StringBuilder();

    //Joy1Default
    joy1sqlBuilder.append("UPDATE gameinfo SET joy1config = '");
    joy1sqlBuilder.append(joy1Default);
    joy1sqlBuilder.append("', joy2config = '");
    joy1sqlBuilder.append(joy2NotDefault);
    joy1sqlBuilder.append("' ");
    if (!view.getSqlQuery().isEmpty())
    {
      joy1sqlBuilder.append(view.getSqlQuery());
      joy1sqlBuilder.append(" AND ");
    }
    else
    {
      joy1sqlBuilder.append("WHERE ");
    }
    joy1sqlBuilder.append("joy1Config LIKE '%*%'");
    logger.debug("Generated SQL for joy1 default:\n{}", joy1sqlBuilder.toString());

    //Joy2Default
    joy2sqlBuilder.append("UPDATE gameinfo SET joy1config = '");
    joy2sqlBuilder.append(joy1NotDefault);
    joy2sqlBuilder.append("', joy2config = '");
    joy2sqlBuilder.append(joy2Default);
    joy2sqlBuilder.append("' ");
    if (!view.getSqlQuery().isEmpty())
    {
      joy2sqlBuilder.append(view.getSqlQuery());
      joy2sqlBuilder.append(" AND ");
    }
    else
    {
      joy2sqlBuilder.append("WHERE ");
    }
    joy2sqlBuilder.append("joy2Config LIKE '%*%'");
    logger.debug("Generated SQL for joy2 default:\n{}", joy2sqlBuilder.toString());

    try (Connection conn = this.connect(); PreparedStatement joy1tmt = conn.prepareStatement(joy1sqlBuilder.toString());
      PreparedStatement joy2tmt = conn.prepareStatement(joy2sqlBuilder.toString()))
    {
      int value = joy1tmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
      value = joy2tmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not update joystick configurations");
    }
  }

  public void updatePrimaryJoystickPort(List<String> idList, boolean port1)
  {
    String joy1Default = "J:1*:";
    String joy1NotDefault = "J:1:";
    String joy2Default = "J:2*:";
    String joy2NotDefault = "J:2:";

    String idListString = String.join(",", idList);
    StringBuilder sqlBuilder = new StringBuilder();

    String expression = String
      .format("UPDATE gameinfo SET Joy1Config = REPLACE(Joy1Config, '%s', '%s'),Joy2Config = REPLACE(Joy2Config, '%s', '%s')  WHERE rowId IN (",
              port1 ? joy1NotDefault : joy1Default,
              port1 ? joy1Default : joy1NotDefault,
              port1 ? joy2Default : joy2NotDefault,
              port1 ? joy2NotDefault : joy2Default);

    sqlBuilder.append(expression);
    sqlBuilder.append(idListString);
    sqlBuilder.append(");");

    logger.debug("Generated SQL for primary joy ports:\n{}", sqlBuilder.toString());

    try (Connection conn = this.connect(); PreparedStatement joy1tmt = conn.prepareStatement(sqlBuilder.toString());)
    {
      int value = joy1tmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not update primary joystick port");
    }
  }

  public void setAccurateDiskForView(GameView view, boolean accurateDisk)
  {

    String validDiskEndings =
      "(gamefile LIKE '%D64%' OR gamefile LIKE '%D8%' OR gamefile LIKE '%D7%' OR gamefile LIKE '%X64%' OR gamefile LIKE '%G64%')";
    StringBuilder accurateDiskBuilder = new StringBuilder();

    if (accurateDisk)
    {
      accurateDiskBuilder.append("UPDATE gameinfo SET System = System || ',driveicon,accuratedisk' ");
      if (view.getSqlQuery().isEmpty())
      {
        accurateDiskBuilder.append("WHERE ");
      }
      else
      {
        accurateDiskBuilder.append(view.getSqlQuery());
        accurateDiskBuilder.append(" AND ");
      }

      accurateDiskBuilder.append(validDiskEndings);
      accurateDiskBuilder.append(" AND System NOT LIKE '%accuratedisk%'");
    }
    else
    {
      accurateDiskBuilder.append("UPDATE gameinfo SET System = REPLACE(System, ',driveicon,accuratedisk', '') ");
      if (view.getSqlQuery().isEmpty())
      {
        accurateDiskBuilder.append("WHERE ");
      }
      else
      {
        accurateDiskBuilder.append(view.getSqlQuery());
        accurateDiskBuilder.append(" AND ");
      }
      accurateDiskBuilder.append(validDiskEndings);
    }
    logger.debug("Generated SQL for accurate disk:\n{}", accurateDiskBuilder.toString());

    try (Connection conn = this.connect();
      PreparedStatement adstmt = conn.prepareStatement(accurateDiskBuilder.toString());)
    {
      int value = adstmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not update accurate disk");
    }
  }
}
