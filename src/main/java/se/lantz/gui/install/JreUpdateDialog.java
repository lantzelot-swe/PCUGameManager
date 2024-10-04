package se.lantz.gui.install;

import java.awt.Dimension;
import java.awt.Frame;

import se.lantz.gui.BaseDialog;

public class JreUpdateDialog extends BaseDialog
{
  private ManagerDownloadPanel panel;

  public JreUpdateDialog(Frame owner)
  {
    super(owner);
    setTitle("New JRE version required");
    addContent(getVersionDownloadPanel());
    this.setResizable(false);
    getOkButton().setText("Exit");
    getCancelButton().setVisible(false);
    getOkButton().setPreferredSize(new Dimension(73, 23));
  }

  private ManagerDownloadPanel getVersionDownloadPanel()
  {
    if (panel == null)
    {
      String downloadUrl = "https://github.com/lantzelot-swe/PCUGameManager/releases/tag/3.1.0";
      String jreUrl = "https://github.com/lantzelot-swe/PCUGameManager/releases/tag/3.1.0/xjre18.zip";
      String message =
        "<html>This version of PCUAE Manager requires an updated JRE. Follow these steps to update:<ol><li>Go to <a href='" +
          downloadUrl + "'>Release 3.1.0</a>" + " and download <a href='" + jreUrl + "'>xjre18.zip</a>.</li>" +
          "<li>Exit this program by closing this dialog.</li>" +
          "<li>Delete the \"jre\" folder in the PCUAE Manager main directory.</li>" +
          "<li>Unpack the zip file in the PCUAE Manager main directory, replacing the \"jre\" folder. An additonal folder named \"natives\" is also created.</li>" +
          "<li>Start PCUAE Manager again.</li></ol></html>";
      panel = new ManagerDownloadPanel(message);
    }
    return panel;
  }
}
