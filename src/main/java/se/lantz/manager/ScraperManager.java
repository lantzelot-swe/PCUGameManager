package se.lantz.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.lantz.gui.MainWindow;
import se.lantz.model.InfoModel;
import se.lantz.model.MainViewModel;
import se.lantz.model.SystemModel;
import se.lantz.model.data.ScraperFields;
import se.lantz.scraper.C64comScraper;
import se.lantz.scraper.GamebaseScraper;
import se.lantz.scraper.MobyGamesScraper;
import se.lantz.scraper.Scraper;
import se.lantz.util.FileManager;

public class ScraperManager
{
  public enum SCRAPER {
    moby, c64com, gamebase
  }
  Scraper mobyScraper = new MobyGamesScraper();
  Scraper c64comScraper = new C64comScraper();
  Scraper gamebaseScraper = new GamebaseScraper();
  Scraper usedScraper = mobyScraper;

  List<BufferedImage> screenshotsList = new ArrayList<>();
  List<BufferedImage> coversList = new ArrayList<>();
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
    case gamebase:
      usedScraper = gamebaseScraper;
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
  
  public void scrapeCovers()
  {
    coversList = usedScraper.scrapeCovers();
  }

  public List<BufferedImage> getCovers()
  {
    return coversList;
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
      MainWindow.getInstance().getMainPanel().selectEnDescriptionTab();
    }
    if (fields.isGenre())
    {
      String genre = usedScraper.getGenre();
      if (!genre.isEmpty())
      {
        infoModel.setGenre(genre);
      }
      else
      {
        //Use first one as default
        infoModel.setGenre("adventure");
      }
    }
    if (fields.isComposer())
    {
      infoModel.setComposer(usedScraper.getComposer());
    }

    if (fields.isGame())
    {
      List<File> scrapedFiles = usedScraper.getGameFiles();
      if (scrapedFiles.size() > 0)
      {
        infoModel.setGamesPath(scrapedFiles.get(0));
      }
      if (scrapedFiles.size() > 1)
      {
        infoModel.setDisk2Path(scrapedFiles.get(1));
      }
      if (scrapedFiles.size() > 2)
      {
        infoModel.setDisk3Path(scrapedFiles.get(2));
      }
      if (scrapedFiles.size() > 3)
      {
        infoModel.setDisk4Path(scrapedFiles.get(3));
      }
      if (scrapedFiles.size() > 4)
      {
        infoModel.setDisk5Path(scrapedFiles.get(4));
      }
      if (scrapedFiles.size() > 5)
      {
        infoModel.setDisk6Path(scrapedFiles.get(5));
      }
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
    if (FileManager.isCropScreenshots())
    {
      infoModel.setScreen1Image(FileManager.cropImageTo320x200(screen1));
      if (screen2 != null)
      {
        infoModel.setScreen2Image(FileManager.cropImageTo320x200(screen2));
      }
    }
    else
    {
      infoModel.setScreen1Image(screen1);
      if (screen2 != null)
      {
        infoModel.setScreen2Image(screen2);
      }
    }
  }
  
  public void updateModelWithCoverImage(BufferedImage cover)
  {
    infoModel.setCoverImage(cover);
  }
}
