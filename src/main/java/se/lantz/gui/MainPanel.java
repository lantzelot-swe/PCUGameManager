package se.lantz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import se.lantz.gui.gameview.GameViewManager;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;

public class MainPanel extends JPanel
{
  private JSplitPane splitPane;
  private JPanel listPanel;
  private JScrollPane listScrollPane;
  private JList<GameListData> list;
  private GameDetailsBackgroundPanel gameDetailsBackgroundPanel;

  private final MainViewModel uiModel;
  private JPanel listViewPanel;
  private JButton listViewEditButton;
  private JComboBox<GameView> listViewComboBox;
  private JPanel viewInfoPanel;
  private JLabel viewInfoLabel;

  private GameViewManager gameViewManager;

  public MainPanel(final MainViewModel uiModel)
  {
    this.uiModel = uiModel;
    setLayout(new BorderLayout(0, 0));
    add(getSplitPane(), BorderLayout.CENTER);
    gameViewManager = new GameViewManager(getListViewComboBox(), uiModel);

    uiModel.addSaveChangeListener(e -> {
      getListViewComboBox().setEnabled(!uiModel.isDataChanged());
      getListViewEditButton().setEnabled(!uiModel.isDataChanged());
    });

    uiModel.addDuplicateGameListener(e -> showDuplicateDialog(e.getNewValue().toString()));
    uiModel.addRequireFieldsListener(e -> showRequiredFieldsDialog((List<String>)e.getNewValue()));
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

  private JPanel getListPanel()
  {
    if (listPanel == null)
    {
      listPanel = new JPanel();
      GridBagLayout gbl_listPanel = new GridBagLayout();
      listPanel.setLayout(gbl_listPanel);
      GridBagConstraints gbc_listViewPanel = new GridBagConstraints();
      gbc_listViewPanel.weightx = 1.0;
      gbc_listViewPanel.anchor = GridBagConstraints.NORTH;
      gbc_listViewPanel.fill = GridBagConstraints.HORIZONTAL;
      gbc_listViewPanel.insets = new Insets(0, 0, 5, 0);
      gbc_listViewPanel.gridx = 0;
      gbc_listViewPanel.gridy = 0;
      listPanel.add(getListViewPanel(), gbc_listViewPanel);
      GridBagConstraints gbc_listScrollPane = new GridBagConstraints();
      gbc_listScrollPane.weighty = 1.0;
      gbc_listScrollPane.weightx = 1.0;
      gbc_listScrollPane.fill = GridBagConstraints.BOTH;
      gbc_listScrollPane.insets = new Insets(0, 5, 5, 5);
      gbc_listScrollPane.gridx = 0;
      gbc_listScrollPane.gridy = 1;
      listPanel.add(getListScrollPane(), gbc_listScrollPane);
      GridBagConstraints gbc_viewInfoPanel = new GridBagConstraints();
      gbc_viewInfoPanel.weightx = 1.0;
      gbc_viewInfoPanel.anchor = GridBagConstraints.NORTH;
      gbc_viewInfoPanel.fill = GridBagConstraints.HORIZONTAL;
      gbc_viewInfoPanel.gridx = 0;
      gbc_viewInfoPanel.gridy = 2;
      listPanel.add(getViewInfoPanel(), gbc_viewInfoPanel);
    }
    return listPanel;
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
              int value = showUnsavedChangesDialog();
              if (value == JOptionPane.YES_OPTION)
              {
                if (uiModel.saveData())
                {
                  super.setSelectionInterval(anchor, lead);
                  getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue());
                }
              }
              else if (value == JOptionPane.NO_OPTION)
              {
                super.setSelectionInterval(anchor, lead);
                getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue());
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
      list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

      list.addListSelectionListener((e) -> {
        if (e.getValueIsAdjusting() == false)
        {
          getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue());
        }
      });
      list.setModel(uiModel.getGameListModel());
    }
    return list;
  }

  int showUnsavedChangesDialog()
  {
    return JOptionPane.showConfirmDialog(MainPanel.this,
                                         "Do you want to save changes for " + list.getSelectedValue() + "?",
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

  private GameDetailsBackgroundPanel getGameDetailsBackgroundPanel()
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

  private JComboBox<GameView> getListViewComboBox()
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
            getList().setSelectedIndex(0);
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
      viewInfoPanel.add(getViewInfoLabel());
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

  public void addNewGame()
  {
    //Add new entry and select in the list
    uiModel.addNewGameListData();
    int rowToSelect = getList().getModel().getSize() - 1;
    getList().setSelectionInterval(rowToSelect, rowToSelect);
    getList().ensureIndexIsVisible(rowToSelect);
    getGameDetailsBackgroundPanel().updateSelectedGame(list.getSelectedValue());
    getGameDetailsBackgroundPanel().focusTitleField();
  }
}
