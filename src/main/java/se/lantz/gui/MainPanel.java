package se.lantz.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import se.lantz.gui.menu.InsetsMenuItem;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.util.ExceptionHandler;

public class MainPanel extends JPanel
{
  private JSplitPane splitPane;
  private ListPanel listPanel;
  private DraggableTabbedPane tabbedPane;

  private GameDetailsBackgroundPanel gameDetailsBackgroundPanel;
  private final MainViewModel uiModel;

  private int previouslySelectedIndex = 0;
  private boolean ignoreTabChange = false;

  public MainPanel(final MainViewModel uiModel)
  {
    this.uiModel = uiModel;
    setLayout(new BorderLayout(0, 0));
    add(getTabbedPane(), BorderLayout.CENTER);

    uiModel.addSaveChangeListener(e -> {
      listPanel.checkSaveChangeStatus();
      setEnablementOfTabs();
    });

    uiModel.addRequiredFieldsListener(e -> showRequiredFieldsDialog((List<String>) e.getNewValue()));
  }

  private DraggableTabbedPane getTabbedPane()
  {
    if (tabbedPane == null)
    {
      tabbedPane = new DraggableTabbedPane();

      for (int i = 0; i < uiModel.getAvailableDatabases().size(); i++)
      {
        if (i == 0)
        {
          tabbedPane.addTab(uiModel.getAvailableDatabases().get(i), getNewContentPanel(getSplitPane()));
        }
        else
        {
          tabbedPane.addTab(uiModel.getAvailableDatabases().get(i), getNewContentPanel(null));
        }
      }
      tabbedPane.addTab(" + ", getNewContentPanel(null));

      //Update previous selected index once a tab is dragged to a new position
      tabbedPane.addTabDraggedListener(e -> previouslySelectedIndex = e.getID());
      //Update preferences when the tab positions changes
      tabbedPane.addTabStructureChangedListener(e -> updateTabOrderPreferences());

      tabbedPane.addChangeListener(e -> {
        if (!tabbedPane.isRemovingTab() && !tabbedPane.isDragging())
        {
          if (tabbedPane.getSelectedIndex() == uiModel.getAvailableDatabases().size())
          {
            ignoreTabChange = true;
            tabbedPane.setSelectedIndex(previouslySelectedIndex);
            ignoreTabChange = false;
            String name = JOptionPane
              .showInputDialog(this, "Enter database name", "Create new database", JOptionPane.QUESTION_MESSAGE);
            createNewTab(name);
            return;
          }
          else if (ignoreTabChange)
          {
            //Do nothing
            return;
          }

          MainWindow.getInstance().setWaitCursor(true);
          int selectedIndex = tabbedPane.getSelectedIndex();

          ((JPanel) tabbedPane.getComponentAt(previouslySelectedIndex)).removeAll();
          ((JPanel) tabbedPane.getComponentAt(selectedIndex)).add(getSplitPane(), BorderLayout.CENTER);
          previouslySelectedIndex = tabbedPane.getSelectedIndex();

          uiModel.setCurrentDatabase(tabbedPane.getTitleAt(selectedIndex));
          MainWindow.getInstance().setWaitCursor(false);
          invalidate();
          repaint();
        }
      });

      tabbedPane.addMouseListener(new MouseAdapter()
        {

          @Override
          public void mouseClicked(MouseEvent e)
          {
            if (SwingUtilities.isRightMouseButton(e))
            {
              int tabNumber = tabbedPane.getUI().tabForCoordinate(tabbedPane, e.getX(), 10);

              if (tabNumber > -1 && (tabNumber < tabbedPane.getTabCount() - 1))
              {
                JPopupMenu menu = new JPopupMenu();
                InsetsMenuItem renameTabItem = new InsetsMenuItem("Rename database");
                renameTabItem.addActionListener(ev -> renameTab(tabNumber));
                menu.add(renameTabItem);
                if (tabbedPane.getTabCount() > 2)
                {
                  InsetsMenuItem deleteTabItem = new InsetsMenuItem("Delete database");
                  deleteTabItem.addActionListener(ev -> deleteTab(tabNumber));
                  menu.add(deleteTabItem);
                }
                menu.show(tabbedPane, e.getX(), e.getY());
              }
            }
          }
        });
    }
    return tabbedPane;
  }

  private void setEnablementOfTabs()
  {
    tabbedPane.setEnabled(!uiModel.isDataChanged());
  }

  private void renameTab(int tabIndex)
  {
    String oldName = tabbedPane.getTitleAt(tabIndex);
    String newName = (String) JOptionPane.showInputDialog(this,
                                                          "Enter new name for \"" + oldName + "\"",
                                                          "Rename database",
                                                          JOptionPane.QUESTION_MESSAGE,
                                                          null,
                                                          null,
                                                          oldName);
    if (checkDbName(newName))
    {
      try
      {
        uiModel.renameTab(oldName, newName);
        tabbedPane.setTitleAt(tabIndex, newName);
        updateTabOrderPreferences();
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not rename database");
      }
    }
  }

  private void deleteTab(int tabIndex)
  {
    String selectedTab = tabbedPane.getTitleAt(tabIndex);
    int answer = JOptionPane.showConfirmDialog(this,
                                               "Are you sure you want to delete the " + selectedTab +
                                                 " database?\nIt will be removed completely from the databases folder.",
                                               "Delete database",
                                               JOptionPane.YES_NO_OPTION);
    if (answer == JOptionPane.YES_OPTION)
    {
      try
      {
        uiModel.deleteTab(selectedTab);
        tabbedPane.removeTabAt(tabIndex);
        updateTabOrderPreferences();
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not delete database");
      }
    }
  }

  private void createNewTab(String name)
  {
    if (checkDbName(name))
    {
      try
      {
        uiModel.addTab(name);
        tabbedPane.insertTab(name, null, getNewContentPanel(null), null, tabbedPane.getTabCount() - 1);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 2);
        updateTabOrderPreferences();
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not create database");
      }
    }
  }

  private boolean checkDbName(String name)
  {
    if (name == null || name.isEmpty() || name.isBlank())
    {
      //Do nothing
      return false;
    }
    List<String> lowerCaseNames =
      uiModel.getAvailableDatabases().stream().map(x -> x.toLowerCase()).collect(Collectors.toList());
    if (lowerCaseNames.contains(name.toLowerCase().trim()))
    {
      JOptionPane.showMessageDialog(this,
                                    "A database with name " + name.trim() + " already exists",
                                    "Duplicate db name",
                                    JOptionPane.INFORMATION_MESSAGE);
      return false;
    }
    return true;
  }

  private void updateTabOrderPreferences()
  {
    List<String> tabTitles = new ArrayList<>();
    for (int i = 0; i < tabbedPane.getTabCount() - 1; i++)
    {
      tabTitles.add(tabbedPane.getTitleAt(i));
    }
    uiModel.updateDbTabPreferences(String.join(",", tabTitles));
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

  private JPanel getNewContentPanel(JComponent content)
  {
    JPanel panel = new JPanel(new BorderLayout());
    if (content != null)
    {
      panel.add(content, BorderLayout.CENTER);
    }
    return panel;
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
