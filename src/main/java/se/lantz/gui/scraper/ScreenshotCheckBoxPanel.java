package se.lantz.gui.scraper;

import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import java.awt.Insets;

public class ScreenshotCheckBoxPanel extends JPanel
{
  private JLabel imageLabel;
  private JCheckBox checkBox;
  private boolean isSelected = false;

  public ScreenshotCheckBoxPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_imageLabel = new GridBagConstraints();
    gbc_imageLabel.weightx = 1.0;
    gbc_imageLabel.insets = new Insets(0, 0, 5, 0);
    gbc_imageLabel.gridx = 0;
    gbc_imageLabel.gridy = 0;
    add(getImageLabel(), gbc_imageLabel);
    GridBagConstraints gbc_checkBox = new GridBagConstraints();
    gbc_checkBox.anchor = GridBagConstraints.NORTH;
    gbc_checkBox.weighty = 1.0;
    gbc_checkBox.weightx = 1.0;
    gbc_checkBox.gridx = 0;
    gbc_checkBox.gridy = 1;
    add(getCheckBox(), gbc_checkBox);
  }
  public JLabel getImageLabel() {
    if (imageLabel == null) {
    	imageLabel = new JLabel("");
    	deselect();
      imageLabel.addMouseListener(new MouseAdapter()
      {
        @Override
        public void mousePressed(MouseEvent arg0)
        {
          if (!isSelected)
          {
            getCheckBox().setSelected(true);
            select();
          }
          else
          {
            getCheckBox().setSelected(false);
            deselect();
          }
          isSelected = !isSelected;
        }
      });
    }
    return imageLabel;
  }
  public JCheckBox getCheckBox() {
    if (checkBox == null) {
    	checkBox = new JCheckBox("");
    	ChangeListener listener = new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          if (checkBox.isSelected())
          {
            select();
          }
          else
          {
            deselect();
          }
        }
      };
      checkBox.addChangeListener(listener);
    }
    return checkBox;
  }
  
  public void select()
  {
    getImageLabel().setBorder(BorderFactory.createLineBorder(Color.red, 2));
  }

  public void deselect()
  {
    getImageLabel().setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
  }
}
