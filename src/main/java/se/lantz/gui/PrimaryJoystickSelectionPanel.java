package se.lantz.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class PrimaryJoystickSelectionPanel extends JPanel
{
  private JLabel infoLabel;
  private JRadioButton port1RadioButton;
  private JRadioButton port2RadioButton;
  private final ButtonGroup buttonGroup = new ButtonGroup();

  public PrimaryJoystickSelectionPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.insets = new Insets(15, 5, 10, 0);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_port1RadioButton = new GridBagConstraints();
    gbc_port1RadioButton.gridx = 0;
    gbc_port1RadioButton.gridy = 1;
    add(getPort1RadioButton(), gbc_port1RadioButton);
    GridBagConstraints gbc_port2RadioButton = new GridBagConstraints();
    gbc_port2RadioButton.anchor = GridBagConstraints.NORTH;
    gbc_port2RadioButton.weighty = 1.0;
    gbc_port2RadioButton.gridx = 0;
    gbc_port2RadioButton.gridy = 2;
    add(getPort2RadioButton(), gbc_port2RadioButton);

  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel = new JLabel("Select which port to set as primary for the selected games:");
    }
    return infoLabel;
  }

  private JRadioButton getPort1RadioButton()
  {
    if (port1RadioButton == null)
    {
      port1RadioButton = new JRadioButton("Port 1");
      buttonGroup.add(port1RadioButton);
    }
    return port1RadioButton;
  }

  private JRadioButton getPort2RadioButton()
  {
    if (port2RadioButton == null)
    {
      port2RadioButton = new JRadioButton("Port 2");
      port2RadioButton.setSelected(true);
      buttonGroup.add(port2RadioButton);
    }
    return port2RadioButton;
  }
  
  public boolean isPort1Primary()
  {
    return getPort1RadioButton().isSelected();
  }
}
