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
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.ScrollablePanel.ScrollableSizeHint;
import se.lantz.gui.scraper.CoverSelectionDialog;
import se.lantz.gui.scraper.ScraperDialog;
import se.lantz.gui.scraper.ScraperProgressDialog;
import se.lantz.gui.scraper.ScraperWorker;
import se.lantz.gui.scraper.ScreenshotsSelectionDialog;
import se.lantz.gui.translation.TranslationDialog;
import se.lantz.gui.translation.TranslationProgressDialog;
import se.lantz.gui.translation.TranslationWorker;
import se.lantz.manager.ScraperManager;
import se.lantz.model.MainViewModel;
import se.lantz.model.PreferencesModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.ScraperFields;
import se.lantz.util.FileManager;

public class GameDetailsBackgroundPanel extends JPanel
{
  private static final String EMPTY = "empty";
  private static final String DETAILS = "details";
  private static final Logger logger = LoggerFactory.getLogger(GameDetailsBackgroundPanel.class);
  private final MainViewModel model;
  private ScrollablePanel settingsPanel;
  private InfoBackgroundPanel infoPanel;
  private JTabbedPane systemSavesTabbedPane;
  private CombinedJoystickPanel joystickPanel;
  private SystemPanel systemPanel;
  private SaveStateBackgroundPanel savesBackgroundPanel;
  private ExtraDisksPanel extraDisksPanel;
  private SystemTabComponent savedStatesTabComponent;
  private SystemTabComponent extraDisksTabComponent;
  private JPanel buttonPanel;
  private JButton saveButton;
  private ScraperDialog scraperDialog = null;
  private JButton runButton;
  private JButton btnNewButton;
  private JPanel emptyPanel;
  private JPanel detailsPanel;
  private CardLayout cardLayout;
  private JButton scrapeButton;
  private JButton translateButton;
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
    this.setMinimumSize(new Dimension(1165, 75));
    cardLayout = new CardLayout();
    setLayout(cardLayout);
    add(getEmptyPanel(), EMPTY);
    add(getDetailsPanel(), DETAILS);
    model.addSaveChangeListener(e -> {
      saveButton.setEnabled(model.isDataChanged());
      runButton.setEnabled(!model.getInfoModel().getGamesFile().isEmpty());
    });
    
