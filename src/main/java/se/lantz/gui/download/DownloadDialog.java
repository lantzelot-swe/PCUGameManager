package se.lantz.gui.download;

import javax.swing.JProgressBar;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;

public class DownloadDialog extends BaseDialog
{
  DownloadPanel deletePanel;
  private boolean complete = false;

  public DownloadDialog(String title)
  {
    super(MainWindow.getInstance());
    setTitle(title);
    addContent(getDownloadPanel());
    this.setResizable(false);
    getCancelButton().setVisible(false);
    getOkButton().setVisible(false);
  }

  private DownloadPanel getDownloadPanel()
  {
    if (deletePanel == null)
    {
      deletePanel = new DownloadPanel();
    }
    return deletePanel;
  }

  @Override
  public boolean showDialog()
  {
    return super.showDialog() || complete;
  }
  
  public JProgressBar getProgressBar()
  {
    return getDownloadPanel().getProgressBar();
  }
  
  public void closeWhenComplete()
  {
    complete = true;
    setVisible(false);
  }
}
