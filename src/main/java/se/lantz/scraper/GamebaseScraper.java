package se.lantz.scraper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import se.lantz.model.data.ScraperFields;

public class GamebaseScraper implements Scraper
{

  Map<String, String> genreMap = new HashMap<>();
  private String gamebaseGameUrl;  
  private List<String> scrapedMusicList = new ArrayList<>();
  
  private String scrapedTitle = "";
  private String scrapedAuthor = "";
  private int scrapedYear = 1985;
  private String scrapedDescription = "";
  private String scrapedGenre = ""; 
  private String scrapedComposer = "";
  private BufferedImage scrapedCover = null;
  private File scrapedFile;
  
  public GamebaseScraper()
  {
    //Keys are Genres defined on gamebase64.com, values are supported genres in the tool
    //TODO
    genreMap.put("Action / Adventure / Miscellaneous / Text adventure", "adventure");
    genreMap.put("Racing / Driving", "driving");
    genreMap.put("Strategy / Board game / Puzzle", "puzzle");
    genreMap.put("Educational", "programming");
    genreMap.put("Simulation / Simulator", "simulation");
    genreMap.put("Sports", "sport");
    genreMap.put("Maze / Breakout", "maze");
    genreMap.put("Platform", "platform");
    genreMap.put("Shoot'em up", "shoot");
  }

  @Override
  public void connect(String url) throws IOException
  {
    this.gamebaseGameUrl = "";
    Jsoup.connect(url).method(Connection.Method.GET).execute();
    this.gamebaseGameUrl = url;
    resetFields();
  }
  
  private void resetFields()
  {
    scrapedTitle = "";
    scrapedYear = 1985;
    scrapedAuthor = "";
    scrapedComposer = "";
    scrapedDescription = "";
    scrapedCover = null;
    scrapedGenre = "";
  }

  @Override
  public void scrapeInformation(ScraperFields fields)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public String getTitle()
  {
    return scrapedTitle;
  }

  @Override
  public String getAuthor()
  {
    return scrapedAuthor;
  }

  @Override
  public int getYear()
  {
    return scrapedYear;
  }

  @Override
  public String getDescription()
  {
    //Not supported, no description on c64.com
    return "";
  }

  @Override
  public String getGenre()
  {
    return scrapedGenre;
  }
  
  @Override
  public String getComposer()
  {
    return String.join(", ", scrapedMusicList);
  }

  @Override
  public BufferedImage getCover()
  {
    return scrapedCover;
  }
  
  @Override
  public File getGameFile()
  {
    return scrapedFile;
  }

  @Override
  public boolean isC64()
  {
    // Only C64 games available
    return true;
  }

  @Override
  public List<BufferedImage> scrapeScreenshots()
  {
    // TODO Auto-generated method stub
    return new ArrayList<>();
  }

}
