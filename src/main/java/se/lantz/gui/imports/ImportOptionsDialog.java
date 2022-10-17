package se.lantz.gui.imports;

import java.awt.Dimension;
import java.awt.Frame;
import java.nio.file.Path;

import se.lantz.gamebase.GamebaseImporter;
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
      this.setPreferredSize(new Dimension(450, 405));
    }
    else
    {
      setTitle("Import games from Gamebase");
      this.setPreferredSize(new Dimension(450, 605));
    }
    addContent(getImportOptionsPanel());
    getOkButton().setText("Import");
    this.setResizable(false);
  }

  private ImportOptionsPanel getImportOptionsPanel()
  {
    if (panel == null)
    {
      panel = new ImportOptionsPanel(isCarouselImport);
    }
    return panel;
  }

  public ImportManager.Options getSelectedOption()
  {
    return getImportOptionsPanel().getSelectedOption();
  }

  public int getMarkAsFavorite()
  {
    return getImportOptionsPanel().getMarkAsFavorite();
  }

  public String getViewTag()
  {
    return getImportOptionsPanel().getViewTag();
  }

  public Path getImportDirectory()
  {
    return getImportOptionsPanel().getImportDirectory();
  }

  public GamebaseOptions getSelectedGbOptions()
  {
    return getImportOptionsPanel().getSelectedGbOptions();
  }
  
  public boolean isCreateGameViews()
  {
    return getImportOptionsPanel().isCreateGameViews();
  }
    
  public boolean showDialog(GamebaseImporter importer)
  {
    getImportOptionsPanel().getGbOptionsPanel().setGamebaseImporter(importer);
    return super.showDialog();
  }
}
