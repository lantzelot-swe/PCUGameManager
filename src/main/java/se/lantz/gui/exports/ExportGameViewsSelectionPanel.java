package se.lantz.gui.exports;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameView;

public class ExportGameViewsSelectionPanel extends JPanel
{
  private JButton addButton;
  private JButton removeButton;
  private JPanel listPanel;
  private JPanel buttonPanel;
  private JPanel selectedListPanel;
  private JList<GameView> gameViewList;
  private JScrollPane gameViewScrollPane;
  private JList<GameView> selectedList;
  private JScrollPane scrollPane;
  private final MainViewModel uiModel;
  DefaultListModel<GameView> selectedListModel = new DefaultListModel<>();
  private JLabel countLabel;
  private JButton exportButton;
  private JLabel infoLabel;

  public ExportGameViewsSelectionPanel(JButton exportButton)
  {
    this.exportButton = exportButton;
    uiModel = new MainViewModel();
    if (!Beans.isDesignTime())
    {
      uiModel.initialize();
    }
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
    gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);
    GridBagConstraints gbc_listPanel = new GridBagConstraints();
    gbc_listPanel.weightx = 1.0;
    gbc_listPanel.fill = GridBagConstraints.BOTH;
    gbc_listPanel.weighty = 1.0;
    gbc_listPanel.insets = new Insets(0, 0, 5, 5);
    gbc_listPanel.gridx = 0;
    gbc_listPanel.gridy = 1;
    add(getListPanel(), gbc_listPanel);
    GridBagConstraints gbc_ButtonPanel = new GridBagConstraints();
    gbc_ButtonPanel.weighty = 1.0;
    gbc_ButtonPanel.insets = new Insets(0, 0, 5, 5);
    gbc_ButtonPanel.gridx = 1;
    gbc_ButtonPanel.gridy = 1;
    add(getButtonPanel(), gbc_ButtonPanel);
    GridBagConstraints gbc_selectedListPanel = new GridBagConstraints();
    gbc_selectedListPanel.fill = GridBagConstraints.BOTH;
    gbc_selectedListPanel.weightx = 1.0;
    gbc_selectedListPanel.weighty = 1.0;
    gbc_selectedListPanel.insets = new Insets(0, 0, 5, 0);
    gbc_selectedListPanel.gridx = 2;
    gbc_selectedListPanel.gridy = 1;
    add(getSelectedListPanel(), gbc_selectedListPanel);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.anchor = GridBagConstraints.WEST;
    gbc_infoLabel.gridwidth = 3;
    gbc_infoLabel.insets = new Insets(10, 10, 5, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
  }

  private JPanel getListPanel()
  {
    if (listPanel == null)
    {
      listPanel = new JPanel();
      GridBagLayout gbl_listPanel = new GridBagLayout();
      gbl_listPanel.columnWidths = new int[] { 0, 0 };
      gbl_listPanel.rowHeights = new int[] { 0, 0 };
      gbl_listPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
      gbl_listPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
      listPanel.setLayout(gbl_listPanel);
      GridBagConstraints gbc_gameViewScrollPane = new GridBagConstraints();
      gbc_gameViewScrollPane.insets = new Insets(0, 10, 20, 10);
      gbc_gameViewScrollPane.weighty = 1.0;
      gbc_gameViewScrollPane.weightx = 1.0;
      gbc_gameViewScrollPane.fill = GridBagConstraints.BOTH;
      gbc_gameViewScrollPane.gridx = 0;
      gbc_gameViewScrollPane.gridy = 0;
      listPanel.add(getGameViewScrollPane(), gbc_gameViewScrollPane);
      listPanel.setPreferredSize(new Dimension(325, 400));
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
            List<GameView> selectedGames = getGameViewList().getSelectedValuesList();
            //Add to selected list
            for (GameView gameListData : selectedGames)
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
            List<GameView> selectedGames = getSelectedList().getSelectedValuesList();
            //Add to selected list
            for (GameView gameView : selectedGames)
            {
              selectedListModel.removeElement(gameView);
            }
            updateAfterEditingSelectedList();
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
      gbc_selectedScrollPane.insets = new Insets(0, 10, 5, 10);
      gbc_selectedScrollPane.fill = GridBagConstraints.BOTH;
      gbc_selectedScrollPane.gridx = 0;
      gbc_selectedScrollPane.gridy = 0;
      selectedListPanel.add(getScrollPane(), gbc_selectedScrollPane);
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

  private JList<GameView> getGameViewList()
  {
    if (gameViewList == null)
    {
      gameViewList = new JList<GameView>();
      gameViewList.addListSelectionListener(new ListSelectionListener()
        {
          public void valueChanged(ListSelectionEvent e)
          {
            getAddButton().setEnabled(!gameViewList.getSelectionModel().isSelectionEmpty());
          }
        });
      gameViewList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      gameViewList.setModel(uiModel.getGameViewModel());
    }
    return gameViewList;
  }

  private JScrollPane getGameViewScrollPane()
  {
    if (gameViewScrollPane == null)
    {
      gameViewScrollPane = new JScrollPane();
      gameViewScrollPane.setViewportView(getGameViewList());
    }
    return gameViewScrollPane;
  }

  private JList<GameView> getSelectedList()
  {
    if (selectedList == null)
    {
      selectedList = new JList<GameView>();
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

  private JScrollPane getScrollPane()
  {
    if (scrollPane == null)
    {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getSelectedList());
    }
    return scrollPane;
  }

  private void updateAfterEditingSelectedList()
  {
    getCountLabel().setText(Integer.toString(selectedListModel.getSize()));
    setExportButtonEnablement();
  }
  
  public void setExportButtonEnablement()
  {
    exportButton.setEnabled(selectedListModel.getSize() > 0);
  }

  private JLabel getCountLabel()
  {
    if (countLabel == null)
    {
      countLabel = new JLabel("0");
    }
    return countLabel;
  }
  
  List<GameView> getSelectedViews()
  {
    List<GameView> returnList = new ArrayList<>();
    for (int i = 0; i < selectedListModel.getSize(); i++)
    {
      returnList.add(selectedListModel.getElementAt(i));
    }
    return returnList;
  }
  private JLabel getInfoLabel() {
    if (infoLabel == null) {
    	infoLabel = new JLabel("Select gamelist views to export:");
    }
    return infoLabel;
  }
}
