package se.lantz.gui;

import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;

public class MainPanel extends JPanel
{
  private JSplitPane splitPane;
  private ListPanel listPanel;

  private GameDetailsBackgroundPanel gameDetailsBackgroundPanel;
  private final MainViewModel uiModel;

  public MainPanel(final MainViewModel uiModel)
  {
    this.uiModel = uiModel;
    setLayout(new BorderLayout(0, 0));
    add(getSplitPane(), BorderLayout.CENTER);

    uiModel.addSaveChangeListener(e -> {
      listPanel.checkSaveChangeStatus();
    });

    uiModel.addRequireFieldsListener(e -> showRequiredFieldsDialog((List<String>) e.getNewValue()));
  }

  private JSplitPane getSplitPane()
  {
    if (splitPane == null)
    {
      splitPane = new JSplitPane();
      splitPane.setLeftComponent(getListPanel());
      splitPane.setRightComponent(getGameDetailsBackgroundPanel());
      splitPane.setResizeWeight(1.0);
    }
    return splitPane;
  }

  public ListPanel getListPanel()
  {
    if (listPanel == null)
    {
      listPanel = new ListPanel(this, uiModel);
    }
    return listPanel;
  }

  int showUnsavedChangesDialog()
  {
    return JOptionPane.showConfirmDialog(MainPanel.this,
                                         "Do you want to save changes for " + uiModel.getInfoModel().getTitle() + "?",
                                         "Unsaved Changes",
                                         JOptionPane.YES_NO_CANCEL_OPTION,
                                         JOptionPane.QUESTION_MESSAGE);
  }

  private void showRequiredFieldsDialog(List<String> missingFields)
  {
    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder.append("The following information needs to be added before saving:\n");
    for (String field : missingFields)
    {
      messageBuilder.append("   ");
      messageBuilder.append(field);
      messageBuilder.append("\n");
    }
    JOptionPane
      .showMessageDialog(MainPanel.this, messageBuilder, "Missing game information", JOptionPane.INFORMATION_MESSAGE);
    //Request focus to the title field
    getGameDetailsBackgroundPanel().focusTitleField();
  }

  GameDetailsBackgroundPanel getGameDetailsBackgroundPanel()
  {
    if (gameDetailsBackgroundPanel == null)
    {
      gameDetailsBackgroundPanel = new GameDetailsBackgroundPanel(uiModel);
    }
    return gameDetailsBackgroundPanel;
  }

  void initialize()
  {
    uiModel.initialize();
  }

  public void addNewGame()
  {
    getListPanel().addNewGame();
    repaintAfterModifications();
  }

  public void addNewInfoSlot()
  {
    if (uiModel.isInfoSlotAvailableForCurrentView())
    {
      int value = JOptionPane
        .showConfirmDialog(this,
                           "There is already an info slot for the current gamelist view. Do you want to add another one?",
                           "Add info slot",
                           JOptionPane.YES_NO_OPTION,
                           JOptionPane.INFORMATION_MESSAGE);
      if (value == JOptionPane.NO_OPTION)
      {
        return;
      }
    }
    getListPanel().addNewInfoSlot();
    reloadCurrentGameView();
    repaintAfterModifications();
  }

  public void deleteCurrentGame()
  {
    List<GameListData> selectedGameListData = getListPanel().getSelectedGameListData();

    if (getListPanel().getSelectedIndexInList() > -1)
    {
      int value = showDeleteDialog(selectedGameListData.size());
      if (value == JOptionPane.YES_OPTION)
      {
        int currentSelectedIndex = getListPanel().getSelectedIndexInList();
        if (selectedGameListData.size() > 1)
        {
          uiModel.deleteGames(selectedGameListData);
        }
        else
        {                
          uiModel.deleteCurrentGame();        
        }
        //Reload the current view
        reloadCurrentGameView();
        getListPanel().setSelectedIndexInList(currentSelectedIndex);
      }
    }
  }

  int showDeleteDialog(int numberOfGamesSelected)
  {
    String message = "";
    if (numberOfGamesSelected == 1)
    {
      message = "Do you want to delete " + uiModel.getInfoModel().getTitle() + " from the database?";

    }
    else
    {
      message = "Do you want to delete " + numberOfGamesSelected + " games from the database?";
    }
    if (uiModel.isNewGameSelected())
    {
      message = "Do you want to delete the new game entry?";
    }
    return JOptionPane
      .showConfirmDialog(MainPanel.this, message, "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
  }

  int showDeleteAllGamesDialog()
  {
    String message =
      "Do you want to delete all games from the database? A backup will added to the backups folder before deleting.\nCover, screenshot and game files will not be deleted.";
    return JOptionPane.showConfirmDialog(MainPanel.this,
                                         message,
                                         "Delete all games",
                                         JOptionPane.YES_NO_OPTION,
                                         JOptionPane.QUESTION_MESSAGE);
  }

  public void selectViewAfterRestore()
  {
    getListPanel().getListViewComboBox().setSelectedIndex(0);
    repaintAfterModifications();
  }

  public void repaintAfterModifications()
  {
    this.invalidate();
    this.repaint();
    getListPanel().updateViewInfoLabel();
  }

  public void clearGameListSelection()
  {
    getListPanel().clearGameListSelection();
  }

  public void toggleFavorite()
  {
    getListPanel().toggleFavorite();
  }

  public void toggleFavorite2()
  {
    getListPanel().toggleFavorite2();
  }

  public void toggleFavorite3()
  {
    getListPanel().toggleFavorite3();
  }

  public void toggleFavorite4()
  {
    getListPanel().toggleFavorite4();
  }

  public void toggleFavorite5()
  {
    getListPanel().toggleFavorite5();
  }

  public void toggleFavorite6()
  {
    getListPanel().toggleFavorite6();
  }

  public void toggleFavorite7()
  {
    getListPanel().toggleFavorite7();
  }

  public void toggleFavorite8()
  {
    getListPanel().toggleFavorite8();
  }

  public void toggleFavorite9()
  {
    getListPanel().toggleFavorite9();
  }

  public void toggleFavorite10()
  {
    getListPanel().toggleFavorite10();
  }
  
  public void setViewTag(String viewTag)
  {
    getListPanel().setViewTag(viewTag);
  }
  
  public boolean isSingleGameSelected()
  {
    return getListPanel().isSingleGameSelected();
  }
  
  public boolean isNoGameSelected()
  {
    return getListPanel().isNoGameSelected();
  }

  public void runCurrentGame()
  {
    if (getListPanel().isSingleGameSelected() && getListPanel().getSelectedIndexInList() > -1)
    {
      getGameDetailsBackgroundPanel().runCurrentGame();
    }
  }

  public void selectEnDescriptionTab()
  {
    getGameDetailsBackgroundPanel().getInfoBackgroundPanel().selectEnDescriptionTab();
  }

  public void reloadCurrentGameView()
  {
    getListPanel().reloadCurrentGameView();
  }
  
  public void updateSavedStatesTabTitle()
  {
    getGameDetailsBackgroundPanel().updateSavedStatesTabTitle();
  }
  
  public void setSelectedGameInGameList(String gameId)
  {
    getListPanel().setSelectedGameInGameList(gameId);
  }
}
