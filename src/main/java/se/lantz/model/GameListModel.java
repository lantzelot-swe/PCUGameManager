package se.lantz.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

import javax.swing.DefaultListModel;

import org.apache.commons.lang.math.NumberUtils;

import se.lantz.model.data.GameListData;

public class GameListModel extends DefaultListModel<GameListData>
{
  private List<GameListData> currentGameList = Collections.emptyList();

  private List<String> filterPrefixList = Arrays.asList("a:", "c:", "y:", "v:");

  //The filter used to filtering the game list based on filter text.
  private BiPredicate<GameListData, String> gameListDataFilter = (data, filterText) -> {
    //Use "," as delimiter between fields. several fields are considered "AND"
    boolean found = false;
    if (filterText.length() == 0)
    {
      return true;
    }

    String[] sections = filterText.split(",+");
    for (String section : sections)
    {
      found = checkFilterSection(section.toLowerCase().trim(), data);
      if (!found)
      {
        break;
      }
    }
    return found;
  };

  private boolean checkFilterSection(String filterText, GameListData data)
  {
    boolean found = false;
    if (filterText.startsWith("c:"))
    {
      String composer = filterText.substring(2);
      found = data.getComposer().toLowerCase().contains(composer);
    }
    else if (filterText.startsWith("a:"))
    {
      String author = filterText.substring(2);
      found = data.getAuthor().toLowerCase().contains(author);
    }
    else if (filterText.startsWith("y:"))
    {
      String year = filterText.substring(2);
      found = NumberUtils.isNumber(year) && data.getYear() == Integer.parseInt(year);
    }
    else if (filterText.startsWith("v:"))
    {
      String viewTag = filterText.substring(2);
      found = data.getViewTag().toLowerCase().contains(viewTag);
    }
    else if (filterText.startsWith("s:"))
    {
      String systemConfig = filterText.substring(2);
      found = data.getSystem().toLowerCase().contains(systemConfig);
    }
    else
    {
      found = data.getTitle().toLowerCase().contains(filterText);
    }

    return found;
  }

  public void notifyChange()
  {
    fireContentsChanged(this, 0, getSize() - 1);
  }

  void addAllGames(List<GameListData> gamesList)
  {
    currentGameList = gamesList;
    //Disable event fireing when adding multiple games 
    clear();
    addAll(gamesList);
    notifyChange();
  }

  public List<GameListData> getCurrentGameList()
  {
    return currentGameList;
  }

  public boolean filterMatch(GameListData data, String filterString)
  {
    return gameListDataFilter.test(data, filterString);
  }

  public String getTitleFilterText(GameListData data, String filterString)
  {
    String returnValue = "";
    String[] sections = filterString.toLowerCase().split(",+");
    for (String section : sections)
    {
      if (section.length() < 2 || !filterPrefixList.contains(section.substring(0, 2)))
      {
        returnValue = section;
        break;
      }
    }
    return returnValue;
  }
}
