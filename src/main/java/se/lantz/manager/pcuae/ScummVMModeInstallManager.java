package se.lantz.manager.pcuae;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;

public class ScummVMModeInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "ScummVM mode";

  public ScummVMModeInstallManager()
  {
  }

  public void installScummVMMode()
  {
    readVersionFromInstallFolder(SCUMMVM_MODE_INSTALL_NAME);
    if (isNewVersionAvailable(SCUMMVM_MODE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, SCUMMVM_MODE_INSTALL_NAME);
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
