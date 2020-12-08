package se.lantz.gui.gameview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import se.lantz.model.data.GameView;
import se.lantz.model.data.ViewFilter;
import se.lantz.util.DbConstants;

public class FilterPanel extends JPanel
{
  private static final long serialVersionUID = 8720361450360137735L;

  private JPanel filterOptionPanel;
  private JRadioButton matchAllRadioButton;
  private JRadioButton anyFilterRadioButton;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JPanel tablePanel;
  private JPanel buttonPanel;
  private JButton addButton;
  private JButton removeButton;
  private JTable filterTable;
  private JScrollPane scrollPane;
  private JComboBox<String> fieldTableComboBox;
  private JComboBox<String> operatorTableComboBox;

  private String[] tableColumns = { "Field", "Operator", "Value" };
  DefaultTableModel model;
  private GameView gameView;

  public FilterPanel(GameView gameView)
  {
    this.gameView = gameView;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_filterOptionPanel = new GridBagConstraints();
    gbc_filterOptionPanel.weightx = 1.0;
    gbc_filterOptionPanel.anchor = GridBagConstraints.NORTH;
    gbc_filterOptionPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_filterOptionPanel.insets = new Insets(0, 3, 5, 3);
    gbc_filterOptionPanel.gridx = 0;
    gbc_filterOptionPanel.gridy = 0;
    add(getFilterOptionPanel(), gbc_filterOptionPanel);
    GridBagConstraints gbc_tablePanel = new GridBagConstraints();
    gbc_tablePanel.weighty = 1.0;
    gbc_tablePanel.weightx = 1.0;
    gbc_tablePanel.fill = GridBagConstraints.BOTH;
    gbc_tablePanel.gridx = 0;
    gbc_tablePanel.gridy = 1;
    add(getTablePanel(), gbc_tablePanel);
    GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
    gbc_buttonPanel.weightx = 1.0;
    gbc_buttonPanel.anchor = GridBagConstraints.NORTH;
    gbc_buttonPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_buttonPanel.gridx = 0;
    gbc_buttonPanel.gridy = 2;
    add(getButtonPanel(), gbc_buttonPanel);
    setupFromGameView();
  }