    if (!Beans.isDesignTime())
    {
      model.getSavedStatesModel().addPropertyChangeListener(e -> updateSystemTabs());
      model.getInfoModel().addPropertyChangeListener(e -> updateSystemTabs());
    }
    cursorTimer = new Timer(500, defaultCursorAction);
  }

  void focusTitleField()
  {
    getInfoBackgroundPanel().focusTitleField();
  }

  void updateSelectedGame(GameListData data)
  {
    scraperDialog = null;
    if (data == null)
    {
      cardLayout.show(this, EMPTY);
      model.checkEnablementOfPalNtscMenuItem(false);
    }
    else
    {
      cardLayout.show(this, DETAILS);
      model.readGameDetails(data);
      model.checkEnablementOfPalNtscMenuItem(true);
    }
  }

  private JPanel getDetailsPanel()
  {
    if (detailsPanel == null)
    {
      detailsPanel = new JPanel();
      detailsPanel.setLayout(new BorderLayout(0, 0));
      JScrollPane scrollpane = new JScrollPane();
      scrollpane.setBorder(BorderFactory.createEmptyBorder());
      scrollpane.setViewportView(getSettingsPanel());
      detailsPanel.add(scrollpane, BorderLayout.CENTER);
      detailsPanel.add(getInfoBackgroundPanel(), BorderLayout.NORTH);
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
      GridBagLayout gbl_settingsPanel = new GridBagLayout();
      settingsPanel = new ScrollablePanel(gbl_settingsPanel);
      settingsPanel.setPreferredSize(new Dimension(1120, 400));
      settingsPanel.setScrollableHeight(ScrollableSizeHint.STRETCH);
      settingsPanel.setScrollableWidth(ScrollableSizeHint.STRETCH);
      GridBagConstraints gbc_systemPanel = new GridBagConstraints();
      gbc_systemPanel.weightx = 1.0;
      gbc_systemPanel.weighty = 1.0;
      gbc_systemPanel.anchor = GridBagConstraints.NORTH;
      gbc_systemPanel.fill = GridBagConstraints.BOTH;
      gbc_systemPanel.insets = new Insets(0, 0, 0, 5);
      gbc_systemPanel.gridx = 1;
      gbc_systemPanel.gridy = 0;
      settingsPanel.add(getSystemSavesTabbedPane(), gbc_systemPanel);
      GridBagConstraints gbc_joystickPanel = new GridBagConstraints();
      gbc_joystickPanel.insets = new Insets(0, 0, 0, 0);
      gbc_joystickPanel.weighty = 1.0;
      gbc_joystickPanel.anchor = GridBagConstraints.NORTH;
      gbc_joystickPanel.fill = GridBagConstraints.BOTH;
      gbc_joystickPanel.gridx = 0;
      gbc_joystickPanel.gridy = 0;
      settingsPanel.add(getCombinedJoystickPanel(), gbc_joystickPanel);
    }
    return settingsPanel;
  }
  
  private JTabbedPane getSystemSavesTabbedPane()
  {
    if (systemSavesTabbedPane == null)
    {
      systemSavesTabbedPane = new JTabbedPane();
      systemSavesTabbedPane.addTab("System Settings", getSystemPanel());
      systemSavesTabbedPane.addTab("Saved states", getSavesBackgroundPanel());
      systemSavesTabbedPane.addTab("Extra disks", getExtraDisksPanel());
      
      systemSavesTabbedPane.setTabComponentAt(1, getSavedStatesTabComponent());
      systemSavesTabbedPane.setTabComponentAt(2, getExtraDisksTabComponent());
      
      updateSavedStatesTabTitle();
    }
    return systemSavesTabbedPane;
  }
  
  private SystemTabComponent getSavedStatesTabComponent()
  {
    if (savedStatesTabComponent == null)
    {
      savedStatesTabComponent = new SystemTabComponent("Saved states");
      savedStatesTabComponent.setNumber("");
    }
    return savedStatesTabComponent;
  }
  
  private SystemTabComponent getExtraDisksTabComponent()
  {
    if (extraDisksTabComponent == null)
    {
      extraDisksTabComponent = new SystemTabComponent("Extra disks");
      extraDisksTabComponent.setNumber("");
    }
    return extraDisksTabComponent;
  }

  protected InfoBackgroundPanel getInfoBackgroundPanel()
  {
    if (infoPanel == null)
    {
      infoPanel = new InfoBackgroundPanel(model);
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
        .setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    }
    return systemPanel;
  }
  
  private SaveStateBackgroundPanel getSavesBackgroundPanel()
  {
    if (savesBackgroundPanel == null)
    {
      savesBackgroundPanel = new SaveStateBackgroundPanel(model);
      savesBackgroundPanel
      .setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    }
    return savesBackgroundPanel;
  }
  
  private ExtraDisksPanel getExtraDisksPanel()
  {
    if (extraDisksPanel == null)
    {
      extraDisksPanel = new ExtraDisksPanel(model.getInfoModel());
      extraDisksPanel
      .setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    }
    return extraDisksPanel;
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
      GridBagConstraints gbc_translateButton = new GridBagConstraints();
      gbc_translateButton.insets = new Insets(5, 10, 5, 5);
      gbc_translateButton.gridx = 1;
      gbc_translateButton.gridy = 0;
      buttonPanel.add(getTranslateButton(), gbc_translateButton);
      GridBagConstraints gbc_viceButton = new GridBagConstraints();
      gbc_viceButton.anchor = GridBagConstraints.WEST;
      gbc_viceButton.insets = new Insets(5, 10, 5, 5);
      gbc_viceButton.gridx = 2;
      gbc_viceButton.gridy = 0;
      buttonPanel.add(getViceButton(), gbc_viceButton);
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
            //Make sure any edits to time for saved states are committed.
            savesBackgroundPanel.commitEdits();
            if (model.saveData())
            {
              getInfoBackgroundPanel().getScreensPanel().resetWhenSavedOrNewGameSelected();
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
      scraperDialog = new ScraperDialog(MainWindow.getInstance(), scraperManager, model.getInfoModel());
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
      if (scraperFields.isCover())
      {
        List<BufferedImage> covers = scraperManager.getCovers();
        if (covers.size() > 1)
        {
          //Show dialog for selecting cover image
          CoverSelectionDialog coverDialog = new CoverSelectionDialog(MainWindow.getInstance(), covers);
          coverDialog.pack();
          coverDialog.setLocationRelativeTo(MainWindow.getInstance());
          if (coverDialog.showDialog())
          {
            covers = Arrays.asList(coverDialog.getSelectedCover());
          }
          else
          {
            covers = new ArrayList<>();
          }
        }
        //Update with cover
        if (covers.size() == 1)
        {
          scraperManager.updateModelWithCoverImage(covers.get(0));
        }
        else
        {
          //Do nothing
        }
      }
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
            screenshots = new ArrayList<>();
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

  private JButton getViceButton()
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

  private JButton getTranslateButton()
  {
    if (translateButton == null)
    {
      translateButton = new JButton("Translate...");
      translateButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            TranslationDialog dialog = new TranslationDialog(model.getInfoModel());
            dialog.pack();
            dialog.setLocationRelativeTo(MainWindow.getInstance());
            if (dialog.showDialog())
            {
              //Check valid language
              if (dialog.getSelectedToLanguages().size() == 1 &&
                !isValidLanguage(dialog.getSelectedToLanguages().get(0)))
              {
                JOptionPane
                  .showMessageDialog(MainWindow.getInstance(),
                                     "\"" + dialog.getSelectedToLanguages().get(0) +
                                       "\" is not a vaild language code. It must be a two-letter code from ISO 639-1.\nAll languages might not be supported.",
                                     "Invalid language code",
                                     JOptionPane.ERROR_MESSAGE);
              }
              else
              {
                TranslationProgressDialog progressDialog = new TranslationProgressDialog(MainWindow.getInstance());
                progressDialog.pack();
                progressDialog.setLocationRelativeTo(MainWindow.getInstance());
                TranslationWorker worker = new TranslationWorker(model.getInfoModel(),
                                                                 progressDialog,
                                                                 dialog.getSelectedFromLanguage(),
                                                                 dialog.getSelectedToLanguages());
                worker.execute();
                progressDialog.setVisible(true);
              }
            }
          }
        });
    }
    return translateButton;
  }

  private boolean isValidLanguage(String language)
  {
    try
    {
      Locale locale = new Locale.Builder().setLanguageTag(language).build();
      return locale.getISO3Language() != null;
    }
    catch (Exception e)
    {
      return false;
    }
  }
  
  public void updateSavedStatesTabTitle()
  {
    String carouselVersion = FileManager.getConfiguredSavedStatesCarouselVersion();
    String title = "Saved States (Carousel " + carouselVersion + ")";
    if (carouselVersion.equals(PreferencesModel.FILE_LOADER))
    {
      title = "Saved States (File Loader)";
    }
    getSystemSavesTabbedPane().setTitleAt(1, title);
    getSavesBackgroundPanel().resetCurrentGameReference();
  }
  
  private void updateSystemTabs()
  {
    int availableSavedStates = model.getSavedStatesModel().getNumberOfAvailableSavedStates();
    getSavedStatesTabComponent().setNumber(availableSavedStates > 0 ? Integer.toString(availableSavedStates) : "");
    
    int availableExtraDisks = model.getInfoModel().getNumberOfExtraDisks();
    getExtraDisksTabComponent().setNumber(availableExtraDisks > 0 ? Integer.toString(availableExtraDisks) : "");
  }
}
