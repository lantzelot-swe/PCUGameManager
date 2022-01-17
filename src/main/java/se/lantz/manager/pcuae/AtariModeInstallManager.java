package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class AtariModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "Atari mode";

  public AtariModeInstallManager()
  {
  }

  public void installAtariMode()
  {
    readVersionFromInstallFolder(ATARI_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(ATARI_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, ATARI_MODE_INSTALL_NAME);
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
