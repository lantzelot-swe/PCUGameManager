package se.lantz.gui.scraper;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.ScraperFields;
import se.lantz.util.MobyGamesScraper;

public class ScraperDialog extends BaseDialog
{
  private MobyGamesOptionsPanel mbyGamesPanel;
  private final MainViewModel model;
  public ScraperDialog(Frame owner, MainViewModel model)
  {
    super(owner);
    this.model = model;
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(getMobyGamesPanel(), BorderLayout.CENTER);
    addContent(content);
    setTitle("Scrape game information");
  }

  private MobyGamesOptionsPanel getMobyGamesPanel()
  {
    if (mbyGamesPanel == null)
    {
      mbyGamesPanel = new MobyGamesOptionsPanel(model, getOkButton());
    }
    return mbyGamesPanel;
  }
  
  public ScraperFields getScraperFields()
  {
    return getMobyGamesPanel().getScraperFields();
  }
}
