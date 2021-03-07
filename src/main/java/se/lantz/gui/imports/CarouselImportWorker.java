package se.lantz.gui.imports;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.manager.ImportManager;
import se.lantz.util.ExceptionHandler;

public class CarouselImportWorker extends SwingWorker<Void, String>
{

  private ImportManager importManager;
  private ImportProgressDialog dialog;

  public CarouselImportWorker(ImportManager importManager, ImportProgressDialog dialog)
  {
    this.importManager = importManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    publish("Reading game info files...");
    StringBuilder infoBuilder = new StringBuilder();
    importManager.readGameInfoFiles(infoBuilder);
    publish(infoBuilder.toString());
    infoBuilder = new StringBuilder();
    importManager.convertIntoDbRows(infoBuilder);
    publish(infoBuilder.toString());
    publish("Importing to db...");
    for (List<String> rowList : importManager.getDbRowReadChunks())
    {
      //Copy the list to avoid modifying it when reading several chunks
      ArrayList<String> copyList = new ArrayList<>();
      copyList.addAll(rowList);
      publish(importManager.insertRowsIntoDb(copyList).toString());
      publish("Copying screenshots, covers and game files...");
      publish(importManager.copyFiles(false, copyList).toString());
    }
    int numberOfGamesProcessed = importManager.clearAfterImport();
    publish("Processed " + numberOfGamesProcessed + " games.");
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
    importManager.clearAfterImport();
  }
}
