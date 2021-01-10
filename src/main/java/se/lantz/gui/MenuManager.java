package se.lantz.gui;

import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import se.lantz.gui.dbbackup.BackupProgressDialog;
import se.lantz.gui.dbbackup.BackupWorker;
import se.lantz.gui.dbrestore.RestoreDbDialog;
import se.lantz.gui.dbrestore.RestoreProgressDialog;
import se.lantz.gui.dbrestore.RestoreWorker;
import se.lantz.gui.exports.ExportGamesDialog;
import se.lantz.gui.exports.ExportProgressDialog;
import se.lantz.gui.exports.ExportWorker;
import se.lantz.gui.imports.ImportOptionsDialog;
import se.lantz.gui.imports.ImportProgressDialog;
import se.lantz.gui.imports.ImportWorker;
import se.lantz.manager.BackupManager;
import se.lantz.manager.ExportManager;
import se.lantz.manager.ImportManager;
import se.lantz.manager.RestoreManager;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;
import se.lantz.util.VersionChecker;

public class MenuManager
{

  private static final String IMPORT_DIR_PROPERTY = "importDir";
  private static final String EXPORT_DIR_PROPERTY = "exportDir";
  private JMenu fileMenu;
  private JMenu editMenu;
  private JMenu dbMenu;
  private JMenu helpMenu;

  private JMenuItem addGameItem;
  private JMenuItem deleteGameItem;
  private JMenuItem runGameItem;
  private JMenuItem importItem;
  private JMenuItem exportItem;
  
  private JMenuItem toggleFavoriteItem;
  private JMenuItem clearFavoritesItem;

  private JMenuItem backupDbItem;
  private JMenuItem restoreDbItem;
  private JMenuItem createEmptyDbItem;

  private JMenuItem helpItem;
  private JMenuItem aboutItem;
  private JMenuItem newVersionItem;

  private JMenuItem exitItem;
  private MainViewModel uiModel;
  private ImportManager importManager;
  private ExportManager exportManager;
  private BackupManager backupManager;
  private RestoreManager restoreManager;
  private MainWindow mainWindow;

  public MenuManager(final MainViewModel uiModel, MainWindow mainWindow)
  {
    this.uiModel = uiModel;
    this.mainWindow = mainWindow;
    this.importManager = new ImportManager(uiModel);
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
    fileMenu.add(getAddGameMenuItem());
    fileMenu.add(getDeleteGameMenuItem());
    fileMenu.addSeparator();
    fileMenu.add(getRunGameMenuItem());
    fileMenu.addSeparator();
    fileMenu.add(getImportItem());
    fileMenu.add(getExportItem());
    fileMenu.addSeparator();
    fileMenu.add(getExitItem());
    editMenu = new JMenu("Edit");
    editMenu.add(getToggleFavoriteItem());
    editMenu.add(getClearFavoritesItem());
    dbMenu = new JMenu("Database");
    dbMenu.add(getBackupDbItem());
    dbMenu.add(getRestoreDbItem());
    dbMenu.add(getCreateEmptyDbItem());
    helpMenu = new JMenu("Help");
    helpMenu.add(getHelpItem());
    helpMenu.add(getCheckVersionItem());
    helpMenu.add(getAboutItem());
  }

  public void intialize()
  {
    uiModel.addSaveChangeListener(e -> {
      boolean okToEnable = !uiModel.isDataChanged();
      addGameItem.setEnabled(okToEnable);
      importItem.setEnabled(okToEnable);
      exportItem.setEnabled(okToEnable);
      toggleFavoriteItem.setEnabled(okToEnable);
      runGameItem.setEnabled(!uiModel.getInfoModel().getGamesFile().isEmpty());
    });
  }

  public List<JMenu> getMenues()
  {
    List<JMenu> menuList = new ArrayList<JMenu>();
    menuList.add(fileMenu);
    menuList.add(editMenu);
    menuList.add(dbMenu);
    menuList.add(helpMenu);
    return menuList;
  }

