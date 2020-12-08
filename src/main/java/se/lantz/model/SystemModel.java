package se.lantz.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemModel extends AbstractModel
{
  private final static String C64 = "64";
  private final static String VIC = "vic";
  private final static String PAL = "pal";
  private final static String NTSC = "ntsc";
  private final static String DRIVEICON = "driveicon";
  private final static String TRUEDRIVE = "truedrive";
  private final static String FULLHEIGHT = "fullheight";
  private final static String SID6581 = "sid6581";
  private final static String SID8580 = "sid8580";
  private final static String SID8580D = "sid8580D";
  private final static String NOAUDIOSCALE = "noaudioscale";
  private final static String BANK0 = "bank0";
  private final static String BANK1 = "bank1";
  private final static String BANK2 = "bank2";
  private final static String BANK3 = "bank3";
  private final static String BANK5 = "bank5";
  
 
  //TODO vertical shift
  
  private boolean c64 = false;
  private boolean vic = false;
  private boolean pal = false;
  private boolean ntsc = false;
  private boolean driveIcon = false;
  private boolean fullHeight = false;
  private boolean sid6581 = false;
  private boolean sid8580 = false;
  private boolean sid8580D = false;
  private boolean bank0 = false;
  private boolean bank1 = false;
  private boolean bank2 = false;
  private boolean bank3 = false;
  private boolean bank5 = false;
  
  private List<String> configList = new ArrayList<>();
  
  private String configString = "";
  
  public SystemModel()
  {
    // TODO Auto-generated constructor stub
  }
 
  public String getConfigString()
  {
    // Construct from config list
    StringBuilder builder = new StringBuilder();
    builder.append(String.join(",", configList));

    return builder.toString();
  }

  public void setConfigStringFromDb(String configString)
  {
    if (configString == null || configString.isEmpty())
    {
      configList = new ArrayList<>();
    }
    else
    {
      configList = new ArrayList<>(Arrays.asList(configString.split(",")));
    }
    resetDataChanged();
    notifyChange();
  }

  public void setConfigString(String configString)
  {
    configList = new ArrayList<>(Arrays.asList(configString.split(",")));
    for (String flag : configList)
    { 
      if (flag.equals(C64))
      {
        setC64(true);
      }
      if (flag.equals(VIC))
      {
        setVic(true);
      }
      //etc...
    }
    
    //TODO
//    // Set all other fields based on configString
//    String[] colonSplit = configString.split(":");
//    if (colonSplit.length != 3)
//    {
//      throw new IllegalStateException("Invalid config string");
//    }
//    configList.clear();
//
//    configList = new ArrayList<>(Arrays.asList(colonSplit[2].split(",")));
//    while (configList.size() < 15)
//    {
//      configList.add("");
//    }
//    disableChangeNotification(true);
//    setUp(configList.get(0));
//    setDown(configList.get(1));
//    setLeft(configList.get(2));
//    setRight(configList.get(3));
//    setLeftFire(configList.get(4));
//    setRightFire(configList.get(5));
//    setTl(configList.get(6));
//    setTr(configList.get(7));
//    setUnused1(configList.get(8));
//    setA(configList.get(9));
//    setB(configList.get(10));
//    setC(configList.get(11));
//    setUnused2(configList.get(12));
//    setUnused3(configList.get(13));
//    setUnused4(configList.get(14));
//
//    setPrimary(colonSplit[1].contains("*"));
//    disableChangeNotification(false);
//    notifyChange();
  }

  public boolean isC64()
  {
    return c64;
  }

  public void setC64(boolean c64)
  {
    boolean old = isC64();
    this.c64 = c64;
    if (!(Boolean.compare(old, c64) == 0))
    {
      notifyChange();
    }
  }

  public boolean isVic()
  {
    return vic;
  }

  public void setVic(boolean vic)
  {
    boolean old = isVic();
    this.vic = vic;
    if (!(Boolean.compare(old, vic) == 0))
    {
      notifyChange();
    }
  }

  public boolean isPal()
  {
    return pal;
  }

  public void setPal(boolean pal)
  {
    this.pal = pal;
  }

  public boolean isNtsc()
  {
    return ntsc;
  }

  public void setNtsc(boolean ntsc)
  {
    this.ntsc = ntsc;
  }

  public boolean isDriveIcon()
  {
    return driveIcon;
  }

  public void setDriveIcon(boolean driveIcon)
  {
    this.driveIcon = driveIcon;
  }

  public boolean isFullHeight()
  {
    return fullHeight;
  }

  public void setFullHeight(boolean fullHeight)
  {
    this.fullHeight = fullHeight;
  }

  public boolean isSid6581()
  {
    return sid6581;
  }

  public void setSid6581(boolean sid6581)
  {
    this.sid6581 = sid6581;
  }

  public boolean isSid8580()
  {
    return sid8580;
  }

  public void setSid8580(boolean sid8580)
  {
    this.sid8580 = sid8580;
  }

  public boolean isSid8580D()
  {
    return sid8580D;
  }

  public void setSid8580D(boolean sid8580d)
  {
    sid8580D = sid8580d;
  }

  public boolean isBank0()
  {
    return bank0;
  }

  public void setBank0(boolean bank0)
  {
    this.bank0 = bank0;
  }

  public boolean isBank1()
  {
    return bank1;
  }

  public void setBank1(boolean bank1)
  {
    this.bank1 = bank1;
  }

  public boolean isBank2()
  {
    return bank2;
  }

  public void setBank2(boolean bank2)
  {
    this.bank2 = bank2;
  }

  public boolean isBank3()
  {
    return bank3;
  }

  public void setBank3(boolean bank3)
  {
    this.bank3 = bank3;
  }

  public boolean isBank5()
  {
    return bank5;
  }

  public void setBank5(boolean bank5)
  {
    this.bank5 = bank5;
  }

}
