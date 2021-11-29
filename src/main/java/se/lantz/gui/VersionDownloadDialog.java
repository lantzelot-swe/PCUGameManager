package se.lantz.gui;

import java.awt.Frame;

public class VersionDownloadDialog extends BaseDialog
{
  private VersionDownloadPanel panel;

  public VersionDownloadDialog(Frame owner)
  {
    super(owner);
    setTitle("New version available");
    addContent(getVersionDownloadPanel());
//    getOkButton().setText("Yes");
//    getCancelButton().setText("No");
    this.setResizable(false);
  }

  private VersionDownloadPanel getVersionDownloadPanel() {
    if (panel == null) {
    	panel = new VersionDownloadPanel();
    }
    return panel;
  }
 
}
