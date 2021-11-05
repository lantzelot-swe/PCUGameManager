package se.lantz.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
    //TODO How to handle when the title (and game name) is changed? 
    
    String fileName = model.getInfoModel().getGamesFile();
    Path saveFolder = new File(SAVES + fileName).toPath();
    if (Files.exists(saveFolder))
    {
      //Check which ones are available
      Path mta0Path = saveFolder.resolve(MTA0);
      if (Files.exists(mta0Path))
      {
        storePlayTime(mta0Path, savedStatesModel.getState1time());
        //TODO: screen and vsz
      }
      Path mta1Path = saveFolder.resolve(MTA1);
      if (Files.exists(mta1Path))
      {
        storePlayTime(mta1Path, savedStatesModel.getState2time());
        //TODO: screen and vsz
      }
      Path mta2Path = saveFolder.resolve(MTA2);
      if (Files.exists(mta2Path))
      {
        storePlayTime(mta2Path, savedStatesModel.getState3time());
        //TODO: screen and vsz
      }
      Path mta3Path = saveFolder.resolve(MTA3);
      if (Files.exists(mta3Path))
      {
        storePlayTime(mta3Path, savedStatesModel.getState4time());
        //TODO: screen and vsz
      }
    }
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
          savedStatesModel.setState1time(readPlayTime(mta0Path));
        }
        Path mta1Path = saveFolder.resolve(MTA1);
        if (Files.exists(mta1Path))
        {
          //Update model
          savedStatesModel.setState2File(saveFolder.resolve(VSZ1).toFile().getName());
          savedStatesModel.setState2PngFile(saveFolder.resolve(PNG1).toFile().getName());
          savedStatesModel.setState2time(readPlayTime(mta1Path));
        }
        Path mta2Path = saveFolder.resolve(MTA2);
        if (Files.exists(mta2Path))
        {
          //Update model
          savedStatesModel.setState3File(saveFolder.resolve(VSZ2).toFile().getName());
          savedStatesModel.setState3PngFile(saveFolder.resolve(PNG2).toFile().getName());
          savedStatesModel.setState3time(readPlayTime(mta2Path));
        }
        Path mta3Path = saveFolder.resolve(MTA3);
        if (Files.exists(mta3Path))
        {
          //Update model
          savedStatesModel.setState4File(saveFolder.resolve(VSZ3).toFile().getName());
          savedStatesModel.setState4PngFile(saveFolder.resolve(PNG3).toFile().getName());
          savedStatesModel.setState4time(readPlayTime(mta3Path));
        }
      }
    }
  }

  private String readPlayTime(Path mtaFilePath)
  {
    String returnValue = "";
    try
    {
      byte[] fileContent = Files.readAllBytes(mtaFilePath);

      //First 4 bytes represents play time
      byte[] timeArray = new byte[] { fileContent[0], fileContent[1], fileContent[2], fileContent[3] };
      //The value seems to be in milliseconds
      int milliSeconds = ByteBuffer.wrap(timeArray).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
      System.out.println("24 bit value Little endian x= " + milliSeconds);
      returnValue = convertSecondToHHMMString(milliSeconds);// LocalTime.MIN.plusSeconds(seconds).toString(); 
      System.out.println("Converted string = " + returnValue);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not read play time from " + mtaFilePath);
    }
    return returnValue;
  }
  
  private void storePlayTime(Path mtaFilePath, String playTime)
  {
    try
    {
      String[] timeparts = playTime.split(":");
      int millis = (Integer.parseInt(timeparts[0])*3600 + Integer.parseInt(timeparts[1])*60 +  Integer.parseInt(timeparts[2]))*1000;
      byte[] fileContent = Files.readAllBytes(mtaFilePath);
      //Replace the first 4 bytes with the correct values
      ByteBuffer b = ByteBuffer.allocate(4);
      b.order(ByteOrder.LITTLE_ENDIAN);
      b.putInt(millis);
      byte[] result = b.array();
      fileContent[0] = result[0];
      fileContent[1] = result[1];
      fileContent[2] = result[2];
      fileContent[3] = result[3];
      
      Files.write(mtaFilePath, fileContent);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not write play time to " + mtaFilePath);
    }
  }
  
  private String convertSecondToHHMMString(int secondtTime)
  {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    df.setTimeZone(tz);
    String time = df.format(new Date(secondtTime));
    return time;
  }

}
