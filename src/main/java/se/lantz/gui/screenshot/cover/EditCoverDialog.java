package se.lantz.gui.screenshot.cover;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;

public class EditCoverDialog extends BaseDialog
{
  private EditCoverPanel editScreenPanel;

  public EditCoverDialog(BufferedImage originalScreen)
  {
    super(MainWindow.getInstance());
    setTitle("Edit screenshot");
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(getEditScreenshotPanel(), BorderLayout.CENTER);
    addContent(content);
    getEditScreenshotPanel().setImage(originalScreen);
    this.setResizable(false);
  }

  private EditCoverPanel getEditScreenshotPanel()
  {
    if (editScreenPanel == null)
    {
      editScreenPanel = new EditCoverPanel();
    }
    return editScreenPanel;
  }

  @Override
  public boolean showDialog()
  {
    return super.showDialog();
  }

  public BufferedImage getEditedImage()
  {
    return getEditScreenshotPanel().getCroppedImage();
  }
}
