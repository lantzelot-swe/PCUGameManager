package se.lantz.gui.scraper;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;
import se.lantz.manager.ScraperManager;
import se.lantz.manager.ScraperManager.SCRAPER;
import se.lantz.model.data.ScraperFields;

public class ScraperDialog extends BaseDialog
{
  private static final String MOBY = "moby";
  private static final String C64COM = "c64com";
  private static final String GB64COM = "gb64com";
  private static final String WWW_C64_COM = "www.c64.com";
  private static final String WWW_MOBYGAMES_COM = "www.mobygames.com";
  private static final String WWW_GAMEBASE_COM = "www.gb64.com";
  private MobyGamesOptionsPanel mobyGamesPanel;
  private C64comOptionsPanel c64comPanel;
  private Gb64comOptionsPanel gb64comPanel;
  private final ScraperManager scraper;
  private CardLayout cardLayout = new CardLayout();
  private JPanel scraperSelectionPanel;
  private JLabel scraperInfoLabel;
  private JComboBox scraperComboBox;
  private JPanel cardPanel;

  public ScraperDialog(Frame owner, ScraperManager scraper)
  {
    super(owner);
    setTitle("Scrape game information");
    this.scraper = scraper;
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    cardPanel = new JPanel();
    cardPanel.setLayout(cardLayout);
    cardPanel.add(getMobyGamesPanel(), MOBY);
    cardPanel.add(getC64comPanel(), C64COM);
    cardPanel.add(getGb64comPanel(), GB64COM);
    content.add(cardPanel, BorderLayout.CENTER);
    addContent(content);
    content.add(getScraperSelectionPanel(), BorderLayout.NORTH);
    this.setResizable(false);
  }

  private MobyGamesOptionsPanel getMobyGamesPanel()
  {
    if (mobyGamesPanel == null)
    {
      mobyGamesPanel = new MobyGamesOptionsPanel(scraper, getOkButton());
    }
    return mobyGamesPanel;
  }

  private C64comOptionsPanel getC64comPanel()
  {
    if (c64comPanel == null)
    {
      c64comPanel = new C64comOptionsPanel(scraper, getOkButton());
    }
    return c64comPanel;
  }
  
  private Gb64comOptionsPanel getGb64comPanel()
  {
    if (gb64comPanel == null)
    {
      gb64comPanel = new Gb64comOptionsPanel(scraper, getOkButton());
    }
    return gb64comPanel;
  }

  public ScraperFields getScraperFields()
  {
    if (getScraperComboBox().getSelectedItem().equals(WWW_MOBYGAMES_COM))
    {
      return getMobyGamesPanel().getScraperFields();
    }
    else if (getScraperComboBox().getSelectedItem().equals(WWW_GAMEBASE_COM))
    {
      return getGb64comPanel().getScraperFields();
    }
    else
    {
      return getC64comPanel().getScraperFields();
    }
  }

  @Override
  public boolean showDialog()
  {
    getMobyGamesPanel().getUrlTextField().requestFocusInWindow();
    this.getRootPane().setDefaultButton(getMobyGamesPanel().getConnectButton());

    return super.showDialog();
  }

  private JPanel getScraperSelectionPanel()
  {
    if (scraperSelectionPanel == null)
    {
      scraperSelectionPanel = new JPanel();
      GridBagLayout gbl_scraperSelectionPanel = new GridBagLayout();
      scraperSelectionPanel.setLayout(gbl_scraperSelectionPanel);
      GridBagConstraints gbc_scraperInfoLabel = new GridBagConstraints();
      gbc_scraperInfoLabel.insets = new Insets(10, 10, 5, 5);
      gbc_scraperInfoLabel.gridx = 0;
      gbc_scraperInfoLabel.gridy = 0;
      scraperSelectionPanel.add(getScraperInfoLabel(), gbc_scraperInfoLabel);
      GridBagConstraints gbc_scraperComboBox = new GridBagConstraints();
      gbc_scraperComboBox.anchor = GridBagConstraints.WEST;
      gbc_scraperComboBox.weightx = 1.0;
      gbc_scraperComboBox.insets = new Insets(10, 0, 5, 5);
      gbc_scraperComboBox.gridx = 1;
      gbc_scraperComboBox.gridy = 0;
      scraperSelectionPanel.add(getScraperComboBox(), gbc_scraperComboBox);
    }
    return scraperSelectionPanel;
  }

  private JLabel getScraperInfoLabel()
  {
    if (scraperInfoLabel == null)
    {
      scraperInfoLabel = new JLabel("Select a site to scrape:");
    }
    return scraperInfoLabel;
  }

  private JComboBox getScraperComboBox()
  {
    if (scraperComboBox == null)
    {
      scraperComboBox = new JComboBox();
      scraperComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            if (scraperComboBox.getSelectedItem().equals(WWW_MOBYGAMES_COM))
            {
              cardLayout.show(cardPanel, MOBY);
              scraper.setScrapertoUse(SCRAPER.moby);
              getRootPane().setDefaultButton(getMobyGamesPanel().getConnectButton());
            }
            else if (scraperComboBox.getSelectedItem().equals(WWW_C64_COM))
            {
              cardLayout.show(cardPanel, C64COM);
              scraper.setScrapertoUse(SCRAPER.c64com);
              getRootPane().setDefaultButton(getC64comPanel().getConnectButton());
            }
            else
            {
              cardLayout.show(cardPanel, GB64COM);
              scraper.setScrapertoUse(SCRAPER.gamebase);
              getRootPane().setDefaultButton(getGb64comPanel().getConnectButton());
            }
          }
        });
      scraperComboBox.addItem(WWW_MOBYGAMES_COM);
      scraperComboBox.addItem(WWW_GAMEBASE_COM);
      scraperComboBox.addItem(WWW_C64_COM);
    }
    return scraperComboBox;
  }
}
