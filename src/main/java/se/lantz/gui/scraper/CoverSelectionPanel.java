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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

public class CoverSelectionPanel extends JPanel
{
  private List<BufferedImage> covers;
  private List<CoverRadioButtonPanel> coverRadioButtonList = new ArrayList<>();
  private JLabel infoLabel;
  private CoversPanel coverPanel;
  private JScrollPane scrollPane;
  private ButtonGroup buttonGroup = new ButtonGroup();

  public CoverSelectionPanel(List<BufferedImage> covers)
  {
    this.covers = covers;
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
    gbc_scrollPane.insets = new Insets(5, 10, 10, 10);
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
      infoLabel = new JLabel("Select one cover below:");
    }
    return infoLabel;
  }

  private CoversPanel getCoverPanel()
  {
    if (coverPanel == null)
    {
      coverPanel = new CoversPanel();
      for (int i = 0; i < covers.size(); i++)
      {
        CoverRadioButtonPanel radioButton = new CoverRadioButtonPanel();
        radioButton.getImageLabel().setIcon(new ImageIcon(covers.get(i)));
        radioButton.getRadioButton().setText("Cover " + (i + 1));
        coverRadioButtonList.add(radioButton);
        coverPanel.add(radioButton);
        buttonGroup.add(radioButton.getRadioButton());
        if (i == 0)
        {
          radioButton.getRadioButton().setSelected(true);
        }
      }
    }
    return coverPanel;
  }

  public BufferedImage getSelectedCover()
  {
    for (int i = 0; i < coverRadioButtonList.size(); i++)
    {
      if (coverRadioButtonList.get(i).getRadioButton().isSelected())
      {
        return covers.get(i);
      }
    }
    return null;
  }

  private JScrollPane getScrollPane()
  {
    if (scrollPane == null)
    {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getCoverPanel());
      scrollPane.setPreferredSize(new Dimension(Math.min(1400, getCoverPanel().getPreferredSize().width + 2),
                                  Math.min(800, getCoverPanel().getPreferredSize().height + 2)));
      scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }
    return scrollPane;
  }
}

class CoversPanel extends JPanel implements Scrollable
{
  
  public CoversPanel()
  {
    this.setLayout(new GridLayout(1, 0, 5, 5));
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
