package se.lantz.gamebase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GamebaseAccessTest
{

  public GamebaseAccessTest()
  {
  }

  public static void main(String[] args)
  {

    String databaseURL = "jdbc:ucanaccess://F://Github//PCUGameManager//GBC_v16.mdb";

    try (Connection connection = DriverManager.getConnection(databaseURL))
    {
      Statement statement = connection.createStatement();
      //Get views

      String sql = "SELECT * FROM ViewData";

      ResultSet result = statement.executeQuery(sql);
      result = statement.executeQuery(sql);
      while (result.next())
      {

        String title = result.getString("Title");
        System.out.println("view: " + title);
      }

      sql = "SELECT Games.Name, Musicians.Musician, Genres.Genre, Publishers.Publisher, Games.Filename, Games.ScrnshotFilename, Games.GA_Id\r\n" + 
        "FROM Publishers INNER JOIN ((Games INNER JOIN Musicians ON Games.MU_Id = Musicians.MU_Id) INNER JOIN Genres ON Games.GE_Id = Genres.GE_Id) ON Publishers.PU_Id = Games.PU_Id\r\n" + 
        "WHERE (((Games.Name)='1942'));";
      result = statement.executeQuery(sql);
      while (result.next())
      {
        String name = result.getString("Name");
        int gameId = result.getInt("GA_Id");
        String filename = result.getString("Filename");
        String screen1 = result.getString("ScrnShotFileName");
        String musician = result.getString("Musician");
        String genre = result.getString("Genre");
        String publisher = result.getString("Publisher");
        System.out.println(gameId + "," + name + ", " + filename + ", " + screen1 + ", " + musician + ", " + genre + ", " + publisher);
        
        //Create a Map of info like in ImportManager where all data is added in a list. 
      }

    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
  }
}
