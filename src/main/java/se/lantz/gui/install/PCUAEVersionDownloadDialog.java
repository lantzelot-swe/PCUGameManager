package se.lantz.gui.install;

import java.awt.Dimension;
import java.awt.Frame;

import se.lantz.gui.BaseDialog;
import se.lantz.manager.PCUAEInstallManager;

public class PCUAEVersionDownloadDialog extends BaseDialog
{
  private VersionDownloadPanel panel;
  private boolean firstDownload;

  public PCUAEVersionDownloadDialog(Frame owner, boolean firstDownload)
  {
    super(owner);
    this.firstDownload = firstDownload;
    setTitle(firstDownload? "Download PCUAE" : "New version available");
    addContent(getVersionDownloadPanel());
    this.setResizable(false);
    getOkButton().setText("Yes");
    getCancelButton().setText("No");
    getOkButton().setPreferredSize(new Dimension(73, 23));
    getCancelButton().setPreferredSize(new Dimension(73, 23));
  }

  private VersionDownloadPanel getVersionDownloadPanel() {
    if (panel == null) {
      String downloadUrl = PCUAEInstallManager.getDownloadUrl();
      String message = "";
      if (firstDownload)
      {
        message = "<html>You have to download PCUAE before installing it. The latest version is <a href='" + downloadUrl  + "'>" + 
          PCUAEInstallManager.getLatestVersion() + "</a><p>" + "Do you want to download it now?</html>";
      }
      else
      {
        message = "<html><a href='" + downloadUrl  + "'>" + 
          PCUAEInstallManager.getLatestVersion() + "</a> is available. (Current install file: " + PCUAEInstallManager.getLatestInInstallFolder()+ ").<p>Do you want to download the new version?</html>";
      }
      
    	panel = new VersionDownloadPanel(message);
    }
    return panel;
  }
 
}
