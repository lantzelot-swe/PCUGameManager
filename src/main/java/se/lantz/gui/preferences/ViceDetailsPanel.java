package se.lantz.gui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import se.lantz.model.PreferencesModel;

public class ViceDetailsPanel extends JPanel
{
  private JRadioButton fullscreenButton;
  private JRadioButton windowedButton;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JLabel primaryControllerLabel;
  private ViceInputComboBox primaryComboBox;
  private JLabel secondaryCOntrollerLabel;
  private ViceInputComboBox secondaryComboBox;
  private PreferencesModel model;
  private boolean mainWindow;

  public ViceDetailsPanel(PreferencesModel model, boolean mainWindow)
  {
    this.model = model;
    this.mainWindow = mainWindow;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWeights = new double[] { 0.0, 1.0 };
    setLayout(gridBagLayout);
    GridBagConstraints gbc_fullscreenButton = new GridBagConstraints();
    gbc_fullscreenButton.gridwidth = 2;
    gbc_fullscreenButton.insets = new Insets(10, 5, 0, 0);
    gbc_fullscreenButton.weightx = 1.0;
    gbc_fullscreenButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_fullscreenButton.gridx = 0;
    gbc_fullscreenButton.gridy = 0;
    add(getFullscreenButton(), gbc_fullscreenButton);
    GridBagConstraints gbc_windowedButton = new GridBagConstraints();
    gbc_windowedButton.weightx = 1.0;
    gbc_windowedButton.gridwidth = 2;
    gbc_windowedButton.insets = new Insets(0, 5, 5, 0);
    gbc_windowedButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_windowedButton.gridx = 0;
    gbc_windowedButton.gridy = 1;
    add(getWindowedButton(), gbc_windowedButton);
    GridBagConstraints gbc_primaryControllerLabel = new GridBagConstraints();
    gbc_primaryControllerLabel.insets = new Insets(23, 10, 5, 5);
    gbc_primaryControllerLabel.anchor = GridBagConstraints.WEST;
    gbc_primaryControllerLabel.gridx = 0;
    gbc_primaryControllerLabel.gridy = 2;
    add(getPrimaryControllerLabel(), gbc_primaryControllerLabel);
    GridBagConstraints gbc_primaryComboBox = new GridBagConstraints();
    gbc_primaryComboBox.insets = new Insets(20, 10, 5, 10);
    gbc_primaryComboBox.anchor = GridBagConstraints.WEST;
    gbc_primaryComboBox.weightx = 1.0;
    gbc_primaryComboBox.gridx = 1;
    gbc_primaryComboBox.gridy = 2;
    add(getPrimaryComboBox(), gbc_primaryComboBox);
    GridBagConstraints gbc_secondaryCOntrollerLabel = new GridBagConstraints();
    gbc_secondaryCOntrollerLabel.weighty = 1.0;
    gbc_secondaryCOntrollerLabel.anchor = GridBagConstraints.NORTHEAST;
    gbc_secondaryCOntrollerLabel.insets = new Insets(3, 10, 0, 5);
    gbc_secondaryCOntrollerLabel.gridx = 0;
    gbc_secondaryCOntrollerLabel.gridy = 3;
    add(getSecondaryControllerLabel(), gbc_secondaryCOntrollerLabel);
    GridBagConstraints gbc_secondaryComboBox = new GridBagConstraints();
    gbc_secondaryComboBox.insets = new Insets(0, 10, 5, 10);
    gbc_secondaryComboBox.anchor = GridBagConstraints.NORTHWEST;
    gbc_secondaryComboBox.weightx = 1.0;
    gbc_secondaryComboBox.weighty = 1.0;
    gbc_secondaryComboBox.gridx = 1;
    gbc_secondaryComboBox.gridy = 3;
    add(getSecondaryComboBox(), gbc_secondaryComboBox);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(e -> modelChanged());
      //Trigger an initial read from the model
      modelChanged();
    }
  }

  private void modelChanged()
  {
    getFullscreenButton().setSelected(mainWindow ? model.isLaunchViceFromMainWindowFullScreen() : model.isLaunchViceFromCarouselFullScreen());
    getPrimaryComboBox().setSelectedInput(mainWindow ? model.getMainWindowPrimaryInput() : model.getCarouselPrimaryInput());
    getSecondaryComboBox().setSelectedInput(mainWindow ? model.getMainWindowSecondaryInput() : model.getCarouselSecondaryInput());
  }

  private JRadioButton getFullscreenButton()
  {
    if (fullscreenButton == null)
    {
      fullscreenButton = new JRadioButton("Launch in fullscreen mode");
      fullscreenButton
      .addItemListener((e) -> {
        if (mainWindow)
        {
          model.setLaunchViceFromMainWindowFullScreen(fullscreenButton.isSelected());
        }
        else
        {
          model.setLaunchViceFromCarouselFullScreen(fullscreenButton.isSelected());
        }
      });
      buttonGroup.add(fullscreenButton);
    }
    return fullscreenButton;
  }

  private JRadioButton getWindowedButton()
  {
    if (windowedButton == null)
    {
      windowedButton = new JRadioButton("Launch in windowed mode");
      buttonGroup.add(windowedButton);
      windowedButton.setSelected(true);
    }
    return windowedButton;
  }

  private JLabel getPrimaryControllerLabel()
  {
    if (primaryControllerLabel == null)
    {
      primaryControllerLabel = new JLabel("Primary Joystick controller:");
    }
    return primaryControllerLabel;
  }

  private ViceInputComboBox getPrimaryComboBox()
  {
    if (primaryComboBox == null)
    {
      primaryComboBox = new ViceInputComboBox();
      primaryComboBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent arg0)
        {
          if (mainWindow)
          {
            model.setMainWindowPrimaryInput(primaryComboBox.getSelectedInput());
          }
          else
          {
            model.setCarouselPrimaryInput(primaryComboBox.getSelectedInput());
          }
        }
      });
    }
    return primaryComboBox;
  }

  private JLabel getSecondaryControllerLabel()
  {
    if (secondaryCOntrollerLabel == null)
    {
      secondaryCOntrollerLabel = new JLabel("Secondary Joystick controller:");
    }
    return secondaryCOntrollerLabel;
  }

  private ViceInputComboBox getSecondaryComboBox()
  {
    if (secondaryComboBox == null)
    {
      secondaryComboBox = new ViceInputComboBox();
      secondaryComboBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent arg0)
        {
          if (mainWindow)
          {
            model.setMainWindowSecondaryInput(secondaryComboBox.getSelectedInput());
          }
          else
          {
            model.setCarouselSecondaryInput(secondaryComboBox.getSelectedInput());
          }
        }
      });
    }
    return secondaryComboBox;
  }
}
