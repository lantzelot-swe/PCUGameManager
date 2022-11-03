package se.lantz.gui.gameview;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import se.lantz.model.data.GameView;
import se.lantz.model.data.ViewFilter;
import se.lantz.util.DbConstants;

public class FilterPanel extends JPanel
{
  private static final long serialVersionUID = 8720361450360137735L;
  private JPanel orTablePanel;
  private JPanel orButtonPanel;
  private JButton orAddButton;
  private JButton orRemoveButton;
  private JTable orTable;
  private JScrollPane orScrollPane;
  private JComboBox<String> fieldTableComboBox;
  private JComboBox<String> operatorTableComboBox;

  private String[] tableColumns = { "Field", "Operator", "Value" };
  DefaultTableModel andModel;
  DefaultTableModel orModel;
  private GameView gameView;
  private JLabel orInfoLabel;
  private JPanel andTablePanel;
  private JLabel andInfoLabel;
  private JTable andTable;
  private JScrollPane andScrollPane;
  private JPanel andButtonPanel;
  private JButton andAddButton;
  private JButton andRemoveButton;

  public FilterPanel(GameView gameView)
  {
    this.gameView = gameView;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_andTablePanel = new GridBagConstraints();
    gbc_andTablePanel.weighty = 0.5;
    gbc_andTablePanel.insets = new Insets(0, 0, 5, 0);
    gbc_andTablePanel.fill = GridBagConstraints.BOTH;
    gbc_andTablePanel.gridx = 0;
    gbc_andTablePanel.gridy = 0;
    add(getAndTablePanel(), gbc_andTablePanel);
    GridBagConstraints gbc_andButtonPanel = new GridBagConstraints();
    gbc_andButtonPanel.insets = new Insets(0, 0, 5, 0);
    gbc_andButtonPanel.fill = GridBagConstraints.BOTH;
    gbc_andButtonPanel.gridx = 0;
    gbc_andButtonPanel.gridy = 1;
    add(getAndButtonPanel(), gbc_andButtonPanel);
    GridBagConstraints gbc_orTablePanel = new GridBagConstraints();
    gbc_orTablePanel.insets = new Insets(0, 0, 5, 0);
    gbc_orTablePanel.weighty = 0.5;
    gbc_orTablePanel.weightx = 1.0;
    gbc_orTablePanel.fill = GridBagConstraints.BOTH;
    gbc_orTablePanel.gridx = 0;
    gbc_orTablePanel.gridy = 2;
    add(getOrTablePanel(), gbc_orTablePanel);
    GridBagConstraints gbc_orButtonPanel = new GridBagConstraints();
    gbc_orButtonPanel.weightx = 1.0;
    gbc_orButtonPanel.anchor = GridBagConstraints.NORTH;
    gbc_orButtonPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_orButtonPanel.gridx = 0;
    gbc_orButtonPanel.gridy = 3;
    add(getOrButtonPanel(), gbc_orButtonPanel);
    setupFromGameView();
  }

  private JPanel getOrTablePanel()
  {
    if (orTablePanel == null)
    {
      orTablePanel = new JPanel();
      GridBagLayout gbl_orTablePanel = new GridBagLayout();
      orTablePanel.setLayout(gbl_orTablePanel);
      GridBagConstraints gbc_orInfoLabel = new GridBagConstraints();
      gbc_orInfoLabel.anchor = GridBagConstraints.WEST;
      gbc_orInfoLabel.insets = new Insets(5, 5, 0, 0);
      gbc_orInfoLabel.gridx = 0;
      gbc_orInfoLabel.gridy = 0;
      orTablePanel.add(getOrInfoLabel(), gbc_orInfoLabel);
      GridBagConstraints gbc_orScrollPane = new GridBagConstraints();
      gbc_orScrollPane.weighty = 1.0;
      gbc_orScrollPane.weightx = 1.0;
      gbc_orScrollPane.insets = new Insets(0, 5, 0, 5);
      gbc_orScrollPane.fill = GridBagConstraints.BOTH;
      gbc_orScrollPane.gridx = 0;
      gbc_orScrollPane.gridy = 1;
      orTablePanel.add(getOrScrollPane(), gbc_orScrollPane);
    }
    return orTablePanel;
  }

