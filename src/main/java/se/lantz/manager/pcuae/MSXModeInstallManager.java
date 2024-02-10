package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class MSXModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "MSX/Colecovison mode";

  public MSXModeInstallManager()
  {
  }

  public void installMSXMode()
  {
    readVersionFromInstallFolder(MSX_COLECO_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(MSX_COLECO_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, MSX_COLECO_MODE_INSTALL_NAME);
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
