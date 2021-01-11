package se.lantz.scraper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.util.ExceptionHandler;

public class C64comScraper
{
  private static final Logger logger = LoggerFactory.getLogger(C64comScraper.class);
  private String c64comGameUrl = "http://www.c64.com/games/259";
  
  private String screenshotCssQuery = "html > body > table > tbody > tr > td:eq(0) > table > tbody > tr:eq(1) > td > table:eq(1) > tbody > tr > td:eq(4) > table > tbody > tr:eq(0) > td > img";
 
  private String baseForTitleAndYear = "html > body > table > tbody > tr > td:eq(0) > table > tbody > tr:eq(1) > td > table:eq(1) > tbody > tr > td:eq(4) > table > tbody > tr:eq(0) > td > table > tbody > tr:eq(1) > td";
  private String titleCssQuery = baseForTitleAndYear + " > span";
  private String yearCssQuery = baseForTitleAndYear + " > a:eq(2) > span";
  private String authorCssQuery = baseForTitleAndYear + " > a:eq(3) > span";
  
  private String infoTableCssQuery = "html > body > table > tbody > tr > td:eq(0) > table > tbody > tr:eq(1) > td > table:eq(1) > tbody > tr > td:eq(4) > table > tbody > tr:eq(1) > td > table > tbody > tr > td > table:eq(2) > tbody";
  
  
  private String scrapedTitle;
  private String scrapedYear;
  private String scrapedAuthor;
  private List<String> scrapedMusicList = new ArrayList<>();
  
  private String scrapedGenre;
  
  public static void main(String[] args)
  {    
    C64comScraper scraper = new C64comScraper();   
    scraper.scrapeInformation();
    scraper.scrapeScreenshots();
  }
  public C64comScraper()
  {
    // TODO Auto-generated constructor stub
  }
  
  public void connectToC64Com(String url) throws IOException
  {
    this.c64comGameUrl = "";
    Jsoup.connect(url).method(Connection.Method.GET).execute();
    this.c64comGameUrl = url;
  }
  
  public void scrapeInformation()//ScraperFields fields)
  {
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(c64comGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();     
      //Fetch right frame 
      Document mainFrameDocument = Jsoup.connect(doc.select("frame[name=text]").first().absUrl("src")).get();

      //Fetch title
      Elements queryElements = mainFrameDocument.select(titleCssQuery);
      logger.debug("queryElements = " + queryElements);
      Element first = queryElements.first();
      if (first != null)
      {
        scrapedTitle = first.text();
      }
      logger.debug("scraped title: {}", scrapedTitle);
      
      //Fetch year
      Elements yearElements = mainFrameDocument.select(yearCssQuery);
      if (yearElements.first() != null)
      {
        scrapedYear = yearElements.first().text();
      }
      logger.debug("scraped year: {}", scrapedYear);
      
      //Fetch author
      Elements authorElements = mainFrameDocument.select(authorCssQuery);
      if (authorElements.first() != null)
      {
        scrapedAuthor = authorElements.first().text();
      }
      logger.debug("scraped author: {}", scrapedAuthor);
      
      
      //Fetch infotable and find misuc and genre
      Elements infoElements = mainFrameDocument.select(infoTableCssQuery);
      if (infoElements.first() != null)
      {
        //Loop over children and find music and genre
        Elements children = infoElements.first().children();
        for (int i = 0; i < children.size(); i++)
        {
          Element child = children.get(i);
          String info = child.select("td:eq(0)").first().text();
          if (info.equalsIgnoreCase("Music:"))
          {
            String music = child.select("td:eq(1)").first().text();
            scrapedMusicList.add(music);
            logger.debug("scraped music: {}", music);
          }
          else if (info.equalsIgnoreCase("Genre:"))
          {
            scrapedGenre = child.select("td:eq(1)").first().text();
            logger.debug("scraped genre: {}", scrapedGenre);
          }
        }
      }     
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape info");
    }
  }
  
  private List<BufferedImage> scrapeScreenshots()
  {
    List<BufferedImage> screensList = new ArrayList<>();
    Document doc;
    try
    {
      Connection.Response result = Jsoup.connect(c64comGameUrl).method(Connection.Method.GET).execute();
      doc = result.parse();     
      //Fetch right frame 
      Document mainFrameDocument = Jsoup.connect(doc.select("frame[name=text]").first().absUrl("src")).get();
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
          absoluteUrl = absoluteUrl.replace("0" + Integer.toString(i), "0" + Integer.toString(i+1));
        }
        
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape screenshot");
    }
    return screensList;
  }
}
