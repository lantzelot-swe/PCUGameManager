package se.lantz.model.data;

public class ScraperFields
{
  private boolean title = true;
  private boolean author = true;
  private boolean year = true;
  private boolean description = true;
  private boolean genre = true;
  private boolean cover = true;
  private boolean screenshots = true;
  public ScraperFields()
  {
    
  }
  public boolean isTitle()
  {
    return title;
  }
  public void setTitle(boolean title)
  {
    this.title = title;
  }
  public boolean isAuthor()
  {
    return author;
  }
  public void setAuthor(boolean author)
  {
    this.author = author;
  }
  public boolean isYear()
  {
    return year;
  }
  public void setYear(boolean year)
  {
    this.year = year;
  }
  public boolean isDescription()
  {
    return description;
  }
  public void setDescription(boolean description)
  {
    this.description = description;
  }
  public boolean isCover()
  {
    return cover;
  }
  public void setCover(boolean cover)
  {
    this.cover = cover;
  }
  public boolean isScreenshots()
  {
    return screenshots;
  }
  public void setScreenshots(boolean screenshots)
  {
    this.screenshots = screenshots;
  }
  public boolean isGenre()
  {
    return genre;
  }
  public void setGenre(boolean genre)
  {
    this.genre = genre;
  }

}
