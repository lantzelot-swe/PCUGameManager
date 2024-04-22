package se.lantz.gui.gamepad;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SpeedlinkImagePanel extends JPanel
{
  public enum SpeedlinkButton
  {
    UP("Up", Character.toString(0x1f815)), DOWN("Down", Character.toString(0x1f817)),
    LEFT("Left", Character.toString(0x1f814)), RIGHT("Right", Character.toString(0x1f816)), LEFT_FIRE("Left fire", "Left fire"),
    RIGHT_FIRE("Right Fire", "A"), LT("Left triangle", "B");

    public final String label;
    public final String joyMapping;

    private SpeedlinkButton(String label, String joyMapping)
    {
      this.label = label;
      this.joyMapping = joyMapping;
    }
  }

  ImageIcon gamepadImage = new ImageIcon(getClass().getResource("/se/lantz/speedlink.png"));
  private JLabel imageLabel;

  private SpeedlinkButton currentButton = null;

  public SpeedlinkImagePanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_imageLabel = new GridBagConstraints();
    gbc_imageLabel.insets = new Insets(20, 0, 0, 0);
    gbc_imageLabel.weightx = 1.0;
    gbc_imageLabel.weighty = 1.0;
    gbc_imageLabel.anchor = GridBagConstraints.NORTH;
    gbc_imageLabel.gridx = 0;
    gbc_imageLabel.gridy = 0;
    add(getImageLabel(), gbc_imageLabel);
  }

  private JLabel getImageLabel()
  {
    if (imageLabel == null)
    {
      imageLabel = new JLabel()
        {

          @Override
          protected void paintComponent(Graphics g)
          {
            super.paintComponent(g);
            //Draw for currently focused button
            drawForButton(g);
          }

        };
      imageLabel.setIcon(gamepadImage);
    }
    return imageLabel;
  }

  public void setCurrentButtonAndRepaint(SpeedlinkButton button)
  {
    this.currentButton = button;
    this.repaint();
  }

  private void drawForButton(Graphics g)
  {
    if (currentButton == null)
    {
      return;
    }

    Graphics2D graphics2D = (Graphics2D) g;
    graphics2D.setStroke(new BasicStroke(3.0f));
    graphics2D.setColor(Color.green);

    switch (currentButton)
    {
    case UP:
      drawArrow(graphics2D,
                new Point2D.Double(77, 50),
                new Point2D.Double(77, 5),
                new BasicStroke(3.0f),
                new BasicStroke(1.0f),
                25f);
      break;
    case DOWN:
      drawArrow(graphics2D,
                new Point2D.Double(77, 5),
                new Point2D.Double(77, 50),
                new BasicStroke(3.0f),
                new BasicStroke(1.0f),
                25f);
      break;
    case LEFT:
      drawArrow(graphics2D,
                new Point2D.Double(100, 25),
                new Point2D.Double(55, 25),
                new BasicStroke(3.0f),
                new BasicStroke(1.0f),
                25f);
      break;
    case RIGHT:
      drawArrow(graphics2D,
                new Point2D.Double(55, 25),
                new Point2D.Double(100, 25),
                new BasicStroke(3.0f),
                new BasicStroke(1.0f),
                25f);
      break;
    case LEFT_FIRE:
      graphics2D.fillOval(18, 82, 35, 17);
      break;
    case RIGHT_FIRE:
      graphics2D.fillOval(97, 82, 35, 17);
      break;
    case LT:
      graphics2D.fillRect(30, 100, 15, 10);
      break;
    default:
      break;
    }
  }

  private void drawArrow(final Graphics2D gfx,
                         final Point2D start,
                         final Point2D end,
                         final Stroke lineStroke,
                         final Stroke arrowStroke,
                         final float arrowSize)
  {

    final double startx = start.getX();
    final double starty = start.getY();

    gfx.setStroke(arrowStroke);
    final double deltax = startx - end.getX();
    final double result;
    if (deltax == 0.0d)
    {
      result = starty < end.getY() ? -(Math.PI / 2) : (Math.PI / 2);
    }
    else
    {
      result = Math.atan((starty - end.getY()) / deltax) + (startx < end.getX() ? Math.PI : 0);
    }

    final double angle = result;

    final double arrowAngle = Math.PI / 12.0d;

    final double x1 = arrowSize * Math.cos(angle - arrowAngle);
    final double y1 = arrowSize * Math.sin(angle - arrowAngle);
    final double x2 = arrowSize * Math.cos(angle + arrowAngle);
    final double y2 = arrowSize * Math.sin(angle + arrowAngle);

    final double cx = (arrowSize / 2.0f) * Math.cos(angle);
    final double cy = (arrowSize / 2.0f) * Math.sin(angle);

    final GeneralPath polygon = new GeneralPath();
    polygon.moveTo(end.getX(), end.getY());
    polygon.lineTo(end.getX() + x1, end.getY() + y1);
    polygon.lineTo(end.getX() + x2, end.getY() + y2);
    polygon.closePath();
    gfx.fill(polygon);

    gfx.setStroke(lineStroke);
    gfx.drawLine((int) startx, (int) starty, (int) (end.getX() + cx), (int) (end.getY() + cy));
  }
}
