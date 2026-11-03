package se.lantz.gui.scale;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

/**
 * UI class that scales the content in a JLayer.
 */
public class ScaleLayerUI extends LayerUI<JComponent>
{
  private static final long serialVersionUID = -1787502329635483814L;
  private double scale;
  private JLayer<?> layer;
  private Component hoverTarget;
  private Component pressedTarget;
  private final MouseAdapter catcher = new MouseAdapter()
    {
    }; // makes the glass pane the event target

  public ScaleLayerUI(double scale)
  {
    this.scale = scale;
  }

  public double getScale()
  {
    return scale;
  }

  public void setScale(double scale)
  {
    this.scale = scale;
    if (layer != null)
    {
      layer.revalidate();
      layer.repaint();
    }
  }

  @Override
  public void installUI(JComponent c)
  {
    super.installUI(c);
    layer = (JLayer<?>) c;
    JPanel glass = layer.getGlassPane();
    glass.setVisible(true);
    glass.addMouseListener(catcher);
    glass.addMouseMotionListener(catcher);
    glass.addMouseWheelListener(catcher);
    layer
      .setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK |
                         AWTEvent.MOUSE_WHEEL_EVENT_MASK);
  }

  @Override
  public void uninstallUI(JComponent c)
  {
    JPanel glass = layer.getGlassPane();
    glass.setVisible(false);
    glass.removeMouseListener(catcher);
    glass.removeMouseMotionListener(catcher);
    glass.removeMouseWheelListener(catcher);
    layer.setLayerEventMask(0);
    layer = null;
    super.uninstallUI(c);
  }

  // ---- painting & layout ----

  @Override
  public void paint(Graphics g, JComponent c)
  {
    Graphics2D g2 = (Graphics2D) g.create();
    try
    {
      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.scale(scale, scale);
      super.paint(g2, c);
    }
    finally
    {
      g2.dispose();
    }
  }

  @Override
  public void doLayout(JLayer<? extends JComponent> l)
  {
    int w = l.getWidth(), h = l.getHeight();
    Component view = l.getView();
    if (view != null)
    {
      view.setBounds(0, 0, (int) Math.ceil(w / scale), (int) Math.ceil(h / scale));
    }
    l.getGlassPane().setBounds(0, 0, w, h); // glass pane stays unscaled: it receives the real mouse events
  }

  @Override
  public Dimension getPreferredSize(JComponent c)
  {
    return scaled(((JLayer<?>) c).getView().getPreferredSize());
  }

  @Override
  public Dimension getMinimumSize(JComponent c)
  {
    return scaled(((JLayer<?>) c).getView().getMinimumSize());
  }

  private Dimension scaled(Dimension d)
  {
    return new Dimension((int) Math.ceil(d.width * scale), (int) Math.ceil(d.height * scale));
  }

  // ---- mouse event redirection ----

  @Override
  public void eventDispatched(AWTEvent e, JLayer<? extends JComponent> l)
  {
    if (!(e instanceof MouseEvent me) || me.getSource() != l.getGlassPane())
    {
      return; // ignore our own redispatched events
    }
    Component view = l.getView();
    Point p = new Point((int) (me.getX() / scale), (int) (me.getY() / scale));
    Component under = SwingUtilities.getDeepestComponentAt(view, p.x, p.y);
    int id = me.getID();

    switch (id)
    {
    case MouseEvent.MOUSE_ENTERED -> {
      return;
    }
    case MouseEvent.MOUSE_EXITED -> {
      if (pressedTarget == null)
        updateHover(null, me, view, p);
      return;
    }
    case MouseEvent.MOUSE_MOVED -> updateHover(under, me, view, p);
    case MouseEvent.MOUSE_PRESSED -> pressedTarget = under;
    default -> {
    }
    }

    Component target =
      (id == MouseEvent.MOUSE_DRAGGED || id == MouseEvent.MOUSE_RELEASED || id == MouseEvent.MOUSE_CLICKED) &&
                       pressedTarget != null ? pressedTarget : under;
    if (target != null)
    {
      redispatch(me, id, target, view, p);
    }
    if (id == MouseEvent.MOUSE_RELEASED)
    {
      pressedTarget = null;
      updateHover(under, me, view, p);
    }
    l.getGlassPane().setCursor(target != null ? target.getCursor() : null);
  }

  private void updateHover(Component newTarget, MouseEvent me, Component view, Point p)
  {
    if (newTarget == hoverTarget)
      return;
    if (hoverTarget != null)
      redispatch(me, MouseEvent.MOUSE_EXITED, hoverTarget, view, p);
    hoverTarget = newTarget;
    if (newTarget != null)
      redispatch(me, MouseEvent.MOUSE_ENTERED, newTarget, view, p);
  }

  private static void redispatch(MouseEvent me, int id, Component target, Component view, Point viewPoint)
  {
    Point tp = SwingUtilities.convertPoint(view, viewPoint, target);
    MouseEvent ne;
    if (me instanceof MouseWheelEvent mw)
    {
      ne = new MouseWheelEvent(target,
                               id,
                               mw.getWhen(),
                               mw.getModifiersEx(),
                               tp.x,
                               tp.y,
                               mw.getXOnScreen(),
                               mw.getYOnScreen(),
                               mw.getClickCount(),
                               mw.isPopupTrigger(),
                               mw.getScrollType(),
                               mw.getScrollAmount(),
                               mw.getWheelRotation(),
                               mw.getPreciseWheelRotation());
    }
    else
    {
      ne = new MouseEvent(target,
                          id,
                          me.getWhen(),
                          me.getModifiersEx(),
                          tp.x,
                          tp.y,
                          me.getXOnScreen(),
                          me.getYOnScreen(),
                          me.getClickCount(),
                          me.isPopupTrigger(),
                          me.getButton());
    }
    target.dispatchEvent(ne);
  }
}

