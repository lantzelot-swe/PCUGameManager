package se.lantz.gui.scraper;

import java.util.Arrays;
import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.manager.ScraperManager;
import se.lantz.model.data.ScraperFields;
import se.lantz.util.ExceptionHandler;

public class ScraperWorker extends SwingWorker<Void, String>
{

  private final ScraperManager scraperManager;
  private final ScraperFields fields;
  private ScraperProgressDialog dialog;

  public ScraperWorker(ScraperManager scraperManager, ScraperFields fields, ScraperProgressDialog dialog)
  {
    this.scraperManager = scraperManager;
    this.fields = fields;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    scraperManager.scrapeGameInformation(fields);
    publish("");
    if (fields.isCover())
    {
      publish("Fetching Covers...");
      scraperManager.scrapeCovers();
    }
    
    if (fields.isScreenshots())
    {
      publish("Fetching Screenshots...");
      scraperManager.scrapeScreenshots();
    }
    return null;
  }

  @Override
  protected void process(List<String> chunks)
  {
    if (chunks.get(0).isEmpty())
    {
      scraperManager.updateModelWithGamesInfo();
    }
    for (String chunk : chunks)
    {
      dialog.updateProgress(chunk);
    }
    
  }

  @Override
  protected void done()
  {
  	try
		{
			get();
		} 
  	catch (Exception e)
		{
			ExceptionHandler.handleException(e, "Error during scraping");
		}
    dialog.finish();
  }
}