  JMenuItem getAddGameMenuItem()
  {
    addGameItem = new JMenuItem("Add New Game");
    KeyStroke keyStrokeToAddGame = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
    addGameItem.setAccelerator(keyStrokeToAddGame);
    addGameItem.setMnemonic('N');

    addGameItem.addActionListener(e -> mainWindow.getMainPanel().addNewGame());
    return addGameItem;
  }

  JMenuItem getDeleteGameMenuItem()
  {
    deleteGameItem = new JMenuItem("Delete Current Game");
    KeyStroke keyStrokeToAddGame = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
    deleteGameItem.setAccelerator(keyStrokeToAddGame);
    deleteGameItem.setMnemonic('D');

    deleteGameItem.addActionListener(e -> mainWindow.getMainPanel().deleteCurrentGame());
    return deleteGameItem;
  }
  
  JMenuItem getRunGameMenuItem()
  {
    runGameItem = new JMenuItem("Run Current Game");
    KeyStroke keyStrokeToRunGame = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
    runGameItem.setAccelerator(keyStrokeToRunGame);
    runGameItem.setMnemonic('R');

    runGameItem.addActionListener(e -> mainWindow.getMainPanel().runCurrentGame());
    return runGameItem;
  }

  private JMenuItem getImportItem()
  {
    importItem = new JMenuItem("Import Games...");
    KeyStroke keyStrokeToImportGames = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);
    importItem.setAccelerator(keyStrokeToImportGames);
    importItem.setMnemonic('I');
    importItem.addActionListener(e -> importGameList());
    return importItem;
  }

  private JMenuItem getExportItem()
  {
    exportItem = new JMenuItem("Export Games...");
    KeyStroke keyStrokeToExportGames = KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK);
    exportItem.setAccelerator(keyStrokeToExportGames);
    exportItem.setMnemonic('E');
    exportItem.addActionListener(e -> exportGames());
    return exportItem;
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
      FileManager.deleteTempFolder();
      System.exit(0);
    });
    return exitItem;
  }
  
  private JMenuItem getToggleFavoriteItem()
  {
    toggleFavoriteItem = new JMenuItem("Add/remove from favorites");
    KeyStroke keyStrokeToToggleFav = KeyStroke.getKeyStroke(KeyEvent.VK_F12, InputEvent.CTRL_DOWN_MASK);
    toggleFavoriteItem.setAccelerator(keyStrokeToToggleFav);
    toggleFavoriteItem.setMnemonic('F');
    toggleFavoriteItem.addActionListener(e -> {
      mainWindow.getMainPanel().toggleFavorite();
    });
    return toggleFavoriteItem;
  }
  
  private JMenuItem getClearFavoritesItem()
  {
    clearFavoritesItem = new JMenuItem("Clear all favorites");
    clearFavoritesItem.setMnemonic('C');
    clearFavoritesItem.addActionListener(e -> {
      clearFavorites();
    });
    return clearFavoritesItem;
  }

  private JMenuItem getBackupDbItem()
  {
    backupDbItem = new JMenuItem("Backup database");
    backupDbItem.addActionListener(e -> backupDb());
    return backupDbItem;
  }

  private JMenuItem getRestoreDbItem()
  {
    restoreDbItem = new JMenuItem("Restore backup...");
    restoreDbItem.addActionListener(e -> restoreDb());
    return restoreDbItem;
  }

  private JMenuItem getCreateEmptyDbItem()
  {
    createEmptyDbItem = new JMenuItem("Delete all games");
    createEmptyDbItem.addActionListener(e -> deleteAllGames());
    return createEmptyDbItem;
  }

  private JMenuItem getHelpItem()
  {
    helpItem = new JMenuItem("Help");
    KeyStroke keyStrokeToImportGames = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
    helpItem.setAccelerator(keyStrokeToImportGames);
    helpItem.addActionListener(e -> {
      try
      {
        Desktop.getDesktop().browse(new URI("https://github.com/lantzelot-swe/PCUGameManager/wiki"));
      }
      catch (IOException | URISyntaxException ex)
      {
        JOptionPane.showMessageDialog(MainWindow.getInstance(), "Could not open help", "Help missing", JOptionPane.ERROR_MESSAGE);
      }
    });
    return helpItem;
  }

  private JMenuItem getAboutItem()
  {
    aboutItem = new JMenuItem("About...");
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
    newVersionItem.addActionListener(e -> {
      checkForNewRelease();
    });
    return newVersionItem;
  }

  private void importGameList()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select directory containing a Carousel");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    String importDir = FileManager.getConfiguredProperties().getProperty(IMPORT_DIR_PROPERTY);
    if (importDir == null)
    {
      importDir = ".";
    }
    fileChooser.setCurrentDirectory(new File(importDir));
    int value = fileChooser.showDialog(this.mainWindow, "Import");
    if (value == JFileChooser.APPROVE_OPTION)
    {
      Path selectedPath = fileChooser.getSelectedFile().toPath();
      FileManager.getConfiguredProperties().put(IMPORT_DIR_PROPERTY, selectedPath.toString());
      if (importManager.checkSelectedFolder(selectedPath))
      {
        //Show options dialog
        ImportOptionsDialog optionsDialog = new ImportOptionsDialog(this.mainWindow);
        optionsDialog.pack();
        optionsDialog.setLocationRelativeTo(this.mainWindow);
        if (optionsDialog.showDialog())
        {
          importManager.setSelectedOption(optionsDialog.getSelectedOption());
          importManager.setAddAsFavorite(optionsDialog.getMarkAsFavorite());
          ImportProgressDialog dialog = new ImportProgressDialog(this.mainWindow);
          ImportWorker worker = new ImportWorker(importManager, dialog);
          worker.execute();
          dialog.setVisible(true);
          //Refresh current game view after import
          uiModel.reloadCurrentGameView();
          MainWindow.getInstance().repaintAfterModifications();
        }
      }
      else
      {
        JOptionPane.showMessageDialog(this.mainWindow,
                                      "The selected directory doesn't contain a valid carousel structure.",
                                      "Import games",
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
        exportManager.setExportFormat(exportSelectionDialog.isFavFormat());
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a directory to export to");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String exportDir = FileManager.getConfiguredProperties().getProperty(EXPORT_DIR_PROPERTY);
        if (exportDir == null)
        {
          exportDir = ".";
        }
        fileChooser.setCurrentDirectory(new File(exportDir));
        fileChooser.setApproveButtonText("Export");
        int value = fileChooser.showDialog(this.mainWindow, "Export");
        if (value == JFileChooser.APPROVE_OPTION)
        {
          File selectedDir = fileChooser.getSelectedFile();
          FileManager.getConfiguredProperties().put(EXPORT_DIR_PROPERTY, selectedDir.toPath().toString());
          exportManager.setTargerDirectory(selectedDir);
          ExportProgressDialog dialog = new ExportProgressDialog(this.mainWindow);
          ExportWorker worker = new ExportWorker(exportManager, dialog);
          worker.execute();
          dialog.setVisible(true);
        }
      }
    }
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
    String message =
      "Do you want to delete all games from the database? A backup will added to the backups folder before deleting.\nCover, screenshot and game files will also be deleted.";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Delete all games", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      backupDb();
      MainWindow.getInstance().getMainPanel().clearGameListSelection();
      uiModel.deleteAllGames();
      FileManager.deleteAllFolderContent();
      //Trigger a reload of game views
      uiModel.reloadGameViews();
      MainWindow.getInstance().selectViewAfterRestore();
    }
  }
  
  private void clearFavorites()
  {
    String message =
      "Are you sure you want to clear all games marked as favorites?";
    int option = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Clear all favorites", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (option == JOptionPane.YES_OPTION)
    {
      uiModel.clearFavorites();
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
