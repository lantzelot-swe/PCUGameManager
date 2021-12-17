package se.lantz.gui.dbvalidation;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

public class DbValidationProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private DbValidationProgressPanel panel;

  public DbValidationProgressDialog(Frame frame)
  {
    super(frame, "Validate database", true);
    this.add(getConvertProgressPanel());
    setSize(900, 600);
    setLocationRelativeTo(frame);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
  }

  public void updateProgress(String infoText)
  {
    getConvertProgressPanel().updateProgress(infoText);
    this.repaint();
  }

  public void finish(int count, Exception e)
  {
    getConvertProgressPanel().finish(count, e);
  }

  public DbValidationProgressPanel getConvertProgressPanel()
  {
    if (panel == null)
    {
      panel = new DbValidationProgressPanel();
      panel.getCloseButton().addActionListener(e -> setVisible(false));
    }
    return panel;
  }
}
