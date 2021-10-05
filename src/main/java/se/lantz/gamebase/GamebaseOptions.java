package se.lantz.gamebase;

import java.nio.file.Path;

public class GamebaseOptions
{
  private Path gamebaseDbFile;
  private boolean c64 = true;
  private String titleQueryString = "";
  private boolean includeMissingGameFileEntries = false;
  private String viewTag = "";
  private GenreInfo genre;

  private GamebaseImporter.Options selectedOption = GamebaseImporter.Options.FAVORITES;

  public GamebaseOptions()
  {
    //Empty
  }

  public Path getGamebaseDbFile()
  {
    return gamebaseDbFile;
  }

  public void setGamebaseDbFile(Path gamebaseFolder)
  {
    this.gamebaseDbFile = gamebaseFolder;
  }
  
  public boolean isC64()
  {
    return c64;
  }

  public void setC64(boolean c64)
  {
    this.c64 = c64;
  }

  public GamebaseImporter.Options getSelectedOption()
  {
    return selectedOption;
  }

  public void setSelectedOption(GamebaseImporter.Options selectedOption)
  {
    this.selectedOption = selectedOption;
  }

  public String getTitleQueryString()
  {
    return titleQueryString;
  }

  public void setTitleQueryString(String titleQueryString)
  {
    this.titleQueryString = titleQueryString;
  }

  public boolean isIncludeMissingGameFileEntries()
  {
    return includeMissingGameFileEntries;
  }

  public void setIncludeMissingGameFileEntries(boolean includeMissingGameFileEntries)
  {
    this.includeMissingGameFileEntries = includeMissingGameFileEntries;
  }

  public String getViewTag()
  {
    return viewTag;
  }

  public void setViewTag(String viewTag)
  {
    this.viewTag = viewTag;
  }

  public GenreInfo getGenre()
  {
    return genre;
  }

  public void setGenre(GenreInfo genre)
  {
    this.genre = genre;
  }
}
