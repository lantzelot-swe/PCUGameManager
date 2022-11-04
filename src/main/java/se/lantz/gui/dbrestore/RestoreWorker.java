package se.lantz.gui.dbrestore;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.manager.RestoreManager;
import se.lantz.util.ExceptionHandler;

public class RestoreWorker extends SwingWorker<Void, String>
{

  private RestoreManager restoreManager;
  private RestoreProgressDialog dialog;

  public RestoreWorker(RestoreManager backupManager, RestoreProgressDialog dialog)
  {
    this.restoreManager = backupManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    StringBuilder infoBuilder = new StringBuilder();
    infoBuilder.append("Restoring database from folder backup/");
    infoBuilder.append(restoreManager.getBackupFolderName());
    infoBuilder.append("...");
    publish(infoBuilder.toString());
    restoreManager.restoreDb();
    publish("Done");
    publish("Restoring covers directory...");
    restoreManager.restoreCovers();
    publish("Done");
    publish("Restoring screens directory...");
    restoreManager.restoreScreens();
    publish("Done");
    publish("Restoring games directory...");
    restoreManager.restoreGames();
    publish("Done");
    publish("Restoring extradisks directory...");
    restoreManager.restoreExtraDisks();
    publish("Done");
    publish("Restoring saves directory...");
    restoreManager.restoreSaves();
    publish("Done");
    return null;
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
			get();
		} 
  	catch (Exception e)
		{
			ExceptionHandler.handleException(e, "Error during db restore");
		}
    dialog.finish();
  }
}
