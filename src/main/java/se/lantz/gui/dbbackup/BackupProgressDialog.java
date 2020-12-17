package se.lantz.gui.dbbackup;

import java.awt.Frame;

import javax.swing.JDialog;

public class BackupProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private BackupProgressPanel panel;

  public BackupProgressDialog(Frame frame)
  {
    super(frame,"Backup database", true);
    this.add(getExportProgressPanel());
    setSize(900, 600);
    setAlwaysOnTop(true);
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

  public BackupProgressPanel getExportProgressPanel()
  {
    if (panel == null)
    {
      panel = new BackupProgressPanel();
      panel.getCloseButton().addActionListener(e -> setVisible(false));
    }
    return panel;
  }
}
