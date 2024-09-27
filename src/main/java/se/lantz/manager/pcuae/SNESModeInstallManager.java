package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class SNESModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "SNES mode";

  public SNESModeInstallManager()
  {
  }

  public void installSnesMode()
  {
    readVersionFromInstallFolder(SNES_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(SNES_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, SNES_MODE_INSTALL_NAME);
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
