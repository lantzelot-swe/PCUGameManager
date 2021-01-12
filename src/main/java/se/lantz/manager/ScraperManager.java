package se.lantz.manager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import se.lantz.model.InfoModel;
import se.lantz.model.MainViewModel;
import se.lantz.model.SystemModel;
import se.lantz.model.data.ScraperFields;
import se.lantz.scraper.C64comScraper;
import se.lantz.scraper.MobyGamesScraper;
import se.lantz.scraper.Scraper;

public class ScraperManager
{
  public enum SCRAPER {
    moby, c64com
  }
  Scraper mobyScraper = new MobyGamesScraper();
  Scraper c64comScraper = new C64comScraper();
  Scraper usedScraper = mobyScraper;

  List<BufferedImage> screenshotsList = new ArrayList<>();
  private InfoModel infoModel;
  private SystemModel systemModel;
  private ScraperFields fields;

  public ScraperManager(MainViewModel model)
  {
    this.infoModel = model.getInfoModel();
    this.systemModel = model.getSystemModel();
  }
  
  public void setScrapertoUse(SCRAPER scraper)
  {
    switch (scraper)
    {
    case moby:
      usedScraper = mobyScraper;
      break;
    case c64com:
      usedScraper = c64comScraper;
      break;
    default:
      break;
    }
  }

  public void connectScraper(String url) throws Exception
  {
    usedScraper.connect(url);
  }

  public void scrapeGameInformation(ScraperFields fields)
  {
    this.fields = fields;
    usedScraper.scrapeInformation(fields);
  }

  public void scrapeScreenshots()
  {
    screenshotsList = usedScraper.scrapeScreenshots();
  }

  public List<BufferedImage> getScreenshots()
  {
    return screenshotsList;
  }

  public void updateModelWithGamesInfo()
  {
    if (fields.isTitle())
    {
      infoModel.setTitle(usedScraper.getTitle());
    }
    if (fields.isAuthor())
    {
      infoModel.setAuthor(usedScraper.getAuthor());
    }
    if (fields.isYear())
    {
      infoModel.setYear(usedScraper.getYear());
    }
    if (fields.isDescription())
    {
      infoModel.setDescription(usedScraper.getDescription());
    }
    if (fields.isGenre())
    {
      String genre = usedScraper.getGenre();
      if (!genre.isEmpty())
      {
        infoModel.setGenre(genre);
      }
    }
    if (fields.isComposer())
    {
      infoModel.setComposer(usedScraper.getComposer());
    }
    if (fields.isCover())
    {
      infoModel.setCoverImage(usedScraper.getCover());
    }
    //Set system based on the scraped URL
    if (usedScraper.isC64())
    {
      systemModel.setC64(true);
    }
    else
    {
      systemModel.setVic(true);
    }
  }

  public void updateModelWithScreenshotImages(BufferedImage screen1, BufferedImage screen2)
  {
    infoModel.setScreen1Image(screen1);
    infoModel.setScreen2Image(screen2);
  }
}
