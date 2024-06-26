package se.lantz.manager.pcuae;

import java.io.File;

import javax.swing.JOptionPane;

import se.lantz.gui.MainWindow;
import se.lantz.gui.menu.InsetsMenuItem;

public class PCUAEInstallManager extends BaseInstallManager
{
  private static final String PRODUCT_NAME = "PCUAE";

  private InsetsMenuItem exportMenuItem;

  public PCUAEInstallManager(InsetsMenuItem exportMenuItem)
  {
    this.exportMenuItem = exportMenuItem;
  }

  public void checkForNewVersionAtStartup()
  {
    readVersionFromInstallFolder(PCUAE_INSTALL_NAME);
    if (isNewVersionAvailable(PCUAE_INSTALL_NAME))
    {
      askAndStartDownloadAtStartup(PRODUCT_NAME, PCUAE_INSTALL_NAME);
    }
  }

  public void installPCUAE()
  {
    readVersionFromInstallFolder(PCUAE_INSTALL_NAME);
    if (isNewVersionAvailable(PCUAE_INSTALL_NAME))
    {
      askAndStartDownload(PRODUCT_NAME, PCUAE_INSTALL_NAME);
    }
    else
    {
      askToInstallExistingVersion(PRODUCT_NAME);
    }
  }

  @Override
  protected void executeAfterInstallation()
  {
    int value =
      JOptionPane.showConfirmDialog(MainWindow.getInstance(),
                                    PRODUCT_NAME + " installed successfully.\nDo you want to export games now?",
                                    "Installation complete",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE);
    if (value == JOptionPane.YES_OPTION)
    {
      exportMenuItem.doClick();
    }
  }

  public void deleteAllInstallFiles()
  {
    File installFolder = new File(INSTALL_FOLDER);
    File[] availableFiles = installFolder.listFiles();
    for (File file : availableFiles)
    {
      file.delete();
    }
  }
}
