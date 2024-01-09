package se.lantz.scraper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.MainWindow;
import se.lantz.model.data.ScraperFields;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class MobyGamesScraper implements Scraper
{
  private static final Logger logger = LoggerFactory.getLogger(MobyGamesScraper.class);

  //"#main > div.flex.flex-space-between > div.mb > h1"
  private String titleCssQuery = "#main > div.flex.flex-space-between > div.mb > h1";

  private String mobyGamesGameUrl = "";
  private String descriptionCssQuery = "#description-text";
  private String authorCssQuery = "#publisherLinks > li:eq(0) > a";
  private String yearC64CssQuery = "#tcommodore-64";
  private String yearVic20Query = "#tvic-20";
  private String alternateYearQuery = "#infoBlock > div.info-release > dl > dd:eq(1) > a:eq(0)";
  private String genreCssQuery = "#infoBlock > div.info-genres > dl > dd:eq(1) > a";
  private String screensCssQuery = "#main";
  private String coversCssQuery = "#main";

  private String creditsC64TableQuery = "#credits-platform-27 > div > table";
  private String creditsVic20TableQuery = "#credits-platform-43 > div > table";

  Map<String, String> genreMap = new HashMap<>();
  private String scrapedTitle = "";
  private String scrapedAuthor = "";
  private int scrapedYear = 1985;
  private String scrapedDescription = "";
  private String scrapedGenre = "";
  private String scrapedComposer = "";
  private BufferedImage scrapedCover = null;

  private boolean isC64Game = true;

  private boolean missingGame = false;

  public MobyGamesScraper()
  {
    //Keys are Genres defined on MobyGames, values are supported genres in the tool
    genreMap.put("Adventure, Role-playing (RPG)", "adventure");
    genreMap.put("Racing / Driving", "driving");
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
    missingGame = false;
  }

  @Override
  public void scrapeInformation(ScraperFields fields)
  {
    this.isC64Game = fields.isC64();
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(mobyGamesGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();

      if (!checkGameAvailability())
      {
        this.missingGame = true;
        return;
      }

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
          scrapedAuthor = getTextFromElement(first);
        }
      }
      if (fields.isYear())
      {
        String yearQuery = isC64Game ? yearC64CssQuery : yearVic20Query;
        if (!scrapeYear(doc, yearQuery))
        {
          scrapeYear(doc, alternateYearQuery);
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
        else
        {
          //Default to adventure
          scrapedGenre = "adventure";
        }
      }
      if (fields.isComposer())
      {
        scrapedComposer = scrapeComposer();
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape information");
    }
  }

  private boolean scrapeYear(Document doc, String query)
  {
    Pattern p = Pattern.compile("\\d+");
    Elements queryElements = doc.select(query);
    Element first = queryElements.first();
    if (first != null)
    {
      Matcher m = p.matcher(getTextFromElement(first));
      if (m.find())
      {
        scrapedYear = Integer.parseInt(m.group());
        //Some may have a specific day, e.g. Mar 17, 2020
        if (m.find())
        {
          scrapedYear = Integer.parseInt(m.group());
        }
        return true;
      }
    }
    return false;
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
      StringBuilder builder = new StringBuilder();
      for (String section : descriptionDiv.first().getElementsByTag("p").eachText())
      {
        builder.append(section);
        builder.append(" ");
      }
      return builder.toString().trim();
    }
    return "";
  }

  private String getTextFromElement(Element e)
  {
    String text = "";
    if (e.hasAttr("href") || e.tagName().equals("i"))
    {
      if (!e.childNodes().isEmpty() && (e.childNode(0) instanceof TextNode))
      {
        text = ((TextNode) e.childNode(0)).text();
      }
    }
    return text;
  }

  public String scrapeGenre(Document doc)
  {
    Elements queryElements = doc.select(genreCssQuery);
    Element first = queryElements.first();
    if (first != null)
    {
      String genreFromMobyGames = getTextFromElement(first);
      String[] split = genreFromMobyGames.split(", ");
      for (int i = 0; i < split.length; i++)
      {
        //Map towards available genres, return first one found
        for (Map.Entry<String, String> entry : genreMap.entrySet())
        {
          if (entry.getKey().toLowerCase().contains(split[i].toLowerCase()))
          {
            return entry.getValue();
          }
        }
      }
    }
    return "";
  }

  public String scrapeComposer()
  {
    String value = "";

    String creditsTableQuery = isC64Game ? creditsC64TableQuery : creditsVic20TableQuery;

    String creditsPath = isC64Game ? "/credits/c64" : "/credits/vic-20";
    Document doc;
    try
    {
      Connection.Response result =
        Jsoup.connect(mobyGamesGameUrl + creditsPath).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements creditsElements = doc.select(creditsTableQuery);
      if (creditsElements.first() != null)
      {
        Element first = creditsElements.first();
        Elements creditTd = first.getElementsByTag("td");

        List<Element> musicElements = creditTd.stream()
          .filter(element -> element.text().toLowerCase().contains("music")).collect(Collectors.toList());
        if (musicElements.isEmpty())
        {
          musicElements = creditTd.stream().filter(element -> element.text().toLowerCase().contains("sound"))
            .collect(Collectors.toList());
        }
        if (!musicElements.isEmpty())
        {
          Element musicElement = musicElements.get(0);
          Element musicParent = musicElement.parent();
          if (musicParent.getElementsByTag("a").first() != null)
          {
            value = musicParent.getElementsByTag("a").first().text();
          }
          else
          {
            Element commaListElement = musicParent.getElementsByClass("commaList").first();
            final List<String> musicList = new ArrayList<>();
            commaListElement.getElementsByTag("li").forEach(liElement -> musicList.add(liElement.text()));
            value = String.join(",", musicList);
          }
        }
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape composer");
    }
    return value;
  }

  public boolean checkGameAvailability()
  {
    boolean available = false;

    String creditsPath = isC64Game ? "/credits/c64" : "/credits/vic-20";
    try
    {
      Connection.Response result =
        Jsoup.connect(mobyGamesGameUrl + creditsPath).method(Connection.Method.GET).execute();
      result.parse();
      available = true;
    }
    catch (org.jsoup.HttpStatusException ex)
    {
      JOptionPane.showMessageDialog(MainWindow
        .getInstance(), "No game available for " + (isC64Game ? "C64" : "VIC-20") + " with URL = " + mobyGamesGameUrl);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not check gamme availability.");
    }
    return available;
  }

  @Override
  public List<BufferedImage> scrapeScreenshots()
  {

    String cssQueryForBigScreenshot = "#main > div.text-center.mb > figure > img"; //*[@id="main"]/div/div[2]/div/div/img

    String screensPath = isC64Game ? "/screenshots/c64" : "/screenshots/vic-20";

    List<BufferedImage> returnList = new ArrayList<>();

    if (missingGame)
    {
      return returnList;
    }
    Document doc;
    try
    {
      Connection.Response result =
        Jsoup.connect(mobyGamesGameUrl + screensPath).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements coverElements = doc.select(screensCssQuery);
      Element first = coverElements.first();
      Elements imageTags = first.getElementsByTag("figure");

      List<Element> screenElements =
        imageTags.stream().map(el -> el.getElementsByTag("a").first()).collect(Collectors.toList());

      logger.debug("Number of screenshots found: {}", screenElements.size());
      //Scrape max 6 screens
      for (int i = 0; i < Math.min(6, screenElements.size()); i++)
      {
        String bigScreenUrl = screenElements.get(i).attr("href");
        logger.debug("Screen URL = " + bigScreenUrl);
        returnList.add(scrapeBigImage(bigScreenUrl, cssQueryForBigScreenshot));
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape cover");
    }
    return returnList;
  }

  @Override
  public List<BufferedImage> scrapeCovers()
  {
    String cssQueryForBigCover = "#main > div.text-center.mb > figure > img"; //*[@id="main"]/div/div[2]/center/img

    String coversPath = isC64Game ? "/covers/c64/" : "/covers/vic-20/";

    List<BufferedImage> returnList = new ArrayList<>();
    if (missingGame)
    {
      return returnList;
    }
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(mobyGamesGameUrl + coversPath).method(Connection.Method.GET).execute();
      doc = result.parse();
      //Fetch the right element
      Elements coverElements = doc.select(coversCssQuery);
      Element first = coverElements.first();
      Elements imageTags = first.getElementsByTag("figure");

      logger.debug("Number of cover art found: {}", imageTags.size());

      List<Element> filteredElements = imageTags.stream().filter(element -> {
        Elements test = element.getElementsByTag("small");
        return test.first().text().contains("Front Cover");
      }).map(el -> el.getElementsByTag("a").first()).collect(Collectors.toList());

      //Scrape max 6 covers
      for (int i = 0; i < Math.min(6, filteredElements.size()); i++)
      {
        String bigScreenUrl = filteredElements.get(i).attr("href");
        logger.debug("Screen URL = " + bigScreenUrl);
        BufferedImage scrapedImage = FileManager.getScaledCoverImage(scrapeBigImage(bigScreenUrl, cssQueryForBigCover));
        returnList.add(scrapedImage);
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape cover");
    }
    return returnList;
  }

  private BufferedImage scrapeBigImage(String url, String cssQuery)
  {
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
    return isC64Game;
  }

  @Override
  public List<File> getGameFiles()
  {
    //No file to download
    return Collections.emptyList();
  }
}
