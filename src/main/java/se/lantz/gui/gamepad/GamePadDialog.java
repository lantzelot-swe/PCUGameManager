package se.lantz.gui.gamepad;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import se.lantz.gui.BaseDialog;
import se.lantz.model.JoystickModel;

public class GamePadDialog extends BaseDialog
{
  private JTabbedPane tabbedPane;
  private USBControllerBackgroundPanel usbControlerBackroundPanel;
  private TheGamepadBackgroundPanel theGamePadBackroundPanel;
  private NesBackgroundPanel nesBackroundPanel;
  private SpeedlinkBackgroundPanel speedlinkBackgroundPanel;
  private JoystickModel model;
  private Dimension dialogSize = new Dimension(660, 810);

  public GamePadDialog(Frame owner, JoystickModel model)
  {
    super(owner);
    //Create a separate model so that changes can be cancelled
    this.model = new JoystickModel(model.isPort1());
    setTitle("Edit USB Controller configuration");
    addContent(getTabbedPane());
    this.setPreferredSize(dialogSize);
    this.setResizable(false);
    //Set initial values to the model
    this.model.setConfigStringFromDb(model.getConfigString());
  }

  private USBControllerBackgroundPanel getUSBControllerBackgroundPanel()
  {
    if (usbControlerBackroundPanel == null)
    {
      usbControlerBackroundPanel = new USBControllerBackgroundPanel(model);
    }
    return usbControlerBackroundPanel;
  }
  
  private TheGamepadBackgroundPanel getTheGamepadBackgroundPanel()
  {
    if (theGamePadBackroundPanel == null)
    {
      theGamePadBackroundPanel = new TheGamepadBackgroundPanel(model);
    }
    return theGamePadBackroundPanel;
  }
  
  private NesBackgroundPanel getNesBackroundPanel()
  {
    if (nesBackroundPanel == null)
    {
      nesBackroundPanel = new NesBackgroundPanel(model);
    }
    return nesBackroundPanel;
  }
  
  private SpeedlinkBackgroundPanel getSpeedlinkBackroundPanel()
  {
    if (speedlinkBackgroundPanel == null)
    {
      speedlinkBackgroundPanel = new SpeedlinkBackgroundPanel(model);
    }
    return speedlinkBackgroundPanel;
  }
  
  public String getJoyConfigString()
  {
    return this.model.getConfigString();
  }
  
  private JTabbedPane getTabbedPane()
  {
    if (tabbedPane == null)
    {
      tabbedPane = new JTabbedPane();
      tabbedPane.addTab("THEGamepad", getTheGamepadBackgroundPanel());
      tabbedPane.addTab("Speedlink Comp. Pro", getSpeedlinkBackroundPanel());
      tabbedPane.addTab("NES controller", getNesBackroundPanel());
      tabbedPane.addTab("Alternative USB controller", getUSBControllerBackgroundPanel());
    }
    return tabbedPane;
  }
}
