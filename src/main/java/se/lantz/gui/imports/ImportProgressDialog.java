package se.lantz.gui.imports;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

public class ImportProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private ImportProgressPanel panel;
  
  private boolean cancelled = false;

  public ImportProgressDialog(Frame frame)
  {
    super(frame,"Import games", true);
    this.add(getImportProgressPanel());
    setSize(900, 600);
    setAlwaysOnTop(true);
    setLocationRelativeTo(frame);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
  }

  public void updateProgress(String infoText)
  {
    getImportProgressPanel().updateProgress(infoText);
    this.repaint();
  }
  
  public void updateProgressBar(String valuestring, int maximum, int value)
  {
    getImportProgressPanel().updateProgressBar(valuestring, maximum, value);
    this.repaint();
  }
  
  public void finish()
  {
    getImportProgressPanel().finish();
    getImportProgressPanel().getCancelButton().setText("Close");
    getImportProgressPanel().getCancelButton().setEnabled(true);
    getImportProgressPanel().getCancelButton().addActionListener(e -> setVisible(false));
  }

  public ImportProgressPanel getImportProgressPanel()
  {
    if (panel == null)
    {
      panel = new ImportProgressPanel();
      panel.getCancelButton().addActionListener(e -> {
        cancelled = true;
        panel.getCancelButton().setEnabled(false);
      });
    }
    return panel;
  }
  
  public boolean isCancelled()
  {
    return cancelled;
  }
}
