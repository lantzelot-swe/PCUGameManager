package se.lantz.gui.gameview;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import se.lantz.gui.ListPanel;
import se.lantz.gui.MainPanel;
import se.lantz.gui.MainWindow;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameView;

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
      dialog.setTitle("Add game view");
    }
    else
    {
      dialog.setTitle("Edit game view");
    }
    dialog.pack();
    dialog.setLocationRelativeTo(MainWindow.getInstance());
    if (dialog.showDialog())
    {
      //Update gameView instance with edited values in the dialog
      dialog.updateGameViewAfterClosing();
      boolean newGameView = gameView.getGameViewId() == 0;
      //Save in Dd
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
  }
  
  public void deleteView(GameView view)
  {
    String message = "Do you want to delete the game view  \"" + view.getName() + "\"?";
    int value = JOptionPane.showConfirmDialog(MainWindow.getInstance().getMainPanel(), message, "Delete game view", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (value == JOptionPane.YES_OPTION)
    {
      uiModel.deleteGameView(view);
      //Trigger a reload of game views
      uiModel.reloadGameViews();
      MainWindow.getInstance().selectViewAfterRestore();
    }
  }
}
