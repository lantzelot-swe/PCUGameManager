package se.lantz.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import se.lantz.util.FileManager;

public class InfoModel extends AbstractModel implements CommonInfoModel
{
  //This is an approximate value of what fits on one row in the carousel on theC64/theVic20
  public static int MAX_TITLE_LENGTH = 163;
  private String title = "";
  //Use this when saving cover/screen/game files: If the title has been changed the files shall be renamed.
  private String titleInDb = "";
  private String description = "";
  private String description_de = "";
  private String description_fr = "";
  private String description_es = "";
  private String description_it = "";
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

  private Path gamesPath;

  private String oldGamesFile = "";
  private String oldCoverFile = "";
  private String oldScreens1File = "";
  private String oldScreens2File = "";

  private int duplicateIndex = 0;

  private String viewTag = "";

  private Path disk2Path;
  private Path disk3Path;
  private Path disk4Path;
  private Path disk5Path;
  private Path disk6Path;
  private String disk2File = "";
  private String disk3File = "";
  private String disk4File = "";
  private String disk5File = "";
  private String disk6File = "";
  private String oldDisk2File = "";
  private String oldDisk3File = "";
  private String oldDisk4File = "";
  private String oldDisk5File = "";
  private String oldDisk6File = "";

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
    //Remove all " since that messes with the SQL
    this.title = title.replace("\"", "").trim();
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
    //Replace all double spaces, tabs and newlines
    this.description = description.replaceAll("\\s\\s+", " ");
    this.description = this.description.replace("\t", " ");
    this.description = this.description.replace("\n", " ");
    this.description = this.description.replace("-", " ");
    if (!Objects.equals(old, description))
    {
      notifyChange();
    }
  }

  public String getDescriptionDe()
  {
    return description_de;
  }

  public void setDescriptionDe(String description)
  {
    String old = getDescriptionDe();
    //Replace all double spaces, tabs and newlines
    this.description_de = description.replaceAll("\\s\\s+", " ");
    this.description_de = this.description_de.replace("\t", " ");
    this.description_de = this.description_de.replace("\n", " ");
    this.description_de = this.description_de.replace("-", " ");
    if (!Objects.equals(old, description))
    {
      notifyChange();
    }
  }

  public String getDescriptionFr()
  {
    return description_fr;
  }

  public void setDescriptionFr(String description)
  {
    String old = getDescriptionFr();
    //Replace all double spaces, tabs and newlines
    this.description_fr = description.replaceAll("\\s\\s+", " ");
    this.description_fr = this.description_fr.replace("\t", " ");
    this.description_fr = this.description_fr.replace("\n", " ");
    this.description_fr = this.description_fr.replace("-", " ");
    if (!Objects.equals(old, description))
    {
      notifyChange();
    }
  }

  public String getDescriptionEs()
  {
    return description_es;
  }

  public void setDescriptionEs(String description)
  {
    String old = getDescriptionEs();
    //Replace all double spaces, tabs and newlines
    this.description_es = description.replaceAll("\\s\\s+", " ");
    this.description_es = this.description_es.replace("\t", " ");
    this.description_es = this.description_es.replace("\n", " ");
    this.description_es = this.description_es.replace("-", " ");
    if (!Objects.equals(old, description))
    {
      notifyChange();
    }
  }

  public String getDescriptionIt()
  {
    return description_it;
  }

  public void setDescriptionIt(String description)
  {
    String old = getDescriptionIt();
    //Replace all double spaces, tabs and newlines
    this.description_it = description.replaceAll("\\s\\s+", " ");
    this.description_it = this.description_it.replace("\t", " ");
    this.description_it = this.description_it.replace("\n", " ");
    this.description_it = this.description_it.replace("-", " ");
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

  public int getDuplicateIndex()
  {
    return duplicateIndex;
  }

  public void setDuplicateIndex(int index)
  {
    this.duplicateIndex = index;
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
    //Remove all " since that messes with the SQL
    this.composer = composer.replace("\"", "").trim();
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
    //Remove all " since that messes with the SQL
    this.author = author.replace("\"", "").trim();
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

  public String getDisk2File()
  {
    return disk2File;
  }

  public void setDisk2File(String disk2File)
  {
    String old = getDisk2File();
    this.disk2File = disk2File;
    if (!Objects.equals(old, disk2File))
    {
      notifyChange();
    }
  }

  public String getDisk3File()
  {
    return disk3File;
  }

  public void setDisk3File(String disk3File)
  {
    String old = getDisk3File();
    this.disk3File = disk3File;
    if (!Objects.equals(old, disk3File))
    {
      notifyChange();
    }
  }

  public String getDisk4File()
  {
    return disk4File;
  }

  public void setDisk4File(String disk4File)
  {
    String old = getDisk4File();
    this.disk4File = disk4File;
    if (!Objects.equals(old, disk4File))
    {
      notifyChange();
    }
  }
  
  public String getDisk5File()
  {
    return disk5File;
  }

  public void setDisk5File(String disk5File)
  {
    String old = getDisk5File();
    this.disk5File = disk5File;
    if (!Objects.equals(old, disk5File))
    {
      notifyChange();
    }
  }
  
  public String getDisk6File()
  {
    return disk6File;
  }

  public void setDisk6File(String disk6File)
  {
    String old = getDisk6File();
    this.disk6File = disk6File;
    if (!Objects.equals(old, disk6File))
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

  public Path getGamesPath()
  {
    return gamesPath;
  }

  public void setGamesPath(File file)
  {
    Path old = getGamesPath();
    this.gamesPath = file.toPath();
    //Set games name, always add ".gz"??
    String fileName = FileManager.generateFileNameFromTitle(this.title, getDuplicateIndex());
    String fileEnding = file.getName().substring(file.getName().lastIndexOf("."));

    if (FileManager.shouldCompressFile(file.getName()))
    {
      setGamesFile(fileName + fileEnding + ".gz");
    }
    else
    {
      setGamesFile(fileName + fileEnding);
    }

    if (!Objects.equals(old, gamesPath))
    {
      notifyChange();
    }
  }

  public Path getDisk2Path()
  {
    return disk2Path;
  }

  public void setDisk2Path(File file)
  {
    Path old = getDisk2Path();
    if (file == null)
    {
      this.disk2Path = null;
    }
    else
    {
      this.disk2Path = file.toPath();
      String fileEnding = file.getName().substring(file.getName().lastIndexOf("."));
      setDisk2File(getDiskFileName(2, fileEnding));
    }

    if (!Objects.equals(old, disk2Path))
    {
      notifyChange();
    }
  }

  public String getDiskFileName(int diskIndex, String fileEnding)
  {
    return FileManager.generateFileNameFromTitle(this.title, getDuplicateIndex()) + "(DISK" + diskIndex + ")" +
      fileEnding;
  }

  public Path getDisk3Path()
  {
    return disk3Path;
  }

  public void setDisk3Path(File file)
  {
    Path old = getDisk3Path();
    if (file == null)
    {
      this.disk3Path = null;
    }
    else
    {
      this.disk3Path = file.toPath();
      String fileEnding = file.getName().substring(file.getName().lastIndexOf("."));
      setDisk3File(getDiskFileName(3, fileEnding));
    }

    if (!Objects.equals(old, disk3Path))
    {
      notifyChange();
    }
  }

  public Path getDisk4Path()
  {
    return disk4Path;
  }

  public void setDisk4Path(File file)
  {
    Path old = getDisk4Path();
    if (file == null)
    {
      this.disk4Path = null;
    }
    else
    {
      this.disk4Path = file.toPath();
      String fileEnding = file.getName().substring(file.getName().lastIndexOf("."));
      setDisk4File(getDiskFileName(4, fileEnding));
    }

    if (!Objects.equals(old, disk4Path))
    {
      notifyChange();
    }
  }
  
  public Path getDisk5Path()
  {
    return disk5Path;
  }

  public void setDisk5Path(File file)
  {
    Path old = getDisk5Path();
    if (file == null)
    {
      this.disk5Path = null;
    }
    else
    {
      this.disk5Path = file.toPath();
      String fileEnding = file.getName().substring(file.getName().lastIndexOf("."));
      setDisk5File(getDiskFileName(5, fileEnding));
    }

    if (!Objects.equals(old, disk5Path))
    {
      notifyChange();
    }
  }
  
  public Path getDisk6Path()
  {
    return disk6Path;
  }

  public void setDisk6Path(File file)
  {
    Path old = getDisk6Path();
    if (file == null)
    {
      this.disk6Path = null;
    }
    else
    {
      this.disk6Path = file.toPath();
      String fileEnding = file.getName().substring(file.getName().lastIndexOf("."));
      setDisk6File(getDiskFileName(6, fileEnding));
    }

    if (!Objects.equals(old, disk6Path))
    {
      notifyChange();
    }
  }
  
  public int getNumberOfExtraDisks()
  {
    int extraDisks = 0;
    if (!disk2File.isBlank())
    {
      extraDisks++;
    }
    if (!disk3File.isBlank())
    {
      extraDisks++;
    }
    if (!disk4File.isBlank())
    {
      extraDisks++;
    }
    if (!disk5File.isBlank())
    {
      extraDisks++;
    }
    if (!disk6File.isBlank())
    {
      extraDisks++;
    }
    return extraDisks;
  }

  public void updateFileNames()
  {
    //Keep track of the old names, used when renaming files when saving
    oldCoverFile = getCoverFile();
    oldScreens1File = getScreens1File();
    oldScreens2File = getScreens2File();
    oldGamesFile = getGamesFile();

    oldDisk2File = getDisk2File();
    oldDisk3File = getDisk3File();
    oldDisk4File = getDisk4File();
    oldDisk5File = getDisk5File();
    oldDisk6File = getDisk6File();
    
    disableChangeNotification(true);
    String fileName = FileManager.generateFileNameFromTitle(this.title, getDuplicateIndex());
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
    if (!getDisk2File().isEmpty())
    {
      String fileEnding = getDisk2File().substring(getDisk2File().indexOf("."));
      setDisk2File(getDiskFileName(2, fileEnding));
    }
    if (!getDisk3File().isEmpty())
    {
      String fileEnding = getDisk3File().substring(getDisk3File().indexOf("."));
      setDisk3File(getDiskFileName(3, fileEnding));
    }
    if (!getDisk4File().isEmpty())
    {
      String fileEnding = getDisk4File().substring(getDisk4File().indexOf("."));
      setDisk4File(getDiskFileName(4, fileEnding));
    }
    if (!getDisk5File().isEmpty())
    {
      String fileEnding = getDisk5File().substring(getDisk5File().indexOf("."));
      setDisk5File(getDiskFileName(5, fileEnding));
    }
    if (!getDisk6File().isEmpty())
    {
      String fileEnding = getDisk6File().substring(getDisk6File().indexOf("."));
      setDisk6File(getDiskFileName(6, fileEnding));
    }
    disableChangeNotification(false);
  }

  public void updateCoverAndScreensIfEmpty(boolean isC64)
  {
    if (getCoverFile().isEmpty() && getCoverImage() == null)
    {
      this.coverImage = isC64 ? FileManager.emptyC64Cover : FileManager.emptyVic20Cover;
    }
    if (getScreens1File().isEmpty() && getScreen1Image() == null)
    {
      //Use screen2 for 1 also
      if (getScreen2Image() != null)
      {
        this.screen1Image = getScreen2Image();
      }
    }
    else if (getScreens2File().isEmpty() && getScreen2Image() == null)
    {
      //Use screen1 for 2 also
      if (getScreen1Image() != null)
      {
        this.screen2Image = getScreen1Image();
      }
    }
  }

  public boolean isNewGame()
  {
    return titleInDb.isEmpty();
  }

  public boolean isInfoSlot()
  {
    return viewTag.contains("GIS:");
  }

  public boolean isTitleChanged()
  {
    return !titleInDb.isEmpty() && !titleInDb.equalsIgnoreCase(title);
  }

  public boolean screenNamesNeedsUpdate()
  {
    if (getScreens1File().isEmpty() && getScreen1Image() != null)
    {
      return true;
    }
    if (getScreens2File().isEmpty() && getScreen2Image() != null)
    {
      return true;
    }
    //Must have names ending with -00.png and -01.png
    return (!getScreens1File().isEmpty() && !getScreens1File().endsWith("-00.png")) ||
      ((!getScreens2File().isEmpty() && !getScreens2File().endsWith("-01.png")));
  }

  public boolean isAnyScreenRenamed()
  {
    return !oldScreens1File.isEmpty() || !oldScreens2File.isEmpty();
  }

  public void resetImagesAndOldFileNames()
  {
    this.coverImage = null;
    this.screen1Image = null;
    this.screen2Image = null;
    this.gamesPath = null;
    this.disk2Path = null;
    this.disk3Path = null;
    this.disk4Path = null;
    this.disk5Path = null;
    this.disk6Path = null;
    resetOldFileNames();
  }

  public void resetOldFileNames()
  {
    this.oldGamesFile = "";
    this.oldCoverFile = "";
    this.oldScreens1File = "";
    this.oldScreens2File = "";
    this.oldDisk2File = "";
    this.oldDisk3File = "";
    this.oldDisk4File = "";
    this.oldDisk5File = "";
    this.oldDisk6File = "";
  }

  public String getOldGamesFile()
  {
    return oldGamesFile;
  }

  public String getOldDisk2File()
  {
    return oldDisk2File;
  }

  public String getOldDisk3File()
  {
    return oldDisk3File;
  }

  public String getOldDisk4File()
  {
    return oldDisk4File;
  }
  
  public String getOldDisk5File()
  {
    return oldDisk5File;
  }
  
  public String getOldDisk6File()
  {
    return oldDisk6File;
  }

  public String getOldCoverFile()
  {
    return oldCoverFile;
  }

  public String getOldScreens1File()
  {
    return oldScreens1File;
  }

  public String getOldScreens2File()
  {
    return oldScreens2File;
  }

  public String getViewTag()
  {
    return viewTag;
  }

  public void setViewTag(String viewTag)
  {
    String old = getViewTag();
    this.viewTag = viewTag;
    if (!Objects.equals(old, viewTag))
    {
      notifyChange();
    }
  }
}
