package se.lantz.gui.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import se.lantz.gamebase.GamebaseImporter;
import se.lantz.gamebase.GbGameInfo;
import se.lantz.manager.ImportManager;
import se.lantz.util.ExceptionHandler;

public class GamebaseImportWorker extends SwingWorker<Void, String>
{
  private ImportManager importManager;
  private ImportProgressDialog dialog;
  private final GamebaseImporter gbInporter;
  
  private volatile String progressValueString = "";
  private volatile int progressMaximum = 0;
  private volatile int progressValue = 0;

  public GamebaseImportWorker(GamebaseImporter gamebaseImporter, ImportManager importManager, ImportProgressDialog dialog)
  {
    this.importManager = importManager;
    this.dialog = dialog;
    this.gbInporter = gamebaseImporter;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    publish("Reading from gamebase db... this may take a while, be patient!");
    publish(gbInporter.importFromGamebase().toString());
    progressValueString = "Checking game files...";
    
    List<List<GbGameInfo>> listChunks = gbInporter.getGbGameInfoChunks();
    progressMaximum = listChunks.size();
    progressValue = 0;
    for (List<GbGameInfo> gbInfoList : listChunks)
    {
      if (dialog.isCancelled())
      {
        progressValueString = "Cancelled";
        progressMaximum = 1;
        progressValue = 1;
        publish("Import cancelled, no games added to the db.");
        return null;
      }
      progressValue++;
      publish(gbInporter.checkGameFileForGbGames(gbInfoList).toString());    
    }
    
    List<List<String>> dbRowReadChunks = importManager.getDbRowReadChunks();
    progressValueString = "Importing to db, copying covers, screens and game files...";
    progressMaximum = dbRowReadChunks.size() + 1;
    progressValue = 0;
    publish("Importing to db, copying covers, screens and game files...");
    for (List<String> rowList : dbRowReadChunks)
    {
      if (dialog.isCancelled())
      {
        progressValueString = "Cancelled";
        progressMaximum = 1;
        progressValue = 1;
        publish("Import cancelled, some games where added to the db.");
        return null;
      }
      progressValue++;
      //Copy the list to avoid modifying it when reading several chunks
      ArrayList<String> copyList = new ArrayList<>();
      copyList.addAll(rowList);
      publish(importManager.insertRowsIntoDb(copyList).toString());
      publish(importManager.copyFiles(true, copyList).toString());
    }
    int numberOfGamesProcessed = importManager.clearAfterImport();
    publish("Processed " + numberOfGamesProcessed + " games.");
    progressValueString = "Finished!";
    progressValue++;
    publish("Done!");
    return null;
  }

  @Override
  protected void process(List<String> chunks)
  {
    for (String value : chunks)
    {
      if (value.isEmpty())
      {
        dialog.updateProgress("");
      }
      else
      {
        dialog.updateProgress(value + "\n");
      }
      if (!progressValueString.isEmpty())
      {
        dialog.updateProgressBar(progressValueString, progressMaximum, progressValue);
      }
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
    gbInporter.clearAfterImport();
  }
}
