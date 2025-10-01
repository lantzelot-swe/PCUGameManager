package se.lantz.gui.exports;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.lantz.model.GameListModel;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;
import se.lantz.util.GameListDataComparator;

public class ExportGamesSelectionPanel extends JPanel
{
  private static final int MAX_GAMES = 255;
  private JPanel listPanel;
  private JPanel buttonPanel;
  private JButton addButton;
  private JButton removeButton;
  private JPanel selectedListPanel;
  private JComboBox<GameView> gameViewComboBox;
  private JScrollPane listScrollPane;
  private JList<GameListData> list;
  private JList<GameListData> selectedList;
  private final MainViewModel uiModel;
  private JScrollPane selectedScrollPane;
  GameListModel selectedListModel = new GameListModel();
  private JLabel InfoLabel;
  private JLabel warningLabel;
  private JLabel countLabel;
  private JButton exportButton;
  private ExportGameViewsSelectionPanel exportGameViewsSelectionPanel;
  private boolean carouselMode;

  public ExportGamesSelectionPanel(JButton exportButton, String selectedDatabase, boolean carouselMode)
  {
    this.exportButton = exportButton;
    this.carouselMode = carouselMode;
    exportButton.setEnabled(false);
    uiModel = new MainViewModel(selectedDatabase);
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_listPanel = new GridBagConstraints();
    gbc_listPanel.weightx = 0.5;
    gbc_listPanel.weighty = 1.0;
    gbc_listPanel.insets = new Insets(0, 0, 5, 5);
    gbc_listPanel.fill = GridBagConstraints.BOTH;
    gbc_listPanel.gridx = 0;
    gbc_listPanel.gridy = 1;
    add(getListPanel(), gbc_listPanel);
    GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
    gbc_buttonPanel.weighty = 1.0;
    gbc_buttonPanel.insets = new Insets(0, 0, 5, 5);
    gbc_buttonPanel.fill = GridBagConstraints.BOTH;
    gbc_buttonPanel.gridx = 1;
    gbc_buttonPanel.gridy = 1;
    add(getButtonPanel(), gbc_buttonPanel);
    GridBagConstraints gbc_selectedListPanel = new GridBagConstraints();
    gbc_selectedListPanel.insets = new Insets(0, 0, 5, 0);
    gbc_selectedListPanel.weightx = 0.5;
    gbc_selectedListPanel.weighty = 1.0;
    gbc_selectedListPanel.fill = GridBagConstraints.BOTH;
    gbc_selectedListPanel.gridx = 2;
    gbc_selectedListPanel.gridy = 1;
    add(getSelectedListPanel(), gbc_selectedListPanel);
    GridBagConstraints gbc_InfoLabel = new GridBagConstraints();
    gbc_InfoLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_InfoLabel.gridwidth = 3;
    gbc_InfoLabel.insets = new Insets(10, 10, 5, 10);
    gbc_InfoLabel.gridx = 0;
    gbc_InfoLabel.gridy = 0;
    add(getInfoLabel(), gbc_InfoLabel);
    GridBagConstraints gbc_warningLabel = new GridBagConstraints();
    gbc_warningLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_warningLabel.gridwidth = 3;
    gbc_warningLabel.insets = new Insets(0, 10, 5, 10);
    gbc_warningLabel.gridx = 0;
    gbc_warningLabel.gridy = 2;
    add(getWarningLabel(), gbc_warningLabel);
    if (!Beans.isDesignTime())
    {
      uiModel.initialize();
    }
  }

