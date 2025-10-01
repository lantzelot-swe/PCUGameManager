package se.lantz.gui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.ComboPopup;

import org.jdesktop.swingx.JXSearchField;

import se.lantz.gui.carousel.CarouselPreviewDialog;
import se.lantz.gui.gameview.GameViewManager;
import se.lantz.gui.menu.InsetsMenuItem;
import se.lantz.model.GameListModel;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;
import se.lantz.util.FileManager;

public class ListPanel extends JPanel
{
  private JScrollPane listScrollPane;
  private JList<GameListData> list;
  private JPanel listViewPanel;
  private JButton listViewEditButton;
  private JComboBox<GameView> listViewComboBox;
  private JPanel viewInfoPanel;
  private JLabel viewInfoLabel;
  private JLabel tooManyGamesInfoLabel;
  private JButton previewButton;
  private GameViewManager gameViewManager;
  private MainViewModel uiModel;
  private MainPanel mainPanel;
  private JXSearchField filterTextField;

  private boolean delayDetailsUpdate = false;
  private boolean pageButtonPressed = false;

  private CarouselPreviewDialog carouselPreviewDialog;

  private boolean filterEnabled = true;

  private boolean isFiltering = false;
  private ListDataListener listDataListener = new ListDataListener()
    {

      @Override
      public void intervalAdded(ListDataEvent e)
      {
        triggerListFiltering();
      }

      @Override
      public void intervalRemoved(ListDataEvent e)
      {
        triggerListFiltering();
      }

      @Override
      public void contentsChanged(ListDataEvent e)
      {
        triggerListFiltering();
      }

    };

  public ListPanel(final MainPanel mainPanel, final MainViewModel uiModel)
  {
    this.mainPanel = mainPanel;
    this.uiModel = uiModel;
    gameViewManager = new GameViewManager(this, uiModel);

    GridBagLayout gbl_listPanel = new GridBagLayout();
    setLayout(gbl_listPanel);
    GridBagConstraints gbc_listViewPanel = new GridBagConstraints();
    gbc_listViewPanel.weightx = 1.0;
    gbc_listViewPanel.anchor = GridBagConstraints.NORTH;
    gbc_listViewPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_listViewPanel.insets = new Insets(0, 0, 5, 0);
    gbc_listViewPanel.gridx = 0;
    gbc_listViewPanel.gridy = 0;
    add(getListViewPanel(), gbc_listViewPanel);
    GridBagConstraints gbc_FilterConstraints = new GridBagConstraints();
    gbc_FilterConstraints.weighty = 0.0;
    gbc_FilterConstraints.weightx = 1.0;
    gbc_FilterConstraints.fill = GridBagConstraints.HORIZONTAL;
    gbc_FilterConstraints.insets = new Insets(0, 5, 5, 5);
    gbc_FilterConstraints.gridx = 0;
    gbc_FilterConstraints.gridy = 1;
    add(getFilterTextField(), gbc_FilterConstraints);

    GridBagConstraints gbc_listScrollPane = new GridBagConstraints();
    gbc_listScrollPane.weighty = 1.0;
    gbc_listScrollPane.weightx = 1.0;
    gbc_listScrollPane.fill = GridBagConstraints.BOTH;
    gbc_listScrollPane.insets = new Insets(0, 5, 5, 5);
    gbc_listScrollPane.gridx = 0;
    gbc_listScrollPane.gridy = 2;
    add(getListScrollPane(), gbc_listScrollPane);
    GridBagConstraints gbc_viewInfoPanel = new GridBagConstraints();
    gbc_viewInfoPanel.weightx = 1.0;
    gbc_viewInfoPanel.anchor = GridBagConstraints.NORTH;
    gbc_viewInfoPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_viewInfoPanel.gridx = 0;
    gbc_viewInfoPanel.gridy = 3;
    add(getViewInfoPanel(), gbc_viewInfoPanel);

    uiModel.addSaveChangeListener(e -> {

      previewButton.setEnabled(!uiModel.isNewGameSelected());
    });
  }

