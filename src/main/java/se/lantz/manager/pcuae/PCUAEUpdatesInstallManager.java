package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class PCUAEUpdatesInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "PCUAE Updates";
  private static final String updatedGitRepo = "https://Wizart009@api.github.com/repos/Wizart009/PCUAE-Updates/releases";

  public PCUAEUpdatesInstallManager()
  {
  }

  public void installPCUAEUpdates()
  {
    readVersionFromInstallFolder(PCUAE_UPDATE_INSTALL_NAME);
    if (isNewVersionAvailable(PCUAE_UPDATE_INSTALL_NAME, updatedGitRepo))
    {
      askAndStartDownload(PRODUCT_NAME, PCUAE_UPDATE_INSTALL_NAME, true);
    }
    else
    {
      askToInstallExistingVersion(PRODUCT_NAME, true);
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
