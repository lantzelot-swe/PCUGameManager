package se.lantz.util;

import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.undo.CompoundEdit;

/**
 * Document supporting undo/redo better than default.
 *
 */
public abstract class CustomUndoPlainDocument extends PlainDocument
{
  private static final long serialVersionUID = 8792731555178459913L;
  private CompoundEdit compoundEdit;

  @Override
  protected void fireUndoableEditUpdate(UndoableEditEvent e)
  {
    if (compoundEdit == null)
    {
      super.fireUndoableEditUpdate(e);
    }
    else
    {
      compoundEdit.addEdit(e.getEdit());
    }
  }

  @Override
  public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException
  {
    if (length == 0)
    {
      super.replace(offset, length, text, attrs);
    }
    else
    {
      compoundEdit = new CompoundEdit();
      super.fireUndoableEditUpdate(new UndoableEditEvent(this, compoundEdit));
      super.replace(offset, length, text, attrs);
      compoundEdit.end();
      compoundEdit = null;
    }
    //Call updateModel to make sure pasted text is persisted properly
    updateModel();
  }

  /**
   * Override to implement behavior
   */
  public abstract void updateModel();
}