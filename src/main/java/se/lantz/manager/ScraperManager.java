package se.lantz.manager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import se.lantz.model.InfoModel;
import se.lantz.model.MainViewModel;
import se.lantz.model.SystemModel;
import se.lantz.model.data.ScraperFields;
import se.lantz.scraper.MobyGamesScraper;

public class ScraperManager
{
  MobyGamesScraper scraper = new MobyGamesScraper();
  List<BufferedImage> screenshotsList = new ArrayList<>();
  private InfoModel infoModel;
  private SystemModel systemModel;
  private ScraperFields fields;
  
  public ScraperManager(MainViewModel model)
  {
    this.infoModel = model.getInfoModel();
    this.systemModel = model.getSystemModel();
  }
  
  public void connectScraper(String url) throws Exception
  {
    scraper.connectToMobyGames(url);
  }
  
  public void scrapeGameInformation(ScraperFields fields)
  {
    this.fields = fields;
    scraper.scrapeInformation(fields);
  }
  
  public void scrapeScreenshots()
  {
    screenshotsList = scraper.scrapeScreenshots();
  }
  
  
  public List<BufferedImage> getScreenshots()
  {
    return screenshotsList;
  }
  
  public void updateModelWithGamesInfo()
  {
    if (fields.isTitle())
    {
      infoModel.setTitle(scraper.getTitle());
    }
    if (fields.isAuthor())
    {
      infoModel.setAuthor(scraper.getAuthor());
    }
    if (fields.isYear())
    {    
      infoModel.setYear(scraper.getYear());
    }
    if (fields.isDescription())
    {
      infoModel.setDescription(scraper.getDescription());
    }
    if (fields.isGenre())
    {
      String genre = scraper.getGenre();
      if (!genre.isEmpty())
      {
        infoModel.setGenre(genre);
      }
    }
    if (fields.isComposer())
    {
      infoModel.setComposer(scraper.getComposer());
    }
    if (fields.isCover())
    {
      infoModel.setCoverImage(scraper.getCover());
    }
    //Set system based on the scraped URL
    if (scraper.isC64())
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
