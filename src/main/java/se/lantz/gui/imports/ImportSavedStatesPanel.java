package se.lantz.gui.imports;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import se.lantz.gui.SelectDirPanel;
import se.lantz.gui.SelectDirPanel.Mode;
import java.awt.Insets;
import java.io.File;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class ImportSavedStatesPanel extends JPanel
{
  private JLabel infoLabel;
  private SelectDirPanel selectDirPanel;
  private JLabel choiceLabel;
  private JPanel choicePanel;
  private JRadioButton overwriteRadioButton;
  private JRadioButton keepRadioButton;
  private JLabel info2Label;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  public ImportSavedStatesPanel() {
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
    GridBagConstraints gbc_choiceLabel = new GridBagConstraints();
    gbc_choiceLabel.insets = new Insets(10, 0, 5, 0);
    gbc_choiceLabel.gridx = 0;
    gbc_choiceLabel.gridy = 2;
    add(getChoiceLabel(), gbc_choiceLabel);
    GridBagConstraints gbc_choicePanel = new GridBagConstraints();
    gbc_choicePanel.insets = new Insets(0, 0, 5, 0);
    gbc_choicePanel.anchor = GridBagConstraints.NORTH;
    gbc_choicePanel.weighty = 1.0;
    gbc_choicePanel.weightx = 1.0;
    gbc_choicePanel.gridx = 0;
    gbc_choicePanel.gridy = 3;
    add(getChoicePanel(), gbc_choicePanel);
    GridBagConstraints gbc_info2Label = new GridBagConstraints();
    gbc_info2Label.insets = new Insets(0, 10, 20, 10);
    gbc_info2Label.fill = GridBagConstraints.HORIZONTAL;
    gbc_info2Label.gridx = 0;
    gbc_info2Label.gridy = 4;
    add(getInfo2Label(), gbc_info2Label);
  }

  private JLabel getInfoLabel() {
    if (infoLabel == null) {
    	infoLabel = new JLabel("Select the \".THEC64SAVE\" folder in the root of your PCUAE USB stick:");
    }
    return infoLabel;
  }
  private SelectDirPanel getSelectDirPanel() {
    if (selectDirPanel == null) {
    	selectDirPanel = new SelectDirPanel(Mode.SAVEDSTATES_IMPORT);
    }
    return selectDirPanel;
  }
  private JLabel getChoiceLabel() {
    if (choiceLabel == null) {
    	choiceLabel = new JLabel("Select how to handle already available saved states in the \"saves\" folder:");
    }
    return choiceLabel;
  }
  private JPanel getChoicePanel() {
    if (choicePanel == null) {
    	choicePanel = new JPanel();
    	GridBagLayout gbl_choicePanel = new GridBagLayout();
    	choicePanel.setLayout(gbl_choicePanel);
    	GridBagConstraints gbc_overwriteRadioButton = new GridBagConstraints();
    	gbc_overwriteRadioButton.anchor = GridBagConstraints.WEST;
    	gbc_overwriteRadioButton.gridx = 0;
    	gbc_overwriteRadioButton.gridy = 0;
    	choicePanel.add(getOverwriteRadioButton(), gbc_overwriteRadioButton);
    	GridBagConstraints gbc_keepRadioButton = new GridBagConstraints();
    	gbc_keepRadioButton.anchor = GridBagConstraints.WEST;
    	gbc_keepRadioButton.gridx = 0;
    	gbc_keepRadioButton.gridy = 1;
    	choicePanel.add(getKeepRadioButton(), gbc_keepRadioButton);
    }
    return choicePanel;
  }
  private JRadioButton getOverwriteRadioButton() {
    if (overwriteRadioButton == null) {
    	overwriteRadioButton = new JRadioButton("Overwrite");
    	buttonGroup.add(overwriteRadioButton);
    	overwriteRadioButton.setSelected(true);
    }
    return overwriteRadioButton;
  }
  private JRadioButton getKeepRadioButton() {
    if (keepRadioButton == null) {
    	keepRadioButton = new JRadioButton("Skip from import");
    	buttonGroup.add(keepRadioButton);
    }
    return keepRadioButton;
  }
  private JLabel getInfo2Label() {
    if (info2Label == null) {
    	info2Label = new JLabel("<html>Saved states available in the import folder that doesn't match any games in the database will still be copied to the game manager's \"saves\" folder. They will also be copied when exporting.</html>");
    }
    return info2Label;
  }
  
  File getTargetDirectory()
  {
    return getSelectDirPanel().getTargetDirectory();
  }
  
  boolean isImportOverwrite()
  {
    return getOverwriteRadioButton().isSelected();
  }
}
