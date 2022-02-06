package se.lantz.model.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.util.DbConstants;

public class GameView implements Comparable
{
  public static final String FAV_GAMEVIEW_NAME_PREF_KEY = "GameViewFavoritesName_";
  public static final int ALL_GAMES_ID = -1;
  public static final int FAVORITES_ID = -2;
  public static final int FAVORITES_2_ID = -3;
  public static final int FAVORITES_3_ID = -4;
  public static final int FAVORITES_4_ID = -5;
  public static final int FAVORITES_5_ID = -6;
  public static final int FAVORITES_6_ID = -7;
  public static final int FAVORITES_7_ID = -8;
  public static final int FAVORITES_8_ID = -9;
  public static final int FAVORITES_9_ID = -10;
  public static final int FAVORITES_10_ID = -11;
  private static final Logger logger = LoggerFactory.getLogger(GameView.class);
  private String name = "";
  private List<ViewFilter> viewFilters = new ArrayList<>();
  private String sqlQuery = "";

  private int gameViewId;

  private int gameCount = -1;

  public GameView(int gameViewId)
  {
    this.gameViewId = gameViewId;
    
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getSqlQuery()
  {
    return sqlQuery;
  }

  public void setSqlQuery(String sqlQuery)
  {
    this.sqlQuery = sqlQuery;
  }

  @Override
  public String toString()
  {
    if (gameCount > -1)
    {
      return name + " (" + gameCount + ")";
    }
    return name;
  }

  public List<ViewFilter> getViewFilters()
  {
    return viewFilters;
  }

  public void setViewFilters(List<ViewFilter> viewFilters)
  {
    this.viewFilters = viewFilters;

    //Divide depending on operator
    List<ViewFilter> andFiltersList = new ArrayList<>();
    List<ViewFilter> orFiltersList = new ArrayList<>();

    for (ViewFilter viewFilter : viewFilters)
    {
      if (viewFilter.isAndOperator())
      {
        andFiltersList.add(viewFilter);
      }
      else
      {
        orFiltersList.add(viewFilter);
      }
    }

    StringBuilder builder = new StringBuilder();
    builder.append("WHERE ");
    //Add info slot condition first
    builder.append(DbConstants.VIEW_TAG);
    builder.append(" LIKE '");
    builder.append("GIS:");
    builder.append(gameViewId);
    builder.append("'");
    builder.append(" OR ");

    int index = 0;
    for (ViewFilter viewFilter : andFiltersList)
    {
      index++;
      if (index > 1)
      {
        builder.append(" AND ");
      }
      builder.append(viewFilter.getField());
      switch (viewFilter.getOperator())
      {
      case ViewFilter.BEGINS_WITH_TEXT:
        builder.append(" LIKE '");
        builder.append(viewFilter.getFilterData());
        builder.append("%'");
        break;

      case ViewFilter.ENDS_WITH_TEXT:
        builder.append(" LIKE '%");
        builder.append(viewFilter.getFilterData());
        builder.append("'");
        break;

      case ViewFilter.CONTAINS_TEXT:
        builder.append(" LIKE '%");
        builder.append(viewFilter.getFilterData());
        builder.append("%'");
        break;

      case ViewFilter.NOT_CONTAINS_TEXT:
        builder.append(" NOT LIKE '%");
        builder.append(viewFilter.getFilterData());
        builder.append("%'");
        break;

      case ViewFilter.EQUALS_TEXT:
        builder.append(" LIKE '");
        builder.append(viewFilter.getFilterData());
        builder.append("'");
        break;

      case ViewFilter.NOT_EMPTY:
        builder.append(" <> ''");
        break;

      case ViewFilter.EMPTY:
        builder.append(" is null or ");
        builder.append(viewFilter.getField());
        builder.append(" = ''");
        break;

      case ViewFilter.IS:
        builder.append(" = ");
        //Handle Favorites where value is either true or false, mapped towards 1 and 0 in the db
        if (viewFilter.getFilterData().equals("true"))
        {
          builder.append("1");
        }
        else if (viewFilter.getFilterData().equals("false"))
        {
          builder.append("0");
        }
        else
        {
          builder.append(viewFilter.getFilterData());
        }
        break;

      case ViewFilter.BEFORE:
        builder.append(" < ");
        builder.append(viewFilter.getFilterData());
        break;

      case ViewFilter.AFTER:
        builder.append(" > ");
        builder.append(viewFilter.getFilterData());
        break;
      default:
        logger.debug("Unexpected value: {}", viewFilter.getOperator());
        break;
      }
    }
    index = 0;
    for (ViewFilter viewFilter : orFiltersList)
    {
      if (index == 0 && !andFiltersList.isEmpty())
      {
        builder.append(" AND ( ");
      }
      index++;
      if (index > 1)
      {
        builder.append(" OR ");
      }
      builder.append(viewFilter.getField());
      switch (viewFilter.getOperator())
      {
      case ViewFilter.BEGINS_WITH_TEXT:
        builder.append(" LIKE '");
        builder.append(viewFilter.getFilterData());
        builder.append("%'");
        break;

      case ViewFilter.ENDS_WITH_TEXT:
        builder.append(" LIKE '%");
        builder.append(viewFilter.getFilterData());
        builder.append("'");
        break;

      case ViewFilter.CONTAINS_TEXT:
        builder.append(" LIKE '%");
        builder.append(viewFilter.getFilterData());
        builder.append("%'");
        break;

      case ViewFilter.EQUALS_TEXT:
        builder.append(" LIKE '");
        builder.append(viewFilter.getFilterData());
        builder.append("'");
        break;

      case ViewFilter.NOT_EMPTY:
        builder.append(" <> ''");
        break;

      case ViewFilter.EMPTY:
        builder.append(" is null or ");
        builder.append(viewFilter.getField());
        builder.append(" = ''");
        break;

      case ViewFilter.IS:
        builder.append(" = ");
        builder.append(viewFilter.getFilterData());
        break;

      case ViewFilter.BEFORE:
        builder.append(" < ");
        builder.append(viewFilter.getFilterData());
        break;

      case ViewFilter.AFTER:
        builder.append(" > ");
        builder.append(viewFilter.getFilterData());
        break;
      default:
        logger.debug("Unexpected value: {}", viewFilter.getOperator());
        break;
      }
    }
    if (!andFiltersList.isEmpty() && !orFiltersList.isEmpty())
    {
      builder.append(")");
    }
    this.sqlQuery = builder.toString();
  }

  public int getGameViewId()
  {
    return gameViewId;
  }

  public void setGameViewId(int gameViewId)
  {
    this.gameViewId = gameViewId;
  }

  public void setGameCount(int count)
  {
    this.gameCount = count;
  }
  
  public int getGameCount()
  {
    return this.gameCount;
  }
  
  public String getFavNamePreferencesKey()
  {
    if (gameViewId < -1)
    {
      return FAV_GAMEVIEW_NAME_PREF_KEY + -(gameViewId + 1);
    }
    return "";
  }

  @Override
  public int compareTo(Object o)
  {
    return this.name.compareTo(o.toString());
  }
}
