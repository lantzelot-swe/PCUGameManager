package se.lantz.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.scraper.ScraperDialog;
import se.lantz.gui.scraper.ScreenshotsSelectionDialog;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.ScraperFields;

public class GameDetailsBackgroundPanel extends JPanel
{
  private static final String EMPTY = "empty";
  private static final String DETAILS = "details";
  private static final Logger logger = LoggerFactory.getLogger(GameDetailsBackgroundPanel.class);
  private MainViewModel model;
  private JPanel settingsPanel;
  private InfoPanel infoPanel;
  private CombinedJoystickPanel joystickPanel;
  private SystemPanel systemPanel;
  private JPanel buttonPanel;
  private JButton saveButton;

  private JPanel emptyPanel;
  private JPanel detailsPanel;
  private CardLayout cardLayout;
  private JButton scrapeButton;

  public GameDetailsBackgroundPanel(MainViewModel model)
  {
    this.model = model;
    this.setMinimumSize(new Dimension(1250, 800));
    cardLayout = new CardLayout();
    setLayout(cardLayout);
    add(getEmptyPanel(), EMPTY);
    add(getDetailsPanel(), DETAILS);
  }

  void focusTitleField()
  {
    getInfoPanel().focusTitleField();
  }

  void updateSelectedGame(GameListData data)
  {
    scraperDialog = null;
    if (data == null)
    {
      cardLayout.show(this, EMPTY);
    }
    else
    {
      cardLayout.show(this, DETAILS);
      model.readGameDetails(data);
    }
  }

  private JPanel getDetailsPanel()
  {
    if (detailsPanel == null)
    {
      detailsPanel = new JPanel();
      detailsPanel.setLayout(new BorderLayout(0, 0));
      detailsPanel.add(getSettingsPanel(), BorderLayout.CENTER);
      detailsPanel.add(getInfoPanel(), BorderLayout.NORTH);
      detailsPanel.add(getButtonPanel(), BorderLayout.SOUTH);
    }
    return detailsPanel;
  }

  private JPanel getEmptyPanel()
  {
    if (emptyPanel == null)
    {
      emptyPanel = new JPanel();
    }
    return emptyPanel;
  }

  private JPanel getSettingsPanel()
  {
    if (settingsPanel == null)
    {
      settingsPanel = new JPanel();
      GridBagLayout gbl_settingsPanel = new GridBagLayout();
      settingsPanel.setLayout(gbl_settingsPanel);
      GridBagConstraints gbc_systemPanel = new GridBagConstraints();
      gbc_systemPanel.weightx = 1.0;
      gbc_systemPanel.weighty = 1.0;
      gbc_systemPanel.anchor = GridBagConstraints.NORTH;
      gbc_systemPanel.fill = GridBagConstraints.BOTH;
      gbc_systemPanel.insets = new Insets(0, 0, 0, 5);
      gbc_systemPanel.gridx = 1;
      gbc_systemPanel.gridy = 0;
      settingsPanel.add(getSystemPanel(), gbc_systemPanel);
      GridBagConstraints gbc_joystickPanel = new GridBagConstraints();
      gbc_joystickPanel.insets = new Insets(0, 10, 0, 0);
      gbc_joystickPanel.weighty = 1.0;
      gbc_joystickPanel.anchor = GridBagConstraints.NORTH;
      gbc_joystickPanel.fill = GridBagConstraints.BOTH;
      gbc_joystickPanel.gridx = 0;
      gbc_joystickPanel.gridy = 0;
      settingsPanel.add(getCombinedJoystickPanel(), gbc_joystickPanel);
    }
    return settingsPanel;
  }

  private InfoPanel getInfoPanel()
  {
    if (infoPanel == null)
    {
      infoPanel = new InfoPanel(model.getInfoModel());
    }
    return infoPanel;
  }

