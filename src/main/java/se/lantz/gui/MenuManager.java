package se.lantz.gui;

import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import se.lantz.gamebase.GamebaseImporter;
import se.lantz.gui.convertscreens.ConvertProgressDialog;
import se.lantz.gui.convertscreens.ConvertWorker;
import se.lantz.gui.dbbackup.BackupProgressDialog;
import se.lantz.gui.dbbackup.BackupWorker;
import se.lantz.gui.dbrestore.RestoreDbDialog;
import se.lantz.gui.dbrestore.RestoreProgressDialog;
import se.lantz.gui.dbrestore.RestoreWorker;
import se.lantz.gui.exports.ExportFileLoaderWorker;
import se.lantz.gui.exports.ExportGamesDialog;
import se.lantz.gui.exports.ExportProgressDialog;
import se.lantz.gui.exports.ExportWorker;
import se.lantz.gui.imports.CarouselImportWorker;
import se.lantz.gui.imports.GamebaseImportWorker;
import se.lantz.gui.imports.ImportOptionsDialog;
import se.lantz.gui.imports.ImportProgressDialog;
import se.lantz.manager.BackupManager;
import se.lantz.manager.ExportManager;
import se.lantz.manager.ImportManager;
import se.lantz.manager.RestoreManager;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;
import se.lantz.util.VersionChecker;

public class MenuManager
{
  private JMenu fileMenu;
  private JMenu importMenu;
  private JMenu exportMenu;
  private JMenu editMenu;
  private JMenu toolsMenu;
  private JMenu helpMenu;

  private JMenuItem addGameItem;
  private JMenuItem deleteGameItem;

  private JMenuItem runGameItem;
  private JMenuItem importCarouselItem;
  private JMenuItem importGamebaseItem;
  private JMenuItem exportItem;
  private JMenuItem exportFLItem;
  private JMenuItem refreshItem;

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

  private JMenuItem backupDbItem;
  private JMenuItem restoreDbItem;
  private JMenuItem deleteAllGamesItem;
  private JMenuItem deleteGamesForViewItem;

  private JMenuItem convertScreensItem;

  private JMenuItem helpItem;
  private JMenuItem aboutItem;
  private JMenuItem newVersionItem;

  private JMenuItem exitItem;
  private MainViewModel uiModel;
  private ImportManager importManager;
  private GamebaseImporter gamebaseImporter;
  private ExportManager exportManager;
  private BackupManager backupManager;
  private RestoreManager restoreManager;
  private MainWindow mainWindow;

  public MenuManager(final MainViewModel uiModel, MainWindow mainWindow)
  {
    this.uiModel = uiModel;
    this.mainWindow = mainWindow;
    this.importManager = new ImportManager(uiModel);
    this.gamebaseImporter = new GamebaseImporter(importManager);
    this.exportManager = new ExportManager(uiModel);
    this.backupManager = new BackupManager(uiModel);
    this.restoreManager = new RestoreManager(uiModel);
    setupMenues();
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
    fileMenu.add(getDeleteGameMenuItem());
    fileMenu.addSeparator();
    fileMenu.add(getRunGameMenuItem());
    fileMenu.addSeparator();
    importMenu = new JMenu("Import");
    importMenu.setMnemonic('I');
    fileMenu.add(importMenu);
    importMenu.add(getImportCarouselItem());
    importMenu.add(getImportGamebaseItem());
    exportMenu = new JMenu("Export");
    exportMenu.setMnemonic('X');
    exportMenu.add(getExportItem());
    exportMenu.add(getExportFileLoaderItem());
    fileMenu.add(exportMenu);
    fileMenu.addSeparator();
    fileMenu.add(getRefreshItem());
    fileMenu.addSeparator();
    fileMenu.add(getExitItem());
    editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');
    editMenu.add(getToggleFavorite1Item());
    editMenu.add(getToggleFavorite2Item());
    editMenu.add(getToggleFavorite3Item());
    editMenu.add(getToggleFavorite4Item());
    editMenu.add(getToggleFavorite5Item());
    editMenu.add(getToggleFavorite6Item());
    editMenu.add(getToggleFavorite7Item());
    editMenu.add(getToggleFavorite8Item());
    editMenu.add(getToggleFavorite9Item());
    editMenu.add(getToggleFavorite10Item());
    editMenu.addSeparator();
    editMenu.add(getClearFavorites1Item());
    editMenu.add(getClearFavorites2Item());
    editMenu.add(getClearFavorites3Item());
    editMenu.add(getClearFavorites4Item());
    editMenu.add(getClearFavorites5Item());
    editMenu.add(getClearFavorites6Item());
    editMenu.add(getClearFavorites7Item());
    editMenu.add(getClearFavorites8Item());
    editMenu.add(getClearFavorites9Item());
    editMenu.add(getClearFavorites10Item());
    toolsMenu = new JMenu("Tools");
    toolsMenu.setMnemonic('T');
    toolsMenu.add(getBackupDbItem());
    toolsMenu.add(getRestoreDbItem());
    toolsMenu.addSeparator();
    toolsMenu.add(getDeleteAllGamesItem());
    toolsMenu.add(getDeleteGamesForViewMenuItem());
    toolsMenu.addSeparator();
    toolsMenu.add(getConvertScreensItem());
    helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');
    helpMenu.add(getHelpItem());
    helpMenu.add(getCheckVersionItem());
    helpMenu.add(getAboutItem());
  }

