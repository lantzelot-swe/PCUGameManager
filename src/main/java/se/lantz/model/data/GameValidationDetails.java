package se.lantz.model.data;

/**
 * The data structure representing a game when running db validation.
 * 
 * @author lantzelot
 *
 */
public class GameValidationDetails
{
  private String title = "";
  private String game = "";
  private String cover = "";
  private String screen1 = "";
  private String screen2 = "";
  private String system = "";

  public GameValidationDetails()
  {

  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title == null ? "" : title;
  }

  public String getGame()
  {
    return game;
  }

  public void setGame(String game)
  {
    this.game = game == null ? "" : game;
  }

  public String getCover()
  {
    return cover;
  }

  public void setCover(String cover)
  {
    this.cover = cover == null ? "" : cover;
  }

  public String getScreen1()
  {
    return screen1;
  }

  public void setScreen1(String screen1)
  {
    this.screen1 = screen1 == null ? "" : screen1;
  }

  public String getScreen2()
  {
    return screen2;
  }

  public void setScreen2(String screen2)
  {
    this.screen2 = screen2 == null ? "" : screen2;
  }
  
  public String getSystem()
  {
    return system;
  }

  public void setSystem(String system)
  {
    this.system = system == null ? "" : system;
  }
  
  public boolean isVic20()
  {
    return system.contains("vic");
  }
}
