package se.lantz.gui.imports;

import java.awt.Dimension;
import java.awt.Frame;
import java.nio.file.Path;

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
    this.setResizable(false);
    this.setPreferredSize(new Dimension(450,330));
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
  
  public boolean getMarkAsFavorite()
  {
   return  getImportOptionsPanel().getMarkAsFavorite();
  }
  
  public Path getImportDirectory()
  {
   return  getImportOptionsPanel().getImportDirectory();
  }
}
