package se.lantz.gui.exports;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

public class ExportProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private ExportProgressPanel panel;

  public ExportProgressDialog(Frame frame)
  {
    super(frame,"Export games", true);
    this.add(getExportProgressPanel());
    setSize(900, 600);
    setLocationRelativeTo(frame);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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

  public ExportProgressPanel getExportProgressPanel()
  {
    if (panel == null)
    {
      panel = new ExportProgressPanel();
      panel.getCloseButton().addActionListener(e -> setVisible(false));
    }
    return panel;
  }
}
