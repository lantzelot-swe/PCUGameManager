package se.lantz.gui.exports;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.manager.ExportManager;
import se.lantz.manager.ImportManager;

public class ExportWorker extends SwingWorker<Void, String>
{

  private ExportManager exportManager;
  private ExportProgressDialog dialog;

  public ExportWorker(ExportManager importManager, ExportProgressDialog dialog)
  {
    this.exportManager = importManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    publish("Exporting from db...");
    StringBuilder infoBuilder = new StringBuilder();
    exportManager.readFromDb(infoBuilder);
    publish(infoBuilder.toString());
    publish("Creating game info files...");
    infoBuilder = new StringBuilder();
    exportManager.createGameInfoFiles(infoBuilder);
    publish(infoBuilder.toString());
//    publish(exportManager.insertRowsIntoDb().toString());
    publish("Copy screenshots, covers and game files...");
//    publish(exportManager.copyFiles().toString());
//    exportManager.clearAfterImport();
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
    dialog.finish();
  }
}
