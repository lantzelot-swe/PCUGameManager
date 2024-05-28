package se.lantz.gui.menu;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JMenu;

public class InsetsMenu extends JMenu
{
  public InsetsMenu(String s)
  {
    super(s);
    setMargin(new Insets(2, -20, 2, 2));
  }
}
