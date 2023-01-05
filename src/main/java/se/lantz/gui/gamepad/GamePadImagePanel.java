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

public class GamePadImagePanel extends JPanel
{
  public enum GamePadButton
  {
    UP("Up", Character.toString(0x1f815)), DOWN("Down", Character.toString(0x1f817)),
    LEFT("Left", Character.toString(0x1f814)), RIGHT("Right", Character.toString(0x1f816)), A("A", "A"), B("B", "B"),
    X("X", "TR"), Y("Y", "TL"), LEFT_TRIGGER("Left Trigger", "Left Fire"), RIGHT_TRIGGER("Right Trigger", "Right Fire"),
    LEFT_SHOULDER("Left Shoulder", "-"), RIGHT_SHOULDER("Right Shoulder", "-"), BACK_GUIDE("Back/Guide", "C"),
    LEFT_STICK("Left stick", "-"), RIGHT_STICK("Right stick", "-");

    public final String label;
    public final String joyMapping;

    private GamePadButton(String label, String joyMapping)
    {
      this.label = label;
      this.joyMapping = joyMapping;
    }
  }

  ImageIcon gamepadImage = new ImageIcon(getClass().getResource("/se/lantz/logitech320.png"));
  private JLabel imageLabel;

  private GamePadButton currentButton = null;

  public GamePadImagePanel()
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

  public void setCurrentButtonAndRepaint(GamePadButton button)
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
      //Oval around dpad
      graphics2D.drawOval(52, 55, 51, 51);
      //up
      graphics2D.fillRect(72, 57, 12, 20);
      break;
    case A:
      break;
    case B:
      break;
    case BACK_GUIDE:
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
      break;
    case LEFT_STICK:
      break;
    case LEFT_TRIGGER:
      break;
    case RIGHT:
      //Oval around dpad
      graphics2D.drawOval(52, 55, 51, 51);
      //right
      graphics2D.fillRect(82, 75, 20, 12);
      break;
    case RIGHT_SHOULDER:
      break;
    case RIGHT_STICK:
      break;
    case RIGHT_TRIGGER:
      break;
    case X:
      break;
    case Y:
      break;
    default:
      break;
    }
  }
}
