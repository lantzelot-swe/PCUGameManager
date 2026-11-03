package se.lantz.gui.scale;

import java.awt.Container;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;


/**
 * Repaint manager that recalculates dirty regions properly for JLayer components using a ScaleLayerUI.
 */
public class ScalingRepaintManager extends RepaintManager
{

  public ScalingRepaintManager()
  {
    //Empty
  }

  @Override
  public void addDirtyRegion(JComponent c, int x, int y, int w, int h)
  {
    for (Container p = c.getParent(); p != null; p = p.getParent())
    {
      if (p instanceof JLayer<?> l && l.getUI() instanceof ScaleLayerUI ui)
      {
        Rectangle r = SwingUtilities.convertRectangle(c, new Rectangle(x, y, w, h), l);
        double s = ui.getScale();
        int x0 = (int) Math.floor(r.x * s), y0 = (int) Math.floor(r.y * s);
        int x1 = (int) Math.ceil((r.x + r.width) * s), y1 = (int) Math.ceil((r.y + r.height) * s);
        super.addDirtyRegion(l, x0, y0, x1 - x0, y1 - y0);
        return;
      }
    }
    super.addDirtyRegion(c, x, y, w, h);
  }
}
