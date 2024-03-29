package se.lantz.model;

import java.util.ArrayList;

import se.lantz.util.ExceptionHandler;

public class SystemModel extends AbstractModel
{
  private static final String C64 = "64";
  private static final String VIC = "vic";
  private static final String PAL = "pal";
  private static final String NTSC = "ntsc";
  private static final String DRIVEICON = "driveicon";
  private static final String ACCURATEDISK = "accuratedisk";
  private static final String READONLY = "readonly";
  private static final String FULLHEIGHT = "fullheight";
  private static final String SID6581 = "sid6581";
  private static final String SID8580 = "sid8580";
  private static final String SID8580D = "sid8580D";
  private static final String NOAUDIOSCALE = "noaudioscale";
  private static final String BANK0 = "bank0";
  private static final String BANK1 = "bank1";
  private static final String BANK2 = "bank2";
  private static final String BANK3 = "bank3";
  private static final String BANK5 = "bank5";
  private static final String BASIC = "basic";
  private static final String REU512K = "reu512";
  private static final String REU2MB = "reu2048";
  private static final String REU16MB = "reu16384";
  //TODO Support for 3K, 4K etc as aliases for Banks
 
  //TODO vertical shift
  
  private boolean c64 = false;
  private boolean vic = false;
  private boolean pal = false;
  private boolean ntsc = false;
  private boolean driveIcon = false;
  private boolean accurateDisk = false;
  private boolean readonly = false;
  private boolean fullHeight = false;
  private boolean sid6581 = false;
  private boolean sid8580 = false;
  private boolean sid8580D = false;
  private boolean noAudioScale = false;
  private boolean bank0 = false;
  private boolean bank1 = false;
  private boolean bank2 = false;
  private boolean bank3 = false;
  private boolean bank5 = false;
  private boolean basic = false;
  private boolean reu512K = false;
  private boolean reu2Mb = false;
  private boolean reu16Mb = false;
  private int verticalShift = 0;
  private String latestConfigString = "";
  
  public SystemModel()
  {
    //Empty
  }
 
  public String getConfigString()
  {
    ArrayList<String> list = new ArrayList<>();
    if (isC64())
    {
      list.add(C64);
    }
    if (isVic())
    {
      list.add(VIC);
    }
    if (isPal())
    {
      list.add(PAL);
    }
    if (isNtsc())
    {
      list.add(NTSC);
    }
    if (isDriveIcon())
    {
      list.add(DRIVEICON);
    }
    if (isAccurateDisk())
    {
      list.add(ACCURATEDISK);
    }
    if (isReadOnly())
    {
      list.add(READONLY);
    }
    if (isFullHeight())
    {
      list.add(FULLHEIGHT);
    }
    if (isC64())
    {
      if (isSid6581())
      {
        list.add(SID6581);
      }
      if (isSid8580())
      {
        list.add(SID8580);
      }
      if (isSid8580D())
      {
        list.add(SID8580D);
      }
      if (isNoAudioScale())
      {
        list.add(NOAUDIOSCALE);
      }
      if (isREU512K())
      {
        list.add(REU512K);
      }
      if (isREU2Mb())
      {
        list.add(REU2MB);
      }
      if (isREU16Mb())
      {
        list.add(REU16MB);
      }
    }
    else
    {
      if (isBank0())
      {
        list.add(BANK0);
      }
      if (isBank1())
      {
        list.add(BANK1);
      }
      if (isBank2())
      {
        list.add(BANK2);
      }
      if (isBank3())
      {
        list.add(BANK3);
      }
      if (isBank5())
      {
        list.add(BANK5);
      }
    }
    if (isBasic())
    {
      list.add(BASIC);
    }
    // Construct from config list
    StringBuilder builder = new StringBuilder();
    builder.append(String.join(",", list));

    return builder.toString();
  }

  public void setConfigStringFromDb(String configString)
  {
    disableChangeNotification(true);
    resetValues();
    if (configString == null || configString.isEmpty())
    {
      disableChangeNotification(false);
      return;
    }
    else
    {
      setConfigString(configString);
    }
    resetDataChanged();
    disableChangeNotification(false);
    notifyChange();
  }
  
  private void resetValues()
  {
    setC64(true);
    setPal(true);
    setVic(false);
    setAccurateDisk(false);
    setDriveIcon(false);
    setReadOnly(false);
    setNoAudioScale(false);
    setFullHeight(false);
    setSid6581(true);
    setBank0(false);
    setBank1(false);
    setBank2(false);
    setBank3(false);
    setBank5(false);
    setBasic(false);
    setREU512K(false);
    setREU2Mb(false);
    setREU16Mb(false);
  }

