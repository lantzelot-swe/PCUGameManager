package se.lantz.gui.dbbackup;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.manager.BackupManager;

public class BackupWorker extends SwingWorker<Void, String>
{

  private BackupManager backupManager;
  private BackupProgressDialog dialog;

  public BackupWorker(BackupManager backupManager, BackupProgressDialog dialog)
  {
    this.backupManager = backupManager;
    this.dialog = dialog;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    String folderName = backupManager.setupTargetFolderName();
    StringBuilder infoBuilder = new StringBuilder();
    infoBuilder.append("Backing up database to folder ");
    infoBuilder.append(folderName);
    infoBuilder.append("...");
    publish(infoBuilder.toString());
    backupManager.backupDb();
    publish("Done");
    publish("Backing up covers directory...");
    backupManager.backupCovers();
    publish("Done");
    publish("Backing up screens directory...");
    backupManager.backupScreens();
    publish("Done");
    publish("Backing up games directory...");
    backupManager.backupGames();
    publish("Done");
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
