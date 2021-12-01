package se.lantz.gui.download;

import javax.swing.JProgressBar;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;

public class DownloadDialog extends BaseDialog
{
  DownloadPanel downloadPanel;
  private boolean complete = false;

  public DownloadDialog(String downloadLabel)
  {
    super(MainWindow.getInstance());
//    setTitle(title);
    addContent(getDownloadPanel());
    this.setResizable(false);
    getCancelButton().setVisible(false);
    getOkButton().setVisible(false);
    
    getDownloadPanel().getInfoLabel().setText(downloadLabel);
  }

  private DownloadPanel getDownloadPanel()
  {
    if (downloadPanel == null)
    {
      downloadPanel = new DownloadPanel();
    }
    return downloadPanel;
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
