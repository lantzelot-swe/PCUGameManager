package se.lantz.gui.scraper;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JCheckBox;
import java.awt.Insets;

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
    gbc_imageLabel.insets = new Insets(0, 0, 5, 0);
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
  public JLabel getImageLabel() {
    if (imageLabel == null) {
    	imageLabel = new JLabel("");
    }
    return imageLabel;
  }
  public JRadioButton getRadioButton() {
    if (radioButton == null) {
    	radioButton = new JRadioButton("");
    }
    return radioButton;
  }
}
