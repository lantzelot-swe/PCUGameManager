package se.lantz.model;

import javax.swing.DefaultListModel;

import se.lantz.model.data.GameListData;

public class GameListModel extends DefaultListModel<GameListData>
{ 
  void notifySave()
  {
    fireContentsChanged(this, 0, getSize()-1);
  }
}
