package se.lantz.scraper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.data.ScraperFields;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class C64comScraper implements Scraper
{
  private static final String FRAME_NAME_TEXT = "frame[name=text]";
  private static final Logger logger = LoggerFactory.getLogger(C64comScraper.class);
  private String c64comGameUrl = "";
 
  private String baseForTitleAndYear = "html > body > table > tbody > tr > td:eq(0) > table > tbody > tr:eq(1) > td > table:eq(1) > tbody > tr > td:eq(4) > table > tbody > tr:eq(0) > td > table > tbody > tr:eq(1) > td";
  private String titleCssQuery = baseForTitleAndYear + " > span";
  private String yearCssQuery = baseForTitleAndYear + " > a:eq(2) > span";
  private String authorCssQuery = baseForTitleAndYear + " > a:eq(3) > span";
  private String infoTableCssQuery = "html > body > table > tbody > tr > td:eq(0) > table > tbody > tr:eq(1) > td > table:eq(1) > tbody > tr > td:eq(4) > table > tbody > tr:eq(1) > td > table > tbody > tr > td > table:eq(2) > tbody";
  private String screenshotCssQuery = "html > body > table > tbody > tr > td:eq(0) > table > tbody > tr:eq(1) > td > table:eq(1) > tbody > tr > td:eq(4) > table > tbody > tr:eq(0) > td > img";
 
  private String scrapedTitle;
  private int scrapedYear = 1985;
  private String scrapedAuthor;
  private List<String> scrapedMusicList = new ArrayList<>();
  private String scrapedGenre;
  private BufferedImage scrapedCover;
  private File scrapedFile;

  Map<String, String> genreMap = new HashMap<>();
  
  public C64comScraper()
  {
    //Keys are Genres defined on c64.com, values are supported genres in the tool
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
    this.c64comGameUrl = "";
    //c64.com gives no errors for invalid urls. Check if there is an non-empty title to make sure it's valid
    Connection.Response result = Jsoup.connect(url).method(Connection.Method.GET).execute();
    Document doc = result.parse();     
    //Fetch right frame 
    Document mainFrameDocument = Jsoup.connect(doc.select(FRAME_NAME_TEXT).first().absUrl("src")).get();
    //Fetch title
    Elements queryElements = mainFrameDocument.select(titleCssQuery);
    Element first = queryElements.first();
    if (first == null || first.text().isEmpty())
    { 
      throw new IllegalArgumentException();     
    }
    
    this.c64comGameUrl = url;
    resetFields();
  }
  
  private void resetFields()
  {
    scrapedTitle = "";
    scrapedYear = 1985;
    scrapedAuthor = "";
    scrapedMusicList.clear();
    scrapedCover = null;
    scrapedGenre = "";
  }
  
  @Override
  public void scrapeInformation(ScraperFields fields)
  {
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(c64comGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();     
      //Fetch right frame 
      Document mainFrameDocument = Jsoup.connect(doc.select(FRAME_NAME_TEXT).first().absUrl("src")).get();

      if (fields.isTitle())
      {
        scrapeTitle(mainFrameDocument);
      }
      
      if (fields.isYear())
      {
        scrapeYear(mainFrameDocument);
      }
      
      if (fields.isAuthor())
      {
        scrapeAuthor(mainFrameDocument);
      }
      
      //Fetch infotable and find music, genre, cover and game
      Elements infoElements = mainFrameDocument.select(infoTableCssQuery);
      if (infoElements.first() != null)
      {
        //Loop over children and find music and genre
        Elements children = infoElements.first().children();
        for (int i = 0; i < children.size(); i++)
        {
          Element child = children.get(i);
          String info = child.select("td:eq(0)").first().text();
          if (fields.isComposer() && info.equalsIgnoreCase("Music:"))
          {
            String music = child.select("td:eq(1)").first().text();
            scrapedMusicList.add(music);
            logger.debug("scraped music: {}", music);
            continue;
          }
          if (fields.isGenre() && info.equalsIgnoreCase("Genre:"))
          {
            scrapedGenre = mapGenre(child.select("td:eq(1)").first().text());
            logger.debug("scraped genre: {}", scrapedGenre);
            continue;
          }
          if (fields.isCover() && info.startsWith("Inlay"))
          {
            scrapeCover(child);
            continue;
          }
          if (fields.isGame() && info.equalsIgnoreCase("Download:"))
          {
            scrapeGame(child);
          }
        }
      }     
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape information");
    }
  }
  
  private void scrapeTitle(Document mainFrameDocument)
  {
    //Fetch title
    Elements queryElements = mainFrameDocument.select(titleCssQuery);
    logger.debug("queryElements = {}", queryElements);
    Element first = queryElements.first();
    if (first != null)
    {
      scrapedTitle = first.text();
    }
    logger.debug("scraped title: {}", scrapedTitle);
  }
  
  private void scrapeYear(Document mainFrameDocument)
  {
    //Fetch year
    Elements yearElements = mainFrameDocument.select(yearCssQuery);
    if (yearElements.first() != null)
    {
      try
      {
        scrapedYear = Integer.parseInt(yearElements.first().text().trim());
      }
      catch (Exception e)
      {
        logger.error("Could not scrape year for {}",  scrapedTitle);
      }
      
    }
    logger.debug("scraped year: {}", scrapedYear);
  }
  
  private void scrapeAuthor(Document mainFrameDocument)
  {
    //Fetch author
    Elements authorElements = mainFrameDocument.select(authorCssQuery);
    if (authorElements.first() != null)
    {
      scrapedAuthor = authorElements.first().text();
    }
    logger.debug("scraped author: {}", scrapedAuthor);
  }
  
  private void scrapeCover(Element element)
  {
    String url = element.select("td:eq(1) > a").first().attr("href");
    //Select the right part 
    url = url.substring(url.indexOf("'")+1);
    url = url.substring(0, url.indexOf("'"));
    url = url.substring(url.indexOf("=")+1);
    url = "http://www.c64.com/games/" + url;
    URL imageUrl;
    try
    {
      imageUrl = new URL(url);
      scrapedCover = ImageIO.read(imageUrl);
    }
    catch (IOException e)
    {
      logger.error("Could not scrape cover for " + scrapedTitle , e);
    }
    logger.debug("Cover url: {}", url);
  }
  
  private void scrapeGame(Element element)
  {
    Element gameElement = element.select("td:eq(1) > a").first();
    if (gameElement.text().equalsIgnoreCase("Game"))
    {
      try
      {
        String url = gameElement.attr("abs:href");
        Response response = Jsoup.connect(url)
          .header("Accept-Encoding", "gzip, deflate")
          .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
          .ignoreContentType(true)
          .maxBodySize(0)
          .timeout(600000)
          .execute();
        
        //create a temp file and fetch the content
        scrapedFile = FileManager.createTempFileForScraper(response.bodyStream(), "ScrapedFile");
        logger.debug("File to include as game: {}", scrapedFile != null ? scrapedFile.getAbsolutePath() : null);
      }
      catch (Exception e)
      {
        logger.error("Could not scrape game file for " + scrapedTitle , e);
      }
    }
  }
  
  private String mapGenre(String genreFromC64com)
  {
    //Map towards available genres, return first one found
    for (Map.Entry<String, String> entry : genreMap.entrySet())
    {
      if (entry.getKey().contains(genreFromC64com))
      {
        return entry.getValue();
      }
    }   
    return "";
  }
  
  @Override
  public List<BufferedImage> scrapeScreenshots()
  {
    List<BufferedImage> screensList = new ArrayList<>();
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(c64comGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();     
      //Fetch right frame 
      Document mainFrameDocument = Jsoup.connect(doc.select(FRAME_NAME_TEXT).first().absUrl("src")).get();
      //Fetch the right element
      Elements coverElements = mainFrameDocument.select(screenshotCssQuery);
      if (coverElements.first() != null)
      {
        Element coverElement = coverElements.first();
        String absoluteUrl = coverElement.absUrl("src");
        //Try to fetch 6 screenshots based on number "01, 02, 03" etc
        for (int i = 1; i < 7; i++)
        {
          URL imageUrl = new URL(absoluteUrl);
          screensList.add(ImageIO.read(imageUrl));
          //Replace number in url
          absoluteUrl = absoluteUrl.replace("0" + Integer.toString(i) + ".", "0" + Integer.toString(i+1) + ".");
        }
      }
    }
    catch (IOException e)
    {
      logger.warn("Could not scrape all six screenshots");
    }
    return screensList;
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
}
