package se.lantz.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DeletePanel extends JPanel
{
  private JLabel infoLabel;
  private JCheckBox createBackupCheckBox;
  private final boolean allGames;

  public DeletePanel(boolean allGames)
  {
    this.allGames = allGames;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.insets = new Insets(10, 10, 10, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_createBackupCheckBox = new GridBagConstraints();
    gbc_createBackupCheckBox.insets = new Insets(0, 5, 10, 5);
    gbc_createBackupCheckBox.anchor = GridBagConstraints.NORTHWEST;
    gbc_createBackupCheckBox.gridx = 0;
    gbc_createBackupCheckBox.gridy = 1;
    add(getCreateBackupCheckBox(), gbc_createBackupCheckBox);
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      String text =
        "<html>Are you sure you want to delete all games in the current view?<br>Covers, screenshots and game files will also be deleted.</html>";
      if (allGames)
      {
        text =
          "<html>Are you sure you want to delete all games in the database?<br>Covers, screenshots and game files will also be deleted.</html>";
      }
      infoLabel = new JLabel(text);
    }
    return infoLabel;
  }

  private JCheckBox getCreateBackupCheckBox()
  {
    if (createBackupCheckBox == null)
    {
      createBackupCheckBox = new JCheckBox("Create a backup before deleting");
      createBackupCheckBox.setSelected(true);
    }
    return createBackupCheckBox;
  }

  public boolean isCreatebackup()
  {
    return getCreateBackupCheckBox().isSelected();
  }
}
