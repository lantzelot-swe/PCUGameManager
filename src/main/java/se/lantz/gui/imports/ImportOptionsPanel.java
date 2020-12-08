package se.lantz.gui.imports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import se.lantz.model.ImportManager;
import javax.swing.ButtonGroup;

public class ImportOptionsPanel extends JPanel
{
  private JLabel infoLabel;
  private JRadioButton overwriteRadioButton;
  private JRadioButton skipRadioButton;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JLabel matchLabel;

  public ImportOptionsPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.anchor = GridBagConstraints.WEST;
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.insets = new Insets(15, 10, 5, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_skipRadioButton = new GridBagConstraints();
    gbc_skipRadioButton.weightx = 1.0;
    gbc_skipRadioButton.anchor = GridBagConstraints.WEST;
    gbc_skipRadioButton.insets = new Insets(0, 5, 5, 0);
    gbc_skipRadioButton.gridx = 0;
    gbc_skipRadioButton.gridy = 1;
    add(getSkipRadioButton(), gbc_skipRadioButton);
    GridBagConstraints gbc_overwriteRadioButton = new GridBagConstraints();
    gbc_overwriteRadioButton.weightx = 1.0;
    gbc_overwriteRadioButton.insets = new Insets(0, 5, 5, 0);
    gbc_overwriteRadioButton.anchor = GridBagConstraints.WEST;
    gbc_overwriteRadioButton.gridx = 0;
    gbc_overwriteRadioButton.gridy = 2;
    add(getOverwriteRadioButton(), gbc_overwriteRadioButton);
    GridBagConstraints gbc_matchLabel = new GridBagConstraints();
    gbc_matchLabel.insets = new Insets(20, 10, 15, 10);
    gbc_matchLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_matchLabel.weighty = 1.0;
    gbc_matchLabel.weightx = 1.0;
    gbc_matchLabel.gridx = 0;
    gbc_matchLabel.gridy = 3;
    add(getMatchLabel(), gbc_matchLabel);
    // TODO Auto-generated constructor stub
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel =
        new JLabel("How shall already existing game entries in the database be handled?");
    }
    return infoLabel;
  }
  private JRadioButton getOverwriteRadioButton() {
    if (overwriteRadioButton == null) {
    	overwriteRadioButton = new JRadioButton("Overwrite with imported game");
    	buttonGroup.add(overwriteRadioButton);
    }
    return overwriteRadioButton;
  }
  private JRadioButton getSkipRadioButton() {
    if (skipRadioButton == null) {
    	skipRadioButton = new JRadioButton("Keep existing game in database");
    	skipRadioButton.setSelected(true);
    	buttonGroup.add(skipRadioButton);
    }
    return skipRadioButton;
  }
  
  public ImportManager.Options getSelectedOption()
  {
    ImportManager.Options returnValue = ImportManager.Options.SKIP;
    if (getOverwriteRadioButton().isSelected())
    {
      returnValue = ImportManager.Options.OVERWRITE;
    } 
    return returnValue;
  }
  private JLabel getMatchLabel() {
    if (matchLabel == null) {
    	matchLabel = new JLabel("Games are matched by title, case insensitive.");
    }
    return matchLabel;
  }
}
