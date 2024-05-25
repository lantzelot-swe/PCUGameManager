package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class DosModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "Dos mode";

  public DosModeInstallManager()
  {
  }

  public void installDosMode()
  {
    readVersionFromInstallFolder(DOS_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(DOS_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, DOS_MODE_INSTALL_NAME);
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
