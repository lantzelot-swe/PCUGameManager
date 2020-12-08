package se.lantz.gui;

import javax.swing.JComboBox;

public class DisplayShiftComboBox extends JComboBox<String>
{

  /**
   * 
   */
  private static final long serialVersionUID = 8793039092191107043L;

  public DisplayShiftComboBox()
  {
    setup64Items();
    setSelectedItem("0");
  }

  void setup64Items()
  {
    String selected = null;
    if (getSelectedItem() != null && !getSelectedItem().equals("-16"))
    {
      selected = getSelectedItem().toString();
    }
    this.removeAllItems();
    for (int i = -15; i < 18; i++)
    {
      this.addItem(Integer.toString(i));
    }
    if (selected != null)
    {
      setSelectedItem(selected);
    }
    else
    {
      setSelectedItem("0");
    }
  }
  
  void setupVic20Items()
  {
    String selected = null;
    if (getSelectedItem() != null && !getSelectedItem().equals("17"))
    {
      selected = getSelectedItem().toString();
    }
    this.removeAllItems();
    for (int i = -16; i < 17; i++)
    {
      this.addItem(Integer.toString(i));
    }
    if (selected != null)
    {
      setSelectedItem(selected);
    }
    else
    {
      setSelectedItem("0");
    }
  }
}
