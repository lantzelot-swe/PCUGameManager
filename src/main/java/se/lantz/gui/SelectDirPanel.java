package se.lantz.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.util.FileManager;

public class SelectDirPanel extends JPanel
{
  private static final Logger logger = LoggerFactory.getLogger(SelectDirPanel.class);
  private static final String IMPORT_DIR_PROPERTY = "importDir";
  private static final String EXPORT_DIR_PROPERTY = "exportDir";
  private JTextField dirTextField;
  private JButton selectDirButton;

  private File targetDirectory;
  private boolean importMode = true;

  private String configuredDir = "";

  public SelectDirPanel(boolean importMode)
  {
    this.importMode = importMode;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_dirTextField = new GridBagConstraints();
    gbc_dirTextField.anchor = GridBagConstraints.NORTHWEST;
    gbc_dirTextField.weighty = 1.0;
    gbc_dirTextField.weightx = 1.0;
    gbc_dirTextField.insets = new Insets(0, 5, 0, 5);
    gbc_dirTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_dirTextField.gridx = 0;
    gbc_dirTextField.gridy = 0;
    add(getDirTextField(), gbc_dirTextField);
    GridBagConstraints gbc_selectDirButton = new GridBagConstraints();
    gbc_selectDirButton.weighty = 1.0;
    gbc_selectDirButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_selectDirButton.insets = new Insets(0, 0, 0, 5);
    gbc_selectDirButton.gridx = 1;
    gbc_selectDirButton.gridy = 0;
    add(getSelectDirButton(), gbc_selectDirButton);
    if (importMode)
    {
      configuredDir = FileManager.getConfiguredProperties().getProperty(IMPORT_DIR_PROPERTY);
      if (configuredDir == null)
      {
        configuredDir = new File(".").getAbsolutePath();
      }
    }
    else
    {
      configuredDir = FileManager.getConfiguredProperties().getProperty(EXPORT_DIR_PROPERTY);
      if (configuredDir == null)
      {
        configuredDir = new File("export").getAbsolutePath();
      }
    }
    targetDirectory = new File(configuredDir);
    getDirTextField().setText(configuredDir);
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
            if (importMode)
            {
              selectImportDirectory();
            }
            else
            {
              selectExportDirectory();
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

  private void selectExportDirectory()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select a directory to export to");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setCurrentDirectory(new File(configuredDir));
    int value = fileChooser.showDialog(this, "OK");
    if (value == JFileChooser.APPROVE_OPTION)
    {
      targetDirectory = fileChooser.getSelectedFile();
      configuredDir = targetDirectory.toPath().toString();
      FileManager.getConfiguredProperties().put(EXPORT_DIR_PROPERTY, configuredDir);
      getDirTextField().setText(configuredDir);
    }
  }

  public File getTargetDirectory()
  {
    return targetDirectory;
  }

  private boolean checkSelectedFolder(Path folder)
  {

    logger.debug("Selected folder: {}", folder);

    //Assume a games subfolder is available
    Path srcParentFolder = folder.resolve("games");
    Path srcCoversFolder = srcParentFolder.resolve("covers");
    Path srcGamesFolder = srcParentFolder.resolve("games");
    Path srcScreensFolder = srcParentFolder.resolve("screens");

    logger.debug("parent folder: {}", srcParentFolder);
    logger.debug("covers folder: {}", srcCoversFolder);
    logger.debug("games folder: {}", srcGamesFolder);
    logger.debug("screens folder: {}", srcScreensFolder);

    // Verify that subfolders are available
    if (Files.exists(srcParentFolder, LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(srcCoversFolder, LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(srcGamesFolder, LinkOption.NOFOLLOW_LINKS) &&
      Files.exists(srcScreensFolder, LinkOption.NOFOLLOW_LINKS))
    {
      logger.debug("A valid directory!");

      return true;
    }
    else
    {
      //Check if there is no games subfolder, but valid structure
      srcCoversFolder = folder.resolve("covers");
      srcGamesFolder = folder.resolve("games");
      srcScreensFolder = folder.resolve("screens");
      if (Files.exists(srcCoversFolder, LinkOption.NOFOLLOW_LINKS) &&
        Files.exists(srcGamesFolder, LinkOption.NOFOLLOW_LINKS) &&
        Files.exists(srcScreensFolder, LinkOption.NOFOLLOW_LINKS))
      {
        logger.debug("A valid directory!");

        return true;
      }
      else
      {
        logger.debug("An ivalid directory!");
        return false;
      }
    }
  }
}
