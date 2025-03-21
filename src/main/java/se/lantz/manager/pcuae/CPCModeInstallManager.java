package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class CPCModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "CPC mode";

  public CPCModeInstallManager()
  {
  }

  public void installCPCMode()
  {
    readVersionFromInstallFolder(CPC_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(CPC_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, CPC_MODE_INSTALL_NAME);
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
