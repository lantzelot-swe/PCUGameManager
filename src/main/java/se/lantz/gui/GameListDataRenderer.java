package se.lantz.gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import se.lantz.model.data.GameListData;

public class GameListDataRenderer extends DefaultListCellRenderer
{
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
    Component listCellRendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    GameListData listData = (GameListData)value;
    if (listData.isFavorite())
    {
      this.setFont(bold);
    }
    else
    {
      this.setFont(plain);
    }
    return listCellRendererComponent;
  }
}
