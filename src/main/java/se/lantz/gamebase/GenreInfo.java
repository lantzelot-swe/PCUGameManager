package se.lantz.gamebase;

public class GenreInfo
{
  private String name;
  private int pgId;
  private int geId;
  
  public GenreInfo(String name, int pgId, int geId)
  {
    this.name = name;
    this.pgId = pgId;
    this.geId = geId;
  }

  public String getGenreName()
  {
    return name;
  }

  public void setGenreName(String genreName)
  {
    this.name = genreName;
  }

  public int getPgId()
  {
    return pgId;
  }

  public void setPgId(int pgId)
  {
    this.pgId = pgId;
  }

  public int getGeId()
  {
    return geId;
  }

  public void setGeId(int geId)
  {
    this.geId = geId;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + geId;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + pgId;
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
    GenreInfo other = (GenreInfo) obj;
    if (geId != other.geId)
      return false;
    if (name == null)
    {
      if (other.name != null)
        return false;
    }
    else if (!name.equals(other.name))
      return false;
    if (pgId != other.pgId)
      return false;
    return true;
  }

  @Override
  public String toString()
  {
    return name;
  }
}
