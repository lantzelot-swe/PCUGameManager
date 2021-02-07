package se.lantz.gui;

public class DeleteDialog extends BaseDialog
{
  DeletePanel deletePanel;
  private final boolean deleteAll;

  public DeleteDialog(boolean deleteAll)
  {
    super(MainWindow.getInstance());
    this.deleteAll = deleteAll;
    setTitle("Delete games");
    ;
    addContent(getDeletePanel());
    this.setResizable(false);
  }

  private DeletePanel getDeletePanel()
  {
    if (deletePanel == null)
    {
      deletePanel = new DeletePanel(deleteAll);
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
