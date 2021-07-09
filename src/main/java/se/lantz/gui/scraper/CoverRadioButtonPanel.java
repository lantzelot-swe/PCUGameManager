package se.lantz.gui.scraper;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CoverRadioButtonPanel extends JPanel
{
  private JLabel imageLabel;
  private JRadioButton radioButton;

  public CoverRadioButtonPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_imageLabel = new GridBagConstraints();
    gbc_imageLabel.weightx = 1.0;
    gbc_imageLabel.insets = new Insets(1, 1, 5, 1);
    gbc_imageLabel.gridx = 0;
    gbc_imageLabel.gridy = 0;
    add(getImageLabel(), gbc_imageLabel);
    GridBagConstraints gbc_button = new GridBagConstraints();
    gbc_button.anchor = GridBagConstraints.NORTH;
    gbc_button.weighty = 1.0;
    gbc_button.weightx = 1.0;
    gbc_button.gridx = 0;
    gbc_button.gridy = 1;
    add(getRadioButton(), gbc_button);
  }

  public JLabel getImageLabel()
  {
    if (imageLabel == null)
    {
      imageLabel = new JLabel("");
      deselect();
      imageLabel.addMouseListener(new MouseAdapter()
        {
          @Override
          public void mousePressed(MouseEvent arg0)
          {
            select();
            getRadioButton().setSelected(true);
          }
        });
    }
    return imageLabel;
  }

  public JRadioButton getRadioButton()
  {
    if (radioButton == null)
    {
      radioButton = new JRadioButton("");
      ChangeListener listener = new ChangeListener()
        {
          public void stateChanged(ChangeEvent e)
          {
            if (radioButton.isSelected())
            {
              select();
            }
            else
            {
              deselect();
            }
          }
        };
      radioButton.addChangeListener(listener);
    }
    return radioButton;
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
