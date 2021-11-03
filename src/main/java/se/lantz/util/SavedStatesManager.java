package se.lantz.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import se.lantz.model.MainViewModel;
import se.lantz.model.SavedStatesModel;

public class SavedStatesManager
{
  public static final String SAVES = "./saves/";

  private static final String MTA0 = "0.mta";
  private static final String MTA1 = "1.mta";
  private static final String MTA2 = "2.mta";
  private static final String MTA3 = "3.mta";

  private static final String PNG0 = "0.png";
  private static final String PNG1 = "1.png";
  private static final String PNG2 = "2.png";
  private static final String PNG3 = "3.png";

  private static final String VSZ0 = "0.vsz";
  private static final String VSZ1 = "1.vsz";
  private static final String VSZ2 = "2.vsz";
  private static final String VSZ3 = "3.vsz";

  private SavedStatesModel savedStatesModel;
  private MainViewModel model;
  
  public SavedStatesManager(MainViewModel model)
  {
    this.model = model;
    this.savedStatesModel = model.getSavedStatesModel();
  }

  public void saveSavedStates()
  {
    //TODO Save saved states here if any changed

  }
  

  public void readSavedStates()
  {
    savedStatesModel.resetProperties();
    //Read from state directory, update model
    String fileName = model.getInfoModel().getGamesFile();
    System.out.println(fileName.toString());
    if (!fileName.isEmpty())
    {
      //Check if folder is available
      Path saveFolder = new File(SAVES + fileName).toPath();
      if (Files.exists(saveFolder))
      {
        //Check which ones are available
        Path mta0Path = saveFolder.resolve(MTA0);
        if (Files.exists(mta0Path))
        {
          //Update model
          savedStatesModel.setState1File(saveFolder.resolve(VSZ0).toFile().getName());
          savedStatesModel.setState1PngFile(saveFolder.resolve(PNG0).toFile().getName());
          //TODO: read time from mta first 3 bytes
        }
        Path mta1Path = saveFolder.resolve(MTA1);
        if (Files.exists(mta1Path))
        {
          //Update model
          savedStatesModel.setState2File(saveFolder.resolve(VSZ1).toFile().getName());
          savedStatesModel.setState2PngFile(saveFolder.resolve(PNG1).toFile().getName());
          //TODO: read time from mta first 3 bytes
        }
        Path mta2Path = saveFolder.resolve(MTA2);
        if (Files.exists(mta2Path))
        {
          //Update model
          savedStatesModel.setState3File(saveFolder.resolve(VSZ2).toFile().getName());
          savedStatesModel.setState3PngFile(saveFolder.resolve(PNG2).toFile().getName());
          //TODO: read time from mta first 3 bytes
        }
        Path mta3Path = saveFolder.resolve(MTA3);
        if (Files.exists(mta3Path))
        {
          //Update model
          savedStatesModel.setState4File(saveFolder.resolve(VSZ3).toFile().getName());
          savedStatesModel.setState4PngFile(saveFolder.resolve(PNG3).toFile().getName());
          //TODO: read time from mta first 3 bytes
        }
      }
    }
  }

}
