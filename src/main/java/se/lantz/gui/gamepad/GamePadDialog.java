package se.lantz.gui.gamepad;

import java.awt.Dimension;
import java.awt.Frame;

import se.lantz.gui.BaseDialog;
import se.lantz.model.JoystickModel;

public class GamePadDialog extends BaseDialog
{
  private GamepadBackgroundPanel panel;
  private JoystickModel model;
  private Dimension dialogSize = new Dimension(660, 790);

  public GamePadDialog(Frame owner, JoystickModel model)
  {
    super(owner);
    //Create a separate model so that changes can be cancelled
    this.model = new JoystickModel(model.isPort1());
    setTitle("Edit joystick/gamepad configuration");
    addContent(getGamepadBackgroundPanel());
    this.setPreferredSize(dialogSize);
    this.setResizable(false);
    //Set initial values to the model
    this.model.setConfigStringFromDb(model.getConfigString());
  }

  private GamepadBackgroundPanel getGamepadBackgroundPanel()
  {
    if (panel == null)
    {
      panel = new GamepadBackgroundPanel(model);
    }
    return panel;
  }

  public String getJoyConfigString()
  {
    return this.model.getConfigString();
  }
}