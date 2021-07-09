package se.lantz.gui.scraper;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;

public class ScreenshotsSelectionDialog extends BaseDialog
{
  private ScreenshotsSelectionPanel mbyGamesPanel;
  private List<BufferedImage> screenshotInfoList;

  public ScreenshotsSelectionDialog(Frame owner, List<BufferedImage> screenshotInfoList)
  {
    super(owner);
    this.screenshotInfoList = screenshotInfoList;
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(getScreenshotsSelectionPanel(), BorderLayout.CENTER);
    addContent(content);
    setTitle("Scrape game information");
    this.setResizable(false);
  }

  private ScreenshotsSelectionPanel getScreenshotsSelectionPanel()
  {
    if (mbyGamesPanel == null)
    {
      mbyGamesPanel = new ScreenshotsSelectionPanel(screenshotInfoList, this.getOkButton());
    }
    return mbyGamesPanel;
  }

  public List<BufferedImage> getSelectedScreenshots()
  {
    return getScreenshotsSelectionPanel().getSelectedScreenshots();
  }
}
