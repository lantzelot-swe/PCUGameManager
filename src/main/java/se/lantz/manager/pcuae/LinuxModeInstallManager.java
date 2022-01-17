package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class LinuxModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "Linux mode";

  public LinuxModeInstallManager()
  {
  }

  public void installLinuxMode()
  {
    readVersionFromInstallFolder(LINUX_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(LINUX_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, LINUX_MODE_INSTALL_NAME);
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
