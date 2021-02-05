package se.lantz.gui.convertscreens;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

public class ConvertProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;

  private ConvertProgressPanel panel;

  public ConvertProgressDialog(Frame frame)
  {
    super(frame, "Convert screenshots", true);
    this.add(getConvertProgressPanel());
    setSize(900, 600);
    setAlwaysOnTop(true);
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

  public ConvertProgressPanel getConvertProgressPanel()
  {
    if (panel == null)
    {
      panel = new ConvertProgressPanel();
      panel.getCloseButton().addActionListener(e -> setVisible(false));
    }
    return panel;
  }
}
