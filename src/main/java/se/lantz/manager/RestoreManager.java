package se.lantz.manager;

import se.lantz.model.MainViewModel;
import se.lantz.util.FileManager;

public class RestoreManager
{
  private MainViewModel uiModel;

  private String currentFolderName = "";

  public RestoreManager(MainViewModel uiModel)
  {
    this.uiModel = uiModel;
  }

  public void setBackupFolderName(String folderName)
  {
    currentFolderName = folderName;
  }

  public String getBackupFolderName()
  {
    return currentFolderName;
  }

  public void restoreDb()
  {
    FileManager.restoreDb(currentFolderName);
    uiModel.getDbConnector().validateMissingColumnsAfterRestore();
  }

  public void restoreCovers()
  {
    FileManager.restoreCovers(currentFolderName);
  }

  public void restoreScreens()
  {
    FileManager.restoreScreens(currentFolderName);
  }

  public void restoreGames()
  {
    FileManager.restoreGames(currentFolderName);
  }
  
  public void restoreExtraDisks()
  {
    FileManager.restoreExtraDisks(currentFolderName);
  }
  
  public void restoreSaves()
  {
    FileManager.restoreSaves(currentFolderName);
  }
}
