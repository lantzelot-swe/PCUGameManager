package se.lantz.gui.install;

import java.awt.Dimension;
import java.awt.Frame;

import se.lantz.gui.BaseDialog;
import se.lantz.util.ManagerVersionChecker;

public class VersionDownloadDialog extends BaseDialog
{
  private VersionDownloadPanel panel;

  public VersionDownloadDialog(Frame owner)
  {
    super(owner);
    setTitle("New version available");
    addContent(getVersionDownloadPanel());
    this.setResizable(false);
    getOkButton().setText("Yes");
    getCancelButton().setText("No");
    getOkButton().setPreferredSize(new Dimension(73, 23));
    getCancelButton().setPreferredSize(new Dimension(73, 23));
  }

  private VersionDownloadPanel getVersionDownloadPanel() {
    if (panel == null) {
      String downloadUrl = ManagerVersionChecker.getDownloadUrl();    
      String message = "<html>There is a new version of PCUAE Manager available: <a href='" + downloadUrl  + "'>" + 
        ManagerVersionChecker.getLatestVersion() + "</a><p>" + "Do you want to update to the new version now?</html>";
    	panel = new VersionDownloadPanel(message);
    }
    return panel;
  }
 
}
