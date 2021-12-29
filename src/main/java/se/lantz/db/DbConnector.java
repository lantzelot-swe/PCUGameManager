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
import java.util.List;
import java.util.Map;

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
  public static final String DB_NAME = "pcusb.db";
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

  public DbConnector()
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
    //Check if database file exists, if not create an empty db.
    File dbFile = new File("./" + DB_NAME);
    if (!dbFile.exists())
    {
      createNewDb();
      logger.debug("Database missing, new db created.");
    }
    //To be backwards compatible with 1.0 db, update if missing
    addLanguageAndDuplicateColumnsIfMissing();
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

  /**
   * Connect to the database.
   */
  public Connection connect()
  {
    Connection connection = null;
    try
    {
      // db parameters
      String url = "jdbc:sqlite:" + DB_NAME;
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
    sqlBuilder.append("SELECT title, gamefile, rowid, favorite, viewtag FROM gameinfo ");
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
        //Filter out all info slots that are not supposed to be shown in "all games"
        if (data.isInfoSlot() && view.getGameViewId() == -1 && !viewTag.equalsIgnoreCase("GIS:-1"))
        {
          //Ignore all info slots not created for "all games"
        }
        else
        {
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
      sqlBuilder.append("UPDATE gameview SET name = '");
      sqlBuilder.append(view.getName());
      sqlBuilder.append("' WHERE viewId = ");
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
                                                 String viewTag)
  {
    StringBuilder returnBuilder = new StringBuilder();
    switch (option)
    {
    case SKIP:
      skipExistingAndInsertMissingIntoGameInfoTable(rowValues, returnBuilder, addAsFavorite, viewTag);
      break;
    case OVERWRITE:
      overwriteExistingAndInsertMissingIntoGameInfoTable(rowValues, returnBuilder, addAsFavorite, viewTag);
      break;
    case ADD:
      insertAllIntoGameInfoTable(rowValues, returnBuilder, addAsFavorite, viewTag);
      break;
    default:
      break;
    }
    return returnBuilder;
  }

  private void overwriteExistingAndInsertMissingIntoGameInfoTable(List<String> rowValues,
                                                                  StringBuilder infoBuilder,
                                                                  int addAsFavorite,
                                                                  String viewTag)
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
      insertAllIntoGameInfoTable(newRowValues, infoBuilder, addAsFavorite, viewTag);
    }
    else
    {
      infoBuilder.append("No games added.\n");
    }
  }

  private void skipExistingAndInsertMissingIntoGameInfoTable(List<String> rowValues,
                                                             StringBuilder infoBuilder,
                                                             int addAsFavorite,
                                                             String viewTag)
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
      insertAllIntoGameInfoTable(newRowValues, infoBuilder, addAsFavorite, viewTag);
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
                                          String viewTag)
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
      String strippedRowData = stripRowDataFromOldFileNames(rowData);
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

      if (oldGameName.isEmpty() && !getSystemInfo(rowData).contains("basic"))
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

  private String stripRowDataFromOldFileNames(String rowData)
  {
    String[] splittedRowData = rowData.split(COMMA);
    List<String> strippedDataList = new ArrayList<>();
    //Remove last 4 entries
    for (int i = 0; i < splittedRowData.length - 4; i++)
    {
      strippedDataList.add(splittedRowData[i]);
    }
    return String.join("\",\"", strippedDataList) + "\"";
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
      logger.debug("SELECT Executed successfully");
    }
    catch (SQLException e)
    {
      ExceptionHandler.handleException(e, "Could not fetch game details.");
    }
    return returnValue;
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
    sqlBuilder.append("[0-9][0-9]-cover*\';");
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

  public void deleteView(GameView view)
  {
    String viewFilterSql = "DELETE FROM viewfilter WHERE gameview = " + view.getGameViewId();
    String gameViewSql = "DELETE FROM gameview WHERE viewId = " + view.getGameViewId();
    try (Connection conn = this.connect(); PreparedStatement viewFilterstmt = conn.prepareStatement(viewFilterSql);
      PreparedStatement gameViewStmt = conn.prepareStatement(gameViewSql))
    {
      int value = viewFilterstmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
      value = gameViewStmt.executeUpdate();
      logger.debug("Executed successfully, value = {}", value);
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
}
