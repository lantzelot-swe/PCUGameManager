package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class PlaystationModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "Playstation mode";

  public PlaystationModeInstallManager()
  {
  }

  public void installPlaystationMode()
  {
    readVersionFromInstallFolder(PLAYSTATION_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(PLAYSTATION_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, PLAYSTATION_MODE_INSTALL_NAME);
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
