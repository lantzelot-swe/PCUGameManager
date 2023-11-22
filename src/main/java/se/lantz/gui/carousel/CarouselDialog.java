package se.lantz.gui.carousel;

import java.awt.Dimension;
import java.awt.Frame;

import se.lantz.gui.BaseDialog;

public class CarouselDialog extends BaseDialog
{
  private BackgroundPanel panel;

  public CarouselDialog(Frame owner)
  {
    super(owner);
    setTitle("Carousel preview");
    addContent(getPreferencesTabPanel());
    getOkButton().setPreferredSize(null);
    this.setPreferredSize(new Dimension(1400, 802));
    this.setResizable(false);
  }

  private BackgroundPanel getPreferencesTabPanel()
  {
    if (panel == null)
    {
      panel = new BackgroundPanel();
    }
    return panel;
  }
}
