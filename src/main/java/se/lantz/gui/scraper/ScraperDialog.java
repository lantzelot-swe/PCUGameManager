package se.lantz.gui.scraper;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;
import se.lantz.manager.ScraperManager;
import se.lantz.model.data.ScraperFields;

public class ScraperDialog extends BaseDialog
{
  private MobyGamesOptionsPanel mbyGamesPanel;
  private final ScraperManager scraper;
  public ScraperDialog(Frame owner, ScraperManager scraper)
  {
    super(owner);
    this.scraper = scraper;
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(getMobyGamesPanel(), BorderLayout.CENTER);
    addContent(content);
    setTitle("Scrape game information");
    this.setResizable(false);
  }

  private MobyGamesOptionsPanel getMobyGamesPanel()
  {
    if (mbyGamesPanel == null)
    {
      mbyGamesPanel = new MobyGamesOptionsPanel(scraper, getOkButton());
    }
    return mbyGamesPanel;
  }
  
  public ScraperFields getScraperFields()
  {
    return getMobyGamesPanel().getScraperFields();
  }
}
