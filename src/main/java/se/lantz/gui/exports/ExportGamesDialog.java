package se.lantz.gui.exports;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import se.lantz.gui.BaseDialog;
import se.lantz.model.data.GameListData;

public class ExportGamesDialog extends BaseDialog
{
  private ExportGamesSelectionPanel panel;

  public ExportGamesDialog(Frame owner)
  {
    super(owner);
    setTitle("Export games");
    addContent(getExportGamesPanel());
    getOkButton().setText("OK");
    this.setPreferredSize(new Dimension(800,700));
  }

  private ExportGamesSelectionPanel getExportGamesPanel() {
    if (panel == null) {
    	panel = new ExportGamesSelectionPanel();
    }
    return panel;
  }
  
  public List<GameListData> getSelectedGames()
  {
    return getExportGamesPanel().getSelectedGames();
  }
  
  public boolean isFavFormat()
  {
    return getExportGamesPanel().isFavFormat();
  }
}
