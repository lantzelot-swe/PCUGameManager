package se.lantz.gui.carousel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.MainWindow;
import se.lantz.gui.carousel.ScaledDialogDemo.ScaleLayerUI;
import se.lantz.model.MainViewModel;
import se.lantz.model.carousel.CarouselPreviewModel;
import se.lantz.util.FileManager;
import se.lantz.util.InputController;

public class CarouselPreviewDialog extends JDialog
{
  private static final long serialVersionUID = 4650999066589598344L;

  private static final Logger logger = LoggerFactory.getLogger(CarouselPreviewDialog.class);

  private BackgroundPanel panel;
  private MainViewModel uiModel;
  private MainWindow mainWindow;
  private CarouselPreviewModel model;
  private InputController inputController;
  private ScaleLayerUI scaleLayerUi;
  private JLayer<JComponent> layerComponent;

  //Timers for continuous scroll when holding joystick/USB device left or right
  Timer scrollRightTimer = new Timer(100,
                                     e -> getBackgroundPanel().getActionMap().get("scrollRight")
                                       .actionPerformed(new ActionEvent(this, 0, "")));
  Timer scrollLeftTimer =
    new Timer(100,
              e -> getBackgroundPanel().getActionMap().get("scrollLeft").actionPerformed(new ActionEvent(this, 0, "")));
  Timer scrollUpTimer =
    new Timer(100,
              e -> getBackgroundPanel().getActionMap().get("pageUp").actionPerformed(new ActionEvent(this, 0, "")));
  Timer scrollDownTimer =
    new Timer(100,
              e -> getBackgroundPanel().getActionMap().get("pageDown").actionPerformed(new ActionEvent(this, 0, "")));

