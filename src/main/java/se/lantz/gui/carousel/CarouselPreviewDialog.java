package se.lantz.gui.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;
import se.lantz.model.MainViewModel;
import se.lantz.model.carousel.CarouselPreviewModel;
import se.lantz.util.InputController;

public class CarouselPreviewDialog extends BaseDialog
{
  private static final Logger logger = LoggerFactory.getLogger(CarouselPreviewDialog.class);

  private BackgroundPanel panel;
  private MainViewModel uiModel;
  private MainWindow mainWindow;
  private CarouselPreviewModel model;
  private JLabel gameListInfoLabel;
  //  private JButton runGameButton;
  private InputController inputController;

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
      @Override
      public void actionPerformed(ActionEvent e)
      {
        logger.debug("Reloading carousel");
        model.reloadCarousel();
      }
    };

  public CarouselPreviewDialog(final MainWindow owner, final MainViewModel uiModel)
  {
    super(owner);
    this.setUndecorated(true);
    this.model = new CarouselPreviewModel(uiModel);
    this.uiModel = uiModel;
    this.mainWindow = owner;
    this.setPreferredSize(new Dimension(1400, 760));
    this.setResizable(false);
    this.setModal(false);

    addContent(getBackgroundCenterPanel());
    getButtonPanel().setVisible(false);

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
  }

  private void modelChanged()
  {
    //    setTitle("Carousel preview - " + uiModel.getSelectedGameView().getName());
    gameListInfoLabel.setText("Carousel preview - " + uiModel.getSelectedGameView().getName());
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

    gameListInfoLabel = new JLabel("Test 123");
    gameListInfoLabel.setForeground(Color.white);
    gameListInfoLabel.setFont(new Font("Verdana", Font.PLAIN, 21));

    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.gridwidth = 1;
    gbc_infoLabel.anchor = GridBagConstraints.SOUTH;
    gbc_infoLabel.weighty = 0.0;
    gbc_infoLabel.weightx = 0.0;
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    gbc_infoLabel.fill = GridBagConstraints.VERTICAL;
    centerPanel.add(gameListInfoLabel, gbc_infoLabel);

    GridBagConstraints gbc_centerPanel = new GridBagConstraints();
    gbc_centerPanel.gridwidth = 1;
    gbc_centerPanel.anchor = GridBagConstraints.CENTER;
    gbc_centerPanel.weighty = 1.0;
    gbc_centerPanel.weightx = 1.0;
    gbc_centerPanel.gridx = 0;
    gbc_centerPanel.gridy = 1;
    centerPanel.add(getBackgroundPanel(), gbc_centerPanel);
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

  @Override
  public boolean showDialog()
  {
    inputController = new InputController(this);  
    return super.showDialog();
  }

  public void scrollRight()
  {
    if (this.isActive())
    {
      logger.debug("scrolling right!");
      getBackgroundPanel().getActionMap().get("scrollRight").actionPerformed(new ActionEvent(this, 0, ""));
      scrollRightTimer.restart();
    }
  }

  public void scrollUp()
  {
    if (this.isActive())
    {
      logger.debug("scrolling up!");
      getBackgroundPanel().getActionMap().get("pageUp").actionPerformed(new ActionEvent(this, 0, ""));
      scrollUpTimer.restart();
    }
  }

  public void scrollDown()
  {
    if (this.isActive())
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
    if (this.isActive())
    {
      getBackgroundPanel().getActionMap().get("scrollLeft").actionPerformed(new ActionEvent(this, 0, ""));
      scrollLeftTimer.restart();
    }
  }

  public void runGame()
  {
    if (this.isActive())
    {
      uiModel.runGameInVice(false);
    }
  }
}
