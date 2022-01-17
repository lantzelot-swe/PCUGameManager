package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class RetroarchModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "Retroarch mode";

  public RetroarchModeInstallManager()
  {
  }

  public void installRetroarchMode()
  {
    readVersionFromInstallFolder(RETROARCH_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(RETROARCH_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, RETROARCH_MODE_INSTALL_NAME);
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
