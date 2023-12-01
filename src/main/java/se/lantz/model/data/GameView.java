package se.lantz.model.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.util.DbConstants;

public class GameView implements Comparable
{
  private static final String fontRedStart = "<FONT COLOR=\"#ff0000\">";
  private static final String fontEnd = "</FONT COLOR>";
  private static final String fontBlackStart = "<FONT COLOR=\"#000000\">";

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

  private int fileCount = -1;

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
    String text = name;
    if (gameCount > -1)
    {
      if (gameViewId != -1)
      {
        String gameCountString =
          gameCount > 255 ? fontRedStart + gameCount + fontEnd : fontBlackStart + gameCount + fontEnd;
        String fileCountString =
          fileCount > 255 ? fontRedStart + fileCount + fontEnd : fontBlackStart + fileCount + fontEnd;
        if (gameCount < fileCount)
        {
          text = name + " (" + gameCountString + "/" + fileCountString + ")";
        }
        else
        {
          text = name + " (" + gameCountString + ")";
        }
      }
      else
      {
        text = name + " (" + gameCount + ")";
      }
    }
    return "<html>" + text + "</html>";
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
      if (index == 0)
      {
        builder.append("((");
      }
      if (index > 0)
      {
        builder.append(" AND (");
      }
      builder.append(viewFilter.getField());
      switch (viewFilter.getOperator())
      {
      case ViewFilter.BEGINS_WITH_TEXT:
        builder.append(" LIKE '");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        builder.append("%'");
        break;

      case ViewFilter.ENDS_WITH_TEXT:
        builder.append(" LIKE '%");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        builder.append("'");
        break;

      case ViewFilter.CONTAINS_TEXT:
        builder.append(" LIKE '%");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        builder.append("%'");
        break;

      case ViewFilter.NOT_CONTAINS_TEXT:
        builder.append(" NOT LIKE '%");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        builder.append("%'");
        break;

      case ViewFilter.EQUALS_TEXT:
        builder.append(" LIKE '");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
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
          builder.append(adaptFilterData(viewFilter.getFilterData()));
        }
        break;

      case ViewFilter.BEFORE:
        builder.append(" < ");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        break;

      case ViewFilter.AFTER:
        builder.append(" > ");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        break;
      default:
        logger.debug("Unexpected value: {}", viewFilter.getOperator());
        break;
      }
      builder.append(")");
      index++;
      if (index == andFiltersList.size())
      {
        builder.append(")");
      }
    }
    index = 0;
    for (ViewFilter viewFilter : orFiltersList)
    {
      if (index == 0)
      {
        if (!andFiltersList.isEmpty())
        {
          builder.append(" AND ((");
        }
        else
        {
          builder.append(" ((");
        }
      }

      if (index > 0)
      {
        builder.append(" OR (");
      }
      builder.append(viewFilter.getField());
      switch (viewFilter.getOperator())
      {
      case ViewFilter.BEGINS_WITH_TEXT:
        builder.append(" LIKE '");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        builder.append("%'");
        break;

      case ViewFilter.ENDS_WITH_TEXT:
        builder.append(" LIKE '%");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        builder.append("'");
        break;

      case ViewFilter.CONTAINS_TEXT:
        builder.append(" LIKE '%");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        builder.append("%'");
        break;

      case ViewFilter.EQUALS_TEXT:
        builder.append(" LIKE '");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
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
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        break;

      case ViewFilter.BEFORE:
        builder.append(" < ");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        break;

      case ViewFilter.AFTER:
        builder.append(" > ");
        builder.append(adaptFilterData(viewFilter.getFilterData()));
        break;
      default:
        logger.debug("Unexpected value: {}", viewFilter.getOperator());
        break;
      }
      builder.append(")");
      index++;
      if (index == orFiltersList.size())
      {
        builder.append(")");
      }
    }
    this.sqlQuery = builder.toString();
  }
  
  private String adaptFilterData(String data)
  {
    return data.replaceAll("'", "''");
  }
  
  public boolean isFavorite()
  {
    return gameViewId < -1;
  }

  public int getGameViewId()
  {
    return gameViewId;
  }

  public void setGameViewId(int gameViewId)
  {
    this.gameViewId = gameViewId;
  }

  public int getGameCount()
  {
    return this.gameCount;
  }

  public void setGameCount(int count)
  {
    this.gameCount = count;
  }

  public int getFileCount()
  {
    return fileCount;
  }

  public void setFileCount(int fileCount)
  {
    this.fileCount = fileCount;
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
    String comparer = o instanceof GameView ? ((GameView)o).name : o.toString();
    return this.name.compareTo(comparer);
  }
}
