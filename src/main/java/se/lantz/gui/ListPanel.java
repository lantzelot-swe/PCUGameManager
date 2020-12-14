package se.lantz.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import se.lantz.gui.gameview.GameViewManager;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;

public class ListPanel extends JPanel
{
  private JPanel listPanel;
  private JScrollPane listScrollPane;
  private JList<GameListData> list;
  private JPanel listViewPanel;
  private JButton listViewEditButton;
  private JComboBox<GameView> listViewComboBox;
  private JPanel viewInfoPanel;
  private JLabel viewInfoLabel;
  private GameViewManager gameViewManager;
  private MainViewModel uiModel;
  private MainPanel mainPanel;

  private boolean delayDetailsUpdate = false;
  private boolean pageButtonPressed = false;

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
    GridBagConstraints gbc_listScrollPane = new GridBagConstraints();
    gbc_listScrollPane.weighty = 1.0;
    gbc_listScrollPane.weightx = 1.0;
    gbc_listScrollPane.fill = GridBagConstraints.BOTH;
    gbc_listScrollPane.insets = new Insets(0, 5, 5, 5);
    gbc_listScrollPane.gridx = 0;
    gbc_listScrollPane.gridy = 1;
    add(getListScrollPane(), gbc_listScrollPane);
    GridBagConstraints gbc_viewInfoPanel = new GridBagConstraints();
    gbc_viewInfoPanel.weightx = 1.0;
    gbc_viewInfoPanel.anchor = GridBagConstraints.NORTH;
    gbc_viewInfoPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_viewInfoPanel.gridx = 0;
    gbc_viewInfoPanel.gridy = 2;
    add(getViewInfoPanel(), gbc_viewInfoPanel);
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
    }
    return listViewPanel;
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
            JMenuItem addItem = new JMenuItem("Add view...");
            addItem.addActionListener(e -> gameViewManager.openViewEditDialog(new GameView(0)));
            menu.add(addItem);
            if (((GameView) getListViewComboBox().getSelectedItem()).getGameViewId() > 0)
            {
              JMenuItem editItem = new JMenuItem("Edit view...");
              editItem.addActionListener(e -> gameViewManager
                .openViewEditDialog((GameView) getListViewComboBox().getSelectedItem()));
              menu.add(editItem);
            }

            menu.show(listViewEditButton, 15, 15);

          }
        });
    }
    return listViewEditButton;
  }

  public JComboBox<GameView> getListViewComboBox()
  {
    if (listViewComboBox == null)
    {
      listViewComboBox = new JComboBox<>();
      listViewComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            uiModel.setSelectedGameView((GameView) listViewComboBox.getSelectedItem());
            //TODO: keep track of selected index for the view and select it once data is updated
            updateViewInfoLabel();
            SwingUtilities.invokeLater(() -> {
              getList().setSelectedIndex(0);
              getList().ensureIndexIsVisible(0);
            });
          }
        });
      listViewComboBox.setModel(uiModel.getGameViewModel());
    }
    return listViewComboBox;
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
      gbc_viewInfoLabel.gridy = 0;
      viewInfoPanel.add(getViewInfoLabel(), gbc_viewInfoLabel);
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
          public void setSelectionInterval(int anchor, int lead)
          {
            if (!uiModel.isDataChanged())
            {
              super.setSelectionInterval(anchor, lead);
            }
            else
            {
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
              }
              else
              {
                //Set focus from list so that it is not possible to change game with keyboard
                getViewInfoPanel().requestFocus();
              }
            }
          }
        };
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      //If the user holds down "down" or "up" (scrolling in the list) the details is not
      //updated until the key is released
      list.addKeyListener(new KeyAdapter()
        {

          public void keyPressed(KeyEvent e)
          {
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

      list.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting() || pageButtonPressed)
        {
          if (!delayDetailsUpdate)
          {
            updateSelectedGame();
          }
        }
      });
      list.setModel(uiModel.getGameListModel());
    }
    return list;
  }

  public void updateViewInfoLabel()
  {
    getViewInfoLabel().setText(uiModel.getGameListModel().getSize() + " of " + uiModel.getAllGamesCount());
  }

  private void updateSelectedGame()
  {
    SwingUtilities
      .invokeLater(() -> mainPanel.getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue()));
  }

  void checkSaveChangeStatus()
  {
    getListViewComboBox().setEnabled(!uiModel.isDataChanged());
    getListViewEditButton().setEnabled(!uiModel.isDataChanged());
  }

  public void addNewGame()
  {
    //Add new entry and select in the list
    uiModel.addNewGameListData();
    int rowToSelect = getList().getModel().getSize() - 1;
    getList().setSelectionInterval(rowToSelect, rowToSelect);
    getList().ensureIndexIsVisible(rowToSelect);
    mainPanel.getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue());
    mainPanel.getGameDetailsBackgroundPanel().focusTitleField();
  }
}