  private JPanel getListPanel()
  {
    if (listPanel == null)
    {
      listPanel = new JPanel();
      GridBagLayout gbl_listPanel = new GridBagLayout();
      listPanel.setLayout(gbl_listPanel);
      GridBagConstraints gbc_comboBox = new GridBagConstraints();
      gbc_comboBox.weightx = 1.0;
      gbc_comboBox.insets = new Insets(0, 10, 5, 10);
      gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
      gbc_comboBox.gridx = 0;
      gbc_comboBox.gridy = 0;
      listPanel.add(getComboBox(), gbc_comboBox);
      GridBagConstraints gbc_listScrollPane = new GridBagConstraints();
      gbc_listScrollPane.weightx = 1.0;
      gbc_listScrollPane.weighty = 1.0;
      gbc_listScrollPane.insets = new Insets(0, 10, 20, 10);
      gbc_listScrollPane.fill = GridBagConstraints.BOTH;
      gbc_listScrollPane.gridx = 0;
      gbc_listScrollPane.gridy = 1;
      listPanel.setPreferredSize(new Dimension(325, 370));
      listPanel.add(getListScrollPane(), gbc_listScrollPane);
    }
    return listPanel;
  }

  private JPanel getButtonPanel()
  {
    if (buttonPanel == null)
    {
      buttonPanel = new JPanel();
      GridBagLayout gbl_buttonPanel = new GridBagLayout();
      buttonPanel.setLayout(gbl_buttonPanel);
      GridBagConstraints gbc_addButton = new GridBagConstraints();
      gbc_addButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_addButton.gridx = 0;
      gbc_addButton.gridy = 0;
      buttonPanel.add(getAddButton(), gbc_addButton);
      GridBagConstraints gbc_removeButton = new GridBagConstraints();
      gbc_removeButton.insets = new Insets(10, 0, 0, 0);
      gbc_removeButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_removeButton.gridx = 0;
      gbc_removeButton.gridy = 1;
      buttonPanel.add(getRemoveButton(), gbc_removeButton);
    }
    return buttonPanel;
  }

