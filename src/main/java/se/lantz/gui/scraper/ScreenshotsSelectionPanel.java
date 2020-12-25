package se.lantz.gui.scraper;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScreenshotsSelectionPanel extends JPanel
{

  private List<BufferedImage> screenshots;
  private List<ScreenshotCheckBoxPanel> screenshotCheckBoxList = new ArrayList<>();
  private JLabel infoLabel;
  private JPanel screenPanel;

  public ScreenshotsSelectionPanel(List<BufferedImage> screenshots)
  {
    this.screenshots = screenshots;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.anchor = GridBagConstraints.WEST;
    gbc_infoLabel.insets = new Insets(10, 5, 5, 0);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_screenPanel = new GridBagConstraints();
    gbc_screenPanel.weighty = 1.0;
    gbc_screenPanel.weightx = 1.0;
    gbc_screenPanel.fill = GridBagConstraints.BOTH;
    gbc_screenPanel.gridx = 0;
    gbc_screenPanel.gridy = 1;
    add(getScreenPanel(), gbc_screenPanel);
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel = new JLabel("Select two screenshots below:");
    }
    return infoLabel;
  }

  private JPanel getScreenPanel()
  {
    if (screenPanel == null)
    {
      screenPanel = new JPanel();
      screenPanel.setLayout(new GridLayout(2, 2, 5, 5));
      for (int i = 0; i < screenshots.size(); i++)
      {
        ScreenshotCheckBoxPanel checkBox = new ScreenshotCheckBoxPanel();
        checkBox.getImageLabel().setIcon(new ImageIcon(screenshots.get(i)));
        screenshotCheckBoxList.add(checkBox);
        screenPanel.add(checkBox);
      }
    }
    return screenPanel;
  }
  
  public List<BufferedImage> getSelectedScreenshots()
  {
    List<BufferedImage> returnList = new ArrayList<>();
    for (int i = 0; i < screenshotCheckBoxList.size(); i++)
    {
      if (screenshotCheckBoxList.get(i).getCheckBox().isSelected())
      {
        returnList.add(screenshots.get(i));
      }
    }
    return returnList;
  }
}
