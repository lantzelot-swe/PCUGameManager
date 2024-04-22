package se.lantz.gui.gamepad;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import se.lantz.gui.gamepad.SpeedlinkImagePanel.SpeedlinkButton;
import se.lantz.model.JoystickModel;

public class SpeedlinkMappingPanel extends JPanel
{
  private SpeedlinkMappingComponent upComponent;
  private JoystickModel model;
  private SpeedlinkMappingComponent downComponent;
  private SpeedlinkMappingComponent leftComponent;
  private SpeedlinkMappingComponent rightComponent;
  private SpeedlinkMappingComponent xComponent;
  private SpeedlinkMappingComponent aComponent;
  private SpeedlinkMappingComponent bComponent;
  private SpeedlinkImagePanel imagePanel;

  public SpeedlinkMappingPanel(JoystickModel model, SpeedlinkImagePanel imagePanel)
  {
    this.model = model;
    this.imagePanel = imagePanel;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
    gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
    setLayout(gridBagLayout);
    GridBagConstraints gbc_upComponent = new GridBagConstraints();
    gbc_upComponent.fill = GridBagConstraints.BOTH;
    gbc_upComponent.gridx = 0;
    gbc_upComponent.gridy = 0;
    add(getUpComponent(), gbc_upComponent);
    GridBagConstraints gbc_downgComponent = new GridBagConstraints();
    gbc_downgComponent.fill = GridBagConstraints.BOTH;
    gbc_downgComponent.gridx = 0;
    gbc_downgComponent.gridy = 1;
    add(getDowngComponent(), gbc_downgComponent);
    GridBagConstraints gbc_leftComponent = new GridBagConstraints();
    gbc_leftComponent.fill = GridBagConstraints.BOTH;
    gbc_leftComponent.gridx = 0;
    gbc_leftComponent.gridy = 2;
    add(getMappingComponent_2(), gbc_leftComponent);
    GridBagConstraints gbc_rightComponent = new GridBagConstraints();
    gbc_rightComponent.weighty = 1.0;
    gbc_rightComponent.fill = GridBagConstraints.BOTH;
    gbc_rightComponent.gridx = 0;
    gbc_rightComponent.gridy = 3;
    add(getRightComponent(), gbc_rightComponent);
    GridBagConstraints gbc_xComponent = new GridBagConstraints();
    gbc_xComponent.fill = GridBagConstraints.BOTH;
    gbc_xComponent.gridx = 1;
    gbc_xComponent.gridy = 2;
    add(getXComponent(), gbc_xComponent);
    GridBagConstraints gbc_aComponent = new GridBagConstraints();
    gbc_aComponent.fill = GridBagConstraints.BOTH;
    gbc_aComponent.gridx = 1;
    gbc_aComponent.gridy = 0;
    add(getAComponent(), gbc_aComponent);
    GridBagConstraints gbc_bComponent = new GridBagConstraints();
    gbc_bComponent.fill = GridBagConstraints.BOTH;
    gbc_bComponent.gridx = 1;
    gbc_bComponent.gridy = 1;
    add(getBComponent(), gbc_bComponent);
  }

  private SpeedlinkMappingComponent getUpComponent()
  {
    if (upComponent == null)
    {
      upComponent = new SpeedlinkMappingComponent(SpeedlinkButton.UP, model, imagePanel);
    }
    return upComponent;
  }

  private SpeedlinkMappingComponent getDowngComponent()
  {
    if (downComponent == null)
    {
      downComponent = new SpeedlinkMappingComponent(SpeedlinkButton.DOWN, model, imagePanel);
    }
    return downComponent;
  }

  private SpeedlinkMappingComponent getMappingComponent_2()
  {
    if (leftComponent == null)
    {
      leftComponent = new SpeedlinkMappingComponent(SpeedlinkButton.LEFT, model, imagePanel);
    }
    return leftComponent;
  }

  private SpeedlinkMappingComponent getRightComponent()
  {
    if (rightComponent == null)
    {
      rightComponent = new SpeedlinkMappingComponent(SpeedlinkButton.RIGHT, model, imagePanel);
    }
    return rightComponent;
  }

  private SpeedlinkMappingComponent getXComponent()
  {
    if (xComponent == null)
    {
      xComponent = new SpeedlinkMappingComponent(SpeedlinkButton.LT, model, imagePanel);
    }
    return xComponent;
  }

  private SpeedlinkMappingComponent getAComponent()
  {
    if (aComponent == null)
    {
      aComponent = new SpeedlinkMappingComponent(SpeedlinkButton.LEFT_FIRE, model, imagePanel);
    }
    return aComponent;
  }

  private SpeedlinkMappingComponent getBComponent()
  {
    if (bComponent == null)
    {
      bComponent = new SpeedlinkMappingComponent(SpeedlinkButton.RIGHT_FIRE, model, imagePanel);
    }
    return bComponent;
  }
}