  public void setConfigString(String configString)
  {
    disableChangeNotification(true);
    resetValues();
    
    for (String flag : configString.split(","))
    { 
      if (flag.equals(C64))
      {
        setC64(true);
      }
      else if (flag.equals(VIC))
      {
        setVic(true);
      }
      else if (flag.equals(PAL))
      {
        setPal(true);
      }
      else if (flag.equals(NTSC))
      {
        setNtsc(true);
      }
      else if (flag.equals(DRIVEICON))
      {
        setDriveIcon(true);
      }
      else if (flag.equals(ACCURATEDISK))
      {
        setAccurateDisk(true);
      }
      else if (flag.equals(READONLY))
      {
        setReadOnly(true);
      }
      else if (flag.equals(FULLHEIGHT))
      {
        setFullHeight(true);
      }
      else if (flag.equals(SID6581))
      {
        setSid6581(true);
      }
      else if (flag.equals(SID8580))
      {
        setSid8580(true);
      }
      else if (flag.equals(SID8580D))
      {
        setSid8580D(true);
      }
      else if (flag.equals(NOAUDIOSCALE))
      {
        setNoAudioScale(true);
      }   
      else if (flag.equals(BANK0))
      {
        setBank0(true);
      }
      else if (flag.equals(BANK1))
      {
        setBank1(true);
      }
      else if (flag.equals(BANK2))
      {
        setBank2(true);
      }
      else if (flag.equals(BANK3))
      {
        setBank3(true);
      }
      else if (flag.equals(BANK5))
      {
        setBank5(true);
      }
      else if (flag.equals(REU512K))
      {
        setREU512K(true);
      }
      else if (flag.equals(REU2MB))
      {
        setREU2Mb(true);
      }
      else if (flag.equals(REU16MB))
      {
        setREU16Mb(true);
      }
      else if (flag.equals(BASIC))
      {
        setBasic(true);
      }
      else
      {
        //Ignore any invalid flags
        ExceptionHandler.logException(new IllegalArgumentException("Does not recognize " + flag), "");
      }
    }
    
    disableChangeNotification(false);
    //Call getConfigString to get everything in the right order
    if (!this.latestConfigString.equals(getConfigString()))
    {
      notifyChange();
    }
    this.latestConfigString = getConfigString();
  }

  public boolean isC64()
  {
    return c64;
  }

