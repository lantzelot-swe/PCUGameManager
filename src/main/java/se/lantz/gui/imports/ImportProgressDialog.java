package se.lantz.gui.imports;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class ImportProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private ImportProgressPanel panel;
  private JFrame frame;

  public ImportProgressDialog(JFrame frame)
  {
    this.setModal(true);
    this.setIconImage(new ImageIcon(getClass().getResource("/se/lantz/FrameIcon.png")).getImage());
    this.frame = frame;
    this.setTitle("Import games");
    this.add(getImportProgressPanel());
    setSize(900, 600);
    setAlwaysOnTop(true);
    setLocationRelativeTo(frame);
  }

  public void updateProgress(String progressText, String infoText)
  {
    getImportProgressPanel().updateProgress(progressText, infoText);
    this.repaint();
  }
  
  public void finish()
  {
    getImportProgressPanel().finish();
  }

  public ImportProgressPanel getImportProgressPanel()
  {
    if (panel == null)
    {
      panel = new ImportProgressPanel();
      panel.getCloseButton().addActionListener(e -> setVisible(false));
    }
    return panel;
  }
}
