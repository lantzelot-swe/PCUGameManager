package se.lantz.gui.gameview;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;
import se.lantz.model.data.GameView;

public class GameViewEditDialog extends BaseDialog
{

  private FilterPanel filterPanel;
  private ViewNamePanel namePanel;

  public GameViewEditDialog(Frame owner, GameView gameView)
  {
    super(owner);
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(getViewNamePanel(gameView), BorderLayout.NORTH);
    content.add(getFilterPanel(gameView), BorderLayout.CENTER);

    addContent(content);
  }

  private FilterPanel getFilterPanel(GameView gameView)
  {
    if (filterPanel == null)
    {
      filterPanel = new FilterPanel(gameView);
    }
    return filterPanel;
  }

  private ViewNamePanel getViewNamePanel(GameView gameView)
  {
    if (namePanel == null)
    {
      namePanel = new ViewNamePanel(gameView);
    }
    return namePanel;
  }
  
  public void updateGameViewAfterClosing() {
    namePanel.updateGameView();
    filterPanel.updateGameView();
    
  }
}
