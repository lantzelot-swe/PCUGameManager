package se.lantz.gui.exports;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.manager.ExportManager;
import se.lantz.util.ExceptionHandler;

public class ExportFileLoaderWorker extends SwingWorker<Void, String>
{

  private ExportManager exportManager;
  private ExportProgressDialog dialog;

  public ExportFileLoaderWorker(ExportManager importManager, ExportProgressDialog dialog)
  {
    this.exportManager = importManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    if (exportManager.isDeleteBeforeExport())
    {
      publish("Deleting existing games before exporting.\n");
    }
    publish("Exporting from db...");
    StringBuilder infoBuilder = new StringBuilder();
    exportManager.readFromDb(infoBuilder);
    publish(infoBuilder.toString());
    publish("Creating cjm files...");
    infoBuilder = new StringBuilder();
    exportManager.createGameInfoFiles(infoBuilder, true);
    publish(infoBuilder.toString());
    publish("Copy game files...");
    infoBuilder = new StringBuilder();
    exportManager.copyGamesForFileLoader(infoBuilder);
    publish(infoBuilder.toString());
    exportManager.clearAfterImport();
    publish("Done!");
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
}
