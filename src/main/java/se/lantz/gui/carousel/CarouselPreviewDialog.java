package se.lantz.gui.carousel;

import java.awt.Dimension;
import java.awt.Frame;
import java.beans.Beans;

import se.lantz.gui.BaseDialog;
import se.lantz.model.MainViewModel;

public class CarouselPreviewDialog extends BaseDialog
{
  private BackgroundPanel panel;
  private MainViewModel uiModel;

  public CarouselPreviewDialog(final Frame owner, final MainViewModel uiModel)
  {
    super(owner);
    this.uiModel = uiModel;
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
      panel = new BackgroundPanel(uiModel);
    }
    return panel;
  }
}
