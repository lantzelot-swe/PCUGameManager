package se.lantz.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import se.lantz.manager.SavedStatesManager;
import se.lantz.model.GameListModel;
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
  private JTextField filterField;
  private GameListModel listModel;

  public GameListDataRenderer(SavedStatesManager savedStatesManager, JTextField filterField, GameListModel listModel)
  {
    this.savedStatesManager = savedStatesManager;
    this.filterField = filterField;
    this.listModel = listModel;
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
      highlightText(listModel.getTitleFilterText((GameListData)value, filterField.getText()));
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
      int numberOfSavedStates =
        savedStatesManager.getNumberOfSavedStatesForGame(listData.getGameFileName(), listData.getTitle());
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
    if (view.getGameViewId() == -FileManager.getConfiguredNumberOfFavorites() - 1)
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

  private List<Rectangle2D> rectangles = new ArrayList<>();
  private Color colorHighlight = Color.YELLOW;

  public void reset()
  {
    rectangles.clear();
    repaint();
  }

  public void highlightText(String textToHighlight)
  {
    if (textToHighlight == null)
    {
      return;
    }
    reset();

    final String textToMatch = textToHighlight.toLowerCase().trim();
    if (textToMatch.length() == 0)
    {
      return;
    }
    textToHighlight = textToHighlight.trim();

    final String labelText = getText().toLowerCase();
    if (labelText.contains(textToMatch))
    {
      FontMetrics fm = getFontMetrics(getFont());
      float w = -1;
      final float h = fm.getHeight() - 1;
      int i = 0;
      while (true)
      {
        i = labelText.indexOf(textToMatch, i);
        if (i == -1)
        {
          break;
        }
        if (w == -1)
        {
          String matchingText = getText().substring(i, i + textToHighlight.length());
          w = fm.stringWidth(matchingText);
        }
        String preText = getText().substring(0, i);
        float x = fm.stringWidth(preText);
        rectangles.add(new Rectangle2D.Float(x, 1, w, h));
        i = i + textToMatch.length();
      }
      repaint();
    }
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    if (rectangles.size() > 0)
    {
      Graphics2D g2d = (Graphics2D) g;
      Color c = g2d.getColor();
      for (Rectangle2D rectangle : rectangles)
      {
        g2d.setColor(colorHighlight);
        g2d.fill(rectangle);
      }
      g2d.setColor(c);
    }
    super.paintComponent(g);
  }
}
