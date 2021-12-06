package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class ViceModeInstallManager extends BaseInstallManger
{
  private static final String PRODUCT_NAME = "Vice mode";

  public ViceModeInstallManager()
  {
  }

  public void installViceMode()
  {
    readVersionFromInstallFolder(VICE_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(VICE_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME);
    }
    else
    {
      askToInstallExistingVersion(PRODUCT_NAME);
    }
  }

  @Override
  protected void executeAfterInstallation()
  {
    JOptionPane.showMessageDialog(MainWindow.getInstance(),
                                  PRODUCT_NAME + " installed successfully.",
                                  "Installation complete",
                                  JOptionPane.INFORMATION_MESSAGE);
  }
}
