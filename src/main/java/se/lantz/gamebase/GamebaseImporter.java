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
import se.lantz.util.FileManager;

public class GamebaseImporter
{
  public enum Options
  {
    ALL, FAVORITES, QUERY;
  }

  private final ImportManager importManager;
  //Just for test
  private Path gbDatabasePath = Path.of("C://GameBase//GBC_V16//");

  public GamebaseImporter(ImportManager importManager)
  {
    this.importManager = importManager;
    importManager = null;
  }

  public void setImportOptions(GamebaseOptions options)
  {
    this.gbDatabasePath = options.getGamebaseDbFile();
  }

  public StringBuilder importFromGamebase()
  {
    StringBuilder builder = new StringBuilder();
    //Use the folder where the gamebase mdb file is located in the import manager
    importManager.setSelectedFolder(gbDatabasePath.getParent());
    //Just for test, use gbDatabasePath - "jdbc:ucanaccess:" + gbDatabasePath.toString()
    String databaseURL = "jdbc:ucanaccess://F://Github//PCUGameManager//GBC_v16.mdb";

    String joyBase = ":JU,JD,JL,JR,JF,JF,SP,EN,,F1,F3,F5,,,";

    String joy1config;
    String joy2config;

    String advanced = "64,pal,sid6581";

    try (Connection connection = DriverManager.getConnection(databaseURL))
    {
      Statement statement = connection.createStatement();
      //Get views

      String sql = "SELECT * FROM ViewData";

      ResultSet result = statement.executeQuery(sql);
      while (result.next())
      {
        String title = result.getString("Title");
        System.out.println("view: " + title);
      }

      sql =
        "SELECT Games.Name, Musicians.Musician, Genres.Genre, Publishers.Publisher, Games.Filename, Games.ScrnshotFilename, Years.Year, Games.GA_Id, Games.Control\r\n" +
          "FROM Years INNER JOIN (Publishers INNER JOIN ((Games INNER JOIN Musicians ON Games.MU_Id = Musicians.MU_Id) INNER JOIN Genres ON Games.GE_Id = Genres.GE_Id) ON Publishers.PU_Id = Games.PU_Id) ON Years.YE_Id = Games.YE_Id\r\n" +
          "WHERE (((Games.Name)='1942'));";

      result = statement.executeQuery(sql);
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

          //Fix joystick port
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
          //Fix game file
          gamefile = getFileToInclude(gbDatabasePath, gamefile);

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
        }
      }
      builder.append("Read " + gameCount + " games from the gamebase db.\n");
    }
    catch (SQLException ex)
    {
      builder.append("ERROR: Query failed : " + ex.toString() + "\n");
    }
    return builder;
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