  private JButton getAddButton()
  {
    if (addButton == null)
    {
      addButton = new JButton("");
      addButton.setIcon(new ImageIcon(this.getClass().getResource("/se/lantz/arrow-right.png")));
      addButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            List<GameListData> selectedGames = getList().getSelectedValuesList();
            //Add to selected list
            for (GameListData gameListData : selectedGames)
            {
              if (!selectedListModel.contains(gameListData))
              {
                selectedListModel.addElement(gameListData);
              }
            }
            updateAfterEditingSelectedList();
          }
        });
      addButton.setEnabled(false);
    }
    return addButton;
  }

  private JButton getRemoveButton()
  {
    if (removeButton == null)
    {
      removeButton = new JButton("");
      removeButton.setIcon(new ImageIcon(this.getClass().getResource("/se/lantz/arrow-left.png")));
      removeButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            List<GameListData> selectedGames = getSelectedList().getSelectedValuesList();
            //Add to selected list
            for (GameListData gameListData : selectedGames)
            {
              selectedListModel.removeElement(gameListData);
            }
            updateAfterEditingSelectedList();
          }
        });
      removeButton.setEnabled(false);
    }
    return removeButton;
  }

  private void updateAfterEditingSelectedList()
  {
    sortSelectedList();
    getWarningLabel().setVisible(selectedListModel.getSize() > MAX_GAMES);
    setExportButtonEnablement();
    getCountLabel().setText(Integer.toString(selectedListModel.getSize()));
  }

  public void setExportButtonEnablement()
  {
    exportButton.setEnabled(selectedListModel.getSize() > 0);
  }

  private JPanel getSelectedListPanel()
  {
    if (selectedListPanel == null)
    {
      selectedListPanel = new JPanel();
      GridBagLayout gbl_selectedListPanel = new GridBagLayout();
      selectedListPanel.setLayout(gbl_selectedListPanel);
      GridBagConstraints gbc_selectedScrollPane = new GridBagConstraints();
      gbc_selectedScrollPane.weightx = 1.0;
      gbc_selectedScrollPane.weighty = 1.0;
      gbc_selectedScrollPane.insets = new Insets(0, 10, 5, 10);
      gbc_selectedScrollPane.fill = GridBagConstraints.BOTH;
      gbc_selectedScrollPane.gridx = 0;
      gbc_selectedScrollPane.gridy = 0;
      selectedListPanel.add(getSelectedScrollPane(), gbc_selectedScrollPane);
      GridBagConstraints gbc_countLabel = new GridBagConstraints();
      gbc_countLabel.insets = new Insets(0, 0, 0, 10);
      gbc_countLabel.anchor = GridBagConstraints.EAST;
      gbc_countLabel.gridx = 0;
      gbc_countLabel.gridy = 1;
      selectedListPanel.setPreferredSize(new Dimension(325, 370));
      selectedListPanel.add(getCountLabel(), gbc_countLabel);
    }
    return selectedListPanel;
  }

  private JComboBox getComboBox()
  {
    if (gameViewComboBox == null)
    {
      gameViewComboBox = new JComboBox<>();
      gameViewComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            uiModel.setSelectedGameView((GameView) gameViewComboBox.getSelectedItem());
            getList().setSelectedIndex(0);
          }
        });

      gameViewComboBox.setModel(uiModel.getGameViewModel());
    }
    return gameViewComboBox;
  }

  private JScrollPane getListScrollPane()
  {
    if (listScrollPane == null)
    {
      listScrollPane = new JScrollPane(getList());
    }
    return listScrollPane;
  }

  private JList<GameListData> getList()
  {
    if (list == null)
    {
      //Override setSelectionInterval to only allow changing game if no changes needs saving
      list = new JList<GameListData>();
      list.addListSelectionListener(new ListSelectionListener()
        {
          public void valueChanged(ListSelectionEvent e)
          {
            getAddButton().setEnabled(!list.getSelectionModel().isSelectionEmpty());
          }
        });
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      list.setModel(uiModel.getGameListModel());
    }
    return list;
  }

  private JScrollPane getSelectedScrollPane()
  {
    if (selectedScrollPane == null)
    {
      selectedScrollPane = new JScrollPane(getSelectedList());
    }
    return selectedScrollPane;
  }

  private JList<GameListData> getSelectedList()
  {
    if (selectedList == null)
    {
      //Override setSelectionInterval to only allow changing game if no changes needs saving
      selectedList = new JList<GameListData>();
      selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      selectedList.setModel(selectedListModel);
      selectedList.addListSelectionListener(new ListSelectionListener()
        {
          public void valueChanged(ListSelectionEvent e)
          {
            getRemoveButton().setEnabled(!selectedList.getSelectionModel().isSelectionEmpty());
          }
        });
    }
    return selectedList;
  }

  private JLabel getInfoLabel()
  {
    if (InfoLabel == null)
    {
      InfoLabel = new JLabel("Select games to export:");
    }
    return InfoLabel;
  }

  private void sortSelectedList()
  {
    List<GameListData> gamesList = Collections.list(selectedListModel.elements()); // get a collection of the elements in the model
    Collections.sort(gamesList, new GameListDataComparator());
    selectedListModel.clear(); // remove all elements
    for (GameListData o : gamesList)
    {
      selectedListModel.addElement(o);
    }
  }

  private JLabel getWarningLabel()
  {
    if (warningLabel == null)
    {
      String text = "";
      if (carouselMode)
      {
        text = "The game carousel only support " + MAX_GAMES +
          " games in total. Are you sure you want to export more than that?";
      }
      else
      {
        text = "The file loader folders only support " + MAX_GAMES +
          " games in total. Are you sure you want to export more than that?";
      }
      warningLabel = new JLabel(text);
      warningLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
      warningLabel.setVisible(false);
    }
    return warningLabel;
  }

  List<GameListData> getSelectedGames()
  {
    List<GameListData> returnList = new ArrayList<>();
    for (int i = 0; i < selectedListModel.getSize(); i++)
    {
      returnList.add(selectedListModel.getElementAt(i));
    }
    return returnList;
  }

  private JLabel getCountLabel()
  {
    if (countLabel == null)
    {
      countLabel = new JLabel("0");
    }
    return countLabel;
  }
}
