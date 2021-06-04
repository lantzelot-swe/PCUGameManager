package se.lantz.gui.exports;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.util.List;

import se.lantz.gui.BaseDialog;
import se.lantz.model.data.GameListData;

public class ExportGamesDialog extends BaseDialog
{
  private ExportMainPanel panel;
  private boolean carouselMode;

  public ExportGamesDialog(Frame owner, boolean carouselMode)
  {
    super(owner);
    this.carouselMode = carouselMode;
    setTitle(carouselMode ? "Export games to carousel" : "Export games to File loader");
    addContent(getExportGamesPanel());
    getOkButton().setText("Export");
    this.setPreferredSize(new Dimension(800, 700));
  }

  private ExportMainPanel getExportGamesPanel()
  {
    if (panel == null)
    {
      panel = new ExportMainPanel(getOkButton(), carouselMode);
    }
    return panel;
  }

  public List<GameListData> getSelectedGames()
  {
    return getExportGamesPanel().getSelectedGames();
  }

  public File getTargetDirectory()
  {
    return getExportGamesPanel().getTargetDirectory();
  }

  public boolean deleteBeforeExport()
  {
    return getExportGamesPanel().deleteBeforeExport();
  }

  public boolean addGamesSubDirectory()
  {
    return getExportGamesPanel().addGamesSubDirectory();
  }
}
