package se.lantz.gui.scraper;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.manager.ScraperManager;
import se.lantz.model.data.ScraperFields;

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
    if (fields.isScreenshots())
    {
      scraperManager.scrapeScreenshots();
    }
    return null;
  }

  @Override
  protected void process(List<String> chunks)
  {
    scraperManager.updateModelWithGamesInfo();  
    dialog.updateProgress();
  }

  @Override
  protected void done()
  {
    dialog.finish();
  }
}