  private JPanel getListViewPanel()
  {
    if (listViewPanel == null)
    {
      listViewPanel = new JPanel();
      GridBagLayout gbl_listViewPanel = new GridBagLayout();
      gbl_listViewPanel.columnWidths = new int[] { 0, 0, 0 };
      gbl_listViewPanel.rowHeights = new int[] { 0, 0 };
      gbl_listViewPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
      gbl_listViewPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
      listViewPanel.setLayout(gbl_listViewPanel);
      GridBagConstraints gbc_listViewEditButton = new GridBagConstraints();
      gbc_listViewEditButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_listViewEditButton.insets = new Insets(9, 5, 5, 0);
      gbc_listViewEditButton.gridx = 0;
      gbc_listViewEditButton.gridy = 0;
      listViewPanel.add(getListViewEditButton(), gbc_listViewEditButton);
      GridBagConstraints gbc_listViewComboBox = new GridBagConstraints();
      gbc_listViewComboBox.anchor = GridBagConstraints.NORTHWEST;
      gbc_listViewComboBox.weightx = 1.0;
      gbc_listViewComboBox.insets = new Insets(10, 5, 5, 5);
      gbc_listViewComboBox.fill = GridBagConstraints.HORIZONTAL;
      gbc_listViewComboBox.gridx = 1;
      gbc_listViewComboBox.gridy = 0;
      listViewPanel.add(getListViewComboBox(), gbc_listViewComboBox);
      
      GridBagConstraints gbc_tooManyGamesLabel = new GridBagConstraints();
      gbc_tooManyGamesLabel.weightx = 1.0;
      gbc_tooManyGamesLabel.gridwidth = 2;
      gbc_tooManyGamesLabel.insets = new Insets(2, 5, 2, 5);
      gbc_tooManyGamesLabel.anchor = GridBagConstraints.WEST;
      gbc_tooManyGamesLabel.fill = GridBagConstraints.HORIZONTAL;
      gbc_tooManyGamesLabel.gridx = 0;
      gbc_tooManyGamesLabel.gridy = 1;
      listViewPanel.add(getTooManyGamesInfoLabel(), gbc_tooManyGamesLabel);
    }
    return listViewPanel;
  }

  private JXSearchField getFilterTextField()
  {
    if (filterTextField == null)
    {
      filterTextField = new JXSearchField();
      String tooltipText =
        "<html>Type to search on game title in<br>the current gamelist view.<p><br><u>Special tags</u><br><b>a:</b> - match Author<br><b>c:</b> - match Composer<br><b>y:</b> - match Year<br><b>v:</b> - match View tag<br><b>s:</b> - match System config<p><br>Use ',' as separator to<br>match several tags.<p>Example: <i>a:imagine,c:martin galway</i><br></html>";
      filterTextField.setToolTipText(tooltipText);
      //Add delay to not filter too quickly while typing, for better performance
      filterTextField.setInstantSearchDelay(350);
      filterTextField.addActionListener(e -> performFiltering());
    }
    return filterTextField;
  }

  private void performFiltering()
  {
    if (filterEnabled)
    {
      isFiltering = true;
      GameListModel listModel = uiModel.getGameListModel();
      GameListData selectedGame = list.getSelectedValue();
      listModel.clear();
      String filterText = filterTextField.getText();
      List<GameListData> filteredList = new ArrayList<>();
      for (GameListData item : listModel.getCurrentGameList())
      {
        if (listModel.filterMatch(item, filterText))
        {
          filteredList.add(item);
        }
      }
      //Add all at once (for performance!)
      listModel.addAll(filteredList);
      if (filteredList.contains(selectedGame))
      {
        list.setSelectedValue(selectedGame, true);
      }
      else if (!filteredList.isEmpty())
      {
        list.setSelectedIndex(0);
        list.ensureIndexIsVisible(0);
      }
      updateViewInfoLabel();
      isFiltering = false;
    }
  }

  public void clearFilter()
  {
    getFilterTextField().setText("");
  }

  private void triggerListFiltering()
  {
    if (!isFiltering)
    {
      performFiltering();
    }
  }

