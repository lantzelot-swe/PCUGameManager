package se.lantz.gamebase;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import se.lantz.manager.ImportManager;
import se.lantz.scraper.GamebaseScraper;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class GamebaseImporter
{
  public enum Options
  {
    ALL, FAVORITES, QUERY;
  }

  private final ImportManager importManager;
  //Just for test
  private Path gbDatabasePath = Path.of("C://GameBase//GBC_V16//");//Path.of("C://GameBase//Vic20_v03//");
  private boolean isC64 = true;//false;

  public GamebaseImporter(ImportManager importManager)
  {
    this.importManager = importManager;
    importManager = null;
  }

  public void setImportOptions(GamebaseOptions options)
  {
//    this.gbDatabasePath = options.getGamebaseDbFile();
//    this.isC64 = options.isC64();
  }

  public StringBuilder importFromGamebase()
  {
    StringBuilder builder = new StringBuilder();
    //Use the folder where the gamebase mdb file is located in the import manager
//    importManager.setSelectedFolder(gbDatabasePath.getParent());
    importManager.setSelectedFolder(gbDatabasePath);
    //Just for test, use gbDatabasePath - "jdbc:ucanaccess:" + gbDatabasePath.toString()
    String vic20Test = "jdbc:ucanaccess://C://GameBase//Vic20_v03//Vic20_v03.mdb";
    
    String databaseURL = "jdbc:ucanaccess://F://Github//PCUGameManager//GBC_v16.mdb";

    String joyBase = ":JU,JD,JL,JR,JF,JF,SP,EN,,F1,F3,F5,,,";

    try (Connection connection = DriverManager.getConnection(databaseURL))
    {
      Statement statement = connection.createStatement();
      //Get views
//
//      String sql = "SELECT * FROM ViewData";
//
//      ResultSet result = statement.executeQuery(sql);
//      while (result.next())
//      {
//        String title = result.getString("Title");
//        System.out.println("view: " + title);
//      }

      String sql =
        "SELECT TOP 300 Games.Name, Musicians.Musician, Genres.Genre, Publishers.Publisher, Games.Filename, Games.ScrnshotFilename, Years.Year, Games.GA_Id, Games.Control, Games.V_PalNTSC, Games.V_TrueDriveEmu, Games.Gemus\r\n" +
          "FROM Years INNER JOIN (Publishers INNER JOIN ((Games INNER JOIN Musicians ON Games.MU_Id = Musicians.MU_Id) INNER JOIN Genres ON Games.GE_Id = Genres.GE_Id) ON Publishers.PU_Id = Games.PU_Id) ON Years.YE_Id = Games.YE_Id\r\n" +
          "WHERE (((Games.Name) LIKE 'a*'));";

      ResultSet result = statement.executeQuery(sql);
      int gameCount = 0;
      while (result.next())
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
          String genre = result.getString("Genre");
          String publisher = result.getString("Publisher");
          int control = result.getInt("Control");
          int palOrNtsc = result.getInt("V_PalNTSC");
          int trueDriveEmu = result.getInt("V_TrueDriveEmu");
          String gemus = result.getString("Gemus");
                    
          if (gamefile.isEmpty())
          {
            builder.append("Ignoring " + title + " (No game file available)\n");
            continue;
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
          screen1 = gbDatabasePath.toString() + "\\screenshots\\" + screen1;
          String screen2 = getScreen2(screen1);

          //Map genre properly towards existing ones for the carousel
          genre = GamebaseScraper.mapGenre(genre);

          //Get cover
          String coverFile = "";
          String coverSql =
            "SELECT Extras.Name, Extras.Path\r\n" + "FROM Games INNER JOIN Extras ON Games.GA_Id = Extras.GA_Id\r\n" +
              "WHERE (((Games.GA_Id)=" + gameId + ") AND ((Extras.Name) Like \"Cover*\"));";

          ResultSet sqlResult = statement.executeQuery(coverSql);
          if (sqlResult.next())
          {
            coverFile = sqlResult.getString("Path");
          }
          if (!coverFile.isEmpty())
          {
            coverFile = gbDatabasePath.toString() + "\\extras\\" + coverFile;
          }
                             
          //Get cartridge if available, easyflash is preferred
          String cartridgeSql =
            "SELECT Extras.Name, Extras.Path\r\n" + "FROM Games INNER JOIN Extras ON Games.GA_Id = Extras.GA_Id\r\n" +
              "WHERE (((Games.GA_Id)=" + gameId + ") AND ((Extras.Name) Like \"*Cartridge*\"));";

          sqlResult = statement.executeQuery(cartridgeSql);
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
          
          if (!cartridgePath.isEmpty())
          {
            gamefile = gbDatabasePath.toString() + "\\extras\\" + cartridgePath;
          }
          
          //Fix game file
          gamefile = getFileToInclude(gbDatabasePath, gamefile);
                   
          importManager.addFromGamebaseImporter(title,
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
                                                advanced);
          gameCount++;
        }
        catch (Exception e)
        {
          builder.append("ERROR: Could not fetch all info for " + title + ":\n" + e.toString() + "\n");
          ExceptionHandler.handleException(e, "Could not fetch all info for " + title);
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
    String video = (palOrNtsc == 2)  ? "ntsc" : "pal";
    advanced = advanced + "," + video;
    //Setup truedrive
    if (trueDriveEmu > 0 || "vte=yes".equalsIgnoreCase(gemus))
    {
      advanced = advanced + "," + "driveicon,accuratedisk";
    }
    return advanced;
  }

  private String getScreen2(String screen1)
  {
    String returnValue = "";
    String screen2 = screen1.substring(0, screen1.lastIndexOf(".")) + "_1.png";
    File screen2File = new File(screen2);
    if (screen2File.exists())
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

  private String getFileToInclude(Path gbPath, String filenameInGb) throws IOException
  {
    Path gamePath = gbPath.resolve("games").resolve(filenameInGb);
    File gameFile = gamePath.toFile();

    File selectedFile = FileManager.createTempFileForScraper(new BufferedInputStream(new FileInputStream(gameFile)));
    Path compressedFilePath = selectedFile.toPath().getParent().resolve(selectedFile.getName() + ".gz");
    FileManager.compressGzip(selectedFile.toPath(), compressedFilePath);
    return compressedFilePath.toString();
  }

}
