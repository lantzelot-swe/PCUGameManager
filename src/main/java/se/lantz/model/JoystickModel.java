package se.lantz.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JoystickModel extends AbstractModel
{
  public static final String DEFAULT_CONFIG = "JU,JD,JL,JR,JF,JF,SP,EN,,F1,F3,F5,,,";
  private final boolean port1;
  private boolean primary = false;

  private List<String> configList = new ArrayList<>();
  private ActionListener primaryListener;
  
  public JoystickModel(boolean port1)
  {
    this.port1 = port1;
    
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
    if (primary)
    {
      builder.append("*");
    }
    builder.append(":");
    builder.append(String.join(",", configList));

    return builder.toString();
  }

  public void setConfigStringFromDb(String configString)
  {
    if (configString == null || configString.isEmpty())
    {
      configList = new ArrayList<>();
      for (int i = 0; i < 15; i++)
      {
        configList.add("");
      }
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
      setPrimaryWithoutListenerNotification(colonSplit[1].contains("*"));
    }
    resetDataChanged();
    notifyChange();
  }

  public void setConfigString(String configString)
  {
    // Set all other fields based on configString
    String[] colonSplit = configString.split(":");
    if (colonSplit.length != 3)
    {
      throw new IllegalStateException("Invalid config string");
    }
    configList.clear();

    configList = new ArrayList<>(Arrays.asList(colonSplit[2].split(",")));
    while (configList.size() < 15)
    {
      configList.add("");
    }
    disableChangeNotification(true);
    setUp(configList.get(0));
    setDown(configList.get(1));
    setLeft(configList.get(2));
    setRight(configList.get(3));
    setLeftFire(configList.get(4));
    setRightFire(configList.get(5));
    setTl(configList.get(6));
    setTr(configList.get(7));
    setUnused1(configList.get(8));
    setA(configList.get(9));
    setB(configList.get(10));
    setC(configList.get(11));
    setUnused2(configList.get(12));
    setUnused3(configList.get(13));
    setUnused4(configList.get(14));

    setPrimary(colonSplit[1].contains("*"));
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
    if (!(Boolean.compare(old, primary) == 0))
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
    boolean old = isPrimary();
    this.primary = primary;
    if (!(Boolean.compare(old, primary) == 0))
    {
      notifyChange();
    }
  }

  void setPrimaryChangeListener(ActionListener e)
  {
    this.primaryListener = e;
  }
}
