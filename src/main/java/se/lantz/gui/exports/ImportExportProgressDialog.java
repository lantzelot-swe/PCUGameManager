package se.lantz.gui.exports;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

public class ImportExportProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private ImportExportProgressPanel panel;

  private final boolean isImport;

  public ImportExportProgressDialog(Frame frame, String title, boolean isImport)
  {
    super(frame, title, true);
    this.isImport = isImport;
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
    getExportProgressPanel().finish(isImport);
  }

  public ImportExportProgressPanel getExportProgressPanel()
  {
    if (panel == null)
    {
      panel = new ImportExportProgressPanel();
      panel.getCloseButton().addActionListener(e -> setVisible(false));
    }
    return panel;
  }
}
