package se.lantz.gui.savedstates;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;

import se.lantz.gui.SelectDirPanel;
import se.lantz.gui.SelectDirPanel.Mode;

public class FixCorruptSavedStatesPanel extends JPanel
{
  private JLabel infoLabel;
  private SelectDirPanel selectDirPanel;

  public FixCorruptSavedStatesPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_infoLabel.anchor = GridBagConstraints.WEST;
    gbc_infoLabel.insets = new Insets(10, 10, 0, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_selectDirPanel = new GridBagConstraints();
    gbc_selectDirPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_selectDirPanel.weighty = 1.0;
    gbc_selectDirPanel.weightx = 1.0;
    gbc_selectDirPanel.insets = new Insets(0, 5, 5, 0);
    gbc_selectDirPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_selectDirPanel.gridx = 0;
    gbc_selectDirPanel.gridy = 1;
    add(getSelectDirPanel(), gbc_selectDirPanel);
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel =
        new JLabel("<html>Sometimes the saved states on a USB stick can become corrupt and cannot be loaded (a yellow triangle is shown over the screenshot in the saved states UI). This can be fixed by recreating the .mta file.<p><p>Select a folder containing saved states to fix any corrupt files:</html>");
    }
    return infoLabel;
  }

  private SelectDirPanel getSelectDirPanel()
  {
    if (selectDirPanel == null)
    {
      selectDirPanel = new SelectDirPanel(Mode.FIX_CORRUPT_SAVEDSTATES);
    }
    return selectDirPanel;
  }

  File getTargetDirectory()
  {
    return getSelectDirPanel().getTargetDirectory();
  }
}
