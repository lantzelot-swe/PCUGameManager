package se.lantz.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class SelectDirPanel extends JPanel
{
  private static final String THEC64SAVE = ".THEC64SAVE";

  public enum Mode
  {
    CAROUSEL_IMPORT, GB_IMPORT, CAROUSEL_EXPORT, FILELOADER_EXPORT, SAVEDSTATES_IMPORT, SAVEDSTATES_EXPORT,
    FIX_CORRUPT_SAVEDSTATES, DATABASE_IMPORT
  }

  private static final Logger logger = LoggerFactory.getLogger(SelectDirPanel.class);
  private static final String IMPORT_DIR_PROPERTY = "importDir";
  private static final String GB_IMPORT_DIR_PROPERTY = "gbImportDir";
  private static final String CAROUSEL_EXPORT_DIR_PROPERTY = "exportDir";
  private static final String FILELOADER_EXPORT_DIR_PROPERTY = "flexportDir";
  private static final String SAVEDSTATES_IMPORT_DIR_PROPERTY = "savedStatesImportDir";
  private static final String SAVEDSTATES_EXPORT_DIR_PROPERTY = "savedStatesExportDir";
  private static final String FIX_SAVEDSTATES_DIR_PROPERTY = "fixSavedStatesDir";
  private static final String DATABASE_IMPORT_DIR_PROPERTY = "dbImportDir";
  private JTextField dirTextField;
  private JButton selectDirButton;

  private File targetDirectory;
  private Mode mode = Mode.CAROUSEL_IMPORT;

  private String configuredDir = "";
  private ActionListener gbDbFileSelectedlistener;
  private JLabel usbInfoLabel;

  public SelectDirPanel(Mode mode)
  {
    this.mode = mode;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_dirTextField = new GridBagConstraints();
    gbc_dirTextField.anchor = GridBagConstraints.NORTHWEST;
    gbc_dirTextField.weightx = 1.0;
    gbc_dirTextField.insets = new Insets(0, 5, 5, 5);
    gbc_dirTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_dirTextField.gridx = 0;
    gbc_dirTextField.gridy = 0;
    add(getDirTextField(), gbc_dirTextField);
    GridBagConstraints gbc_selectDirButton = new GridBagConstraints();
    gbc_selectDirButton.gridheight = 2;
    gbc_selectDirButton.insets = new Insets(0, 0, 5, 5);
    gbc_selectDirButton.weighty = 1.0;
    gbc_selectDirButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_selectDirButton.gridx = 1;
    gbc_selectDirButton.gridy = 0;
    add(getSelectDirButton(), gbc_selectDirButton);
    switch (mode)
    {
    case CAROUSEL_IMPORT:
      configuredDir = getUsbFilePath(false, false);
      if (configuredDir.isEmpty())
      {
        configuredDir = FileManager.getConfiguredProperties().getProperty(IMPORT_DIR_PROPERTY);
      }
      if (configuredDir == null)
      {
        configuredDir = new File(".").getAbsolutePath();
      }
      break;

    case GB_IMPORT:
      configuredDir = FileManager.getConfiguredProperties().getProperty(GB_IMPORT_DIR_PROPERTY);
      if (configuredDir == null)
      {
        configuredDir = new File(".").getAbsolutePath();
      }
      break;
    case CAROUSEL_EXPORT:
      configuredDir = getUsbFilePath(false, false);
      if (configuredDir.isEmpty())
      {
        configuredDir = FileManager.getConfiguredProperties().getProperty(CAROUSEL_EXPORT_DIR_PROPERTY);
      }
      if (configuredDir == null)
      {
        configuredDir = new File("export").getAbsolutePath();
      }
      break;
    case FILELOADER_EXPORT:
      configuredDir = getUsbFilePath(false, true);
      if (configuredDir.isEmpty())
      {
        configuredDir = FileManager.getConfiguredProperties().getProperty(FILELOADER_EXPORT_DIR_PROPERTY);
      }
      if (configuredDir == null)
      {
        configuredDir = new File("export").getAbsolutePath();
      }
      break;
    case SAVEDSTATES_IMPORT:
      configuredDir = getUsbFilePath(true, false);
      if (configuredDir.isEmpty())
      {
        configuredDir = FileManager.getConfiguredProperties().getProperty(SAVEDSTATES_IMPORT_DIR_PROPERTY);
      }
      if (configuredDir == null)
      {
        configuredDir = new File(".").getAbsolutePath();
      }
      break;
    case SAVEDSTATES_EXPORT:
      configuredDir = getUsbFilePath(true, false);
      if (configuredDir.isEmpty())
      {
        configuredDir = FileManager.getConfiguredProperties().getProperty(SAVEDSTATES_EXPORT_DIR_PROPERTY);
      }
      if (configuredDir == null)
      {
        configuredDir = new File(".").getAbsolutePath();
      }
      break;
    case FIX_CORRUPT_SAVEDSTATES:
      configuredDir = getUsbFilePath(true, false);
      if (configuredDir.isEmpty())
      {
        configuredDir = FileManager.getConfiguredProperties().getProperty(FIX_SAVEDSTATES_DIR_PROPERTY);
      }
      if (configuredDir == null)
      {
        configuredDir = new File(".").getAbsolutePath();
      }
      break;
    case DATABASE_IMPORT:
      configuredDir = getUsbFilePath(true, false);
      if (configuredDir.isEmpty())
      {
        configuredDir = FileManager.getConfiguredProperties().getProperty(DATABASE_IMPORT_DIR_PROPERTY);
      }
      if (configuredDir == null)
      {
        configuredDir = new File(".").getAbsolutePath();
      }
      break;
    default:
      break;
    }
    targetDirectory = new File(configuredDir);
    getDirTextField().setText(configuredDir);
    GridBagConstraints gbc_usbInfoLabel = new GridBagConstraints();
    gbc_usbInfoLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_usbInfoLabel.weighty = 1.0;
    gbc_usbInfoLabel.insets = new Insets(0, 5, 0, 5);
    gbc_usbInfoLabel.gridx = 0;
    gbc_usbInfoLabel.gridy = 1;
    add(getUsbInfoLabel(), gbc_usbInfoLabel);
  }

  private String getUsbFilePath(boolean saveState, boolean fileLoader)
  {
    String usbDir = FileManager.getPCUAEUSBPath(saveState, fileLoader);
    if (!usbDir.isEmpty())
    {
      getUsbInfoLabel().setText("PCUAE USB detected (" + usbDir.substring(0, usbDir.indexOf("\\")) + ")");
    }
    else
    {
      getUsbInfoLabel().setText("");
    }
    return usbDir;
  }

  private JTextField getDirTextField()
  {
    if (dirTextField == null)
    {
      dirTextField = new JTextField();
      dirTextField.setEditable(false);
      dirTextField.setPreferredSize(new Dimension(500, dirTextField.getPreferredSize().height));
    }
    return dirTextField;
  }

  private JButton getSelectDirButton()
  {
    if (selectDirButton == null)
    {
      selectDirButton = new JButton("...");
      selectDirButton.addActionListener(new ActionListener()
        {

          public void actionPerformed(ActionEvent e)
          {
            switch (mode)
            {
            case CAROUSEL_IMPORT:
              selectImportDirectory();
              break;
            case GB_IMPORT:
              selectGbImportFile();
              break;
            case CAROUSEL_EXPORT:
              selectExportDirectory(true);
              break;
            case FILELOADER_EXPORT:
              selectExportDirectory(false);
              break;
            case SAVEDSTATES_IMPORT:
              selectSavedStatesImportDirectory();
              break;
            case SAVEDSTATES_EXPORT:
              selectSavedStatesExportDirectory();
              break;
            case FIX_CORRUPT_SAVEDSTATES:
              selectFixSavedStatesDirectory();
              break;
            case DATABASE_IMPORT:
              selectDatabaseImportDirectory();
              break;
            default:
              break;
            }
          }
        });
      selectDirButton.setMargin(new Insets(1, 3, 1, 3));
    }
    return selectDirButton;
  }

  private void selectImportDirectory()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select directory containing a Carousel");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setCurrentDirectory(new File(configuredDir));
    int value = fileChooser.showDialog(this, "OK");
    if (value == JFileChooser.APPROVE_OPTION)
    {
      if (checkSelectedFolder(fileChooser.getSelectedFile().toPath()))
      {
        targetDirectory = fileChooser.getSelectedFile();
        configuredDir = targetDirectory.toPath().toString();
        FileManager.getConfiguredProperties().put(IMPORT_DIR_PROPERTY, configuredDir);
        getDirTextField().setText(configuredDir);
      }
      else
      {
        JOptionPane
          .showMessageDialog(this,
                             "The selected directory doesn't contain a valid carousel structure. It must either contain game info files(.tsg) and subfolders for screens, games and covers\nor the same structure in a subfolder named \"games\".",
                             "Import games",
                             JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void selectGbImportFile()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select a Gamebase database file (.mdb)");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    FileFilter filter = new FileNameExtensionFilter(".mdb file", "mdb");
    fileChooser.setFileFilter(filter);
    fileChooser.setCurrentDirectory(new File(configuredDir));
    int value = fileChooser.showDialog(this, "OK");
    if (value == JFileChooser.APPROVE_OPTION)
    {
      if (checkSelectedGbDirectory(fileChooser.getSelectedFile().toPath()))
      {
        targetDirectory = fileChooser.getSelectedFile();
        configuredDir = targetDirectory.toPath().toString();
        FileManager.getConfiguredProperties().put(GB_IMPORT_DIR_PROPERTY, configuredDir);
        getDirTextField().setText(configuredDir);
        gbDbFileSelectedlistener.actionPerformed(new ActionEvent(this, 0, ""));
      }
      else
      {
        JOptionPane
          .showMessageDialog(this,
                             "The selected directory doesn't contain a valid Gamebase database, the file \"Paths.ini\" is missing.",
                             "Import Gamebase games",
                             JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void selectExportDirectory(boolean carousel)
  {
    final JFileChooser fileChooser = new JFileChooser()
      {
        @Override
        protected JDialog createDialog(Component parent) throws HeadlessException
        {
          //Set parent to the export dialog
          JDialog dlg = super.createDialog(SwingUtilities.getAncestorOfClass(JDialog.class, SelectDirPanel.this));
          return dlg;
        }
      };
    fileChooser.setDialogTitle("Select a directory to export to");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setCurrentDirectory(new File(configuredDir));
    int value = fileChooser.showDialog(this, "OK");
    if (value == JFileChooser.APPROVE_OPTION)
    {
      targetDirectory = fileChooser.getSelectedFile();
      if (carousel && targetDirectory.getName().contains(" "))
      {
        String message = String
          .format("<html>The carousel does not support folders that contains a space in the name.<br>Are you sure you want to export to the \"%s\" directory?</html>",
                  targetDirectory.getName());
        int choice = JOptionPane.showConfirmDialog(SwingUtilities.getAncestorOfClass(JDialog.class, this),
                                                   message,
                                                   "Folder name",
                                                   JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.NO_OPTION)
        {
          selectExportDirectory(carousel);
        }
      }
      configuredDir = targetDirectory.toPath().toString();
      FileManager.getConfiguredProperties()
        .put(carousel ? CAROUSEL_EXPORT_DIR_PROPERTY : FILELOADER_EXPORT_DIR_PROPERTY, configuredDir);
      getDirTextField().setText(configuredDir);
    }
  }

  private void selectSavedStatesImportDirectory()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select the \"" + THEC64SAVE + "\" directory to import from");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setCurrentDirectory(new File(configuredDir));
    int value = fileChooser.showDialog(this, "OK");
    if (value == JFileChooser.APPROVE_OPTION)
    {
      targetDirectory = fileChooser.getSelectedFile();
      if (!targetDirectory.getName().toUpperCase().equals(THEC64SAVE))
      {
        String message = "<html>You have not selected a \"" + THEC64SAVE +
          "\" directory.<br> Are you sure you want to import from the selected directory?</html>";
        int choice = JOptionPane.showConfirmDialog(SwingUtilities.getAncestorOfClass(JDialog.class, this),
                                                   message,
                                                   "Folder name",
                                                   JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.NO_OPTION)
        {
          selectSavedStatesImportDirectory();
        }
      }
      configuredDir = targetDirectory.toPath().toString();
      FileManager.getConfiguredProperties().put(SAVEDSTATES_IMPORT_DIR_PROPERTY, configuredDir);
      getDirTextField().setText(configuredDir);
    }
  }

  private void selectSavedStatesExportDirectory()
  {
    final JFileChooser fileChooser = new JFileChooser()
      {
        @Override
        protected JDialog createDialog(Component parent) throws HeadlessException
        {
          //Set parent to the export dialog
          JDialog dlg = super.createDialog(SwingUtilities.getAncestorOfClass(JDialog.class, SelectDirPanel.this));
          return dlg;
        }
      };
    fileChooser.setDialogTitle("Select the \"" + THEC64SAVE + "\" directory on your PCUAE USB stick");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setCurrentDirectory(new File(configuredDir));
    int value = fileChooser.showDialog(this, "OK");
    if (value == JFileChooser.APPROVE_OPTION)
    {
      targetDirectory = fileChooser.getSelectedFile();
      if (!targetDirectory.getName().toUpperCase().equals(THEC64SAVE))
      {
        String message = "<html>You have not selected a \"" + THEC64SAVE +
          "\" directory.<br> Are you sure you want to export to the selected directory?</html>";
        int choice = JOptionPane.showConfirmDialog(SwingUtilities.getAncestorOfClass(JDialog.class, this),
                                                   message,
                                                   "Folder name",
                                                   JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.NO_OPTION)
        {
          selectSavedStatesExportDirectory();
        }
      }
      configuredDir = targetDirectory.toPath().toString();
      FileManager.getConfiguredProperties().put(SAVEDSTATES_EXPORT_DIR_PROPERTY, configuredDir);
      getDirTextField().setText(configuredDir);
    }
  }

  private void selectFixSavedStatesDirectory()
  {
    final JFileChooser fileChooser = new JFileChooser()
      {
        @Override
        protected JDialog createDialog(Component parent) throws HeadlessException
        {
          //Set parent to the export dialog
          JDialog dlg = super.createDialog(SwingUtilities.getAncestorOfClass(JDialog.class, SelectDirPanel.this));
          return dlg;
        }
      };
    fileChooser.setDialogTitle("Select a directory containing saved states");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setCurrentDirectory(new File(configuredDir));
    int value = fileChooser.showDialog(this, "OK");
    if (value == JFileChooser.APPROVE_OPTION)
    {
      targetDirectory = fileChooser.getSelectedFile();
      configuredDir = targetDirectory.toPath().toString();
      FileManager.getConfiguredProperties().put(FIX_SAVEDSTATES_DIR_PROPERTY, configuredDir);
      getDirTextField().setText(configuredDir);
    }
  }
  
  private void selectDatabaseImportDirectory()
  {
    final JFileChooser fileChooser = new JFileChooser()
    {
      @Override
      protected JDialog createDialog(Component parent) throws HeadlessException
      {
        //Set parent to the export dialog
        JDialog dlg = super.createDialog(SwingUtilities.getAncestorOfClass(JDialog.class, SelectDirPanel.this));
        return dlg;
      }
    };
  fileChooser.setDialogTitle("Select the root directory for a PCUAE manager instance");
  fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  fileChooser.setCurrentDirectory(new File(configuredDir));
  int value = fileChooser.showDialog(this, "OK");
  if (value == JFileChooser.APPROVE_OPTION)
  {
    targetDirectory = fileChooser.getSelectedFile();
    configuredDir = targetDirectory.toPath().toString();
    FileManager.getConfiguredProperties().put(DATABASE_IMPORT_DIR_PROPERTY, configuredDir);
    getDirTextField().setText(configuredDir);
  }
  }

  public File getTargetDirectory()
  {
    return targetDirectory;
  }

  private boolean checkSelectedGbDirectory(Path gbFile)
  {
    Path iniPath = gbFile.getParent().resolve("Paths.ini");
    return Files.exists(iniPath);
  }

  private boolean checkSelectedFolder(Path folder)
  {
    logger.debug("Selected folder: {}", folder);
    if (isCarouselFolder(folder))
    {
      return true;
    }
    else
    {
      List<Path> foundCarousels = Collections.EMPTY_LIST;
      try (Stream<Path> filePathStream = Files.walk(folder, 1))
      {
        foundCarousels =
          filePathStream.filter(Files::isDirectory).filter(dir -> isCarouselFolder(dir)).collect(Collectors.toList());
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not read gameInfo files");
      }
      return foundCarousels.size() > 0;
    }
  }

  private boolean isCarouselFolder(Path folder)
  {
    return Files.exists(folder.resolve("covers"), LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(folder.resolve("screens"), LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(folder.resolve("games"), LinkOption.NOFOLLOW_LINKS);
  }

  public void registerGBFileSelectedActionListener(ActionListener listener)
  {
    this.gbDbFileSelectedlistener = listener;
  }

  private JLabel getUsbInfoLabel()
  {
    if (usbInfoLabel == null)
    {
      usbInfoLabel = new JLabel("");
      usbInfoLabel.setFont(usbInfoLabel.getFont().deriveFont(Font.BOLD));
    }
    return usbInfoLabel;
  }
}
