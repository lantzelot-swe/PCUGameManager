package se.lantz.util;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class TextComponentSupport
{
  public final static String UNDO_ACTION = "Undo";
  public final static String REDO_ACTION = "Redo";

  private static List<UndoManager> managerList = new ArrayList<>();

  private TextComponentSupport()
  {
    // Empty
  }

  private static void addAction(JPopupMenu popupMenu, TextAction action, int key, String text)
  {
    action.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
    action.putValue(AbstractAction.NAME, text);
    popupMenu.add(new JMenuItem(action));
  }

  public static void setupPopupAndUndoable(JTextComponent... components)
  {
    if (components == null)
    {
      return;
    }
    for (JTextComponent tc : components)
    {
      JPopupMenu popupMenu = new JPopupMenu();
      addAction(popupMenu, new DefaultEditorKit.CutAction(), KeyEvent.VK_X, "Cut");
      addAction(popupMenu, new DefaultEditorKit.CopyAction(), KeyEvent.VK_C, "Copy");
      addAction(popupMenu, new DefaultEditorKit.PasteAction(), KeyEvent.VK_V, "Paste");

      tc.setComponentPopupMenu(popupMenu);
      makeUndoable(popupMenu, tc);
    }
  }

  public static void clearUndoManagers()
  {
    for (UndoManager undoManager : managerList)
    {
      undoManager.discardAllEdits();
    }
  }

  private static void makeUndoable(JPopupMenu popupMenu, JTextComponent pTextComponent)
  {
    final UndoManager undoMgr = new UndoManager();
    managerList.add(undoMgr);

    // Add listener for undoable events
    pTextComponent.getDocument().addUndoableEditListener(new UndoableEditListener()
      {
        public void undoableEditHappened(UndoableEditEvent evt)
        {
          undoMgr.addEdit(evt.getEdit());
        }
      });

    // Add undo/redo actions
    AbstractAction undoAction = new AbstractAction(UNDO_ACTION)
      {
        public void actionPerformed(ActionEvent evt)
        {
          try
          {
            if (undoMgr.canUndo())
            {
              undoMgr.undo();
            }
          }
          catch (CannotUndoException e)
          {
            e.printStackTrace();
          }
        }
      };
    pTextComponent.getActionMap().put(UNDO_ACTION, undoAction);
    undoAction.putValue(AbstractAction.ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
    AbstractAction redoAction = new AbstractAction(REDO_ACTION)
      {
        public void actionPerformed(ActionEvent evt)
        {
          try
          {
            if (undoMgr.canRedo())
            {
              undoMgr.redo();
            }
          }
          catch (CannotRedoException e)
          {
            e.printStackTrace();
          }
        }
      };
    pTextComponent.getActionMap().put(REDO_ACTION, redoAction);
    redoAction.putValue(AbstractAction.ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
    // Create keyboard accelerators for undo/redo actions (Ctrl+Z/Ctrl+Y)
    pTextComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), UNDO_ACTION);
    pTextComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), REDO_ACTION);
    popupMenu.addSeparator();
    popupMenu.add(undoAction);
    popupMenu.add(redoAction);
  }

}