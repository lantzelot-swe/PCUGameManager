package se.lantz.manager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.MainViewModel;
import se.lantz.util.FileManager;

public class BackupManager
{
  private static final Logger logger = LoggerFactory.getLogger(BackupManager.class);
  private MainViewModel uiModel;

  private String currentFolderName = "";

  public BackupManager(MainViewModel uiModel)
  {
    this.uiModel = uiModel;
  }

  public String setupTargetFolderName()
  {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    LocalDateTime now = LocalDateTime.now();
    //String for current date and time
    currentFolderName = dtf.format(now);

    currentFolderName = FileManager.getCurrentDbName() + "-" + currentFolderName;
    return currentFolderName;
  }

  public void backupDb()
  {
    FileManager.backupDb(currentFolderName);
  }

  public void backupCovers()
  {
    FileManager.backupCovers(currentFolderName);
  }

  public void backupScreens()
  {
    FileManager.backupScreens(currentFolderName);
  }

  public void backupGames()
  {
    FileManager.backupGames(currentFolderName);
  }

  public void backupExtraDisks()
  {
    FileManager.backupExtraDisks(currentFolderName);
  }

  public void backupSaves()
  {
    FileManager.backupSaves(currentFolderName);
  }
}
