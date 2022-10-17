package se.lantz.gui.imports;

import java.util.ArrayList;
import java.util.List;

import se.lantz.gamebase.GamebaseImporter;
import se.lantz.gamebase.GbGameInfo;
import se.lantz.manager.ImportManager;

public class GamebaseImportWorker extends AbstractImportWorker
{
  private ImportManager importManager;
  private final GamebaseImporter gbInporter;

  public GamebaseImportWorker(GamebaseImporter gamebaseImporter,
                              ImportManager importManager,
                              ImportProgressDialog dialog)
  {
    super(dialog);
    this.importManager = importManager;
    this.gbInporter = gamebaseImporter;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    progressValueString = "Querying gamebase db...";
    publish("Reading from gamebase db... this may take a while, be patient!");

    gbInporter.importFromGamebase(this);
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
        publish("Import cancelled, no games where added to the db.");
        return null;
      }
      progressValue++;
      progressValueString = String.format("Checking game files (batch %s of %s)", progressValue, progressMaximum);
      gbInporter.checkGameFileForGbGames(gbInfoList, this);
    }

    List<List<String>> dbRowReadChunks = importManager.getDbRowReadChunks();
    progressValueString = "Importing to db and copying files...";
    progressMaximum = dbRowReadChunks.size();
    progressValue = 0;
    publish("\nImporting to db, copying covers, screens and game files...");
    int chunkCount = 0;
    for (List<String> rowList : dbRowReadChunks)
    {
      if (dialog.isCancelled())
      {
        progressValueString = "Cancelled";
        progressMaximum = 1;
        progressValue = 1;
        if (chunkCount == 0)
        {
          publish("Import cancelled, no games where added to the db.");
        }
        else
        {
          publish("Import cancelled, " + (chunkCount * ImportManager.DB_ROW_CHUNK_SIZE) +
            " games where added to the db.");
        }
        return null;
      }
      chunkCount++;
      progressValue++;
      progressValueString =
        String.format("Importing to db and copying files (batch %s of %s)", progressValue, progressMaximum);
      //Copy the list to avoid modifying it when reading several chunks
      ArrayList<String> copyList = new ArrayList<>();
      copyList.addAll(rowList);
      publish(importManager.insertRowsIntoDb(copyList, 0).toString());
      importManager.copyFiles(true, copyList, this);
    }
    //Create game view if view tag is defined
    importManager.createGameViewForViewTag(this);
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
    gbInporter.clearAfterImport();
  }
}
