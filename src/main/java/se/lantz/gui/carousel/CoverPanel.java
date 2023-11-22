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
  private JLabel test1Label;

  public CoverPanel()
  {
    //    setBorder(new LineBorder(new Color(0, 0, 0)));
    setBackground(new Color(138, 137, 138));
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
    }
    return panel;
  }

  int test = 0;

  ActionListener timerListener = (e) -> {
    scrollFromTimer();
  };

  Timer javaTimer = new Timer(10, timerListener);
  boolean stopped = true;
  boolean scrollDirectionRight = true;

  private void scrollFromTimer()
  {
    int valueToScroll = scrollDirectionRight
      ? scrollPane.getHorizontalScrollBar().getValue() + 32
      : scrollPane.getHorizontalScrollBar().getValue() - 32;
    //Scroll 
    scrollPane.getHorizontalScrollBar().setValue(valueToScroll);
    test++;
    if (test > 4)
    {
      int lastScrollValue = scrollDirectionRight
        ? scrollPane.getHorizontalScrollBar().getValue() + 10
        : scrollPane.getHorizontalScrollBar().getValue() - 10;
      scrollPane.getHorizontalScrollBar().setValue(lastScrollValue);
      test = 0;
      javaTimer.stop();
      stopped = true;
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

      for (MouseWheelListener listener : scrollPane.getMouseWheelListeners())
      {
        scrollPane.removeMouseWheelListener(listener);
      }

      scrollPane.addMouseWheelListener(a -> {
        scrollOneGame(a.getWheelRotation() > 0);
      });
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
    return label;
  }

  private void scrollOneGame(boolean right)
  {
    if (stopped)
    {
      scrollDirectionRight = right;
      stopped = false;
      javaTimer.start();
    }
  }
}
