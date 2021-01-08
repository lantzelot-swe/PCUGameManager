package se.lantz.gui.scraper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

public class ScreenshotsSelectionPanel extends JPanel
{
  private List<BufferedImage> screenshots;
  private List<ScreenshotCheckBoxPanel> screenshotCheckBoxList = new ArrayList<>();
  private JLabel infoLabel;
  private ScreensPanel screenPanel;
  private JScrollPane scrollPane;

  public ScreenshotsSelectionPanel(List<BufferedImage> screenshots)
  {
    this.screenshots = screenshots;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.anchor = GridBagConstraints.WEST;
    gbc_infoLabel.insets = new Insets(10, 10, 5, 0);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_scrollPane = new GridBagConstraints();
    gbc_scrollPane.insets = new Insets(0, 5, 5, 5);
    gbc_scrollPane.weighty = 1.0;
    gbc_scrollPane.weightx = 1.0;
    gbc_scrollPane.fill = GridBagConstraints.BOTH;
    gbc_scrollPane.gridx = 0;
    gbc_scrollPane.gridy = 1;
    add(getScrollPane(), gbc_scrollPane);
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel = new JLabel("Select two screenshots below:");
    }
    return infoLabel;
  }

  private ScreensPanel getScreenPanel()
  {
    if (screenPanel == null)
    {
      screenPanel = new ScreensPanel();
      for (int i = 0; i < screenshots.size(); i++)
      {
        ScreenshotCheckBoxPanel checkBox = new ScreenshotCheckBoxPanel();
        checkBox.getImageLabel().setIcon(new ImageIcon(screenshots.get(i)));
        checkBox.getCheckBox().setText("Screenshot " + (i + 1));
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

  private JScrollPane getScrollPane()
  {
    if (scrollPane == null)
    {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getScreenPanel());
      scrollPane.setPreferredSize(new Dimension(Math.min(1400, getScreenPanel().getPreferredSize().width + 2),
                                  Math.min(800, getScreenPanel().getPreferredSize().height + 2)));
    }
    return scrollPane;
  }
}

class ScreensPanel extends JPanel implements Scrollable
{
  
  public ScreensPanel()
  {
    this.setLayout(new GridLayout(2, 2, 5, 5));
  }

  @Override
  public Dimension getPreferredScrollableViewportSize()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
  {
    return 10;
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
  {
    return 40;
  }

  @Override
  public boolean getScrollableTracksViewportWidth()
  {
    return false;
  }

  @Override
  public boolean getScrollableTracksViewportHeight()
  {
    return false;
  }
  
}
