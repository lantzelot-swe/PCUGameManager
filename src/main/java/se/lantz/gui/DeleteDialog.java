package se.lantz.gui;

public class DeleteDialog extends BaseDialog
{
  public static enum TYPE_OF_DELETE
  {
    ALL, VIEW, ALL_VIEWS
  }

  DeletePanel deletePanel;
  private final TYPE_OF_DELETE typeOfDelete;

  public DeleteDialog(TYPE_OF_DELETE typeOfDelete)
  {
    super(MainWindow.getInstance());
    this.typeOfDelete = typeOfDelete;
    if (typeOfDelete.equals(TYPE_OF_DELETE.ALL_VIEWS))
    {
      setTitle("Delete gamelist views");
    }
    else
    {
      setTitle("Delete games");
    }
    addContent(getDeletePanel());
    this.setResizable(false);
  }

  private DeletePanel getDeletePanel()
  {
    if (deletePanel == null)
    {
      deletePanel = new DeletePanel(typeOfDelete);
    }
    return deletePanel;
  }

  @Override
  public boolean showDialog()
  {
    return super.showDialog();
  }

  public boolean isCreatebackup()
  {
    return getDeletePanel().isCreatebackup();
  }
}
