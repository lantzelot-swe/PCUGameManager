package se.lantz.gui.gamepad;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import se.lantz.gui.gamepad.GamePadImagePanel.GamePadButton;
import se.lantz.model.JoystickModel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GamePadMappingPanel extends JPanel
{
  private MappingComponent upComponent;
  private JoystickModel model;
  private MappingComponent downComponent;
  private MappingComponent leftComponent;
  private MappingComponent rightComponent;
  private MappingComponent xComponent;
  private MappingComponent yComponent;
  private MappingComponent aComponent;
  private MappingComponent bComponent;
  private MappingComponent leftTriggerComponent;
  private MappingComponent rightTriggerComponent;
  private MappingComponent backGuideComponent;
  private MappingComponent leftShoulderComponent;
  private MappingComponent rightShoulderComponent;
  private MappingComponent leftStickComponent;
  private MappingComponent rightStickComponent;
  private GamePadImagePanel imagePanel;

  public GamePadMappingPanel(JoystickModel model, GamePadImagePanel imagePanel)
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
  private MappingComponent getUpComponent() {
    if (upComponent == null) {
    	upComponent = new MappingComponent(GamePadButton.UP, model, imagePanel);
    }
    return upComponent;
  }
  private MappingComponent getDowngComponent() {
    if (downComponent == null) {
    	downComponent = new MappingComponent(GamePadButton.DOWN, model, imagePanel);
    }
    return downComponent;
  }
  private MappingComponent getMappingComponent_2() {
    if (leftComponent == null) {
    	leftComponent = new MappingComponent(GamePadButton.LEFT, model, imagePanel);
    }
    return leftComponent;
  }
  private MappingComponent getRightComponent() {
    if (rightComponent == null) {
    	rightComponent = new MappingComponent(GamePadButton.RIGHT, model, imagePanel);
    }
    return rightComponent;
  }
  private MappingComponent getXComponent() {
    if (xComponent == null) {
    	xComponent = new MappingComponent(GamePadButton.X, model, imagePanel);
    }
    return xComponent;
  }
  private MappingComponent getYComponent() {
    if (yComponent == null) {
    	yComponent = new MappingComponent(GamePadButton.Y, model, imagePanel);
    }
    return yComponent;
  }
  private MappingComponent getAComponent() {
    if (aComponent == null) {
    	aComponent = new MappingComponent(GamePadButton.A, model, imagePanel);
    }
    return aComponent;
  }
  private MappingComponent getBComponent() {
    if (bComponent == null) {
    	bComponent = new MappingComponent(GamePadButton.B, model, imagePanel);
    }
    return bComponent;
  }
  private MappingComponent getLeftTriggerComponent() {
    if (leftTriggerComponent == null) {
    	leftTriggerComponent = new MappingComponent(GamePadButton.LEFT_TRIGGER, model, imagePanel);
    }
    return leftTriggerComponent;
  }
  private MappingComponent getRightTriggerComponent() {
    if (rightTriggerComponent == null) {
    	rightTriggerComponent = new MappingComponent(GamePadButton.RIGHT_TRIGGER, model, imagePanel);
    }
    return rightTriggerComponent;
  }
  private MappingComponent getBackGuideComponent() {
    if (backGuideComponent == null) {
    	backGuideComponent = new MappingComponent(GamePadButton.BACK_GUIDE, model, imagePanel);
    }
    return backGuideComponent;
  }
  private MappingComponent getLeftShoulderComponent() {
    if (leftShoulderComponent == null) {
    	leftShoulderComponent = new MappingComponent(GamePadButton.LEFT_SHOULDER, model, imagePanel);
    }
    return leftShoulderComponent;
  }
  private MappingComponent getRightShoulderComponent() {
    if (rightShoulderComponent == null) {
    	rightShoulderComponent = new MappingComponent(GamePadButton.RIGHT_SHOULDER, model, imagePanel);
    }
    return rightShoulderComponent;
  }
  private MappingComponent getLeftStickComponent() {
    if (leftStickComponent == null) {
    	leftStickComponent = new MappingComponent(GamePadButton.LEFT_STICK, model, imagePanel);
    }
    return leftStickComponent;
  }
  private MappingComponent getRightStickComponent() {
    if (rightStickComponent == null) {
    	rightStickComponent = new MappingComponent(GamePadButton.RIGHT_STICK, model, imagePanel);
    }
    return rightStickComponent;
  }
}
