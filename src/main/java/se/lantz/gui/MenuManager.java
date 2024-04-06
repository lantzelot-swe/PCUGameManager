package se.lantz.gui;

import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;

import se.lantz.gamebase.GamebaseImporter;
import se.lantz.gui.DeleteDialog.TYPE_OF_DELETE;
import se.lantz.gui.dbbackup.BackupProgressDialog;
import se.lantz.gui.dbbackup.BackupWorker;
import se.lantz.gui.dbrestore.RestoreDbDialog;
import se.lantz.gui.dbrestore.RestoreProgressDialog;
import se.lantz.gui.dbrestore.RestoreWorker;
import se.lantz.gui.dbvalidation.DbValidationProgressDialog;
import se.lantz.gui.dbvalidation.DbValidationWorker;
import se.lantz.gui.exports.ExportFileLoaderWorker;
import se.lantz.gui.exports.ExportGamesDialog;
import se.lantz.gui.exports.ExportSavedStatesDialog;
import se.lantz.gui.exports.ExportSavedStatesWorker;
import se.lantz.gui.exports.ExportWorker;
import se.lantz.gui.exports.ImportExportProgressDialog;
import se.lantz.gui.exports.ImportExportProgressDialog.DIALOGTYPE;
import se.lantz.gui.imports.CarouselImportWorker;
import se.lantz.gui.imports.GamebaseImportWorker;
import se.lantz.gui.imports.ImportOptionsDialog;
import se.lantz.gui.imports.ImportProgressDialog;
import se.lantz.gui.imports.ImportSavedStatesDialog;
import se.lantz.gui.imports.ImportSavedStatesWorker;
import se.lantz.gui.install.ManagerDownloadDialog;
import se.lantz.gui.preferences.PreferencesDialog;
import se.lantz.gui.savedstates.FixCorruptSavedStatesDialog;
import se.lantz.gui.savedstates.FixCorruptSavedStatesWorker;
import se.lantz.manager.BackupManager;
import se.lantz.manager.ExportManager;
import se.lantz.manager.ImportManager;
import se.lantz.manager.RestoreManager;
import se.lantz.manager.SavedStatesManager;
import se.lantz.manager.pcuae.AmigaModeInstallManager;
import se.lantz.manager.pcuae.AtariModeInstallManager;
import se.lantz.manager.pcuae.LinuxModeInstallManager;
import se.lantz.manager.pcuae.MSXModeInstallManager;
import se.lantz.manager.pcuae.PCUAEInstallManager;
import se.lantz.manager.pcuae.RetroarchModeInstallManager;
import se.lantz.manager.pcuae.ScummVMModeInstallManager;
import se.lantz.manager.pcuae.ViceModeInstallManager;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;
import se.lantz.util.ManagerVersionChecker;

public class MenuManager
{
  private JMenu fileMenu;
  private JMenu importMenu;
  private JMenu exportMenu;
  private JMenu editMenu;
  private JMenu toolsMenu;
  private JMenu clearFavoritesMenu;
  private JMenu pcuaeMenu;
  private JMenu pcuaeModeMenu;
  private JMenu resourcesMenu;
  private JMenu helpMenu;

  private JMenuItem addGameItem;
  private JMenuItem addInfoSlotItem;
  private JMenuItem deleteGameItem;

  private JMenuItem preferencesItem;

  private JMenuItem runGameItem;
  private JMenuItem importCarouselItem;
  private JMenuItem importGamebaseItem;
  private JMenuItem importSavedStatesItem;
  private JMenuItem exportItem;
  private JMenuItem exportFLItem;
  private JMenuItem exportSavedStatesItem;
  private JMenuItem refreshItem;
  private JMenuItem refreshAllItem;

  private JMenuItem carouselPreviewItem;

  private JMenuItem toggleFavorite1Item;
  private JMenuItem toggleFavorite2Item;
  private JMenuItem toggleFavorite3Item;
  private JMenuItem toggleFavorite4Item;
  private JMenuItem toggleFavorite5Item;
  private JMenuItem toggleFavorite6Item;
  private JMenuItem toggleFavorite7Item;
  private JMenuItem toggleFavorite8Item;
  private JMenuItem toggleFavorite9Item;
  private JMenuItem toggleFavorite10Item;
  private JMenuItem clearFavorites1Item;
  private JMenuItem clearFavorites2Item;
  private JMenuItem clearFavorites3Item;
  private JMenuItem clearFavorites4Item;
  private JMenuItem clearFavorites5Item;
  private JMenuItem clearFavorites6Item;
  private JMenuItem clearFavorites7Item;
  private JMenuItem clearFavorites8Item;
  private JMenuItem clearFavorites9Item;
  private JMenuItem clearFavorites10Item;

  private JMenuItem editViewTagItem;
  private JMenuItem editPrimaryJoystickItem;

  private JMenuItem backupDbItem;
  private JMenuItem restoreDbItem;
  private JMenuItem deleteAllGamesItem;
  private JMenuItem deleteGamesForViewItem;
  private JMenuItem deleteAllGameViewsItem;

  private JMenuItem validateDbItem;
  private JMenuItem palNtscFixItem;
  private JMenuItem convertSavedStatesItem;
  private JMenuItem copySavedStatesItem;
  private JMenuItem fixCorruptSavedStatesItem;
  private JMenuItem resetJoystickConfigItem;
  private JMenuItem enableAccurateDiskItem;
  private JMenuItem disableAccurateDiskItem;

  private JMenuItem installPCUAEItem;
  private JMenuItem installAmigaModeItem;
  private JMenuItem installAtariModeItem;
  private JMenuItem installLinuxModeItem;
  private JMenuItem installRetroarchModeItem;
  private JMenuItem installViceModeItem;
  private JMenuItem installScummVMModeItem;
  private JMenuItem installMSXModeItem;
  private JMenuItem deleteInstallFilesItem;

  private JMenuItem helpItem;
  private JMenuItem pcuaeWikiItem;
  private JMenuItem aboutItem;
  private JMenuItem newVersionItem;

  private JMenuItem exitItem;
  private MainViewModel uiModel;
  private ImportManager importManager;
  private GamebaseImporter gamebaseImporter;
  private ExportManager exportManager;
  private BackupManager backupManager;
  private RestoreManager restoreManager;
  private SavedStatesManager savedStatesManager;
  private PCUAEInstallManager installPCUAEManager;
  private AmigaModeInstallManager installAmigaManager;
  private AtariModeInstallManager installAtariManager;
  private LinuxModeInstallManager installLinuxManager;
  private RetroarchModeInstallManager installRetroarchManager;
  private ViceModeInstallManager installViceManager;
  private ScummVMModeInstallManager installScummVMManager;
  private MSXModeInstallManager installMSXManager;
  private int currentFavoritesCount = 10;

  private Map<String, String> resourcesMap = new LinkedHashMap<>();

  public MenuManager(final MainViewModel uiModel)
  {
    this.uiModel = uiModel;
    this.importManager = new ImportManager(uiModel);
    this.gamebaseImporter = new GamebaseImporter(importManager);
    this.exportManager = new ExportManager(uiModel);
    this.backupManager = new BackupManager(uiModel);
    this.restoreManager = new RestoreManager(uiModel);
    this.savedStatesManager = new SavedStatesManager(uiModel, getPalNtscFixMenuItem());
    this.installPCUAEManager = new PCUAEInstallManager(getExportItem());
    this.installAmigaManager = new AmigaModeInstallManager();
    this.installAtariManager = new AtariModeInstallManager();
    this.installLinuxManager = new LinuxModeInstallManager();
    this.installRetroarchManager = new RetroarchModeInstallManager();
    this.installViceManager = new ViceModeInstallManager();
    this.installScummVMManager = new ScummVMModeInstallManager();
    this.installMSXManager = new MSXModeInstallManager();
    uiModel.setSavedStatesManager(savedStatesManager);
    setupResourcesMap();
    setupMenues();
  }

