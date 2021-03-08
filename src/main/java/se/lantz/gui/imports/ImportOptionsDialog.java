package se.lantz.gui.imports;

import java.awt.Dimension;
import java.awt.Frame;
import java.nio.file.Path;

import se.lantz.gamebase.GamebaseOptions;
import se.lantz.gui.BaseDialog;
import se.lantz.manager.ImportManager;

public class ImportOptionsDialog extends BaseDialog
{
  private ImportOptionsPanel panel;
  private boolean isCarouselImport;

  public ImportOptionsDialog(Frame owner, boolean isCarouselImport)
  {
    super(owner);
    this.isCarouselImport = isCarouselImport;
    if (isCarouselImport)
    {
      setTitle("Import carousel folder");
      this.setPreferredSize(new Dimension(450,330));
    }
    else
    {
      setTitle("Import games from Gamebase");
      this.setPreferredSize(new Dimension(450,420));
    }
    addContent(getImportOptionsPanel());
    getOkButton().setText("Import");
    this.setResizable(false);  
  }

  private ImportOptionsPanel getImportOptionsPanel() {
    if (panel == null) {
    	panel = new ImportOptionsPanel(isCarouselImport);
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
  
  public GamebaseOptions getSelectedGbOptions()
  {
    return getImportOptionsPanel().getSelectedGbOptions();
  }
}
