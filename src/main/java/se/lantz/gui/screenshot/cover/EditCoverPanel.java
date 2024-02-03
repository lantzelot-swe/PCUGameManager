package se.lantz.gui.screenshot.cover;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import se.lantz.gui.screenshot.TypomaticButton;
import javax.swing.JButton;

public class EditCoverPanel extends JPanel
{
  private JLabel imageLabel;
  private JPanel buttonPanel;
  private TypomaticButton leftButton;
  private TypomaticButton righButton;
  private BufferedImage image;

  private int x = 0;
  private int y = 0;
  private int width = 0;
  private int height = 0;

  private TypomaticButton upButton;
  private TypomaticButton downButton;
  private JLabel infoLabel;
  private JButton rotateButton;

  public EditCoverPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.ipady = 10;
    gbc_infoLabel.fill = GridBagConstraints.BOTH;
    gbc_infoLabel.insets = new Insets(10, 0, 0, 0);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_imageLabel = new GridBagConstraints();
    gbc_imageLabel.weighty = 1.0;
    gbc_imageLabel.insets = new Insets(0, 10, 10, 10);
    gbc_imageLabel.weightx = 1.0;
    gbc_imageLabel.gridx = 0;
    gbc_imageLabel.gridy = 1;
    add(getImageLabel(), gbc_imageLabel);
    GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
    gbc_buttonPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_buttonPanel.weightx = 1.0;
    gbc_buttonPanel.anchor = GridBagConstraints.NORTH;
    gbc_buttonPanel.gridx = 0;
    gbc_buttonPanel.gridy = 3;
    add(getButtonPanel(), gbc_buttonPanel);

    InputMap inputMap = this.getInputMap(WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
    getActionMap().put("left", new AbstractAction()
      {
        public void actionPerformed(ActionEvent e)
        {
          performLeftAction();
        }
      });

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
    getActionMap().put("right", new AbstractAction()
      {
        public void actionPerformed(ActionEvent e)
        {
          performRightAction();
        }
      });

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
    getActionMap().put("up", new AbstractAction()
      {
        public void actionPerformed(ActionEvent e)
        {
          performUpAction();
        }
      });

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
    getActionMap().put("down", new AbstractAction()
      {
        public void actionPerformed(ActionEvent e)
        {
          performDownAction();
        }
      });

  }

  private void performLeftAction()
  {
    if (x > 0)
    {
      x = x - 1;
      updateLabelIcon();
    }
  }

  private void performRightAction()
  {
    if (x < (image.getWidth() - width - 1))
    {
      x = x + 1;
      updateLabelIcon();
    }
  }

  private void performUpAction()
  {
    if (y > 0)
    {
      y = y - 1;
      updateLabelIcon();
    }
  }

  private void performDownAction()
  {
    if (y < (image.getHeight() - height - 1))
    {
      y = y + 1;
      updateLabelIcon();
    }
  }

  private JLabel getImageLabel()
  {
    if (imageLabel == null)
    {
      imageLabel = new JLabel("");
      imageLabel.addMouseListener(new MouseAdapter()
        {
          @Override
          public void mousePressed(MouseEvent e)
          {
            x = e.getX();
            y = e.getY();
            width = 0;
            height = 0;
            updateLabelIcon();
          }
        });

      imageLabel.addMouseMotionListener(new MouseMotionAdapter()
        {
          @Override
          public void mouseDragged(MouseEvent e)
          {
            mouseDrag(e.getX(), e.getY());
          }
        });
    }
    return imageLabel;
  }

  private JPanel getButtonPanel()
  {
    if (buttonPanel == null)
    {
      buttonPanel = new JPanel();
      GridBagLayout gbl_buttonPanel = new GridBagLayout();
      gbl_buttonPanel.columnWidths = new int[]{33, 33, 33, 33, 33, 0};
      gbl_buttonPanel.rowHeights = new int[]{33, 0};
      gbl_buttonPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
      gbl_buttonPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
      buttonPanel.setLayout(gbl_buttonPanel);
      GridBagConstraints gbc_leftButton = new GridBagConstraints();
      gbc_leftButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_leftButton.insets = new Insets(5, 10, 0, 0);
      gbc_leftButton.gridx = 0;
      gbc_leftButton.gridy = 0;
      buttonPanel.add(getLeftButton(), gbc_leftButton);
      GridBagConstraints gbc_righButton = new GridBagConstraints();
      gbc_righButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_righButton.insets = new Insets(5, 5, 0, 5);
      gbc_righButton.gridx = 1;
      gbc_righButton.gridy = 0;
      buttonPanel.add(getRighButton(), gbc_righButton);
      GridBagConstraints gbc_upButton = new GridBagConstraints();
      gbc_upButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_upButton.insets = new Insets(5, 5, 0, 5);
      gbc_upButton.gridx = 2;
      gbc_upButton.gridy = 0;
      buttonPanel.add(getUpButton(), gbc_upButton);
      GridBagConstraints gbc_downButton = new GridBagConstraints();
      gbc_downButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_downButton.insets = new Insets(5, 5, 0, 5);
      gbc_downButton.gridx = 3;
      gbc_downButton.gridy = 0;
      buttonPanel.add(getDownButton(), gbc_downButton);
      GridBagConstraints gbc_rotateButton = new GridBagConstraints();
      gbc_rotateButton.weightx = 1.0;
      gbc_rotateButton.insets = new Insets(5, 0, 5, 10);
      gbc_rotateButton.anchor = GridBagConstraints.NORTHEAST;
      gbc_rotateButton.gridx = 4;
      gbc_rotateButton.gridy = 0;
      buttonPanel.add(getRotateButton(), gbc_rotateButton);
    }
    return buttonPanel;
  }

