package se.lantz.gui.gameview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JPanel;

import se.lantz.gui.BaseDialog;
import se.lantz.model.data.GameView;

public class GameViewRenameFavoritesDialog extends BaseDialog
{

  private ViewNamePanel namePanel;

  public GameViewRenameFavoritesDialog(Frame owner, GameView gameView)
  {
    super(owner);
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(getViewNamePanel(gameView), BorderLayout.CENTER);
    setPreferredSize(new Dimension(400, 130));
    this.setResizable(false);
    addContent(content);
    setTitle("Rename favorites view");
  }

  private ViewNamePanel getViewNamePanel(GameView gameView)
  {
    if (namePanel == null)
    {
      namePanel = new ViewNamePanel(gameView);
    }
    return namePanel;
  }

  public void updateGameViewAfterClosing()
  {
    namePanel.updateGameView();
  }

  @Override
  public void setVisible(boolean visible)
  {
    if (visible)
    {
      namePanel.getTextField().requestFocusInWindow();
    }
    super.setVisible(visible);
  }
}