  private JPanel getOrButtonPanel()
  {
    if (orButtonPanel == null)
    {
      orButtonPanel = new JPanel();
      GridBagLayout gbl_orButtonPanel = new GridBagLayout();
      orButtonPanel.setLayout(gbl_orButtonPanel);
      GridBagConstraints gbc_orAddButton = new GridBagConstraints();
      gbc_orAddButton.anchor = GridBagConstraints.EAST;
      gbc_orAddButton.weightx = 1.0;
      gbc_orAddButton.insets = new Insets(0, 5, 5, 5);
      gbc_orAddButton.gridx = 0;
      gbc_orAddButton.gridy = 0;
      orButtonPanel.add(getOrAddButton(), gbc_orAddButton);
      GridBagConstraints gbc_orRemoveButton = new GridBagConstraints();
      gbc_orRemoveButton.anchor = GridBagConstraints.EAST;
      gbc_orRemoveButton.insets = new Insets(0, 0, 5, 5);
      gbc_orRemoveButton.gridx = 1;
      gbc_orRemoveButton.gridy = 0;
      orButtonPanel.add(getOrRemoveButton(), gbc_orRemoveButton);
    }
    return orButtonPanel;
  }

  private JButton getOrAddButton()
  {
    if (orAddButton == null)
    {
      orAddButton = new JButton("Add row");
      orAddButton.addActionListener(e -> {
        orModel.addRow(new String[] { DbConstants.TITLE, ViewFilter.BEGINS_WITH_TEXT, "" });
      });
    }
    return orAddButton;
  }

  private JButton getOrRemoveButton()
  {
    if (orRemoveButton == null)
    {
      orRemoveButton = new JButton("Remove row");
      orRemoveButton.addActionListener(e -> {
        orModel.removeRow(getOrTable().getSelectedRow());
        if (orModel.getRowCount() > 0)
        {
          //Select last row
          getOrTable().getSelectionModel().setSelectionInterval(orModel.getRowCount() - 1, orModel.getRowCount() - 1);
        }
      });
      orRemoveButton.setEnabled(false);
    }
    return orRemoveButton;
  }