  public void intialize()
  {
    uiModel.addSaveChangeListener(e -> {
      boolean okToEnable = !uiModel.isDataChanged();
      addGameItem.setEnabled(okToEnable);
      importMenu.setEnabled(okToEnable);
      exportItem.setEnabled(okToEnable);
      toolsMenu.setEnabled(okToEnable);
      editMenu.setEnabled(okToEnable);
      runGameItem.setEnabled(!uiModel.getInfoModel().getGamesFile().isEmpty());
      refreshItem.setEnabled(okToEnable);
    });
  }

  public List<JMenu> getMenues()
  {
    List<JMenu> menuList = new ArrayList<JMenu>();
    menuList.add(fileMenu);
    menuList.add(editMenu);
    menuList.add(toolsMenu);
    menuList.add(helpMenu);
    return menuList;
  }

  JMenuItem getAddGameMenuItem()
  {
    addGameItem = new JMenuItem("Add new game");
    KeyStroke keyStrokeToAddGame = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
    addGameItem.setAccelerator(keyStrokeToAddGame);
    addGameItem.setMnemonic('N');

    addGameItem.addActionListener(e -> mainWindow.getMainPanel().addNewGame());
    return addGameItem;
  }

  JMenuItem getDeleteGameMenuItem()
  {
    deleteGameItem = new JMenuItem("Delete selected game(s)");
    KeyStroke keyStrokeToAddGame = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
    deleteGameItem.setAccelerator(keyStrokeToAddGame);
    deleteGameItem.setMnemonic('D');

    deleteGameItem.addActionListener(e -> mainWindow.getMainPanel().deleteCurrentGame());
    return deleteGameItem;
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

    runGameItem.addActionListener(e -> mainWindow.getMainPanel().runCurrentGame());
    return runGameItem;
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

  private JMenuItem getRefreshItem()
  {
    refreshItem = new JMenuItem("Reload current gamelist view");
    KeyStroke keyStrokeToReloadGameView = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
    refreshItem.setAccelerator(keyStrokeToReloadGameView);
    refreshItem.setMnemonic('C');
    refreshItem.addActionListener(e -> reloadView());
    return refreshItem;
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
        int value = mainWindow.getMainPanel().showUnsavedChangesDialog();
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
    toggleFavorite1Item = new JMenuItem("Add/remove from favorites 1");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite1Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite1Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite1Item;
  }

  private JMenuItem getToggleFavorite2Item()
  {
    toggleFavorite2Item = new JMenuItem("Add/remove from favorites 2");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite2Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite2Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite2();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite2Item;
  }

  private JMenuItem getToggleFavorite3Item()
  {
    toggleFavorite3Item = new JMenuItem("Add/remove from favorites 3");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite3Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite3Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite3();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite3Item;
  }

  private JMenuItem getToggleFavorite4Item()
  {
    toggleFavorite4Item = new JMenuItem("Add/remove from favorites 4");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite4Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite4Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite4();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite4Item;
  }

  private JMenuItem getToggleFavorite5Item()
  {
    toggleFavorite5Item = new JMenuItem("Add/remove from favorites 5");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite5Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite5Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite5();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite5Item;
  }

  private JMenuItem getToggleFavorite6Item()
  {
    toggleFavorite6Item = new JMenuItem("Add/remove from favorites 6");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite6Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite6Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite6();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite6Item;
  }

  private JMenuItem getToggleFavorite7Item()
  {
    toggleFavorite7Item = new JMenuItem("Add/remove from favorites 7");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite7Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite7Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite7();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite7Item;
  }

  private JMenuItem getToggleFavorite8Item()
  {
    toggleFavorite8Item = new JMenuItem("Add/remove from favorites 8");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite8Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite8Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite8();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite8Item;
  }

  private JMenuItem getToggleFavorite9Item()
  {
    toggleFavorite9Item = new JMenuItem("Add/remove from favorites 9");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite9Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite9Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite9();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite9Item;
  }

  private JMenuItem getToggleFavorite10Item()
  {
    toggleFavorite10Item = new JMenuItem("Add/remove from favorites 10");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.CTRL_DOWN_MASK);
    toggleFavorite10Item.setAccelerator(keyStrokeToToggleFav);
    toggleFavorite10Item.addActionListener(e -> {
      mainWindow.setWaitCursor(true);
      mainWindow.getMainPanel().toggleFavorite10();
      mainWindow.setWaitCursor(false);
    });
    return toggleFavorite10Item;
  }

