package se.lantz.gui.carousel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import se.lantz.gui.MainWindow;
import se.lantz.model.carousel.CarouselPreviewModel;
import se.lantz.util.FileManager;

public class BackgroundPanel extends JPanel
{
  private boolean screen1Showing = true;
  private ActionListener screenshotSwitchAction = e -> loadScreenshot(!screen1Showing);
  private Timer screenshotRotationTimer = new Timer(4000, screenshotSwitchAction);
  private CarouselPreviewModel model;
  private MainWindow mainWindow;

  private Image background;
  private JLabel screenShotLabel;
  private TextPanel textPanel;
  private CoverPanel coverPanel;

  public BackgroundPanel(final CarouselPreviewModel model, final MainWindow mainWindow)
  {
    this.mainWindow = mainWindow;
    this.model = model;
    this.setPreferredSize(new Dimension(1385, 721));
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_coverPanel = new GridBagConstraints();
    gbc_coverPanel.gridwidth = 2;
    gbc_coverPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_coverPanel.weighty = 1.0;
    gbc_coverPanel.weightx = 1.0;
    gbc_coverPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_coverPanel.gridx = 0;
    gbc_coverPanel.gridy = 1;
    add(getCoverPanel(), gbc_coverPanel);
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.insets = new Insets(29, 37, 22, 10);
    gbc_panel.fill = GridBagConstraints.BOTH;
    gbc_panel.anchor = GridBagConstraints.NORTHWEST;
    gbc_panel.gridx = 0;
    gbc_panel.gridy = 0;
    add(getScreenshotLabel(), gbc_panel);
    GridBagConstraints gbc_textPanel = new GridBagConstraints();
    gbc_textPanel.weightx = 1.0;
    gbc_textPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_textPanel.fill = GridBagConstraints.BOTH;
    gbc_textPanel.gridx = 1;
    gbc_textPanel.gridy = 0;
    add(getTextPanel(), gbc_textPanel);

    setBackground("/se/lantz/carousel/Carousel1400x788.png");

    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(CarouselPreviewModel.SELECTED_GAME, e -> reloadScreens());
      //trigger once at startup
      reloadScreens();
    }

    this.addMouseWheelListener(a -> getCoverPanel().scrollOneGame(a.getWheelRotation() > 0));

    this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "runGame");
    this.getActionMap().put("runGame", new AbstractAction()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          model.runCurrentGame();
        }
      });

    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "scrollRight");
    getActionMap().put("scrollRight", new AbstractAction()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          getCoverPanel().scrollOneGame(true);
        }
      });

    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "scrollLeft");
    getActionMap().put("scrollLeft", new AbstractAction()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          getCoverPanel().scrollOneGame(false);
        }
      });
    
    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "pageUp");
    getActionMap().put("pageUp", new AbstractAction()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          getCoverPanel().pageUpTriggered();
        }
      });
      getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "pageDown");
      getActionMap().put("pageDown", new AbstractAction()
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            getCoverPanel().pageDownTriggered();
          }
        });
  }

  private void reloadScreens()
  {
    if (model.getSelectedGame() == null)
    {
      return;
    }
    loadScreenshot(true);
    screenshotRotationTimer.restart();
  }
  
  private void loadScreenshot(boolean screen1)
  {
    String filename = screen1 ? model.getSelectedGame().getScreen1() : model.getSelectedGame().getScreen2();
    BufferedImage image = null;
    if (!filename.isEmpty())
    {
      File imagefile = new File(FileManager.SCREENS + filename);
      try
      {
        image = ImageIO.read(imagefile);
        Image newImage = image.getScaledInstance(695, 402, Image.SCALE_SMOOTH);
        getScreenshotLabel().setIcon(new ImageIcon(newImage));
      }
      catch (IOException e)
      {
        getScreenshotLabel().setIcon(null);
      }
    }
    else
    {
      getScreenshotLabel().setIcon(null);
    }
    screen1Showing = screen1;
  }

  public void paintComponent(Graphics g)
  {

    int width = this.getSize().width;
    int height = this.getSize().height;

    if (this.background != null)
    {
      g.drawImage(this.background, 0, 0, width, height, null);
    }

    super.paintComponent(g);
  }

  public void setBackground(String imagePath)
  {

    this.setOpaque(false);
    this.background = new ImageIcon(getClass().getResource(imagePath)).getImage();
    repaint();
  }

  private JLabel getScreenshotLabel()
  {
    if (screenShotLabel == null)
    {
      screenShotLabel = new JLabel();
    }
    return screenShotLabel;
  }

  private TextPanel getTextPanel()
  {
    if (textPanel == null)
    {
      textPanel = new TextPanel(model);
    }
    return textPanel;
  }

  private CoverPanel getCoverPanel()
  {
    if (coverPanel == null)
    {
      coverPanel = new CoverPanel(model, this.mainWindow);
    }
    return coverPanel;
  }

  public void initialScroll()
  {
    //Scroll one game 
    getCoverPanel().scrollToPosition();
  }
}
