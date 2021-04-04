package se.lantz.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;

public class GameListDataRenderer extends DefaultListCellRenderer
{
  private Color fav1Color = Color.BLACK;
  private Color fav1ColorSelected = Color.WHITE;
  //Orange
  private Color fav2Color = new Color(255, 106, 0);
  private Color fav2ColorSelected = new Color(255, 163, 132);
  //Blue
  private Color fav3Color = new Color(0, 38, 255);
  private Color fav3ColorSelected = new Color(186, 202, 255);
  //Red
  private Color fav4Color = Color.RED;
  private Color fav4ColorSelected = Color.PINK;
  //Green
  private Color fav5Color = Color.GREEN.darker();
  private Color fav5ColorSelected = Color.GREEN;

  private final Font bold;
  private final Font plain;

  public GameListDataRenderer()
  {
    this.plain = getFont().deriveFont(Font.PLAIN);
    this.bold = getFont().deriveFont(Font.BOLD);
  }

  @Override
  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus)
  {
    Component listCellRendererComponent =
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    if (value instanceof GameListData)
    {
      handleGameListData(value, isSelected);
    }
    else
    {
      handleGameListView(value, isSelected);
    }
    return listCellRendererComponent;
  }

  private void handleGameListData(Object value, boolean isSelected)
  {
    GameListData listData = (GameListData) value;
    if (listData.isFavorite())
    {
      this.setFont(bold);

      switch (listData.getFavoriteNumber())
      {
      case 1:
        this.setForeground(isSelected ? fav1ColorSelected : fav1Color);
        break;
      case 2:
        this.setForeground(isSelected ? fav2ColorSelected : fav2Color);
        break;
      case 3:
        this.setForeground(isSelected ? fav3ColorSelected : fav3Color);
        break;
      case 4:
        this.setForeground(isSelected ? fav4ColorSelected : fav4Color);
        break;
      case 5:
        this.setForeground(isSelected ? fav5ColorSelected : fav5Color);
        break;
      default:
        break;
      }
    }
    else
    {
      this.setFont(plain);
    }
  }
  
  private void handleGameListView(Object value, boolean isSelected)
  {
    GameView view = (GameView) value;
    if (view.getGameViewId() == GameView.FAVORITES_ID)
    {
      this.setFont(bold);
      this.setForeground(isSelected ? fav1ColorSelected : fav1Color);
    }
    else if (view.getGameViewId() == GameView.FAVORITES_2_ID)
    {
      this.setFont(bold);
      this.setForeground(isSelected ? fav2ColorSelected : fav2Color);
    }
    else if (view.getGameViewId() == GameView.FAVORITES_3_ID)
    {
      this.setFont(bold);
      this.setForeground(isSelected ? fav3ColorSelected : fav3Color);
    }
    else if (view.getGameViewId() == GameView.FAVORITES_4_ID)
    {
      this.setFont(bold);
      this.setForeground(isSelected ? fav4ColorSelected : fav4Color);
    }
    else if (view.getGameViewId() == GameView.FAVORITES_5_ID)
    {
      this.setFont(bold);
      this.setForeground(isSelected ? fav5ColorSelected : fav5Color);
    }
    else
    {
      this.setFont(plain);
      this.setForeground(isSelected ? fav1ColorSelected : fav1Color);
    }
  }
}
