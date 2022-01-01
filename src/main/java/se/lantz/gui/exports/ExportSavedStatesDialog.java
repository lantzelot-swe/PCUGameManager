package se.lantz.gui.exports;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;

import se.lantz.gui.BaseDialog;

public class ExportSavedStatesDialog extends BaseDialog
{
  ExportSavedStatesPanel panel;

  public ExportSavedStatesDialog(Frame owner)
  {
    super(owner);
    addContent(getExportSavedStatesPanel());
    setTitle("Export saved states");
    this.setPreferredSize(new Dimension(435, 240));
    getOkButton().setText("Export");
    this.setResizable(false);
  }
  
  private ExportSavedStatesPanel getExportSavedStatesPanel()
  {
    if (panel == null)
    {
      panel = new ExportSavedStatesPanel();
    }
    return panel;
  }
  
  public File getTargetDirectory()
  {
    return getExportSavedStatesPanel().getTargetDirectory();
  }
  
  public boolean isExportOverwrite()
  {
    return getExportSavedStatesPanel().isExportOverwrite();
  }
}
