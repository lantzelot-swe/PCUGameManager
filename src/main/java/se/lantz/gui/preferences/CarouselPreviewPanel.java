package se.lantz.gui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.Beans;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import se.lantz.model.PreferencesModel;
import java.awt.Insets;

public class CarouselPreviewPanel extends JPanel
{
  private JCheckBox fullscreenCheckBox;
  private PreferencesModel model;

  public CarouselPreviewPanel(PreferencesModel model)
  {
    this.model = model;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_fullscreenCheckBox = new GridBagConstraints();
    gbc_fullscreenCheckBox.insets = new Insets(5, 0, 5, 5);
    gbc_fullscreenCheckBox.weighty = 1.0;
    gbc_fullscreenCheckBox.weightx = 1.0;
    gbc_fullscreenCheckBox.anchor = GridBagConstraints.NORTHWEST;
    gbc_fullscreenCheckBox.gridx = 0;
    gbc_fullscreenCheckBox.gridy = 0;
    add(getFullscreenCheckBox(), gbc_fullscreenCheckBox);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(e -> modelChanged());
      //Trigger an initial read from the model
      modelChanged();
    }
  }
  
  private void modelChanged()
  {
    getFullscreenCheckBox().setSelected(model.isStartCarouselInFullscreen());
  }

  private JCheckBox getFullscreenCheckBox()
  {
    if (fullscreenCheckBox == null)
    {
      fullscreenCheckBox = new JCheckBox("Launch Carousel Preview dialog in fullscreen mode.");
      fullscreenCheckBox
      .addItemListener((e) -> model.setStartCarouselInFullscreen(fullscreenCheckBox.isSelected()));
    }
    return fullscreenCheckBox;
  }
}
