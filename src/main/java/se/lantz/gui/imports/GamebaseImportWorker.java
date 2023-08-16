package se.lantz.gui.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import se.lantz.gamebase.GamebaseImporter;
import se.lantz.gamebase.GbGameInfo;
import se.lantz.gamebase.GenreInfo;
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

    if (gbInporter.isImportAllWithViews())
    {
      int totalProcessed = 0;
      //Get all genres and create viewtags, then run one-by-one
      int counter = 0;
      List<GenreInfo> genreList = gbInporter.getAvailableGenres();
      int numberOfGenres = genreList.size();
      for (GenreInfo genre : genreList)
      {
        if (genre.getGenreName().contains("Collect"))
        {
          counter++;
          this.gbInporter.setGenreOption(genre);
          publish("Processing games for " + genre.getGenreName());
          String viewName = getViewName(genre);
          importManager.setViewTag(viewName);
          importManager.setViewName(viewName);
          String additonalInfo = ", genre: " + genre.getGenreName() + " (" + counter + " of " + numberOfGenres + ")";
          int processedForGenre = executeImport(additonalInfo);

          createAdditionalGameViews(processedForGenre, viewName);

          totalProcessed = totalProcessed + processedForGenre;
        }
      }
      publish("Processed " + totalProcessed + " games.");
      progressValueString = "Finished!";
      progressValue++;
      publish("Done!");
      return null;
    }
    else
    {
      int numberOfGamesProcessed = executeImport("");
      if (numberOfGamesProcessed > 0)
      {
        publish("Processed " + numberOfGamesProcessed + " games.");
        progressValueString = "Finished!";
        progressValue++;
        publish("Done!");
      }
      return null;
    }
  }

  private void createAdditionalGameViews(int processedGames, String viewName)
  {
    //Lets use 250 as limit for each view
    int numOfViews = processedGames / 250;
    if (processedGames % 250 > 0)
    {
      numOfViews++;
    }
    for (int i = 2; i < (numOfViews + 1); i++)
    {
      //Create additional views that can be filled later by editing view tags
      String name = viewName + "/" + i;
      importManager.setViewName(name);
      importManager.setViewTag(name);
      importManager.createGameViewForViewTag(this);
    }
  }

  private String getViewName(GenreInfo info)
  {
    String newName = info.getGenreName();
    newName = newName.replaceAll(" - ", "/");
    newName = newName.replace("[", "");
    newName = newName.replace("]", "");
    if (newName.startsWith("/"))
    {
      newName = newName.substring(1);
    }
    //Always use capital first letter
    newName = Pattern.compile("^.").matcher(newName).replaceFirst(m -> m.group().toUpperCase());
    return newName;
  }

  private int executeImport(String additionalInfo)
  {
    gbInporter.importFromGamebase(this);
    progressValueString = "Checking game files" + additionalInfo;

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
        return -1;
      }
      progressValue++;
      progressValueString =
        String.format("Checking game files (batch %s of %s)" + additionalInfo, progressValue, progressMaximum);
      gbInporter.checkGameFileForGbGames(gbInfoList, this);
    }

    List<List<String>> dbRowReadChunks = importManager.getDbRowReadChunks();
    progressValueString = "Importing to db and copying files" + additionalInfo;
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
        return -1;
      }
      chunkCount++;
      progressValue++;
      progressValueString = String
        .format("Importing to db and copying files (batch %s of %s)" + additionalInfo, progressValue, progressMaximum);
      //Copy the list to avoid modifying it when reading several chunks
      ArrayList<String> copyList = new ArrayList<>();
      copyList.addAll(rowList);
      publish(importManager.insertRowsIntoDb(copyList, 0).toString());
      importManager.copyFiles(true, copyList, this);
    }
    //Create game view if view tag is defined and processed games are not empty
    if (!dbRowReadChunks.isEmpty())
    {
      importManager.createGameViewForViewTag(this);
    }
    return importManager.clearAfterImport();
  }

  @Override
  protected void done()
  {
    super.done();
    importManager.clearAfterImport();
    gbInporter.clearAfterImport();
  }
}
