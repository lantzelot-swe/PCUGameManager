package se.lantz.gui.dbrestore;

import java.awt.Dimension;
import java.awt.Frame;

import se.lantz.gui.BaseDialog;

public class RestoreDbDialog extends BaseDialog
{
  private RestoreDbPanel panel;

  public RestoreDbDialog(Frame owner)
  {
    super(owner);
    setTitle("Restore database backup");
    addContent(getRestoreDbPanel());
    getOkButton().setText("Restore");
    getOkButton().setPreferredSize(null);
    this.setPreferredSize(new Dimension(400,200));
  }

  private RestoreDbPanel getRestoreDbPanel() {
    if (panel == null) {
    	panel = new RestoreDbPanel();
    }
    return panel;
  }
  
  public String getSelectedFolder()
  {
    return getRestoreDbPanel().getSelectedFolder();
  }
}
