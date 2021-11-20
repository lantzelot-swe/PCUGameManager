package se.lantz.model;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Objects;

import se.lantz.model.SavedStatesModel.SAVESTATE;

public class SavedStatesModel extends AbstractModel
{
  public static enum SAVESTATE {Save0, Save1, Save2, Save3};
  private String state1PngFile = "";
  private String state2PngFile = "";
  private String state3PngFile = "";
  private String state4PngFile = "";
  
  private BufferedImage state1PngImage;
  private BufferedImage state2PngImage;
  private BufferedImage state3PngImage;
  private BufferedImage state4PngImage;

  private Path state1Path;
  private Path state2Path;
  private Path state3Path;
  private Path state4Path;
  
  private String state1File;
  private String state2File;
  private String state3File;
  private String state4File;
  
  private String state1time = "00:00:00";
  private String state2time = "00:00:00";
  private String state3time = "00:00:00";
  private String state4time = "00:00:00";
  
  private boolean state1Deleted = false;
  private boolean state2Deleted = false;
  private boolean state3Deleted = false;
  private boolean state4Deleted = false;
  
  public String getState1PngFile()
  {
    return state1PngFile;
  }
  public void setState1PngFile(String state1PngFile)
  {
    String old = getState1PngFile();
    this.state1PngFile = state1PngFile;
    if (!Objects.equals(old, state1PngFile))
    {
      notifyChange();
    }
  }
  
  public String getState2PngFile()
  {
    return state2PngFile;
  }
  public void setState2PngFile(String state2PngFile)
  {
    String old = getState2PngFile();
    this.state2PngFile = state2PngFile;
    if (!Objects.equals(old, state2PngFile))
    {
      notifyChange();
    }
  }
  public String getState3PngFile()
  {
    return state3PngFile;
  }
  public void setState3PngFile(String state3PngFile)
  {
    String old = getState3PngFile();
    this.state3PngFile = state3PngFile;
    if (!Objects.equals(old, state3PngFile))
    {
      notifyChange();
    }
  }
  public String getState4PngFile()
  {
    return state4PngFile;
  }
  public void setState4PngFile(String state4PngFile)
  {
    String old = getState4PngFile();
    this.state4PngFile = state4PngFile;
    if (!Objects.equals(old, state4PngFile))
    {
      notifyChange();
    }
  }
  
  public BufferedImage getState1PngImage()
  {
    return state1PngImage;
  }
  public void setState1PngImage(BufferedImage state1PngImage)
  {
    BufferedImage old = getState1PngImage();
    this.state1PngImage = state1PngImage;
    if (!Objects.equals(old, state1PngImage))
    {
      notifyChange();
    }
  }
  public BufferedImage getState2PngImage()
  {
    return state2PngImage;
  }
  public void setState2PngImage(BufferedImage state2PngImage)
  {
    BufferedImage old = getState2PngImage();
    this.state2PngImage = state2PngImage;
    if (!Objects.equals(old, state2PngImage))
    {
      notifyChange();
    }
  }
  public BufferedImage getState3PngImage()
  {
    return state3PngImage;
  }
  public void setState3PngImage(BufferedImage state3PngImage)
  {
    BufferedImage old = getState3PngImage();
    this.state3PngImage = state3PngImage;
    if (!Objects.equals(old, state3PngImage))
    {
      notifyChange();
    }
  }
  public BufferedImage getState4PngImage()
  {
    return state4PngImage;
  }
  public void setState4PngImage(BufferedImage state4PngImage)
  {
    BufferedImage old = getState4PngImage();
    this.state4PngImage = state4PngImage;
    if (!Objects.equals(old, state4PngImage))
    {
      notifyChange();
    }
  }
  public Path getState1Path()
  {
    return state1Path;
  }
  public void setState1Path(Path state1Path)
  {
    Path old = getState1Path();
    this.state1Path = state1Path;
    if (!Objects.equals(old, state1Path))
    {
      notifyChange();
    }
  }
  public Path getState2Path()
  {
    return state2Path;
  }
  public void setState2Path(Path state2Path)
  {
    Path old = getState2Path();
    this.state2Path = state2Path;
    if (!Objects.equals(old, state2Path))
    {
      notifyChange();
    }
  }
  public Path getState3Path()
  {
    return state3Path;
  }
  public void setState3Path(Path state3Path)
  {
    Path old = getState3Path();
    this.state3Path = state3Path;
    if (!Objects.equals(old, state3Path))
    {
      notifyChange();
    }
  }
  public Path getState4Path()
  {
    return state4Path;
  }
  public void setState4Path(Path state4Path)
  {
    Path old = getState4Path();
    this.state4Path = state4Path;
    if (!Objects.equals(old, state4Path))
    {
      notifyChange();
    }
  }
  public String getState1time()
  {
    return state1time;
  }
  public void setState1time(String state1time)
  {
    String old = getState1time();
    this.state1time = validatePlayTime(state1time);
    if (!Objects.equals(old, state1time))
    {
      notifyChange();
    }
  }
  public String getState2time()
  {
    return state2time;
  }
  public void setState2time(String state2time)
  {
    String old = getState2time();
    this.state2time = validatePlayTime(state2time);
    if (!Objects.equals(old, state2time))
    {
      notifyChange();
    }
  }
  public String getState3time()
  {
    return state3time;
  }
  public void setState3time(String state3time)
  {
    String old = getState3time();
    this.state3time = validatePlayTime(state3time);
    if (!Objects.equals(old, state3time))
    {
      notifyChange();
    }
  }
  public String getState4time()
  {
    return state4time;
  }
  public void setState4time(String state4time)
  {
    String old = getState4time();
    this.state4time = validatePlayTime(state4time);
    if (!Objects.equals(old, state4time))
    {
      notifyChange();
    }
  }
  
