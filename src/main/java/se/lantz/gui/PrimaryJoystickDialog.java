package se.lantz.gui;

import java.awt.Dimension;
import java.awt.Frame;

public class PrimaryJoystickDialog extends BaseDialog
{

  private Dimension dialogSize = new Dimension(350, 170);
  private PrimaryJoystickSelectionPanel portPanel;

  public PrimaryJoystickDialog(Frame owner)
  {
    super(owner);
    //Create a separate model so that changes can be cancelled

    setTitle("Edit primary port");
    addContent(getPrimaryPortPanel());
    this.setPreferredSize(dialogSize);
    this.setResizable(false);
  }

  private PrimaryJoystickSelectionPanel getPrimaryPortPanel()
  {
    if (portPanel == null)
    {
      portPanel = new PrimaryJoystickSelectionPanel();
    }
    return portPanel;
  }

  public boolean isPort1Primary()
  {
    return getPrimaryPortPanel().isPort1Primary();
  }
}