  private JTable getOrTable()
  {
    if (orTable == null)
    {

      orTable = new JTable();
      setupTable(orTable, false);
    }
    return orTable;
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
      fieldTableComboBox.addItem(DbConstants.DESC_DE);
      fieldTableComboBox.addItem(DbConstants.DESC_ES);
      fieldTableComboBox.addItem(DbConstants.DESC_FR);
      fieldTableComboBox.addItem(DbConstants.DESC_IT);
      fieldTableComboBox.addItem(DbConstants.JOY1);
      fieldTableComboBox.addItem(DbConstants.JOY2);
      fieldTableComboBox.addItem(DbConstants.SYSTEM);
      fieldTableComboBox.addItem(DbConstants.FAVORITE);
      fieldTableComboBox.addItem(DbConstants.GAME);
      fieldTableComboBox.addItem(DbConstants.VIEW_TAG);
      fieldTableComboBox.addItem(DbConstants.DISK_2);
      fieldTableComboBox.addItem(DbConstants.DISK_3);
      fieldTableComboBox.addItem(DbConstants.DISK_4);
      fieldTableComboBox.addItem(DbConstants.DISK_5);
      fieldTableComboBox.addItem(DbConstants.DISK_6);

      fieldTableComboBox.addActionListener(e -> {

        if (fieldTableComboBox.getSelectedItem().equals(DbConstants.FAVORITE))
        {
          addBooleanOperators();
        }
        else if (fieldTableComboBox.getSelectedItem().equals(DbConstants.YEAR))
        {
          addIntOperators();
        }
        else if (fieldTableComboBox.getSelectedItem().equals(DbConstants.DISK_2) ||
          fieldTableComboBox.getSelectedItem().equals(DbConstants.DISK_3) ||
          fieldTableComboBox.getSelectedItem().equals(DbConstants.DISK_4) ||
          fieldTableComboBox.getSelectedItem().equals(DbConstants.DISK_5) ||
          fieldTableComboBox.getSelectedItem().equals(DbConstants.DISK_6))
        {
          addDiskOperators();
        }
        else
        {
          addStringOperators();
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
    getOperatorTableComboBox().addItem(ViewFilter.NOT_CONTAINS_TEXT);
    getOperatorTableComboBox().addItem(ViewFilter.EQUALS_TEXT);
    getOperatorTableComboBox().addItem(ViewFilter.EMPTY);
    getOperatorTableComboBox().addItem(ViewFilter.NOT_EMPTY);
  }

  private void addDiskOperators()
  {
    getOperatorTableComboBox().removeAllItems();
    getOperatorTableComboBox().addItem(ViewFilter.EMPTY);
    getOperatorTableComboBox().addItem(ViewFilter.NOT_EMPTY);
  }

  private void addIntOperators()
  {
    getOperatorTableComboBox().removeAllItems();
    getOperatorTableComboBox().addItem(ViewFilter.IS);
    getOperatorTableComboBox().addItem(ViewFilter.BEFORE);
    getOperatorTableComboBox().addItem(ViewFilter.AFTER);
  }

  private void addBooleanOperators()
  {
    getOperatorTableComboBox().removeAllItems();
    getOperatorTableComboBox().addItem(ViewFilter.IS);
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

  private JScrollPane getOrScrollPane()
  {
    if (orScrollPane == null)
    {
      orScrollPane = new JScrollPane();
      orScrollPane.setViewportView(getOrTable());
    }
    return orScrollPane;
  }

  private void setupTable(JTable table, boolean andTable)
  {
    table.putClientProperty("terminateEditOnFocusLost", true);
    DefaultTableModel model = new DefaultTableModel(tableColumns, 0)
      {
        @Override
        public void setValueAt(Object value, int row, int column)
        {
          if (column == 0)
          {
            if (value.equals(DbConstants.FAVORITE))
            {
              setValueAt(ViewFilter.IS, row, 1);
              if (!getValueAt(row, 2).equals("false"))
              {
                setValueAt("true", row, 2);
              }
            }
            else if (value.equals(DbConstants.YEAR))
            {
              setValueAt(ViewFilter.IS, row, 1);
              try
              {
                Integer.parseInt((String) getValueAt(row, 2));
              }
              catch (NumberFormatException e)
              {
                setValueAt("1986", row, 2);
              }
            }
            else if (getValueAt(row, 0).equals(DbConstants.YEAR) || getValueAt(row, 0).equals(DbConstants.FAVORITE))
            {
              setValueAt(ViewFilter.BEGINS_WITH_TEXT, row, 1);
            }
            else if (value.equals(DbConstants.DISK_2) || value.equals(DbConstants.DISK_3) ||
              value.equals(DbConstants.DISK_4) || value.equals(DbConstants.DISK_5) || value.equals(DbConstants.DISK_6))
            {
              setValueAt(ViewFilter.NOT_EMPTY, row, 1);
              setValueAt("", row, 2);
            }
          }
          else if (column == 1)
          {
            if (value.equals(ViewFilter.EMPTY) || value.equals(ViewFilter.NOT_EMPTY))
            {
              setValueAt("", row, 2);
            }
          }
          super.setValueAt(value, row, column);
        }
      };
    table.setModel(model);
    if (andTable)
    {
      this.andModel = model;
    }
    else
    {
      this.orModel = model;
    }

    TableColumn fieldColumn = table.getColumnModel().getColumn(0);
    fieldColumn.setCellEditor(new DefaultCellEditor(getFieldTableComboBox()));
    TableColumn operatorColumn = table.getColumnModel().getColumn(1);
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
            if (field.equals(DbConstants.FAVORITE))
            {
              addBooleanOperators();
            }
            else if (field.equals(DbConstants.YEAR))
            {
              addIntOperators();
            }
            else if (field.equals(DbConstants.DISK_2) || field.equals(DbConstants.DISK_3) ||
              field.equals(DbConstants.DISK_4) || field.equals(DbConstants.DISK_5) || field.equals(DbConstants.DISK_6))
            {
              addDiskOperators();
            }
            else
            {
              addStringOperators();
            }
          }
          return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
      });

    TableColumn valueColumn = table.getColumnModel().getColumn(2);
    valueColumn.setCellEditor(new ValueCellEditor());

    table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setColumnSelectionAllowed(false);
    table.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting())
      {
        if (andTable)
        {
          getAndRemoveButton().setEnabled(table.getSelectedRow() > -1);
        }
        else
        {
          getOrRemoveButton().setEnabled(table.getSelectedRow() > -1);
        }
      }
    });
  }

  private void setupFromGameView()
  {
    for (ViewFilter filter : this.gameView.getViewFilters())
    {
      if (filter.isAndOperator())
      {
        andModel.addRow(new String[] { filter.getField(), filter.getOperator(), filter.getFilterData() });
      }
      else
      {
        orModel.addRow(new String[] { filter.getField(), filter.getOperator(), filter.getFilterData() });
      }
    }
  }

  public void updateGameView()
  {
    //Set fields in game view from UI
    List<ViewFilter> filterList = new ArrayList<>();
    for (int i = 0; i < andModel.getRowCount(); i++)
    {
      filterList.add(new ViewFilter((String) andModel.getValueAt(i, 0),
                                    (String) andModel.getValueAt(i, 1),
                                    (String) andModel.getValueAt(i, 2),
                                    true));
    }
    for (int i = 0; i < orModel.getRowCount(); i++)
    {
      filterList.add(new ViewFilter((String) orModel.getValueAt(i, 0),
                                    (String) orModel.getValueAt(i, 1),
                                    (String) orModel.getValueAt(i, 2),
                                    false));
    }
    this.gameView.setViewFilters(filterList);
  }

  private JLabel getOrInfoLabel()
  {
    if (orInfoLabel == null)
    {
      orInfoLabel = new JLabel("Any filter must match:");
    }
    return orInfoLabel;
  }

  private JPanel getAndTablePanel()
  {
    if (andTablePanel == null)
    {
      andTablePanel = new JPanel();
      GridBagLayout gbl_andTablePanel = new GridBagLayout();
      andTablePanel.setLayout(gbl_andTablePanel);
      GridBagConstraints gbc_andInfoLabel = new GridBagConstraints();
      gbc_andInfoLabel.anchor = GridBagConstraints.WEST;
      gbc_andInfoLabel.insets = new Insets(5, 5, 0, 0);
      gbc_andInfoLabel.gridx = 0;
      gbc_andInfoLabel.gridy = 0;
      andTablePanel.add(getAndInfoLabel(), gbc_andInfoLabel);
      GridBagConstraints gbc_andScrollPane = new GridBagConstraints();
      gbc_andScrollPane.insets = new Insets(0, 5, 0, 5);
      gbc_andScrollPane.weighty = 1.0;
      gbc_andScrollPane.weightx = 1.0;
      gbc_andScrollPane.fill = GridBagConstraints.BOTH;
      gbc_andScrollPane.gridx = 0;
      gbc_andScrollPane.gridy = 1;
      andTablePanel.add(getAndScrollPane(), gbc_andScrollPane);
    }
    return andTablePanel;
  }

  private JLabel getAndInfoLabel()
  {
    if (andInfoLabel == null)
    {
      andInfoLabel = new JLabel("All filters must match:");
    }
    return andInfoLabel;
  }

  private JTable getAndTable()
  {
    if (andTable == null)
    {
      andTable = new JTable();
      setupTable(andTable, true);
    }
    return andTable;
  }

  private JScrollPane getAndScrollPane()
  {
    if (andScrollPane == null)
    {
      andScrollPane = new JScrollPane();
      andScrollPane.setViewportView(getAndTable());
    }
    return andScrollPane;
  }

  private JPanel getAndButtonPanel()
  {
    if (andButtonPanel == null)
    {
      andButtonPanel = new JPanel();
      GridBagLayout gbl_andButtonPanel = new GridBagLayout();
      andButtonPanel.setLayout(gbl_andButtonPanel);
      GridBagConstraints gbc_andAddButton = new GridBagConstraints();
      gbc_andAddButton.weightx = 1.0;
      gbc_andAddButton.anchor = GridBagConstraints.EAST;
      gbc_andAddButton.insets = new Insets(0, 5, 5, 5);
      gbc_andAddButton.gridx = 0;
      gbc_andAddButton.gridy = 0;
      andButtonPanel.add(getAndAddButton(), gbc_andAddButton);
      GridBagConstraints gbc_andRemoveButton = new GridBagConstraints();
      gbc_andRemoveButton.insets = new Insets(0, 0, 5, 5);
      gbc_andRemoveButton.anchor = GridBagConstraints.EAST;
      gbc_andRemoveButton.gridx = 1;
      gbc_andRemoveButton.gridy = 0;
      andButtonPanel.add(getAndRemoveButton(), gbc_andRemoveButton);
    }
    return andButtonPanel;
  }

  private JButton getAndAddButton()
  {
    if (andAddButton == null)
    {
      andAddButton = new JButton("Add row");
      andAddButton.addActionListener(e -> {
        andModel.addRow(new String[] { DbConstants.TITLE, ViewFilter.BEGINS_WITH_TEXT, "" });
      });
    }
    return andAddButton;
  }

  private JButton getAndRemoveButton()
  {
    if (andRemoveButton == null)
    {
      andRemoveButton = new JButton("Remove row");
      andRemoveButton.addActionListener(e -> {
        andModel.removeRow(getAndTable().getSelectedRow());
        if (andModel.getRowCount() > 0)
        {
          //Select last row
          getAndTable().getSelectionModel().setSelectionInterval(andModel.getRowCount() - 1,
                                                                 andModel.getRowCount() - 1);
        }
      });
      andRemoveButton.setEnabled(false);
    }
    return andRemoveButton;
  }
}
