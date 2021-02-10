package se.lantz.gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import se.lantz.model.MainViewModel;

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

    uiModel.addDuplicateGameListener(e -> showDuplicateDialog(e.getNewValue().toString()));
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

  private ListPanel getListPanel()
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

  private void showDuplicateDialog(String title)
  {
    JOptionPane.showMessageDialog(MainPanel.this,
                                  "A game already exists with the name \"" + title + "\". Give it another name.",
                                  "Game exists",
                                  JOptionPane.INFORMATION_MESSAGE);
    //Request focus to the title field
    getGameDetailsBackgroundPanel().focusTitleField();
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

  public void deleteCurrentGame()
  {
    if (getListPanel().getSelectedIndexInList() > -1)
    {
      int value = showDeleteDialog();
      if (value == JOptionPane.YES_OPTION)
      {
        int currentSelectedIndex = getListPanel().getSelectedIndexInList();
        uiModel.deleteCurrentGame();
        repaintAfterModifications();
        getListPanel().setSelectedIndexInList(currentSelectedIndex);
      }
    }
  }
  
  int showDeleteDialog()
  {
    String message = "Do you want to delete " + uiModel.getInfoModel().getTitle() + " from the database?";
    if (uiModel.isNewGameSelected())
    {
      message = "Do you want to delete the new game entry?";
    }
    return JOptionPane
      .showConfirmDialog(MainPanel.this, message, "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
  }
  
  int showDeleteAllGamesDialog()
  {
    String message = "Do you want to delete all games from the database? A backup will added to the backups folder before deleting.\nCover, screenshot and game files will not be deleted.";
    return  JOptionPane.showConfirmDialog(MainPanel.this,
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
  
  public void runCurrentGame()
  {
    if (getListPanel().getSelectedIndexInList() > -1)
    {
      getGameDetailsBackgroundPanel().runCurrentGame();
    }
  }
  
  public void selectEnDescriptionTab()
  {
    getGameDetailsBackgroundPanel().getInfoBackgroundPanel().selectEnDescriptionTab();
  }
}
