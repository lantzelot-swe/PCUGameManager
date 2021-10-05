package se.lantz.gui.checkdescriptions;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import se.lantz.db.DbConnector;
import se.lantz.util.FileManager;

public class CheckDescrWorker extends SwingWorker<Integer, String>
{
  private CheckDescrProgressDialog dialog;
  private DbConnector dbConnector;

  public CheckDescrWorker(CheckDescrProgressDialog dialog, DbConnector dbConnector)
  {
    this.dialog = dialog;
    this.dbConnector = dbConnector;
  }

  @Override
  protected Integer doInBackground() throws Exception
  {
    publish("Reading descriptions...");
    List<String> fixedEntriesList = dbConnector.fixDescriptions();
    for (String game : fixedEntriesList)
    {
      publish("Fixed " + game);
    }
    return fixedEntriesList.size();
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
