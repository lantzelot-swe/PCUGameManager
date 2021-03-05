package se.lantz.gui.imports;

import javax.swing.JPanel;

import se.lantz.gamebase.GamebaseOptions;

public class GameBaseOptionsPanel extends JPanel
{

  public GameBaseOptionsPanel()
  {

  }
  
  public GamebaseOptions getSelectedGbOptions()
  {
    //TODO: add all configurable things to this (favorites, querys gamebase location etc).
    return new GamebaseOptions();
  }
}
