package se.lantz.gui.scraper;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;

public class CoverSelectionDialog extends BaseDialog
{
  private CoverSelectionPanel mbyGamesPanel;
  private List<BufferedImage> coverInfoList;

  public CoverSelectionDialog(Frame owner, List<BufferedImage> coverInfoList)
  {
    super(owner);
    this.coverInfoList = coverInfoList;
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(getCoverSelectionPanel(), BorderLayout.CENTER);
    addContent(content);
    setTitle("Scrape game information");
    this.setResizable(false);
  }

  private CoverSelectionPanel getCoverSelectionPanel()
  {
    if (mbyGamesPanel == null)
    {
      mbyGamesPanel = new CoverSelectionPanel(coverInfoList);
    }
    return mbyGamesPanel;
  }

  public BufferedImage getSelectedCover()
  {
    return getCoverSelectionPanel().getSelectedCover();
  }
}
