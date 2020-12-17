package se.lantz.gui.dbrestore;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import se.lantz.util.FileManager;

public class RestoreDbPanel extends JPanel
{
  private JLabel infoLabel;
  private JComboBox<String> backupComboBox;
  private JLabel selectLabel;
  
  private String selectedBackupFolder = "";

  public RestoreDbPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWeights = new double[]{1.0};
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.anchor = GridBagConstraints.WEST;
    gbc_infoLabel.insets = new Insets(10, 10, 10, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_selectLabel = new GridBagConstraints();
    gbc_selectLabel.weightx = 1.0;
    gbc_selectLabel.anchor = GridBagConstraints.WEST;
    gbc_selectLabel.insets = new Insets(10, 10, 5, 10);
    gbc_selectLabel.gridx = 0;
    gbc_selectLabel.gridy = 1;
    add(getSelectLabel(), gbc_selectLabel);
    GridBagConstraints gbc_backupComboBox = new GridBagConstraints();
    gbc_backupComboBox.anchor = GridBagConstraints.NORTH;
    gbc_backupComboBox.weightx = 1.0;
    gbc_backupComboBox.weighty = 1.0;
    gbc_backupComboBox.insets = new Insets(0, 10, 10, 10);
    gbc_backupComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_backupComboBox.gridx = 0;
    gbc_backupComboBox.gridy = 2;
    add(getBackupComboBox(), gbc_backupComboBox);
    setupComboBox();
  }
  private JLabel getInfoLabel() {
    if (infoLabel == null) {
    	infoLabel = new JLabel("<html>Restoring a backup will overwrite the existing database and all covers, screens and game files. Custom views are also replaced.</html>");
    	infoLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
    }
    return infoLabel;
  }
  private JComboBox<String> getBackupComboBox() {
    if (backupComboBox == null) {
    	backupComboBox = new JComboBox<>();
    	backupComboBox.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    selectedBackupFolder = backupComboBox.getSelectedItem().toString();
    	  }
    	});
    }
    return backupComboBox;
  }
  private JLabel getSelectLabel() {
    if (selectLabel == null) {
    	selectLabel = new JLabel("Select backup to restore:");
    }
    return selectLabel;
  }
  
  private void setupComboBox()
  {
    List<String> backupsList = FileManager.getAllBackups();
    Collections.sort(backupsList, Collections.reverseOrder()); 
    
    for (String dirName : backupsList)
    {
      backupComboBox.addItem(dirName);
    }    
  }
  
  public String getSelectedFolder()
  {
    return selectedBackupFolder;
  }
}
