package se.lantz.gui.exports;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.manager.SavedStatesManager;
import se.lantz.util.ExceptionHandler;

public class ExportSavedStatesWorker extends SwingWorker<Void, String>
{

  private SavedStatesManager savedStatesManager;
  private ImportExportProgressDialog dialog;

  public ExportSavedStatesWorker(SavedStatesManager savedStatesManager, ImportExportProgressDialog dialog)
  {
    this.savedStatesManager = savedStatesManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    if (savedStatesManager.isExportOverwrite())
    {
      publish("Overwriting existing saved states in the export folder.\n");
    }
    else
    {
      publish("Skipping already existing saved states in the export folder.\n");
    }
    StringBuilder infoBuilder = new StringBuilder();
    savedStatesManager.exportSavedStates(infoBuilder);
    publish(infoBuilder.toString());
    publish("Copied " + savedStatesManager.getNumberOfFilesCopied() + " files.");
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
