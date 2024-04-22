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

public class NesImagePanel extends JPanel
{
  public enum NesButton
  {
    UP("Up", Character.toString(0x1f815)), DOWN("Down", Character.toString(0x1f817)),
    LEFT("Left", Character.toString(0x1f814)), RIGHT("Right", Character.toString(0x1f816)), A("A", "Left fire"), B("B", "TL"),
    START("Start", "B");

    public final String label;
    public final String joyMapping;

    private NesButton(String label, String joyMapping)
    {
      this.label = label;
      this.joyMapping = joyMapping;
    }
  }

  ImageIcon gamepadImage = new ImageIcon(getClass().getResource("/se/lantz/nesController.png"));
  private JLabel imageLabel;

  private NesButton currentButton = null;

  public NesImagePanel()
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

  public void setCurrentButtonAndRepaint(NesButton button)
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
      graphics2D.fillRect(51, 78, 22, 20);
      break;
    case DOWN:
      graphics2D.fillRect(51, 118, 22, 20);
      break;
    case LEFT:
      graphics2D.fillRect(32, 98, 22, 20);
      break;
    case RIGHT:
      graphics2D.fillRect(70, 98, 22, 20);
      break;
    case A:
      graphics2D.fillRect(254, 108, 30, 30);
      break;
    case B:
      graphics2D.fillRect(214, 108, 30, 30);
      break;
    case START:
      graphics2D.fillRect(163, 116, 25, 15);
      break;
    default:
      break;
    }
  }
}
