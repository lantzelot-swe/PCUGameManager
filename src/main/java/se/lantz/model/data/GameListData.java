package se.lantz.model.data;

public class GameListData implements Comparable
{
  private String title;
  private String gameId;
  private int favorite;

  public GameListData(String title, String gameId, int favorite)
  {
    super();
    this.title = title;
    this.gameId = gameId;
    this.favorite = favorite;
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
  
  public void toggleFavorite()
  {
    this.favorite = favorite == 1 ? 0 : 1;
  }
  
  public void toggleFavorite2()
  {
    this.favorite = favorite == 2 ? 0 : 2;
  }
  
  public void toggleFavorite3()
  {
    this.favorite = favorite == 3 ? 0 : 3;
  }
  public void toggleFavorite4()
  {
    this.favorite = favorite == 4 ? 0 : 4;
  }
  public void toggleFavorite5()
  {
    this.favorite = favorite == 5 ? 0 : 5;
  }

  public boolean isFavorite()
  {
    return favorite > 0;
  }
  
  public int getFavoriteNumber()
  {
    return favorite;
  }

  public void setFavorite(int favorite)
  {
    this.favorite = favorite;
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
}
