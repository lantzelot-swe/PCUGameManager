package se.lantz.gui.imports;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;

import se.lantz.gui.BaseDialog;

public class ImportSavedStatesDialog extends BaseDialog
{
  ImportSavedStatesPanel panel;

  public ImportSavedStatesDialog(Frame owner)
  {
    super(owner);
    addContent(getImportSavedStatesPanel());
    setTitle("Import saved states");
    this.setPreferredSize(new Dimension(435, 310));
    getOkButton().setText("Import");
    this.setResizable(false);
  }
  
  private ImportSavedStatesPanel getImportSavedStatesPanel()
  {
    if (panel == null)
    {
      panel = new ImportSavedStatesPanel();
    }
    return panel;
  }
  
  public File getTargetDirectory()
  {
    return getImportSavedStatesPanel().getTargetDirectory();
  }
  
  public boolean isImportOverwrite()
  {
    return getImportSavedStatesPanel().isImportOverwrite();
  }
}
