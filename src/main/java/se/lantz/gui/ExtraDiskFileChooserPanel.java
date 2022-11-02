package se.lantz.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import se.lantz.model.InfoModel;
import se.lantz.util.FileDrop;
import se.lantz.util.FileManager;

public class ExtraDiskFileChooserPanel extends JPanel
{
  private static final String EXTRA_DISKS_DIR_PROPERTY = "extraDisksDir";
  private JLabel diskLabel;
  private JTextField diskTextField;
  private JButton diskButton;

  private InfoModel infomodel;
  private int index;
  private boolean gamesFileUpdated = false;
  private JButton clearButton;

  public ExtraDiskFileChooserPanel(InfoModel infoModel, int index)
  {
    this.infomodel = infoModel;
    this.index = index;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_diskLabel = new GridBagConstraints();
    gbc_diskLabel.weightx = 1.0;
    gbc_diskLabel.anchor = GridBagConstraints.WEST;
    gbc_diskLabel.insets = new Insets(10, 5, 0, 5);
    gbc_diskLabel.gridx = 0;
    gbc_diskLabel.gridy = 0;
    add(getDiskLabel(), gbc_diskLabel);
    GridBagConstraints gbc_diskTextField = new GridBagConstraints();
    gbc_diskTextField.anchor = GridBagConstraints.NORTHWEST;
    gbc_diskTextField.weighty = 1.0;
    gbc_diskTextField.weightx = 1.0;
    gbc_diskTextField.insets = new Insets(0, 5, 0, 5);
    gbc_diskTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_diskTextField.gridx = 0;
    gbc_diskTextField.gridy = 1;
    add(getDiskTextField(), gbc_diskTextField);
    GridBagConstraints gbc_diskButton = new GridBagConstraints();
    gbc_diskButton.weighty = 1.0;
    gbc_diskButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_diskButton.insets = new Insets(0, 0, 0, 5);
    gbc_diskButton.gridx = 1;
    gbc_diskButton.gridy = 1;
    add(getDiskButton(), gbc_diskButton);
    GridBagConstraints gbc_clearButton = new GridBagConstraints();
    gbc_clearButton.weighty = 1.0;
    gbc_clearButton.insets = new Insets(0, 0, 0, 5);
    gbc_clearButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_clearButton.gridx = 2;
    gbc_clearButton.gridy = 1;
    add(getClearButton(), gbc_clearButton);
    if (!Beans.isDesignTime())
    {
      infomodel.addPropertyChangeListener(e -> modelChanged());
    }
  }

  private void modelChanged()
  {
    if (!infomodel.isDataChanged())
    {
      gamesFileUpdated = false;
    }
    // Read from model
    getDiskTextField().setText(getDiskFileName());
    getClearButton().setEnabled(!getDiskTextField().getText().isEmpty());
  }

  private JLabel getDiskLabel()
  {
    if (diskLabel == null)
    {
      diskLabel = new JLabel("Disk " + index + ":");
    }
    return diskLabel;
  }

  private JTextField getDiskTextField()
  {
    if (diskTextField == null)
    {
      diskTextField = new JTextField();
      diskTextField.setEditable(false);
      diskTextField.setPreferredSize(new Dimension(155, 20));
      new FileDrop(diskTextField, new FileDrop.Listener()
        {
          public void filesDropped(java.io.File[] files)
          {
            if (files.length > 0)
            {
              //Matcher for valid files
              PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{d64,g64,d81,d82,d71,x64}");
              if (!matcher.matches(files[0].toPath()))
              {
                JOptionPane.showMessageDialog(getDiskTextField(),
                                              "Invalid file format, it must be a d64, g64, d71, x64, d81 or d82 file.",
                                              "Game file",
                                              JOptionPane.ERROR_MESSAGE);
              }
              else
              {
                gamesFileUpdated = true;
                updateModelWithFile(files[0]);
              }
            }
          }
        });
    }
    return diskTextField;
  }

  private String getDiskFileName()
  {
    String returnValue = "";
    switch (index)
    {
    case 2:
      returnValue = infomodel.getDisk2File();
      break;
    case 3:
      returnValue = infomodel.getDisk3File();
      break;
    case 4:
      returnValue = infomodel.getDisk4File();
      break;
    case 5:
      returnValue = infomodel.getDisk5File();
      break;
    case 6:
      returnValue = infomodel.getDisk6File();
      break;
    default:
      break;
    }
    if (gamesFileUpdated)
    {
      returnValue = returnValue + " (updated)";
    }
    return returnValue;
  }

  private JButton getDiskButton()
  {
    if (diskButton == null)
    {
      diskButton = new JButton("...");
      diskButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            selectDiskFile();
          }
        });
      diskButton.setMargin(new Insets(1, 3, 1, 3));
    }
    return diskButton;
  }

  private void selectDiskFile()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select a valid extra disk file for " + infomodel.getTitle());
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    String gameDir = FileManager.getConfiguredProperties().getProperty(EXTRA_DISKS_DIR_PROPERTY);
    if (gameDir == null)
    {
      gameDir = ".";
    }
    fileChooser.setCurrentDirectory(new File(gameDir));

    FileNameExtensionFilter vicefilter =
      new FileNameExtensionFilter("Disk files", "d64", "g64", "d81", "d82", "d71", "x64");
    fileChooser.addChoosableFileFilter(vicefilter);
    fileChooser.setFileFilter(vicefilter);
    int value = fileChooser.showOpenDialog(MainWindow.getInstance());
    if (value == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = fileChooser.getSelectedFile();
      FileManager.getConfiguredProperties().put(EXTRA_DISKS_DIR_PROPERTY, selectedFile.toPath().getParent().toString());
      gamesFileUpdated = true;
      updateModelWithFile(selectedFile);
    }
  }

  private void updateModelWithFile(File selectedFile)
  {
    switch (index)
    {
    case 2:
      infomodel.setDisk2Path(selectedFile);
      break;
    case 3:
      infomodel.setDisk3Path(selectedFile);
      break;
    case 4:
      infomodel.setDisk4Path(selectedFile);
      break;
    case 5:
      infomodel.setDisk5Path(selectedFile);
      break;
    case 6:
      infomodel.setDisk6Path(selectedFile);
      break;
      
    default:
      break;
    }
  }

  private void clearSelectedDisk()
  {
    gamesFileUpdated = false;
    switch (index)
    {
    case 2:
      infomodel.setDisk2Path(null);
      infomodel.setDisk2File("");
      break;
    case 3:
      infomodel.setDisk3Path(null);
      infomodel.setDisk3File("");
      break;
    case 4:
      infomodel.setDisk4Path(null);
      infomodel.setDisk4File("");
      break;
    case 5:
      infomodel.setDisk5Path(null);
      infomodel.setDisk5File("");
      break;
    case 6:
      infomodel.setDisk6Path(null);
      infomodel.setDisk6File("");
      break;
    default:
      break;
    }
  }

  private JButton getClearButton()
  {
    if (clearButton == null)
    {
      clearButton = new JButton("Delete");
      clearButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            clearSelectedDisk();
            getClearButton().setEnabled(false);
          }
        });
      clearButton.setMargin(new Insets(1, 3, 1, 3));
    }
    return clearButton;
  }
}
