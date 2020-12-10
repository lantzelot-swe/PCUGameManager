package se.lantz.gui.gameview;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import se.lantz.gui.MainPanel;
import se.lantz.gui.MainWindow;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameView;

public class GameViewManager
{
  private JComboBox<GameView> viewCombobox;
  private MainWindow mainWindow;
  private final MainViewModel uiModel;
  private MainPanel mainPanel;
  
  public GameViewManager(MainPanel mainPanel, MainViewModel uiModel)
  {
    this.mainPanel = mainPanel;
    this.viewCombobox = mainPanel.getListViewComboBox();
    this.uiModel = uiModel;
  }

  public void openViewEditDialog(GameView gameView)
  {
    mainWindow = (MainWindow)SwingUtilities.getAncestorOfClass(MainWindow.class, viewCombobox);
    GameViewEditDialog dialog = new GameViewEditDialog(mainWindow, gameView);
    if (gameView.getGameViewId() == 0)
    {
      dialog.setTitle("Add game view");
    }
    else
    {
      dialog.setTitle("Edit game view");
    }
    dialog.pack();
    dialog.setLocationRelativeTo(mainWindow);
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
}
