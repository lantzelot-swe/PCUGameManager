package se.lantz.gui.imports;

import java.awt.Frame;

import se.lantz.gui.BaseDialog;
import se.lantz.manager.ImportManager;

public class ImportOptionsDialog extends BaseDialog
{
  private ImportOptionsPanel panel;

  public ImportOptionsDialog(Frame owner)
  {
    super(owner);
    setTitle("Import game carousel");
    addContent(getImportOptionsPanel());
    getOkButton().setText("Import");
  }

  private ImportOptionsPanel getImportOptionsPanel() {
    if (panel == null) {
    	panel = new ImportOptionsPanel();
    }
    return panel;
  }
  
  public ImportManager.Options getSelectedOption()
  {
    return getImportOptionsPanel().getSelectedOption();
  }
  
  
}
