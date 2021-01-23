package se.lantz.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.scraper.ScraperDialog;
import se.lantz.gui.scraper.ScraperProgressDialog;
import se.lantz.gui.scraper.ScraperWorker;
import se.lantz.gui.scraper.ScreenshotsSelectionDialog;
import se.lantz.manager.ScraperManager;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.ScraperFields;

public class GameDetailsBackgroundPanel extends JPanel
{
  private static final String EMPTY = "empty";
  private static final String DETAILS = "details";
  private static final Logger logger = LoggerFactory.getLogger(GameDetailsBackgroundPanel.class);
  private final MainViewModel model;
  private JPanel settingsPanel;
  private InfoPanel infoPanel;
  private CombinedJoystickPanel joystickPanel;
  private SystemPanel systemPanel;
  private JPanel buttonPanel;
  private JButton saveButton;
  private ScraperDialog scraperDialog = null;
  private JButton runButton;
  private JButton btnNewButton;
  private JPanel emptyPanel;
  private JPanel detailsPanel;
  private CardLayout cardLayout;
  private JButton scrapeButton;
  private Timer cursorTimer;
  private ActionListener defaultCursorAction = e -> {
    MainWindow.getInstance().setWaitCursor(false);
    cursorTimer.stop();
  };
  private Action runGameAction = new AbstractAction("Run Game")
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (runButton.isEnabled())
        {
          MainWindow.getInstance().setWaitCursor(true);
          cursorTimer.start();
          model.runGameInVice();
        }
      }
    };

  private final ScraperManager scraperManager;

  public GameDetailsBackgroundPanel(MainViewModel model)
  {
    this.model = model;
    this.scraperManager = new ScraperManager(model);
    this.setMinimumSize(new Dimension(1250, 800));
    cardLayout = new CardLayout();
    setLayout(cardLayout);
    add(getEmptyPanel(), EMPTY);
    add(getDetailsPanel(), DETAILS);
    model.addSaveChangeListener(e -> {
      saveButton.setEnabled(model.isDataChanged());
      runButton.setEnabled(!model.getInfoModel().getGamesFile().isEmpty());
    });
    cursorTimer = new Timer(500, defaultCursorAction);
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

  protected InfoPanel getInfoPanel()
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
      buttonPanel.setLayout(gbl_buttonPanel);
      GridBagConstraints gbc_scrapeButton = new GridBagConstraints();
      gbc_scrapeButton.weighty = 1.0;
      gbc_scrapeButton.anchor = GridBagConstraints.SOUTHWEST;
      gbc_scrapeButton.insets = new Insets(5, 10, 5, 5);
      gbc_scrapeButton.gridx = 0;
      gbc_scrapeButton.gridy = 0;
      buttonPanel.add(getScrapeButton(), gbc_scrapeButton);
      GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
      gbc_btnNewButton.insets = new Insets(5, 10, 5, 5);
      gbc_btnNewButton.gridx = 1;
      gbc_btnNewButton.gridy = 0;
      buttonPanel.add(getBtnNewButton(), gbc_btnNewButton);
      GridBagConstraints gbc_saveButton = new GridBagConstraints();
      gbc_saveButton.weighty = 1.0;
      gbc_saveButton.anchor = GridBagConstraints.SOUTHEAST;
      gbc_saveButton.insets = new Insets(5, 5, 5, 6);
      gbc_saveButton.gridx = 3;
      gbc_saveButton.gridy = 0;
      buttonPanel.add(getSaveButton(), gbc_saveButton);
      GridBagConstraints gbc_runButton = new GridBagConstraints();
      gbc_runButton.anchor = GridBagConstraints.EAST;
      gbc_runButton.weighty = 1.0;
      gbc_runButton.weightx = 1.0;
      gbc_runButton.insets = new Insets(5, 0, 5, 10);
      gbc_runButton.gridx = 2;
      gbc_runButton.gridy = 0;
      buttonPanel.add(getRunButton(), gbc_runButton);
    }
    return buttonPanel;
  }

  private JButton getSaveButton()
  {
    if (saveButton == null)
    {
      saveButton = new JButton("Save");
      KeyStroke keySave = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
      Action performSave = new AbstractAction("Save")
        {
          public void actionPerformed(ActionEvent e)
          {
            saveButton.doClick();
          }
        };
      saveButton.getActionMap().put("performSave", performSave);
      saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keySave, "performSave");

      saveButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            if (model.saveData())
            {
              getInfoPanel().getScreensPanel().resetWhenSaved();
            }
          }
        });
    }
    return saveButton;
  }

  private JButton getScrapeButton()
  {
    if (scrapeButton == null)
    {
      scrapeButton = new JButton("Scrape...");
      scrapeButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            scrapeGamesInformation();
          }
        });
    }
    return scrapeButton;
  }

  private void scrapeGamesInformation()
  {
    if (scraperDialog == null)
    {
      scraperDialog = new ScraperDialog(MainWindow.getInstance(), scraperManager);
      scraperDialog.pack();
      scraperDialog.setLocationRelativeTo(MainWindow.getInstance());
    }

    if (scraperDialog.showDialog())
    {
      MainWindow.getInstance().setWaitCursor(true);

      ScraperFields scraperFields = scraperDialog.getScraperFields();
      ScraperProgressDialog dialog = new ScraperProgressDialog(MainWindow.getInstance());
      dialog.pack();
      dialog.setLocationRelativeTo(MainWindow.getInstance());

      ScraperWorker worker = new ScraperWorker(scraperManager, scraperFields, dialog);
      worker.execute();
      dialog.setVisible(true);

      MainWindow.getInstance().setWaitCursor(false);
      if (scraperFields.isScreenshots())
      {
        List<BufferedImage> screenshots = scraperManager.getScreenshots();
        if (screenshots.size() > 2)
        {
          //Show dialog for selecting screenshots
          ScreenshotsSelectionDialog screenDialog =
            new ScreenshotsSelectionDialog(MainWindow.getInstance(), screenshots);
          screenDialog.pack();
          screenDialog.setLocationRelativeTo(MainWindow.getInstance());
          if (screenDialog.showDialog())
          {
            screenshots = screenDialog.getSelectedScreenshots();
          }
          else
          {
            return;
          }
        }
        //Update with screenshots
        if (screenshots.size() >= 2)
        {
          scraperManager.updateModelWithScreenshotImages(screenshots.get(0), screenshots.get(1));
        }
        else if (screenshots.size() == 1)
        {
          scraperManager.updateModelWithScreenshotImages(screenshots.get(0), null);
        }
        else
        {
          //Do nothing
        }
      }
    }
  }

  private JButton getRunButton()
  {
    if (runButton == null)
    {
      runButton = new JButton(runGameAction);
    }
    return runButton;
  }

  public void runCurrentGame()
  {
    if (getRunButton().isEnabled())
    {
      getRunButton().doClick();
    }
  }

  private JButton getBtnNewButton()
  {
    if (btnNewButton == null)
    {
      btnNewButton = new JButton("VICE");
      btnNewButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            model.runVice();
          }
        });
    }
    return btnNewButton;
  }
}
