package se.lantz.gamebase;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import se.lantz.gui.exports.PublishWorker;
import se.lantz.manager.ImportManager;
import se.lantz.scraper.GamebaseScraper;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class GamebaseImporter
{
  private static final int CHUNK_SIZE = 100;

  public enum Options
  {
    ALL, FAVORITES, QUERY, GENRE;
  }

  private final ImportManager importManager;
  //Path to to .mdb file
  private Path gbDatabasePath;

  private Path gbGamesPath;
  private Path gbScreensPath;
  private Path gbExtrasPath;

  private boolean isC64 = true;

  private String joyBase = ":JU,JD,JL,JR,JF,JF,SP,EN,,F1,F3,F5,,,";
  private List<GbGameInfo> gbGameInfoList = new ArrayList<>();
  private int importIndexForTempFiles = 0;

  private Options selectedOption = Options.FAVORITES;
  private String titleQueryString = "";

  private boolean includeEntriesWithMissingGameFile = false;
  private GenreInfo selectedGenre;

  public GamebaseImporter(ImportManager importManager)
  {
    this.importManager = importManager;
    importManager = null;
    //Ucanaccess does not work properly in standalone installation if this is not added
    try
    {
      Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
    }
    catch (ClassNotFoundException e1)
    {
      ExceptionHandler.handleException(e1, "");
    }
  }

  public boolean setImportOptions(GamebaseOptions options)
  {
    this.gbDatabasePath = options.getGamebaseDbFile();
    this.isC64 = options.isC64();
    this.selectedOption = options.getSelectedOption();
    this.titleQueryString = options.getTitleQueryString();
    this.selectedGenre = options.getGenre();
    this.includeEntriesWithMissingGameFile = options.isIncludeMissingGameFileEntries();
    return readPathsIni();
  }

  /**
   * This assumes that Games, Screenshots and Extras are located in the same parent folder.
   */
  private boolean readPathsIni()
  {
    try
    {
      Path iniFile = this.gbDatabasePath.getParent().resolve("Paths.ini");
      List<String> lines = Files.readAllLines(iniFile, StandardCharsets.ISO_8859_1);
      boolean games = false;
      boolean pictures = false;
      boolean extras = false;
      for (String line : lines)
      {
        if (line.equals("[Games]"))
        {
          games = true;
          pictures = false;
          extras = false;
        }
        else if (line.equals("[Pictures]"))
        {
          games = false;
          pictures = true;
          extras = false;
        }
        else if (line.equals("[Extras]"))
        {
          games = false;
          pictures = false;
          extras = true;
        }
        else
        {
          if (line.startsWith("1="))
          {
            if (games)
            {
              this.gbGamesPath = Paths.get(line.substring(2));
            }
            else if (pictures)
            {
              this.gbScreensPath = Paths.get(line.substring(2));
            }
            else if (extras)
            {
              this.gbExtrasPath = Paths.get(line.substring(2));
            }
            else
            {
              //Do nothing
            }
          }
        }
      }
      return true;
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not read file Paths.ini");
    }
    return false;
  }

  public void importFromGamebase(PublishWorker worker)
  {
    gbGameInfoList.clear();
    //Use the folder where the gamebase mdb file is located in the import manager
    importManager.setSelectedFoldersForGamebase(gbGamesPath, gbScreensPath, gbExtrasPath.resolve("covers"));

    String databaseURL = "jdbc:ucanaccess://" + gbDatabasePath.toString();

    try (Connection connection = DriverManager.getConnection(databaseURL))
    {
      Statement statement = connection.createStatement();

      String sql =
        "SELECT Games.Name, Musicians.Musician, PGenres.ParentGenre, Publishers.Publisher, Games.Filename, Games.ScrnshotFilename, Years.Year, Games.GA_Id, Games.Control, Games.V_PalNTSC, Games.V_TrueDriveEmu, Games.Gemus\r\n" +
          "FROM PGenres INNER JOIN (Years INNER JOIN (Publishers INNER JOIN ((Games INNER JOIN Musicians ON Games.MU_Id = Musicians.MU_Id) INNER JOIN Genres ON Games.GE_Id = Genres.GE_Id) ON Publishers.PU_Id = Games.PU_Id) ON Years.YE_Id = Games.YE_Id) ON PGenres.PG_Id = Genres.PG_Id\r\n";

      String condition = "";
      switch (selectedOption)
      {
      case FAVORITES:
        condition = "WHERE (((Games.Fav)=True));";
        break;
      case QUERY:
        titleQueryString = titleQueryString.replaceAll("'", "");
        condition = "WHERE (((Games.Name) LIKE '" + titleQueryString + "'));";
        break;
      case GENRE:
        condition =
          "WHERE (Genres.PG_Id = " + selectedGenre.getPgId() + " AND Genres.GE_Id = " + selectedGenre.getGeId() + ");";
        break;
      default:
        break;
      }
      sql = sql + condition;

      ResultSet result = statement.executeQuery(sql);
      int gameCount = 0;
      while (result.next())
      {
        if (createGbGameInfo(result, statement, worker))
        {
          gameCount++;
        }
      }
      worker.publishMessage("\nRead " + gameCount + " games from the gamebase db.\n");
    }
    catch (SQLException ex)
    {
      worker.publishMessage("ERROR: Query failed : " + ex.toString());
      ExceptionHandler.handleException(ex, "Query failed");
    }
  }

  private boolean createGbGameInfo(ResultSet result, Statement statement, PublishWorker worker)
  {
    String title = "";
    try
    {
      title = result.getString("Name");
      String year = result.getString("Year");
      int gameId = result.getInt("GA_Id");
      String gamefile = result.getString("Filename");
      String screen1 = result.getString("ScrnShotFileName");
      String musician = result.getString("Musician");
      String genre = result.getString("ParentGenre");
      String publisher = result.getString("Publisher");
      int control = result.getInt("Control");
      int palOrNtsc = result.getInt("V_PalNTSC");
      int trueDriveEmu = result.getInt("V_TrueDriveEmu");
      String gemus = result.getString("Gemus");

      String vic20Description = "";
      boolean vic20Cart = false;

      worker.publishMessage("Creating game info for " + title + "...");
      //Game file
      if (isC64)
      {
        //GB64 includes game files for all games, no additional extras available.
        if (gamefile.isEmpty() && !includeEntriesWithMissingGameFile)
        {
          worker.publishMessage("Ignoring " + title + " (No game file available)");
          return false;
        }

        //1: Cartridge preferred, easyflash is preferred
        String cartridgePath = getCartridgePath(gameId, statement);
        if (!cartridgePath.isEmpty())
        {
          gamefile = gbExtrasPath.toString() + "\\" + cartridgePath;
        }
      }
      else
      {
        //Description: add key-value pairs for Vic-20 since that holds important info about memory expansion
        vic20Description = gemus;
        //1: Cartridge preferred
        String cartridgePath = getCartridgePath(gameId, statement);
        if (!cartridgePath.isEmpty())
        {
          gamefile = gbExtrasPath.toString() + "\\" + cartridgePath;
          vic20Cart = true;
        }
        if (!gamefile.isEmpty())
        {
          //2: GameFile
          //Extra check for cart or not for vic-20: if description contains "cart", treat it as a cart.
          if (vic20Description.contains("cart"))
          {
            vic20Cart = true;
          }
        }
        else
        {
          //3: Tap
          String tapFile = getTapPath(gameId, statement);
          if (!tapFile.isEmpty())
          {
            gamefile = gbExtrasPath.toString() + "\\" + tapFile;
          }

          if (gamefile.isEmpty() && !includeEntriesWithMissingGameFile)
          {
            worker.publishMessage("Ignoring " + title + " (No game file available)");
            return false;
          }
        }
      }

      //Year can start with 99 for unknown, use 9999 for unknown. Gamebase uses several different ones (e.g. 9994)
      if (year.startsWith("99"))
      {
        year = "9999";
      }

      //Setup advanced string (system, sid, pal, truedrive etc)
      String advanced = constructAdvancedString(palOrNtsc, trueDriveEmu, gemus);

      //Control: 0=JoyPort2, 1=JoyPort1, 2=Keyboard, 3=PaddlePort2, 4=PaddlePort1, 5=Mouse, 6=LightPen, 7=KoalaPad, 8=LightGun
      //Setup joystick port
      String joy1config;
      String joy2config;
      if (control == 1)
      {
        //1 means joystick port 1 in the gb database
        joy1config = "J:1*" + joyBase;
        joy2config = "J:2" + joyBase;
      }
      else
      {
        //For anything else, use port 2.
        joy1config = "J:1" + joyBase;
        joy2config = "J:2*" + joyBase;
      }

      //Fix screenshots
      String screen2 = "";
      if (screen1 != null && !screen1.isEmpty())
      {
        screen1 = gbScreensPath.toString() + "\\" + screen1;
        screen2 = getScreen2(screen1);
      }

      //Map genre properly towards existing ones for the carousel
      genre = GamebaseScraper.mapGenre(genre);

      //Get cover
      String coverFile = getCoverPath(gameId, statement);

      //Add to list to be processed in next import step.
      gbGameInfoList.add(new GbGameInfo(title,
                                        year,
                                        publisher,
                                        musician,
                                        genre,
                                        gamefile,
                                        coverFile,
                                        screen1,
                                        screen2,
                                        joy1config,
                                        joy2config,
                                        advanced,
                                        vic20Description,
                                        vic20Cart));
      return true;
    }
    catch (Exception e)
    {
      worker.publishMessage("ERROR: Could not fetch all info for " + title + ":\n" + e.toString());
      ExceptionHandler.handleException(e, "Could not fetch all info for " + title);
    }
    return false;
  }

  private String getCoverPath(int gameId, Statement statement) throws SQLException
  {
    String coverFile = "";
    String coverSql = "SELECT Extras.Name, Extras.Path, Extras.DisplayOrder\r\n" +
      "FROM Games INNER JOIN Extras ON Games.GA_Id = Extras.GA_Id\r\n" + "WHERE (((Games.GA_Id)=" + gameId +
      ") AND ((Extras.Name) Like ";

    if (isC64)
    {
      coverSql = coverSql + "\"Cover*\"));";
    }
    else
    {
      coverSql = coverSql + "\"*Box Front\" OR (Extras.Name) Like \"*Inlay*\"));";
    }
    ResultSet sqlResult = statement.executeQuery(coverSql);
    int displayOrder = -1;
    //Get the one with the lowest display order (probably the best one)
    while (sqlResult.next())
    {
      int currentDisplayOrder = sqlResult.getInt("DisplayOrder");
      if (displayOrder == -1 || currentDisplayOrder < displayOrder)
      {
        displayOrder = currentDisplayOrder;
        coverFile = sqlResult.getString("Path");
      }
    }
    if (!coverFile.isEmpty())
    {
      coverFile = gbExtrasPath.toString() + "\\" + coverFile;
    }
    return coverFile;
  }

  private String getCartridgePath(int gameId, Statement statement) throws SQLException
  {
    //Get cartridge if available, easyflash is preferred
    String cartridgeSql =
      "SELECT Extras.Name, Extras.Path\r\n" + "FROM Games INNER JOIN Extras ON Games.GA_Id = Extras.GA_Id\r\n" +
        "WHERE (((Games.GA_Id)=" + gameId + ") AND ((Extras.Name) Like ";

    if (isC64)
    {
      cartridgeSql = cartridgeSql + "\"*Cartridge*\"));";
    }
    else
    {
      cartridgeSql = cartridgeSql + "\"*CART\"));";
    }

    ResultSet sqlResult = statement.executeQuery(cartridgeSql);
    String cartridgePath = "";
    while (sqlResult.next())
    {
      if (cartridgePath.isEmpty())
      {
        cartridgePath = sqlResult.getString("Path");
      }
      //Pick easyflash if available
      String name = sqlResult.getString("Name");
      if (name.contains("EasyFlash"))
      {
        cartridgePath = sqlResult.getString("Path");
      }
    }
    return cartridgePath;
  }

  private String getTapPath(int gameId, Statement statement) throws SQLException
  {
    //Get TAP file
    String tapSql =
      "SELECT Extras.Name, Extras.Path\r\n" + "FROM Games INNER JOIN Extras ON Games.GA_Id = Extras.GA_Id\r\n" +
        "WHERE (((Games.GA_Id)=" + gameId + ") AND ((Extras.Name) Like \"TAP\"));";

    ResultSet sqlResult = statement.executeQuery(tapSql);
    String tapPath = "";
    if (sqlResult.next())
    {
      tapPath = sqlResult.getString("Path");
    }
    return tapPath;
  }

  public List<List<GbGameInfo>> getGbGameInfoChunks()
  {
    return importManager.getReadChunks(gbGameInfoList, CHUNK_SIZE);
  }

  public void checkGameFileForGbGames(List<GbGameInfo> gbGameList, PublishWorker worker)
  {
    for (GbGameInfo gbGameInfo : gbGameList)
    {
      try
      {
        worker.publishMessage("Checking game file for " + gbGameInfo.getTitle() + "...");
        List<String> gameFilesList =
          getGameFilesToInclude(gbGamesPath, gbGameInfo.getGamefile(), gbGameInfo.isVic20Cart());
        String mainGameFile = gameFilesList.get(0);
        String disk2File = gameFilesList.size() > 1 ? gameFilesList.get(1) : "";
        String disk3File = gameFilesList.size() > 2 ? gameFilesList.get(2) : "";
        String disk4File = gameFilesList.size() > 3 ? gameFilesList.get(3) : "";
        String disk5File = gameFilesList.size() > 4 ? gameFilesList.get(4) : "";
        String disk6File = gameFilesList.size() > 5 ? gameFilesList.get(5) : "";

        importManager.addFromGamebaseImporter(gbGameInfo.getTitle(),
                                              gbGameInfo.getYear(),
                                              gbGameInfo.getPublisher(),
                                              gbGameInfo.getMusician(),
                                              gbGameInfo.getGenre(),
                                              mainGameFile,
                                              gbGameInfo.getCoverFile(),
                                              gbGameInfo.getScreen1(),
                                              gbGameInfo.getScreen2(),
                                              gbGameInfo.getJoy1config(),
                                              gbGameInfo.getJoy2config(),
                                              gbGameInfo.getAdvanced(),
                                              gbGameInfo.getDescription(),
                                              disk2File,
                                              disk3File,
                                              disk4File,
                                              disk5File,
                                              disk6File,
                                              isC64);
      }
      catch (Exception e)
      {
        worker.publishMessage("Ignoring " + gbGameInfo.getTitle() +
          ", Could not check game file (file is corrupt?). Game is not imported.");
        ExceptionHandler
          .logException(e, "Could not check game file for " + gbGameInfo.getTitle() + ", game is not imported");
      }
    }
  }

  private String constructAdvancedString(int palOrNtsc, int trueDriveEmu, String gemus)
  {
    //Setup advanced string (system, sid, pal, truedrive etc)
    String advanced = "sid6581";
    if (isC64)
    {
      advanced = "64," + advanced;
    }
    else
    {
      advanced = "vic," + advanced;
    }
    //Setup video mode
    //0=PAL, 1=BOTH, 2=NTSC, 3=PAL[+NTSC?]
    String video = (palOrNtsc == 2) ? "ntsc" : "pal";
    advanced = advanced + "," + video;
    //Setup truedrive
    if (trueDriveEmu > 0 || "vtde=yes".equalsIgnoreCase(gemus))
    {
      advanced = advanced + "," + "driveicon,accuratedisk";
    }
    //For Vic-20 setup memory banks
    if (!isC64)
    {
      String memoryBanks = "";
      gemus = gemus.replaceAll("\\r\\n", "");
      switch (gemus)
      {
      case "memory=3k":
        memoryBanks = "bank0";
        break;
      case "memory=8k":
        memoryBanks = "bank1";
        break;
      case "memory=16k":
        memoryBanks = "bank1,bank2";
        break;
      case "memory=24k":
        memoryBanks = "bank1,bank2,bank3";
        break;
      case "cart=a0":
        memoryBanks = "bank5";
        break;
      case "cart=a000":
        memoryBanks = "bank5";
        break;
      case "cart=20":
        memoryBanks = "bank1";
        break;
      case "cart=2000":
        memoryBanks = "bank1";
        break;
      case "cart=40":
        memoryBanks = "bank2";
        break;
      case "cart=4000":
        memoryBanks = "bank2";
        break;
      case "cart=60":
        memoryBanks = "bank3";
        break;
      case "cart=6000":
        memoryBanks = "bank3";
        break;
      case "memory=all":
        memoryBanks = "bank0,bank1,bank2,bank3,bank5";
        break;
      default:
        break;
      }

      if (!memoryBanks.isEmpty())
      {
        advanced = advanced + "," + memoryBanks;
      }
    }
    return advanced;
  }

  private String getScreen2(String screen1)
  {
    String returnValue = "";

    String screen2 = screen1.substring(0, screen1.lastIndexOf(".")) + "_1.png";
    if (new File(screen2).exists())
    {
      returnValue = screen2;
    }
    else
    {
      //Use screen1 for screen2 also
      returnValue = screen1;
    }
    return returnValue;
  }

  private List<String> getGameFilesToInclude(Path gbPath, String filenameInGb, boolean isVic20cart) throws IOException
  {
    if (filenameInGb.isEmpty())
    {
      return Arrays.asList("");
    }

    File gameFile = gbPath.resolve(filenameInGb).toFile();
    //Increase index to be sure file names are unique in the temp folder
    importIndexForTempFiles++;
    if (isVic20cart)
    {
      return Arrays.asList(
                           FileManager
                             .getTempFileForVic20Cart(new BufferedInputStream(new FileInputStream(gameFile)),
                                                      importIndexForTempFiles + "_" + gameFile.getName())
                             .toPath().toString());
    }
    else
    {
      List<File> selectedFilesList =
        FileManager.createTempFileForScraper(new BufferedInputStream(new FileInputStream(gameFile)),
                                             importIndexForTempFiles + "_" + gameFile.getName());

      sortToGetCorrectMainGameFileFirst(selectedFilesList);
      File mainGameFile = selectedFilesList.get(0);

      List<String> returnList =
        selectedFilesList.stream().map(file -> file.toPath().toString()).collect(Collectors.toList());

      //Do not compress some files: Vice doesn't seem to unzip them properly
      if (FileManager.shouldCompressFile(mainGameFile.getName()))
      {
        Path compressedFilePath = mainGameFile.toPath().getParent().resolve(mainGameFile.getName() + ".gz");
        FileManager.compressGzip(mainGameFile.toPath(), compressedFilePath);
        returnList.set(0, compressedFilePath.toString());
      }
      return returnList;
    }
  }

  private void sortToGetCorrectMainGameFileFirst(List<File> files)
  {
    //Check all files for a crt file and sort that one first, some games may have save disks in a cart zip
    List<File> crtFiles = files.stream().filter(file -> file.getName().endsWith(".crt")).collect(Collectors.toList());
    if (!crtFiles.isEmpty())
    {
      files.remove(crtFiles.get(0));
      files.add(0, crtFiles.get(0));
    }
  }

  public void clearAfterImport()
  {
    gbGameInfoList.clear();
    FileManager.deleteTempFolder();
    importIndexForTempFiles = 0;
  }

  public List<GenreInfo> getAvailableGenres(Path gbDbPath) throws SQLException
  {
    List<GenreInfo> genreList = new ArrayList<>();
    String databaseURL = "jdbc:ucanaccess://" + gbDbPath.toString();
    try (Connection connection = DriverManager.getConnection(databaseURL))
    {
      Statement statement = connection.createStatement();
      String sql = "SELECT PGenres.PG_Id, PGenres.ParentGenre FROM PGenres\r\n";
      ResultSet result = statement.executeQuery(sql);
      Map<Integer, String> pGenresMap = new HashMap<>();
      while (result.next())
      {
        pGenresMap.put(result.getInt("PG_Id"), result.getString("ParentGenre"));
      }

      String subGenreSql = "SELECT Genres.GE_Id, Genres.PG_Id, Genres.Genre FROM Genres\r\n";
      result = statement.executeQuery(subGenreSql);
      Map<Integer, List<SubGenre>> subGenresMap = new HashMap<>();
      while (result.next())
      {
        int pgId = result.getInt("PG_Id");
        List<SubGenre> subgenres = subGenresMap.getOrDefault(pgId, new ArrayList<>());
        subgenres.add(new SubGenre(result.getInt("GE_Id"), result.getString("Genre")));
        subGenresMap.put(pgId, subgenres);
      }

      //Create list
      for (Integer pgId : pGenresMap.keySet())
      {
        String genreString = pGenresMap.get(pgId);
        for (SubGenre subGenre : subGenresMap.get(pgId))
        {
          genreList.add(new GenreInfo(genreString + " - " + subGenre.getName(), pgId, subGenre.getGeId()));
        }
      }
    }
    return genreList;
  }

  private class SubGenre
  {
    private int geId;
    private String name;

    public SubGenre(int geId, String name)
    {
      this.geId = geId;
      this.name = name;
    }

    public int getGeId()
    {
      return geId;
    }

    public void setGeId(int geId)
    {
      this.geId = geId;
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }
  }
}