  private JMenuItem getClearFavorites1Item()
  {
    clearFavorites1Item = new JMenuItem("Clear favorites 1");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites1Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites1Item.addActionListener(e -> {
      clearFavorites(1);
    });
    return clearFavorites1Item;
  }

  private JMenuItem getClearFavorites2Item()
  {
    clearFavorites2Item = new JMenuItem("Clear favorites 2");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites2Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites2Item.addActionListener(e -> {
      clearFavorites(2);
    });
    return clearFavorites2Item;
  }

  private JMenuItem getClearFavorites3Item()
  {
    clearFavorites3Item = new JMenuItem("Clear favorites 3");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites3Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites3Item.addActionListener(e -> {
      clearFavorites(3);
    });
    return clearFavorites3Item;
  }

  private JMenuItem getClearFavorites4Item()
  {
    clearFavorites4Item = new JMenuItem("Clear favorites 4");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites4Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites4Item.addActionListener(e -> {
      clearFavorites(4);
    });
    return clearFavorites4Item;
  }

  private JMenuItem getClearFavorites5Item()
  {
    clearFavorites5Item = new JMenuItem("Clear favorites 5");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites5Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites5Item.addActionListener(e -> {
      clearFavorites(5);
    });
    return clearFavorites5Item;
  }

  private JMenuItem getClearFavorites6Item()
  {
    clearFavorites6Item = new JMenuItem("Clear favorites 6");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites6Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites6Item.addActionListener(e -> {
      clearFavorites(6);
    });
    return clearFavorites6Item;
  }

  private JMenuItem getClearFavorites7Item()
  {
    clearFavorites7Item = new JMenuItem("Clear favorites 7");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites7Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites7Item.addActionListener(e -> {
      clearFavorites(7);
    });
    return clearFavorites7Item;
  }

  private JMenuItem getClearFavorites8Item()
  {
    clearFavorites8Item = new JMenuItem("Clear favorites 8");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites8Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites8Item.addActionListener(e -> {
      clearFavorites(8);
    });
    return clearFavorites8Item;
  }

