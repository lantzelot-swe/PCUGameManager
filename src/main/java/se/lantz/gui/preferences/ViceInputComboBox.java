package se.lantz.gui.preferences;

import java.beans.Beans;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.JComboBox;

public class ViceInputComboBox extends JComboBox<String>
{

  /**
   * 
   */
  private static final long serialVersionUID = 5027151055966823980L;

  Map<Integer, String> inputMap = new HashMap<>();

  public ViceInputComboBox()
  {
    super();
    if (!Beans.isDesignTime())
    {
      setupItemsAndCodes();
    }
  }

  private void setupItemsAndCodes()
  {
    this.addItem("None");
    this.addItem("Numpad + RCtrl");
    this.addItem("Keyset A");
    this.addItem("Keyset B");
    
    this.addItem("PC Joystick #0");
    this.addItem("PC Joystick #1");

    inputMap.put(0, "None");
    inputMap.put(1, "Numpad + RCtrl");
    inputMap.put(2, "Keyset A");
    inputMap.put(3, "Keyset B");
    inputMap.put(4, "PC Joystick #0");
    inputMap.put(5, "PC Joystick #1");
  }

  public void setSelectedInput(Integer value)
  {
    String item = inputMap.get(value);
    if (item != null)
    {
      this.setSelectedItem(item);
    }
    else
    {
      this.setSelectedItem("None");
    }
  }

  public Integer getSelectedInput()
  {
    for (Entry<Integer, String> entry : inputMap.entrySet())
    {
      if (Objects.equals(getSelectedItem(), entry.getValue()))
      {
        return entry.getKey();
      }
    }
    return 0;
  }
}
