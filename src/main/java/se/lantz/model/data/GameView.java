package se.lantz.model.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameView
{
  public static final int ALL_GAMES_ID = -1;
  private static final Logger logger = LoggerFactory.getLogger(GameView.class);
  private String name = "";
  private boolean matchAll = true;
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

  public boolean isMatchAll()
  {
    return matchAll;
  }

  public void setMatchAll(boolean matchAll)
  {
    this.matchAll = matchAll;
  }

  public List<ViewFilter> getViewFilters()
  {
    return viewFilters;
  }

  public void setViewFilters(List<ViewFilter> viewFilters)
  {
    this.viewFilters = viewFilters;
    StringBuilder builder = new StringBuilder();
    builder.append("WHERE ");
    int index = 0;
    for (ViewFilter viewFilter : viewFilters)
    {
      index++;
      if (index > 1)
      {
        builder.append(isMatchAll() ? " AND ": " OR ");
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
}
