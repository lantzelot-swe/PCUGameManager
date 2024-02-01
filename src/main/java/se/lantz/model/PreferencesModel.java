package se.lantz.model;

import java.util.Objects;
import java.util.Properties;

import se.lantz.util.FileManager;

public class PreferencesModel extends AbstractModel implements CommonInfoModel
{
  public static final String CAROUSEL_132 = "1.3.2";
  public static final String CAROUSEL_152 = "1.5.2";
  public static final String FILE_LOADER = "fl";

  public static final String PCUAE_VERSION_CHECK = "checkForPCUAEVersion";
  public static final String MANGER_VERSION_CHECK = "checkForManagerVersion";
  public static final String DELETE_OLD_INSTALL_FILES = "deleteOldInstallFiles";
  public static final String CROP_SCREENSHOTS = "cropScreenshotsWhenAdded";
  public static final String SHOW_CROP_DIALOG_FOR_COVER = "showCropDialogForCover";
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

  public static final String FAVORITES_1_ALIAS = "GameViewFavoritesName_1";
  public static final String FAVORITES_2_ALIAS = "GameViewFavoritesName_2";
  public static final String FAVORITES_3_ALIAS = "GameViewFavoritesName_3";
  public static final String FAVORITES_4_ALIAS = "GameViewFavoritesName_4";
  public static final String FAVORITES_5_ALIAS = "GameViewFavoritesName_5";
  public static final String FAVORITES_6_ALIAS = "GameViewFavoritesName_6";
  public static final String FAVORITES_7_ALIAS = "GameViewFavoritesName_7";
  public static final String FAVORITES_8_ALIAS = "GameViewFavoritesName_8";
  public static final String FAVORITES_9_ALIAS = "GameViewFavoritesName_9";
  public static final String FAVORITES_10_ALIAS = "GameViewFavoritesName_10";

  private boolean checkPCUAEVersionAtStartup = true;
  private boolean checkManagerVersionAtStartup = true;
  private boolean deleteOldInstallfilesAfterDownload = false;
  private boolean cropScreenshots = false;
  private boolean cropDialogForCover = false;

  private String description =
    "For more Info on PCUAE go to https://github.com/CommodoreOS/PCUAE. Main keys: CTRL + F1 for Carousel Gamelist Changer, CTRL + F3 for Carousel Version Changer, CTRL + F5 for Mode Changer (Amiga, Atari, linux etc), CTRL + F7 for PCUAE Options Menu.";
  private String descriptionDe = "";
  private String descriptionFr = "";
  private String descriptionEs = "";
  private String descriptionIt = "";
  private int year = 2023;
  private String genre = "adventure";
  private String author = "";
  private String composer = "C64 SID Background Music";
  private int favoritesCount = 10;

  private String joystickConfig = "J:2*:" + JoystickModel.DEFAULT_CONFIG;
  private String savedStatesCarouselVersion = CAROUSEL_152;

  private String fav1Alias = "";
  private String fav2Alias = "";
  private String fav3Alias = "";
  private String fav4Alias = "";
  private String fav5Alias = "";
  private String fav6Alias = "";
  private String fav7Alias = "";
  private String fav8Alias = "";
  private String fav9Alias = "";
  private String fav10Alias = "";

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
    setDeleteOldInstallfilesAfterDownload(Boolean
      .parseBoolean(configuredProperties.getProperty(DELETE_OLD_INSTALL_FILES, "false")));
    setCropScreenshots(Boolean.parseBoolean(configuredProperties.getProperty(CROP_SCREENSHOTS, "false")));
    setShowCropDialogForCover(Boolean.parseBoolean(configuredProperties.getProperty(SHOW_CROP_DIALOG_FOR_COVER, "false")));
    setFav1Alias(configuredProperties.getProperty(FAVORITES_1_ALIAS, fav1Alias));
    setFav2Alias(configuredProperties.getProperty(FAVORITES_2_ALIAS, fav2Alias));
    setFav3Alias(configuredProperties.getProperty(FAVORITES_3_ALIAS, fav3Alias));
    setFav4Alias(configuredProperties.getProperty(FAVORITES_4_ALIAS, fav4Alias));
    setFav5Alias(configuredProperties.getProperty(FAVORITES_5_ALIAS, fav5Alias));
    setFav6Alias(configuredProperties.getProperty(FAVORITES_6_ALIAS, fav6Alias));
    setFav7Alias(configuredProperties.getProperty(FAVORITES_7_ALIAS, fav7Alias));
    setFav8Alias(configuredProperties.getProperty(FAVORITES_8_ALIAS, fav8Alias));
    setFav9Alias(configuredProperties.getProperty(FAVORITES_9_ALIAS, fav9Alias));
    setFav10Alias(configuredProperties.getProperty(FAVORITES_10_ALIAS, fav10Alias));
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

  public String getFav1Alias()
  {
    return fav1Alias;
  }

  public void setFav1Alias(String alias)
  {
    String old = getFav1Alias();
    this.fav1Alias = alias;
    if (!Objects.equals(old, fav1Alias))
    {
      notifyChange();
    }
  }

