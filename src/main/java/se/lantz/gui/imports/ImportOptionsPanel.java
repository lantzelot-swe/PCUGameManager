package se.lantz.gui.imports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import se.lantz.manager.ImportManager;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;

import se.lantz.gamebase.GamebaseOptions;
import se.lantz.gui.SelectDirPanel;
import se.lantz.gui.SelectDirPanel.Mode;

public class ImportOptionsPanel extends JPanel
{
  private JLabel infoLabel;
  private JRadioButton overwriteRadioButton;
  private JRadioButton skipRadioButton;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JLabel matchLabel;
  private JCheckBox favoriteCheckBox;
  private SelectDirPanel selectDirPanel;
  private GameBaseOptionsPanel gbOptionsPanel;
  private JLabel selectDirLabel;
  private JRadioButton addRadioButton;
  private JPanel selectionPanel;
  private boolean isCarouselImport;

  public ImportOptionsPanel()
  {
    this(true);
  }
  public ImportOptionsPanel(boolean isCarouselImport)
  {
    this.isCarouselImport = isCarouselImport;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_selectDirPanel = new GridBagConstraints();
    gbc_selectDirPanel.weightx = 1.0;
    gbc_selectDirPanel.insets = new Insets(0, 5, 5, 10);
    gbc_selectDirPanel.fill = GridBagConstraints.BOTH;
    gbc_selectDirPanel.gridx = 0;
    gbc_selectDirPanel.gridy = 1;
    if (isCarouselImport)
    {
      add(getSelectDirPanel(), gbc_selectDirPanel);
    }
    else
    {
      add(getGbOptionsPanel(), gbc_selectDirPanel);
    }
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.insets = new Insets(15, 10, 5, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 2;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_selectionPanel = new GridBagConstraints();
    gbc_selectionPanel.weightx = 1.0;
    gbc_selectionPanel.insets = new Insets(0, 0, 5, 0);
    gbc_selectionPanel.gridx = 0;
    gbc_selectionPanel.gridy = 3;
    add(getSelectionPanel(), gbc_selectionPanel);
    
    GridBagConstraints gbc_matchLabel = new GridBagConstraints();
    gbc_matchLabel.insets = new Insets(5, 10, 15, 10);
    gbc_matchLabel.anchor = GridBagConstraints.NORTH;
    gbc_matchLabel.weighty = 1.0;
    gbc_matchLabel.weightx = 1.0;
    gbc_matchLabel.gridx = 0;
    gbc_matchLabel.gridy = 4;
    add(getMatchLabel(), gbc_matchLabel);
    GridBagConstraints gbc_selectDirLabel = new GridBagConstraints();
    gbc_selectDirLabel.weightx = 1.0;
    gbc_selectDirLabel.insets = new Insets(10, 10, 0, 10);
    gbc_selectDirLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_selectDirLabel.gridx = 0;
    gbc_selectDirLabel.gridy = 0;
    add(getSelectDirLabel(), gbc_selectDirLabel);
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel =
        new JLabel("Select how to handle games where title matches an existing game in the database:");
    }
    return infoLabel;
  }
  private JRadioButton getOverwriteRadioButton() {
    if (overwriteRadioButton == null) {
    	overwriteRadioButton = new JRadioButton("Overwrite with imported game");
    	buttonGroup.add(overwriteRadioButton);
    	if (!this.isCarouselImport)
    	{
    	  //Too complex to overwrite several duplicates, don't allow it.
    	  overwriteRadioButton.setVisible(false);
    	}
    }
    return overwriteRadioButton;
  }
  private JRadioButton getSkipRadioButton() {
    if (skipRadioButton == null) {
    	skipRadioButton = new JRadioButton("Skip game from import");
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
    else if (getAddRadioButton().isSelected())
    {
      returnValue = ImportManager.Options.ADD;
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
  private SelectDirPanel getSelectDirPanel() {
    if (selectDirPanel == null) {
    	selectDirPanel = new SelectDirPanel(Mode.CAROUSEL_IMPORT);
    }
    return selectDirPanel;
  }
  private GameBaseOptionsPanel getGbOptionsPanel()
  {
    if (gbOptionsPanel == null) {
      gbOptionsPanel = new GameBaseOptionsPanel();
    }
    return gbOptionsPanel;
  }
  
  
  private JLabel getSelectDirLabel() {
    if (selectDirLabel == null) {
      String text = isCarouselImport ? "Select a directory containing a game carousel:" : "Select a gamebase database file (.mdb):"; 
    	selectDirLabel = new JLabel(text);
    }
    return selectDirLabel;
  }
  
  public Path getImportDirectory()
  {
    return selectDirPanel.getTargetDirectory().toPath();
  }
  private JRadioButton getAddRadioButton() {
    if (addRadioButton == null) {
    	addRadioButton = new JRadioButton("Add new duplicate game entry");
    	buttonGroup.add(addRadioButton);
    }
    return addRadioButton;
  }
  private JPanel getSelectionPanel() {
    if (selectionPanel == null) {
    	selectionPanel = new JPanel();
    	GridBagLayout gbl_selectionPanel = new GridBagLayout();
    	selectionPanel.setLayout(gbl_selectionPanel);
    	GridBagConstraints gbc_skipRadioButton = new GridBagConstraints();
      gbc_skipRadioButton.anchor = GridBagConstraints.WEST;
      gbc_skipRadioButton.weightx = 1.0;
      gbc_skipRadioButton.insets = new Insets(0, 5, 0, 0);
      gbc_skipRadioButton.gridx = 0;
      gbc_skipRadioButton.gridy = 0;
      selectionPanel.add(getSkipRadioButton(), gbc_skipRadioButton);
      GridBagConstraints gbc_overwriteRadioButton = new GridBagConstraints();
      gbc_overwriteRadioButton.anchor = GridBagConstraints.WEST;
      gbc_overwriteRadioButton.weightx = 1.0;
      gbc_overwriteRadioButton.insets = new Insets(0, 5, 0, 0);
      gbc_overwriteRadioButton.gridx = 0;
      gbc_overwriteRadioButton.gridy = 1;
      selectionPanel.add(getOverwriteRadioButton(), gbc_overwriteRadioButton);
      GridBagConstraints gbc_addRadioButton = new GridBagConstraints();
      gbc_addRadioButton.anchor = GridBagConstraints.WEST;
      gbc_addRadioButton.insets = new Insets(0, 5, 0, 0);
      gbc_addRadioButton.gridx = 0;
      gbc_addRadioButton.gridy = 2;
      selectionPanel.add(getAddRadioButton(), gbc_addRadioButton);
      GridBagConstraints gbc_favoriteCheckBox = new GridBagConstraints();
      gbc_favoriteCheckBox.weighty = 1.0;
      gbc_favoriteCheckBox.anchor = GridBagConstraints.NORTHWEST;
      gbc_favoriteCheckBox.insets = new Insets(5, 5, 5, 0);
      gbc_favoriteCheckBox.gridx = 0;
      gbc_favoriteCheckBox.gridy = 3;
      selectionPanel.add(getFavoriteCheckBox(), gbc_favoriteCheckBox);
    }
    return selectionPanel;
  }
  
  public GamebaseOptions getSelectedGbOptions()
  {
    return getGbOptionsPanel().getSelectedGbOptions();
  }
}
