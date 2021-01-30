package se.lantz.util;

import java.util.Comparator;

import se.lantz.model.data.GameListData;

public class GameListDataComparator implements Comparator<GameListData>
{
  @Override
  public int compare(GameListData o1, GameListData o2)
  {
    return o1.getTitle().compareToIgnoreCase(o2.getTitle());
  }
}
