package se.lantz.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import se.lantz.manager.SavedStatesManager;
import se.lantz.model.InfoModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;
import se.lantz.util.FileManager;

public class GameListDataRenderer extends DefaultListCellRenderer
{
  private Color fav1Color = Color.BLACK;
  private Color fav1ColorSelected = Color.WHITE;
  //Orange
  private Color fav2Color = new Color(0, 38, 255);
  private Color fav2ColorSelected = new Color(186, 202, 255);
  //Blue
  private Color fav3Color = new Color(255, 106, 0);
  private Color fav3ColorSelected = new Color(255, 163, 132);
  //Red
  private Color fav4Color = Color.GREEN.darker();
  private Color fav4ColorSelected = Color.GREEN;
  //Green
  private Color fav5Color = Color.RED;
  private Color fav5ColorSelected = Color.PINK;

  private ImageIcon saves1Icon = new ImageIcon(this.getClass().getResource("/se/lantz/16x16SaveIcon-1.png"));
  private ImageIcon saves2Icon = new ImageIcon(this.getClass().getResource("/se/lantz/16x16SaveIcon-2.png"));
  private ImageIcon saves3Icon = new ImageIcon(this.getClass().getResource("/se/lantz/16x16SaveIcon-3.png"));
  private ImageIcon saves4Icon = new ImageIcon(this.getClass().getResource("/se/lantz/16x16SaveIcon-4.png"));
  
  private ImageIcon warningIcon = new ImageIcon(getClass().getResource("/se/lantz/warning-icon.png"));

  private final Font bold;
  private final Font boldItalic;
  private SavedStatesManager savedStatesManager;

  public GameListDataRenderer(SavedStatesManager savedStatesManager)
  {
    this.savedStatesManager = savedStatesManager;
    this.boldItalic = getFont().deriveFont(Font.BOLD + Font.ITALIC);
    this.bold = getFont().deriveFont(Font.BOLD);
    this.setHorizontalTextPosition(SwingConstants.LEADING);
    this.setHorizontalAlignment(SwingConstants.LEADING);
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
      handleGameListData(value, isSelected, list);
    }
    else
    {
      handleGameListView(value, isSelected, index);
    }
    return listCellRendererComponent;
  }

  private void handleGameListData(Object value, boolean isSelected, JList list)
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
      case 6:
        this.setForeground(isSelected ? fav1ColorSelected : fav1Color);
        this.setFont(boldItalic);
        break;
      case 7:
        this.setForeground(isSelected ? fav2ColorSelected : fav2Color);
        this.setFont(boldItalic);
        break;
      case 8:
        this.setForeground(isSelected ? fav3ColorSelected : fav3Color);
        this.setFont(boldItalic);
        break;
      case 9:
        this.setForeground(isSelected ? fav4ColorSelected : fav4Color);
        this.setFont(boldItalic);
        break;
      case 10:
        this.setForeground(isSelected ? fav5ColorSelected : fav5Color);
        this.setFont(boldItalic);
        break;
      default:
        break;
      }
    }
    //Decide which icon to use
    int titleLength = list.getGraphics().getFontMetrics().stringWidth(listData.getTitle());
    if (titleLength > InfoModel.MAX_TITLE_LENGTH)
    {
      this.setIcon(warningIcon);
    }
    else
    { 
      int numberOfSavedStates = savedStatesManager.getNumberOfSavedStatesForGame(listData.getGameFileName());
      if (numberOfSavedStates == 1)
      {
        this.setIcon(saves1Icon);
      }
      else if (numberOfSavedStates == 2)
      {
        this.setIcon(saves2Icon);
      }
      else if (numberOfSavedStates == 3)
      {
        this.setIcon(saves3Icon);
      }
      else if (numberOfSavedStates == 4)
      {
        this.setIcon(saves4Icon);
      }
      else
      {
        this.setIcon(null);
      }
    }
  }

  private void handleGameListView(Object value, boolean isSelected, int index)
  {
    this.setIcon(null);
    this.setBorder(null);
    GameView view = (GameView) value;
    if (view.getGameViewId() == GameView.FAVORITES_ID)
    {
      this.setFont(bold);
      this.setForeground(isSelected ? fav1ColorSelected : fav1Color);
      if (index > -1)
      {
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
      }
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
    else if (view.getGameViewId() == GameView.FAVORITES_6_ID)
    {
      this.setFont(boldItalic);
      this.setForeground(isSelected ? fav1ColorSelected : fav1Color);
    }
    else if (view.getGameViewId() == GameView.FAVORITES_7_ID)
    {
      this.setFont(boldItalic);
      this.setForeground(isSelected ? fav2ColorSelected : fav2Color);
    }
    else if (view.getGameViewId() == GameView.FAVORITES_8_ID)
    {
      this.setFont(boldItalic);
      this.setForeground(isSelected ? fav3ColorSelected : fav3Color);
    }
    else if (view.getGameViewId() == GameView.FAVORITES_9_ID)
    {
      this.setFont(boldItalic);
      this.setForeground(isSelected ? fav4ColorSelected : fav4Color);
    }
    else if (view.getGameViewId() == GameView.FAVORITES_10_ID)
    {
      this.setFont(boldItalic);
      this.setForeground(isSelected ? fav5ColorSelected : fav5Color);     
    }
    //Check if view is a favorite and the last one configured
    if (view.getGameViewId() == -FileManager.getConfiguredNumberOfFavorites()-1)
    {
      if (index > -1)
      {
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
      }
    }
  }

  @Override
  public void setBounds(int x, int y, int width, int height)
  {
    super.setBounds(x, y, width, height);
    if (getIcon() != null)
    {
      int padding = 1;
      int textWidth = getFontMetrics(getFont()).stringWidth(getText());
      Insets insets = getInsets();
      int iconTextGap = width - textWidth - getIcon().getIconWidth() - insets.left - insets.right - padding;
      setIconTextGap(iconTextGap);
    }
    else
    {
      setIconTextGap(0);
    }
  }
}
