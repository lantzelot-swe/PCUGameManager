package se.lantz.gui.checkdescriptions;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

public class CheckDescrProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private CheckDescrProgressPanel panel;

  public CheckDescrProgressDialog(Frame frame)
  {
    super(frame, "Check descriptions", true);
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

  public CheckDescrProgressPanel getConvertProgressPanel()
  {
    if (panel == null)
    {
      panel = new CheckDescrProgressPanel();
      panel.getCloseButton().addActionListener(e -> setVisible(false));
    }
    return panel;
  }
}
