package se.lantz.gui.gamepad;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import se.lantz.gui.gamepad.TheGamepadImagePanel.TheGamepadButton;
import se.lantz.gui.gamepad.USBControllerImagePanel.USBControllerButton;
import se.lantz.model.JoystickModel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class TheGamepadMappingPanel extends JPanel
{
  private TheGamepadMappingComponent upComponent;
  private JoystickModel model;
  private TheGamepadMappingComponent downComponent;
  private TheGamepadMappingComponent leftComponent;
  private TheGamepadMappingComponent rightComponent;
  private TheGamepadMappingComponent xComponent;
  private TheGamepadMappingComponent yComponent;
  private TheGamepadMappingComponent aComponent;
  private TheGamepadMappingComponent bComponent;
  private TheGamepadMappingComponent leftTriggerComponent;
  private TheGamepadMappingComponent rightTriggerComponent;
  private TheGamepadMappingComponent backGuideComponent;
  private TheGamepadMappingComponent leftShoulderComponent;
  private TheGamepadMappingComponent rightShoulderComponent;
  private TheGamepadMappingComponent leftStickComponent;
  private TheGamepadMappingComponent rightStickComponent;
  private TheGamepadImagePanel imagePanel;

  public TheGamepadMappingPanel(JoystickModel model, TheGamepadImagePanel imagePanel)
  {
    this.model = model;
    this.imagePanel = imagePanel;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
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
  }
  private TheGamepadMappingComponent getUpComponent() {
    if (upComponent == null) {
    	upComponent = new TheGamepadMappingComponent(TheGamepadButton.UP, model, imagePanel);
    }
    return upComponent;
  }
  private TheGamepadMappingComponent getDowngComponent() {
    if (downComponent == null) {
    	downComponent = new TheGamepadMappingComponent(TheGamepadButton.DOWN, model, imagePanel);
    }
    return downComponent;
  }
  private TheGamepadMappingComponent getMappingComponent_2() {
    if (leftComponent == null) {
    	leftComponent = new TheGamepadMappingComponent(TheGamepadButton.LEFT, model, imagePanel);
    }
    return leftComponent;
  }
  private TheGamepadMappingComponent getRightComponent() {
    if (rightComponent == null) {
    	rightComponent = new TheGamepadMappingComponent(TheGamepadButton.RIGHT, model, imagePanel);
    }
    return rightComponent;
  }
  private TheGamepadMappingComponent getXComponent() {
    if (xComponent == null) {
    	xComponent = new TheGamepadMappingComponent(TheGamepadButton.X, model, imagePanel);
    }
    return xComponent;
  }
  private TheGamepadMappingComponent getYComponent() {
    if (yComponent == null) {
    	yComponent = new TheGamepadMappingComponent(TheGamepadButton.Y, model, imagePanel);
    }
    return yComponent;
  }
  private TheGamepadMappingComponent getAComponent() {
    if (aComponent == null) {
    	aComponent = new TheGamepadMappingComponent(TheGamepadButton.A, model, imagePanel);
    }
    return aComponent;
  }
  private TheGamepadMappingComponent getBComponent() {
    if (bComponent == null) {
    	bComponent = new TheGamepadMappingComponent(TheGamepadButton.B, model, imagePanel);
    }
    return bComponent;
  }
  private TheGamepadMappingComponent getLeftTriggerComponent() {
    if (leftTriggerComponent == null) {
    	leftTriggerComponent = new TheGamepadMappingComponent(TheGamepadButton.LSB, model, imagePanel);
    }
    return leftTriggerComponent;
  }
  private TheGamepadMappingComponent getRightTriggerComponent() {
    if (rightTriggerComponent == null) {
    	rightTriggerComponent = new TheGamepadMappingComponent(TheGamepadButton.RSB, model, imagePanel);
    }
    return rightTriggerComponent;
  }
  private TheGamepadMappingComponent getBackGuideComponent() {
    if (backGuideComponent == null) {
    	backGuideComponent = new TheGamepadMappingComponent(TheGamepadButton.MENU, model, imagePanel);
    }
    return backGuideComponent;
  }
}
