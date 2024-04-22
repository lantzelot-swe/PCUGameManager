package se.lantz.gui.gamepad;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import se.lantz.gui.gamepad.NesImagePanel.NesButton;
import se.lantz.model.JoystickModel;

public class NesMappingPanel extends JPanel
{
  private NesMappingComponent upComponent;
  private JoystickModel model;
  private NesMappingComponent downComponent;
  private NesMappingComponent leftComponent;
  private NesMappingComponent rightComponent;
  private NesMappingComponent xComponent;
  private NesMappingComponent aComponent;
  private NesMappingComponent bComponent;
  private NesImagePanel imagePanel;

  public NesMappingPanel(JoystickModel model, NesImagePanel imagePanel)
  {
    this.model = model;
    this.imagePanel = imagePanel;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
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
  private NesMappingComponent getUpComponent() {
    if (upComponent == null) {
    	upComponent = new NesMappingComponent(NesButton.UP, model, imagePanel);
    }
    return upComponent;
  }
  private NesMappingComponent getDowngComponent() {
    if (downComponent == null) {
    	downComponent = new NesMappingComponent(NesButton.DOWN, model, imagePanel);
    }
    return downComponent;
  }
  private NesMappingComponent getMappingComponent_2() {
    if (leftComponent == null) {
    	leftComponent = new NesMappingComponent(NesButton.LEFT, model, imagePanel);
    }
    return leftComponent;
  }
  private NesMappingComponent getRightComponent() {
    if (rightComponent == null) {
    	rightComponent = new NesMappingComponent(NesButton.RIGHT, model, imagePanel);
    }
    return rightComponent;
  }
  private NesMappingComponent getXComponent() {
    if (xComponent == null) {
    	xComponent = new NesMappingComponent(NesButton.START, model, imagePanel);
    }
    return xComponent;
  }
  private NesMappingComponent getAComponent() {
    if (aComponent == null) {
    	aComponent = new NesMappingComponent(NesButton.A, model, imagePanel);
    }
    return aComponent;
  }
  private NesMappingComponent getBComponent() {
    if (bComponent == null) {
    	bComponent = new NesMappingComponent(NesButton.B, model, imagePanel);
    }
    return bComponent;
  }
}
