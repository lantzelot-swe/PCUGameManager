package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class ZesaruxModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "Zesarux mode";

  public ZesaruxModeInstallManager()
  {
  }

  public void installZesaruxMode()
  {
    readVersionFromInstallFolder(ZESARUX_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(ZESARUX_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, ZESARUX_MODE_INSTALL_NAME);
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
