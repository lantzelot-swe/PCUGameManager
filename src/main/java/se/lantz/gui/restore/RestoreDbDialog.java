package se.lantz.gui.restore;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;
import se.lantz.model.data.GameListData;

public class RestoreDbDialog extends BaseDialog
{
  private RestoreDbPanel panel;

  public RestoreDbDialog(Frame owner)
  {
    super(owner);
    setTitle("Export games");
    addContent(getRestoreDbPanel());
    getOkButton().setText("OK");
    this.setPreferredSize(new Dimension(800,700));
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
