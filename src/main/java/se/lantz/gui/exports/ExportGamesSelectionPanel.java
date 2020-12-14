package se.lantz.gui.exports;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class ExportGamesSelectionPanel extends JPanel
{
  private static final int _MAX_GAMES = 226;
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
  private JPanel formatPanel;
  private JLabel formatInfoLabel;
  private JRadioButton maxiFormatRadioButton;
  private JRadioButton favFormatRadioButton;
  private final ButtonGroup formatGroup = new ButtonGroup();

  public ExportGamesSelectionPanel()
  {
    uiModel = new MainViewModel();
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
    GridBagConstraints gbc_formatPanel = new GridBagConstraints();
    gbc_formatPanel.anchor = GridBagConstraints.NORTH;
    gbc_formatPanel.gridwidth = 3;
    gbc_formatPanel.insets = new Insets(0, 0, 0, 5);
    gbc_formatPanel.fill = GridBagConstraints.BOTH;
    gbc_formatPanel.gridx = 0;
    gbc_formatPanel.gridy = 3;
    add(getFormatPanel(), gbc_formatPanel);
    uiModel.initialize();
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
      listPanel.setPreferredSize(new Dimension(325, 400));
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
      addButton = new JButton("->");
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
            sortSelectedList();
            getWarningLabel().setVisible(selectedListModel.getSize() > 226);
            getCountLabel().setText(Integer.toString(selectedListModel.getSize()));
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
      removeButton = new JButton("<-");
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
            sortSelectedList();
            getWarningLabel().setVisible(selectedListModel.getSize() > _MAX_GAMES);
            getCountLabel().setText(Integer.toString(selectedListModel.getSize()));
          }
        });
      removeButton.setEnabled(false);
    }
    return removeButton;
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
      gbc_selectedScrollPane.insets = new Insets(5, 10, 5, 10);
      gbc_selectedScrollPane.fill = GridBagConstraints.BOTH;
      gbc_selectedScrollPane.gridx = 0;
      gbc_selectedScrollPane.gridy = 0;
      selectedListPanel.add(getSelectedScrollPane(), gbc_selectedScrollPane);
      GridBagConstraints gbc_countLabel = new GridBagConstraints();
      gbc_countLabel.insets = new Insets(0, 0, 0, 10);
      gbc_countLabel.anchor = GridBagConstraints.EAST;
      gbc_countLabel.gridx = 0;
      gbc_countLabel.gridy = 1;
      selectedListPanel.setPreferredSize(new Dimension(325, 400));
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
      String text = "Select which games to export.";
      InfoLabel = new JLabel("Select games to export.");
    }
    return InfoLabel;
  }

  private void sortSelectedList()
  {
    List<GameListData> gamesList = Collections.list(selectedListModel.elements()); // get a collection of the elements in the model
    Collections.sort(gamesList);
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
      warningLabel = new JLabel("The game carousel only support " + _MAX_GAMES +
        " games in total. Are you sure you want to export more than that?");
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
  
  boolean isFavFormat()
  {
    return getFavFormatRadioButton().isSelected();
  }

  private JLabel getCountLabel()
  {
    if (countLabel == null)
    {
      countLabel = new JLabel("0");
    }
    return countLabel;
  }
  private JPanel getFormatPanel() {
    if (formatPanel == null) {
    	formatPanel = new JPanel();
    	GridBagLayout gbl_formatPanel = new GridBagLayout();
    	formatPanel.setLayout(gbl_formatPanel);
    	GridBagConstraints gbc_formatInfoLabel = new GridBagConstraints();
    	gbc_formatInfoLabel.weightx = 1.0;
    	gbc_formatInfoLabel.anchor = GridBagConstraints.WEST;
    	gbc_formatInfoLabel.insets = new Insets(10, 10, 5, 5);
    	gbc_formatInfoLabel.gridx = 0;
    	gbc_formatInfoLabel.gridy = 0;
    	formatPanel.add(getFormatInfoLabel(), gbc_formatInfoLabel);
    	GridBagConstraints gbc_maxiFormatRadioButton = new GridBagConstraints();
    	gbc_maxiFormatRadioButton.weightx = 1.0;
    	gbc_maxiFormatRadioButton.anchor = GridBagConstraints.WEST;
    	gbc_maxiFormatRadioButton.insets = new Insets(0, 5, 0, 0);
    	gbc_maxiFormatRadioButton.gridx = 0;
    	gbc_maxiFormatRadioButton.gridy = 1;
    	formatPanel.add(getMaxiFormatRadioButton(), gbc_maxiFormatRadioButton);
    	GridBagConstraints gbc_favFormatRadioButton = new GridBagConstraints();
    	gbc_favFormatRadioButton.weighty = 1.0;
    	gbc_favFormatRadioButton.weightx = 1.0;
    	gbc_favFormatRadioButton.insets = new Insets(0, 5, 5, 0);
    	gbc_favFormatRadioButton.anchor = GridBagConstraints.NORTHWEST;
    	gbc_favFormatRadioButton.gridx = 0;
    	gbc_favFormatRadioButton.gridy = 2;
    	formatPanel.add(getFavFormatRadioButton(), gbc_favFormatRadioButton);
    }
    return formatPanel;
  }
  private JLabel getFormatInfoLabel() {
    if (formatInfoLabel == null) {
    	formatInfoLabel = new JLabel("Select which format to export to:");
    }
    return formatInfoLabel;
  }
  private JRadioButton getMaxiFormatRadioButton() {
    if (maxiFormatRadioButton == null) {
    	maxiFormatRadioButton = new JRadioButton("Separate folders for covers, screens and games (supported by the Maxi game tool)");
    	maxiFormatRadioButton.setSelected(true);
    	formatGroup.add(maxiFormatRadioButton);
    }
    return maxiFormatRadioButton;
  }
  private JRadioButton getFavFormatRadioButton() {
    if (favFormatRadioButton == null) {
    	favFormatRadioButton = new JRadioButton("Separate folder for each game");
    	formatGroup.add(favFormatRadioButton);
    }
    return favFormatRadioButton;
  }
}