package se.lantz.gui.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.MainWindow;
import se.lantz.model.carousel.CarouselPreviewModel;
import se.lantz.model.data.GameDetails;

public class CoverPanel extends JPanel
{
  private static final Logger logger = LoggerFactory.getLogger(CarouselPreviewModel.class);

  private JPanel panel;
  private JScrollPane scrollPane;

  int scrollingTimerIndex = 0;
  boolean scrolingStopped = true;
  boolean scrollDirectionRight = true;

  private ActionListener timerListener = e -> scrollFromTimer();

  private Timer scrolingTimer = new Timer(7, timerListener);
  private CarouselPreviewModel model;
  private MainWindow mainWindow;

  public CoverPanel(CarouselPreviewModel model, final MainWindow mainWindow)
  {
    this.model = model;
    this.mainWindow = mainWindow;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[] { 1.0 };
    gridBagLayout.columnWeights = new double[] { 1.0 };
    setLayout(gridBagLayout);
    this.setPreferredSize(new Dimension(700, 187));
    GridBagConstraints gbc_scrollPane = new GridBagConstraints();
    gbc_scrollPane.weighty = 1.0;
    gbc_scrollPane.weightx = 1.0;
    gbc_scrollPane.fill = GridBagConstraints.BOTH;
    gbc_scrollPane.gridx = 0;
    gbc_scrollPane.gridy = 0;
    add(getScrollPane(), gbc_scrollPane);
    addCovers(10);

    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(CarouselPreviewModel.SELECTED_GAME, e -> {
        reloadScreens();
        updateSelectedBorder();
      });
      //trigger once at startup
      reloadScreens();
    }
  }

  private JPanel getPanel()
  {
    if (panel == null)
    {
      panel = new JPanel();
      GridBagLayout gbl_panel = new GridBagLayout();
      panel.setLayout(gbl_panel);

      panel.setBackground(new Color(138, 137, 138));
    }
    return panel;
  }

  private void scrollFromTimer()
  {
    int currentScrollValue = scrollPane.getHorizontalScrollBar().getValue();
    int newScrollValue = scrollDirectionRight ? currentScrollValue + 32 : currentScrollValue - 32;
    //Scroll 
    scrollPane.getHorizontalScrollBar().setValue(newScrollValue);
    scrollingTimerIndex++;
    if (scrollingTimerIndex > 5)
    {
      //Scroll one last time
      int lastScrollValue = scrollDirectionRight ? currentScrollValue + 10 : currentScrollValue - 10;
      scrollPane.getHorizontalScrollBar().setValue(lastScrollValue);
      scrollingTimerIndex = 0;
      scrolingTimer.stop();
      scrolingStopped = true;
      //Select the next game in the list
      String gameId = "";

      if (scrollDirectionRight)
      {
        gameId = model.getNextGameToSelectWhenScrollingRight();
      }
      else
      {
        gameId = model.getNextGameToSelectWhenScrollingLeft();
      }
      this.mainWindow.setSelectedGameInGameList(gameId);
    }
  }

  private JScrollPane getScrollPane()
  {
    if (scrollPane == null)
    {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getPanel());
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
      scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
      scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
      scrollPane.getHorizontalScrollBar().setBlockIncrement(10);

      //Remove all previously registered mouse wheel listeners to not interfere
      for (MouseWheelListener listener : scrollPane.getMouseWheelListeners())
      {
        scrollPane.removeMouseWheelListener(listener);
      }
    }
    return scrollPane;
  }

  private void addCovers(int converCount)
  {
    for (int i = 0; i < converCount; i++)
    {
      GridBagConstraints gbc_label = new GridBagConstraints();
      gbc_label.fill = GridBagConstraints.BOTH;
      if (i == 0)
      {
        gbc_label.insets = new Insets(5, 140, 5, 23);
      }
      else if (i == converCount - 1)
      {
        gbc_label.insets = new Insets(5, 22, 5, 100);
      }
      else
      {
        gbc_label.insets = new Insets(5, 22, 5, 23);
      }
      gbc_label.weighty = 1.0;
      gbc_label.gridx = i;
      gbc_label.gridy = 0;
      panel.add(getLabel(i), gbc_label);
    }
  }

  private JLabel getLabel(int index)
  {
    JLabel label = new JLabel();
    label.setPreferredSize(new Dimension(125, 175));
    if (index == 4)
    {
      label.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
      label.addMouseListener(new MouseAdapter()
      {

        @Override
        public void mouseClicked(MouseEvent e)
        {
          if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)
          {
            //trigger run game
            mainWindow.getMainPanel().runCurrentGame();
          }
        }
      });
    }
    return label;
  }

  protected void scrollOneGame(boolean right)
  {
    if (scrolingStopped)
    {
      scrollDirectionRight = right;
      scrolingStopped = false;
      scrolingTimer.start();
    }
  }
  
  protected void pageUpTriggered()
  {
    String gameId = model.getGameIdForPageUp();
    this.mainWindow.setSelectedGameInGameList(gameId);
  }
  
  protected void pageDownTriggered()
  {
    String gameId = model.getGameIdForPageDown();
    this.mainWindow.setSelectedGameInGameList(gameId);
  }

  private void reloadScreens()
  {
    panel.setVisible(false);
    //Remove all existing
    panel.removeAll();
    addCovers(10);
    List<GameDetails> games = model.getGameDetails();

    for (int i = 0; i < games.size(); i++)
    {
      GameDetails game = games.get(i);
      loadScreen((JLabel) panel.getComponent(i), game);
    }
    scrollToPosition();
    panel.setVisible(true);
  }

  private void updateSelectedBorder()
  {
    for (int i = 0; i < panel.getComponentCount(); i++)
    {
      ((JLabel) panel.getComponent(i)).setBorder(null);
    }

    List<GameDetails> games = model.getGameDetails();

    for (int i = 0; i < games.size(); i++)
    {
      if (games.indexOf(model.getSelectedGame()) == i)
      {
        //        logger.debug("Setting selected border to cover nr " + i);
        ((JLabel) panel.getComponent(i)).setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
      }
    }
  }

  private void loadScreen(JLabel label, GameDetails game)
  {
    String filename = game.getCover();
    File imagefile = new File("./covers/" + filename);
    try
    {
      BufferedImage image = ImageIO.read(imagefile);
      Image newImage = image.getScaledInstance(125, 175, Image.SCALE_SMOOTH);
      label.setIcon(new ImageIcon(newImage));
    }
    catch (IOException e)
    {
      (label).setIcon(null);
    }
  }

  public void scrollToPosition()
  {
    //Scroll one game 
    scrollPane.getHorizontalScrollBar().setValue(200);
  }
}
