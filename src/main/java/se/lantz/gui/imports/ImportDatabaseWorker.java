package se.lantz.gui.imports;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.gui.MainWindow;
import se.lantz.gui.exports.ImportExportProgressDialog;
import se.lantz.gui.exports.PublishWorker;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class ImportDatabaseWorker extends SwingWorker<Void, String> implements PublishWorker
{
  private ImportExportProgressDialog dialog;
  private Path source;
  private Path target;
  private String dbName;

  public ImportDatabaseWorker(ImportExportProgressDialog dialog, Path source, Path target, String dbName)
  {
    this.dialog = dialog;
    this.source = source;
    this.target = target;
    this.dbName = dbName;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    Files.createDirectories(target);
    publish("Creating database with name \"" + dbName + "\"");
    publish("Copying pcusb.db");
    Files.copy(source.resolve("pcusb.db"), target.resolve("pcusb.db"), StandardCopyOption.REPLACE_EXISTING);
    publish("Copying games folder");
    FileManager.copyDirectory(source.resolve("games").toString(), target.resolve("games").toString());
    publish("Copying screens folder");
    FileManager.copyDirectory(source.resolve("screens").toString(), target.resolve("screens").toString());
    publish("Copying covers folder");
    FileManager.copyDirectory(source.resolve("covers").toString(), target.resolve("covers").toString());
    publish("Copying saves folder");
    FileManager.copyDirectory(source.resolve("saves").toString(), target.resolve("saves").toString());
    publish("Copying extradisks folder");
    FileManager.copyDirectory(source.resolve("extradisks").toString(), target.resolve("extradisks").toString());
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
      MainWindow.getInstance().createNewDatabaseTab(dbName);
    }
    catch (Exception e)
    {
      ExceptionHandler.handleException(e, "Error during import");
    }
    dialog.finish();
  }

  @Override
  public void publishMessage(String message)
  {
    publish(message);
  }
}
