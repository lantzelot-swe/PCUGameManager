package se.lantz.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import se.lantz.util.FileManager;

public class SelectDirPanel extends JPanel
{
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
    }
    else
    {
      configuredDir = FileManager.getConfiguredProperties().getProperty(EXPORT_DIR_PROPERTY);
    }
    if (configuredDir == null)
    {
      configuredDir = ".";
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
      dirTextField.setColumns(10);
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
    int value = fileChooser.showDialog(this, "Import");
    //    if (value == JFileChooser.APPROVE_OPTION)
    //    {
    //      targetDirectory = fileChooser.getSelectedFile();
    //      configuredDir = targetDirectory.toPath().toString();
    //      FileManager.getConfiguredProperties().put(IMPORT_DIR_PROPERTY, configuredDir);
    //      if (importManager.checkSelectedFolder(targetDirectory.toPath()))
    //      {
    //        //Show options dialog
    //        ImportOptionsDialog optionsDialog = new ImportOptionsDialog(this.mainWindow);
    //        optionsDialog.pack();
    //        optionsDialog.setLocationRelativeTo(this.mainWindow);
    //        if (optionsDialog.showDialog())
    //        {
    //          importManager.setSelectedOption(optionsDialog.getSelectedOption());
    //          importManager.setAddAsFavorite(optionsDialog.getMarkAsFavorite());
    //          ImportProgressDialog dialog = new ImportProgressDialog(this.mainWindow);
    //          ImportWorker worker = new ImportWorker(importManager, dialog);
    //          worker.execute();
    //          dialog.setVisible(true);
    //          //Refresh current game view after import
    //          uiModel.reloadCurrentGameView();
    //          MainWindow.getInstance().repaintAfterModifications();
    //        }
    //      }
    //      else
    //      {
    //        JOptionPane.showMessageDialog(this.mainWindow,
    //                                      "The selected directory doesn't contain a valid carousel structure.",
    //                                      "Import games",
    //                                      JOptionPane.ERROR_MESSAGE);
    //      }
  }

  private void selectExportDirectory()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select a directory to export to");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setCurrentDirectory(new File(configuredDir));
    fileChooser.setApproveButtonText("Export");
    int value = fileChooser.showDialog(this, "Export");
    if (value == JFileChooser.APPROVE_OPTION)
    {
      if (value == JFileChooser.APPROVE_OPTION)
      {
        targetDirectory = fileChooser.getSelectedFile();
        configuredDir = targetDirectory.toPath().toString();
        FileManager.getConfiguredProperties().put(EXPORT_DIR_PROPERTY, configuredDir);
        getDirTextField().setText(configuredDir);
      }
    }
  }

  public File getTargetDirectory()
  {
    return targetDirectory;
  }
}
