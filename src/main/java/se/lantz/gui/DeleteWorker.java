package se.lantz.gui;

import javax.swing.SwingWorker;

import se.lantz.model.MainViewModel;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class DeleteWorker extends SwingWorker<Void, String>
{
  private DeleteProgressDialog dialog;
  private boolean deleteAll;
  private MainViewModel model;

  public DeleteWorker(DeleteProgressDialog dialog, boolean deleteAll, MainViewModel model)
  {
    this.deleteAll = deleteAll;
    this.dialog = dialog;
    this.model = model;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    if (deleteAll)
    {
      model.deleteAllGames();
      FileManager.deleteAllFolderContent();
    }
    else
    {
      model.deleteAllGamesInCurrentView();
    }
    return null;
  }

  @Override
  protected void done()
  {
    try
    {
      get();
    }
    catch (Exception e)
    {
      ExceptionHandler.handleException(e, "Error when deleting");
    }
    dialog.finish();
    if (deleteAll)
    {
      //Trigger a reload of game views
      model.reloadGameViews();
      MainWindow.getInstance().selectViewAfterRestore();
    }
    MainWindow.getInstance().getMainPanel().repaintAfterModifications();
  }
}