  private JButton getListViewEditButton()
  {
    if (listViewEditButton == null)
    {
      listViewEditButton = new JButton("...");
      listViewEditButton.setMargin(new Insets(1, 1, 1, 1));

      listViewEditButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            JPopupMenu menu = new JPopupMenu();
            InsetsMenuItem addItem = new InsetsMenuItem("Add gamelist view...");
            addItem.addActionListener(e -> gameViewManager.openViewEditDialog(new GameView(0)));
            menu.add(addItem);
            int gameListViewId = ((GameView) getListViewComboBox().getSelectedItem()).getGameViewId();
            if (gameListViewId > 0)
            {
              InsetsMenuItem editItem = new InsetsMenuItem("Edit gamelist view...");
              editItem.addActionListener(e -> gameViewManager
                .openViewEditDialog((GameView) getListViewComboBox().getSelectedItem()));
              menu.add(editItem);
              InsetsMenuItem deleteItem = new InsetsMenuItem("Delete gamelist view...");
              deleteItem
                .addActionListener(e -> gameViewManager.deleteView((GameView) getListViewComboBox().getSelectedItem()));
              menu.add(deleteItem);
            }
            else if (gameListViewId < -1)
            {
              InsetsMenuItem renameItem = new InsetsMenuItem("Rename gamelist view...");
              renameItem.addActionListener(e -> gameViewManager
                .renameFavoritesView((GameView) getListViewComboBox().getSelectedItem()));
              menu.add(renameItem);
            }

            menu.show(listViewEditButton, 15, 15);

          }
        });

      listViewEditButton.setToolTipText("<html>Manage gamelist views</html>");
    }
    return listViewEditButton;
  }

  public JComboBox<GameView> getListViewComboBox()
  {
    if (listViewComboBox == null)
    {
      listViewComboBox = new JComboBox<>();
      listViewComboBox.addItemListener(event -> {
        if (event.getStateChange() == ItemEvent.SELECTED)
        {
          ComboPopup popup = (ComboPopup) listViewComboBox.getUI().getAccessibleChild(listViewComboBox, 0);
          if (!popup.isVisible())
          {
            setSelectedGameView();
          }
        }
      });

      listViewComboBox.setModel(uiModel.getGameViewModel());
      listViewComboBox.setRenderer(new GameListDataRenderer(uiModel.getSavedStatesManager(),
                                                            getFilterTextField(),
                                                            uiModel.getGameListModel()));
      listViewComboBox.addPopupMenuListener(new PopupMenuListener()
        {
          @Override
          public void popupMenuWillBecomeVisible(PopupMenuEvent e)
          {
            ComboPopup popup = (ComboPopup) listViewComboBox.getUI().getAccessibleChild(listViewComboBox, 0);
            //Each row is 14 pixels
            int height = listViewComboBox.getModel().getSize() * 14 + 6;
            ((JComponent) popup)
              .setPreferredSize(new Dimension(listViewComboBox.getSize().width, Math.min(height, 700)));
            ((JComponent) popup).setLayout(new GridLayout(1, 1));
          }

          @Override
          public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
          {
            setSelectedGameView();
          }

          @Override
          public void popupMenuCanceled(PopupMenuEvent e)
          {
            //Empty
          }
        });
    }

    return listViewComboBox;
  }

  private void setSelectedGameView()
  {
    if (!uiModel.isDisableChangeNotifcation())
    {
      getList().clearSelection();
      GameView selectedItem = (GameView) listViewComboBox.getSelectedItem();
      uiModel.setSelectedGameView(selectedItem);
      if (selectedItem.getGameViewId() != -1 && selectedItem.getGameCount() < selectedItem.getFileCount())
      {
        listViewComboBox.setToolTipText("(Number of games/Number of game files incl. extra disks)");
      }
      else
      {
        listViewComboBox.setToolTipText(null);
      }
      SwingUtilities.invokeLater(() -> {
        updateViewInfoLabel();
        getList().setSelectedIndex(0);
        getList().ensureIndexIsVisible(0);
        getList().requestFocusInWindow();
      });
    }
  }

  List<GameListData> getSelectedGameListData()
  {
    return getList().getSelectedValuesList();
  }

  boolean isSingleGameSelected()
  {
    return getList().getSelectedIndices().length == 1;
  }

  boolean isNoGameSelected()
  {
    return getList().getSelectedIndices().length == 0;
  }

  int getSelectedIndexInList()
  {
    return getList().getSelectedIndex();
  }

  void setSelectedIndexInList(int index)
  {
    int indexToSelect = index;
    if (index >= uiModel.getGameListModel().getSize())
    {
      indexToSelect = uiModel.getGameListModel().getSize() - 1;
    }
    list.setSelectionInterval(indexToSelect, indexToSelect);
    updateSelectedGame();
    list.ensureIndexIsVisible(indexToSelect);
  }

  public void setSelectedGameInGameList(String gameId)
  {
    //Called from Carousel, make sure no filtering is active
    getFilterTextField().setText("");

    List<GameListData> currentGameList = uiModel.getGameListModel().getCurrentGameList();
    for (int i = 0; i < currentGameList.size(); i++)
    {
      if (currentGameList.get(i).getGameId().equals(gameId))
      {
        int indexToSelect = i;
        if (i >= uiModel.getGameListModel().getSize())
        {
          indexToSelect = uiModel.getGameListModel().getSize() - 1;
        }
        list.getSelectionModel().setValueIsAdjusting(false);
        list.setSelectedIndex(indexToSelect);
        list.ensureIndexIsVisible(indexToSelect);
        break;
      }
    }
  }

  public void clearGameListSelection()
  {
    list.clearSelection();
  }

  private JPanel getViewInfoPanel()
  {
    if (viewInfoPanel == null)
    {
      viewInfoPanel = new JPanel();
      GridBagLayout gbl_viewInfoPanel = new GridBagLayout();
      viewInfoPanel.setLayout(gbl_viewInfoPanel);
      
      GridBagConstraints gbc_viewInfoLabel = new GridBagConstraints();
      gbc_viewInfoLabel.weightx = 1.0;
      gbc_viewInfoLabel.insets = new Insets(0, 0, 5, 5);
      gbc_viewInfoLabel.anchor = GridBagConstraints.EAST;
      gbc_viewInfoLabel.gridx = 1;
      gbc_viewInfoLabel.gridy = 1;
      viewInfoPanel.add(getViewInfoLabel(), gbc_viewInfoLabel);

      GridBagConstraints gbc_carouselPreviewButton = new GridBagConstraints();
      gbc_carouselPreviewButton.weightx = 0.0;
      gbc_carouselPreviewButton.insets = new Insets(3, 5, 5, 5);
      gbc_carouselPreviewButton.anchor = GridBagConstraints.WEST;
      gbc_carouselPreviewButton.gridx = 0;
      gbc_carouselPreviewButton.gridy = 1;
      viewInfoPanel.add(getPreviewButton(), gbc_carouselPreviewButton);
    }
    return viewInfoPanel;
  }

  private JLabel getViewInfoLabel()
  {
    if (viewInfoLabel == null)
    {
      viewInfoLabel = new JLabel("125 of 1000");
    }
    return viewInfoLabel;
  }
  
  private JLabel getTooManyGamesInfoLabel()
  {
    if (tooManyGamesInfoLabel == null)
    {
      tooManyGamesInfoLabel = new JLabel("<html>The gamelist view contains more than 255 games. Only 255 games can be shown in the carousel or added to a folder in the file loader.</html>");
      tooManyGamesInfoLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
    }
    return tooManyGamesInfoLabel;
  }
  
  private void showTooManyGamesLabel(boolean visible)
  {
    getTooManyGamesInfoLabel().setVisible(visible);
  }

  private JButton getPreviewButton()
  {
    if (previewButton == null)
    {
      previewButton = new JButton("Carousel preview");
      previewButton.addActionListener(e -> showCarouselPreview());
    }
    return previewButton;
  }

  private JScrollPane getListScrollPane()
  {
    if (listScrollPane == null)
    {
      listScrollPane = new JScrollPane(getList());
      listScrollPane.setMinimumSize(new Dimension(100, 23));
    }
    return listScrollPane;
  }

  private JList<GameListData> getList()
  {
    if (list == null)
    {
      //Override setSelectionInterval to only allow changing game if no changes needs saving
      list = new JList<GameListData>()
        {
          @Override
          public void ensureIndexIsVisible(int index)
          {
            //Don't allow scrolling to any other index if unsaved data
            if (!uiModel.isDataChanged())
            {
              super.ensureIndexIsVisible(index);
            }
          }

          @Override
          public void setSelectedIndex(int index)
          {
            //Don't allow changing selection if unsaved data
            if (!uiModel.isDataChanged())
            {
              super.setSelectedIndex(index);
            }
          }

          @Override
          public void removeSelectionInterval(int start, int end)
          {
            //Don't allow clearing selection if unsaved data
            if (!uiModel.isDataChanged())
            {
              super.removeSelectionInterval(start, end);
            }
          }

          @Override
          public void addSelectionInterval(int anchor, int lead)
          {
            if (!uiModel.isDataChanged())
            {
              super.addSelectionInterval(anchor, lead);
            }
          }

          @Override
          public void setSelectionInterval(int anchor, int lead)
          {
            if (!uiModel.isDataChanged())
            {
              super.setSelectionInterval(anchor, lead);
            }
            else
            {
              if (list.getSelectedIndex() == anchor)
              {
                //Clicked on the selected game, just ignore
                return;
              }
              //Just ignore
              int value = mainPanel.showUnsavedChangesDialog();
              if (value == JOptionPane.YES_OPTION)
              {
                if (uiModel.saveData())
                {
                  super.setSelectionInterval(anchor, lead);
                  mainPanel.getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue());
                }
              }
              else if (value == JOptionPane.NO_OPTION)
              {
                super.setSelectionInterval(anchor, lead);
                mainPanel.getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue());
                uiModel.removeNewGameListData();
                mainPanel.repaintAfterModifications();
              }
              else
              {
                //Set focus from list so that it is not possible to change game with keyboard
                getViewInfoPanel().requestFocus();
              }
            }
          }
        };
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

      //If the user holds down "down" or "up" (scrolling in the list) the details is not
      //updated until the key is released
      list.addKeyListener(new KeyAdapter()
        {

          @Override
          public void keyTyped(KeyEvent e)
          {
            if (uiModel.isDataChanged())
            {
              e.consume();
              return;
            }
          }

          public void keyPressed(KeyEvent e)
          {
            if (uiModel.isDataChanged())
            {
              e.consume();
              return;
            }

            if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP)
            {
              delayDetailsUpdate = true;
            }
            else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN || e.getKeyCode() == KeyEvent.VK_PAGE_UP)
            {
              pageButtonPressed = true;
            }
          }

          public void keyReleased(KeyEvent e)
          {
            if (uiModel.isDataChanged())
            {
              e.consume();
              return;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP)
            {
              delayDetailsUpdate = false;
              updateSelectedGame();
            }
            else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN || e.getKeyCode() == KeyEvent.VK_PAGE_UP)
            {
              pageButtonPressed = false;
            }
          }
        });
      list.addMouseListener(new MouseAdapter()
        {

          @Override
          public void mouseClicked(MouseEvent e)
          {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 &&
              list.getSelectionModel().getSelectedItemsCount() == 1)
            {
              //trigger run game...
              MainWindow.getInstance().getMainPanel().runCurrentGame();
            }
          }

        });
      list.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting() || pageButtonPressed)
        {
          updateViewInfoLabel();
          if (!delayDetailsUpdate && !uiModel.isDisableChangeNotifcation())
          {
            updateSelectedGame();
          }
        }
      });
      list.setModel(uiModel.getGameListModel());
      list.setCellRenderer(new GameListDataRenderer(uiModel.getSavedStatesManager(),
                                                    getFilterTextField(),
                                                    uiModel.getGameListModel()));
      //Add listener to filter on changes
      uiModel.getGameListModel().addListDataListener(listDataListener);
      //Remove from tootlipManager to avoid throwing a nullpointer for CTRL+F1
      ToolTipManager.sharedInstance().unregisterComponent(list);
    }
    return list;
  }

  public void updateViewInfoLabel()
  {
    String text = uiModel.getGameListModel().getSize() + " of " + uiModel.getAllGamesCount();
    int selectedGames = list.getSelectedIndices().length;
    if (selectedGames > 1)
    {
      int selectedFileCount = uiModel.getFileCount(list.getSelectedValuesList());
      text = text + " (" + selectedGames + "/" + selectedFileCount + ")";
    }
    getViewInfoLabel().setText(text);
    showTooManyGamesLabel(uiModel.getSelectedGameView().getGameViewId() != -1 && uiModel.getSelectedGameView().getGameCount() > 255);
  }

  private void updateSelectedGame()
  {
    SwingUtilities.invokeLater(() -> {
      boolean singelSelected = list.getSelectionModel().getSelectedItemsCount() == 1;
      mainPanel.getGameDetailsBackgroundPanel().updateSelectedGame(singelSelected ? list.getSelectedValue() : null);
    });
  }

  void checkSaveChangeStatus()
  {
    getListViewComboBox().setEnabled(!uiModel.isDataChanged());
    getListViewEditButton().setEnabled(!uiModel.isDataChanged());
    if (!getFilterTextField().hasFocus())
    {
      getFilterTextField().setEnabled(!uiModel.isDataChanged());
    }
    filterEnabled = !uiModel.isDataChanged();
  }

  public void addNewGame()
  {
    getFilterTextField().setText("");
    filterEnabled = false;
    //Add new entry and select in the list
    uiModel.addNewGameListData();
    int rowToSelect = getList().getModel().getSize() - 1;
    getList().setSelectionInterval(rowToSelect, rowToSelect);
    getList().ensureIndexIsVisible(rowToSelect);
    mainPanel.getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue());
    mainPanel.getGameDetailsBackgroundPanel().focusTitleField();
    getFilterTextField().setEnabled(false);
  }

  public void addNewInfoSlot()
  {
    filterEnabled = false;
    //Add new entry and select in the list
    uiModel.addNewInfoSlotData();
    int rowToSelect = getList().getModel().getSize() - 1;
    getList().setSelectionInterval(rowToSelect, rowToSelect);
    getList().ensureIndexIsVisible(rowToSelect);
    mainPanel.getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue());
    mainPanel.getGameDetailsBackgroundPanel().focusTitleField();
  }

  public void toggleFavorite()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void toggleFavorite2()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite2(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void toggleFavorite3()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite3(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void toggleFavorite4()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite4(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void toggleFavorite5()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite5(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void toggleFavorite6()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite6(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void toggleFavorite7()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite7(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void toggleFavorite8()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite8(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void toggleFavorite9()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite9(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void toggleFavorite10()
  {
    if (!uiModel.isDataChanged())
    {
      uiModel.toggleFavorite10(list.getSelectedValuesList(), false);
      mainPanel.repaintAfterModifications();
    }
  }

  public void setViewTag(String viewTag)
  {
    if (!uiModel.isDataChanged())
    {
      int[] selectedIndices = list.getSelectionModel().getSelectedIndices();
      for (GameListData glData : list.getSelectedValuesList())
      {
        if (!glData.isInfoSlot())
        {
          uiModel.setViewTag(glData, viewTag);
        }
      }
      //Trigger a reload of current game view
      uiModel.reloadCurrentGameView();
      list.setSelectedIndices(selectedIndices);
      mainPanel.repaintAfterModifications();
    }
  }

  public void reloadCurrentGameView()
  {
    GameListData selectedData = getList().getSelectedValue();
    getList().clearSelection();
    uiModel.reloadCurrentGameView();
    SwingUtilities.invokeLater(() -> {
      getList().setSelectedValue(selectedData, true);
    });
  }

  public void showCarouselPreview()
  {
    if (this.uiModel.getCurrentGameViewGameCount() < 10)
    {
      String message = "You can only preview the Carousel for gamelists that contain a minimum of 10 games.";
      JOptionPane
        .showMessageDialog(MainWindow.getInstance(), message, "Carousel preview", JOptionPane.INFORMATION_MESSAGE);
    }
    else
    {
      if (carouselPreviewDialog == null || !carouselPreviewDialog.isShowing())
      {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        GraphicsDevice mainWindowDevice = MainWindow.getInstance().getGraphicsConfiguration().getDevice();
        GraphicsDevice secondaryDevice = null;
        for (GraphicsDevice graphicsDevice : screenDevices)
        {
          if (!graphicsDevice.equals(mainWindowDevice))
          {
            secondaryDevice = graphicsDevice;
          }
        }
        if (secondaryDevice == null)
        {
          secondaryDevice = mainWindowDevice;
        }

        GraphicsConfiguration graphicsConfigurations = secondaryDevice.getDefaultConfiguration();
        Rectangle graphicsBounds = graphicsConfigurations.getBounds();

        if (FileManager.isLaunchCarouselPreviewInFullscreen())
        {
          carouselPreviewDialog = new CarouselPreviewDialog(MainWindow.getInstance(), this.uiModel, true);
          carouselPreviewDialog.setBounds(graphicsBounds);
          carouselPreviewDialog.setUndecorated(true);
        }
        else
        {
          carouselPreviewDialog = new CarouselPreviewDialog(MainWindow.getInstance(), this.uiModel, false);
          carouselPreviewDialog.pack();
          //Position centred over secondary monitor
          carouselPreviewDialog
            .setLocation(((graphicsBounds.width / 2) - (carouselPreviewDialog.getSize().width / 2)) + graphicsBounds.x,
                         ((graphicsBounds.height / 2) - (carouselPreviewDialog.getSize().height / 2)) +
                           graphicsBounds.y);

        }
        carouselPreviewDialog.showDialog();
      }
      else
      {
        carouselPreviewDialog.requestFocus();
      }
    }
  }

  public void hideCarouselPreview()
  {
    if (carouselPreviewDialog != null && carouselPreviewDialog.isShowing())
    {
      carouselPreviewDialog.dispose();
    }
  }
}
