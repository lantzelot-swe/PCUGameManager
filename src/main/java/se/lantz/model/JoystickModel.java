package se.lantz.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JoystickModel extends AbstractModel
{
  public static final String DEFAULT_CONFIG = "JU,JD,JL,JR,JF,JF,SP,EN,,F1,F3,F5,,,";
  private final boolean port1;
  private boolean primary = false;
  private boolean mouse = false;

  private List<String> configList = new ArrayList<>();
  private ActionListener primaryListener;

  private Map<String, String> keyCodeMap = new HashMap<>();
  private List<String> keyCodeList = new ArrayList<>();
  private Object latestConfigString;

  public JoystickModel(boolean port1)
  {
    this.port1 = port1;
    setupKeyKodes();
    //Initialize with an empty list of right length
    for (int i = 0; i < 15; i++)
    {
      configList.add("");
    }
  }

  private void setupKeyKodes()
  {
    keyCodeList.add("F1");
    keyCodeList.add("F2");
    keyCodeList.add("F3");
    keyCodeList.add("F4");
    keyCodeList.add("F5");
    keyCodeList.add("F6");
    keyCodeList.add("F7");
    keyCodeList.add("F8");
    keyCodeList.add("A");
    keyCodeList.add("B");
    keyCodeList.add("C");
    keyCodeList.add("D");
    keyCodeList.add("E");
    keyCodeList.add("F");
    keyCodeList.add("G");
    keyCodeList.add("H");
    keyCodeList.add("I");
    keyCodeList.add("J");
    keyCodeList.add("K");
    keyCodeList.add("L");
    keyCodeList.add("M");
    keyCodeList.add("N");
    keyCodeList.add("O");
    keyCodeList.add("P");
    keyCodeList.add("Q");
    keyCodeList.add("R");
    keyCodeList.add("S");
    keyCodeList.add("T");
    keyCodeList.add("U");
    keyCodeList.add("V");
    keyCodeList.add("W");
    keyCodeList.add("X");
    keyCodeList.add("Y");
    keyCodeList.add("Z");
    keyCodeList.add("1");
    keyCodeList.add("2");
    keyCodeList.add("3");
    keyCodeList.add("4");
    keyCodeList.add("5");
    keyCodeList.add("6");
    keyCodeList.add("7");
    keyCodeList.add("8");
    keyCodeList.add("9");
    keyCodeList.add("0");

    // Codes not matching the text
    keyCodeMap.put("", "----");
    keyCodeMap.put("JU", "Up");
    keyCodeMap.put("JD", "Down");
    keyCodeMap.put("JL", "Left");
    keyCodeMap.put("JR", "Right");
    keyCodeMap.put("JF", "Fire");
    keyCodeMap.put("AL", "ARROW LEFT");
    keyCodeMap.put("AU", "ARROW UP");
    keyCodeMap.put("CM", "THEC64");
    keyCodeMap.put("CO", ", (Comma)");
    keyCodeMap.put("CT", "CTRL");
    keyCodeMap.put("CU", "Cursor Up");
    keyCodeMap.put("CD", "Cursor Down");
    keyCodeMap.put("CL", "Cursor Left");
    keyCodeMap.put("CR", "Cursor Right");
    keyCodeMap.put("DL", "INST/DEL");
    keyCodeMap.put("EN", "RETURN");
    keyCodeMap.put("HM", "CLR/HOME");
    keyCodeMap.put("RS", "RUN/STOP");
    keyCodeMap.put("RE", "RESTORE");
    keyCodeMap.put("SL", "Left SHIFT");
    keyCodeMap.put("SR", "Right SHIFT");
    keyCodeMap.put("SS", "SHIFT LOCK");
    keyCodeMap.put("SP", "SPACE");
    keyCodeMap.put("PO", "£ (Pound)");
  }

  public List<String> getKeyCodeList()
  {
    return keyCodeList;
  }

  public Map<String, String> getKeyCodeMap()
  {
    return keyCodeMap;
  }

  public String getConfigString()
  {
    // Construct from config list
    StringBuilder builder = new StringBuilder();
    if (port1)
    {
      builder.append("J:1");
    }
    else
    {
      builder.append("J:2");
    }
    if (mouse)
    {
      builder.append("M:");
    }
    else
    {
      if (primary)
      {
        builder.append("*");
      }
      builder.append(":");
      builder.append(String.join(",", configList));
    }
    return builder.toString();
  }

  public void setConfigStringFromDb(String configString)
  {
    this.latestConfigString = configString;
    if (configString == null || configString.isEmpty() || configString.endsWith("M:"))
    {
      configList = new ArrayList<>();
      for (int i = 0; i < 15; i++)
      {
        configList.add("");
      }     
      this.mouse = configString.endsWith("M:");
      this.primary = false;
    }
    else
    {
      String[] colonSplit = configString.split(":");
      if (colonSplit.length != 3)
      {
        throw new IllegalStateException("Invalid config string");
      }

      configList = new ArrayList<>(Arrays.asList(colonSplit[2].split(",")));
      while (configList.size() < 15)
      {
        configList.add("");
      }
      this.mouse = false;
      setPrimaryWithoutListenerNotification(colonSplit[1].contains("*"));
    }
    resetDataChanged();
    notifyChange();
  }

  public void setConfigString(String configString)
  {
    if (this.latestConfigString.equals(configString))
    {
      return;
    }
    disableChangeNotification(true);
    
    if (configString.endsWith("M:"))
    {
      //Mouse
      setMouse(true);
      setPrimary(false);
      setUp("");
      setDown("");
      setLeft("");
      setRight("");
      setLeftFire("");
      setRightFire("");
      setTl("");
      setTr("");
      setUnused1("");
      setA("");
      setB("");
      setC("");
      setUnused2("");
      setUnused3("");
      setUnused4("");
      this.latestConfigString = configString;
    }
    else
    {
      String definitions = configString;
      // Set all other fields based on configString
      String[] colonSplit = configString.split(":");
      if (colonSplit.length == 3)
      {
        definitions = colonSplit[2];
        setPrimary(colonSplit[1].contains("*"));
      }
  
      ArrayList<String> newConfigList = new ArrayList<>(Arrays.asList(definitions.split(",")));
      while (newConfigList.size() < 15)
      {
        newConfigList.add("");
      }
      //Validate all entries
      for (int i = 0; i < newConfigList.size(); i++)
      {
        String value = newConfigList.get(i);
        if (!keyCodeList.contains(value) && !keyCodeMap.keySet().contains(value))
        {
          newConfigList.set(i, "");
        }
      }
      setMouse(false);
      setUp(newConfigList.get(0));
      setDown(newConfigList.get(1));
      setLeft(newConfigList.get(2));
      setRight(newConfigList.get(3));
      setLeftFire(newConfigList.get(4));
      setRightFire(newConfigList.get(5));
      setTl(newConfigList.get(6));
      setTr(newConfigList.get(7));
      setUnused1(newConfigList.get(8));
      setA(newConfigList.get(9));
      setB(newConfigList.get(10));
      setC(newConfigList.get(11));
      setUnused2(newConfigList.get(12));
      setUnused3(newConfigList.get(13));
      setUnused4(newConfigList.get(14));
      this.latestConfigString = configString;
    }
    disableChangeNotification(false);
    notifyChange();
  }

  public String getUp()
  {
    return configList.get(0);
  }

  public void setUp(String up)
  {
    String old = getUp();
    configList.set(0, up);
    if (!Objects.equals(old, up))
    {
      notifyChange();
    }
  }

  public String getDown()
  {
    return configList.get(1);
  }

  public void setDown(String down)
  {
    String old = getDown();
    configList.set(1, down);
    if (!Objects.equals(old, down))
    {
      notifyChange();
    }
  }

  public String getLeft()
  {
    return configList.get(2);
  }

  public void setLeft(String left)
  {
    String old = getLeft();
    configList.set(2, left);
    if (!Objects.equals(old, left))
    {
      notifyChange();
    }
  }

  public String getRight()
  {
    return configList.get(3);
  }

  public void setRight(String right)
  {
    String old = getRight();
    configList.set(3, right);
    if (!Objects.equals(old, right))
    {
      notifyChange();
    }
  }

  public String getLeftFire()
  {
    return configList.get(4);
  }

  public void setLeftFire(String leftFire)
  {
    String old = getLeftFire();
    configList.set(4, leftFire);
    if (!Objects.equals(old, leftFire))
    {
      notifyChange();
    }
  }

  public String getRightFire()
  {
    return configList.get(5);
  }

  public void setRightFire(String rightFire)
  {
    String old = getRightFire();
    configList.set(5, rightFire);
    if (!Objects.equals(old, rightFire))
    {
      notifyChange();
    }
  }

  public String getTl()
  {
    return configList.get(6);
  }

  public void setTl(String tl)
  {
    String old = getTl();
    configList.set(6, tl);
    if (!Objects.equals(old, tl))
    {
      notifyChange();
    }
  }

  public String getTr()
  {
    return configList.get(7);
  }

  public void setTr(String tr)
  {
    String old = getTr();
    configList.set(7, tr);
    if (!Objects.equals(old, tr))
    {
      notifyChange();
    }
  }

  public String getUnused1()
  {
    return configList.get(8);
  }

  public void setUnused1(String unused1)
  {
    String old = getUnused1();
    configList.set(8, unused1);
    if (!Objects.equals(old, unused1))
    {
      notifyChange();
    }
  }

  public String getA()
  {
    return configList.get(9);
  }

  public void setA(String a)
  {
    String old = getA();
    configList.set(9, a);
    if (!Objects.equals(old, a))
    {
      notifyChange();
    }
  }

  public String getB()
  {
    return configList.get(10);
  }

  public void setB(String b)
  {
    String old = getB();
    configList.set(10, b);
    if (!Objects.equals(old, b))
    {
      notifyChange();
    }
  }

  public String getC()
  {
    return configList.get(11);
  }

  public void setC(String c)
  {
    String old = getC();
    configList.set(11, c);
    if (!Objects.equals(old, c))
    {
      notifyChange();
    }
  }

  public String getUnused2()
  {
    return configList.get(12);
  }

  public void setUnused2(String unused2)
  {
    String old = getUnused2();
    configList.set(12, unused2);
    if (!Objects.equals(old, unused2))
    {
      notifyChange();
    }
  }

  public String getUnused3()
  {
    return configList.get(13);
  }

  public void setUnused3(String unused3)
  {
    String old = getUnused3();
    configList.set(13, unused3);
    if (!Objects.equals(old, unused3))
    {
      notifyChange();
    }
  }

  public String getUnused4()
  {
    return configList.get(14);
  }

  public void setUnused4(String unused4)
  {
    String old = getUnused4();
    configList.set(14, unused4);
    if (!Objects.equals(old, unused4))
    {
      notifyChange();
    }
  }

  public boolean isPrimary()
  {
    return primary;
  }

  public void setPrimary(boolean primary)
  {
    boolean old = isPrimary();
    this.primary = primary;
    if ((Boolean.compare(old, primary) != 0))
    {
      notifyChange();
      if (primaryListener != null)
      {
        this.primaryListener.actionPerformed(new ActionEvent(this, 0, Boolean.toString(this.primary)));
      }
    }
  }

  void setPrimaryWithoutListenerNotification(boolean primary)
  {
    if (isMouse())
    {
      //Do nothing
      return;
    }
    boolean old = isPrimary();
    this.primary = primary;
    if ((Boolean.compare(old, primary) != 0))
    {
      notifyChange();
    }
  }

  void setPrimaryChangeListener(ActionListener e)
  {
    this.primaryListener = e;
  }
  
  public boolean isMouse()
  {
    return mouse;
  }
  
  public void setMouse(boolean mouse)
  {
    boolean old = isMouse();
    this.mouse = mouse;
    if ((Boolean.compare(old, mouse) != 0))
    {
      if (mouse)
      {
        setConfigString(getConfigString());
      }
      else
      {
        //Set default config string
        if (port1)
        {
          setConfigString("J:1:" + DEFAULT_CONFIG);
        }
        else
        {
          setConfigString("J:2*:" + DEFAULT_CONFIG);
        }
      }
      notifyChange();
    }
  }
}
