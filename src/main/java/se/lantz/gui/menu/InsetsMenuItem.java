package se.lantz.gui.menu;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

public class InsetsMenuItem extends JMenuItem
{

  public InsetsMenuItem()
  {
    setMargin(new Insets(2, -20, 2, 2));
  }
  
  public InsetsMenuItem(String text)
  {
    super(text);
    setMargin(new Insets(2, -20, 2, 2));
  }
  
  public InsetsMenuItem(Action action)
  {
    super(action);
    setMargin(new Insets(2, -20, 2, 2));
  }
}