  private TypomaticButton getLeftButton()
  {
    if (leftButton == null)
    {
      leftButton = new TypomaticButton("");
      leftButton.setIcon(new ImageIcon(this.getClass().getResource("/se/lantz/arrow-plain-left.png")));
      leftButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            performLeftAction();
          }
        });
    }
    return leftButton;
  }

  private TypomaticButton getRighButton()
  {
    if (righButton == null)
    {
      righButton = new TypomaticButton("");
      righButton.setIcon(new ImageIcon(this.getClass().getResource("/se/lantz/arrow-plain-right.png")));
      righButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            performRightAction();
          }
        });
    }
    return righButton;
  }

  public void setImage(BufferedImage image)
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenHeight = (int) screenSize.getHeight();
    int heightThreshold = screenHeight - 250;
    //Handle very large images by scaling them down so that the dialog fits on the screen
    if (image.getHeight() > heightThreshold)
    {
      Image scaledImage = image.getScaledInstance(-1, heightThreshold, Image.SCALE_SMOOTH);
      BufferedImage newBufImage =
        new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
      Graphics2D bGr = newBufImage.createGraphics();
      bGr.drawImage(scaledImage, 0, 0, null);
      bGr.dispose();
      this.image = newBufImage;
    }
    else
    {
      this.image = image;
    }

    updateLabelIcon();
  }

  private void updateLabelIcon()
  {
    BufferedImage copyOfImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = copyOfImage.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.setColor(Color.red);
    g.setStroke(new BasicStroke(2));
    g.drawRect(x, y, width, height);
    getImageLabel().setIcon(new ImageIcon(copyOfImage));
  }

  public BufferedImage getImage()
  {
    return this.image;
  }

  private TypomaticButton getUpButton()
  {
    if (upButton == null)
    {
      upButton = new TypomaticButton("");
      upButton.setIcon(new ImageIcon(this.getClass().getResource("/se/lantz/arrow-plain-up.png")));
      upButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            performUpAction();
          }
        });
    }
    return upButton;
  }

  private TypomaticButton getDownButton()
  {
    if (downButton == null)
    {
      downButton = new TypomaticButton("");
      downButton.setIcon(new ImageIcon(this.getClass().getResource("/se/lantz/arrow-plain-down.png")));
      downButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            performDownAction();
          }
        });
    }
    return downButton;
  }

  public BufferedImage getCroppedImage()
  {
    if (width < 5 || height < 5)
    {
      return image;
    }
    BufferedImage newImage = image.getSubimage(x, y, width, height);
    BufferedImage copyOfImage =
      new BufferedImage(newImage.getWidth(), newImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = copyOfImage.createGraphics();
    g.drawImage(newImage, 0, 0, null);
    return newImage;
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel =
        new JLabel("<html>Left-click and drag to mark the area to crop.<br>Move the rectangle with the arrow buttons.</html>");
      infoLabel.setVerticalAlignment(SwingConstants.TOP);
      infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
      infoLabel.addMouseListener(new MouseAdapter()
        {
          @Override
          public void mousePressed(MouseEvent e)
          {
            //The image might not be at position 0 if the image is small, compensate for that.
            x = e.getX() - imageLabel.getLocation().x;
            if (x < 0)
            {
              x = 0;
            }
            y = 0;
            width = 0;
            height = 0;
            updateLabelIcon();
          }
        });

      infoLabel.addMouseMotionListener(new MouseMotionAdapter()
        {
          @Override
          public void mouseDragged(MouseEvent e)
          {
            int ypos = e.getY();

            if (e.getY() > (infoLabel.getHeight()))
            {
              ypos = e.getY() - infoLabel.getHeight();
              mouseDrag(e.getX(), ypos);
            }
          }
        });
    }
    return infoLabel;
  }

  private void mouseDrag(int xPos, int yPos)
  {
    width = xPos - x;
    height = yPos - y;
    if (x + width > image.getWidth())
    {
      width = image.getWidth() - x;
    }

    if (y + height > image.getHeight())
    {
      height = image.getHeight() - y;
    }
    updateLabelIcon();
  }
  private JButton getRotateButton() {
    if (rotateButton == null) {
    	rotateButton = new JButton("");
    	rotateButton.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    	rotateButton.setIcon(new ImageIcon(this.getClass().getResource("/se/lantz/rotate.png")));
    	rotateButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            performRotateAction();
          }
        });
    }
    return rotateButton;
  }
  
  private void performRotateAction()
  {
    int width = image.getWidth();
    int height = image.getHeight();

    BufferedImage dest = new BufferedImage(height, width, image.getType());

    Graphics2D graphics2D = dest.createGraphics();
    graphics2D.translate((height - width) / 2, (height - width) / 2);
    graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
    graphics2D.drawRenderedImage(image, null);
    this.image = dest;
    updateLabelIcon();
    EditCoverDialog dialog = (EditCoverDialog)SwingUtilities.getAncestorOfClass(EditCoverDialog.class, this);
    if (dialog != null)
    {
      dialog.pack();
    }
  }
}
