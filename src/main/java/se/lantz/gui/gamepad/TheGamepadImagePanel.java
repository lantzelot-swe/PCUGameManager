package se.lantz.gui.gamepad;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TheGamepadImagePanel extends JPanel
{
  public enum TheGamepadButton
  {
    UP("Up", Character.toString(0x1f815)), DOWN("Down", Character.toString(0x1f817)),
    LEFT("Left", Character.toString(0x1f814)), RIGHT("Right", Character.toString(0x1f816)), A("A", "TL"), B("B", "Right Fire"),
    X("X", "TR"), Y("Y", "Left Fire"), LSB("LSB", "A"), RSB("RSB", "B"), MENU("Menu", "C");

    public final String label;
    public final String joyMapping;

    private TheGamepadButton(String label, String joyMapping)
    {
      this.label = label;
      this.joyMapping = joyMapping;
    }
  }

  ImageIcon gamepadImage = new ImageIcon(getClass().getResource("/se/lantz/TheGamepad.png"));
  private JLabel imageLabel;

  private TheGamepadButton currentButton = null;

  public TheGamepadImagePanel()
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

  public void setCurrentButtonAndRepaint(TheGamepadButton button)
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
    graphics2D.setColor(Color.red);
    
    switch (currentButton)
    {
    case UP:
      graphics2D.drawOval(55, 50, 22, 22);
      break;
    case DOWN:
      graphics2D.drawOval(53, 84, 22, 22);
      break;
    case LEFT:
      graphics2D.drawOval(40, 66, 22, 22);
      break;
    case RIGHT:
      graphics2D.drawOval(72, 66, 22, 22);
      break;
    case A:
      graphics2D.drawOval(253, 89, 22, 22);
      break;
    case B:
      graphics2D.drawOval(276, 68, 22, 22);
      break;
    case MENU:
      graphics2D.drawRect(122, 92, 25, 25);
      break;
    case LSB:
      graphics2D.drawRect(47, 15, 25, 20);
      break;
    case RSB:
      graphics2D.drawRect(253, 15, 25, 20);
      break;
    case X:
      graphics2D.drawOval(230, 67, 22, 22);
      break;
    case Y:
      graphics2D.drawOval(250, 46, 22, 22);
      break;
    default:
      break;
    }
  }
}
