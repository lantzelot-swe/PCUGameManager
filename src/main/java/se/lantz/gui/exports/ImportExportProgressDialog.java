package se.lantz.gui.exports;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

public class ImportExportProgressDialog extends JDialog
{
  public enum DIALOGTYPE
  {
    IMPORT, EXPORT, FIX
  }
  private static final long serialVersionUID = 1L;

  private ImportExportProgressPanel panel;

  private final DIALOGTYPE type;

  public ImportExportProgressDialog(Frame frame, String title, DIALOGTYPE type)
  {
    super(frame, title, true);
    this.type = type;
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
    getExportProgressPanel().finish(type);
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
