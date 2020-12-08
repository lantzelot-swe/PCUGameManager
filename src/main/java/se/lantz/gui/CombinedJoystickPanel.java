package se.lantz.gui;

import javax.swing.JPanel;

import se.lantz.model.MainViewModel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.Beans;

import javax.swing.border.TitledBorder;

public class CombinedJoystickPanel extends JPanel
{
  private JoystickPanel port1Panel;
  private JoystickPanel port2Panel;
  private MainViewModel model;

  public CombinedJoystickPanel(MainViewModel model)
  {
    setBorder(new TitledBorder(null, "Joystick configuration", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    this.model = model;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_port1Panel = new GridBagConstraints();
    gbc_port1Panel.weighty = 1.0;
    gbc_port1Panel.anchor = GridBagConstraints.NORTHWEST;
    gbc_port1Panel.insets = new Insets(0, 0, 0, 5);
    gbc_port1Panel.gridx = 0;
    gbc_port1Panel.gridy = 0;
    add(getPort1Panel(), gbc_port1Panel);
    GridBagConstraints gbc_port2Panel = new GridBagConstraints();
    gbc_port2Panel.weighty = 1.0;
    gbc_port2Panel.weightx = 1.0;
    gbc_port2Panel.anchor = GridBagConstraints.NORTHWEST;
    gbc_port2Panel.gridx = 1;
    gbc_port2Panel.gridy = 0;
    add(getPort2Panel(), gbc_port2Panel);
  }

  private JoystickPanel getPort1Panel()
  {
    if (port1Panel == null)
    {
      if (!Beans.isDesignTime())
      {
        port1Panel = new JoystickPanel(1, model.getJoy1Model());
      }
      else
      {
        port1Panel = new JoystickPanel(1, null);
      }
    }
    return port1Panel;
  }

  private JoystickPanel getPort2Panel()
  {
    if (port2Panel == null)
    {
      if (!Beans.isDesignTime())
      {
        port2Panel = new JoystickPanel(2, model.getJoy2Model());
      }
      else
      {
        port2Panel = new JoystickPanel(2, null);
      }
    }
    return port2Panel;
  }
}
