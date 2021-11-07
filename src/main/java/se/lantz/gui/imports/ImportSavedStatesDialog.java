package se.lantz.gui.imports;

import java.awt.Dimension;
import java.awt.Frame;

import se.lantz.gui.BaseDialog;

public class ImportSavedStatesDialog extends BaseDialog
{
  ImportSavedStatesPanel panel;

  public ImportSavedStatesDialog(Frame owner)
  {
    super(owner);
    addContent(getImportSavedStatesPanel());
    setTitle("Import saved states");
    this.setPreferredSize(new Dimension(435, 280));
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
}
