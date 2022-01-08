package se.lantz.gui.install;

import java.awt.Dimension;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;
import se.lantz.manager.pcuae.BaseInstallManger;

public class PCUAEProductDownloadDialog extends BaseDialog
{
  private ManagerDownloadPanel panel;
  private boolean firstDownload;
  private String productText;
  private BaseInstallManger manager;
  private boolean startupCheck;

  public PCUAEProductDownloadDialog(final boolean firstDownload, final BaseInstallManger manager, String productName, final boolean startupCheck)
  {
    super(MainWindow.getInstance());
    this.firstDownload = firstDownload;
    this.manager = manager;
    this.productText = productName;
    this.startupCheck = startupCheck;
    setTitle(firstDownload ? "Download " + productName : "New version available");
    addContent(getVersionDownloadPanel());
    this.setResizable(false);
    getOkButton().setText("Yes");
    getCancelButton().setText("No");
    getOkButton().setPreferredSize(new Dimension(73, 23));
    getCancelButton().setPreferredSize(new Dimension(73, 23));
  }

  private ManagerDownloadPanel getVersionDownloadPanel()
  {
    if (panel == null)
    {
      String downloadUrl = manager.getReleaseTagUrl();
      String message = "";
      if (firstDownload)
      {
        message = "<html>You have to download " + productText +  "  before installing it. The latest version is <a href='" +
          downloadUrl + "'>" + manager.getLatestVersion() + "</a><p>Do you want to download now?</html>";
      }
      else
      {
        if (startupCheck)
        {
          message = "<html>PCUAE main setup <a href='" + downloadUrl + "'>" + manager.getLatestVersion() +
            "</a> is available.<p>Do you want to download this version now?<p>You can install it to a USB drive later from the PCUAE menu.</html>";
        }
        else
        {
          message = "<html>" + productText + " <a href='" + downloadUrl + "'>" + manager.getLatestVersion() +
            "</a> is available.<p>(Current install file: " + manager.getLatestInInstallFolder() +
            ").<br>Do you want to download and install the new version?</html>";
        }
      }

      panel = new ManagerDownloadPanel(message);
    }
    return panel;
  }

}