  private void setupResourcesMap()
  {
    resourcesMap.put("mobygames.com", "https://www.mobygames.com/platform/c64/");
    resourcesMap.put("gb64.com", "https://gb64.com/search.php?h=0");
    resourcesMap.put("c64.com", "https://www.c64.com/");
    resourcesMap.put("retrocollector.org", "https://retrocollector.org/");
    resourcesMap.put("web.archive.org (Vic 20)",
                     "https://web.archive.org/web/20100530121115/http:/www.6502dude.com/cbm/vic20/arma/0to9taps.html");
  }

  public void triggerExit()
  {
    exitItem.doClick();
  }

  private void setupMenues()
  {
    fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    fileMenu.add(getAddGameMenuItem());
    fileMenu.add(getAddInfoSlotMenuItem());
    fileMenu.add(getDeleteGameMenuItem());
    fileMenu.addSeparator();
    fileMenu.add(getRunGameMenuItem());
    fileMenu.addSeparator();
    importMenu = new JMenu("Import");
    importMenu.setMnemonic('I');
    fileMenu.add(importMenu);
    importMenu.add(getImportCarouselItem());
    importMenu.add(getImportGamebaseItem());
    importMenu.add(getImportSavedStatesItem());
    exportMenu = new JMenu("Export");
    exportMenu.setMnemonic('E');
    exportMenu.add(getExportItem());
    exportMenu.add(getExportFileLoaderItem());
    exportMenu.add(getExportSavedStatesItem());
    fileMenu.add(exportMenu);
    fileMenu.addSeparator();
    fileMenu.add(getCarouselPreviewMenuItem());

    fileMenu.addSeparator();
    fileMenu.add(getRefreshItem());
    fileMenu.add(getRefreshAllItem());
    fileMenu.addSeparator();
    fileMenu.add(getPreferencesMenuItem());
    fileMenu.addSeparator();
    fileMenu.add(getExitItem());
    clearFavoritesMenu = new JMenu("Clear Favorites");
    editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');
    updateEditMenu();
    toolsMenu = new JMenu("Tools");
    toolsMenu.setMnemonic('T');
    toolsMenu.add(getBackupDbItem());
    toolsMenu.add(getRestoreDbItem());
    toolsMenu.add(getValidateDbItem());
    toolsMenu.addSeparator();
    toolsMenu.add(getDeleteAllGamesItem());
    toolsMenu.add(getDeleteGamesForViewMenuItem());
    toolsMenu.add(getDeleteAllGameViewsItem());
    toolsMenu.addSeparator();
    toolsMenu.add(clearFavoritesMenu);
    toolsMenu.addSeparator();
    toolsMenu.add(getConvertSavedStatesItem());
    toolsMenu.add(getCopySavedStatesToFileLoaderItem());
    toolsMenu.add(getFixCorruptSavedStatesItem());
    toolsMenu.addSeparator();
    toolsMenu.add(getResetJoystickConfigItem());
    toolsMenu.add(getEnableAccurateDiskItem());
    toolsMenu.add(getDisableAccurateDiskItem());
    toolsMenu.addSeparator();
    toolsMenu.add(getPalNtscFixMenuItem());
    pcuaeMenu = new JMenu("PCUAE");
    pcuaeMenu.setMnemonic('P');
    pcuaeMenu.add(getInstallPCUAEItem());
    pcuaeModeMenu = new JMenu("Mode Packs");
    pcuaeModeMenu.setMnemonic('M');
    pcuaeModeMenu.add(getInstallAmigaModeItem());
    pcuaeModeMenu.add(getInstallAtariModeItem());
    pcuaeModeMenu.add(getInstallLinuxModeItem());
    pcuaeModeMenu.add(getInstallRetroarchModeItem());
    pcuaeModeMenu.add(getInstallScummVMModeItem());
    pcuaeModeMenu.add(getInstallViceModeItem());
    pcuaeModeMenu.add(getInstallMSXModeItem());
    pcuaeMenu.add(pcuaeModeMenu);
    pcuaeMenu.addSeparator();
    pcuaeMenu.add(getDeleteInstallFilesItem());

    setupResourcesMenu();

    helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');
    helpMenu.add(getHelpItem());
    helpMenu.add(getPcuaeWikiItem());
    helpMenu.addSeparator();
    helpMenu.add(getCheckVersionItem());
    helpMenu.add(getAboutItem());
  }

  public void setupResourcesMenu()
  {
    resourcesMenu = new JMenu("Online resources");
    resourcesMenu.setMnemonic('R');
    resourcesMap.forEach((key, value) -> {
      resourcesMenu.add(getResourcesItem(key, value));
    });
  }

  private JMenuItem getResourcesItem(String text, String url)
  {
    JMenuItem item = new JMenuItem(text);
    item.addActionListener(e -> {
      try
      {
        Desktop.getDesktop().browse(new URI(url));
      }
      catch (IOException | URISyntaxException ex)
      {
        ExceptionHandler.handleException(ex, "Could not open default browser");
      }
    });
    return item;
  }

  public void updateEditMenu()
  {
    editMenu.removeAll();
    this.currentFavoritesCount = FileManager.getConfiguredNumberOfFavorites();

    editMenu.add(getToggleFavorite1Item());
    if (currentFavoritesCount > 1)
    {
      editMenu.add(getToggleFavorite2Item());
    }
    if (currentFavoritesCount > 2)
    {
      editMenu.add(getToggleFavorite3Item());
    }
    if (currentFavoritesCount > 3)
    {
      editMenu.add(getToggleFavorite4Item());
    }
    if (currentFavoritesCount > 4)
    {
      editMenu.add(getToggleFavorite5Item());
    }
    if (currentFavoritesCount > 5)
    {
      editMenu.add(getToggleFavorite6Item());
    }
    if (currentFavoritesCount > 6)
    {
      editMenu.add(getToggleFavorite7Item());
    }
    if (currentFavoritesCount > 7)
    {
      editMenu.add(getToggleFavorite8Item());
    }
    if (currentFavoritesCount > 8)
    {
      editMenu.add(getToggleFavorite9Item());
    }
    if (currentFavoritesCount > 9)
    {
      editMenu.add(getToggleFavorite10Item());
    }
    editMenu.addSeparator();
    editMenu.add(getEditViewTagItem());
    editMenu.add(getPrimaryJoystickItem());

    //Update the clear favorites menu based on used favorites lists
    clearFavoritesMenu.removeAll();
    clearFavoritesMenu.add(getClearFavorites1Item());
    if (currentFavoritesCount > 1)
    {
      clearFavoritesMenu.add(getClearFavorites2Item());
    }
    if (currentFavoritesCount > 2)
    {
      clearFavoritesMenu.add(getClearFavorites3Item());
    }
    if (currentFavoritesCount > 3)
    {
      clearFavoritesMenu.add(getClearFavorites4Item());
    }
    if (currentFavoritesCount > 4)
    {
      clearFavoritesMenu.add(getClearFavorites5Item());
    }
    if (currentFavoritesCount > 5)
    {
      clearFavoritesMenu.add(getClearFavorites6Item());
    }
    if (currentFavoritesCount > 6)
    {
      clearFavoritesMenu.add(getClearFavorites7Item());
    }
    if (currentFavoritesCount > 7)
    {
      clearFavoritesMenu.add(getClearFavorites8Item());
    }
    if (currentFavoritesCount > 8)
    {
      clearFavoritesMenu.add(getClearFavorites9Item());
    }
    if (currentFavoritesCount > 9)
    {
      clearFavoritesMenu.add(getClearFavorites10Item());
    }
  }

  public void intialize()
  {
    uiModel.addSaveChangeListener(e -> {
      boolean okToEnable = !uiModel.isDataChanged();
      addGameItem.setEnabled(okToEnable);
      addInfoSlotItem.setEnabled(okToEnable);
      importMenu.setEnabled(okToEnable);
      exportMenu.setEnabled(okToEnable);
      toolsMenu.setEnabled(okToEnable);
      pcuaeMenu.setEnabled(okToEnable);
      editMenu.setEnabled(okToEnable);
      runGameItem.setEnabled(!uiModel.getInfoModel().getGamesFile().isEmpty());
      refreshItem.setEnabled(okToEnable);
      preferencesItem.setEnabled(okToEnable);
      carouselPreviewItem.setEnabled(!uiModel.isNewGameSelected());
    });
  }

