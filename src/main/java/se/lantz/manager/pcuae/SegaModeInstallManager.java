package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class SegaModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "Sega mode";

  public SegaModeInstallManager()
  {
  }

  public void installSegaMode()
  {
    readVersionFromInstallFolder(SEGA_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(SEGA_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, SEGA_MODE_INSTALL_NAME);
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