  private CombinedJoystickPanel getCombinedJoystickPanel()
  {
    if (joystickPanel == null)
    {
      joystickPanel = new CombinedJoystickPanel(model);
    }
    return joystickPanel;
  }

  private SystemPanel getSystemPanel()
  {
    if (systemPanel == null)
    {
      systemPanel = new SystemPanel(model.getSystemModel());
      systemPanel
        .setBorder(new TitledBorder(null, "System Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    }
    return systemPanel;
  }

  private JPanel getButtonPanel()
  {
    if (buttonPanel == null)
    {
      buttonPanel = new JPanel();
      GridBagLayout gbl_buttonPanel = new GridBagLayout();
      gbl_buttonPanel.columnWidths = new int[] { 0, 0, 0 };
      gbl_buttonPanel.rowHeights = new int[] { 0, 0 };
      gbl_buttonPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
      gbl_buttonPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
      buttonPanel.setLayout(gbl_buttonPanel);
      GridBagConstraints gbc_scrapeButton = new GridBagConstraints();
      gbc_scrapeButton.weighty = 1.0;
      gbc_scrapeButton.anchor = GridBagConstraints.SOUTHWEST;
      gbc_scrapeButton.insets = new Insets(5, 10, 5, 5);
      gbc_scrapeButton.gridx = 0;
      gbc_scrapeButton.gridy = 0;
      buttonPanel.add(getScrapeButton(), gbc_scrapeButton);
      GridBagConstraints gbc_saveButton = new GridBagConstraints();
      gbc_saveButton.weighty = 1.0;
      gbc_saveButton.weightx = 1.0;
      gbc_saveButton.anchor = GridBagConstraints.SOUTHEAST;
      gbc_saveButton.insets = new Insets(5, 5, 5, 6);
      gbc_saveButton.gridx = 1;
      gbc_saveButton.gridy = 0;
      buttonPanel.add(getSaveButton(), gbc_saveButton);
    }
    return buttonPanel;
  }

  private JButton getSaveButton()
  {
    if (saveButton == null)
    {
      model.addSaveChangeListener(e -> {
        saveButton.setEnabled(model.isDataChanged());
      });
      saveButton = new JButton("Save");
      saveButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            model.saveData();
          }
        });
    }
    return saveButton;
  }
  private JButton getScrapeButton() {
    if (scrapeButton == null) {
    	scrapeButton = new JButton("Scrape...");
    	scrapeButton.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    scrapeGamesInformation();
    	  }
    	});
    }
    return scrapeButton;
  }
  
  private ScraperDialog scraperDialog = null;
  
  private void scrapeGamesInformation()
  {
    if (scraperDialog == null)
    {
      scraperDialog = new ScraperDialog(MainWindow.getInstance(), model);
      scraperDialog.pack();
      scraperDialog.setLocationRelativeTo(MainWindow.getInstance());
    }
    
    if (scraperDialog.showDialog())
    {
      MainWindow.getInstance().setWaitCursor(true);
      ScraperFields scraperFields = scraperDialog.getScraperFields();
      model.scrapeGameInformation(scraperFields);
      MainWindow.getInstance().setWaitCursor(false);
      if (scraperFields.isScreenshots())
      {
        //Scrape the screens and check how many there are.
        List<BufferedImage> screenshots =  model.scrapeScreenshots();
        if (screenshots.size() > 2)
        {
          //Show dialog for selecting screenshots
          ScreenshotsSelectionDialog screenDialog = new ScreenshotsSelectionDialog(MainWindow.getInstance(), screenshots);
          screenDialog.pack();
          screenDialog.setLocationRelativeTo(MainWindow.getInstance());
          if (screenDialog.showDialog())
          {
            List<BufferedImage> selectedScreenshots = screenDialog.getSelectedScreenshots();
            model.setScreenshotImages(selectedScreenshots.get(0), selectedScreenshots.get(1));
          }
        }
        else
        {
          //TODO
        }
      }
    }
    
  }
}
