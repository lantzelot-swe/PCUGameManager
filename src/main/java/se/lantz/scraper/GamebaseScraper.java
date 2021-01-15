package se.lantz.scraper;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.data.ScraperFields;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class GamebaseScraper implements Scraper
{
  private static final Logger logger = LoggerFactory.getLogger(GamebaseScraper.class);
  Map<String, String> genreMap = new HashMap<>();
  private String gamebaseGameUrl;
  private String titleCssQuery =
    "body > table:eq(3) > tbody > tr > td.back > table > tbody > tr > td > table > tbody > tr:eq(0) > td > table > tbody > tr > td:eq(0) > font > b";

  private String authorCssQuery =
    "body > table:eq(3) > tbody > tr > td.back > table > tbody > tr > td > table > tbody > tr:eq(1) > td > table > tbody > tr:eq(1) > td:eq(1) > table > tbody > tr > td:eq(0) > table:eq(1) > tbody > tr > td > table > tbody > tr:eq(0) > td > font:eq(2) > a:eq(1) > b";
  private String yearCssQuery =
    "body > table:eq(3) > tbody > tr > td.back > table > tbody > tr > td > table > tbody > tr:eq(1) > td > table > tbody > tr:eq(1) > td:eq(1) > table > tbody > tr > td:eq(0) > table:eq(1) > tbody > tr > td > table > tbody > tr:eq(0) > td > font:eq(2) > a:eq(0) > b";
  private String genreCssQuery =
    "body > table:eq(3) > tbody > tr > td.back > table > tbody > tr > td > table > tbody > tr:eq(1) > td > table > tbody > tr:eq(1) > td:eq(1) > table > tbody > tr > td:eq(0) > table:eq(1) > tbody > tr > td > table > tbody > tr:eq(5) > td > font:eq(2) > a > b";
  private String composerCssQuery =
    "body > table:eq(3) > tbody > tr > td.back > table > tbody > tr > td > table > tbody > tr:eq(1) > td > table > tbody > tr:eq(1) > td:eq(1) > table > tbody > tr > td:eq(0) > table:eq(1) > tbody > tr > td > table > tbody > tr:eq(1) > td > font:eq(2) > a > b";
  private String screensCssQuery =
    "body > table:eq(3) > tbody > tr > td.back > table > tbody > tr > td > table > tbody > tr:eq(1) > td > table > tbody > tr:eq(1) > td:eq(1) > table > tbody > tr > td:eq(2) > div > table > tbody > tr:eq(1) > td > div > table > tbody > tr:eq(1) > td:eq(1) > img";
  private String gameCssQuery =
    "body > table:eq(3) > tbody > tr > td.back > table > tbody > tr > td > table > tbody > tr:eq(1) > td > table > tbody > tr:eq(1) > td:eq(1) > table > tbody > tr > td:eq(2) > div > table > tbody > tr:eq(3) > td > font > table > tbody > tr:eq(1) > td > b > a";

  private String scrapedTitle = "";
  private String scrapedAuthor = "";
  private int scrapedYear = 1985;
  private String scrapedGenre = "";
  private String scrapedComposer = "";
  private File scrapedFile;

  public GamebaseScraper()
  {
    //Keys are Genres defined on gamebase64.com, values are supported genres in the tool
    genreMap.put("Arcade / Adventure / Miscellaneous", "adventure");
    genreMap.put("Racing / Driving", "driving");
    genreMap.put("Strategy / Brain / Puzzle", "puzzle");
    genreMap.put("Educational", "programming");
    genreMap.put("Simulation / Gambling / Cards / Board Game", "simulation");
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
    scrapedGenre = "";
  }

  @Override
  public void scrapeInformation(ScraperFields fields)
  {
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(gamebaseGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch title
      if (fields.isTitle())
      {
        Elements queryElements = doc.select(titleCssQuery);
        Element first = queryElements.first();
        if (first != null)
        {
          scrapedTitle = first.text();
        }
      }
      if (fields.isAuthor())
      {
        Elements queryElements = doc.select(authorCssQuery);
        Element first = queryElements.first();
        if (first != null)
        {
          scrapedAuthor = first.text();
        }
      }
      if (fields.isYear())
      {
        Elements queryElements = doc.select(yearCssQuery);
        Element first = queryElements.first();
        if (first != null)
        {
          try
          {
            scrapedYear = Integer.parseInt(first.text().trim());
          }
          catch (Exception e)
          {
            logger.error("Could not scrape year for {}", scrapedTitle);
          }
        }
      }

      if (fields.isGenre())
      {
        Elements queryElements = doc.select(genreCssQuery);
        Element first = queryElements.first();
        if (first != null)
        {
          scrapedGenre = mapGenre(first.text());
        }
      }
      if (fields.isComposer())
      {
        Elements queryElements = doc.select(composerCssQuery);
        Element first = queryElements.first();
        if (first != null)
        {
          scrapedComposer = first.text();
        }
      }
      if (fields.isGame())
      {
        scrapeGame(doc);
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape information");
    }

  }

  private String mapGenre(String genreFromGb64com)
  {
    //Strip subgenre
    String[] genres = genreFromGb64com.split("-");
    String parentGenre = genres[0].trim();
    //Map towards available genres, return first one found
    for (Map.Entry<String, String> entry : genreMap.entrySet())
    {
      if (entry.getKey().contains(parentGenre))
      {
        return entry.getValue();
      }
    }
    return "";
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
    return scrapedComposer;
  }

  @Override
  public BufferedImage getCover()
  {
    return null;
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
    List<BufferedImage> screensList = new ArrayList<>();
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(gamebaseGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements coverElements = doc.select(screensCssQuery);
      if (coverElements.first() != null)
      {
        Element coverElement = coverElements.first();
        String absoluteUrl = coverElement.absUrl("src");
        //Try to fetch 6 screenshots based on number " 1,  2,  3" etc
        for (int i = 0; i < 6; i++)
        {
          URL imageUrl = new URL(absoluteUrl);
          screensList.add(ImageIO.read(imageUrl));
          //Replace number in url
          if (i == 0)
          {
            absoluteUrl = absoluteUrl.replace(".png", "_1.png");
          }
          else
          {
            absoluteUrl = absoluteUrl.replace("_" + Integer.toString(i), "_" + Integer.toString(i + 1));
          }
        }
      }
    }
    catch (IOException e)
    {
      logger.warn("Could not scrape all six screenshots");
    }
    return screensList;
  }

  private void scrapeGame(Document doc)
  {
    Elements queryElements = doc.select(gameCssQuery);
    Element gameElement = queryElements.first();
    if (gameElement != null)
    {
      try
      {
        String urlString = gameElement.attr("abs:href");
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        InputStream inputStream = conn.getInputStream();
        //create a temp file and fetch the content
        scrapedFile = FileManager.createTempFileForScraper(new BufferedInputStream(inputStream));
        logger.debug("File to include as game: {}", scrapedFile != null ? scrapedFile.getAbsolutePath() : null);
      }
      catch (Exception e)
      {
        logger.error("Could not scrape game file for " + scrapedTitle, e);
      }
    }
  }
}
