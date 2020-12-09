package se.lantz.gui.imports;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class ImportProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private ImportProgressPanel panel;

  public ImportProgressDialog(Frame frame)
  {
    super(frame,"Import games", true);
//    this.setIconImage(new ImageIcon(getClass().getResource("/se/lantz/FrameIcon.png")).getImage());
    this.add(getImportProgressPanel());
    setSize(900, 600);
    setAlwaysOnTop(true);
    setLocationRelativeTo(frame);
  }

  public void updateProgress(String infoText)
  {
    getImportProgressPanel().updateProgress(infoText);
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
