package se.lantz.gui.carousel;

import java.awt.Dimension;
import java.awt.Frame;
import java.beans.Beans;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;
import se.lantz.model.MainViewModel;
import se.lantz.model.carousel.CarouselPreviewModel;

public class CarouselPreviewDialog extends BaseDialog
{
  private BackgroundPanel panel;
  private MainViewModel uiModel;
  private MainWindow mainWindow;
  private CarouselPreviewModel model;

  public CarouselPreviewDialog(final MainWindow owner, final MainViewModel uiModel)
  {
    super(owner);
    this.model = new CarouselPreviewModel(uiModel);
    this.uiModel = uiModel;
    this.mainWindow = owner;
    addContent(getBackgroundPanel());
    getOkButton().setPreferredSize(null);
    getOkButton().setText("Close");
    getCancelButton().setVisible(false);
    this.setPreferredSize(new Dimension(1400, 802));
    this.setResizable(false);
    this.setModal(false);
    if (!Beans.isDesignTime())
    {
      uiModel.addPropertyChangeListener("selectedGamelistView", e -> modelChanged());
      model.addPropertyChangeListener(CarouselPreviewModel.CLOSE_PREVIEW, e -> getOkButton().doClick());
      //trigger once at startup
      modelChanged();
    }
    
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
}
