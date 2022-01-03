package se.lantz.gui.exports;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.manager.ExportManager;
import se.lantz.util.ExceptionHandler;

public class ExportFileLoaderWorker extends SwingWorker<Void, String> implements PublishWorker
{

  private ExportManager exportManager;
  private ImportExportProgressDialog dialog;

  public ExportFileLoaderWorker(ExportManager importManager, ImportExportProgressDialog dialog)
  {
    this.exportManager = importManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    if (exportManager.isDeleteBeforeExport())
    {
      publish("Deleting existing games before exporting...\n");
      exportManager.deleteBeforeExport(this);
    }
    exportManager.createDirectoriesBeforeExport();
    publish("\nExporting from db...");
    exportManager.readFromDb(this);
    publish("\nCreating cjm files...");
    exportManager.createGameInfoFiles(this, true);
    publish("\nCopying game files...");
    exportManager.copyGamesForFileLoader(this);
    exportManager.clearAfterImport();
    publish("\nDone!");
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
			ExceptionHandler.handleException(e, "Error during export");
		}
    dialog.finish();
  }

  @Override
  public void publishMessage(String message)
  {
    publish(message);
  }
}
