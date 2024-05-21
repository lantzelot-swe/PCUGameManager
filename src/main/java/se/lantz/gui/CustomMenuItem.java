package se.lantz.gui;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

public class CustomMenuItem extends JMenuItem
{

  public CustomMenuItem()
  {
    setMargin(new Insets(2, -20, 2, 2));
  }
  
  public CustomMenuItem(String text)
  {
    super(text);
    setMargin(new Insets(2, -20, 2, 2));
  }
}