  private Action reloadAction = new AbstractAction()
    {
      private final Timer cooldownTimer = new Timer(1000, event -> {
        // Cooldown finished: Re-enable the action so it can be triggered again
        setEnabled(true);
      });
      {
        // Configure the timer to only run once per trigger, not loop infinitely
        cooldownTimer.setRepeats(false);
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
        layerComponent.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setEnabled(false);
        logger.debug("Reloading carousel");
        model.reloadCarousel();
        logger.debug("Rescan of input controllers");
        inputController.scanAvailableControllers();
        cooldownTimer.start();
        layerComponent.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    };

  private boolean fullscreen;

  private int currentZoomLevel = 100;
  //Max zoom level, calculated when the dialog is shown based on the screen resolution
  private int maxZoomLevel = 100;
  //Min zoom level, set a default value.
  private int minZoomLevel = 50;

  KeyStroke escKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  KeyStroke zoomInKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ADD, KeyEvent.CTRL_DOWN_MASK);
  KeyStroke zoomOutKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, KeyEvent.CTRL_DOWN_MASK);

  Action escAction = new AbstractAction()
    {
      private static final long serialVersionUID = 1756396251349970052L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        model.updateCarouselZoomLevel(currentZoomLevel);
        setVisible(false);
      }
    };

  Action zoomInAction = new AbstractAction()
    {
      private static final long serialVersionUID = 1756396251349970052L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        //Zoom in
        if (currentZoomLevel < (maxZoomLevel - 1))
        {
          currentZoomLevel = currentZoomLevel + 2;
          updateScaling();
        }
      }
    };

  Action zoomOutAction = new AbstractAction()
    {
      private static final long serialVersionUID = 1756396251349970052L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        //Zoom out
        if (currentZoomLevel > (minZoomLevel + 1))
        {
          currentZoomLevel = currentZoomLevel - 2;
          updateScaling();
        }
      }
    };

  public CarouselPreviewDialog(final MainWindow owner, final MainViewModel uiModel, final boolean fullscreen)
  {
    super(owner);
    this.fullscreen = fullscreen;

    this.model = new CarouselPreviewModel(uiModel);
    this.uiModel = uiModel;
    this.mainWindow = owner;

    this.setResizable(false);
    this.setModal(false);

    //The scale layer UI
    scaleLayerUi = new ScaleLayerUI(1.0);

    JPanel content;

    if (fullscreen)
    {
      content = getBackgroundCenterPanel();
    }
    else
    {
      content = getBackgroundPanel();
      getBackgroundPanel().addMouseWheelListener(e -> getBackgroundPanel().mouseWheelScrolled(e));
    }

    layerComponent = new JLayer<>(content, scaleLayerUi);
    this.add(layerComponent, BorderLayout.CENTER);

    getBackgroundPanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "reload");
    getBackgroundPanel().getActionMap().put("reload", reloadAction);

    if (!Beans.isDesignTime())
    {
      uiModel.addPropertyChangeListener("selectedGamelistView", e -> modelChanged());
      model.addPropertyChangeListener(CarouselPreviewModel.CLOSE_PREVIEW, e -> dispose());
      //trigger once at startup
      modelChanged();
    }
    //An initial delay so that a short joystick movement only triggers one scroll
    scrollRightTimer.setInitialDelay(500);
    scrollLeftTimer.setInitialDelay(500);
    scrollUpTimer.setInitialDelay(500);
    scrollDownTimer.setInitialDelay(500);

    //Register esc as closing the window
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escKeyStroke, "closeDialog");
    getRootPane().getActionMap().put("closeDialog", escAction);
    //Register zoom actions
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(zoomInKeyStroke, "zoomIn");
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(zoomOutKeyStroke, "zoomOut");

    getRootPane().getActionMap().put("zoomIn", zoomInAction);
    getRootPane().getActionMap().put("zoomOut", zoomOutAction);

    //Set stored zoom level
    currentZoomLevel = FileManager.getCarouselZoomLevel();
    updateScaling();
  }

  private DisplayMode getCurrentScreenDisplayMode()
  {
    GraphicsConfiguration config = getGraphicsConfiguration();

    if (config != null)
    {
      //Get the device (monitor) associated with that configuration
      GraphicsDevice device = config.getDevice();

      //Extract the resolution (DisplayMode)
      DisplayMode mode = device.getDisplayMode();
      return mode;
    }
    else
    {
      logger.warn("Dialog is not yet peer-connected to a screen.");
    }
    return null;
  }

  private void modelChanged()
  {
    if (!fullscreen)
    {
      setTitle("Carousel preview - " + uiModel.getSelectedGameView().getName());
    }
  }

  private BackgroundPanel getBackgroundPanel()
  {
    if (panel == null)
    {
      panel = new BackgroundPanel(model, mainWindow);
    }
    return panel;
  }

  private JPanel getBackgroundCenterPanel()
  {
    JPanel centerPanel = new JPanel();
    centerPanel.setBackground(Color.black);
    GridBagLayout gridBagLayout = new GridBagLayout();
    centerPanel.setLayout(gridBagLayout);

    GridBagConstraints gbc_centerPanel = new GridBagConstraints();
    gbc_centerPanel.gridwidth = 1;
    gbc_centerPanel.anchor = GridBagConstraints.CENTER;
    gbc_centerPanel.weighty = 1.0;
    gbc_centerPanel.weightx = 1.0;
    gbc_centerPanel.gridx = 0;
    gbc_centerPanel.gridy = 0;
    centerPanel.add(getBackgroundPanel(), gbc_centerPanel);
    //Add mouseWheel listener so that scrolling the mouse always work
    centerPanel.addMouseWheelListener(e -> getBackgroundPanel().mouseWheelScrolled(e));
    return centerPanel;
  }

  @Override
  public void setVisible(boolean visible)
  {
    super.setVisible(visible);
    if (visible)
    {
      getBackgroundPanel().initialScroll();
    }
    else
    {
      dispose();
    }
  }

  @Override
  public void dispose()
  {
    if (inputController != null)
    {
      inputController.stop();
    }
    model.dispose();
    super.dispose();
  }

  public void showDialog()
  {
    inputController = new InputController(this);
    this.setVisible(true);

    //Calculate max value for zooming in, based on screen size
    DisplayMode mode = getCurrentScreenDisplayMode();
    int screenWidth = mode.getWidth();
    int screenHeight = mode.getHeight();
    double unscaledWidth = 1385.0;
    double unscaledHeight = 721.0;
    double widthRatio = screenWidth / unscaledWidth;
    double heightRatio = screenHeight / unscaledHeight;

    double maxSliderValue = Math.min(widthRatio, heightRatio);
    this.maxZoomLevel = (int) (maxSliderValue * 100.0);
    //Set current level if max is smaller than current value.
    this.currentZoomLevel = Math.min(currentZoomLevel, maxZoomLevel);
  }

  public void scrollRight()
  {
    if (this.isActive() || mainWindow.isActive())
    {
      logger.debug("scrolling right!");
      getBackgroundPanel().getActionMap().get("scrollRight").actionPerformed(new ActionEvent(this, 0, ""));
      scrollRightTimer.restart();
    }
  }

  public void scrollUp()
  {
    if (this.isActive() || mainWindow.isActive())
    {
      logger.debug("scrolling up!");
      getBackgroundPanel().getActionMap().get("pageUp").actionPerformed(new ActionEvent(this, 0, ""));
      scrollUpTimer.restart();
    }
  }

  public void scrollDown()
  {
    if (this.isActive() || mainWindow.isActive())
    {
      logger.debug("scrolling down!");
      getBackgroundPanel().getActionMap().get("pageDown").actionPerformed(new ActionEvent(this, 0, ""));
      scrollDownTimer.restart();
    }
  }

  public void stopScroll()
  {
    logger.debug("scrolling stop!");
    scrollRightTimer.stop();
    scrollLeftTimer.stop();
    scrollUpTimer.stop();
    scrollDownTimer.stop();
  }

  public void scrollLeft()
  {
    if (this.isActive() || mainWindow.isActive())
    {
      getBackgroundPanel().getActionMap().get("scrollLeft").actionPerformed(new ActionEvent(this, 0, ""));
      scrollLeftTimer.restart();
    }
  }

  public void runGame()
  {
    if (this.isActive() || mainWindow.isActive())
    {
      getBackgroundPanel().getActionMap().get("runGame").actionPerformed(new ActionEvent(this, 0, ""));
    }
  }

  private void updateScaling()
  {
    scaleLayerUi.setScale(currentZoomLevel / 100.0);
    if (!fullscreen)
    {
      pack();
    }
  }
}
