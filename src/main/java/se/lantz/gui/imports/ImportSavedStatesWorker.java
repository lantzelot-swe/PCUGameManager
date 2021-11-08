package se.lantz.gui.imports;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.gui.exports.ImportExportProgressDialog;
import se.lantz.manager.SavedStatesManager;
import se.lantz.util.ExceptionHandler;

public class ImportSavedStatesWorker extends SwingWorker<Void, String>
{

  private SavedStatesManager savedStatesManager;
  private ImportExportProgressDialog dialog;

  public ImportSavedStatesWorker(SavedStatesManager savedStatesManager, ImportExportProgressDialog dialog)
  {
    this.savedStatesManager = savedStatesManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    if (savedStatesManager.isImportOverwrite())
    {
      publish("Overwriting existing saved states in the saves folder.\n");
    }
    else
    {
      publish("Skipping already existing saved states in the saves folder.\n");
    }
    StringBuilder infoBuilder = new StringBuilder();
    savedStatesManager.importSavedStates(infoBuilder);
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
      ExceptionHandler.handleException(e, "Error during import");
    }
    dialog.finish();
  }
}
