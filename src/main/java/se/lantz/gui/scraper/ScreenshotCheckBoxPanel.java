package se.lantz.gui.scraper;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JCheckBox;
import java.awt.Insets;

public class ScreenshotCheckBoxPanel extends JPanel
{
  private JLabel imageLabel;
  private JCheckBox checkBox;

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
    }
    return imageLabel;
  }
  public JCheckBox getCheckBox() {
    if (checkBox == null) {
    	checkBox = new JCheckBox("");
    }
    return checkBox;
  }
}
