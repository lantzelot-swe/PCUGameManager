package se.lantz.gui.screenshot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.JCheckBox;

public class EditScreenshotPanel extends JPanel
{
  private JLabel imageLabel;
  private JPanel buttonPanel;
  private TypomaticButton leftButton;
  private TypomaticButton righButton;
  private BufferedImage image;

  private int x = 0;
  private int y = 0;
  
  private int width = 320;
  private int height = 200;
  private TypomaticButton upButton;
  private TypomaticButton downButton;
  private JLabel infoLabel;
  private JCheckBox largeSizeCheckBox;

  public EditScreenshotPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.insets = new Insets(10, 5, 5, 0);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_imageLabel = new GridBagConstraints();
    gbc_imageLabel.fill = GridBagConstraints.BOTH;
    gbc_imageLabel.weighty = 1.0;
    gbc_imageLabel.insets = new Insets(10, 10, 10, 10);
    gbc_imageLabel.weightx = 1.0;
    gbc_imageLabel.gridx = 0;
    gbc_imageLabel.gridy = 1;
    add(getImageLabel(), gbc_imageLabel);
    GridBagConstraints gbc_largeSizeCheckBox = new GridBagConstraints();
    gbc_largeSizeCheckBox.insets = new Insets(0, 0, 5, 0);
    gbc_largeSizeCheckBox.gridx = 0;
    gbc_largeSizeCheckBox.gridy = 2;
    add(getLargeSizeCheckBox(), gbc_largeSizeCheckBox);
    GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
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
    }
    return imageLabel;
  }

  private JPanel getButtonPanel()
  {
    if (buttonPanel == null)
    {
      buttonPanel = new JPanel();
      buttonPanel.add(getLeftButton());
      buttonPanel.add(getRighButton());
      buttonPanel.add(getUpButton());
      buttonPanel.add(getDownButton());
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
    this.image = image;  
    getLargeSizeCheckBox().setVisible(image.getWidth() > 447 && image.getHeight() > 279);  
    x = (image.getWidth() - width) / 2;
    y = ((image.getHeight() - height) / 2) - 1;
    updateLabelIcon();
  }

  private void updateLabelIcon()
  {
    BufferedImage copyOfImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = copyOfImage.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.setColor(Color.red);
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
    BufferedImage newImage = image
      .getSubimage(x, y, width, height);
    BufferedImage copyOfImage =
      new BufferedImage(newImage.getWidth(), newImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = copyOfImage.createGraphics();
    g.drawImage(newImage, 0, 0, null);
    return newImage;
  }
  private JLabel getInfoLabel() {
    if (infoLabel == null) {
    	infoLabel = new JLabel("Move the rectangle to decide where to crop the image");
    }
    return infoLabel;
  }
  private JCheckBox getLargeSizeCheckBox() {
    if (largeSizeCheckBox == null) {
    	largeSizeCheckBox = new JCheckBox("Crop to 448x280 (image will be resized)");
    	largeSizeCheckBox.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    if (largeSizeCheckBox.isSelected())
    	    {
    	      width = 448;
    	      height = 280;
    	    }
    	    else
    	    {
    	      width = 320;
            height = 200;
    	    }
    	    x = (image.getWidth() - width) / 2;
          y = ((image.getHeight() - height) / 2) - 1;
    	    updateLabelIcon();
    	  }
    	});
    }
    return largeSizeCheckBox;
  }
}
