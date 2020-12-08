package se.lantz.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.JComboBox;

public class KeySelectionComboBox extends JComboBox<String>
{

  /**
   * 
   */
  private static final long serialVersionUID = 5027151055966823980L;

  Map<String, String> codeMap = new HashMap<>();

  public KeySelectionComboBox()
  {
    super();
    setupItemsAndCodes();
  }

  private void setupItemsAndCodes()
  {
    this.addItem("----");
    this.addItem("Up");
    this.addItem("Down");
    this.addItem("Left");
    this.addItem("Right");
    this.addItem("Fire");
    this.addItem("F1");
    this.addItem("F2");
    this.addItem("F3");
    this.addItem("F4");
    this.addItem("F5");
    this.addItem("F6");
    this.addItem("F7");
    this.addItem("F8");
    this.addItem("A");
    this.addItem("B");
    this.addItem("C");
    this.addItem("D");
    this.addItem("E");
    this.addItem("F");
    this.addItem("G");
    this.addItem("H");
    this.addItem("I");
    this.addItem("J");
    this.addItem("K");
    this.addItem("L");
    this.addItem("M");
    this.addItem("N");
    this.addItem("O");
    this.addItem("P");
    this.addItem("Q");
    this.addItem("R");
    this.addItem("S");
    this.addItem("T");
    this.addItem("U");
    this.addItem("V");
    this.addItem("W");
    this.addItem("X");
    this.addItem("Y");
    this.addItem("Z");
    this.addItem("1");
    this.addItem("2");
    this.addItem("3");
    this.addItem("4");
    this.addItem("5");
    this.addItem("6");
    this.addItem("7");
    this.addItem("8");
    this.addItem("9");
    this.addItem("0");
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
    // Codes not matching the text
    codeMap.put("", "----");
    codeMap.put("JU", "Up");
    codeMap.put("JD", "Down");
    codeMap.put("JL", "Left");
    codeMap.put("JR", "Right");
    codeMap.put("JF", "Fire");
    codeMap.put("AL", "ARROW LEFT");
    codeMap.put("AU", "ARROW UP");
    codeMap.put("CM", "THEC64");
    codeMap.put("CO", ", (Comma)");
    codeMap.put("CT", "CTRL");
    codeMap.put("CU", "Cursor Up");
    codeMap.put("CD", "Cursor Down");
    codeMap.put("CL", "Cursor Left");
    codeMap.put("CR", "Cursor Right");
    codeMap.put("DL", "INST/DEL");
    codeMap.put("EN", "RETURN");
    codeMap.put("HM", "CLR/HOME");
    codeMap.put("RS", "RUN/STOP");
    codeMap.put("RE", "RESTORE");
    codeMap.put("SL", "Left SHIFT");
    codeMap.put("SR", "Right SHIFT");
    codeMap.put("SS", "SHIFT LOCK");
    codeMap.put("SP", "SPACE");
    codeMap.put("PO", "£ (Pound)");
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
