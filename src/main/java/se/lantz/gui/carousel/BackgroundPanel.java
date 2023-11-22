package se.lantz.gui.carousel;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;

public class BackgroundPanel extends JPanel {
  public BackgroundPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_coverPanel = new GridBagConstraints();
    gbc_coverPanel.gridwidth = 2;
    gbc_coverPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_coverPanel.weighty = 1.0;
    gbc_coverPanel.weightx = 1.0;
    gbc_coverPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_coverPanel.gridx = 0;
    gbc_coverPanel.gridy = 1;
    add(getCoverPanel(), gbc_coverPanel);
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.insets = new Insets(27, 37, 22, 10);
    gbc_panel.fill = GridBagConstraints.BOTH;
    gbc_panel.anchor = GridBagConstraints.NORTHWEST;
    gbc_panel.gridx = 0;
    gbc_panel.gridy = 0;
    add(getScreenshotLabel(), gbc_panel);
    GridBagConstraints gbc_textPanel = new GridBagConstraints();
    gbc_textPanel.weightx = 1.0;
    gbc_textPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_textPanel.fill = GridBagConstraints.BOTH;
    gbc_textPanel.gridx = 1;
    gbc_textPanel.gridy = 0;
    add(getTextPanel(), gbc_textPanel);
    
    setBackground("/se/lantz/carousel/Carousel1400x788-modified.png");
  }

  private Image background;
  private JLabel lblNewLabel;
  private JLabel screenShotLabel;
  private TextPanel textPanel;
  private CoverPanel coverPanel;

  public void paintComponent(Graphics g) {

    int width = this.getSize().width;
    int height = this.getSize().height;

    if (this.background != null) {
      g.drawImage(this.background, 0, 0, width, height, null);
    }

    super.paintComponent(g);
  }

  public void setBackground(String imagePath) {
    
    this.setOpaque(false);
    this.background = new ImageIcon(getClass().getResource(imagePath)).getImage();
    repaint();
  }


  private JLabel getScreenshotLabel() {
    if (screenShotLabel == null) {
    	screenShotLabel = new JLabel();
    	screenShotLabel.setBackground(Color.YELLOW);
    	Image image = new ImageIcon(getClass().getResource("/se/lantz/carousel/test.png")).getImage();
    	Image scaledImage = image.getScaledInstance(694, 401, Image.SCALE_SMOOTH);
    	screenShotLabel.setIcon(new ImageIcon(scaledImage));
    }
    return screenShotLabel;
  }
  private TextPanel getTextPanel() {
    if (textPanel == null) {
    	textPanel = new TextPanel();
    }
    return textPanel;
  }
  private CoverPanel getCoverPanel() {
    if (coverPanel == null) {
    	coverPanel = new CoverPanel();
    }
    return coverPanel;
  }
}
