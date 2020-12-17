package se.lantz.manager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.MainViewModel;
import se.lantz.util.FileManager;

public class RestoreManager
{
  private static final Logger logger = LoggerFactory.getLogger(RestoreManager.class);
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
}
