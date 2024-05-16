package se.lantz.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class DraggableTabbedPane extends JTabbedPane
{

  private boolean dragging = false;
  private Image tabImage = null;
  private Point currentMouseLocation = null;
  private int draggedTabIndex = 0;

  private Rectangle draggedTabBounds = null;
  private int previouslySelectedIndex;

  private ActionListener draggedTabListener = null;
  private ActionListener tabStructureChangedListener = null;

  public DraggableTabbedPane()
  {
    super();

    addMouseMotionListener(new MouseMotionAdapter()
      {

        public void mouseDragged(MouseEvent e)
        {

          if (!dragging)
          {
            // Gets the tab index based on the mouse position
            int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());

            if (tabNumber >= 0 && tabNumber < (getTabCount() - 1))
            {
              draggedTabIndex = tabNumber;
              Rectangle bounds = getUI().getTabBounds(DraggableTabbedPane.this, tabNumber);

              draggedTabBounds = bounds;
              // Paint the tabbed pane to a buffer
              Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
              Graphics totalGraphics = totalImage.getGraphics();
              totalGraphics.setClip(bounds);
              // Don't be double buffered when painting to a static image.
              setDoubleBuffered(false);
              paintComponent(totalGraphics);

              // Paint just the dragged tab to the buffer
              tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
              Graphics graphics = tabImage.getGraphics();
              graphics.drawImage(totalImage,
                                 0,
                                 0,
                                 bounds.width,
                                 bounds.height,
                                 bounds.x,
                                 bounds.y,
                                 bounds.x + bounds.width,
                                 bounds.y + bounds.height,
                                 DraggableTabbedPane.this);

              dragging = true;
              repaint();
            }
          }
          else
          {
            currentMouseLocation = e.getPoint();

            // Need to repaint
            repaint();
          }

          super.mouseDragged(e);
        }
      });

    addMouseListener(new MouseAdapter()
      {
        @Override
        public void mouseReleased(MouseEvent e)
        {
          if (dragging)
          {
            int tabNumber = -1;

            Rectangle lastTabBounds = getUI().getTabBounds(DraggableTabbedPane.this, getTabCount() - 2);

            int draggedTabXposToCheck = e.getX() + draggedTabBounds.width / 2;

            //Dragging left of first tab always puts the tab first
            if (e.getX() <= 0)
            {
              tabNumber = 0;
            }
            //Dragging right of next-to-last tab always puts the tab last
            else if (lastTabBounds.x + lastTabBounds.width / 2 < draggedTabXposToCheck)
            {
              //Add in last position (before "+")
              tabNumber = getTabCount() - 2;
            }
            else
            {

              tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, draggedTabXposToCheck, 10);

              Rectangle tabBounds = getUI().getTabBounds(DraggableTabbedPane.this, tabNumber);
              int halfTabPosition = tabBounds.x + tabBounds.width / 2;

              //Dragging right
              if (e.getX() > draggedTabBounds.x)
              {
                if (draggedTabXposToCheck < halfTabPosition)
                {
                  //Do not move the tab
                  tabNumber--;
                }
              }
              //Dragging left
              else
              {
                if (draggedTabXposToCheck > halfTabPosition)
                {
                  //Do not move the tab
                  tabNumber++;
                }
              }
            }

            if (tabNumber == draggedTabIndex)
            {
              //Do nothing
            }
            else if (tabNumber >= 0 && tabNumber < (getTabCount() - 1))
            {
              Component comp = getComponentAt(draggedTabIndex);
              String title = getTitleAt(draggedTabIndex);
              removeTabAt(draggedTabIndex);
              insertTab(title, null, comp, null, tabNumber);
              notifyTabDraggedListener(tabNumber);
              notifyTabStructureChangedListener();
              setSelectedIndex(tabNumber);
            }
          }

          dragging = false;
          tabImage = null;
          // Need to repaint
          repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
          if (SwingUtilities.isRightMouseButton(e))
          {

            int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), 10);

            if (tabNumber > -1 && (tabNumber < getTabCount() - 1))
            {
              JPopupMenu menu = new JPopupMenu();
              JMenuItem renameTabItem = new JMenuItem("Rename database");
              JMenuItem deleteTabItem = new JMenuItem("Delete database");
              menu.add(renameTabItem);
              menu.add(deleteTabItem);
              menu.show(DraggableTabbedPane.this, e.getX(), e.getY());
            }
          }
        }
      });
  }

  public void addTabDraggedListener(ActionListener listener)
  {
    draggedTabListener = listener;
  }
  
  public void addTabStructureChangedListener(ActionListener listener)
  {
    tabStructureChangedListener = listener;
  }

  private void notifyTabDraggedListener(int tabNumber)
  {
    if (draggedTabListener != null)
    {
      draggedTabListener.actionPerformed(new ActionEvent(this, tabNumber, "inserted"));
    }
  }
  
  private void notifyTabStructureChangedListener()
  {
    if (tabStructureChangedListener != null)
    {
      tabStructureChangedListener.actionPerformed(new ActionEvent(this, 0, "structure changed"));
    }
  }

  public boolean isDragging()
  {
    return dragging;
  }

  public int getPreviouslySelectedIndex()
  {
    return previouslySelectedIndex;
  }

  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);

    // Are we dragging?
    if (dragging && currentMouseLocation != null && tabImage != null)
    {
      // Draw the dragged tab
      g.drawImage(tabImage, currentMouseLocation.x, 0, this);//currentMouseLocation.y, this);
    }
  }

  public static void main(String[] args)
  {
    JFrame test = new JFrame("Tab test");
    test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    test.setSize(800, 800);

    DraggableTabbedPane tabs = new DraggableTabbedPane();
    tabs.addTab("One", new JButton("One"));
    tabs.addTab("Two", new JButton("Two"));
    tabs.addTab("Three", new JButton("Three"));
    tabs.addTab("+", new JButton("+"));

    test.add(tabs);
    test.setVisible(true);
  }
}