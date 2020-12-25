package se.lantz.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.data.ScraperFields;

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

  private String scrapedTitle = "";

  private String scrapedAuthor = "";

  private int scrapedYear = 1985;

  private String scrapedDescription = "";

  private String scrapedGenre = "";

  private BufferedImage scrapedCover = null;

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

  public void connectToMobyGames(String url) throws IOException
  {
    this.mobyGamesGameUrl = "";
    Jsoup.connect(url).method(Connection.Method.GET).execute();
    this.mobyGamesGameUrl = url;
  }

  public void scrapeInformation(ScraperFields fields)
  {
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(mobyGamesGameUrl).method(Connection.Method.GET).execute();
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
        scrapedAuthor = scarpeElementValue(doc, authorCssQuery);
      }
      if (fields.isYear())
      {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(scarpeElementValue(doc, yearCssQuery));
        if (m.find())
        {
          scrapedYear = Integer.parseInt(m.group());
        }
      }
      if (fields.isDescription())
      {
        scrapedDescription = scrapeDescription(doc);
      }
      if (fields.isGenre())
      {
        String genre = scrapeGenre(doc);
        if (!genre.isEmpty())
        {
          scrapedGenre = genre;
        }
      }
      if (fields.isCover())
      {
        scrapedCover = scrapeCover(doc);
      }
      //TODO: Screens
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape title");
    }
    //    
    //    if (fields.isScreenshots())
    //    {  //TODO: Make it possible to select which screenshot to use
    //      List<BufferedImage> images = scraper.scrapeScreenshots();
    //      if (images.size() > 0)
    //      {
    //        infoModel.setScreen1Image(images.get(0));
    //      }
    //      if (images.size() > 1)
    //      {
    //        infoModel.setScreen2Image(images.get(1));
    //      }
    //    }
  }

  public String getTitle()
  {
    return scrapedTitle;
  }

  public String getAuthor()
  {
    return scrapedAuthor;
  }

  public int getYear()
  {
    return scrapedYear;
  }

  public String getDescription()
  {
    return scrapedDescription;
  }

  public String getGenre()
  {
    return scrapedGenre;
  }

  public BufferedImage getCover()
  {
    return scrapedCover;
  }

  //  public String scrapeTitle()
  //  {
  //    String value = "";
  //    Document doc;
  //    try
  //    {
  //      Connection.Response result = Jsoup.connect(mobyGamesGameUrl).method(Connection.Method.GET).execute();
  //      doc = result.parse();
  //      //Fetch the right element
  //      Elements queryElements = doc.select(titleCssQuery);
  //      Element first = queryElements.first();
  //      if (first != null)
  //      {
  //        value = first.text();
  //      }
  //    }
  //    catch (IOException e)
  //    {
  //      ExceptionHandler.handleException(e, "Could not scrape title");
  //    }
  //    return value;
  //  }

  public String scrapeDescription(Document doc)
  {
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
    return "";
  }

  public String scrapeGenre(Document doc)
  {
    String genreFromMobyGames = scarpeElementValue(doc, genreCssQuery);
    String[] split = genreFromMobyGames.split(", ");
    for (int i = 0; i < split.length; i++)
    {
      //Map towards available genres, return first one found
      for (Map.Entry<String, String> entry : genreMap.entrySet())
      {
        if (entry.getKey().contains(split[i]))
        {
          System.out.println(entry.getKey() + "/" + entry.getValue());
          return entry.getValue();
        }
      }
    }
    return "";
  }

  private String scarpeElementValue(Document doc, String cssQuery)
  {
    String value = "";
    //Fetch the right element
    Elements queryElements = doc.select(cssQuery);
    Element first = queryElements.first();
    if (first != null)
    {
      int index = queryElements.first().elementSiblingIndex();
      Element valueElement = first.parent().child(index + 1);
      value = valueElement.text();
    }
    return value;
  }

  public BufferedImage scrapeCover(Document doc)
  {
    //Fetch the right element
    Elements coverElements = doc.select(coverCssQuery);
    if (coverElements.first() != null)
    {
      Element coverElement = coverElements.first();
      String bigCoverUrl = coverElement.parent().attr("href");
      return scrapeBigCover(bigCoverUrl);
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
      Connection.Response result =
        Jsoup.connect(mobyGamesGameUrl + "/screenshots").method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements coverElements = doc.select(screensCssQuery);

      logger.debug("Number of screenshots found: {}", coverElements.size());
      for (Element element : coverElements)
      {
        String bigScreenUrl = element.attr("href");
        logger.debug("Screen URL = " + bigScreenUrl);
        returnList.add(scrapeBigScreenshot(bigScreenUrl));
      }
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
