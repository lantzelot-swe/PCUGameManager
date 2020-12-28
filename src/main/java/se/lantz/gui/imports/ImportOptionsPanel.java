package se.lantz.gui.imports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import se.lantz.manager.ImportManager;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;

public class ImportOptionsPanel extends JPanel
{
  private JLabel infoLabel;
  private JRadioButton overwriteRadioButton;
  private JRadioButton skipRadioButton;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JLabel matchLabel;
  private JCheckBox favoriteCheckBox;

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
    gbc_skipRadioButton.anchor = GridBagConstraints.WEST;
    gbc_skipRadioButton.weightx = 1.0;
    gbc_skipRadioButton.insets = new Insets(0, 5, 0, 0);
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
    GridBagConstraints gbc_favoriteCheckBox = new GridBagConstraints();
    gbc_favoriteCheckBox.anchor = GridBagConstraints.WEST;
    gbc_favoriteCheckBox.insets = new Insets(5, 5, 5, 0);
    gbc_favoriteCheckBox.gridx = 0;
    gbc_favoriteCheckBox.gridy = 3;
    add(getFavoriteCheckBox(), gbc_favoriteCheckBox);
    GridBagConstraints gbc_matchLabel = new GridBagConstraints();
    gbc_matchLabel.insets = new Insets(15, 10, 15, 10);
    gbc_matchLabel.anchor = GridBagConstraints.NORTH;
    gbc_matchLabel.weighty = 1.0;
    gbc_matchLabel.weightx = 1.0;
    gbc_matchLabel.gridx = 0;
    gbc_matchLabel.gridy = 4;
    add(getMatchLabel(), gbc_matchLabel);
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
    	skipRadioButton = new JRadioButton("Skip, keep existing game in database");
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
  
  public boolean getMarkAsFavorite()
  {
    return getFavoriteCheckBox().isSelected();
  }
  
  private JLabel getMatchLabel() {
    if (matchLabel == null) {
    	matchLabel = new JLabel("Games are matched by title, case insensitive.");
    }
    return matchLabel;
  }
  private JCheckBox getFavoriteCheckBox() {
    if (favoriteCheckBox == null) {
    	favoriteCheckBox = new JCheckBox("Mark imported games as favorite");
    }
    return favoriteCheckBox;
  }
}
