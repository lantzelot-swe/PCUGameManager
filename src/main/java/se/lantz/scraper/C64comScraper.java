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

import se.lantz.model.data.ScraperFields;
import se.lantz.util.ExceptionHandler;

public class C64comScraper implements Scraper
{
  private static final Logger logger = LoggerFactory.getLogger(C64comScraper.class);
  private String c64comGameUrl = "http://www.c64.com/games/53";
  
  private String screenshotCssQuery = "html > body > table > tbody > tr > td:eq(0) > table > tbody > tr:eq(1) > td > table:eq(1) > tbody > tr > td:eq(4) > table > tbody > tr:eq(0) > td > img";
 
  private String baseForTitleAndYear = "html > body > table > tbody > tr > td:eq(0) > table > tbody > tr:eq(1) > td > table:eq(1) > tbody > tr > td:eq(4) > table > tbody > tr:eq(0) > td > table > tbody > tr:eq(1) > td";
  private String titleCssQuery = baseForTitleAndYear + " > span";
  private String yearCssQuery = baseForTitleAndYear + " > a:eq(2) > span";
  private String authorCssQuery = baseForTitleAndYear + " > a:eq(3) > span";
  
  private String infoTableCssQuery = "html > body > table > tbody > tr > td:eq(0) > table > tbody > tr:eq(1) > td > table:eq(1) > tbody > tr > td:eq(4) > table > tbody > tr:eq(1) > td > table > tbody > tr > td > table:eq(2) > tbody";
  
  
  private String scrapedTitle;
  private int scrapedYear;
  private String scrapedAuthor;
  private List<String> scrapedMusicList = new ArrayList<>();
  
  private String scrapedGenre;
  private BufferedImage scrapedCover;
  
  public static void main(String[] args)
  {    
    C64comScraper scraper = new C64comScraper();   
    scraper.scrapeInformation(null);
    scraper.scrapeScreenshots();
  }
  public C64comScraper()
  {
    // TODO Auto-generated constructor stub
  }
  
  @Override
  public void connect(String url) throws IOException
  {
    this.c64comGameUrl = "";
    Jsoup.connect(url).method(Connection.Method.GET).execute();
    this.c64comGameUrl = url;
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
        scrapedYear = Integer.parseInt(yearElements.first().text().trim());
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
            continue;
          }
          if (info.equalsIgnoreCase("Genre:"))
          {
            scrapedGenre = child.select("td:eq(1)").first().text();
            logger.debug("scraped genre: {}", scrapedGenre);
            continue;
          }
          if (info.startsWith("Inlay"))
          {
            String url = child.select("td:eq(1) > a").first().attr("href");
            //Select the right part 
            url = url.substring(url.indexOf("'")+1);
            url = url.substring(0, url.indexOf("'"));
            url = url.substring(url.indexOf("=")+1);
            url = "http://www.c64.com/games/" + url;
            URL imageUrl = new URL(url);
            scrapedCover = ImageIO.read(imageUrl);
            logger.debug("Cover url: {}", url);
            
//            http://www.c64.com/games/inlay.php?url=inlays/a/arkanoid.png
          }
        }
      }     
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not scrape info");
    }
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
      logger.warn("Could not scrape all six screenshots", e);
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
  public boolean isC64()
  {
    // Only C64 games available
    return true;
  }
}