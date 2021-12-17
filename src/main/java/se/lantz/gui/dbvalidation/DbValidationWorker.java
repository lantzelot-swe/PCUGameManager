package se.lantz.gui.dbvalidation;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import se.lantz.db.DbConnector;
import se.lantz.util.FileManager;

public class DbValidationWorker extends SwingWorker<Integer, String>
{
  private DbValidationProgressDialog dialog;
  private DbConnector dbConnector;

  public DbValidationWorker(DbValidationProgressDialog dialog, DbConnector dbConnector)
  {
    this.dialog = dialog;
    this.dbConnector = dbConnector;
  }

  @Override
  protected Integer doInBackground() throws Exception
  {
    publish("Checking descriptions for CR characters...");
    List<String> fixedEntriesList = dbConnector.fixDescriptions();
    for (String game : fixedEntriesList)
    {
      publish("Fixed " + game);
    }
    publish("");
    publish("Checking screenshots for 32-bit color depth...");
    List<String> convertionList = FileManager.convertAllScreenshotsTo32Bit();
    for (String screenshot : convertionList)
    {
      publish(screenshot);
    }
    publish("");
    
    publish("Checking covers, screens and games directories for missing files...");
    List<String> fixedGamesList = FileManager.checkAllFilesForDbValidation(dbConnector.fetchAllGamesForDbValdation());
    for (String game : fixedGamesList)
    {
      publish("Fixed " + game);
    }
    
    //TODO: Look for invalid chars also (in all fields?) Is it UTF-8 that is supported? Look here: 
    //https://www.baeldung.com/java-string-encode-utf-8
    
    return convertionList.size() + fixedEntriesList.size() + fixedGamesList.size();
  }

  @Override
  protected void process(List<String> chunks)
  {
    for (String value : chunks)
    {
      dialog.updateProgress(value + "\n");
    }
  }

  @Override
  protected void done()
  {
    try
    {
      dialog.finish(this.get(), null);
    }
    catch (InterruptedException | ExecutionException e)
    {
      dialog.finish(0, e);
    }
  }
}
