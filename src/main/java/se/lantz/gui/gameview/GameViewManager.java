package se.lantz.gui.gameview;

import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import se.lantz.gui.ListPanel;
import se.lantz.gui.MainWindow;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameView;
import se.lantz.util.FileManager;

public class GameViewManager
{
  private JComboBox<GameView> viewCombobox;
  private final MainViewModel uiModel;
  private ListPanel mainPanel;

  public GameViewManager(ListPanel mainPanel, MainViewModel uiModel)
  {
    this.mainPanel = mainPanel;
    this.viewCombobox = mainPanel.getListViewComboBox();
    this.uiModel = uiModel;
  }

  public void openViewEditDialog(GameView gameView)
  {
    GameViewEditDialog dialog = new GameViewEditDialog(MainWindow.getInstance(), gameView);
    if (gameView.getGameViewId() == 0)
    {
      dialog.setTitle("Add gamelist view");
    }
    else
    {
      dialog.setTitle("Edit gamelist view");
    }
    dialog.pack();
    dialog.setLocationRelativeTo(MainWindow.getInstance());
    if (dialog.showDialog())
    {
      String originalName = gameView.getName();
      //Update gameView instance with edited values in the dialog
      dialog.updateGameViewAfterClosing();
      if (okToSave(gameView))
      {
        boolean newGameView = gameView.getGameViewId() == 0;
        //Save in db
        uiModel.saveGameView(gameView);
        if (newGameView)
        {
          //new view, add to combobox
          viewCombobox.addItem(gameView);
          viewCombobox.setSelectedItem(gameView);
        }
        else
        {
          //Trigger reload of game view after editing
          uiModel.setSelectedGameView(gameView);
          viewCombobox.invalidate();
          viewCombobox.repaint();
          mainPanel.updateViewInfoLabel();
        }
      }
      else
      {
        gameView.setName(originalName);
        openViewEditDialog(gameView);
      }
    }
  }

  private boolean okToSave(GameView gameView)
  {
    String title = "No filters";
    if (gameView.getViewFilters().isEmpty())
    {
      JOptionPane.showMessageDialog(MainWindow.getInstance(),
                                    "The gamelist view have no filters defined, add a filter and try again.",
                                    title,
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }

    title = "Invalid name";
    //Check name towards all other available gameviews, no duplicates. Also minimum two characters
    if (gameView.getName().length() < 2)
    {
      JOptionPane.showMessageDialog(MainWindow.getInstance(),
                                    "The gamelist view name is too short, give it a different name.",
                                    title,
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    for (int i = 0; i < uiModel.getGameViewModel().getSize(); i++)
    {
      GameView currentView = uiModel.getGameViewModel().getElementAt(i);
      if (currentView.getGameViewId() != gameView.getGameViewId() &&
        currentView.getName().equalsIgnoreCase(gameView.getName()))
      {
        JOptionPane.showMessageDialog(MainWindow.getInstance(),
                                      "A gamelist view with the same name aleady exists, give it a different name.",
                                      title,
                                      JOptionPane.ERROR_MESSAGE);
        return false;
      }
    }

    return true;
  }

  public void deleteView(GameView view)
  {
    String message = "Do you want to delete the gamelist view  \"" + view.getName() + "\"?";
    int value = JOptionPane.showConfirmDialog(MainWindow.getInstance()
      .getMainPanel(), message, "Delete gamelist view", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (value == JOptionPane.YES_OPTION)
    {
      uiModel.deleteGameView(view);
      //Trigger a reload of game views
      uiModel.reloadGameViews();
      MainWindow.getInstance().selectViewAfterRestore();
    }
  }

  public void renameFavoritesView(GameView gameView)
  {
    GameViewRenameFavoritesDialog dialog = new GameViewRenameFavoritesDialog(MainWindow.getInstance(), gameView);
    dialog.pack();
    dialog.setLocationRelativeTo(MainWindow.getInstance());
    if (dialog.showDialog())
    {
      String originalName = gameView.getName();
      //Update gameView instance with edited values in the dialog
      dialog.updateGameViewAfterClosing();
      if (okToSave(gameView))
      {
        saveFavNameInPreferences(gameView);

        //Refresh after renaming
        MainWindow.getInstance().refreshMenuAndUI();
        //Trigger reload of game view after editing
        uiModel.setSelectedGameView(gameView);
        viewCombobox.invalidate();
        viewCombobox.repaint();
        mainPanel.updateViewInfoLabel();
      }
      else
      {
        gameView.setName(originalName);
        renameFavoritesView(gameView);
      }
    }
  }

  private void saveFavNameInPreferences(GameView gameView)
  {
    Properties configuredProperties = FileManager.getConfiguredProperties();
    configuredProperties.put(gameView.getFavNamePreferencesKey(), gameView.getName());
    FileManager.storeProperties();
  }
}
