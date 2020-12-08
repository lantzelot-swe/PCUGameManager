package se.lantz.gui;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JRadioButton;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class SystemPanel extends JPanel
{
  private JPanel radioPanel;
  private JRadioButton c64Button;
  private JRadioButton vic20Button;
  private final ButtonGroup typeButtonGroup = new ButtonGroup();
  private JPanel typePanel;
  private JPanel drivePanel;
  private JPanel displayPanel;
  private JPanel cardPanel;
  private JPanel audioPanel;
  private JPanel ramPanel;
  private JPanel configPanel;
  private JLabel configLabel;
  private JTextField configTextField;
  private JRadioButton palRadioButton;
  private JRadioButton ntscRadioButton;
  private final ButtonGroup typeGroup = new ButtonGroup();
  private JCheckBox driveIconCheckBox;
  private JCheckBox accurateDiskCheckBox;
  private JCheckBox fullHeightCheckBox;
  private DisplayShiftComboBox displayShiftComboBox;
  private JLabel displayShiftLabel;
  private JRadioButton sid6581RadioButton;
  private JRadioButton sid8580RadioButton;
  private JRadioButton sid8580dRadioButton;
  private JCheckBox noAudioScaleCheckBox;
  private JCheckBox bank0CheckBox;
  private JCheckBox bank1CheckBox;
  private JCheckBox bank2CheckBox;
  private JCheckBox bank3CheckBox;
  private JCheckBox bank5CheckBox;
  private JLabel ramLabel;
  
  private CardLayout cardLayout = new CardLayout(0, 0);
  private final ButtonGroup audioGroup = new ButtonGroup();

  public SystemPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_radioPanel = new GridBagConstraints();
    gbc_radioPanel.weightx = 1.0;
    gbc_radioPanel.gridwidth = 2;
    gbc_radioPanel.insets = new Insets(0, 0, 5, 0);
    gbc_radioPanel.fill = GridBagConstraints.BOTH;
    gbc_radioPanel.gridx = 0;
    gbc_radioPanel.gridy = 0;
    add(getRadioPanel(), gbc_radioPanel);
    GridBagConstraints gbc_typePanel = new GridBagConstraints();
    gbc_typePanel.weightx = 0.5;
    gbc_typePanel.insets = new Insets(5, 5, 5, 5);
    gbc_typePanel.fill = GridBagConstraints.BOTH;
    gbc_typePanel.gridx = 0;
    gbc_typePanel.gridy = 1;
    add(getTypePanel(), gbc_typePanel);
    GridBagConstraints gbc_drivePanel = new GridBagConstraints();
    gbc_drivePanel.insets = new Insets(5, 0, 5, 5);
    gbc_drivePanel.weightx = 0.5;
    gbc_drivePanel.fill = GridBagConstraints.BOTH;
    gbc_drivePanel.gridx = 1;
    gbc_drivePanel.gridy = 1;
    add(getDrivePanel(), gbc_drivePanel);
    GridBagConstraints gbc_displayPanel = new GridBagConstraints();
    gbc_displayPanel.weightx = 1.0;
    gbc_displayPanel.insets = new Insets(0, 5, 5, 5);
    gbc_displayPanel.gridwidth = 2;
    gbc_displayPanel.fill = GridBagConstraints.BOTH;
    gbc_displayPanel.gridx = 0;
    gbc_displayPanel.gridy = 2;
    add(getDisplayPanel(), gbc_displayPanel);
    GridBagConstraints gbc_cardPanel = new GridBagConstraints();
    gbc_cardPanel.weightx = 1.0;
    gbc_cardPanel.insets = new Insets(0, 5, 5, 5);
    gbc_cardPanel.gridwidth = 2;
    gbc_cardPanel.fill = GridBagConstraints.BOTH;
    gbc_cardPanel.gridx = 0;
    gbc_cardPanel.gridy = 3;
    add(getCardPanel(), gbc_cardPanel);
    GridBagConstraints gbc_configPanel = new GridBagConstraints();
    gbc_configPanel.gridwidth = 2;
    gbc_configPanel.weightx = 1.0;
    gbc_configPanel.weighty = 1.0;
    gbc_configPanel.insets = new Insets(0, 0, 0, 5);
    gbc_configPanel.fill = GridBagConstraints.BOTH;
    gbc_configPanel.gridx = 0;
    gbc_configPanel.gridy = 4;
    add(getConfigPanel(), gbc_configPanel);
  }

  private JPanel getRadioPanel() {
    if (radioPanel == null) {
    	radioPanel = new JPanel();
    	GridBagLayout gbl_radioPanel = new GridBagLayout();
    	radioPanel.setLayout(gbl_radioPanel);
    	GridBagConstraints gbc_c64Button = new GridBagConstraints();
    	gbc_c64Button.weightx = 1.0;
    	gbc_c64Button.anchor = GridBagConstraints.NORTHWEST;
    	gbc_c64Button.insets = new Insets(5, 5, 0, 5);
    	gbc_c64Button.gridx = 0;
    	gbc_c64Button.gridy = 0;
    	radioPanel.add(getC64Button(), gbc_c64Button);
    	GridBagConstraints gbc_vic20Button = new GridBagConstraints();
    	gbc_vic20Button.weightx = 1.0;
    	gbc_vic20Button.weighty = 1.0;
    	gbc_vic20Button.insets = new Insets(0, 5, 5, 5);
    	gbc_vic20Button.anchor = GridBagConstraints.NORTHWEST;
    	gbc_vic20Button.gridx = 0;
    	gbc_vic20Button.gridy = 1;
    	radioPanel.add(getVic20Button(), gbc_vic20Button);
    }
    return radioPanel;
  }
  private JRadioButton getC64Button() {
    if (c64Button == null) {
    	c64Button = new JRadioButton("Commodore 64");
    	c64Button.setSelected(true);
    	c64Button.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    cardLayout.show(getCardPanel(), "audio");
    	    getDisplayShiftComboBox().setup64Items();
    	  }
    	});
    	typeButtonGroup.add(c64Button);
    }
    return c64Button;
  }
  private JRadioButton getVic20Button() {
    if (vic20Button == null) {
    	vic20Button = new JRadioButton("Commodore Vic-20");
    	vic20Button.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    cardLayout.show(getCardPanel(), "ram");
    	    getDisplayShiftComboBox().setupVic20Items();
    	  }
    	});
    	typeButtonGroup.add(vic20Button);
    }
    return vic20Button;
  }
  private JPanel getTypePanel() {
    if (typePanel == null) {
    	typePanel = new JPanel();
    	typePanel.setBorder(new TitledBorder(null, "System Type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    	GridBagLayout gbl_typePanel = new GridBagLayout();
    	typePanel.setLayout(gbl_typePanel);
    	GridBagConstraints gbc_palRadioButton = new GridBagConstraints();
    	gbc_palRadioButton.weightx = 1.0;
    	gbc_palRadioButton.anchor = GridBagConstraints.NORTHWEST;
    	gbc_palRadioButton.gridx = 0;
    	gbc_palRadioButton.gridy = 0;
    	typePanel.add(getPalRadioButton(), gbc_palRadioButton);
    	GridBagConstraints gbc_ntscRadioButton = new GridBagConstraints();
    	gbc_ntscRadioButton.weightx = 1.0;
    	gbc_ntscRadioButton.anchor = GridBagConstraints.WEST;
    	gbc_ntscRadioButton.gridx = 0;
    	gbc_ntscRadioButton.gridy = 1;
    	typePanel.add(getNtscRadioButton(), gbc_ntscRadioButton);
    }
    return typePanel;
  }
  private JPanel getDrivePanel() {
    if (drivePanel == null) {
    	drivePanel = new JPanel();
    	drivePanel.setBorder(new TitledBorder(null, "Drive Emulation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    	GridBagLayout gbl_drivePanel = new GridBagLayout();
    	drivePanel.setLayout(gbl_drivePanel);
    	GridBagConstraints gbc_driveIconCheckBox = new GridBagConstraints();
    	gbc_driveIconCheckBox.weightx = 1.0;
    	gbc_driveIconCheckBox.anchor = GridBagConstraints.NORTHWEST;
    	gbc_driveIconCheckBox.gridx = 0;
    	gbc_driveIconCheckBox.gridy = 0;
    	drivePanel.add(getDriveIconCheckBox(), gbc_driveIconCheckBox);
    	GridBagConstraints gbc_accurateDiskCheckBox = new GridBagConstraints();
    	gbc_accurateDiskCheckBox.weightx = 1.0;
    	gbc_accurateDiskCheckBox.anchor = GridBagConstraints.NORTHWEST;
    	gbc_accurateDiskCheckBox.gridx = 0;
    	gbc_accurateDiskCheckBox.gridy = 1;
    	drivePanel.add(getAccurateDiskCheckBox(), gbc_accurateDiskCheckBox);
    }
    return drivePanel;
  }
  private JPanel getDisplayPanel() {
    if (displayPanel == null) {
    	displayPanel = new JPanel();
    	displayPanel.setBorder(new TitledBorder(null, "Display", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    	GridBagLayout gbl_displayPanel = new GridBagLayout();
    	displayPanel.setLayout(gbl_displayPanel);
    	GridBagConstraints gbc_fullHeightCheckBox = new GridBagConstraints();
    	gbc_fullHeightCheckBox.gridwidth = 2;
    	gbc_fullHeightCheckBox.anchor = GridBagConstraints.NORTHWEST;
    	gbc_fullHeightCheckBox.insets = new Insets(0, 0, 5, 0);
    	gbc_fullHeightCheckBox.gridx = 0;
    	gbc_fullHeightCheckBox.gridy = 0;
    	displayPanel.add(getFullHeightCheckBox(), gbc_fullHeightCheckBox);
    	GridBagConstraints gbc_displayShiftComboBox = new GridBagConstraints();
    	gbc_displayShiftComboBox.insets = new Insets(0, 0, 0, 5);
    	gbc_displayShiftComboBox.fill = GridBagConstraints.HORIZONTAL;
    	gbc_displayShiftComboBox.gridx = 0;
    	gbc_displayShiftComboBox.gridy = 1;
    	displayPanel.add(getDisplayShiftComboBox(), gbc_displayShiftComboBox);
    	GridBagConstraints gbc_displayShiftLabel = new GridBagConstraints();
    	gbc_displayShiftLabel.anchor = GridBagConstraints.WEST;
    	gbc_displayShiftLabel.weightx = 1.0;
    	gbc_displayShiftLabel.gridx = 1;
    	gbc_displayShiftLabel.gridy = 1;
    	displayPanel.add(getDisplayShiftLabel(), gbc_displayShiftLabel);
    }
    return displayPanel;
  }
  private JPanel getCardPanel() {
    if (cardPanel == null) {
    	cardPanel = new JPanel();
    	cardPanel.setLayout(cardLayout);
    	cardPanel.add(getAudioPanel(), "audio");
    	cardPanel.add(getRamPanel(), "ram");
    }
    return cardPanel;
  }
  private JPanel getAudioPanel() {
    if (audioPanel == null) {
    	audioPanel = new JPanel();
    	audioPanel.setBorder(new TitledBorder(null, "Audio", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    	GridBagLayout gbl_audioPanel = new GridBagLayout();
    	gbl_audioPanel.columnWidths = new int[]{0, 0, 0};
    	gbl_audioPanel.rowHeights = new int[]{0, 0, 0, 0};
    	gbl_audioPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
    	gbl_audioPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
    	audioPanel.setLayout(gbl_audioPanel);
    	GridBagConstraints gbc_sid6581RadioButton = new GridBagConstraints();
    	gbc_sid6581RadioButton.anchor = GridBagConstraints.WEST;
    	gbc_sid6581RadioButton.insets = new Insets(0, 0, 0, 5);
    	gbc_sid6581RadioButton.gridx = 0;
    	gbc_sid6581RadioButton.gridy = 0;
    	audioPanel.add(getSid6581RadioButton(), gbc_sid6581RadioButton);
    	GridBagConstraints gbc_noAudioScaleCheckBox = new GridBagConstraints();
    	gbc_noAudioScaleCheckBox.anchor = GridBagConstraints.WEST;
    	gbc_noAudioScaleCheckBox.weightx = 1.0;
    	gbc_noAudioScaleCheckBox.gridx = 1;
    	gbc_noAudioScaleCheckBox.gridy = 0;
    	audioPanel.add(getNoAudioScaleCheckBox(), gbc_noAudioScaleCheckBox);
    	GridBagConstraints gbc_sid8580RadioButton = new GridBagConstraints();
    	gbc_sid8580RadioButton.anchor = GridBagConstraints.WEST;
    	gbc_sid8580RadioButton.insets = new Insets(0, 0, 0, 5);
    	gbc_sid8580RadioButton.gridx = 0;
    	gbc_sid8580RadioButton.gridy = 1;
    	audioPanel.add(getSid8580RadioButton(), gbc_sid8580RadioButton);
    	GridBagConstraints gbc_sid8580dRadioButton = new GridBagConstraints();
    	gbc_sid8580dRadioButton.anchor = GridBagConstraints.WEST;
    	gbc_sid8580dRadioButton.insets = new Insets(0, 0, 0, 5);
    	gbc_sid8580dRadioButton.gridx = 0;
    	gbc_sid8580dRadioButton.gridy = 2;
    	audioPanel.add(getSid8580dRadioButton(), gbc_sid8580dRadioButton);
    }
    return audioPanel;
  }
  private JPanel getRamPanel() {
    if (ramPanel == null) {
    	ramPanel = new JPanel();
    	ramPanel.setBorder(new TitledBorder(null, "Ram", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    	GridBagLayout gbl_ramPanel = new GridBagLayout();
    	ramPanel.setLayout(gbl_ramPanel);
    	GridBagConstraints gbc_bank0CheckBox = new GridBagConstraints();
    	gbc_bank0CheckBox.weightx = 1.0;
    	gbc_bank0CheckBox.anchor = GridBagConstraints.WEST;
    	gbc_bank0CheckBox.gridx = 0;
    	gbc_bank0CheckBox.gridy = 0;
    	ramPanel.add(getBank0CheckBox(), gbc_bank0CheckBox);
    	GridBagConstraints gbc_bank1CheckBox = new GridBagConstraints();
    	gbc_bank1CheckBox.weightx = 1.0;
    	gbc_bank1CheckBox.anchor = GridBagConstraints.WEST;
    	gbc_bank1CheckBox.gridx = 0;
    	gbc_bank1CheckBox.gridy = 1;
    	ramPanel.add(getBank1CheckBox(), gbc_bank1CheckBox);
    	GridBagConstraints gbc_bank2CheckBox = new GridBagConstraints();
    	gbc_bank2CheckBox.weightx = 1.0;
    	gbc_bank2CheckBox.anchor = GridBagConstraints.WEST;
    	gbc_bank2CheckBox.gridx = 0;
    	gbc_bank2CheckBox.gridy = 2;
    	ramPanel.add(getBank2CheckBox(), gbc_bank2CheckBox);
    	GridBagConstraints gbc_bank3CheckBox = new GridBagConstraints();
    	gbc_bank3CheckBox.weightx = 1.0;
    	gbc_bank3CheckBox.anchor = GridBagConstraints.WEST;
    	gbc_bank3CheckBox.gridx = 0;
    	gbc_bank3CheckBox.gridy = 3;
    	ramPanel.add(getBank3CheckBox(), gbc_bank3CheckBox);
    	GridBagConstraints gbc_bank5CheckBox = new GridBagConstraints();
    	gbc_bank5CheckBox.weightx = 1.0;
    	gbc_bank5CheckBox.anchor = GridBagConstraints.WEST;
    	gbc_bank5CheckBox.insets = new Insets(0, 0, 5, 0);
    	gbc_bank5CheckBox.gridx = 0;
    	gbc_bank5CheckBox.gridy = 4;
    	ramPanel.add(getBank5CheckBox(), gbc_bank5CheckBox);
    	GridBagConstraints gbc_ramLabel = new GridBagConstraints();
    	gbc_ramLabel.insets = new Insets(0, 5, 10, 5);
    	gbc_ramLabel.anchor = GridBagConstraints.WEST;
    	gbc_ramLabel.weightx = 1.0;
    	gbc_ramLabel.gridx = 0;
    	gbc_ramLabel.gridy = 5;
    	ramPanel.add(getRamLabel(), gbc_ramLabel);
    }
    return ramPanel;
  }
  private JPanel getConfigPanel() {
    if (configPanel == null) {
    	configPanel = new JPanel();
    	GridBagLayout gbl_configPanel = new GridBagLayout();
    	configPanel.setLayout(gbl_configPanel);
    	GridBagConstraints gbc_configLabel = new GridBagConstraints();
    	gbc_configLabel.weighty = 1.0;
    	gbc_configLabel.anchor = GridBagConstraints.SOUTHWEST;
    	gbc_configLabel.insets = new Insets(5, 5, 16, 5);
    	gbc_configLabel.gridx = 0;
    	gbc_configLabel.gridy = 0;
    	configPanel.add(getConfigLabel(), gbc_configLabel);
    	GridBagConstraints gbc_configTextField = new GridBagConstraints();
    	gbc_configTextField.fill = GridBagConstraints.HORIZONTAL;
    	gbc_configTextField.weightx = 1.0;
    	gbc_configTextField.insets = new Insets(5, 0, 14, 5);
    	gbc_configTextField.weighty = 1.0;
    	gbc_configTextField.anchor = GridBagConstraints.SOUTHWEST;
    	gbc_configTextField.gridx = 1;
    	gbc_configTextField.gridy = 0;
    	configPanel.add(getConfigTextField(), gbc_configTextField);
    }
    return configPanel;
  }
  private JLabel getConfigLabel() {
    if (configLabel == null) {
    	configLabel = new JLabel("Config:");
    }
    return configLabel;
  }
  private JTextField getConfigTextField() {
    if (configTextField == null) {
    	configTextField = new JTextField();
    	configTextField.setColumns(10);
    }
    return configTextField;
  }
  private JRadioButton getPalRadioButton() {
    if (palRadioButton == null) {
    	palRadioButton = new JRadioButton("PAL");
    	palRadioButton.setSelected(true);
    	typeGroup.add(palRadioButton);
    }
    return palRadioButton;
  }
  private JRadioButton getNtscRadioButton() {
    if (ntscRadioButton == null) {
    	ntscRadioButton = new JRadioButton("NTSC");
    	typeGroup.add(ntscRadioButton);
    }
    return ntscRadioButton;
  }
  private JCheckBox getDriveIconCheckBox() {
    if (driveIconCheckBox == null) {
    	driveIconCheckBox = new JCheckBox("Drive Icon");
    }
    return driveIconCheckBox;
  }
  private JCheckBox getAccurateDiskCheckBox() {
    if (accurateDiskCheckBox == null) {
    	accurateDiskCheckBox = new JCheckBox("Accurate Disk");
    }
    return accurateDiskCheckBox;
  }
  private JCheckBox getFullHeightCheckBox() {
    if (fullHeightCheckBox == null) {
    	fullHeightCheckBox = new JCheckBox("Full Height");
    }
    return fullHeightCheckBox;
  }
  private DisplayShiftComboBox getDisplayShiftComboBox() {
    if (displayShiftComboBox == null) {
    	displayShiftComboBox = new DisplayShiftComboBox();
    }
    return displayShiftComboBox;
  }
  private JLabel getDisplayShiftLabel() {
    if (displayShiftLabel == null) {
    	displayShiftLabel = new JLabel("Vertical Display Shift");
    }
    return displayShiftLabel;
  }
  private JRadioButton getSid6581RadioButton() {
    if (sid6581RadioButton == null) {
    	sid6581RadioButton = new JRadioButton("SID 6581");
    	audioGroup.add(sid6581RadioButton);
    	sid6581RadioButton.setSelected(true);
    	sid6581RadioButton.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	  }
    	});
    }
    return sid6581RadioButton;
  }
  private JRadioButton getSid8580RadioButton() {
    if (sid8580RadioButton == null) {
    	sid8580RadioButton = new JRadioButton("SID 8580");
    	audioGroup.add(sid8580RadioButton);
    }
    return sid8580RadioButton;
  }
  private JRadioButton getSid8580dRadioButton() {
    if (sid8580dRadioButton == null) {
    	sid8580dRadioButton = new JRadioButton("SID 8580D");
    	audioGroup.add(sid8580dRadioButton);
    }
    return sid8580dRadioButton;
  }
  private JCheckBox getNoAudioScaleCheckBox() {
    if (noAudioScaleCheckBox == null) {
    	noAudioScaleCheckBox = new JCheckBox("No Audio Scale");
    }
    return noAudioScaleCheckBox;
  }
  private JCheckBox getBank0CheckBox() {
    if (bank0CheckBox == null) {
    	bank0CheckBox = new JCheckBox("Bank 0 (3K @ $0400-$0FFF)");
    	bank0CheckBox.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    calculateTotalRam();
    	  }
    	});
    }
    return bank0CheckBox;
  }
  private JCheckBox getBank1CheckBox() {
    if (bank1CheckBox == null) {
    	bank1CheckBox = new JCheckBox("Bank 1 (8K @ $2000-$3FFF)");
    	bank1CheckBox.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    calculateTotalRam();
    	  }
    	});
    }
    return bank1CheckBox;
  }
  private JCheckBox getBank2CheckBox() {
    if (bank2CheckBox == null) {
    	bank2CheckBox = new JCheckBox("Bank 2 (8K @ $4000-$5FFF)");
    	bank2CheckBox.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    calculateTotalRam();
    	  }
    	});
    }
    return bank2CheckBox;
  }
  private JCheckBox getBank3CheckBox() {
    if (bank3CheckBox == null) {
    	bank3CheckBox = new JCheckBox("Bank 3 (8K @ $6000-$7FFF)");
    	bank3CheckBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          calculateTotalRam();
        }
      });
    }
    return bank3CheckBox;
  }
  private JCheckBox getBank5CheckBox() {
    if (bank5CheckBox == null) {
    	bank5CheckBox = new JCheckBox("Bank 5 (8K @ $A000-$BFFF)");
    	bank5CheckBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          calculateTotalRam();
        }
      });
    }
    return bank5CheckBox;
  }
  private JLabel getRamLabel() {
    if (ramLabel == null) {
    	ramLabel = new JLabel("Ram Expansion: 0K");
    	ramLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
    }
    return ramLabel;
  }
  
  private void calculateTotalRam()
  {
    int totalRam = 0;
    if (getBank0CheckBox().isSelected())
    {
      totalRam = totalRam + 3;
    }
    if (getBank1CheckBox().isSelected())
    {
      totalRam = totalRam + 8;
    }
    if (getBank2CheckBox().isSelected())
    {
      totalRam = totalRam + 8;
    }
    if (getBank3CheckBox().isSelected())
    {
      totalRam = totalRam + 8;
    }
    if (getBank5CheckBox().isSelected())
    {
      totalRam = totalRam + 8;
    }
    getRamLabel().setText("Ram Expansion: " + totalRam + "K");
  }
}
