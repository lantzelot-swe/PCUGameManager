package se.lantz.scraper;

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
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.data.ScraperFields;
import se.lantz.util.ExceptionHandler;

public class MobyGamesScraper implements Scraper
{
  private static final Logger logger = LoggerFactory.getLogger(MobyGamesScraper.class);

  private String mobyGamesGameUrl = "";

  private String descriptionCssQuery = "#main > div > div:eq(2) > div";

  private String titleCssQuery = ".niceHeaderTitle > a";

  private String authorCssQuery = "#coreGameRelease > div:contains(Published)";

  private String yearCssQuery = "#coreGameRelease > div:contains(Released)";

  private String genreCssQuery = "#coreGameGenre > div > div:contains(Genre)";

  private String coverCssQuery = "#coreGameCover > a > img";

  private String screensCssQuery = ".thumbnail-image-wrapper > a";

  Map<String, String> genreMap = new HashMap<>();

  private String scrapedTitle = "";

  private String scrapedAuthor = "";

  private int scrapedYear = 1985;

  private String scrapedDescription = "";

  private String scrapedGenre = "";
  
  private String scrapedComposer = "";

  private BufferedImage scrapedCover = null;

  public MobyGamesScraper()
  {
    //Keys are Genres defined on MobyGames, values are supported genres in the tool
    genreMap.put("Adventure, Role-Playing (RPG)", "adventure");
    genreMap.put("Racing / driving", "driving");
    genreMap.put("Puzzle, Strategy / tactics", "puzzle");
    genreMap.put("Educational", "programming");
    genreMap.put("Simulation", "simulation");
    genreMap.put("Sports", "sport");
  }

  @Override
  public void connect(String url) throws IOException
  {
    this.mobyGamesGameUrl = "";
    Jsoup.connect(url).method(Connection.Method.GET).execute();
    this.mobyGamesGameUrl = url;
  }

  @Override
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
          //Some may have a specific day, e.g. Mar 17, 2020
          if (m.find())
          {
            scrapedYear = Integer.parseInt(m.group());
          }
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
      if (fields.isComposer())
      {
        scrapedComposer = scrapeComposer(doc);
      }
      if (fields.isCover())
      {
        scrapedCover = scrapeCover(doc);
      }
      
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape title");
    }
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
    return scrapedDescription;
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
    return scrapedCover;
  }

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
  
  public String scrapeComposer(Document doc)
  {
    String value = "";
    //Look for div with music in sidebar
    String cssQuery = ".sideBarContent";
    Elements queryElements = doc.select(cssQuery);
    Element first = queryElements.first();
    if (first != null)
    {
      boolean musicFound = false;
      for (Node node : first.childNodes())
      {
        if (node instanceof TextNode)
        {
          String test = ((TextNode)node).text();
          //TODO: Add more possible labels
          if (test.contains("Music") || test.contains("music"))
          {
            musicFound = true;
          }
        }
        else if (node instanceof Element && musicFound)
        {
          value = ((Element)node).text();
          if (!value.isEmpty())
          {
            break;
          }
        }
      }
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

  @Override
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
      //Scrape max 6 screens
      for (int i = 0; i < Math.min(6, coverElements.size()); i++)
      {
        String bigScreenUrl = coverElements.get(i).attr("href");
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
  
  @Override
  public boolean isC64()
  {
    return mobyGamesGameUrl.contains("c64");
  }
}
