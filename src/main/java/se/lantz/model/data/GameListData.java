package se.lantz.model.data;

public class GameListData implements Comparable
{
  private String title = "";
  private String gameFileName = "";
  private String gameId = "";
  private int favorite = 0;
  private boolean infoSlot = false;
  private int fileCount = 1;
  //Properties below are used for filtering
  private String composer = "";
  private String author = "";
  private int year = 0;
  private String viewTag = "";

  public GameListData(String title, String gameFileName, String gameId, int favorite, boolean infoSlot)
  {
    super();
    this.title = title;
    this.gameFileName = gameFileName;
    this.gameId = gameId;
    this.favorite = favorite;
    this.infoSlot = infoSlot;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getGameId()
  {
    return gameId;
  }

  public void setGameId(String gameId)
  {
    this.gameId = gameId;
  }

  public int getFavorite()
  {
    return favorite;
  }

  public void toggleFavorite(int number)
  {
    this.favorite = favorite == number ? 0 : number;
  }

  public boolean isFavorite()
  {
    return favorite > 0;
  }

  public boolean isInfoSlot()
  {
    return infoSlot;
  }

  public int getFavoriteNumber()
  {
    return favorite;
  }

  public void setFavorite(int favorite)
  {
    this.favorite = favorite;
  }

  public String getGameFileName()
  {
    return gameFileName;
  }

  public void setGameFileName(String gameFileName)
  {
    this.gameFileName = gameFileName;
  }

  public int getFileCount()
  {
    return fileCount;
  }

  public void setFileCount(int fileCount)
  {
    this.fileCount = fileCount;
  }

  @Override
  public String toString()
  {
    return title;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((gameId == null) ? 0 : gameId.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GameListData other = (GameListData) obj;
    if (gameId == null)
    {
      if (other.gameId != null)
        return false;
    }
    else if (!gameId.equals(other.gameId))
      return false;
    if (title == null)
    {
      if (other.title != null)
        return false;
    }
    else if (!title.equals(other.title))
      return false;
    return true;
  }

  @Override
  public int compareTo(Object o)
  {
    return title.compareTo(o.toString());
  }

  public String getComposer()
  {
    return composer;
  }

  public void setComposer(String composer)
  {
    this.composer = composer;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    this.year = year;
  }

  public String getViewTag()
  {
    return viewTag;
  }

  public void setViewTag(String viewTag)
  {
    this.viewTag = viewTag;
  }
}
