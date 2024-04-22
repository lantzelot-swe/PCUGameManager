package se.lantz.gui.gamepad;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.Beans;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import se.lantz.gui.KeySelectionComboBox;
import se.lantz.gui.gamepad.SpeedlinkImagePanel.SpeedlinkButton;
import se.lantz.model.JoystickModel;

public class SpeedlinkMappingComponent extends JPanel
{
  private JLabel buttonTextLabel;
  private KeySelectionComboBox keySelectionComboBox;
  private JLabel joyMapLabel;
  private SpeedlinkButton button;
  private JoystickModel model;
  private SpeedlinkImagePanel imagePanel;

  public SpeedlinkMappingComponent(SpeedlinkButton button, JoystickModel model, SpeedlinkImagePanel imagePanel)
  {
    this.button = button;
    this.model = model;
    this.imagePanel = imagePanel;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_buttonTextLabel = new GridBagConstraints();
    gbc_buttonTextLabel.anchor = GridBagConstraints.WEST;
    gbc_buttonTextLabel.insets = new Insets(5, 5, 0, 5);
    gbc_buttonTextLabel.gridx = 0;
    gbc_buttonTextLabel.gridy = 0;
    add(getButtonTextLabel(), gbc_buttonTextLabel);
    GridBagConstraints gbc_keySelectionComboBox = new GridBagConstraints();
    gbc_keySelectionComboBox.weighty = 1.0;
    gbc_keySelectionComboBox.gridwidth = 2;
    gbc_keySelectionComboBox.insets = new Insets(0, 4, 5, 5);
    gbc_keySelectionComboBox.anchor = GridBagConstraints.NORTHWEST;
    gbc_keySelectionComboBox.gridx = 0;
    gbc_keySelectionComboBox.gridy = 1;
    add(getKeySelectionComboBox(), gbc_keySelectionComboBox);
    GridBagConstraints gbc_joyMapLabel = new GridBagConstraints();
    gbc_joyMapLabel.anchor = GridBagConstraints.WEST;
    gbc_joyMapLabel.weightx = 1.0;
    gbc_joyMapLabel.insets = new Insets(5, 0, 0, 5);
    gbc_joyMapLabel.gridx = 1;
    gbc_joyMapLabel.gridy = 0;
    add(getJoyMapLabel(), gbc_joyMapLabel);

    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener((e) -> modelChanged());
    }
  }

  private void modelChanged()
  {
    switch (button)
    {
    case UP:
      getKeySelectionComboBox().setSelectedCode(model.getUp());
      break;
    case DOWN:
      getKeySelectionComboBox().setSelectedCode(model.getDown());
      break;
    case LEFT:
      getKeySelectionComboBox().setSelectedCode(model.getLeft());
      break;
    case RIGHT:
      getKeySelectionComboBox().setSelectedCode(model.getRight());
      break;
    case LEFT_FIRE:
      getKeySelectionComboBox().setSelectedCode(model.getLeftFire());
      break;
    case RIGHT_FIRE:
      getKeySelectionComboBox().setSelectedCode(model.getA());
      break;
    case LT:
      getKeySelectionComboBox().setSelectedCode(model.getB());
      break;
    default:
      break;
    }
  }

  private JLabel getButtonTextLabel()
  {
    if (buttonTextLabel == null)
    {
      buttonTextLabel = new JLabel(button.label);
    }
    return buttonTextLabel;
  }

  private KeySelectionComboBox getKeySelectionComboBox()
  {
    if (keySelectionComboBox == null)
    {
      keySelectionComboBox = new KeySelectionComboBox(model);
      keySelectionComboBox.addActionListener(e -> {
        switch (button)
        {
        case UP:
          model.setUp(keySelectionComboBox.getSelectedCode());
          break;
        case DOWN:
          model.setDown(keySelectionComboBox.getSelectedCode());
          break;
        case LEFT:
          model.setLeft(keySelectionComboBox.getSelectedCode());
          break;
        case RIGHT:
          model.setRight(keySelectionComboBox.getSelectedCode());
          break;
        case RIGHT_FIRE:
          model.setA(keySelectionComboBox.getSelectedCode());
          break;
        case LEFT_FIRE:
          model.setLeftFire(keySelectionComboBox.getSelectedCode());
          break;
        case LT:
          model.setB(keySelectionComboBox.getSelectedCode());
          break;

        default:
          break;
        }
      });
      keySelectionComboBox.addMouseListener(new MouseAdapter()
        {
          @Override
          public void mouseExited(MouseEvent e)
          {
            if (!keySelectionComboBox.hasFocus())
            {
              Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
              if (focusOwner instanceof KeySelectionComboBox)
              {
                SpeedlinkMappingComponent focusedMappingComponent =
                  (SpeedlinkMappingComponent) SwingUtilities.getAncestorOfClass(SpeedlinkMappingComponent.class, focusOwner);
                if (focusedMappingComponent != null)
                {
                  imagePanel.setCurrentButtonAndRepaint(focusedMappingComponent.button);
                }
              }
              else
              {
                imagePanel.setCurrentButtonAndRepaint(null);
              }
            }
          }

          @Override
          public void mouseEntered(MouseEvent me)
          {
            imagePanel.setCurrentButtonAndRepaint(button);
          }
        });
      keySelectionComboBox.addFocusListener(new FocusAdapter()
        {
          @Override
          public void focusGained(FocusEvent e)
          {
            imagePanel.setCurrentButtonAndRepaint(button);
          }

          @Override
          public void focusLost(FocusEvent e)
          {
            imagePanel.setCurrentButtonAndRepaint(null);
          }
        });
    }
    return keySelectionComboBox;
  }

  private JLabel getJoyMapLabel()
  {
    if (joyMapLabel == null)
    {
      joyMapLabel = new JLabel("(" + button.joyMapping + ")");
    }
    return joyMapLabel;
  }

}