  private JPanel getFilterOptionPanel()
  {
    if (filterOptionPanel == null)
    {
      filterOptionPanel = new JPanel();
      filterOptionPanel
        .setBorder(new TitledBorder(null, "Filter matching", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_filterOptionPanel = new GridBagLayout();
      gbl_filterOptionPanel.columnWidths = new int[] { 0, 0, 0 };
      gbl_filterOptionPanel.rowHeights = new int[] { 0, 0, 0 };
      gbl_filterOptionPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
      gbl_filterOptionPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
      filterOptionPanel.setLayout(gbl_filterOptionPanel);
      GridBagConstraints gbc_matchAllRadioButton = new GridBagConstraints();
      gbc_matchAllRadioButton.anchor = GridBagConstraints.WEST;
      gbc_matchAllRadioButton.insets = new Insets(0, 0, 0, 5);
      gbc_matchAllRadioButton.gridx = 0;
      gbc_matchAllRadioButton.gridy = 0;
      filterOptionPanel.add(getMatchAllRadioButton(), gbc_matchAllRadioButton);
      GridBagConstraints gbc_anyFilterRadioButton = new GridBagConstraints();
      gbc_anyFilterRadioButton.anchor = GridBagConstraints.WEST;
      gbc_anyFilterRadioButton.gridx = 0;
      gbc_anyFilterRadioButton.gridy = 1;
      filterOptionPanel.add(getAnyFilterRadioButton(), gbc_anyFilterRadioButton);
    }
    return filterOptionPanel;
  }

  private JRadioButton getMatchAllRadioButton()
  {
    if (matchAllRadioButton == null)
    {
      matchAllRadioButton = new JRadioButton("All filters must match");
      buttonGroup.add(matchAllRadioButton);
      matchAllRadioButton.setSelected(this.gameView.isMatchAll());
    }
    return matchAllRadioButton;
  }

  private JRadioButton getAnyFilterRadioButton()
  {
    if (anyFilterRadioButton == null)
    {
      anyFilterRadioButton = new JRadioButton("Any filter must match");
      buttonGroup.add(anyFilterRadioButton);
      anyFilterRadioButton.setSelected(!this.gameView.isMatchAll());
    }
    return anyFilterRadioButton;
  }

  private JPanel getTablePanel()
  {
    if (tablePanel == null)
    {
      tablePanel = new JPanel();
      GridBagLayout gbl_tablePanel = new GridBagLayout();
      tablePanel.setLayout(gbl_tablePanel);
      GridBagConstraints gbc_scrollPane = new GridBagConstraints();
      gbc_scrollPane.weighty = 1.0;
      gbc_scrollPane.weightx = 1.0;
      gbc_scrollPane.insets = new Insets(5, 5, 0, 5);
      gbc_scrollPane.fill = GridBagConstraints.BOTH;
      gbc_scrollPane.gridx = 0;
      gbc_scrollPane.gridy = 0;
      tablePanel.add(getScrollPane(), gbc_scrollPane);
    }
    return tablePanel;
  }

  private JPanel getButtonPanel()
  {
    if (buttonPanel == null)
    {
      buttonPanel = new JPanel();
      GridBagLayout gbl_buttonPanel = new GridBagLayout();
      buttonPanel.setLayout(gbl_buttonPanel);
      GridBagConstraints gbc_addButton = new GridBagConstraints();
      gbc_addButton.anchor = GridBagConstraints.EAST;
      gbc_addButton.weightx = 1.0;
      gbc_addButton.insets = new Insets(5, 5, 5, 5);
      gbc_addButton.gridx = 0;
      gbc_addButton.gridy = 0;
      buttonPanel.add(getAddButton(), gbc_addButton);
      GridBagConstraints gbc_removeButton = new GridBagConstraints();
      gbc_removeButton.anchor = GridBagConstraints.EAST;
      gbc_removeButton.insets = new Insets(5, 0, 5, 5);
      gbc_removeButton.gridx = 1;
      gbc_removeButton.gridy = 0;
      buttonPanel.add(getRemoveButton(), gbc_removeButton);
    }
    return buttonPanel;
  }

  private JButton getAddButton()
  {
    if (addButton == null)
    {
      addButton = new JButton("Add row");
      addButton.addActionListener(e -> {
        model.addRow(new String[] { DbConstants.TITLE, ViewFilter.BEGINS_WITH_TEXT, "" });
      });
    }
    return addButton;
  }

  private JButton getRemoveButton()
  {
    if (removeButton == null)
    {
      removeButton = new JButton("Remove row");
      removeButton.addActionListener(e -> {
        model.removeRow(getFilterTable().getSelectedRow());
        if (model.getRowCount() > 0)
        {
          //Select last row
          getFilterTable().getSelectionModel().setSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
        }
      });
      removeButton.setEnabled(false);
    }
    return removeButton;
  }

  private JTable getFilterTable()
  {
    if (filterTable == null)
    {

      filterTable = new JTable();
      setupTable();
    }
    return filterTable;
  }

  private JComboBox<String> getFieldTableComboBox()
  {
    if (fieldTableComboBox == null)
    {
      fieldTableComboBox = new JComboBox<>();
      fieldTableComboBox.addItem(DbConstants.TITLE);
      fieldTableComboBox.addItem(DbConstants.AUTHOR);
      fieldTableComboBox.addItem(DbConstants.COMPOSER);
      fieldTableComboBox.addItem(DbConstants.GENRE);
      fieldTableComboBox.addItem(DbConstants.YEAR);
      fieldTableComboBox.addItem(DbConstants.DESC);
      fieldTableComboBox.addItem(DbConstants.JOY1);
      fieldTableComboBox.addItem(DbConstants.JOY2);
      fieldTableComboBox.addItem(DbConstants.SYSTEM);

      fieldTableComboBox.addActionListener(e -> {
        if (!fieldTableComboBox.getSelectedItem().equals(DbConstants.YEAR))
        {
          addStringOperators();
        }
        else
        {
          addIntOperators();
        }
      });
    }
    return fieldTableComboBox;
  }

  private void addStringOperators()
  {
    getOperatorTableComboBox().removeAllItems();
    getOperatorTableComboBox().addItem(ViewFilter.BEGINS_WITH_TEXT);
    getOperatorTableComboBox().addItem(ViewFilter.ENDS_WITH_TEXT);
    getOperatorTableComboBox().addItem(ViewFilter.CONTAINS_TEXT);
    getOperatorTableComboBox().addItem(ViewFilter.EQUALS_TEXT);
  }

  private void addIntOperators()
  {
    getOperatorTableComboBox().removeAllItems();
    getOperatorTableComboBox().addItem(ViewFilter.IS);
    getOperatorTableComboBox().addItem(ViewFilter.BEFORE);
    getOperatorTableComboBox().addItem(ViewFilter.AFTER);
  }

  private JComboBox<String> getOperatorTableComboBox()
  {
    if (operatorTableComboBox == null)
    {
      operatorTableComboBox = new JComboBox<>();
      addStringOperators();
    }
    return operatorTableComboBox;
  }

  private JScrollPane getScrollPane()
  {
    if (scrollPane == null)
    {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getFilterTable());
    }
    return scrollPane;
  }

  private void setupTable()
  {
    filterTable.putClientProperty("terminateEditOnFocusLost", true);
    model = new DefaultTableModel(tableColumns, 0)
      {
        @Override
        public void setValueAt(Object value, int row, int column)
        {
          if (column == 0)
          {
            if (!value.equals(DbConstants.YEAR) && getValueAt(row, 0).equals(DbConstants.YEAR))
            {
              setValueAt(ViewFilter.BEGINS_WITH_TEXT, row, 1);
            }
            if (value.equals(DbConstants.YEAR) && !getValueAt(row, 0).equals(DbConstants.YEAR))
            {
              setValueAt(ViewFilter.IS, row, 1);
            }
          }
          super.setValueAt(value, row, column);
        }
      };
    filterTable.setModel(model);

    TableColumn fieldColumn = filterTable.getColumnModel().getColumn(0);
    fieldColumn.setCellEditor(new DefaultCellEditor(getFieldTableComboBox()));
    TableColumn operatorColumn = filterTable.getColumnModel().getColumn(1);
    //Set operator combobox editor, updates based on selected field.
    operatorColumn.setCellEditor(new DefaultCellEditor(getOperatorTableComboBox())
      {
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column)
        {
          if (column == 1)
          {
            String field = (String) table.getModel().getValueAt(row, 0);
            if (field.equals(DbConstants.YEAR))
            {
              addIntOperators();
            }
            else
            {
              addStringOperators();
            }
          }
          return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
      });

    filterTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    filterTable.setColumnSelectionAllowed(false);
    filterTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting())
      {
        getRemoveButton().setEnabled(filterTable.getSelectedRow() > -1);
      }
    });
  }

  private void setupFromGameView()
  {
    getMatchAllRadioButton().setSelected(this.gameView.isMatchAll());

    for (ViewFilter filter : this.gameView.getViewFilters())
    {
      model.addRow(new String[] { filter.getField(), filter.getOperator(), filter.getFilterData() });
    }
  }

  public void updateGameView()
  {
    //Set fields in game view from UI
    this.gameView.setMatchAll(getMatchAllRadioButton().isSelected());
    List<ViewFilter> filterList = new ArrayList<>();
    for (int i = 0; i < model.getRowCount(); i++)
    {
      filterList.add(new ViewFilter((String) model.getValueAt(i, 0),
                                    (String) model.getValueAt(i, 1),
                                    (String) model.getValueAt(i, 2)));
    }
    this.gameView.setViewFilters(filterList);
  }
}
