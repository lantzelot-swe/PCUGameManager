package se.lantz.gui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import se.lantz.gui.exports.ExportGamesDialog;
import se.lantz.gui.exports.ExportProgressDialog;
import se.lantz.gui.exports.ExportWorker;
import se.lantz.gui.imports.ImportOptionsDialog;
import se.lantz.gui.imports.ImportProgressDialog;
import se.lantz.gui.imports.ImportWorker;
import se.lantz.manager.ExportManager;
import se.lantz.manager.ImportManager;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.util.FileManager;

public class MenuManager
{

  private static final String IMPORT_DIR_PROPERTY = "importDir";
  private static final String EXPORT_DIR_PROPERTY = "exportDir";
  private JMenu fileMenu;
  private JMenu dbMenu;
  private JMenu helpMenu;

  private JMenuItem addGameItem;
  private JMenuItem deleteGameItem;
  private JMenuItem importItem;
  private JMenuItem exportItem;
  private JMenuItem exitItem;

  private JMenuItem backupDbItem;
  private JMenuItem restoreDbItem;
  private JMenuItem createEmptyDbItem;

  private JMenuItem helpItem;
  private JMenuItem aboutItem;

  private MainViewModel uiModel;
  private ImportManager importManager;
  private ExportManager exportManager;
  private MainWindow mainWindow;

  public MenuManager(final MainViewModel uiModel, MainWindow mainWindow)
  {
    this.uiModel = uiModel;
    this.mainWindow = mainWindow;
    this.importManager = new ImportManager(uiModel);
    this.exportManager = new ExportManager(uiModel);
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
    fileMenu.add(getImportItem());
    fileMenu.add(getExportItem());
    fileMenu.addSeparator();
    fileMenu.add(getExitItem());
    dbMenu = new JMenu("Database");
    dbMenu.add(getBackupDbItem());
    dbMenu.add(getRestoreDbItem());
    dbMenu.add(getCreateEmptyDbItem());
    helpMenu = new JMenu("Help");
    helpMenu.add(getHelpItem());
    helpMenu.add(getAboutItem());

    uiModel.addSaveChangeListener(e -> {
      addGameItem.setEnabled(!uiModel.isDataChanged());
    });
    //    KeyStroke keyStrokeToAddGame = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK);
    //    
    //    mainWindow.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStrokeToAddGame, "AddGame");
    //    mainWindow.getRootPane().getActionMap().put("AddGame", new AbstractAction()
    //    {
    //      @Override
    //      public void actionPerformed(ActionEvent arg0)
    //      {
    //        mainWindow.getMainPanel().addNewGame();
    //      }
    //    });

  }

  public List<JMenu> getMenues()
  {
    List<JMenu> menuList = new ArrayList<JMenu>();
    menuList.add(fileMenu);
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
      System.exit(0);
    });
    return exitItem;
  }

  private JMenuItem getBackupDbItem()
  {
    backupDbItem = new JMenuItem("Backup database...");
    backupDbItem.addActionListener(e -> {

    });
    return backupDbItem;
  }

  private JMenuItem getRestoreDbItem()
  {
    restoreDbItem = new JMenuItem("Restore backup...");
    restoreDbItem.addActionListener(e -> {
      //TODO
    });
    return restoreDbItem;
  }

  private JMenuItem getCreateEmptyDbItem()
  {
    createEmptyDbItem = new JMenuItem("Create empty database...");
    createEmptyDbItem.addActionListener(e -> {
      //TODO
    });
    return createEmptyDbItem;
  }

  private JMenuItem getHelpItem()
  {
    helpItem = new JMenuItem("Help");
    KeyStroke keyStrokeToImportGames = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
    helpItem.setAccelerator(keyStrokeToImportGames);
    helpItem.addActionListener(e -> {
      //TODO
    });
    return helpItem;
  }

  private JMenuItem getAboutItem()
  {
    aboutItem = new JMenuItem("About...");
    aboutItem.addActionListener(e -> {
      //TODO
    });
    return aboutItem;
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
          ImportProgressDialog dialog = new ImportProgressDialog(this.mainWindow);
          ImportWorker worker = new ImportWorker(importManager, dialog);
          worker.execute();
          dialog.setVisible(true);
          //Refresh current game view after import
          uiModel.reloadCurrentGameView();
          MainWindow.getInstance().repaintAfterImport();
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
}