  public String getFav2Alias()
  {
    return fav2Alias;
  }

  public void setFav2Alias(String alias)
  {
    String old = getFav2Alias();
    this.fav2Alias = alias;
    if (!Objects.equals(old, fav2Alias))
    {
      notifyChange();
    }
  }

  public String getFav3Alias()
  {
    return fav3Alias;
  }

  public void setFav3Alias(String alias)
  {
    String old = getFav3Alias();
    this.fav3Alias = alias;
    if (!Objects.equals(old, fav3Alias))
    {
      notifyChange();
    }
  }

  public String getFav4Alias()
  {
    return fav4Alias;
  }

  public void setFav4Alias(String alias)
  {
    String old = getFav4Alias();
    this.fav4Alias = alias;
    if (!Objects.equals(old, fav4Alias))
    {
      notifyChange();
    }
  }

  public String getFav5Alias()
  {
    return fav5Alias;
  }

  public void setFav5Alias(String alias)
  {
    String old = getFav5Alias();
    this.fav5Alias = alias;
    if (!Objects.equals(old, fav5Alias))
    {
      notifyChange();
    }
  }

  public String getFav6Alias()
  {
    return fav6Alias;
  }

  public void setFav6Alias(String alias)
  {
    String old = getFav6Alias();
    this.fav6Alias = alias;
    if (!Objects.equals(old, fav6Alias))
    {
      notifyChange();
    }
  }

  public String getFav7Alias()
  {
    return fav7Alias;
  }

  public void setFav7Alias(String alias)
  {
    String old = getFav7Alias();
    this.fav7Alias = alias;
    if (!Objects.equals(old, fav7Alias))
    {
      notifyChange();
    }
  }

  public String getFav8Alias()
  {
    return fav8Alias;
  }

  public void setFav8Alias(String alias)
  {
    String old = getFav8Alias();
    this.fav8Alias = alias;
    if (!Objects.equals(old, fav8Alias))
    {
      notifyChange();
    }
  }

  public String getFav9Alias()
  {
    return fav9Alias;
  }

  public void setFav9Alias(String alias)
  {
    String old = getFav9Alias();
    this.fav9Alias = alias;
    if (!Objects.equals(old, fav9Alias))
    {
      notifyChange();
    }
  }

  public String getFav10Alias()
  {
    return fav10Alias;
  }

  public void setFav10Alias(String alias)
  {
    String old = getFav10Alias();
    this.fav10Alias = alias;
    if (!Objects.equals(old, fav10Alias))
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

  public boolean isDeleteOldInstallfilesAfterDownload()
  {
    return deleteOldInstallfilesAfterDownload;
  }

  public void setDeleteOldInstallfilesAfterDownload(boolean deleteOldInstallfilesAfterDownload)
  {
    boolean old = isDeleteOldInstallfilesAfterDownload();
    this.deleteOldInstallfilesAfterDownload = deleteOldInstallfilesAfterDownload;
    if ((Boolean.compare(old, deleteOldInstallfilesAfterDownload) != 0))
    {
      notifyChange();
    }
  }

  public boolean isCropScreenshots()
  {
    return cropScreenshots;
  }

  public void setCropScreenshots(boolean cropScreenshots)
  {
    boolean old = isCropScreenshots();
    this.cropScreenshots = cropScreenshots;
    if ((Boolean.compare(old, cropScreenshots) != 0))
    {
      notifyChange();
    }
  }
  
  public void setShowCropDialogForCover(boolean cropDialogForCover)
  {
    boolean old = isShowCropDialogForCover();
    this.cropDialogForCover = cropDialogForCover;
    if ((Boolean.compare(old, cropDialogForCover) != 0))
    {
      notifyChange();
    }
  }
  
  public boolean isShowCropDialogForCover()
  {
    return cropDialogForCover;
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
    configuredProperties.put(DELETE_OLD_INSTALL_FILES, Boolean.toString(deleteOldInstallfilesAfterDownload));
    configuredProperties.put(CROP_SCREENSHOTS, Boolean.toString(cropScreenshots));
    configuredProperties.put(SHOW_CROP_DIALOG_FOR_COVER, Boolean.toString(cropDialogForCover));
    configuredProperties.put(FAVORITES_1_ALIAS, fav1Alias);
    configuredProperties.put(FAVORITES_2_ALIAS, fav2Alias);
    configuredProperties.put(FAVORITES_3_ALIAS, fav3Alias);
    configuredProperties.put(FAVORITES_4_ALIAS, fav4Alias);
    configuredProperties.put(FAVORITES_5_ALIAS, fav5Alias);
    configuredProperties.put(FAVORITES_6_ALIAS, fav6Alias);
    configuredProperties.put(FAVORITES_7_ALIAS, fav7Alias);
    configuredProperties.put(FAVORITES_8_ALIAS, fav8Alias);
    configuredProperties.put(FAVORITES_9_ALIAS, fav9Alias);
    configuredProperties.put(FAVORITES_10_ALIAS, fav10Alias);
    FileManager.storeProperties();
  }
}
