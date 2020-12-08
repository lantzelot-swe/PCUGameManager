package se.lantz.gui;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class DocumentSizeFilter extends DocumentFilter
{
  int maxCharacters;
  boolean DEBUG = false;

  public DocumentSizeFilter(int maxChars)
  {
    maxCharacters = maxChars;
  }

  public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException
  {
    if (DEBUG)
    {
      System.out.println("in DocumentSizeFilter's insertString method");
    }

    //This rejects the entire insertion if it would make
    //the contents too long. Another option would be
    //to truncate the inserted string so the contents
    //would be exactly maxCharacters in length.
    if ((fb.getDocument().getLength() + str.length()) <= maxCharacters)
      super.insertString(fb, offs, str, a);
    else
      Toolkit.getDefaultToolkit().beep();
  }

  public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException
  {
    if (DEBUG)
    {
      System.out.println("in DocumentSizeFilter's replace method");
    }
    //This rejects the entire replacement if it would make
    //the contents too long. Another option would be
    //to truncate the replacement string so the contents
    //would be exactly maxCharacters in length.
    if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters)
      super.replace(fb, offs, length, str, a);
    else
      Toolkit.getDefaultToolkit().beep();
  }

}