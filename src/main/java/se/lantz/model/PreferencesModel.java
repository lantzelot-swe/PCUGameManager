package se.lantz.model;

import java.util.Objects;
import java.util.Properties;

import se.lantz.util.FileManager;

public class PreferencesModel extends AbstractModel implements CommonInfoModel
{
  public static final String CAROUSEL_132 = "1.3.2";
  public static final String CAROUSEL_152 = "1.5.2";
  
  public static final String PCUAE_VERSION_CHECK = "checkForPCUAEVersion";
  public static final String MANGER_VERSION_CHECK = "checkForManagerVersion";
  public static final String GENRE = "infoSlotGenre";
  public static final String AUTHOR = "infoSlotAuthor";
  public static final String YEAR = "infoSlotYear";
  public static final String COMPOSER = "infoSlotComposer";
  public static final String DESCRIPTION = "infoSlotdescriptionEn";
  public static final String DESCRIPTION_DE = "infoSlotdescriptionDe";
  public static final String DESCRIPTION_FR = "infoSlotdescriptionFr";
  public static final String DESCRIPTION_ES = "infoSlotdescriptionEs";
  public static final String DESCRIPTION_IT = "infoSlotdescriptionIt";
  public static final String FAVORITESCOUNT = "favoritesCount";
  public static final String JOYSTICK = "joystick";
  public static final String SAVED_STATES_CAROUSEL = "savedStatesCarousel";

  private boolean checkPCUAEVersionAtStartup = true;
  private boolean checkManagerVersionAtStartup = true;

  private String description =
    "For more Info on PCUAE look in The Help Menu. Main keys are CTRL + F1 for The Help Menu, CTRL + F3 for Carousel Version Changer, CTRL + F5 for Mode Changer, CTRL + F7 for PCUAE Option Menu, CTRL + SHIFT + F7 for Carousel Gamelist Changer.";
  private String descriptionDe = "";
  private String descriptionFr = "";
  private String descriptionEs = "";
  private String descriptionIt = "";
  private int year = 2022;
  private String genre = "adventure";
  private String author = "";
  private String composer = "C64 SID Background Music";
  private int favoritesCount = 10;

  private String joystickConfig = "J:2*:" + JoystickModel.DEFAULT_CONFIG;
  private String savedStatesCarouselVersion = CAROUSEL_152;

  public PreferencesModel()
  {
    Properties configuredProperties = FileManager.getConfiguredProperties();
    setCheckManagerVersionAtStartup(Boolean
      .parseBoolean(configuredProperties.getProperty(MANGER_VERSION_CHECK, "true")));
    setCheckPCUAEVersionAtStartup(Boolean.parseBoolean(configuredProperties.getProperty(PCUAE_VERSION_CHECK, "true")));
    setGenre(configuredProperties.getProperty(GENRE, genre));
    setAuthor(configuredProperties.getProperty(AUTHOR, author));
    setComposer(configuredProperties.getProperty(COMPOSER, composer));
    setDescription(configuredProperties.getProperty(DESCRIPTION, description));
    setDescriptionDe(configuredProperties.getProperty(DESCRIPTION_DE, descriptionDe));
    setDescriptionFr(configuredProperties.getProperty(DESCRIPTION_FR, descriptionFr));
    setDescriptionEs(configuredProperties.getProperty(DESCRIPTION_ES, descriptionEs));
    setDescriptionIt(configuredProperties.getProperty(DESCRIPTION_IT, descriptionIt));
    setYear(Integer.parseInt(configuredProperties.getProperty(YEAR, Integer.toString(year))));
    setFavoritesCount(Integer
      .parseInt(configuredProperties.getProperty(FAVORITESCOUNT, Integer.toString(favoritesCount))));
    setJoystickConfig(configuredProperties.getProperty(JOYSTICK, joystickConfig));
    setSavedStatesCarouselVersion(configuredProperties.getProperty(SAVED_STATES_CAROUSEL, CAROUSEL_152));
  }

  public boolean isCheckPCUAEVersionAtStartup()
  {
    return checkPCUAEVersionAtStartup;
  }

  public void setCheckPCUAEVersionAtStartup(boolean checkPCUAEVersionAtStartup)
  {
    boolean old = isCheckPCUAEVersionAtStartup();
    this.checkPCUAEVersionAtStartup = checkPCUAEVersionAtStartup;
    if ((Boolean.compare(old, checkPCUAEVersionAtStartup) != 0))
    {
      notifyChange();
    }
  }

  public boolean isCheckManagerVersionAtStartup()
  {
    return checkManagerVersionAtStartup;
  }

  public void setCheckManagerVersionAtStartup(boolean checkManagerVersionAtStartup)
  {
    boolean old = isCheckManagerVersionAtStartup();
    this.checkManagerVersionAtStartup = checkManagerVersionAtStartup;
    if ((Boolean.compare(old, checkManagerVersionAtStartup) != 0))
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
    if (!Objects.equals(old, this.description))
    {
      notifyChange();
    }
  }

