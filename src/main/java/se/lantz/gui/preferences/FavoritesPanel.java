package se.lantz.gui.preferences;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.border.TitledBorder;

import se.lantz.model.PreferencesModel;

import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.Beans;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class FavoritesPanel extends JPanel
{
  private JPanel favoritesPanel;
  private JLabel numberOfFavoritesLabel;
  private JSpinner favoritesSpinner;
  private JPanel aliasPanel;

  private PreferencesModel model;
  private AliasPanel aliasPanel_1;
  private AliasPanel aliasPanel_2;
  private AliasPanel aliasPanel_3;
  private AliasPanel aliasPanel_4;
  private AliasPanel aliasPanel_5;
  private AliasPanel aliasPanel_6;
  private AliasPanel aliasPanel_7;
  private AliasPanel aliasPanel_8;
  private AliasPanel aliasPanel_9;
  private AliasPanel aliasPanel_10;
  private JLabel aliasInfoLabel;

  public FavoritesPanel(PreferencesModel model)
  {
    this.model = model;

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_favoritesPanel = new GridBagConstraints();
    gbc_favoritesPanel.weightx = 1.0;
    gbc_favoritesPanel.insets = new Insets(5, 5, 5, 3);
    gbc_favoritesPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_favoritesPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_favoritesPanel.gridx = 0;
    gbc_favoritesPanel.gridy = 0;
    add(getFavoritesPanel(), gbc_favoritesPanel);
    GridBagConstraints gbc_aliasPanel = new GridBagConstraints();
    gbc_aliasPanel.weightx = 1.0;
    gbc_aliasPanel.weighty = 1.0;
    gbc_aliasPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_aliasPanel.fill = GridBagConstraints.BOTH;
    gbc_aliasPanel.gridx = 0;
    gbc_aliasPanel.gridy = 1;
    add(getAliasPanel(), gbc_aliasPanel);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(e -> modelChanged());
      //Trigger an initial read from the model
      modelChanged();
    }
  }

  private JPanel getFavoritesPanel()
  {
    if (favoritesPanel == null)
    {
      favoritesPanel = new JPanel();
      favoritesPanel.setBorder(new TitledBorder(

                                                new EtchedBorder(EtchedBorder.LOWERED,

                                                                 new Color(255, 255, 255),

                                                                 new Color(160, 160, 160)),

                                                "Favorites",

                                                TitledBorder.LEADING,

                                                TitledBorder.TOP,

                                                null,

                                                new Color(0, 0, 0)));
      GridBagLayout gbl_favoritesPanel = new GridBagLayout();
      gbl_favoritesPanel.columnWidths = new int[] { 0, 0, 0 };
      gbl_favoritesPanel.rowHeights = new int[] { 0, 0 };
      gbl_favoritesPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
      gbl_favoritesPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
      favoritesPanel.setLayout(gbl_favoritesPanel);
      GridBagConstraints gbc_numberOfFavoritesLabel = new GridBagConstraints();
      gbc_numberOfFavoritesLabel.anchor = GridBagConstraints.NORTH;
      gbc_numberOfFavoritesLabel.insets = new Insets(7, 5, 0, 5);
      gbc_numberOfFavoritesLabel.gridx = 0;
      gbc_numberOfFavoritesLabel.gridy = 0;
      favoritesPanel.add(getNumberOfFavoritesLabel(), gbc_numberOfFavoritesLabel);
      GridBagConstraints gbc_favoritesSpinner = new GridBagConstraints();
      gbc_favoritesSpinner.weightx = 1.0;
      gbc_favoritesSpinner.insets = new Insets(5, 5, 5, 0);
      gbc_favoritesSpinner.anchor = GridBagConstraints.WEST;
      gbc_favoritesSpinner.gridx = 1;
      gbc_favoritesSpinner.gridy = 0;
      favoritesPanel.add(getFavoritesSpinner(), gbc_favoritesSpinner);
    }
    return favoritesPanel;
  }

  private JLabel getNumberOfFavoritesLabel()
  {
    if (numberOfFavoritesLabel == null)
    {
      numberOfFavoritesLabel = new JLabel("Number of favorites lists");
    }
    return numberOfFavoritesLabel;
  }

  private JSpinner getFavoritesSpinner()
  {
    if (favoritesSpinner == null)
    {
      SpinnerModel spinnerModel = new SpinnerNumberModel(10, // initial value
                                                         1, // min
                                                         10, // max
                                                         1);
      favoritesSpinner = new JSpinner(spinnerModel);
      JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(favoritesSpinner, "####");
      favoritesSpinner.setEditor(numberEditor);
      // Select all when gaining focus
      numberEditor.getTextField().addFocusListener(new FocusAdapter()
        {
          @Override
          public void focusGained(final FocusEvent e)
          {
            SwingUtilities.invokeLater(() -> {
              JTextField tf = (JTextField) e.getSource();
              tf.selectAll();
            });
          }
        });

      favoritesSpinner.addChangeListener(e -> {
        JSpinner textField = (JSpinner) e.getSource();
        model.setFavoritesCount(Integer.parseInt(textField.getValue().toString()));
      });
    }
    return favoritesSpinner;
  }

  private JPanel getAliasPanel()
  {
    if (aliasPanel == null)
    {
      aliasPanel = new JPanel();
      GridBagLayout gbl_aliasPanel = new GridBagLayout();
      gbl_aliasPanel.columnWidths = new int[] { 0, 0 };
      gbl_aliasPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      gbl_aliasPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
      gbl_aliasPanel.rowWeights =
        new double[] { 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
      aliasPanel.setLayout(gbl_aliasPanel);
      GridBagConstraints gbc_aliasInfoLabel = new GridBagConstraints();
      gbc_aliasInfoLabel.anchor = GridBagConstraints.WEST;
      gbc_aliasInfoLabel.insets = new Insets(5, 5, 5, 0);
      gbc_aliasInfoLabel.gridx = 0;
      gbc_aliasInfoLabel.gridy = 0;
      aliasPanel.add(getAliasInfoLabel(), gbc_aliasInfoLabel);
      GridBagConstraints gbc_aliasPanel_2 = new GridBagConstraints();
      gbc_aliasPanel_2.insets = new Insets(0, 0, 5, 0);
      gbc_aliasPanel_2.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_2.gridx = 0;
      gbc_aliasPanel_2.gridy = 2;
      aliasPanel.add(getAliasPanel_2(), gbc_aliasPanel_2);
      GridBagConstraints gbc_aliasPanel_1 = new GridBagConstraints();
      gbc_aliasPanel_1.insets = new Insets(0, 0, 5, 0);
      gbc_aliasPanel_1.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_1.gridx = 0;
      gbc_aliasPanel_1.gridy = 1;
      aliasPanel.add(getAliasPanel_1(), gbc_aliasPanel_1);
      GridBagConstraints gbc_aliasPanel_3 = new GridBagConstraints();
      gbc_aliasPanel_3.insets = new Insets(0, 0, 5, 0);
      gbc_aliasPanel_3.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_3.gridx = 0;
      gbc_aliasPanel_3.gridy = 3;
      aliasPanel.add(getAliasPanel_3(), gbc_aliasPanel_3);
      GridBagConstraints gbc_aliasPanel_4 = new GridBagConstraints();
      gbc_aliasPanel_4.insets = new Insets(0, 0, 5, 0);
      gbc_aliasPanel_4.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_4.gridx = 0;
      gbc_aliasPanel_4.gridy = 4;
      aliasPanel.add(getAliasPanel_4(), gbc_aliasPanel_4);
      GridBagConstraints gbc_aliasPanel_5 = new GridBagConstraints();
      gbc_aliasPanel_5.insets = new Insets(0, 0, 5, 0);
      gbc_aliasPanel_5.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_5.gridx = 0;
      gbc_aliasPanel_5.gridy = 5;
      aliasPanel.add(getAliasPanel_5(), gbc_aliasPanel_5);
      GridBagConstraints gbc_aliasPanel_6 = new GridBagConstraints();
      gbc_aliasPanel_6.insets = new Insets(0, 0, 5, 0);
      gbc_aliasPanel_6.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_6.gridx = 0;
      gbc_aliasPanel_6.gridy = 6;
      aliasPanel.add(getAliasPanel_6(), gbc_aliasPanel_6);
      GridBagConstraints gbc_aliasPanel_7 = new GridBagConstraints();
      gbc_aliasPanel_7.insets = new Insets(0, 0, 5, 0);
      gbc_aliasPanel_7.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_7.gridx = 0;
      gbc_aliasPanel_7.gridy = 7;
      aliasPanel.add(getAliasPanel_7(), gbc_aliasPanel_7);
      GridBagConstraints gbc_aliasPanel_8 = new GridBagConstraints();
      gbc_aliasPanel_8.insets = new Insets(0, 0, 5, 0);
      gbc_aliasPanel_8.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_8.gridx = 0;
      gbc_aliasPanel_8.gridy = 8;
      aliasPanel.add(getAliasPanel_8(), gbc_aliasPanel_8);
      GridBagConstraints gbc_aliasPanel_9 = new GridBagConstraints();
      gbc_aliasPanel_9.insets = new Insets(0, 0, 5, 0);
      gbc_aliasPanel_9.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_9.gridx = 0;
      gbc_aliasPanel_9.gridy = 9;
      aliasPanel.add(getAliasPanel_9(), gbc_aliasPanel_9);
      GridBagConstraints gbc_aliasPanel_10 = new GridBagConstraints();
      gbc_aliasPanel_10.fill = GridBagConstraints.BOTH;
      gbc_aliasPanel_10.gridx = 0;
      gbc_aliasPanel_10.gridy = 10;
      aliasPanel.add(getAliasPanel_10(), gbc_aliasPanel_10);
    }
    return aliasPanel;
  }

  private AliasPanel getAliasPanel_1()
  {
    if (aliasPanel_1 == null)
    {
      aliasPanel_1 = new AliasPanel(1);
    }
    return aliasPanel_1;
  }

  private AliasPanel getAliasPanel_2()
  {
    if (aliasPanel_2 == null)
    {
      aliasPanel_2 = new AliasPanel(2);
    }
    return aliasPanel_2;
  }

  private AliasPanel getAliasPanel_3()
  {
    if (aliasPanel_3 == null)
    {
      aliasPanel_3 = new AliasPanel(3);
    }
    return aliasPanel_3;
  }

  private AliasPanel getAliasPanel_4()
  {
    if (aliasPanel_4 == null)
    {
      aliasPanel_4 = new AliasPanel(4);
    }
    return aliasPanel_4;
  }

  private AliasPanel getAliasPanel_5()
  {
    if (aliasPanel_5 == null)
    {
      aliasPanel_5 = new AliasPanel(5);
    }
    return aliasPanel_5;
  }

  private AliasPanel getAliasPanel_6()
  {
    if (aliasPanel_6 == null)
    {
      aliasPanel_6 = new AliasPanel(6);
    }
    return aliasPanel_6;
  }

  private AliasPanel getAliasPanel_7()
  {
    if (aliasPanel_7 == null)
    {
      aliasPanel_7 = new AliasPanel(7);
    }
    return aliasPanel_7;
  }

  private AliasPanel getAliasPanel_8()
  {
    if (aliasPanel_8 == null)
    {
      aliasPanel_8 = new AliasPanel(8);
    }
    return aliasPanel_8;
  }

  private AliasPanel getAliasPanel_9()
  {
    if (aliasPanel_9 == null)
    {
      aliasPanel_9 = new AliasPanel(9);
    }
    return aliasPanel_9;
  }

  private AliasPanel getAliasPanel_10()
  {
    if (aliasPanel_10 == null)
    {
      aliasPanel_10 = new AliasPanel(10);
    }
    return aliasPanel_10;
  }

  private JLabel getAliasInfoLabel()
  {
    if (aliasInfoLabel == null)
    {
      aliasInfoLabel = new JLabel("Aliases for the favorites gameviews");
    }
    return aliasInfoLabel;
  }

  private void modelChanged()
  {
    if (!getFavoritesSpinner().hasFocus() && !getFavoritesSpinner().getValue().equals(model.getFavoritesCount()))
    {
      getFavoritesSpinner().setValue(model.getFavoritesCount());
    }
    getAliasPanel_1().getTextField().setText(model.getFav1Alias());
    getAliasPanel_2().getTextField().setText(model.getFav2Alias());
    getAliasPanel_3().getTextField().setText(model.getFav3Alias());
    getAliasPanel_4().getTextField().setText(model.getFav4Alias());
    getAliasPanel_5().getTextField().setText(model.getFav5Alias());
    getAliasPanel_6().getTextField().setText(model.getFav6Alias());
    getAliasPanel_7().getTextField().setText(model.getFav7Alias());
    getAliasPanel_8().getTextField().setText(model.getFav8Alias());
    getAliasPanel_9().getTextField().setText(model.getFav9Alias());
    getAliasPanel_10().getTextField().setText(model.getFav10Alias());
  }

  public void savePreferences()
  {
    model.setFav1Alias(getAliasPanel_1().getTextField().getText());
    model.setFav2Alias(getAliasPanel_2().getTextField().getText());
    model.setFav3Alias(getAliasPanel_3().getTextField().getText());
    model.setFav4Alias(getAliasPanel_4().getTextField().getText());
    model.setFav5Alias(getAliasPanel_5().getTextField().getText());
    model.setFav6Alias(getAliasPanel_6().getTextField().getText());
    model.setFav7Alias(getAliasPanel_7().getTextField().getText());
    model.setFav8Alias(getAliasPanel_8().getTextField().getText());
    model.setFav9Alias(getAliasPanel_9().getTextField().getText());
    model.setFav10Alias(getAliasPanel_10().getTextField().getText());
  }
}
