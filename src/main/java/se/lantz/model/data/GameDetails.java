package se.lantz.model.data;

import se.lantz.model.JoystickModel;

/**
 * The data structure representing a specific game.
 * 
 * @author lantzelot
 *
 */
public class GameDetails
{
  private String title = "";
  private int year = 1986;
  private String author = "";
  private String composer = "";
  private String genre = "";
  private String description = "";
  private String description_de = "";
  private String description_fr = "";
  private String description_es = "";
  private String description_it = "";
  private String game = "";
  private String cover = "";
  private String screen1 = "";
  private String screen2 = "";
  private String joy1 = "";
  private String joy2 = "";
  private String system = "";
  private int verticalshift = 0;
  private int duplicateIndex = 0;
  private String viewTag = "";

  public GameDetails()
  {
    setJoy1("J:1:" + JoystickModel.DEFAULT_CONFIG);
    setJoy2("J:2*:" + JoystickModel.DEFAULT_CONFIG);
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title == null ? "" : title;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description == null ? "" : description;
  }
  
  public String getDescriptionDe()
  {
    return description_de;
  }

  public void setDescriptionDe(String description)
  {
    this.description_de = description == null ? "" : description;
  }
  
  public String getDescriptionFr()
  {
    return description_fr;
  }

  public void setDescriptionFr(String description)
  {
    this.description_fr = description == null ? "" : description;
  }
  
  public String getDescriptionEs()
  {
    return description_es;
  }

  public void setDescriptionEs(String description)
  {
    this.description_es = description == null ? "" : description;
  }
  
  public String getDescriptionIt()
  {
    return description_it;
  }

  public void setDescriptionIt(String description)
  {
    this.description_it = description == null ? "" : description;
  }

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    this.year = year;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author == null ? "" : author;
  }

  public String getComposer()
  {
    return composer;
  }

  public void setComposer(String composer)
  {
    this.composer = composer == null ? "" : composer;
  }

  public String getGenre()
  {
    return genre;
  }

  public void setGenre(String genre)
  {
    this.genre = genre == null ? "" : genre;
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

  public String getJoy1()
  {
    return joy1;
  }

  public void setJoy1(String joy1)
  {
    this.joy1 = joy1 == null ? "" : joy1;
  }

  public String getJoy2()
  {
    return joy2;
  }

  public void setJoy2(String joy2)
  {
    this.joy2 = joy2 == null ? "" : joy2;
  }

  public String getSystem()
  {
    return system;
  }

  public void setSystem(String system)
  {
    this.system = system == null ? "" : system;
  }
  
  public int getVerticalShift()
  {
    return verticalshift;
  }

  public void setVerticalShift(int shift)
  {
    this.verticalshift = shift;
  }
  
  public int getDuplicateIndex()
  {
    return duplicateIndex;
  }

  public void setDuplicateIndex(int index)
  {
    this.duplicateIndex = index;
  }

  public String getViewTag()
  {
    return viewTag;
  }

  public void setViewTag(String viewTag)
  {
    this.viewTag = viewTag == null ? "" : viewTag;
  }
}