  public void setC64(boolean c64)
  {
    boolean old = isC64();
    this.c64 = c64;
    if (this.c64)
    {
      this.vic = false;
    }
    if ((Boolean.compare(old, c64) != 0))
    {
      //Notify with explicit values to be able to react on this in ScreenshotsPanel.
      notifyChange("c64", old, c64);
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
    if (this.vic)
    {
      this.c64 = false;
    }
    if ((Boolean.compare(old, vic) != 0))
    {
      //Notify with explicit values to be able to react on this in ScreenshotsPanel.
      notifyChange("c64", !old, !vic);
    }
  }

  public boolean isPal()
  {
    return pal;
  }

  public void setPal(boolean pal)
  {
    boolean old = isPal();
    this.pal = pal;
    if (this.pal)
    {
      this.ntsc = false;
    }
    if ((Boolean.compare(old, pal) != 0))
    {
      notifyChange();
    }
  }

  public boolean isNtsc()
  {
    return ntsc;
  }

  public void setNtsc(boolean ntsc)
  {    
    boolean old = isNtsc();
    this.ntsc = ntsc;
    if (this.ntsc)
    {
      this.pal = false;
    }
    if ((Boolean.compare(old, ntsc) != 0))
    {
      notifyChange();
    }
  }

  public boolean isDriveIcon()
  {
    return driveIcon;
  }

  public void setDriveIcon(boolean driveIcon)
  {
    boolean old = isDriveIcon();
    this.driveIcon = driveIcon;
    if ((Boolean.compare(old, driveIcon) != 0))
    {
      notifyChange();
    }
  }
  
  public boolean isAccurateDisk()
  {
    return accurateDisk;
  }

  public void setAccurateDisk(boolean trueDrive)
  {    
    boolean old = isAccurateDisk();
    this.accurateDisk = trueDrive;
    if ((Boolean.compare(old, trueDrive) != 0))
    {
      notifyChange();
    }
  }
  
  public boolean isReadOnly()
  {
    return readonly;
  }

  public void setReadOnly(boolean readOnly)
  {    
    boolean old = isReadOnly();
    this.readonly = readOnly;
    if ((Boolean.compare(old, readOnly) != 0))
    {
      notifyChange();
    }
  }

  public boolean isFullHeight()
  {
    return fullHeight;
  }

  public void setFullHeight(boolean fullHeight)
  {
    boolean old = isFullHeight();
    this.fullHeight = fullHeight;
    if ((Boolean.compare(old, fullHeight) != 0))
    {
      notifyChange();
    }
  }
  
  public boolean isNoAudioScale()
  {
    return noAudioScale;
  }

  public void setNoAudioScale(boolean noAudioScale)
  {
    boolean old = isNoAudioScale();
    this.noAudioScale = noAudioScale;
    if ((Boolean.compare(old, noAudioScale) != 0))
    {
      notifyChange();
    }
  }

  public boolean isSid6581()
  {
    return sid6581;
  }

  public void setSid6581(boolean sid6581)
  {
    boolean old = isSid6581();
    this.sid6581 = sid6581;
    if(this.sid6581)
    {
      this.sid8580 = false;
      this.sid8580D = false;
    }
    if ((Boolean.compare(old, sid6581) != 0))
    {
      notifyChange();
    }
  }

  public boolean isSid8580()
  {
    return sid8580;
  }

  public void setSid8580(boolean sid8580)
  {    
    boolean old = isSid8580();
    this.sid8580 = sid8580;
    if(this.sid8580)
    {
      this.sid6581 = false;
      this.sid8580D = false;
    }
    if ((Boolean.compare(old, sid8580) != 0))
    {
      notifyChange();
    }
  }

  public boolean isSid8580D()
  {
    return sid8580D;
  }

  public void setSid8580D(boolean sid8580d)
  {
    boolean old = isSid8580D();
    this.sid8580D = sid8580d;
    if(this.sid8580D)
    {
      this.sid6581 = false;
      this.sid8580 = false;
    }
    if ((Boolean.compare(old, sid8580d) != 0))
    {
      notifyChange();
    }
  }

  public boolean isBank0()
  {
    return bank0;
  }

  public void setBank0(boolean bank0)
  {  
    boolean old = isBank0();
    this.bank0 = bank0;
    if ((Boolean.compare(old, bank0) != 0))
    {
      notifyChange();
    }
  }

  public boolean isBank1()
  {
    return bank1;
  }

  public void setBank1(boolean bank1)
  {
    boolean old = isBank1();
    this.bank1 = bank1;
    if ((Boolean.compare(old, bank1) != 0))
    {
      notifyChange();
    }
  }

  public boolean isBank2()
  {
    return bank2;
  }

  public void setBank2(boolean bank2)
  {
    boolean old = isBank2();
    this.bank2 = bank2;
    if ((Boolean.compare(old, bank2) != 0))
    {
      notifyChange();
    }
  }

  public boolean isBank3()
  {
    return bank3;
  }

  public void setBank3(boolean bank3)
  {
    boolean old = isBank3();
    this.bank3 = bank3;
    if ((Boolean.compare(old, bank3) != 0))
    {
      notifyChange();
    }
  }

  public boolean isBank5()
  {
    return bank5;
  }

  public void setBank5(boolean bank5)
  {
    boolean old = isBank5();
    this.bank5 = bank5;
    if ((Boolean.compare(old, bank5) != 0))
    {
      notifyChange();
    }
  }
  
  public boolean isBasic()
  {
    return basic;
  }

  public void setBasic(boolean basic)
  {
    boolean old = isBasic();
    this.basic = basic;
    if ((Boolean.compare(old, basic) != 0))
    {
      notifyChange();
    }
  }
  
  public boolean isREU512K()
  {
    return reu512K;
  }

  public void setREU512K(boolean reu)
  {
    boolean old = isREU512K();
    this.reu512K = reu;
    if ((Boolean.compare(old, reu) != 0))
    {
      notifyChange();
    }
  }
  
  public boolean isREU2Mb()
  {
    return reu2Mb;
  }

  public void setREU2Mb(boolean reu)
  {
    boolean old = isREU2Mb();
    this.reu2Mb = reu;
    if ((Boolean.compare(old, reu) != 0))
    {
      notifyChange();
    }
  }
  
  public boolean isREU16Mb()
  {
    return reu16Mb;
  }

  public void setREU16Mb(boolean reu)
  {
    boolean old = isREU16Mb();
    this.reu16Mb = reu;
    if ((Boolean.compare(old, reu) != 0))
    {
      notifyChange();
    }
  }
  
  public int getVerticalShift()
  {
    return verticalShift;
  }
  
  public void setVerticalShift(int shift)
  {
    int old = getVerticalShift();
    this.verticalShift = shift;
    if (old != shift)
    {
      notifyChange();
    }
  }
}
