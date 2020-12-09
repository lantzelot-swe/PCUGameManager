package se.lantz.model;

import java.awt.image.BufferedImage;
import java.util.Objects;

import se.lantz.util.FileManager;

public class InfoModel extends AbstractModel
{
  private String title = "";
  //Use this when saving cover/screen/game files: If the title has been changed the files shall be renamed.
  private String titleInDb = "";
  private String description = "";
  private int year = 0;
  private String genre = "";
  private String composer = "";
  private String author = "";
  private String gamesFile = "";
  private String coverFile = "";
  private String screens1File = "";
  private String screens2File = "";
  
  //These images have there original sizes. Scale cover to 122x175 once storing it
  private BufferedImage coverImage;
  private BufferedImage screen1Image;
  private BufferedImage screen2Image;

  public String getTitle()
  {
    return title;
  }
  
  public void setTitleFromDb(String title)
  {
    titleInDb = title;
    this.title = title;
  }
  
  public String getTitleInDb()
  {
    return titleInDb;
  }

  public void setTitle(String title)
  {
    String old = getTitle();
    this.title = title;
    if (!Objects.equals(old, title))
    {     
      notifyChange();
    }
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    String old = getDescription();
    this.description = description;
    if (!Objects.equals(old, description))
    {
      notifyChange();
    }
  }

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    int old = getYear();
    this.year = year;
    if (old != year)
    {
      notifyChange();
    }
  }

  public String getGenre()
  {
    return genre;
  }

  public void setGenre(String genre)
  {
    String old = getGenre();
    this.genre = genre;
    if (!Objects.equals(old, genre))
    {
      notifyChange();
    }
  }

  public String getComposer()
  {
    return composer;
  }

  public void setComposer(String composer)
  {
    String old = getComposer();
    this.composer = composer;
    if (!Objects.equals(old, composer))
    {
      notifyChange();
    }

  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    String old = getAuthor();
    this.author = author;
    if (!Objects.equals(old, author))
    {
      notifyChange();
    }
  }

  public String getGamesFile()
  {
    return gamesFile;
  }

  public void setGamesFile(String gamesFile)
  {
    String old = getGamesFile();
    this.gamesFile = gamesFile;
    if (!Objects.equals(old, gamesFile))
    {
      notifyChange();
    }
  }

  public String getCoverFile()
  {
    return coverFile;
  }

  public void setCoverFile(String coverFile)
  {
    String old = getCoverFile();
    this.coverFile = coverFile;
    if (!Objects.equals(old, coverFile))
    {
      notifyChange();
    }
  }

  public String getScreens1File()
  {
    return screens1File;
  }

  public void setScreens1File(String screens1File)
  {
    String old = getScreens1File();
    this.screens1File = screens1File;
    if (!Objects.equals(old, screens1File))
    {
      notifyChange();
    }
  }

  public String getScreens2File()
  {
    return screens2File;
  }

  public void setScreens2File(String screens2File)
  {
    String old = getScreens2File();
    this.screens2File = screens2File;
    if (!Objects.equals(old, screens2File))
    {
      notifyChange();
    }
  }

  public BufferedImage getCoverImage()
  {
    return coverImage;
  }

  public void setCoverImage(BufferedImage newCoverImage)
  {
    BufferedImage old = getCoverImage();
    this.coverImage = newCoverImage;
    if (!Objects.equals(old, newCoverImage))
    {
      notifyChange();
    }
  }

  public BufferedImage getScreen1Image()
  {
    return screen1Image;
  }

  public void setScreen1Image(BufferedImage newScreen1Image)
  {
    BufferedImage old = getScreen1Image();
    this.screen1Image = newScreen1Image;
    if (!Objects.equals(old, newScreen1Image))
    {
      notifyChange();
    }
  }

  public BufferedImage getScreen2Image()
  {
    return screen2Image;
  }

  public void setScreen2Image(BufferedImage newScreen2Image)
  {
    BufferedImage old = getScreen2Image();
    this.screen2Image = newScreen2Image;
    if (!Objects.equals(old, newScreen2Image))
    {
      notifyChange();
    }
  }
  
  public void updateFileNames()
  {
    disableChangeNotification(true);
    String fileName = FileManager.generateFileNameFromTitle(this.title);
    if (!getCoverFile().isEmpty() || getCoverImage() != null)
    {
      setCoverFile(fileName + "-cover.png");
    }
    if (!getScreens1File().isEmpty() || getScreen1Image() != null)
    {
      setScreens1File(fileName + "-00.png");
    }
    if (!getScreens2File().isEmpty() || getScreen2Image() != null)
    {
      setScreens2File(fileName + "-01.png");
    }
    if (!getGamesFile().isEmpty())
    {
      String fileEnding = getGamesFile().substring(getGamesFile().indexOf("."));
      setGamesFile(fileName + fileEnding);
    }
    disableChangeNotification(false);
  }
  
  public boolean isNewGame()
  {
    return titleInDb.isEmpty();
  }
  
  public boolean isTitleChanged()
  {
    return !titleInDb.isEmpty() && !titleInDb.equals(title);
  }
  
  
  public void resetImages()
  {
    this.coverImage = null;
    this.screen1Image = null;
    this.screen2Image = null;
  }
}
