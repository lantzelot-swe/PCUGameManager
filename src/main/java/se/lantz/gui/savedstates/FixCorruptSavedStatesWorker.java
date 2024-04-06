package se.lantz.gui.savedstates;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.gui.exports.ImportExportProgressDialog;
import se.lantz.gui.exports.PublishWorker;
import se.lantz.manager.SavedStatesManager;
import se.lantz.util.ExceptionHandler;

public class FixCorruptSavedStatesWorker extends SwingWorker<Void, String> implements PublishWorker
{

  private SavedStatesManager savedStatesManager;
  private ImportExportProgressDialog dialog;

  public FixCorruptSavedStatesWorker(SavedStatesManager savedStatesManager, ImportExportProgressDialog dialog)
  {
    this.savedStatesManager = savedStatesManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    publish("Processing saved states...\n");
    savedStatesManager.fixCorruptSavedStates(this);
    publish("Processed " + savedStatesManager.getNumberOfFixedSavedStates() + " saved states.");
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
      ExceptionHandler.handleException(e, "Error during fixing corrupt saved states");
    }
    dialog.finish();
  }

  @Override
  public void publishMessage(String message)
  {
    publish(message);
  }
}
