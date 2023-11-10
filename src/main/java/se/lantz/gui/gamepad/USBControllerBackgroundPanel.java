package se.lantz.gui.gamepad;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import se.lantz.model.JoystickModel;

public class USBControllerBackgroundPanel extends JPanel
{
  private USBControllerInfoPanel gamePadInfoPanel;
  private USBControllerImagePanel gamePadImagePanel;
  private USBControllerMappingPanel gamePadMappingPanel;
  private JoystickModel model;
  public USBControllerBackgroundPanel(JoystickModel model) {
    this.model = model;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_gamePadInfoPanel = new GridBagConstraints();
    gbc_gamePadInfoPanel.gridwidth = 2;
    gbc_gamePadInfoPanel.insets = new Insets(0, 0, 5, 0);
    gbc_gamePadInfoPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_gamePadInfoPanel.weightx = 1.0;
    gbc_gamePadInfoPanel.fill = GridBagConstraints.BOTH;
    gbc_gamePadInfoPanel.gridx = 0;
    gbc_gamePadInfoPanel.gridy = 0;
    add(getGamePadInfoPanel(), gbc_gamePadInfoPanel);
    GridBagConstraints gbc_gamePadImagePanel = new GridBagConstraints();
    gbc_gamePadImagePanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_gamePadImagePanel.insets = new Insets(0, 0, 5, 10);
    gbc_gamePadImagePanel.weightx = 1.0;
    gbc_gamePadImagePanel.fill = GridBagConstraints.BOTH;
    gbc_gamePadImagePanel.gridx = 1;
    gbc_gamePadImagePanel.gridy = 1;
    add(getGamePadImagePanel(), gbc_gamePadImagePanel);
    GridBagConstraints gbc_gamePadMappingPanel = new GridBagConstraints();
    gbc_gamePadMappingPanel.insets = new Insets(0, 10, 0, 0);
    gbc_gamePadMappingPanel.weighty = 1.0;
    gbc_gamePadMappingPanel.weightx = 1.0;
    gbc_gamePadMappingPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_gamePadMappingPanel.fill = GridBagConstraints.BOTH;
    gbc_gamePadMappingPanel.gridx = 0;
    gbc_gamePadMappingPanel.gridy = 1;
    add(getGamePadMappingPanel(), gbc_gamePadMappingPanel);
  }
  private USBControllerInfoPanel getGamePadInfoPanel() {
    if (gamePadInfoPanel == null) {
    	gamePadInfoPanel = new USBControllerInfoPanel();
    }
    return gamePadInfoPanel;
  }
  private USBControllerImagePanel getGamePadImagePanel() {
    if (gamePadImagePanel == null) {
    	gamePadImagePanel = new USBControllerImagePanel();
    }
    return gamePadImagePanel;
  }
  private USBControllerMappingPanel getGamePadMappingPanel() {
    if (gamePadMappingPanel == null) {
    	gamePadMappingPanel = new USBControllerMappingPanel(model, getGamePadImagePanel());
    }
    return gamePadMappingPanel;
  }
}
