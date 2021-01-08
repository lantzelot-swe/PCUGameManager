package se.lantz.gui.screenshot;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;
import se.lantz.manager.ScraperManager;
import se.lantz.model.data.ScraperFields;

public class EditScreenshotDialog extends BaseDialog
{
  private EditScreenshotPanel editScreenPanel;
  private BufferedImage originalScreen;
  public EditScreenshotDialog(BufferedImage originalScreen)
  {
    super(MainWindow.getInstance());
    setTitle("Edit screenshot");
    this.originalScreen = originalScreen;
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(getEditScreenshotPanel(), BorderLayout.CENTER);
    addContent(content);
    getEditScreenshotPanel().setImage(originalScreen);
    this.setResizable(false);
  }

  private EditScreenshotPanel getEditScreenshotPanel()
  {
    if (editScreenPanel == null)
    {
      editScreenPanel = new EditScreenshotPanel();
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