  public String getDescriptionDe()
  {
    return descriptionDe;
  }

  public void setDescriptionDe(String descriptionDe)
  {
    String old = getDescriptionDe();
    //Replace all double spaces, tabs and newlines
    this.descriptionDe = descriptionDe.replaceAll("\\s\\s+", " ");
    this.descriptionDe = this.descriptionDe.replace("\t", " ");
    this.descriptionDe = this.descriptionDe.replace("\n", " ");
    this.descriptionDe = this.descriptionDe.replace("-", " ");
    if (!Objects.equals(old, this.descriptionDe))
    {
      notifyChange();
    }
  }

  public String getDescriptionFr()
  {
    return descriptionFr;
  }

  public void setDescriptionFr(String descriptionFr)
  {
    String old = getDescriptionFr();
    //Replace all double spaces, tabs and newlines
    this.descriptionFr = descriptionFr.replaceAll("\\s\\s+", " ");
    this.descriptionFr = this.descriptionFr.replace("\t", " ");
    this.descriptionFr = this.descriptionFr.replace("\n", " ");
    this.descriptionFr = this.descriptionFr.replace("-", " ");
    if (!Objects.equals(old, this.descriptionFr))
    {
      notifyChange();
    }
  }

  public String getDescriptionEs()
  {
    return descriptionEs;
  }

  public void setDescriptionEs(String descriptionEs)
  {
    String old = getDescriptionEs();
    //Replace all double spaces, tabs and newlines
    this.descriptionEs = descriptionEs.replaceAll("\\s\\s+", " ");
    this.descriptionEs = this.descriptionEs.replace("\t", " ");
    this.descriptionEs = this.descriptionEs.replace("\n", " ");
    this.descriptionEs = this.descriptionEs.replace("-", " ");
    if (!Objects.equals(old, this.descriptionEs))
    {
      notifyChange();
    }
  }

  public String getDescriptionIt()
  {
    return descriptionIt;
  }

  public void setDescriptionIt(String descriptionIt)
  {
    String old = getDescriptionIt();
    //Replace all double spaces, tabs and newlines
    this.descriptionIt = descriptionIt.replaceAll("\\s\\s+", " ");
    this.descriptionIt = this.descriptionIt.replace("\t", " ");
    this.descriptionIt = this.descriptionIt.replace("\n", " ");
    this.descriptionIt = this.descriptionIt.replace("-", " ");
    if (!Objects.equals(old, this.descriptionIt))
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

  public int getFavoritesCount()
  {
    return favoritesCount;
  }

  public void setFavoritesCount(int favoritesCount)
  {
    int old = getFavoritesCount();
    this.favoritesCount = favoritesCount;
    if (old != favoritesCount)
    {
      notifyChange();
    }
  }

  public String getJoystickConfig()
  {
    return joystickConfig;
  }

  public void setJoystickConfig(String joystickConfig)
  {
    String old = getJoystickConfig();
    this.joystickConfig = joystickConfig;
    if (!Objects.equals(old, joystickConfig))
    {
      notifyChange();
    }
  }

  public String getSavedStatesCarouselVersion()
  {
    return savedStatesCarouselVersion;
  }

  public void setSavedStatesCarouselVersion(String savedStatesCarouselVersion)
  {
    String old = getSavedStatesCarouselVersion();
    this.savedStatesCarouselVersion = savedStatesCarouselVersion;
    if (!Objects.equals(old, savedStatesCarouselVersion))
    {
      notifyChange();
    }
  }

  public void savePreferences()
  {
    Properties configuredProperties = FileManager.getConfiguredProperties();
    configuredProperties.put(MANGER_VERSION_CHECK, Boolean.toString(checkManagerVersionAtStartup));
    configuredProperties.put(PCUAE_VERSION_CHECK, Boolean.toString(checkPCUAEVersionAtStartup));

    configuredProperties.put(GENRE, genre);
    configuredProperties.put(AUTHOR, author);
    configuredProperties.put(COMPOSER, composer);
    configuredProperties.put(DESCRIPTION, description);
    configuredProperties.put(DESCRIPTION_DE, descriptionDe);
    configuredProperties.put(DESCRIPTION_FR, descriptionFr);
    configuredProperties.put(DESCRIPTION_ES, descriptionEs);
    configuredProperties.put(DESCRIPTION_IT, descriptionIt);
    configuredProperties.put(YEAR, Integer.toString(year));
    configuredProperties.put(FAVORITESCOUNT, Integer.toString(favoritesCount));
    configuredProperties.put(JOYSTICK, joystickConfig);
    configuredProperties.put(SAVED_STATES_CAROUSEL, savedStatesCarouselVersion);
    FileManager.storeProperties();
  }
}
