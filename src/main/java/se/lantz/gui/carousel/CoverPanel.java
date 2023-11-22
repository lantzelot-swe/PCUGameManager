package se.lantz.gui.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class CoverPanel extends JPanel
{
  private JPanel panel;
  private JScrollPane scrollPane;

  int scrollingTimerIndex = 0;
  boolean scrolingStopped = true;
  boolean scrollDirectionRight = true;

  private ActionListener timerListener = e -> scrollFromTimer();

  private Timer scrolingTimer = new Timer(10, timerListener);

  public CoverPanel()
  {
    //    setBorder(new LineBorder(new Color(0, 0, 0)));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[] { 1.0 };
    gridBagLayout.columnWeights = new double[] { 1.0 };
    setLayout(gridBagLayout);
    this.setPreferredSize(new Dimension(700, 187));
    GridBagConstraints gbc_scrollPane = new GridBagConstraints();
    gbc_scrollPane.weighty = 1.0;
    gbc_scrollPane.weightx = 1.0;
    gbc_scrollPane.fill = GridBagConstraints.BOTH;
    gbc_scrollPane.gridx = 0;
    gbc_scrollPane.gridy = 0;
    add(getScrollPane(), gbc_scrollPane);
    addCovers(100);
  }

  private JPanel getPanel()
  {
    if (panel == null)
    {
      panel = new JPanel();
      GridBagLayout gbl_panel = new GridBagLayout();
      panel.setLayout(gbl_panel);

      panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                                                               "scrollRight");
      panel.getActionMap().put("scrollRight", new AbstractAction()
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            scrollOneGame(true);
          }
        });

      panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                                                               "scrollLeft");
      panel.getActionMap().put("scrollLeft", new AbstractAction()
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            scrollOneGame(false);
          }
        });
      panel.setBackground(new Color(138, 137, 138));
    }
    return panel;
  }

  private void scrollFromTimer()
  {
    int currentScrollValue = scrollPane.getHorizontalScrollBar().getValue();
    int newScrollValue = scrollDirectionRight ? currentScrollValue + 32 : currentScrollValue - 32;
    //Scroll 
    scrollPane.getHorizontalScrollBar().setValue(newScrollValue);
    scrollingTimerIndex++;
    if (scrollingTimerIndex > 5)
    {
      //Scroll one last time
      int lastScrollValue = scrollDirectionRight ? currentScrollValue + 10 : currentScrollValue - 10;
      scrollPane.getHorizontalScrollBar().setValue(lastScrollValue);
      scrollingTimerIndex = 0;
      scrolingTimer.stop();
      scrolingStopped = true;
    }
  }

  private JScrollPane getScrollPane()
  {
    if (scrollPane == null)
    {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getPanel());
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
      scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
      scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
      scrollPane.getHorizontalScrollBar().setBlockIncrement(10);

      //Remove all previously registered mouse wheel listeners to not interfere
      for (MouseWheelListener listener : scrollPane.getMouseWheelListeners())
      {
        scrollPane.removeMouseWheelListener(listener);
      }
      scrollPane.addMouseWheelListener(a -> scrollOneGame(a.getWheelRotation() > 0));
    }
    return scrollPane;
  }

  private void addCovers(int converCount)
  {

    for (int i = 0; i < converCount; i++)
    {
      GridBagConstraints gbc_label = new GridBagConstraints();
      gbc_label.fill = GridBagConstraints.BOTH;
      gbc_label.insets = new Insets(5, 22, 5, 23);
      gbc_label.weighty = 1.0;
      gbc_label.gridx = i;
      gbc_label.gridy = 0;
      panel.add(getLabel(i), gbc_label);
    }
  }

  private JLabel getLabel(int index)
  {
    JLabel label = new JLabel("Label");
    label.setBorder(new LineBorder(new Color(0, 0, 0)));
    label.setPreferredSize(new Dimension(125, 175));
//    label.setBackground(Color.red);
//    label.setOpaque(true);
    return label;
  }

  private void scrollOneGame(boolean right)
  {
    if (scrolingStopped)
    {
      scrollDirectionRight = right;
      scrolingStopped = false;
      scrolingTimer.start();
    }
  }
}