  private JMenuItem getClearFavorites9Item()
  {
    clearFavorites9Item = new JMenuItem("Clear favorites 9");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites9Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites9Item.addActionListener(e -> {
      clearFavorites(9);
    });
    return clearFavorites9Item;
  }

  private JMenuItem getClearFavorites10Item()
  {
    clearFavorites10Item = new JMenuItem("Clear favorites 10");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites10Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites10Item.addActionListener(e -> {
      clearFavorites(10);
    });
    return clearFavorites10Item;
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

  private JMenuItem getConvertScreensItem()
  {
    convertScreensItem = new JMenuItem("Convert screenshots...");
    convertScreensItem.setMnemonic('c');
    convertScreensItem.addActionListener(e -> convertScreens());
    return convertScreensItem;
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

  private JMenuItem getAboutItem()
  {
    aboutItem = new JMenuItem("About...");
    aboutItem.setMnemonic('a');
    aboutItem.addActionListener(e -> {
      AboutDialog dialog = new AboutDialog();
      dialog.pack();
      dialog.setLocationRelativeTo(this.mainWindow);
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
    ImportOptionsDialog optionsDialog = new ImportOptionsDialog(this.mainWindow, true);
    optionsDialog.pack();
    optionsDialog.setLocationRelativeTo(this.mainWindow);
    if (optionsDialog.showDialog())
    {
      importManager.setSelectedFolderForCarousel(optionsDialog.getImportDirectory());
      importManager.setSelectedOption(optionsDialog.getSelectedOption());
      importManager.setAddAsFavorite(optionsDialog.getMarkAsFavorite());
      importManager.setViewTag(optionsDialog.getViewTag());
      ImportProgressDialog dialog = new ImportProgressDialog(this.mainWindow);
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
    ImportOptionsDialog optionsDialog = new ImportOptionsDialog(this.mainWindow, false);
    optionsDialog.pack();
    optionsDialog.setLocationRelativeTo(this.mainWindow);
    if (optionsDialog.showDialog())
    {
      //Set selected option in gamebaseImporter from the dialog.
      if (gamebaseImporter.setImportOptions(optionsDialog.getSelectedGbOptions()))
      {
        //Set options for how to handle games during import
        importManager.setSelectedOption(optionsDialog.getSelectedOption());
        importManager.setAddAsFavorite(optionsDialog.getMarkAsFavorite());
        importManager.setViewTag(optionsDialog.getViewTag());
        ImportProgressDialog dialog = new ImportProgressDialog(this.mainWindow);
        GamebaseImportWorker worker = new GamebaseImportWorker(gamebaseImporter, importManager, dialog);
        worker.execute();
        dialog.setVisible(true);
        //Refresh game views after import
        uiModel.reloadGameViews();
        MainWindow.getInstance().selectViewAfterRestore();
        MainWindow.getInstance().repaintAfterModifications();
      }
      else
      {
        JOptionPane.showMessageDialog(mainWindow,
                                      "Could not read Paths.ini, see log for details.",
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void exportGames()
  {
    final ExportGamesDialog exportSelectionDialog = new ExportGamesDialog(MainWindow.getInstance(), true);
    exportSelectionDialog.pack();
    exportSelectionDialog.setLocationRelativeTo(this.mainWindow);
    if (exportSelectionDialog.showDialog())
    {
      if (exportSelectionDialog.isExportGameViews())
      {
        List<GameView> viewList = exportSelectionDialog.getSelectedGameViews();
        if (!viewList.isEmpty())
        {
          exportManager.setGameViewsToExport(viewList);
          exportManager.setTargetDirectory(exportSelectionDialog.getTargetDirectory(),
                                           exportSelectionDialog.deleteBeforeExport());
          ExportProgressDialog dialog = new ExportProgressDialog(this.mainWindow);
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
          exportManager.setTargetDirectory(exportSelectionDialog.getTargetDirectory(),
                                           exportSelectionDialog.deleteBeforeExport());
          ExportProgressDialog dialog = new ExportProgressDialog(this.mainWindow);
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
    exportSelectionDialog.setLocationRelativeTo(this.mainWindow);
    if (exportSelectionDialog.showDialog())
    {
      if (exportSelectionDialog.isExportGameViews())
      {
        List<GameView> viewList = exportSelectionDialog.getSelectedGameViews();
        if (!viewList.isEmpty())
        {
          exportManager.setGameViewsToExport(viewList);
          exportManager.setTargetDirectory(exportSelectionDialog.getTargetDirectory(),
                                           exportSelectionDialog.deleteBeforeExport());
          ExportProgressDialog dialog = new ExportProgressDialog(this.mainWindow);
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
          exportManager.setTargetDirectory(exportSelectionDialog.getTargetDirectory(),
                                           exportSelectionDialog.deleteBeforeExport());
          ExportProgressDialog dialog = new ExportProgressDialog(this.mainWindow);
          ExportFileLoaderWorker worker = new ExportFileLoaderWorker(exportManager, dialog);
          worker.execute();
          dialog.setVisible(true);
        }
      }
    }
  }

  private void reloadView()
  {
    this.mainWindow.reloadCurrentGameView();
  }

  private void backupDb()
  {
    BackupProgressDialog dialog = new BackupProgressDialog(this.mainWindow);
    BackupWorker worker = new BackupWorker(backupManager, dialog);
    worker.execute();
    dialog.setVisible(true);
  }

  private void restoreDb()
  {
    RestoreDbDialog restoreDialog = new RestoreDbDialog(MainWindow.getInstance());
    restoreDialog.pack();
    restoreDialog.setLocationRelativeTo(this.mainWindow);
    if (restoreDialog.showDialog())
    {
      restoreManager.setBackupFolderName(restoreDialog.getSelectedFolder());
      RestoreProgressDialog progressDialog = new RestoreProgressDialog(this.mainWindow);
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
    DeleteDialog dialog = new DeleteDialog(true);
    dialog.pack();
    dialog.setLocationRelativeTo(MainWindow.getInstance());
    if (dialog.showDialog())
    {
      if (dialog.isCreatebackup())
      {
        backupDb();
      }
      startDeleteProgress(true);
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
      DeleteDialog dialog = new DeleteDialog(false);
      dialog.pack();
      dialog.setLocationRelativeTo(MainWindow.getInstance());
      if (dialog.showDialog())
      {
        if (dialog.isCreatebackup())
        {
          backupDb();
        }
        startDeleteProgress(false);
      }
    }
  }

  private void startDeleteProgress(boolean deleteAll)
  {
    MainWindow.getInstance().getMainPanel().clearGameListSelection();
    DeleteProgressDialog delDialog = new DeleteProgressDialog(MainWindow.getInstance());
    delDialog.pack();
    delDialog.setLocationRelativeTo(MainWindow.getInstance());
    DeleteWorker worker = new DeleteWorker(delDialog, deleteAll, uiModel);
    worker.execute();
    delDialog.setVisible(true);
  }

  private void convertScreens()
  {
    String message =
      "Do you want to check all screenshots in the database and convert them to use 32-bit color depths?\nThe PCU list selector screen requires 32-bit depths for the screenshots to be rendered properly.";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Convert screenshots", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      ConvertProgressDialog dialog = new ConvertProgressDialog(this.mainWindow);
      ConvertWorker worker = new ConvertWorker(dialog);
      worker.execute();
      dialog.setVisible(true);
    }
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
    VersionChecker.fetchLatestVersionFromGithub();
    if (VersionChecker.isNewVersionAvailable())
    {
      VersionDownloadDialog dialog = new VersionDownloadDialog(MainWindow.getInstance());
      dialog.pack();
      dialog.setLocationRelativeTo(MainWindow.getInstance());
      if (dialog.showDialog())
      {
        getExitItem().doClick();
      }
    }
    else
    {
      JOptionPane.showMessageDialog(this.mainWindow,
                                    "This is the latest version.",
                                    "Version check",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
  }
}
