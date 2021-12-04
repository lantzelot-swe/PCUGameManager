package se.lantz.manager.pcuae;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import se.lantz.gui.MainWindow;
import se.lantz.gui.download.DownloadDialog;
import se.lantz.gui.install.PCUAEVersionDownloadDialog;
import se.lantz.util.ManagerVersionChecker;

public class PCUAEInstallManager extends BaseInstallManger
{
  public static final String INSTALL_FOLDER = "./pcuae-install/";

  private static final String PCUAE_INSTALL_NAME = "pcuae";

//  private static final String AMIGA_MODE_INSTALL_NAME = "amiga";
//  private static final String ATARI_MODE_INSTALL_NAME = "atari";
//  private static final String LINUX_MODE_INSTALL_NAME = "linux";
//  private static final String RETROARCH_MODE_INSTALL_NAME = "retroarch";
//  private static final String VICE_MODE_INSTALL_NAME = "vice";

  private static String pcuaeLatestInInstallFolder = "";

  private static GithubAssetInformation gitHubReleaseInformation = new GithubAssetInformation();

  private JMenuItem exportMenuItem;

  public PCUAEInstallManager(JMenuItem exportMenuItem)
  {
    this.exportMenuItem = exportMenuItem;
  }

  public void installPCUAE()
  {
    pcuaeLatestInInstallFolder = readVersionFromInstallFolder(PCUAE_INSTALL_NAME);
    if (isNewVersionAvailable())
    {
      askAndStartDownload(pcuaeLatestInInstallFolder.isEmpty());
    }
    else
    {
      askToInstallExistingVersion();
    }
  }

  private void askAndStartDownload(boolean firstDownload)
  {
    PCUAEVersionDownloadDialog dialog = new PCUAEVersionDownloadDialog(MainWindow.getInstance(), firstDownload);
    dialog.pack();
    dialog.setLocationRelativeTo(MainWindow.getInstance());
    if (dialog.showDialog())
    {
      downloadLatestPCUAE();
    }
    else if (!firstDownload)
    {
      askToInstallExistingVersion();
    }
  }

  private void askToInstallExistingVersion()
  {
    int value = JOptionPane.showConfirmDialog(MainWindow.getInstance(),
                                              "Do you want to install PCUAE (" + pcuaeLatestInInstallFolder + ") now?",
                                              "Install PCUAE",
                                              JOptionPane.YES_NO_OPTION);
    if (value == JOptionPane.YES_OPTION)
    {
      singleThreadExecutor.execute(() -> runAndWaitForInstallation(pcuaeLatestInInstallFolder));
    }
  }

  private boolean isNewVersionAvailable()
  {
    gitHubReleaseInformation = fetchLatestVersionFromGithub(PCUAE_INSTALL_NAME);
    return ManagerVersionChecker.getIntVersion(pcuaeLatestInInstallFolder) < ManagerVersionChecker
      .getIntVersion(gitHubReleaseInformation.getInstallFile());
  }

  @Override
  protected void executeAfterInstallation()
  {
    SwingUtilities.invokeLater(() -> exportMenuItem.doClick());
  }

  private void downloadLatestPCUAE()
  {
    DownloadDialog progressDialog =
      new DownloadDialog("Downloading PCUAE version " + gitHubReleaseInformation.getLatestVersion());
    singleThreadExecutor.execute(() -> startDownload(progressDialog, gitHubReleaseInformation));
    progressDialog.pack();
    progressDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (progressDialog.showDialog())
    {
      pcuaeLatestInInstallFolder = gitHubReleaseInformation.getInstallFile();
      int value = JOptionPane.showConfirmDialog(MainWindow.getInstance(),
                                                "Download completed, do you want to install PCUAE " +
                                                  gitHubReleaseInformation.getLatestVersion() + " now?",
                                                "Download Complete",
                                                JOptionPane.YES_NO_OPTION);
      if (value == JOptionPane.YES_OPTION)
      {
        singleThreadExecutor.execute(() -> runAndWaitForInstallation(pcuaeLatestInInstallFolder));
      }
    }
    else
    {
      cleanupInterruptedDownload(gitHubReleaseInformation.getInstallFile());
    }
  }

  public static String getLatestInInstallFolder()
  {
    return pcuaeLatestInInstallFolder;
  }

  public static String getLatestVersion()
  {
    return gitHubReleaseInformation.getLatestVersion();
  }

  public static String getDownloadUrl()
  {
    return gitHubReleaseInformation.getDownloadUrl();
  }
}
