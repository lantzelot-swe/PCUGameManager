package se.lantz.gui.preferences;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.Beans;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import se.lantz.model.PreferencesModel;

public class VicePanel extends JPanel
{
  private ViceDetailsPanel mainWindowPanel;
  private ViceDetailsPanel carouselPanel;
  private PreferencesModel model;

  public VicePanel(PreferencesModel model)
  {
    this.model = model;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_mainWindowPanel = new GridBagConstraints();
    gbc_mainWindowPanel.weightx = 1.0;
    gbc_mainWindowPanel.anchor = GridBagConstraints.NORTH;
    gbc_mainWindowPanel.insets = new Insets(5, 5, 5, 5);
    gbc_mainWindowPanel.fill = GridBagConstraints.BOTH;
    gbc_mainWindowPanel.gridx = 0;
    gbc_mainWindowPanel.gridy = 0;
    add(getMainWindowPanel(), gbc_mainWindowPanel);
    GridBagConstraints gbc_carouselPanel = new GridBagConstraints();
    gbc_carouselPanel.insets = new Insets(5, 5, 0, 5);
    gbc_carouselPanel.anchor = GridBagConstraints.NORTH;
    gbc_carouselPanel.weighty = 1.0;
    gbc_carouselPanel.weightx = 1.0;
    gbc_carouselPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_carouselPanel.gridx = 0;
    gbc_carouselPanel.gridy = 1;
    add(getCarouselPanel(), gbc_carouselPanel);
  }
  
  private ViceDetailsPanel getMainWindowPanel()
  {
    if (mainWindowPanel == null)
    {
      mainWindowPanel = new ViceDetailsPanel(model, true);
      mainWindowPanel.setBorder(new TitledBorder(
                                                 new EtchedBorder(EtchedBorder.LOWERED,
                                                                  new Color(255, 255, 255),
                                                                  new Color(160, 160, 160)),
                                                 "Launching Vice from Main window",
                                                 TitledBorder.LEADING,
                                                 TitledBorder.TOP,
                                                 null,
                                                 new Color(0, 0, 0)));
    }
    return mainWindowPanel;
  }

  private ViceDetailsPanel getCarouselPanel()
  {
    if (carouselPanel == null)
    {
      carouselPanel = new ViceDetailsPanel(model, false);
      carouselPanel.setBorder(new TitledBorder(null,
                                               "Launching Vice from Carousel Preview",
                                               TitledBorder.LEADING,
                                               TitledBorder.TOP,
                                               null,
                                               null));
    }
    return carouselPanel;
  }
}
