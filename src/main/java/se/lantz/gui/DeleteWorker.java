package se.lantz.gui;

import javax.swing.SwingWorker;

import se.lantz.gui.DeleteDialog.TYPE_OF_DELETE;
import se.lantz.model.MainViewModel;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;

public class DeleteWorker extends SwingWorker<Void, String>
{
  private DeleteProgressDialog dialog;
  private TYPE_OF_DELETE typeOfDelete;
  private MainViewModel model;

  public DeleteWorker(DeleteProgressDialog dialog, TYPE_OF_DELETE typeOfDelete, MainViewModel model)
  {
    this.typeOfDelete = typeOfDelete;
    this.dialog = dialog;
    this.model = model;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    if (typeOfDelete.equals(TYPE_OF_DELETE.ALL))
    {
      model.deleteAllGames();
      FileManager.deleteAllFolderContent();
    }
    else if (typeOfDelete.equals(TYPE_OF_DELETE.VIEW))
    {
      model.deleteAllGamesInCurrentView();
    }
    else
    {
      model.deleteAllGameListViews();
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
    if (!typeOfDelete.equals(TYPE_OF_DELETE.VIEW))
    {
      //Trigger a reload of game views
      model.reloadGameViews();
      MainWindow.getInstance().selectViewAfterRestore();
    }
    MainWindow.getInstance().getMainPanel().repaintAfterModifications();
  }
}
