package se.lantz.gui.imports;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;

import se.lantz.gui.BaseDialog;

public class ImportDatabaseDialog extends BaseDialog
{
  ImportDatabasePanel panel;

  public ImportDatabaseDialog(Frame owner)
  {
    super(owner);
    addContent(getImportDatabasePanel());
    setTitle("Import database");
    this.setPreferredSize(new Dimension(435, 200));
    getOkButton().setText("Import");
    this.setResizable(false);
  }

  private ImportDatabasePanel getImportDatabasePanel()
  {
    if (panel == null)
    {
      panel = new ImportDatabasePanel();
    }
    return panel;
  }

  public File getImportDirectory()
  {
    return getImportDatabasePanel().getTargetDirectory();
  }
}
