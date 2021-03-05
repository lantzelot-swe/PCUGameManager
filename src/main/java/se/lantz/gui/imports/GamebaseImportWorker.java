package se.lantz.gui.imports;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import se.lantz.gamebase.GamebaseImporter;
import se.lantz.manager.ImportManager;
import se.lantz.util.ExceptionHandler;

public class GamebaseImportWorker extends SwingWorker<Void, String>
{
  private ImportManager importManager;
  private ImportProgressDialog dialog;
  private final GamebaseImporter gbInporter;

  public GamebaseImportWorker(GamebaseImporter gamebaseImporter, ImportManager importManager, ImportProgressDialog dialog)
  {
    this.importManager = importManager;
    this.dialog = dialog;
    this.gbInporter = gamebaseImporter;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    publish("Reading from gamebase db...");
    publish(gbInporter.importFromGamebase().toString());
    publish("Importing to db...");
    publish(importManager.insertRowsIntoDb().toString());
    publish("Copying screenshots, covers and game files...");
    publish(importManager.copyFiles(true).toString());
    importManager.clearAfterImport();
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
