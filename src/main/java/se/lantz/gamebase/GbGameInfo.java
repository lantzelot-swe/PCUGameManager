package se.lantz.gamebase;

public class GbGameInfo
{

  private String title;
  private String year;
  private String publisher;
  private String musician;
  private String genre;
  private String gamefile;
  private String coverFile;
  private String screen1;
  private String screen2;
  private String joy1config;
  private String joy2config;
  private String advanced;
  private String description;
  
  private boolean vic20cart = false;

  public GbGameInfo(String title,
                    String year,
                    String publisher,
                    String musician,
                    String genre,
                    String gamefile,
                    String coverFile,
                    String screen1,
                    String screen2,
                    String joy1config,
                    String joy2config,
                    String advanced,
                    String description, 
                    boolean vic20cart)
  {
    this.title = title;
    this.year = year;
    this.publisher = publisher;
    this.musician = musician;
    this.genre = genre;
    this.gamefile = gamefile;
    this.coverFile = coverFile;
    this.screen1 = screen1;
    this.screen2 = screen2;
    this.joy1config = joy1config;
    this.joy2config = joy2config;
    this.advanced = advanced;
    this.description = description;
    this.vic20cart = vic20cart;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getYear()
  {
    return year;
  }

  public void setYear(String year)
  {
    this.year = year;
  }

  public String getPublisher()
  {
    return publisher;
  }

  public void setPublisher(String publisher)
  {
    this.publisher = publisher;
  }

  public String getMusician()
  {
    return musician;
  }

  public void setMusician(String musician)
  {
    this.musician = musician;
  }

  public String getGenre()
  {
    return genre;
  }

  public void setGenre(String genre)
  {
    this.genre = genre;
  }

  public String getGamefile()
  {
    return gamefile;
  }

  public void setGamefile(String gamefile)
  {
    this.gamefile = gamefile;
  }

  public String getCoverFile()
  {
    return coverFile;
  }

  public void setCoverFile(String coverFile)
  {
    this.coverFile = coverFile;
  }

  public String getScreen1()
  {
    return screen1;
  }

  public void setScreen1(String screen1)
  {
    this.screen1 = screen1;
  }

  public String getScreen2()
  {
    return screen2;
  }

  public void setScreen2(String screen2)
  {
    this.screen2 = screen2;
  }

  public String getJoy1config()
  {
    return joy1config;
  }

  public void setJoy1config(String joy1config)
  {
    this.joy1config = joy1config;
  }

  public String getJoy2config()
  {
    return joy2config;
  }

  public void setJoy2config(String joy2config)
  {
    this.joy2config = joy2config;
  }

  public String getAdvanced()
  {
    return advanced;
  }

  public void setAdvanced(String advanced)
  {
    this.advanced = advanced;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public boolean isVic20Cart()
  {
    return vic20cart;
  }
  
  public void setVic20Cart(boolean vic20Cart)
  {
    this.vic20cart = vic20Cart;
  }
}
