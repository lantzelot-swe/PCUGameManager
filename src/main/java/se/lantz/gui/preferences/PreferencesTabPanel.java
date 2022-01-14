package se.lantz.gui.preferences;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import se.lantz.model.PreferencesModel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import se.lantz.gui.JoystickPanel;
import se.lantz.model.JoystickModel;
import javax.swing.JLabel;

public class PreferencesTabPanel extends JPanel
{
  private PreferencesModel model;
  private JTabbedPane tabbedPane;
  private MiscPanel miscPanel;
  private InfoSlotPreferencesPanel infoSlotPreferencesPanel;
  private JPanel infoSlotBackgroundPanel;
  private JPanel panel;
  private JPanel joystickBackgroundPanel;
  private JoystickPanel joystickPanel;
  
  private JoystickModel joyModel;
  private JLabel infoLabel;

  public PreferencesTabPanel()
  {
    this.model = new PreferencesModel();
    this.joyModel = new JoystickModel(false);
    setLayout(new BorderLayout(0, 0));
    add(getTabbedPane(), BorderLayout.CENTER);

    getInfoSlotPreferencesPanel().init();
  }

  private JTabbedPane getTabbedPane()
  {
    if (tabbedPane == null)
    {
      tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane.addTab("Misc.", null, getMiscPanel(), null);
      tabbedPane.addTab("Infoslot", null, getInfoSlotBackgroundPanel(), null);
      tabbedPane.addTab("Joystick", null, getJoystickBackgroundPanel(), null);
    }
    return tabbedPane;
  }

  private MiscPanel getMiscPanel()
  {
    if (miscPanel == null)
    {
      miscPanel = new MiscPanel(model);
    }
    return miscPanel;
  }

  private InfoSlotPreferencesPanel getInfoSlotPreferencesPanel()
  {
    if (infoSlotPreferencesPanel == null)
    {
      infoSlotPreferencesPanel = new InfoSlotPreferencesPanel(model);
    }
    return infoSlotPreferencesPanel;
  }

  public void savePreferences()
  {
    model.setJoystickConfig(joyModel.getConfigString());
    model.savePreferences();
  }
  
  private JPanel getInfoSlotBackgroundPanel() {
    if (infoSlotBackgroundPanel == null) {
    	infoSlotBackgroundPanel = new JPanel();
    	GridBagLayout gbl_infoSlotBackgroundPanel = new GridBagLayout();
    	infoSlotBackgroundPanel.setLayout(gbl_infoSlotBackgroundPanel);
    	GridBagConstraints gbc_panel = new GridBagConstraints();
    	gbc_panel.fill = GridBagConstraints.HORIZONTAL;
    	gbc_panel.weighty = 1.0;
    	gbc_panel.insets = new Insets(5, 5, 5, 10);
    	gbc_panel.anchor = GridBagConstraints.NORTHWEST;
    	gbc_panel.weightx = 1.0;
    	gbc_panel.gridx = 0;
    	gbc_panel.gridy = 0;
    	infoSlotBackgroundPanel.add(getInfoSlotPreferencesPanel(), gbc_panel);
    }
    return infoSlotBackgroundPanel;
  }
  private JPanel getJoystickBackgroundPanel() {
    if (joystickBackgroundPanel == null) {
    	joystickBackgroundPanel = new JPanel();
    	GridBagLayout gbl_joystickBackgroundPanel = new GridBagLayout();
    	joystickBackgroundPanel.setLayout(gbl_joystickBackgroundPanel);
    	GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    	gbc_infoLabel.fill = GridBagConstraints.HORIZONTAL;
    	gbc_infoLabel.weightx = 1.0;
    	gbc_infoLabel.insets = new Insets(10, 10, 20, 9);
    	gbc_infoLabel.gridx = 0;
    	gbc_infoLabel.gridy = 0;
    	joystickBackgroundPanel.add(getInfoLabel(), gbc_infoLabel);
    	GridBagConstraints gbc_joystickPanel = new GridBagConstraints();
    	gbc_joystickPanel.insets = new Insets(0, 5, 0, 5);
    	gbc_joystickPanel.weighty = 1.0;
    	gbc_joystickPanel.weightx = 1.0;
    	gbc_joystickPanel.gridx = 0;
    	gbc_joystickPanel.gridy = 1;
    	joystickBackgroundPanel.add(getJoystickPanel(), gbc_joystickPanel);
    }
    return joystickBackgroundPanel;
  }
  private JoystickPanel getJoystickPanel() {
    if (joystickPanel == null) {
    	joystickPanel = new JoystickPanel(2, joyModel);
    	//Don't allow mouse as default
    	joystickPanel.getMouseCheckBox().setVisible(false);
    	//Set initial value
    	joyModel.setConfigStringFromDb(model.getJoystickConfig());
    }
    return joystickPanel;
  }
  private JLabel getInfoLabel() {
    if (infoLabel == null) {
    	infoLabel = new JLabel("<html>Specify the default joystick configuration to use when adding new games. Port 1 and Port 2 uses the same mapping.</html>");
    }
    return infoLabel;
  }
}
