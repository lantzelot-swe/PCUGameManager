package se.lantz.gui.carousel;

import javax.swing.JLabel;

public class GameLabel extends JLabel
{
  private String gameId = "";

  public GameLabel()
  {
    // TODO Auto-generated constructor stub
  }

  public void setGameId(String gameId)
  {
    this.gameId = gameId;
  }

  public String getGameId()
  {
    return this.gameId;
  }

}
