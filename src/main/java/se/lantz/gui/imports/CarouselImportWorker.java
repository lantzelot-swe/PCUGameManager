package se.lantz.gui.imports;

import java.util.ArrayList;
import java.util.List;

import se.lantz.manager.ImportManager;

public class CarouselImportWorker extends AbstractImportWorker
{
  private ImportManager importManager;

  public CarouselImportWorker(ImportManager importManager, ImportProgressDialog dialog)
  {
    super(dialog);
    this.importManager = importManager;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    progressValueString = "Reading game info files...";
    publish(progressValueString);
    importManager.readGameInfoFiles(this);
    importManager.convertIntoDbRows(this);
    publish("\nImporting to db...");
    List<List<String>> dbRowReadChunks = importManager.getDbRowReadChunks();
    progressValueString = "Importing to db, copying covers, screens and game files...";
    progressMaximum = dbRowReadChunks.size();
    progressValue = 0;
    int chunkCount = 0;
    for (List<String> rowList : importManager.getDbRowReadChunks())
    {
      if (dialog.isCancelled())
      {
        progressValueString = "Cancelled";
        progressMaximum = 1;
        progressValue = 1;
        if (chunkCount == 0)
        {
          publish("\nImport cancelled, no games where added to the db.");
        }
        else
        {
          publish("\nImport cancelled, " + (chunkCount * ImportManager.DB_ROW_CHUNK_SIZE) +
            " games where added to the db.");
        }
        return null;
      }
      chunkCount++;
      progressValue++;
      //Copy the list to avoid modifying it when reading several chunks
      ArrayList<String> copyList = new ArrayList<>();
      copyList.addAll(rowList);
      publish(importManager.insertRowsIntoDb(copyList).toString());
      importManager.copyFiles(false, copyList, this);
    }
    int numberOfGamesProcessed = importManager.clearAfterImport();
    publish("Processed " + numberOfGamesProcessed + " games.");
    progressValueString = "Finished!";
    progressValue++;
    publish("Done!");
    return null;
  }

  @Override
  protected void done()
  {
    super.done();
    importManager.clearAfterImport();
  }
}
