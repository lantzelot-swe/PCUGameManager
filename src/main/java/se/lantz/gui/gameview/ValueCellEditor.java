package se.lantz.gui.gameview;

import java.awt.Component;
import java.awt.Toolkit;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import se.lantz.model.data.ViewFilter;
import se.lantz.util.DbConstants;

public class ValueCellEditor implements TableCellEditor
{
  private final static int BOOLEAN = 0;

  private final static int STRING = 1;

  private final static int NUM_EDITOR = 2;
  
  private final static int NOP_EDITOR = 3;

  DefaultCellEditor[] cellEditors;

  JComboBox<String> comboBox;

  int flg;

  public ValueCellEditor()
  {
    cellEditors = new DefaultCellEditor[4];
    comboBox = new JComboBox<>();
    comboBox.addItem("true");
    comboBox.addItem("false");
    cellEditors[BOOLEAN] = new DefaultCellEditor(comboBox);
    JTextField textField = new JTextField();
    cellEditors[STRING] = new DefaultCellEditor(textField);
    JTextField numberField = new JTextField();
    ((AbstractDocument) numberField.getDocument()).setDocumentFilter(new NumberDocumentFilter());
    cellEditors[NUM_EDITOR] = new DefaultCellEditor(numberField);
    
    JTextField disabledTextField = new JTextField();
    disabledTextField.setEditable(false);
    cellEditors[NOP_EDITOR] = new DefaultCellEditor(disabledTextField);
    flg = STRING;
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
  {

    String field = (String) table.getModel().getValueAt(row, 0);
    String operator = (String) table.getModel().getValueAt(row, 1);
    if (field.equals(DbConstants.FAVORITE))
    {
      flg = BOOLEAN;
      return cellEditors[BOOLEAN].getTableCellEditorComponent(table, value, isSelected, row, column);
    }
    else if (field.equals(DbConstants.YEAR))
    {
      flg = NUM_EDITOR;
      return cellEditors[NUM_EDITOR].getTableCellEditorComponent(table, value, isSelected, row, column);
    }
    else if (operator.equals(ViewFilter.EMPTY) || operator.equals(ViewFilter.NOT_EMPTY))
    {
      flg = NOP_EDITOR;
      return cellEditors[NOP_EDITOR].getTableCellEditorComponent(table, value, isSelected, row, column);
    }
    else
    {
      flg = STRING;
      return cellEditors[STRING].getTableCellEditorComponent(table, value, isSelected, row, column);
    }
  }

  public Object getCellEditorValue()
  {
    return cellEditors[flg].getCellEditorValue();
  }

  public Component getComponent()
  {
    return cellEditors[flg].getComponent();
  }

  public boolean stopCellEditing()
  {
    return cellEditors[flg].stopCellEditing();
  }

  public void cancelCellEditing()
  {
    cellEditors[flg].cancelCellEditing();
  }

  public boolean isCellEditable(EventObject anEvent)
  {
    return cellEditors[flg].isCellEditable(anEvent);
  }

  public boolean shouldSelectCell(EventObject anEvent)
  {
    return cellEditors[flg].shouldSelectCell(anEvent);
  }

  public void addCellEditorListener(CellEditorListener l)
  {
    cellEditors[flg].addCellEditorListener(l);
  }

  public void removeCellEditorListener(CellEditorListener l)
  {
    cellEditors[flg].removeCellEditorListener(l);
  }

  public void setClickCountToStart(int n)
  {
    cellEditors[flg].setClickCountToStart(n);
  }

  public int getClickCountToStart()
  {
    return cellEditors[flg].getClickCountToStart();
  }
}

class NumberDocumentFilter extends DocumentFilter
{
  @Override
  public void insertString(DocumentFilter.FilterBypass fp, int offset, String string, AttributeSet aset)
    throws BadLocationException
  {
    if (offset > 3 || offset + string.length() > 4)
    {
      Toolkit.getDefaultToolkit().beep();
      return;
    }
    int len = string.length();
    boolean isValidInteger = true;

    for (int i = 0; i < len; i++)
    {
      if (!Character.isDigit(string.charAt(i)))
      {
        isValidInteger = false;
        break;
      }
    }
    if (isValidInteger)
      super.insertString(fp, offset, string, aset);
    else
      Toolkit.getDefaultToolkit().beep();
  }

  @Override
  public void replace(DocumentFilter.FilterBypass fp, int offset, int length, String string, AttributeSet aset)
    throws BadLocationException
  {
    if (offset > 3 || offset + string.length() > 4)
    {
      Toolkit.getDefaultToolkit().beep();
      return;
    }

    int len = string.length();
    boolean isValidInteger = true;

    for (int i = 0; i < len; i++)
    {
      if (!Character.isDigit(string.charAt(i)))
      {
        isValidInteger = false;
        break;
      }
    }
    if (isValidInteger)
      super.replace(fp, offset, length, string, aset);
    else
      Toolkit.getDefaultToolkit().beep();
  }
}