  public List<JMenu> getMenues()
  {
    List<JMenu> menuList = new ArrayList<JMenu>();
    menuList.add(fileMenu);
    menuList.add(editMenu);
    menuList.add(toolsMenu);
    menuList.add(pcuaeMenu);
    menuList.add(resourcesMenu);
    menuList.add(helpMenu);
    return menuList;
  }

  JMenuItem getAddGameMenuItem()
  {
    addGameItem = new JMenuItem("Add new game");
    KeyStroke keyStrokeToAddGame = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
    addGameItem.setAccelerator(keyStrokeToAddGame);
    addGameItem.setMnemonic('A');

    addGameItem.addActionListener(e -> MainWindow.getInstance().getMainPanel().addNewGame());
    return addGameItem;
  }

  JMenuItem getAddInfoSlotMenuItem()
  {
    addInfoSlotItem = new JMenuItem("Add info slot for current gamelist view");
    KeyStroke keyStrokeToAddGame = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);
    addInfoSlotItem.setAccelerator(keyStrokeToAddGame);
    addInfoSlotItem.setMnemonic('I');

    addInfoSlotItem.addActionListener(e -> MainWindow.getInstance().getMainPanel().addNewInfoSlot());
    return addInfoSlotItem;
  }

  JMenuItem getDeleteGameMenuItem()
  {
    deleteGameItem = new JMenuItem("Delete selected game(s)");
    KeyStroke keyStrokeToAddGame = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
    deleteGameItem.setAccelerator(keyStrokeToAddGame);
    deleteGameItem.setMnemonic('D');

    deleteGameItem.addActionListener(e -> {
      MainWindow.getInstance().setWaitCursor(true);
      MainWindow.getInstance().getMainPanel().deleteCurrentGame();
      MainWindow.getInstance().setWaitCursor(false);
    });
    return deleteGameItem;
  }

  JMenuItem getPreferencesMenuItem()
  {
    preferencesItem = new JMenuItem("Preferences...");
    KeyStroke keyStrokeToPreferences = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK);
    preferencesItem.setAccelerator(keyStrokeToPreferences);
    preferencesItem.setMnemonic('P');

    preferencesItem.addActionListener(e -> editPreferences());
    return preferencesItem;
  }

  JMenuItem getDeleteGamesForViewMenuItem()
  {
    deleteGamesForViewItem = new JMenuItem("Delete all games in current gamelist view");
    deleteGamesForViewItem.setMnemonic('g');
    deleteGamesForViewItem.addActionListener(e -> deleteAllGamesInView());
    return deleteGamesForViewItem;
  }

  JMenuItem getRunGameMenuItem()
  {
    runGameItem = new JMenuItem("Run selected game");
    KeyStroke keyStrokeToRunGame = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
    runGameItem.setAccelerator(keyStrokeToRunGame);
    runGameItem.setMnemonic('R');

    runGameItem.addActionListener(e -> MainWindow.getInstance().getMainPanel().runCurrentGame());
    return runGameItem;
  }

  JMenuItem getCarouselPreviewMenuItem()
  {
    carouselPreviewItem = new JMenuItem("Carousel preview");
    KeyStroke keyStrokeCarouselPreview = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK);
    carouselPreviewItem.setAccelerator(keyStrokeCarouselPreview);
    carouselPreviewItem.setMnemonic('W');
    carouselPreviewItem
      .addActionListener(e -> MainWindow.getInstance().getMainPanel().getListPanel().showCarouselPreview());
    return carouselPreviewItem;
  }

  private JMenuItem getImportCarouselItem()
  {
    importCarouselItem = new JMenuItem("Import Carousel folder...");
    KeyStroke keyStrokeToImportGames = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);
    importCarouselItem.setAccelerator(keyStrokeToImportGames);
    importCarouselItem.setMnemonic('I');
    importCarouselItem.addActionListener(e -> importCarouselGames());
    return importCarouselItem;
  }

  private JMenuItem getImportGamebaseItem()
  {
    importGamebaseItem = new JMenuItem("Import from Gamebase...");
    KeyStroke keyStrokeToImportGames = KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK);
    importGamebaseItem.setAccelerator(keyStrokeToImportGames);
    importGamebaseItem.setMnemonic('G');
    importGamebaseItem.addActionListener(e -> importGamebaseGames());
    return importGamebaseItem;
  }

  private JMenuItem getImportSavedStatesItem()
  {
    importSavedStatesItem = new JMenuItem("Import Saved states...");
    importSavedStatesItem.setMnemonic('S');
    importSavedStatesItem.addActionListener(e -> importSavedStates());
    return importSavedStatesItem;
  }

  private JMenuItem getExportItem()
  {
    exportItem = new JMenuItem("Export to Carousel...");
    KeyStroke keyStrokeToExportGames = KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK);
    exportItem.setAccelerator(keyStrokeToExportGames);
    exportItem.setMnemonic('E');
    exportItem.addActionListener(e -> exportGames());
    return exportItem;
  }

  private JMenuItem getExportFileLoaderItem()
  {
    exportFLItem = new JMenuItem("Export to File loader...");
    KeyStroke keyStrokeToExportGames = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);
    exportFLItem.setAccelerator(keyStrokeToExportGames);
    exportFLItem.setMnemonic('L');
    exportFLItem.addActionListener(e -> exportGamesToFileLoader());
    return exportFLItem;
  }

  private JMenuItem getExportSavedStatesItem()
  {
    exportSavedStatesItem = new JMenuItem("Export Saved states...");
    exportSavedStatesItem.setMnemonic('S');
    exportSavedStatesItem.addActionListener(e -> exportSavedStates());
    return exportSavedStatesItem;
  }

  private JMenuItem getRefreshItem()
  {
    refreshItem = new JMenuItem("Reload current gamelist view");
    KeyStroke keyStrokeToReloadGameView = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
    refreshItem.setAccelerator(keyStrokeToReloadGameView);
    refreshItem.setMnemonic('C');
    refreshItem.addActionListener(e -> reloadView());
    return refreshItem;
  }

  private JMenuItem getRefreshAllItem()
  {
    refreshAllItem = new JMenuItem("Reload all gamelist views");
    KeyStroke keyStrokeToReloadGameViews = KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.SHIFT_DOWN_MASK);
    refreshAllItem.setAccelerator(keyStrokeToReloadGameViews);
    refreshAllItem.setMnemonic('g');
    refreshAllItem.addActionListener(e -> reloadAll());
    return refreshAllItem;
  }

  private JMenuItem getExitItem()
  {
    exitItem = new JMenuItem("Exit");
    KeyStroke keyStrokeExit = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK);
    exitItem.setAccelerator(keyStrokeExit);

    exitItem.setMnemonic('x');
    exitItem.addActionListener(e -> {
      if (uiModel.isDataChanged())
      {
        int value = MainWindow.getInstance().getMainPanel().showUnsavedChangesDialog();
        if (value == JOptionPane.YES_OPTION && !uiModel.saveData())
        {
          //Do not exit, save was not successful
          return;
        }
        if (value == JOptionPane.CANCEL_OPTION)
        {
          return;
        }
      }
      //Save properties before exit
      FileManager.storeProperties();
      Future<?> deleteTempFolder = FileManager.deleteTempFolder();
      try
      {
        deleteTempFolder.get(10, TimeUnit.SECONDS);
      }
      catch (Exception e1)
      {
        ExceptionHandler.logException(e1, "Could not delete temp folder");
      }
      System.exit(0);
    });
    return exitItem;
  }

  private JMenuItem getToggleFavorite1Item()
  {
    if (toggleFavorite1Item == null)
    {
      toggleFavorite1Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite1Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite1Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite1Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(1));
    return toggleFavorite1Item;
  }

  private JMenuItem getToggleFavorite2Item()
  {
    if (toggleFavorite2Item == null)
    {
      toggleFavorite2Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite2Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite2Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite2();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite2Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(2));
    return toggleFavorite2Item;
  }

  private JMenuItem getToggleFavorite3Item()
  {
    if (toggleFavorite3Item == null)
    {
      toggleFavorite3Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite3Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite3Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite3();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite3Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(3));
    return toggleFavorite3Item;
  }

  private JMenuItem getToggleFavorite4Item()
  {
    if (toggleFavorite4Item == null)
    {
      toggleFavorite4Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite4Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite4Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite4();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite4Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(4));
    return toggleFavorite4Item;
  }

  private JMenuItem getToggleFavorite5Item()
  {
    if (toggleFavorite5Item == null)
    {
      toggleFavorite5Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite5Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite5Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite5();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite5Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(5));
    return toggleFavorite5Item;
  }

  private JMenuItem getToggleFavorite6Item()
  {
    if (toggleFavorite6Item == null)
    {
      toggleFavorite6Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite6Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite6Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite6();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite6Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(6));
    return toggleFavorite6Item;
  }

  private JMenuItem getToggleFavorite7Item()
  {
    if (toggleFavorite7Item == null)
    {
      toggleFavorite7Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite7Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite7Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite7();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite7Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(7));
    return toggleFavorite7Item;
  }

  private JMenuItem getToggleFavorite8Item()
  {
    if (toggleFavorite8Item == null)
    {
      toggleFavorite8Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite8Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite8Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite8();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite8Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(8));
    return toggleFavorite8Item;
  }

  private JMenuItem getToggleFavorite9Item()
  {
    if (toggleFavorite9Item == null)
    {
      toggleFavorite9Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite9Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite9Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite9();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite9Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(9));
    return toggleFavorite9Item;
  }

  private JMenuItem getToggleFavorite10Item()
  {
    if (toggleFavorite10Item == null)
    {
      toggleFavorite10Item = new JMenuItem();
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.CTRL_DOWN_MASK);
      toggleFavorite10Item.setAccelerator(keyStrokeToToggleFav);
      toggleFavorite10Item.addActionListener(e -> {
        MainWindow.getInstance().setWaitCursor(true);
        MainWindow.getInstance().getMainPanel().toggleFavorite10();
        MainWindow.getInstance().setWaitCursor(false);
      });
    }
    toggleFavorite10Item.setText("Add/remove from " + FileManager.getConfiguredFavGameViewName(10));
    return toggleFavorite10Item;
  }

  private JMenuItem getClearFavorites1Item()
  {
    if (clearFavorites1Item == null)
    {
      clearFavorites1Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites1Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites1Item.addActionListener(e -> {
        clearFavorites(1);
      });
    }
    clearFavorites1Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(1));
    return clearFavorites1Item;
  }

  private JMenuItem getClearFavorites2Item()
  {
    if (clearFavorites2Item == null)
    {
      clearFavorites2Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites2Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites2Item.addActionListener(e -> {
        clearFavorites(2);
      });
    }
    clearFavorites2Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(2));
    return clearFavorites2Item;
  }

  private JMenuItem getClearFavorites3Item()
  {
    if (clearFavorites3Item == null)
    {
      clearFavorites3Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites3Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites3Item.addActionListener(e -> {
        clearFavorites(3);
      });
    }
    clearFavorites3Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(3));
    return clearFavorites3Item;
  }

  private JMenuItem getClearFavorites4Item()
  {
    if (clearFavorites4Item == null)
    {
      clearFavorites4Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites4Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites4Item.addActionListener(e -> {
        clearFavorites(4);
      });
    }
    clearFavorites4Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(4));
    return clearFavorites4Item;
  }

  private JMenuItem getClearFavorites5Item()
  {
    if (clearFavorites5Item == null)
    {
      clearFavorites5Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites5Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites5Item.addActionListener(e -> {
        clearFavorites(5);
      });
    }
    clearFavorites5Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(5));
    return clearFavorites5Item;
  }

  private JMenuItem getClearFavorites6Item()
  {
    if (clearFavorites6Item == null)
    {
      clearFavorites6Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites6Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites6Item.addActionListener(e -> {
        clearFavorites(6);
      });
    }
    clearFavorites6Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(6));
    return clearFavorites6Item;
  }

  private JMenuItem getClearFavorites7Item()
  {
    if (clearFavorites7Item == null)
    {
      clearFavorites7Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites7Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites7Item.addActionListener(e -> {
        clearFavorites(7);
      });
    }
    clearFavorites7Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(7));
    return clearFavorites7Item;
  }

  private JMenuItem getClearFavorites8Item()
  {
    if (clearFavorites8Item == null)
    {
      clearFavorites8Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites8Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites8Item.addActionListener(e -> {
        clearFavorites(8);
      });
    }
    clearFavorites8Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(8));
    return clearFavorites8Item;
  }

  private JMenuItem getClearFavorites9Item()
  {
    if (clearFavorites9Item == null)
    {
      clearFavorites9Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites9Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites9Item.addActionListener(e -> {
        clearFavorites(9);
      });
    }
    clearFavorites9Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(9));
    return clearFavorites9Item;
  }

  private JMenuItem getClearFavorites10Item()
  {
    if (clearFavorites10Item == null)
    {
      clearFavorites10Item = new JMenuItem();
      KeyStroke keyStrokeToClearFav =
        KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
      clearFavorites10Item.setAccelerator(keyStrokeToClearFav);
      clearFavorites10Item.addActionListener(e -> {
        clearFavorites(10);
      });
    }
    clearFavorites10Item.setText("Clear " + FileManager.getConfiguredFavGameViewName(10));
    return clearFavorites10Item;
  }

  private JMenuItem getEditViewTagItem()
  {
    if (editViewTagItem == null)
    {
      editViewTagItem = new JMenuItem("Edit view tag...");
      KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK);
      editViewTagItem.setAccelerator(keyStrokeToToggleFav);
      editViewTagItem.addActionListener(e -> {
        if (!MainWindow.getInstance().getMainPanel().isNoGameSelected())
        {
          String initialValue =
            MainWindow.getInstance().getMainPanel().isSingleGameSelected() ? uiModel.getInfoModel().getViewTag() : "";
          String message = MainWindow.getInstance().getMainPanel().isSingleGameSelected()
            ? "Enter the view tag to set for " + uiModel.getInfoModel().getTitle()
            : "Enter the view tag to set for the selected games";
          String viewTag = JOptionPane.showInputDialog(MainWindow.getInstance(), message, initialValue);
          if (viewTag != null)
          {
            MainWindow.getInstance().setWaitCursor(true);
            MainWindow.getInstance().getMainPanel().setViewTag(viewTag);
            MainWindow.getInstance().setWaitCursor(false);
          }
        }
      });
    }
    return editViewTagItem;
  }

  private JMenuItem getPrimaryJoystickItem()
  {
    if (editPrimaryJoystickItem == null)
    {
      editPrimaryJoystickItem = new JMenuItem("Edit primary Joystick...");
      KeyStroke keyStrokeToEditJoy = KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_DOWN_MASK);
      editPrimaryJoystickItem.setAccelerator(keyStrokeToEditJoy);
      editPrimaryJoystickItem.addActionListener(e -> {
        if (!MainWindow.getInstance().getMainPanel().isNoGameSelected())
        {
          PrimaryJoystickDialog dialog = new PrimaryJoystickDialog(MainWindow.getInstance());
          dialog.pack();
          dialog.setLocationRelativeTo(MainWindow.getInstance());

          if (dialog.showDialog())
          {
            MainWindow.getInstance().setWaitCursor(true);
            List<String> selectedGameIds = MainWindow.getInstance().getMainPanel().getListPanel()
              .getSelectedGameListData().stream().map(data -> data.getGameId()).collect(Collectors.toList());

            uiModel.updatePrimaryJoystickPort(selectedGameIds, dialog.isPort1Primary());

            MainWindow.getInstance().setWaitCursor(false);
            JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(),
                                          "Primary joystick port updated for the selected games.",
                                          "Primary port",
                                          JOptionPane.INFORMATION_MESSAGE);
            if (selectedGameIds.size() == 1)
            {
              MainWindow.getInstance().reloadCurrentGameView();
            }
          }
        }
      });
    }
    return editPrimaryJoystickItem;
  }

  private JMenuItem getBackupDbItem()
  {
    backupDbItem = new JMenuItem("Backup database");
    backupDbItem.setMnemonic('b');
    backupDbItem.addActionListener(e -> backupDb());
    return backupDbItem;
  }

  private JMenuItem getRestoreDbItem()
  {
    restoreDbItem = new JMenuItem("Restore backup...");
    restoreDbItem.setMnemonic('r');
    restoreDbItem.addActionListener(e -> restoreDb());
    return restoreDbItem;
  }

  private JMenuItem getDeleteAllGamesItem()
  {
    deleteAllGamesItem = new JMenuItem("Delete all games in database");
    deleteAllGamesItem.setMnemonic('d');
    deleteAllGamesItem.addActionListener(e -> deleteAllGames());
    return deleteAllGamesItem;
  }

  private JMenuItem getDeleteAllGameViewsItem()
  {
    deleteAllGameViewsItem = new JMenuItem("Delete all gamelist views in database");
    deleteAllGameViewsItem.setMnemonic('l');
    deleteAllGameViewsItem.addActionListener(e -> deleteAllGamelistViews());
    return deleteAllGameViewsItem;
  }

  private JMenuItem getValidateDbItem()
  {
    if (validateDbItem == null)
    {
      validateDbItem = new JMenuItem("Validate database...");
      validateDbItem.setMnemonic('v');
      validateDbItem.addActionListener(e -> validateDb());
    }
    return validateDbItem;
  }

  private JMenuItem getPalNtscFixMenuItem()
  {
    if (palNtscFixItem == null)
    {
      palNtscFixItem = new JMenuItem("Swap game file and first saved state to fix NTSC/PAL issue");
      palNtscFixItem.setMnemonic('s');
      palNtscFixItem.addActionListener(e -> fixPalNtscIssue());
    }
    return palNtscFixItem;
  }

  private JMenuItem getConvertSavedStatesItem()
  {
    if (convertSavedStatesItem == null)
    {
      convertSavedStatesItem = new JMenuItem("Convert Saved states...");
      convertSavedStatesItem.setMnemonic('c');
      convertSavedStatesItem.addActionListener(e -> convertSavedStates());
    }
    return convertSavedStatesItem;
  }

  private JMenuItem getCopySavedStatesToFileLoaderItem()
  {

    if (copySavedStatesItem == null)
    {
      copySavedStatesItem = new JMenuItem("Copy Saved states to File Loader...");
      copySavedStatesItem.setMnemonic('f');
      copySavedStatesItem.addActionListener(e -> copySavedStatesFromCarouselToFileLoader());
    }
    return copySavedStatesItem;
  }

  private JMenuItem getFixCorruptSavedStatesItem()
  {
    if (fixCorruptSavedStatesItem == null)
    {
      fixCorruptSavedStatesItem = new JMenuItem("Fix corrupt Saved states...");
      fixCorruptSavedStatesItem.setMnemonic('o');
      fixCorruptSavedStatesItem.addActionListener(e -> fixCorruptSavedStates());
    }
    return fixCorruptSavedStatesItem;
  }

  private JMenuItem getResetJoystickConfigItem()
  {
    if (resetJoystickConfigItem == null)
    {
      resetJoystickConfigItem = new JMenuItem("Reset controller configs for current gamelist view");
      resetJoystickConfigItem.setMnemonic('j');
      resetJoystickConfigItem.addActionListener(e -> resetControllerConfigs());
    }
    return resetJoystickConfigItem;
  }

  private JMenuItem getEnableAccurateDiskItem()
  {
    if (enableAccurateDiskItem == null)
    {
      enableAccurateDiskItem = new JMenuItem("Enable accurate disk for current gamelist view");
      enableAccurateDiskItem.setMnemonic('e');
      enableAccurateDiskItem.addActionListener(e -> enableAccurateDisk());
    }
    return enableAccurateDiskItem;
  }

  private JMenuItem getDisableAccurateDiskItem()
  {
    if (disableAccurateDiskItem == null)
    {
      disableAccurateDiskItem = new JMenuItem("Disable accurate disk for current gamelist view");
      disableAccurateDiskItem.setMnemonic('u');
      disableAccurateDiskItem.addActionListener(e -> disableAccurateDisk());
    }
    return disableAccurateDiskItem;
  }

  private JMenuItem getInstallPCUAEItem()
  {
    if (installPCUAEItem == null)
    {
      installPCUAEItem = new JMenuItem("Install PCUAE to a USB drive...");
      installPCUAEItem.setMnemonic('i');
      installPCUAEItem.addActionListener(e -> installPCUAE());
    }
    return installPCUAEItem;
  }

  private JMenuItem getInstallAmigaModeItem()
  {
    if (installAmigaModeItem == null)
    {
      installAmigaModeItem = new JMenuItem("Install Amiga mode...");
      installAmigaModeItem.setMnemonic('A');
      installAmigaModeItem.addActionListener(e -> installAmigaMode());
    }
    return installAmigaModeItem;
  }

  private JMenuItem getInstallAtariModeItem()
  {
    if (installAtariModeItem == null)
    {
      installAtariModeItem = new JMenuItem("Install Atari mode...");
      installAtariModeItem.setMnemonic('t');
      installAtariModeItem.addActionListener(e -> installAtariMode());
    }
    return installAtariModeItem;
  }

  private JMenuItem getInstallLinuxModeItem()
  {
    if (installLinuxModeItem == null)
    {
      installLinuxModeItem = new JMenuItem("Install Linux mode...");
      installLinuxModeItem.setMnemonic('L');
      installLinuxModeItem.addActionListener(e -> installLinuxMode());
    }
    return installLinuxModeItem;
  }

  private JMenuItem getInstallRetroarchModeItem()
  {
    if (installRetroarchModeItem == null)
    {
      installRetroarchModeItem = new JMenuItem("Install Retroarch mode...");
      installRetroarchModeItem.setMnemonic('R');
      installRetroarchModeItem.addActionListener(e -> installRetroarchMode());
    }
    return installRetroarchModeItem;
  }

  private JMenuItem getInstallViceModeItem()
  {
    if (installViceModeItem == null)
    {
      installViceModeItem = new JMenuItem("Install Vice mode...");
      installViceModeItem.setMnemonic('V');
      installViceModeItem.addActionListener(e -> installViceMode());
    }
    return installViceModeItem;
  }

  private JMenuItem getInstallMSXModeItem()
  {
    if (installMSXModeItem == null)
    {
      installMSXModeItem = new JMenuItem("Install MSX/Colecovision mode...");
      installMSXModeItem.setMnemonic('M');
      installMSXModeItem.addActionListener(e -> installMSXMode());
    }
    return installMSXModeItem;
  }

  private JMenuItem getInstallScummVMModeItem()
  {
    if (installScummVMModeItem == null)
    {
      installScummVMModeItem = new JMenuItem("Install ScummVM mode...");
      installScummVMModeItem.setMnemonic('s');
      installScummVMModeItem.addActionListener(e -> installScummVMMode());
    }
    return installScummVMModeItem;
  }

  private JMenuItem getDeleteInstallFilesItem()
  {
    if (deleteInstallFilesItem == null)
    {
      deleteInstallFilesItem = new JMenuItem("Delete all installation files in install folder");
      deleteInstallFilesItem.setMnemonic('d');
      deleteInstallFilesItem.addActionListener(e -> deleteInstallFiles());
    }
    return deleteInstallFilesItem;
  }

  private JMenuItem getHelpItem()
  {
    helpItem = new JMenuItem("Help");
    KeyStroke keyStrokeToImportGames = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
    helpItem.setAccelerator(keyStrokeToImportGames);
    helpItem.setMnemonic('h');
    helpItem.addActionListener(e -> {
      try
      {
        Desktop.getDesktop().browse(new URI("https://github.com/lantzelot-swe/PCUGameManager/wiki"));
      }
      catch (IOException | URISyntaxException ex)
      {
        JOptionPane.showMessageDialog(MainWindow.getInstance(),
                                      "Could not open help",
                                      "Help missing",
                                      JOptionPane.ERROR_MESSAGE);
      }
    });
    return helpItem;
  }

  private JMenuItem getPcuaeWikiItem()
  {
    pcuaeWikiItem = new JMenuItem("PCUAE wiki");
    KeyStroke keyStrokeToImportGames = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
    pcuaeWikiItem.setAccelerator(keyStrokeToImportGames);
    pcuaeWikiItem.setMnemonic('p');
    pcuaeWikiItem.addActionListener(e -> {
      try
      {
        Desktop.getDesktop().browse(new URI("https://github.com/CommodoreOS/PCUAE/wiki"));
      }
      catch (IOException | URISyntaxException ex)
      {
        JOptionPane.showMessageDialog(MainWindow.getInstance(),
                                      "Could not open PCUAE wiki",
                                      "Help missing",
                                      JOptionPane.ERROR_MESSAGE);
      }
    });
    return pcuaeWikiItem;
  }

  private JMenuItem getAboutItem()
  {
    aboutItem = new JMenuItem("About...");
    aboutItem.setMnemonic('a');
    aboutItem.addActionListener(e -> {
      AboutDialog dialog = new AboutDialog();
      dialog.pack();
      dialog.setLocationRelativeTo(MainWindow.getInstance());
      dialog.setVisible(true);
    });
    return aboutItem;
  }

  private JMenuItem getCheckVersionItem()
  {
    newVersionItem = new JMenuItem("Check for updates");
    newVersionItem.setMnemonic('c');
    newVersionItem.addActionListener(e -> {
      checkForNewRelease();
    });
    return newVersionItem;
  }

  private void importCarouselGames()
  {
    ImportOptionsDialog optionsDialog = new ImportOptionsDialog(MainWindow.getInstance(), true);
    optionsDialog.pack();
    optionsDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (optionsDialog.showDialog())
    {
      importManager.setSelectedFolderForCarousels(optionsDialog.getImportDirectory());
      importManager.setSelectedOption(optionsDialog.getSelectedOption());
      importManager.setAddAsFavorite(optionsDialog.getMarkAsFavorite());
      importManager.setViewTag(optionsDialog.getViewTag());
      //This will reset the other options if selected
      importManager.setCreateGameViews(optionsDialog.isCreateGameViews());
      ImportProgressDialog dialog = new ImportProgressDialog(MainWindow.getInstance());
      CarouselImportWorker worker = new CarouselImportWorker(importManager, dialog);
      worker.execute();
      dialog.setVisible(true);
      //Refresh game views after import
      uiModel.reloadGameViews();
      MainWindow.getInstance().selectViewAfterRestore();
      MainWindow.getInstance().repaintAfterModifications();
    }
  }

  private void importGamebaseGames()
  {
    ImportOptionsDialog optionsDialog = new ImportOptionsDialog(MainWindow.getInstance(), false);
    optionsDialog.pack();
    optionsDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (optionsDialog.showDialog(gamebaseImporter))
    {
      //Set selected option in gamebaseImporter from the dialog.
      if (gamebaseImporter.setImportOptions(optionsDialog.getSelectedGbOptions()))
      {
        //Set options for how to handle games during import
        importManager.setSelectedOption(optionsDialog.getSelectedOption());
        importManager.setAddAsFavorite(optionsDialog.getMarkAsFavorite());
        importManager.setViewTag(optionsDialog.getViewTag());
        importManager.setCreateGameViews(false);
        ImportProgressDialog dialog = new ImportProgressDialog(MainWindow.getInstance());
        GamebaseImportWorker worker = new GamebaseImportWorker(gamebaseImporter, importManager, dialog);
        worker.execute();
        dialog.setVisible(true);
        //Refresh game views after import
        uiModel.reloadGameViews();
        MainWindow.getInstance().selectViewAfterRestore();
        MainWindow.getInstance().repaintAfterModifications();
        MainWindow.getInstance().refreshMenuAndUI();
      }
      else
      {
        JOptionPane.showMessageDialog(MainWindow.getInstance(),
                                      "Could not read Paths.ini, see log for details.",
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void importSavedStates()
  {
    final ImportSavedStatesDialog importSavedStatesDialog = new ImportSavedStatesDialog(MainWindow.getInstance());
    importSavedStatesDialog.pack();
    importSavedStatesDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (importSavedStatesDialog.showDialog())
    {
      savedStatesManager.setImportDirectory(importSavedStatesDialog.getTargetDirectory());
      savedStatesManager.setImportOverwrite(importSavedStatesDialog.isImportOverwrite());
      ImportExportProgressDialog dialog =
        new ImportExportProgressDialog(MainWindow.getInstance(), "Import saved states", DIALOGTYPE.IMPORT);
      ImportSavedStatesWorker worker = new ImportSavedStatesWorker(savedStatesManager, dialog);
      worker.execute();
      dialog.setVisible(true);
    }
  }

  private void exportSavedStates()
  {
    final ExportSavedStatesDialog exportSavedStatesDialog = new ExportSavedStatesDialog(MainWindow.getInstance());
    exportSavedStatesDialog.pack();
    exportSavedStatesDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (exportSavedStatesDialog.showDialog())
    {
      savedStatesManager.setExportDirectory(exportSavedStatesDialog.getTargetDirectory());
      savedStatesManager.setExportOverwrite(exportSavedStatesDialog.isExportOverwrite());
      ImportExportProgressDialog dialog =
        new ImportExportProgressDialog(MainWindow.getInstance(), "Export saved states", DIALOGTYPE.EXPORT);
      ExportSavedStatesWorker worker = new ExportSavedStatesWorker(savedStatesManager, dialog);
      worker.execute();
      dialog.setVisible(true);
    }
  }

  private void exportGames()
  {
    final ExportGamesDialog exportSelectionDialog = new ExportGamesDialog(MainWindow.getInstance(), true);
    exportSelectionDialog.pack();
    exportSelectionDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (exportSelectionDialog.showDialog())
    {
      if (exportSelectionDialog.isExportGameViews())
      {
        List<GameView> viewList = exportSelectionDialog.getSelectedGameViews();
        if (!viewList.isEmpty())
        {
          exportManager.setGameViewsToExport(viewList);
          exportManager.setDeleteBeforeExport(exportSelectionDialog.deleteBeforeExport());
          exportManager.setTargetDirectory(exportSelectionDialog.getTargetDirectory());
          ImportExportProgressDialog dialog =
            new ImportExportProgressDialog(MainWindow.getInstance(), "Export games", DIALOGTYPE.EXPORT);
          ExportWorker worker = new ExportWorker(exportManager, dialog);
          worker.execute();
          dialog.setVisible(true);
        }
      }
      else
      {
        List<GameListData> gamesList = exportSelectionDialog.getSelectedGames();
        if (!gamesList.isEmpty())
        {
          exportManager.setGamesToExport(gamesList);
          exportManager.setDeleteBeforeExport(exportSelectionDialog.deleteBeforeExport());
          exportManager.setTargetDirectory(exportSelectionDialog.getTargetDirectory());
          ImportExportProgressDialog dialog =
            new ImportExportProgressDialog(MainWindow.getInstance(), "Export games", DIALOGTYPE.EXPORT);
          ExportWorker worker = new ExportWorker(exportManager, dialog);
          worker.execute();
          dialog.setVisible(true);
        }
      }
    }
  }

  private void exportGamesToFileLoader()
  {
    final ExportGamesDialog exportSelectionDialog = new ExportGamesDialog(MainWindow.getInstance(), false);
    exportSelectionDialog.pack();
    exportSelectionDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (exportSelectionDialog.showDialog())
    {
      if (exportSelectionDialog.isExportGameViews())
      {
        List<GameView> viewList = exportSelectionDialog.getSelectedGameViews();
        if (!viewList.isEmpty())
        {
          exportManager.setGameViewsToExport(viewList);
          exportManager.setDeleteBeforeExport(exportSelectionDialog.deleteBeforeExport());
          exportManager.setTargetDirectory(exportSelectionDialog.getTargetDirectory());
          ImportExportProgressDialog dialog =
            new ImportExportProgressDialog(MainWindow.getInstance(), "Export games", DIALOGTYPE.EXPORT);
          ExportFileLoaderWorker worker = new ExportFileLoaderWorker(exportManager, dialog);
          worker.execute();
          dialog.setVisible(true);
        }
      }
      else
      {
        List<GameListData> gamesList = exportSelectionDialog.getSelectedGames();
        if (!gamesList.isEmpty())
        {
          exportManager.setGamesToExport(gamesList);
          exportManager.setDeleteBeforeExport(exportSelectionDialog.deleteBeforeExport());
          exportManager.setTargetDirectory(exportSelectionDialog.getTargetDirectory());
          ImportExportProgressDialog dialog =
            new ImportExportProgressDialog(MainWindow.getInstance(), "Export games", DIALOGTYPE.EXPORT);
          ExportFileLoaderWorker worker = new ExportFileLoaderWorker(exportManager, dialog);
          worker.execute();
          dialog.setVisible(true);
        }
      }
    }
  }

  private void editPreferences()
  {
    PreferencesDialog prefDialog = new PreferencesDialog(MainWindow.getInstance());
    prefDialog.pack();
    prefDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (prefDialog.showDialog())
    {
      prefDialog.savePreferences();
      //Update favorites menu
      MainWindow.getInstance().refreshMenuAndUI();
      //Refresh game views
      uiModel.reloadGameViews();
      //Set all games as selected
      uiModel.setSelectedGameView(null);
    }
  }

  private void reloadView()
  {
    MainWindow.getInstance().reloadCurrentGameView();
  }

  private void reloadAll()
  {
    MainWindow.getInstance().setWaitCursor(true);
    //Refresh game views
    uiModel.reloadGameViews();
    MainWindow.getInstance().getMainPanel().getListPanel().clearFilter();
    MainWindow.getInstance().refreshMenuAndUI();
    MainWindow.getInstance().setWaitCursor(false);
  }

  private void backupDb()
  {
    BackupProgressDialog dialog = new BackupProgressDialog(MainWindow.getInstance());
    BackupWorker worker = new BackupWorker(backupManager, dialog);
    worker.execute();
    dialog.setVisible(true);
  }

  private void restoreDb()
  {
    RestoreDbDialog restoreDialog = new RestoreDbDialog(MainWindow.getInstance());
    restoreDialog.pack();
    restoreDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (restoreDialog.showDialog())
    {
      restoreManager.setBackupFolderName(restoreDialog.getSelectedFolder());
      RestoreProgressDialog progressDialog = new RestoreProgressDialog(MainWindow.getInstance());
      RestoreWorker worker = new RestoreWorker(restoreManager, progressDialog);
      worker.execute();
      progressDialog.setVisible(true);
      //Trigger a reload of game views
      uiModel.reloadGameViews();
      MainWindow.getInstance().selectViewAfterRestore();
    }
  }

  private void deleteAllGames()
  {
    DeleteDialog dialog = new DeleteDialog(TYPE_OF_DELETE.ALL);
    dialog.pack();
    dialog.setLocationRelativeTo(MainWindow.getInstance());
    if (dialog.showDialog())
    {
      if (dialog.isCreatebackup())
      {
        backupDb();
      }
      startDeleteProgress(TYPE_OF_DELETE.ALL);
    }
  }

  private void deleteAllGamelistViews()
  {
    DeleteDialog dialog = new DeleteDialog(TYPE_OF_DELETE.ALL_VIEWS);
    dialog.pack();
    dialog.setLocationRelativeTo(MainWindow.getInstance());
    if (dialog.showDialog())
    {
      startDeleteProgress(TYPE_OF_DELETE.ALL_VIEWS);
    }
  }

  private void deleteAllGamesInView()
  {
    if (uiModel.getSelectedGameView().getGameViewId() == GameView.ALL_GAMES_ID)
    {
      deleteAllGames();
    }
    else
    {
      DeleteDialog dialog = new DeleteDialog(TYPE_OF_DELETE.VIEW);
      dialog.pack();
      dialog.setLocationRelativeTo(MainWindow.getInstance());
      if (dialog.showDialog())
      {
        if (dialog.isCreatebackup())
        {
          backupDb();
        }
        startDeleteProgress(TYPE_OF_DELETE.VIEW);
      }
    }
  }

  private void startDeleteProgress(TYPE_OF_DELETE typeOfDelete)
  {
    MainWindow.getInstance().getMainPanel().clearGameListSelection();
    DeleteProgressDialog delDialog = new DeleteProgressDialog(MainWindow.getInstance());
    delDialog.pack();
    delDialog.setLocationRelativeTo(MainWindow.getInstance());
    DeleteWorker worker = new DeleteWorker(delDialog, typeOfDelete, uiModel);
    worker.execute();
    delDialog.setVisible(true);
  }

  private void fixPalNtscIssue()
  {
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance().getMainPanel(),
                                               getPalNtscEditorPane(),
                                               "Swap game file and first saved state",
                                               JOptionPane.YES_NO_OPTION,
                                               JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      if (savedStatesManager.swapGameFileAndSavedState())
      {
        JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(),
                                      "Game file and saved state successfully swapped. System type was also switched.",
                                      "Swap game file and first saved state",
                                      JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  private void validateDb()
  {
    String message =
      "<html>Do you want to validate the database? The following actions will be performed: <ul><li>Check all description texts in the database and remove all carrage return (CR) characters.<br>Earlier versions of the manager allowed for CR characters, the Carousel " +
        "does not handle that properly.<br>CR characters will be replaced by a space character.</li><br><li>Check all screenshots and convert them to use 32-bit color depths.<br>The Carousel Gamelist Loader screen requires 32-bit depths for the screenshots to be rendered properly.</li><br>" +
        "<li>Verify that all covers, screenshots and game files are available in the folders<br>and replace any missing ones with generic versions.</li></ul></html>";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Validate database", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      DbValidationProgressDialog dialog = new DbValidationProgressDialog(MainWindow.getInstance());
      DbValidationWorker worker = new DbValidationWorker(dialog, this.uiModel.getDbConnector());
      worker.execute();
      dialog.setVisible(true);
    }
  }

  private void convertSavedStates()
  {
    //First check how many old saves there is, then ask if they shall be converted.
    int numberOf132Games = savedStatesManager.checkFor132SavedStates();

    if (numberOf132Games == 0)
    {
      JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(),
                                    "No saved states uses the old Carousel version 1.3.2 format. All is up to date.",
                                    "Convert saved states to Carousel 1.5.2 format",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    else
    {
      String message = String
        .format("<html>There are %s games saved with the old format used by the Carousel version 1.3.2 and earlier.<br>Do you want to convert them to the new format so that they can be used by the Carousel version 1.5.2 and later?</html>",
                Integer.valueOf(numberOf132Games));
      int option = JOptionPane.showConfirmDialog(MainWindow.getInstance().getMainPanel(),
                                                 message,
                                                 "Convert saved states to Carousel 1.5.2 format",
                                                 JOptionPane.YES_NO_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);
      if (option == JOptionPane.YES_OPTION)
      {
        savedStatesManager.convertToCarousel152Version();
        //Refresh after converting
        MainWindow.getInstance().refreshMenuAndUI();
        //Refresh game views
        uiModel.reloadGameViews();
        //Set all games as selected
        uiModel.setSelectedGameView(null);
      }
    }
  }

  private void copySavedStatesFromCarouselToFileLoader()
  {

    JOptionPane
      .showMessageDialog(MainWindow.getInstance().getMainPanel(),
                         "<html>A Carousel saves the saved states for a game in a folder named after the game file name.<br>When exporting games to File Loader the " +
                           "file name will be based on the title for the game, so the carousel saved states<br>will not be available for exported games.<p><p>With this " +
                           "function you can copy existing saved states for the games in the carousel to a new folder that the File loader will find. <br>It will check all " +
                           "imported saved states and match them towards available games in the manager and create a new folder in the saves directory.<br>You can then export the saved states to your USB stick to use them in the File Loader. <p><p>Press OK to check for saved states that can be copied.</html>",
                         "Copy saved states to File Loader",
                         JOptionPane.INFORMATION_MESSAGE);

    int numberOfSavesNotAvailableForFileLoader = savedStatesManager.checkForSavedStatesToCopyToFileLoader();

    if (numberOfSavesNotAvailableForFileLoader == 0)
    {
      JOptionPane
        .showMessageDialog(MainWindow.getInstance().getMainPanel(),
                           "No carousel saved states exists that are not available for File Loader. All is up to date.",
                           "Copy saved states to File Loader",
                           JOptionPane.INFORMATION_MESSAGE);
    }
    else
    {
      String message = String
        .format("<html>There are %s games that have saved states used by the Carousel version 1.5.2 or later.<br>Do you want to copy them to the File Loader format so that they are accessible from the File Loader?</html>",
                Integer.valueOf(numberOfSavesNotAvailableForFileLoader));
      int option = JOptionPane.showConfirmDialog(MainWindow.getInstance().getMainPanel(),
                                                 message,
                                                 "Copy saved states to File Loader",
                                                 JOptionPane.YES_NO_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);
      if (option == JOptionPane.YES_OPTION)
      {
        savedStatesManager.copyFromCarouselToFileLoader();
        //Refresh after converting
        MainWindow.getInstance().refreshMenuAndUI();
        //Refresh game views
        uiModel.reloadGameViews();
        //Set all games as selected
        uiModel.setSelectedGameView(null);
      }
    }
  }

  private void fixCorruptSavedStates()
  {
    FixCorruptSavedStatesDialog corruptedDialog = new FixCorruptSavedStatesDialog();
    corruptedDialog.pack();
    corruptedDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (corruptedDialog.showDialog())
    {
      savedStatesManager.setFixDirectory(corruptedDialog.getTargetDirectory());
      ImportExportProgressDialog dialog =
        new ImportExportProgressDialog(MainWindow.getInstance(), "Fix corrupt saved states", DIALOGTYPE.FIX);
      FixCorruptSavedStatesWorker worker = new FixCorruptSavedStatesWorker(savedStatesManager, dialog);
      worker.execute();
      dialog.setVisible(true);
    }
  }

  private void resetControllerConfigs()
  {
    String message =
      "Do you want to reset the controller configurations for all games in the current gamelist view?\n" +
        "Only the mappings are reset to the default (defined in preferences), primary controller port is preserved.\n" +
        "The second controller is also reset with the default mappings.";

    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance().getMainPanel(),
                                               message,
                                               "Reset controller configurations",
                                               JOptionPane.YES_NO_OPTION,
                                               JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.resetJoystickConfigsForCurrentView();
      JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(),
                                    "Controller configurations updated.",
                                    "Reset controller configurations",
                                    JOptionPane.INFORMATION_MESSAGE);
      MainWindow.getInstance().reloadCurrentGameView();
    }
  }

  private void enableAccurateDisk()
  {
    String message = "Do you want to enable accurate disk for all disk games in the current gamelist view?";

    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Enable accurate disk", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.enableAccurateDiskForAllGamesInCurrentView();
      JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(),
                                    "Accurate disk enabled for all disk games.",
                                    "Enable accurate disk",
                                    JOptionPane.INFORMATION_MESSAGE);
      MainWindow.getInstance().reloadCurrentGameView();
    }
  }

  private void disableAccurateDisk()
  {
    String message = "Do you want to disable accurate disk for all disk games in the current gamelist view?";

    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Disable accurate disk", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.disableAccurateDiskForAllGamesInCurrentView();
      JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(),
                                    "Accurate disk disabled for all disk games.",
                                    "Disable accurate disk",
                                    JOptionPane.INFORMATION_MESSAGE);
      MainWindow.getInstance().reloadCurrentGameView();
    }
  }

  private void installPCUAE()
  {
    installPCUAEManager.installPCUAE();
  }

  private void installAmigaMode()
  {
    installAmigaManager.installAmigaMode();
  }

  private void installAtariMode()
  {
    installAtariManager.installAtariMode();
  }

  private void installLinuxMode()
  {
    installLinuxManager.installLinuxMode();
  }

  private void installRetroarchMode()
  {
    installRetroarchManager.installRetroarchMode();
  }

  private void installViceMode()
  {
    installViceManager.installViceMode();
  }

  private void installScummVMMode()
  {
    installScummVMManager.installScummVMMode();
  }

  private void installMSXMode()
  {
    installMSXManager.installMSXMode();
  }

  private void deleteInstallFiles()
  {
    String message = "Are you sure you want to delete all downloaded PCUAE installation files from the install folder?";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance().getMainPanel(),
                                               message,
                                               "Delete all installation files",
                                               JOptionPane.YES_NO_OPTION,
                                               JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      installPCUAEManager.deleteAllInstallFiles();
      JOptionPane.showMessageDialog(MainWindow.getInstance(),
                                    "All files deleted.",
                                    "Delete all installation files",
                                    JOptionPane.INFORMATION_MESSAGE);

    }
  }

  private JEditorPane getPalNtscEditorPane()
  {
    String message =
      "<html>Some VICE snapshots of games made for NTSC does not run properly on a PAL machine, and vice versa. You need to start them twice from the carousel." +
        "<br>See this thread on <a href=\"https://thec64community.online/thread/790/pcuae-problem-starting-games-first\">TheC64 Community forum</a>." +
        "<br><br>Do you want to swap the game file with the first saved state and change system type for the selected game?</html>";

    JEditorPane infoEditorPane = new JEditorPane("text/html", message);
    infoEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    infoEditorPane.setFont(UIManager.getDefaults().getFont("Label.font"));
    infoEditorPane.setEditable(false);
    infoEditorPane.setOpaque(false);
    infoEditorPane.addHyperlinkListener((hle) -> {
      if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()) && Desktop.isDesktopSupported() &&
        Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
      {
        try
        {
          Desktop.getDesktop().browse(hle.getURL().toURI());
        }
        catch (IOException | URISyntaxException e)
        {
          ExceptionHandler.handleException(e, "Could not open default browser");
        }
      }
    });
    return infoEditorPane;
  }

  private void clearFavorites(int number)
  {
    String message = "Are you sure you want to clear all games marked as favorites " + number + "?";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance().getMainPanel(),
                                               message,
                                               "Clear all favorites " + number,
                                               JOptionPane.YES_NO_OPTION,
                                               JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.clearFavorites(number);
    }
  }

  private void checkForNewRelease()
  {
    ManagerVersionChecker.fetchLatestVersionFromGithub();
    if (ManagerVersionChecker.isNewVersionAvailable())
    {
      ManagerDownloadDialog dialog = new ManagerDownloadDialog(MainWindow.getInstance());
      dialog.pack();
      dialog.setLocationRelativeTo(MainWindow.getInstance());
      if (dialog.showDialog())
      {
        ManagerVersionChecker.updateVersion();
      }
    }
    else
    {
      JOptionPane.showMessageDialog(MainWindow.getInstance(),
                                    "This is the latest version.",
                                    "Version check",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
  }

  void checkForNewPCUAEVersionAtStartup()
  {
    installPCUAEManager.checkForNewVersionAtStartup();
  }
}