  public String getState1File()
  {
    return state1File;
  }
  public void setState1File(String state1File)
  {
    String old = getState1File();
    this.state1File = state1File;
    if (!Objects.equals(old, state1File))
    {
      notifyChange();
    }
  }
  public String getState2File()
  {
    return state2File;
  }
  public void setState2File(String state2File)
  {
    String old = getState2File();
    this.state2File = state2File;
    if (!Objects.equals(old, state2File))
    {
      notifyChange();
    }
  }
  public String getState3File()
  {
    return state3File;
  }
  public void setState3File(String state3File)
  {
    String old = getState3File();
    this.state3File = state3File;
    if (!Objects.equals(old, state3File))
    {
      notifyChange();
    }
  }
  public String getState4File()
  {
    return state4File;
  }
  public void setState4File(String state4File)
  {
    String old = getState4File();
    this.state4File = state4File;
    if (!Objects.equals(old, state4File))
    {
      notifyChange();
    }
  }
  public boolean isState1Deleted()
  {
    return state1Deleted;
  }
  public void setState1Deleted(boolean state1Deleted)
  {
    this.state1Deleted = state1Deleted;
    state1PngImage = null;
    state1PngFile = "";
    state1Path = null;
    state1time = "00:00:00";
    state1File = "";
    notifyChange();
  }
  public boolean isState2Deleted()
  {
    return state2Deleted;
  }
  public void setState2Deleted(boolean state2Deleted)
  {
    this.state2Deleted = state2Deleted;
    state2PngImage = null;
    state2PngFile = "";
    state2Path = null;
    state2time = "00:00:00";
    state2File = "";
    notifyChange();
  }
  public boolean isState3Deleted()
  {
    return state3Deleted;
  }
  public void setState3Deleted(boolean state3Deleted)
  {
    this.state3Deleted = state3Deleted;
    state3PngImage = null;
    state3PngFile = "";
    state3Path = null;
    state3time = "00:00:00";
    state3File = "";
    notifyChange();
  }
  public boolean isState4Deleted()
  {
    return state4Deleted;
  }
  public void setState4Deleted(boolean state4Deleted)
  {
    this.state4Deleted = state4Deleted;
    state4PngImage = null;
    state4PngFile = "";
    state4Path = null;
    state4time = "00:00:00";
    state4File = "";
    notifyChange();
  }
  
  public void resetProperties()
  {
    state1PngFile = "";
    state2PngFile = "";
    state3PngFile = "";
    state4PngFile = "";
    state1PngImage = null;
    state2PngImage = null;
    state3PngImage = null;
    state4PngImage = null;
    state1Path = null;
    state2Path = null;
    state3Path = null;
    state4Path = null;
    state1time = "00:00:00";
    state2time = "00:00:00";
    state3time = "00:00:00";
    state4time = "00:00:00";
    state1File = "";
    state2File = "";
    state3File = "";
    state4File = "";
    state1Deleted = false;
    state2Deleted = false;
    state3Deleted = false;
    state4Deleted = false;
  }
  
  public void notifyChange()
  {
    super.notifyChange();
  }
  
  private String validatePlayTime(String playTime)
  {
    String[] timeparts = playTime.split(":");
    //Validate hours and minutes
    if (Integer.parseInt(timeparts[1]) > 59)
    {
      timeparts[1] = "00";
    }
    if (Integer.parseInt(timeparts[2]) > 59)
    {
      timeparts[2] = "00";
    }
    return timeparts[0] + ":" + timeparts[1] + ":" + timeparts[2];
  }  
}
