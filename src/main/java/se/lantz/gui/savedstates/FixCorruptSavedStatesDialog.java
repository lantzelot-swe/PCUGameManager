package se.lantz.gui.savedstates;

import java.awt.Dimension;
import java.io.File;

import se.lantz.gui.BaseDialog;
import se.lantz.gui.MainWindow;

public class FixCorruptSavedStatesDialog extends BaseDialog
{
  FixCorruptSavedStatesPanel panel;

  public FixCorruptSavedStatesDialog()
  {
    super(MainWindow.getInstance());
    addContent(getImportSavedStatesPanel());
    setTitle("Fix corrupt saved states");
    this.setPreferredSize(new Dimension(435, 310));
    //    getOkButton().setText("");
    this.setResizable(false);
  }

  private FixCorruptSavedStatesPanel getImportSavedStatesPanel()
  {
    if (panel == null)
    {
      panel = new FixCorruptSavedStatesPanel();
    }
    return panel;
  }

  public File getTargetDirectory()
  {
    return getImportSavedStatesPanel().getTargetDirectory();
  }

  //  public boolean isImportOverwrite()
  //  {
  //    return getImportSavedStatesPanel().isImportOverwrite();
  //  }
}
