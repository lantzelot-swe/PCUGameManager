package se.lantz.gui.screenshot.cover;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;

public class EditCoverDialog extends BaseDialog
{
  private EditCoverPanel editCoverPanel;

  public EditCoverDialog(BufferedImage originalCover)
  {
    super(MainWindow.getInstance());
    setTitle("Edit cover image");
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(getEditCoverPanel(), BorderLayout.CENTER);
    addContent(content);
    getEditCoverPanel().setImage(originalCover);
    this.setResizable(false);
  }

  private EditCoverPanel getEditCoverPanel()
  {
    if (editCoverPanel == null)
    {
      editCoverPanel = new EditCoverPanel();
    }
    return editCoverPanel;
  }

  @Override
  public boolean showDialog()
  {
    return super.showDialog();
  }

  public BufferedImage getEditedImage()
  {
    return getEditCoverPanel().getCroppedImage();
  }
}
