package se.lantz.gui.carousel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;
import se.lantz.model.MainViewModel;
import se.lantz.model.carousel.CarouselPreviewModel;

public class CarouselPreviewDialog extends BaseDialog
{
  private static final Logger logger = LoggerFactory.getLogger(CarouselPreviewDialog.class);
  
  private BackgroundPanel panel;
  private MainViewModel uiModel;
  private MainWindow mainWindow;
  private CarouselPreviewModel model;
  private JButton runGameButton;

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
    this.model = new CarouselPreviewModel(uiModel);
    this.uiModel = uiModel;
    this.mainWindow = owner;
    this.setPreferredSize(new Dimension(1400, 760));
    this.setResizable(false);
    this.setModal(false);
    addContent(getBackgroundPanel());
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
  }

  private JButton getRunGameButton()
  {
    if (runGameButton == null)
    {
      runGameButton = new JButton("Run selected game");
      runGameButton.setPreferredSize(null);

    }
    return runGameButton;
  }

  private void modelChanged()
  {
    setTitle("Carousel preview - " + uiModel.getSelectedGameView().getName());
  }

  private BackgroundPanel getBackgroundPanel()
  {
    if (panel == null)
    {
      panel = new BackgroundPanel(model, mainWindow);
    }
    return panel;
  }

  @Override
  public void setVisible(boolean visible)
  {
    super.setVisible(visible);
    if (visible)
    {
      getBackgroundPanel().initialScroll();
    }
  }

  @Override
  public void dispose()
  {
    model.dispose();
    super.dispose();
  }

}
