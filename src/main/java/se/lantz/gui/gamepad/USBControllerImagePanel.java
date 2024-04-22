package se.lantz.gui.gamepad;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.lantz.util.FileManager;

public class USBControllerImagePanel extends JPanel
{
  public enum USBControllerButton
  {
    UP("Up", Character.toString(0x1f815)), DOWN("Down", Character.toString(0x1f817)),
    LEFT("Left", Character.toString(0x1f814)), RIGHT("Right", Character.toString(0x1f816)), A("A", "A"), B("B", "B"),
    X("X", "TR"), Y("Y", "TL"), LEFT_TRIGGER("Left Trigger", "Left Fire"), RIGHT_TRIGGER("Right Trigger", "Right Fire"),
    LEFT_SHOULDER("Left Shoulder", "-"), RIGHT_SHOULDER("Right Shoulder", "-"), BACK_GUIDE("Back/Guide", "C"),
    LEFT_STICK("Left stick", "-"), RIGHT_STICK("Right stick", "-");

    public final String label;
    public final String joyMapping;

    private USBControllerButton(String label, String joyMapping)
    {
      this.label = label;
      this.joyMapping = joyMapping;
    }
  }

  ImageIcon gamepadImage = new ImageIcon(getClass().getResource("/se/lantz/logitech.png"));
  private JLabel imageLabel;

  private USBControllerButton currentButton = null;

  public USBControllerImagePanel()
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

  public void setCurrentButtonAndRepaint(USBControllerButton button)
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
      //Oval around dpad
      graphics2D.drawOval(52, 55, 51, 51);
      //up
      graphics2D.fillRect(72, 57, 12, 20);
      break;
    case A:
      graphics2D.drawOval(241, 94, 22, 22);
      break;
    case B:
      graphics2D.drawOval(263, 72, 22, 22);
      break;
    case BACK_GUIDE:
      graphics2D.drawOval(124, 55, 20, 16);
      break;
    case DOWN:
      //Oval around dpad
      graphics2D.drawOval(52, 55, 51, 51);
      //down
      graphics2D.fillRect(72, 85, 12, 20);
      break;
    case LEFT:
      //Oval around dpad
      graphics2D.drawOval(52, 55, 51, 51);
      //Left
      graphics2D.fillRect(54, 75, 20, 12);
      break;
    case LEFT_SHOULDER:
      graphics2D.drawRect(242, 240, 35, 25);
      break;
    case LEFT_STICK:
      graphics2D.fillOval(114, 126, 15, 15);
      break;
    case LEFT_TRIGGER:
      graphics2D.drawOval(240, 270, 36, 36);
      break;
    case RIGHT:
      //Oval around dpad
      graphics2D.drawOval(52, 55, 51, 51);
      //right
      graphics2D.fillRect(82, 75, 20, 12);
      break;
    case RIGHT_SHOULDER:
      graphics2D.drawRect(48, 240, 35, 25);
      break;
    case RIGHT_STICK:
      graphics2D.fillOval(202, 126, 15, 15);
      break;
    case RIGHT_TRIGGER:
      graphics2D.drawOval(48, 270, 36, 36);
      break;
    case X:
      graphics2D.drawOval(219, 72, 22, 22);
      break;
    case Y:
      graphics2D.drawOval(241, 49, 22, 22);
      break;
    default:
      break;
    }
  }
}
