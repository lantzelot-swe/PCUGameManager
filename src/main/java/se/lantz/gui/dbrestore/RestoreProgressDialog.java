package se.lantz.gui.dbrestore;

import java.awt.Frame;

import javax.swing.JDialog;

public class RestoreProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private RestoreProgressPanel panel;

  public RestoreProgressDialog(Frame frame)
  {
    super(frame,"Restore backup", true);
    this.add(getExportProgressPanel());
    setSize(900, 600);
    setLocationRelativeTo(frame);
  }

  public void updateProgress(String infoText)
  {
    getExportProgressPanel().updateProgress(infoText);
    this.repaint();
  }
  
  public void finish()
  {
    getExportProgressPanel().finish();
  }

  public RestoreProgressPanel getExportProgressPanel()
  {
    if (panel == null)
    {
      panel = new RestoreProgressPanel();
      panel.getCloseButton().addActionListener(e -> setVisible(false));
    }
    return panel;
  }
}
