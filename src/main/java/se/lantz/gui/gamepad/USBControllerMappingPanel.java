package se.lantz.gui.gamepad;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import se.lantz.gui.gamepad.USBControllerImagePanel.USBControllerButton;
import se.lantz.model.JoystickModel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class USBControllerMappingPanel extends JPanel
{
  private USBControllerMappingComponent upComponent;
  private JoystickModel model;
  private USBControllerMappingComponent downComponent;
  private USBControllerMappingComponent leftComponent;
  private USBControllerMappingComponent rightComponent;
  private USBControllerMappingComponent xComponent;
  private USBControllerMappingComponent yComponent;
  private USBControllerMappingComponent aComponent;
  private USBControllerMappingComponent bComponent;
  private USBControllerMappingComponent leftTriggerComponent;
  private USBControllerMappingComponent rightTriggerComponent;
  private USBControllerMappingComponent backGuideComponent;
  private USBControllerMappingComponent leftShoulderComponent;
  private USBControllerMappingComponent rightShoulderComponent;
  private USBControllerMappingComponent leftStickComponent;
  private USBControllerMappingComponent rightStickComponent;
  private USBControllerImagePanel imagePanel;

  public USBControllerMappingPanel(JoystickModel model, USBControllerImagePanel imagePanel)
  {
    this.model = model;
    this.imagePanel = imagePanel;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    gridBagLayout.columnWeights = new double[]{1.0, 1.0};
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
    gbc_rightComponent.fill = GridBagConstraints.BOTH;
    gbc_rightComponent.gridx = 0;
    gbc_rightComponent.gridy = 3;
    add(getRightComponent(), gbc_rightComponent);
    GridBagConstraints gbc_xComponent = new GridBagConstraints();
    gbc_xComponent.fill = GridBagConstraints.BOTH;
    gbc_xComponent.gridx = 1;
    gbc_xComponent.gridy = 2;
    add(getXComponent(), gbc_xComponent);
    GridBagConstraints gbc_yComponent = new GridBagConstraints();
    gbc_yComponent.fill = GridBagConstraints.BOTH;
    gbc_yComponent.gridx = 1;
    gbc_yComponent.gridy = 3;
    add(getYComponent(), gbc_yComponent);
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
    GridBagConstraints gbc_leftTriggerComponent = new GridBagConstraints();
    gbc_leftTriggerComponent.fill = GridBagConstraints.BOTH;
    gbc_leftTriggerComponent.gridx = 0;
    gbc_leftTriggerComponent.gridy = 4;
    add(getLeftTriggerComponent(), gbc_leftTriggerComponent);
    GridBagConstraints gbc_rightTriggerComponent = new GridBagConstraints();
    gbc_rightTriggerComponent.fill = GridBagConstraints.BOTH;
    gbc_rightTriggerComponent.gridx = 1;
    gbc_rightTriggerComponent.gridy = 4;
    add(getRightTriggerComponent(), gbc_rightTriggerComponent);
    GridBagConstraints gbc_backGuideComponent = new GridBagConstraints();
    gbc_backGuideComponent.fill = GridBagConstraints.BOTH;
    gbc_backGuideComponent.gridx = 0;
    gbc_backGuideComponent.gridy = 5;
    add(getBackGuideComponent(), gbc_backGuideComponent);
    GridBagConstraints gbc_leftStickComponent = new GridBagConstraints();
    gbc_leftStickComponent.anchor = GridBagConstraints.NORTHWEST;
    gbc_leftStickComponent.fill = GridBagConstraints.HORIZONTAL;
    gbc_leftStickComponent.gridx = 0;
    gbc_leftStickComponent.gridy = 7;
    add(getLeftStickComponent(), gbc_leftStickComponent);
    GridBagConstraints gbc_leftShoulderComponent = new GridBagConstraints();
    gbc_leftShoulderComponent.fill = GridBagConstraints.BOTH;
    gbc_leftShoulderComponent.gridx = 0;
    gbc_leftShoulderComponent.gridy = 6;
    add(getLeftShoulderComponent(), gbc_leftShoulderComponent);
    GridBagConstraints gbc_rightShoulderComponent = new GridBagConstraints();
    gbc_rightShoulderComponent.fill = GridBagConstraints.BOTH;
    gbc_rightShoulderComponent.gridx = 1;
    gbc_rightShoulderComponent.gridy = 6;
    add(getRightShoulderComponent(), gbc_rightShoulderComponent);
    GridBagConstraints gbc_rightStickComponent = new GridBagConstraints();
    gbc_rightStickComponent.anchor = GridBagConstraints.NORTHWEST;
    gbc_rightStickComponent.fill = GridBagConstraints.HORIZONTAL;
    gbc_rightStickComponent.gridx = 1;
    gbc_rightStickComponent.gridy = 7;
    add(getRightStickComponent(), gbc_rightStickComponent);
  }
  private USBControllerMappingComponent getUpComponent() {
    if (upComponent == null) {
    	upComponent = new USBControllerMappingComponent(USBControllerButton.UP, model, imagePanel);
    }
    return upComponent;
  }
  private USBControllerMappingComponent getDowngComponent() {
    if (downComponent == null) {
    	downComponent = new USBControllerMappingComponent(USBControllerButton.DOWN, model, imagePanel);
    }
    return downComponent;
  }
  private USBControllerMappingComponent getMappingComponent_2() {
    if (leftComponent == null) {
    	leftComponent = new USBControllerMappingComponent(USBControllerButton.LEFT, model, imagePanel);
    }
    return leftComponent;
  }
  private USBControllerMappingComponent getRightComponent() {
    if (rightComponent == null) {
    	rightComponent = new USBControllerMappingComponent(USBControllerButton.RIGHT, model, imagePanel);
    }
    return rightComponent;
  }
  private USBControllerMappingComponent getXComponent() {
    if (xComponent == null) {
    	xComponent = new USBControllerMappingComponent(USBControllerButton.X, model, imagePanel);
    }
    return xComponent;
  }
  private USBControllerMappingComponent getYComponent() {
    if (yComponent == null) {
    	yComponent = new USBControllerMappingComponent(USBControllerButton.Y, model, imagePanel);
    }
    return yComponent;
  }
  private USBControllerMappingComponent getAComponent() {
    if (aComponent == null) {
    	aComponent = new USBControllerMappingComponent(USBControllerButton.A, model, imagePanel);
    }
    return aComponent;
  }
  private USBControllerMappingComponent getBComponent() {
    if (bComponent == null) {
    	bComponent = new USBControllerMappingComponent(USBControllerButton.B, model, imagePanel);
    }
    return bComponent;
  }
  private USBControllerMappingComponent getLeftTriggerComponent() {
    if (leftTriggerComponent == null) {
    	leftTriggerComponent = new USBControllerMappingComponent(USBControllerButton.LEFT_TRIGGER, model, imagePanel);
    }
    return leftTriggerComponent;
  }
  private USBControllerMappingComponent getRightTriggerComponent() {
    if (rightTriggerComponent == null) {
    	rightTriggerComponent = new USBControllerMappingComponent(USBControllerButton.RIGHT_TRIGGER, model, imagePanel);
    }
    return rightTriggerComponent;
  }
  private USBControllerMappingComponent getBackGuideComponent() {
    if (backGuideComponent == null) {
    	backGuideComponent = new USBControllerMappingComponent(USBControllerButton.BACK_GUIDE, model, imagePanel);
    }
    return backGuideComponent;
  }
  private USBControllerMappingComponent getLeftShoulderComponent() {
    if (leftShoulderComponent == null) {
    	leftShoulderComponent = new USBControllerMappingComponent(USBControllerButton.LEFT_SHOULDER, model, imagePanel);
    }
    return leftShoulderComponent;
  }
  private USBControllerMappingComponent getRightShoulderComponent() {
    if (rightShoulderComponent == null) {
    	rightShoulderComponent = new USBControllerMappingComponent(USBControllerButton.RIGHT_SHOULDER, model, imagePanel);
    }
    return rightShoulderComponent;
  }
  private USBControllerMappingComponent getLeftStickComponent() {
    if (leftStickComponent == null) {
    	leftStickComponent = new USBControllerMappingComponent(USBControllerButton.LEFT_STICK, model, imagePanel);
    }
    return leftStickComponent;
  }
  private USBControllerMappingComponent getRightStickComponent() {
    if (rightStickComponent == null) {
    	rightStickComponent = new USBControllerMappingComponent(USBControllerButton.RIGHT_STICK, model, imagePanel);
    }
    return rightStickComponent;
  }
}
