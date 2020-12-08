package se.lantz.gui.imports;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.model.ImportManager;

public class ImportWorker extends SwingWorker<Void, String>
{

  private ImportManager importManager;
  private ImportProgressDialog dialog;

  public ImportWorker(ImportManager importManager, ImportProgressDialog dialog)
  {
    this.importManager = importManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    //TODO: These are the steps to perform:
    //-Read gameinfo files
    //-Convert into db rows
    //-Check which rows (games) are to be updated/added
    //-Update db
    //-Copy screenshots/covers/games for all that has been updated
    //-Cleanup
    
    publish("Reading game info files...");
    StringBuilder infoBuilder = new StringBuilder();
    importManager.readGameInfoFiles(infoBuilder);
    publish(infoBuilder.toString());
    
    importManager.convertIntoDbRows();
    publish("Importing to db...");
    publish(importManager.insertRowsIntoDb().toString());
    publish("Copy screenshots, covers and game files...");
    publish(importManager.copyFiles().toString());
    importManager.clearAfterImport();
    publish("Done!");
    return null;
  }

  @Override
  protected void process(List<String> chunks)
  {
    for (String value : chunks)
    {
      dialog.updateProgress(value, value + "\n");
    }
  }

  @Override
  protected void done()
  {
    dialog.finish();
  }
}
