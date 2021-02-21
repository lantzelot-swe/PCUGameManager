package se.lantz.gui;

import java.beans.Beans;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.JComboBox;

import se.lantz.model.JoystickModel;

public class KeySelectionComboBox extends JComboBox<String>
{

  /**
   * 
   */
  private static final long serialVersionUID = 5027151055966823980L;

  Map<String, String> codeMap = new HashMap<>();

  private JoystickModel model;

  public KeySelectionComboBox(JoystickModel model)
  {
    super();
    this.model = model;
    if (!Beans.isDesignTime())
    {
    	setupItemsAndCodes();
    }
  }

  private void setupItemsAndCodes()
  {
    this.addItem("----");
    this.addItem("Up");
    this.addItem("Down");
    this.addItem("Left");
    this.addItem("Right");
    this.addItem("Fire");
    for (String code : model.getKeyCodeList())
    {
      this.addItem(code);
    }
    this.addItem("ARROW LEFT");
    this.addItem("ARROW UP");
    this.addItem("THEC64");
    this.addItem(", (Comma)");
    this.addItem("CTRL");
    this.addItem("Cursor Up");
    this.addItem("Cursor Down");
    this.addItem("Cursor Left");
    this.addItem("Cursor Right");
    this.addItem("INST/DEL");
    this.addItem("RETURN");
    this.addItem("CLR/HOME");
    this.addItem("RUN/STOP");
    this.addItem("RESTORE");
    this.addItem("Left SHIFT");
    this.addItem("Right SHIFT");
    this.addItem("SHIFT LOCK");
    this.addItem("SPACE");
    this.addItem("£ (Pound)");
    
    codeMap = model.getKeyCodeMap();
  }

  public void setSelectedCode(String code)
  {
    String item = codeMap.get(code);
    if (item != null)
    {
      this.setSelectedItem(item);
    }
    else
    {
      this.setSelectedItem(code);
    }
  }

  public String getSelectedCode()
  {
    for (Entry<String, String> entry : codeMap.entrySet())
    {
      if (Objects.equals(getSelectedItem(), entry.getValue()))
      {
        return entry.getKey();
      }
    }
    return getSelectedItem().toString();
  }

}
