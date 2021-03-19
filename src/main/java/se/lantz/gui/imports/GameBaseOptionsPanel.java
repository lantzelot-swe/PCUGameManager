package se.lantz.gui.imports;

import javax.swing.JPanel;

import se.lantz.gamebase.GamebaseImporter.Options;
import se.lantz.gamebase.GamebaseOptions;
import se.lantz.gui.SelectDirPanel;
import se.lantz.gui.SelectDirPanel.Mode;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class GameBaseOptionsPanel extends JPanel
{
  private SelectDirPanel selectDirPanel;
  private JPanel gameOptionsPanel;
  private JRadioButton favoritesRadioButton;
  private JRadioButton allRadioButton;
  private JRadioButton queryRadioButton;
  private final ButtonGroup optionButtonGroup = new ButtonGroup();
  private JTextField titleQueryTextField;
  private JLabel optionsLabel;
  private JPanel systemPanel;
  private JRadioButton c64RadioButton;
  private JRadioButton vic20RadioButton;
  private JLabel systemLabel;
  private final ButtonGroup systemButtonGroup = new ButtonGroup();
  private JCheckBox includeMissingCheckBox;

  public GameBaseOptionsPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_selectDirPanel = new GridBagConstraints();
    gbc_selectDirPanel.weightx = 1.0;
    gbc_selectDirPanel.insets = new Insets(0, 0, 5, 0);
    gbc_selectDirPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_selectDirPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_selectDirPanel.gridx = 0;
    gbc_selectDirPanel.gridy = 0;
    add(getSelectDirPanel(), gbc_selectDirPanel);
    GridBagConstraints gbc_systemLabel = new GridBagConstraints();
    gbc_systemLabel.insets = new Insets(0, 0, 5, 0);
    gbc_systemLabel.gridx = 0;
    gbc_systemLabel.gridy = 1;
    add(getSystemLabel(), gbc_systemLabel);
    GridBagConstraints gbc_systemPanel = new GridBagConstraints();
    gbc_systemPanel.insets = new Insets(0, 0, 5, 0);
    gbc_systemPanel.gridx = 0;
    gbc_systemPanel.gridy = 2;
    add(getSystemPanel(), gbc_systemPanel);
    GridBagConstraints gbc_gameOptionsPanel = new GridBagConstraints();
    gbc_gameOptionsPanel.anchor = GridBagConstraints.NORTH;
    gbc_gameOptionsPanel.gridx = 0;
    gbc_gameOptionsPanel.gridy = 4;
    add(getGameOptionsPanel(), gbc_gameOptionsPanel);
    GridBagConstraints gbc_optionsLabel = new GridBagConstraints();
    gbc_optionsLabel.insets = new Insets(10, 0, 5, 0);
    gbc_optionsLabel.gridx = 0;
    gbc_optionsLabel.gridy = 3;
    add(getOptionsLabel(), gbc_optionsLabel);
    GridBagConstraints gbc_includeMissingCheckBox = new GridBagConstraints();
    gbc_includeMissingCheckBox.anchor = GridBagConstraints.NORTH;
    gbc_includeMissingCheckBox.weighty = 1.0;
    gbc_includeMissingCheckBox.insets = new Insets(5, 0, 0, 0);
    gbc_includeMissingCheckBox.gridx = 0;
    gbc_includeMissingCheckBox.gridy = 5;
    add(getIncludeMissingCheckBox(), gbc_includeMissingCheckBox);

  }
  
  public GamebaseOptions getSelectedGbOptions()
  {
    GamebaseOptions options = new GamebaseOptions();
    options.setGamebaseDbFile(getSelectDirPanel().getTargetDirectory().toPath());
    options.setC64(getC64RadioButton().isSelected());
    if (getFavoritesRadioButton().isSelected())
    {
      options.setSelectedOption(Options.FAVORITES);
    }
    else if (getAllRadioButton().isSelected())
    {
      options.setSelectedOption(Options.ALL);
    }
    else
    {
      options.setSelectedOption(Options.QUERY);
      options.setTitleQueryString(getTitleQueryTextField().getText());
    }    
    options.setIncludeMissingGameFileEntries(includeMissingCheckBox.isSelected());   
    return options;
  }
  private SelectDirPanel getSelectDirPanel() {
    if (selectDirPanel == null) {
    	selectDirPanel = new SelectDirPanel(Mode.GB_IMPORT);
    }
    return selectDirPanel;
  }
  private JPanel getGameOptionsPanel() {
    if (gameOptionsPanel == null) {
    	gameOptionsPanel = new JPanel();
    	GridBagLayout gbl_gameOptionsPanel = new GridBagLayout();
    	gameOptionsPanel.setLayout(gbl_gameOptionsPanel);
    	GridBagConstraints gbc_favoritesRadioButton = new GridBagConstraints();
    	gbc_favoritesRadioButton.anchor = GridBagConstraints.WEST;
    	gbc_favoritesRadioButton.gridx = 0;
    	gbc_favoritesRadioButton.gridy = 0;
    	gameOptionsPanel.add(getFavoritesRadioButton(), gbc_favoritesRadioButton);
    	GridBagConstraints gbc_allRadioButton = new GridBagConstraints();
    	gbc_allRadioButton.anchor = GridBagConstraints.WEST;
    	gbc_allRadioButton.gridx = 0;
    	gbc_allRadioButton.gridy = 1;
    	gameOptionsPanel.add(getAllRadioButton(), gbc_allRadioButton);
    	GridBagConstraints gbc_queryRadioButton = new GridBagConstraints();
    	gbc_queryRadioButton.anchor = GridBagConstraints.WEST;
    	gbc_queryRadioButton.gridx = 0;
    	gbc_queryRadioButton.gridy = 2;
    	gameOptionsPanel.add(getQueryRadioButton(), gbc_queryRadioButton);
    	GridBagConstraints gbc_titleQueryTextField = new GridBagConstraints();
    	gbc_titleQueryTextField.insets = new Insets(0, 20, 5, 10);
    	gbc_titleQueryTextField.anchor = GridBagConstraints.WEST;
    	gbc_titleQueryTextField.fill = GridBagConstraints.HORIZONTAL;
    	gbc_titleQueryTextField.gridx = 0;
    	gbc_titleQueryTextField.gridy = 3;
    	gameOptionsPanel.add(getTitleQueryTextField(), gbc_titleQueryTextField);
    }
    return gameOptionsPanel;
  }
  private JRadioButton getFavoritesRadioButton() {
    if (favoritesRadioButton == null) {
    	favoritesRadioButton = new JRadioButton("All games marked as favorites in the gamebase db");
    	favoritesRadioButton.setSelected(true);
    	favoritesRadioButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          getTitleQueryTextField().setEnabled(queryRadioButton.isSelected());
        }
      });
    	optionButtonGroup.add(favoritesRadioButton);
    }
    return favoritesRadioButton;
  }
  private JRadioButton getAllRadioButton() {
    if (allRadioButton == null) {
    	allRadioButton = new JRadioButton("ALL games (WARNING: This can take a very long time)");
    	allRadioButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          getTitleQueryTextField().setEnabled(queryRadioButton.isSelected());
        }
      });
    	optionButtonGroup.add(allRadioButton);
    }
    return allRadioButton;
  }
  private JRadioButton getQueryRadioButton() {
    if (queryRadioButton == null) {
    	queryRadioButton = new JRadioButton("All games where title matches the following text:");
    	queryRadioButton.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    getTitleQueryTextField().setEnabled(queryRadioButton.isSelected());
    	  }
    	});
    	optionButtonGroup.add(queryRadioButton);
    }
    return queryRadioButton;
  }
  private JTextField getTitleQueryTextField() {
    if (titleQueryTextField == null) {
    	titleQueryTextField = new JTextField();
    	titleQueryTextField.setEnabled(false);
    	titleQueryTextField.setToolTipText("<html>Use wildcard characters for Microsoft Access, e.g. * or ?</html>");
    }
    return titleQueryTextField;
  }
  private JLabel getOptionsLabel() {
    if (optionsLabel == null) {
    	optionsLabel = new JLabel("What do you want to import?");
    }
    return optionsLabel;
  }
  private JPanel getSystemPanel() {
    if (systemPanel == null) {
    	systemPanel = new JPanel();
    	GridBagLayout gbl_systemPanel = new GridBagLayout();
    	systemPanel.setLayout(gbl_systemPanel);
    	GridBagConstraints gbc_c64RadioButton = new GridBagConstraints();
    	gbc_c64RadioButton.anchor = GridBagConstraints.WEST;
    	gbc_c64RadioButton.gridx = 0;
    	gbc_c64RadioButton.gridy = 0;
    	systemPanel.add(getC64RadioButton(), gbc_c64RadioButton);
    	GridBagConstraints gbc_vic20RadioButton = new GridBagConstraints();
    	gbc_vic20RadioButton.anchor = GridBagConstraints.WEST;
    	gbc_vic20RadioButton.gridx = 0;
    	gbc_vic20RadioButton.gridy = 1;
    	systemPanel.add(getVic20RadioButton(), gbc_vic20RadioButton);
    }
    return systemPanel;
  }
  private JRadioButton getC64RadioButton() {
    if (c64RadioButton == null) {
    	c64RadioButton = new JRadioButton("C64");
    	c64RadioButton.setSelected(true);
    	systemButtonGroup.add(c64RadioButton);
    }
    return c64RadioButton;
  }
  private JRadioButton getVic20RadioButton() {
    if (vic20RadioButton == null) {
    	vic20RadioButton = new JRadioButton("Vic-20");
    	systemButtonGroup.add(vic20RadioButton);
    }
    return vic20RadioButton;
  }
  private JLabel getSystemLabel() {
    if (systemLabel == null) {
    	systemLabel = new JLabel("Which system is the gamebase db for?");
    }
    return systemLabel;
  }
  private JCheckBox getIncludeMissingCheckBox() {
    if (includeMissingCheckBox == null) {
    	includeMissingCheckBox = new JCheckBox("Include game entries with missing game file");
    }
    return includeMissingCheckBox;
  }
}
