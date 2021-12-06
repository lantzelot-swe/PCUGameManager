package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class AmigaModeInstallManager extends BaseInstallManger
{
  private static final String PRODUCT_NAME = "Amiga mode";

  public AmigaModeInstallManager()
  {
  }

  public void installAmigaMode()
  {
    readVersionFromInstallFolder(AMIGA_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(AMIGA_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME);
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
