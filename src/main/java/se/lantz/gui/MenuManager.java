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
  private JMenu editMenu;
  private JMenu toolsMenu;
  private JMenu helpMenu;

  private JMenuItem addGameItem;
  private JMenuItem deleteGameItem;

  private JMenuItem runGameItem;
  private JMenuItem importCarouselItem;
  private JMenuItem importGamebaseItem;
  private JMenuItem exportItem;
  private JMenuItem refreshItem;

  private JMenuItem toggleFavorite1Item;
  private JMenuItem toggleFavorite2Item;
  private JMenuItem toggleFavorite3Item;
  private JMenuItem toggleFavorite4Item;
  private JMenuItem toggleFavorite5Item;
  private JMenuItem clearFavorites1Item;
  private JMenuItem clearFavorites2Item;
  private JMenuItem clearFavorites3Item;
  private JMenuItem clearFavorites4Item;
  private JMenuItem clearFavorites5Item;

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
    fileMenu.add(getExportItem());
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
    editMenu.addSeparator();
    editMenu.add(getClearFavorites1Item());
    editMenu.add(getClearFavorites2Item());
    editMenu.add(getClearFavorites3Item());
    editMenu.add(getClearFavorites4Item());
    editMenu.add(getClearFavorites5Item());
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
      importCarouselItem.setEnabled(okToEnable);
      importGamebaseItem.setEnabled(okToEnable);
      exportItem.setEnabled(okToEnable);
      toggleFavorite1Item.setEnabled(okToEnable);
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
    exportItem = new JMenuItem("Export games...");
    KeyStroke keyStrokeToExportGames = KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK);
    exportItem.setAccelerator(keyStrokeToExportGames);
    exportItem.setMnemonic('E');
    exportItem.addActionListener(e -> exportGames());
    return exportItem;
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

  private JMenuItem getClearFavorites1Item()
  {
    clearFavorites1Item = new JMenuItem("Clear favorites 1");
    KeyStroke keyStrokeToClearFav =
      KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK);
    clearFavorites1Item.setAccelerator(keyStrokeToClearFav);
    clearFavorites1Item.addActionListener(e -> {
      clearFavorites();
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
      clearFavorites2();
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
      clearFavorites3();
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
      clearFavorites4();
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
      clearFavorites5();
    });
    return clearFavorites5Item;
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
    final ExportGamesDialog exportSelectionDialog = new ExportGamesDialog(MainWindow.getInstance());
    exportSelectionDialog.pack();
    exportSelectionDialog.setLocationRelativeTo(this.mainWindow);
    if (exportSelectionDialog.showDialog())
    {
      List<GameListData> gamesList = exportSelectionDialog.getSelectedGames();
      if (!gamesList.isEmpty())
      {
        exportManager.setGamesToExport(gamesList);
        exportManager.setTargetDirectory(exportSelectionDialog.getTargetDirectory(),
                                         exportSelectionDialog.deleteBeforeExport(),
                                         exportSelectionDialog.addGamesSubDirectory());
        ExportProgressDialog dialog = new ExportProgressDialog(this.mainWindow);
        ExportWorker worker = new ExportWorker(exportManager, dialog);
        worker.execute();
        dialog.setVisible(true);
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

  private void clearFavorites()
  {
    String message = "Are you sure you want to clear all games marked as favorites 1?";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Clear all favorites 1", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.clearFavorites(1);
    }
  }

  private void clearFavorites2()
  {
    String message = "Are you sure you want to clear all games marked as favorites 2?";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Clear all favorites 2", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.clearFavorites(2);
    }
  }

  private void clearFavorites3()
  {
    String message = "Are you sure you want to clear all games marked as favorites 3?";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Clear all favorites 3", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.clearFavorites(3);
    }
  }

  private void clearFavorites4()
  {
    String message = "Are you sure you want to clear all games marked as favorites 4?";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Clear all favorites 4", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.clearFavorites(4);
    }
  }

  private void clearFavorites5()
  {
    String message = "Are you sure you want to clear all games marked as favorites 5?";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Clear all favorites 5", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.clearFavorites(5);
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
