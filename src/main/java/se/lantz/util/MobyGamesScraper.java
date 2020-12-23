package se.lantz.util;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobyGamesScraper
{
  private static final Logger logger = LoggerFactory.getLogger(MobyGamesScraper.class);

  private String mobyGamesBaseUrl = "https://www.mobygames.com/game/c64/*";

  private String mobyGamesGameUrl = "";

  private String game = "rushn-attack";

  private String descriptionCssQuery = "#main > div > div:eq(2) > div";

  private String titleCssQuery = ".niceHeaderTitle > a";

  private String authorCssQuery = "#coreGameRelease > div:contains(Published)";

  private String yearCssQuery = "#coreGameRelease > div:contains(Released)";

  private String genreCssQuery = "#coreGameGenre > div > div:contains(Genre)";

  private String coverCssQuery = "#coreGameCover > a > img";
  
  private String screensCssQuery = ".thumbnail-image-wrapper > a";

  private long startTime = 0L;

  Map<String, String> genreMap = new HashMap<>();
  
  public MobyGamesScraper()
  {
    // TODO Auto-generated constructor stub

  //*[@id="main"]/div/div[2]/h1/a
    
  //*[@id="main"]/div/div[3]/div[1]/h2[1]
    
    //*[@id="coreGameCover"]/a/img
    
    //Keys are Genres defined on MobyGames, values are supported genres in the tool
    genreMap.put("Adventure, Role-Playing (RPG)", "adventure");
    genreMap.put("Racing / driving", "driving");
    genreMap.put("Puzzle, Strategy / tactics", "puzzle");
    genreMap.put("Educational", "programming");
    genreMap.put("Simulation", "simulation");
    genreMap.put("Sports", "sport");
  }

  public static void main(String[] args)
  {
    MobyGamesScraper scraper = new MobyGamesScraper();
    scraper.scrapeMobyGames();
  }

  public void connectToMobyGames(String url) throws IOException
  {
    this.mobyGamesGameUrl = "";
    Jsoup.connect(url).method(Connection.Method.GET).execute();
    this.mobyGamesGameUrl = url;
  }

  public String scrapeTitle()
  {
    String value = "";
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(mobyGamesGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements queryElements = doc.select(titleCssQuery);
      Element first = queryElements.first();
      if (first != null)
      {
        value = first.text();
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape title");
    }
    return value;
  }

  public String scrapeAuthor()
  {
    return scarpeElementValue(authorCssQuery);
  }

  public String scrapeYear()
  {
    return scarpeElementValue(yearCssQuery);
  }
  
  private void scrapeMobyGames()
  {
    startTime = System.currentTimeMillis();
    logger.debug("Scraping  {} ...", mobyGamesBaseUrl);

    scrapeDescription();

    System.out.println("Author: " + scarpeElementValue(authorCssQuery));
    System.out.println("Year: " + scarpeElementValue(yearCssQuery));
    System.out.println("Genre: " + scarpeElementValue(genreCssQuery));

    scrapeCover();
  }

  public String scrapeDescription()
  {
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(mobyGamesGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements descriptionDiv = doc.select(descriptionCssQuery);
      if (descriptionDiv.first() != null)
      {
        //Get all text elements
        List<TextNode> textNodes = descriptionDiv.first().textNodes();
        StringBuilder builder = new StringBuilder();
        for (TextNode textNode : textNodes)
        {
          if (textNode.text().length() > 1)
          {
            builder.append(textNode.text());
          }
        }
        return builder.toString();
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape description");
    }
    return "";
  }
  
  public String scrapeGenre()
  {
    String genreFromMobyGames = scarpeElementValue(genreCssQuery);
    String[] split = genreFromMobyGames.split(", ");
    for (int i = 0; i < split.length; i++)
    {
      //Map towards available genres, return first one found
      for (Map.Entry<String, String> entry : genreMap.entrySet()) {
        if (entry.getKey().contains(split[i]))
        {
          System.out.println(entry.getKey() + "/" + entry.getValue());
          return entry.getValue();
        } 
      }
    }
    
    return "";
  }

  private String scarpeElementValue(String cssQuery)
  {
    String value = "";
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(mobyGamesGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements queryElements = doc.select(cssQuery);
      Element first = queryElements.first();
      if (first != null)
      {
        int index = queryElements.first().elementSiblingIndex();
        Element valueElement = first.parent().child(index + 1);
        value = valueElement.text();
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape information (" + cssQuery + ")");
    }
    return value;
  }

  public BufferedImage scrapeCover()
  {
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(mobyGamesGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements coverElements = doc.select(coverCssQuery);
      if (coverElements.first() != null)
      {
        Element coverElement = coverElements.first();
        String bigCoverUrl = coverElement.parent().attr("href");
        return scrapeBigCover(bigCoverUrl);
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape cover");
    }
    return null;
  }
 
  private BufferedImage scrapeBigCover(String url)
  {
    String cssQuery = "#main > div > div:eq(1) > center > img"; //*[@id="main"]/div/div[2]/center/img
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(url).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements coverElements = doc.select(cssQuery);
      if (coverElements.first() != null)
      {
        Element coverElement = coverElements.first();
        String absoluteUrl = coverElement.absUrl("src");

        URL imageUrl = new URL(absoluteUrl);
        return ImageIO.read(imageUrl);    
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape cover");
    }
    return null;
  }
  
  public List<BufferedImage> scrapeScreenshots()
  {
    List<BufferedImage> returnList = new ArrayList<>();
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(mobyGamesGameUrl + "/screenshots").method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements coverElements = doc.select(screensCssQuery);
      
      logger.debug("Number of screenshots found: {}", coverElements.size());
      //Only scrape first two for now
      for (int i = 0; i < Math.min(2, coverElements.size()); i++)
      {
        String bigScreenUrl = coverElements.get(i).attr("href");
        logger.debug("Screen URL = " + bigScreenUrl);
        returnList.add(scrapeBigScreenshot(bigScreenUrl));
      } 
//      for (Element element : coverElements)
//      {
//        String bigScreenUrl = element.attr("href");
//        logger.debug("Screen URL = " + bigScreenUrl);
//        returnList.add(scrapeBigScreenshot(bigScreenUrl));
//      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape cover");
    }
    return returnList;
  }

  private BufferedImage scrapeBigScreenshot(String url)
  {
    String cssQuery = "#main > div > div:eq(1) > div > div > img"; //*[@id="main"]/div/div[2]/div/div/img
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(url).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements coverElements = doc.select(cssQuery);
      if (coverElements.first() != null)
      {
        Element coverElement = coverElements.first();
        String absoluteUrl = coverElement.absUrl("src");

        URL imageUrl = new URL(absoluteUrl);
        return ImageIO.read(imageUrl);    
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape screenshot");
    }
    return null;
  }
}
