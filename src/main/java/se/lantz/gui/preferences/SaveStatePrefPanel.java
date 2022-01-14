package se.lantz.gui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Beans;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import se.lantz.model.PreferencesModel;

public class SaveStatePrefPanel extends JPanel
{
  private JLabel infoLabel;
  private JRadioButton v132RadioButton;
  private JRadioButton v152RadioButton;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private PreferencesModel model;

  public SaveStatePrefPanel(PreferencesModel model)
  {
    this.model = model;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.insets = new Insets(10, 10, 10, 10);
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_infoLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_v132RadioButton = new GridBagConstraints();
    gbc_v132RadioButton.anchor = GridBagConstraints.WEST;
    gbc_v132RadioButton.weightx = 1.0;
    gbc_v132RadioButton.insets = new Insets(0, 40, 0, 0);
    gbc_v132RadioButton.gridx = 0;
    gbc_v132RadioButton.gridy = 1;
    add(getV132RadioButton(), gbc_v132RadioButton);
    GridBagConstraints gbc_v152RadioButton = new GridBagConstraints();
    gbc_v152RadioButton.weighty = 1.0;
    gbc_v152RadioButton.insets = new Insets(0, 40, 10, 0);
    gbc_v152RadioButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_v152RadioButton.weightx = 1.0;
    gbc_v152RadioButton.gridx = 0;
    gbc_v152RadioButton.gridy = 2;
    add(getV152RadioButton(), gbc_v152RadioButton);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(e -> modelChanged());
      //Trigger an initial read from the model
      modelChanged();
    }
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      String info = "<html>Different versions of the Carousel adds the saved states in different folders. " +
        "You have to choose which verison of the Carousel you want the manager to read the saved states for. " +
        "Only saved states for one carousel version at a time is shown for the games in the game list views.</html>";
      infoLabel = new JLabel(info);
    }
    return infoLabel;
  }

  private JRadioButton getV132RadioButton()
  {
    if (v132RadioButton == null)
    {
      v132RadioButton = new JRadioButton("Carousel version 1.3.2 and earlier.");
      v132RadioButton.addItemListener(new ItemListener()
        {
          public void itemStateChanged(ItemEvent e)
          {
            if (v132RadioButton.isSelected())
            {
              model.setSavedStatesCarouselVersion(PreferencesModel.CAROUSEL_132);
            }
          }
        });
      buttonGroup.add(v132RadioButton);
    }
    return v132RadioButton;
  }

  private JRadioButton getV152RadioButton()
  {
    if (v152RadioButton == null)
    {
      v152RadioButton = new JRadioButton("Carousel version 1.5.2 and later.");
      v152RadioButton.setSelected(true);
      v152RadioButton.addItemListener(new ItemListener()
        {
          public void itemStateChanged(ItemEvent e)
          {
            if (v152RadioButton.isSelected())
            {
              model.setSavedStatesCarouselVersion(PreferencesModel.CAROUSEL_152);
            }
          }
        });
      buttonGroup.add(v152RadioButton);
    }
    return v152RadioButton;
  }

  private void modelChanged()
  {
    getV132RadioButton().setSelected(model.getSavedStatesCarouselVersion().equals(PreferencesModel.CAROUSEL_132));
  }
}
