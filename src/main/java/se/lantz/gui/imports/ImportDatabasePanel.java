package se.lantz.gui.imports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import se.lantz.gui.SelectDirPanel;
import se.lantz.gui.SelectDirPanel.Mode;

public class ImportDatabasePanel extends JPanel
{
  private JLabel infoLabel;
  private SelectDirPanel selectDirPanel;
  private JLabel info2Label;

  public ImportDatabasePanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.anchor = GridBagConstraints.WEST;
    gbc_infoLabel.insets = new Insets(10, 10, 0, 0);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_selectDirPanel = new GridBagConstraints();
    gbc_selectDirPanel.insets = new Insets(0, 5, 5, 0);
    gbc_selectDirPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_selectDirPanel.gridx = 0;
    gbc_selectDirPanel.gridy = 1;
    add(getSelectDirPanel(), gbc_selectDirPanel);
    GridBagConstraints gbc_info2Label = new GridBagConstraints();
    gbc_info2Label.anchor = GridBagConstraints.NORTHWEST;
    gbc_info2Label.weightx = 1.0;
    gbc_info2Label.weighty = 1.0;
    gbc_info2Label.insets = new Insets(0, 10, 20, 10);
    gbc_info2Label.fill = GridBagConstraints.HORIZONTAL;
    gbc_info2Label.gridx = 0;
    gbc_info2Label.gridy = 2;
    add(getInfo2Label(), gbc_info2Label);
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel = new JLabel("Select the root folder of a PCUAE Manager instance:");
    }
    return infoLabel;
  }

  private SelectDirPanel getSelectDirPanel()
  {
    if (selectDirPanel == null)
    {
      selectDirPanel = new SelectDirPanel(Mode.DATABASE_IMPORT);
    }
    return selectDirPanel;
  }

  private JLabel getInfo2Label()
  {
    if (info2Label == null)
    {
      info2Label =
        new JLabel("<html>You can import a complete database from an existing PCUAE Manager instance.<br>It will be added as a new database tab.</html>");
    }
    return info2Label;
  }

  File getTargetDirectory()
  {
    return getSelectDirPanel().getTargetDirectory();
  }
}
