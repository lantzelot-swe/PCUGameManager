package se.lantz.gui.exports;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.model.ExportManager;
import se.lantz.model.ImportManager;

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
    
    StringBuilder infoBuilder = new StringBuilder();
//    exportManager.readGameInfoFiles(infoBuilder);
    publish(infoBuilder.toString());
//    exportManager.convertIntoDbRows();
    publish("Exporting from db...");
    publish("Creating game info files...");
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
