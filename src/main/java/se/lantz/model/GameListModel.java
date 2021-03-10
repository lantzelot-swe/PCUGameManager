package se.lantz.model;

import java.util.List;

import javax.swing.DefaultListModel;

import se.lantz.model.data.GameListData;

public class GameListModel extends DefaultListModel<GameListData>
{ 
  private boolean disableIntervalChange = false;
  
  void notifyChange()
  {
    fireContentsChanged(this, 0, getSize()-1);
  }
  
  @Override
  protected void fireIntervalAdded(Object source, int index0, int index1)
  {
    if (!disableIntervalChange)
    {
      super.fireIntervalAdded(source, index0, index1);;
    }
  }
  
  void addAllGames(List<GameListData> gamesList) 
  {
    //Disable event fireing when adding multiple games 
    clear();
    disableIntervalChange = true;
    for (GameListData gameListData : gamesList)
    {
      addElement(gameListData);
    }
    disableIntervalChange = false;
    notifyChange();
  }
}
