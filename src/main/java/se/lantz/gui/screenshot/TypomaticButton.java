package se.lantz.gui.screenshot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class TypomaticButton extends JButton implements MouseListener
{
  private final int SPEED = 25;
  private final int WAIT = 150;
  private boolean autotype = false;
  private static Thread theThread = null;
  private String myName = "unknown";
  private int speed = SPEED, wait = WAIT, decrement = (wait - speed) / 10;

  TypomaticButton(Action action)
  {
    super(action);
    myName = action.getValue(Action.NAME).toString();
    addMouseListener(this);
  }

  TypomaticButton(String text)
  {
    super(text);
    setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

    myName = text;
    addMouseListener(this);
  }

  @Override
  public void mouseClicked(MouseEvent arg0)
  {
  }

  @Override
  public void mouseEntered(MouseEvent arg0)
  {
  }

  @Override
  public void mouseExited(MouseEvent arg0)
  {
  }

  @Override
  public void mousePressed(MouseEvent arg0)
  {
    autotype = true;
    theThread = new Thread(new Runnable()
      { // do it on a new thread so we don't block the UI thread
        @Override
        public void run()
        {
          for (int i = 10000; i > 0 && autotype; i--)
          { // don't go on for ever
            try
            {
              Thread.sleep(wait);     // wait awhile
            }
            catch (InterruptedException e)
            {
              break;
            }
            if (wait != speed)
            {
              wait = wait - decrement;        // gradually accelerate to top speed
              if (wait < speed)
                wait = speed;
            }
            SwingUtilities.invokeLater(new Runnable()
              { // run this bit on the UI thread
                public void run()
                {
                  if (!autotype)   // it may have been stopped meanwhile
                    return;
                  ActionListener[] als = getActionListeners();
                  for (ActionListener al : als)
                  {   // distribute to all listeners
                    ActionEvent aevent = new ActionEvent(getClass(), 0, myName);
                    al.actionPerformed(aevent);
                  }
                }
              });
          }
          autotype = false;
        }
      });
    theThread.start();
  }

  @Override
  public void mouseReleased(MouseEvent arg0)
  {
    autotype = false;
    wait = WAIT;
  }
}