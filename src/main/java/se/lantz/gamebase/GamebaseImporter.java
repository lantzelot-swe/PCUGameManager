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
import java.util.List;

import se.lantz.manager.ImportManager;
import se.lantz.scraper.GamebaseScraper;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class GamebaseImporter
{
  private static final int CHUNK_SIZE = 100;

  public enum Options
  {
    ALL, FAVORITES, QUERY;
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

  public GamebaseImporter(ImportManager importManager)
  {
    this.importManager = importManager;
    importManager = null;
  }

  public boolean setImportOptions(GamebaseOptions options)
  {
    this.gbDatabasePath = options.getGamebaseDbFile();
    this.isC64 = options.isC64();
    this.selectedOption = options.getSelectedOption();
    this.titleQueryString = options.getTitleQueryString();
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

  public StringBuilder importFromGamebase()
  {
    gbGameInfoList.clear();
    StringBuilder builder = new StringBuilder();
    //Use the folder where the gamebase mdb file is located in the import manager
    importManager.setSelectedFoldersForGamebase(gbGamesPath, gbScreensPath, gbExtrasPath.resolve("covers"));

    String databaseURL = "jdbc:ucanaccess://" + gbDatabasePath.toString();

    //Ucanaccess does not work properly in standalone installation if this is not added
    try
    {
      Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
    }
    catch (ClassNotFoundException e1)
    {
      ExceptionHandler.handleException(e1, "");
    }

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
        condition = "WHERE (((Games.Name) LIKE '" + titleQueryString + "'));";
        break;
      default:
        break;
      }
      sql = sql + condition;

      ResultSet result = statement.executeQuery(sql);
      int gameCount = 0;
      while (result.next())
      {
        if (createGbGameInfo(result, statement, builder))
        {
          gameCount++;
        }
      }
      builder.append("Read " + gameCount + " games from the gamebase db.\n");
    }
    catch (SQLException ex)
    {
      builder.append("ERROR: Query failed : " + ex.toString() + "\n");
      ExceptionHandler.handleException(ex, "Query failed");
    }
    return builder;
  }
  
  private boolean createGbGameInfo(ResultSet result, Statement statement, StringBuilder builder)
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
      
      //Game file
      if (isC64)
      {
        //GB64 includes game files for all games, no additional extras available.
        if (gamefile.isEmpty() && !includeEntriesWithMissingGameFile)
        {
          builder.append("Ignoring " + title + " (No game file available)\n");
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
            builder.append("Ignoring " + title + " (No game file available)\n");
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
      builder.append("ERROR: Could not fetch all info for " + title + ":\n" + e.toString() + "\n");
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

  public StringBuilder checkGameFileForGbGames(List<GbGameInfo> gbGameList)
  {
    StringBuilder builder = new StringBuilder();
    for (GbGameInfo gbGameInfo : gbGameList)
    {
      try
      {
        String gameFile = getFileToInclude(gbGamesPath, gbGameInfo.getGamefile(), gbGameInfo.isVic20Cart());
        importManager.addFromGamebaseImporter(gbGameInfo.getTitle(),
                                              gbGameInfo.getYear(),
                                              gbGameInfo.getPublisher(),
                                              gbGameInfo.getMusician(),
                                              gbGameInfo.getGenre(),
                                              gameFile,
                                              gbGameInfo.getCoverFile(),
                                              gbGameInfo.getScreen1(),
                                              gbGameInfo.getScreen2(),
                                              gbGameInfo.getJoy1config(),
                                              gbGameInfo.getJoy2config(),
                                              gbGameInfo.getAdvanced(),
                                              gbGameInfo.getDescription(),
                                              isC64);
      }
      catch (Exception e)
      {
        builder.append("Ignoring " + gbGameInfo.getTitle() +
          ", Could not check game file (file is corrupt?). Game is not imported.\n");
        ExceptionHandler
          .logException(e, "Could not check game file for " + gbGameInfo.getTitle() + ", game is not imported");
      }
    }
    return builder;
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

  private String getFileToInclude(Path gbPath, String filenameInGb, boolean isVic20cart) throws IOException
  {
    if (filenameInGb.isEmpty())
    {
      return "";
    }

    File gameFile = gbPath.resolve(filenameInGb).toFile();
    //Increase index to be sure file names are unique in the temp folder
    importIndexForTempFiles++;
    if (isVic20cart)
    {
      return FileManager
        .getTempFileForVic20Cart(new BufferedInputStream(new FileInputStream(gameFile)), importIndexForTempFiles + "_" + gameFile.getName()).toPath()
        .toString();
    }
    else
    {
      File selectedFile = FileManager.createTempFileForScraper(new BufferedInputStream(new FileInputStream(gameFile)),
                                                               importIndexForTempFiles + "_" + gameFile.getName());
      //Do not compress prg files: Vice doesn't seem to unzip them properly
      String lowercaseName =  selectedFile.getName().toLowerCase();
      if (lowercaseName.endsWith(".gz") || lowercaseName.endsWith(".prg") || lowercaseName.endsWith(".p00") )
      {
        return selectedFile.toPath().toString();
      }
      else
      {
        Path compressedFilePath = selectedFile.toPath().getParent().resolve(selectedFile.getName() + ".gz");
        FileManager.compressGzip(selectedFile.toPath(), compressedFilePath);
        return compressedFilePath.toString();
      }
    }
  }

  public void clearAfterImport()
  {
    gbGameInfoList.clear();
    FileManager.deleteTempFolder();
    importIndexForTempFiles = 0;
  }
}
