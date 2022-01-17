package se.lantz.gui.preferences;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.Beans;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import se.lantz.model.PreferencesModel;

public class MiscPanel extends JPanel
{
  private JPanel startupPanel;
  private JCheckBox managerVersionCheckBox;
  private JCheckBox pcuaeVersionCheckBox;

  private PreferencesModel model;
  private JPanel favoritesPanel;
  private JLabel numberOfFavoritesLabel;
  private JSpinner favoritesSpinner;
  private SaveStatePrefPanel saveStatePrefPanel;
  private JPanel installPanel;
  private JCheckBox deleteOldInstallsCheckBox;

  public MiscPanel(PreferencesModel model)
  {
    this.model = model;

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_startupPanel = new GridBagConstraints();
    gbc_startupPanel.insets = new Insets(5, 5, 5, 3);
    gbc_startupPanel.weightx = 1.0;
    gbc_startupPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_startupPanel.fill = GridBagConstraints.BOTH;
    gbc_startupPanel.gridx = 0;
    gbc_startupPanel.gridy = 0;
    add(getStartupPanel(), gbc_startupPanel);
    GridBagConstraints gbc_installPanel = new GridBagConstraints();
    gbc_installPanel.weightx = 1.0;
    gbc_installPanel.insets = new Insets(5, 5, 5, 5);
    gbc_installPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_installPanel.gridx = 0;
    gbc_installPanel.gridy = 1;
    add(getInstallPanel(), gbc_installPanel);
    GridBagConstraints gbc_favoritesPanel = new GridBagConstraints();
    gbc_favoritesPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_favoritesPanel.weightx = 1.0;
    gbc_favoritesPanel.insets = new Insets(5, 5, 5, 3);
    gbc_favoritesPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_favoritesPanel.gridx = 0;
    gbc_favoritesPanel.gridy = 2;
    add(getFavoritesPanel(), gbc_favoritesPanel);
    GridBagConstraints gbc_saveStatePrefPanel = new GridBagConstraints();
    gbc_saveStatePrefPanel.insets = new Insets(5, 5, 0, 3);
    gbc_saveStatePrefPanel.weighty = 1.0;
    gbc_saveStatePrefPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_saveStatePrefPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_saveStatePrefPanel.gridx = 0;
    gbc_saveStatePrefPanel.gridy = 3;
    add(getSaveStatePrefPanel(), gbc_saveStatePrefPanel);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(e -> modelChanged());
      //Trigger an initial read from the model
      modelChanged();
    }
  }

  private JPanel getStartupPanel()
  {
    if (startupPanel == null)
    {
      startupPanel = new JPanel();
      startupPanel.setBorder(new TitledBorder(
                                              new EtchedBorder(EtchedBorder.LOWERED,
                                                               new Color(255, 255, 255),
                                                               new Color(160, 160, 160)),
                                              "Startup preferences",
                                              TitledBorder.LEADING,
                                              TitledBorder.TOP,
                                              null,
                                              new Color(0, 0, 0)));
      GridBagLayout gbl_startupPanel = new GridBagLayout();
      startupPanel.setLayout(gbl_startupPanel);
      GridBagConstraints gbc_managerVersionCheckBox = new GridBagConstraints();
      gbc_managerVersionCheckBox.weightx = 1.0;
      gbc_managerVersionCheckBox.insets = new Insets(5, 0, 5, 0);
      gbc_managerVersionCheckBox.anchor = GridBagConstraints.WEST;
      gbc_managerVersionCheckBox.gridx = 0;
      gbc_managerVersionCheckBox.gridy = 0;
      startupPanel.add(getManagerVersionCheckBox(), gbc_managerVersionCheckBox);
      GridBagConstraints gbc_pcuaeVersionCheckBox = new GridBagConstraints();
      gbc_pcuaeVersionCheckBox.insets = new Insets(0, 0, 5, 0);
      gbc_pcuaeVersionCheckBox.weighty = 1.0;
      gbc_pcuaeVersionCheckBox.weightx = 1.0;
      gbc_pcuaeVersionCheckBox.anchor = GridBagConstraints.NORTHWEST;
      gbc_pcuaeVersionCheckBox.gridx = 0;
      gbc_pcuaeVersionCheckBox.gridy = 1;
      startupPanel.add(getPcuaeVersionCheckBox(), gbc_pcuaeVersionCheckBox);
    }
    return startupPanel;
  }

  private JCheckBox getManagerVersionCheckBox()
  {
    if (managerVersionCheckBox == null)
    {
      managerVersionCheckBox = new JCheckBox("Check for new version of PCUAE Manager at startup.");
      managerVersionCheckBox
        .addItemListener((e) -> model.setCheckManagerVersionAtStartup(managerVersionCheckBox.isSelected()));
    }
    return managerVersionCheckBox;
  }

  private JCheckBox getPcuaeVersionCheckBox()
  {
    if (pcuaeVersionCheckBox == null)
    {
      pcuaeVersionCheckBox = new JCheckBox("Check for new version of PCUAE main install at startup.");
      pcuaeVersionCheckBox
        .addItemListener((e) -> model.setCheckPCUAEVersionAtStartup(pcuaeVersionCheckBox.isSelected()));
    }
    return pcuaeVersionCheckBox;
  }

  private void modelChanged()
  {
    getManagerVersionCheckBox().setSelected(model.isCheckManagerVersionAtStartup());
    getPcuaeVersionCheckBox().setSelected(model.isCheckPCUAEVersionAtStartup());
    if (!getFavoritesSpinner().hasFocus() && !getFavoritesSpinner().getValue().equals(model.getFavoritesCount()))
    {
      getFavoritesSpinner().setValue(model.getFavoritesCount());
    }
    getDeleteOldInstallsCheckBox().setSelected(model.isDeleteOldInstallfilesAfterDownload());
  }

  private JPanel getFavoritesPanel()
  {
    if (favoritesPanel == null)
    {
      favoritesPanel = new JPanel();
      favoritesPanel
        .setBorder(new TitledBorder(null, "Favorites preferences", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_favoritesPanel = new GridBagLayout();
      favoritesPanel.setLayout(gbl_favoritesPanel);
      GridBagConstraints gbc_numberOfFavoritesLabel = new GridBagConstraints();
      gbc_numberOfFavoritesLabel.insets = new Insets(5, 5, 5, 5);
      gbc_numberOfFavoritesLabel.gridx = 0;
      gbc_numberOfFavoritesLabel.gridy = 0;
      favoritesPanel.add(getNumberOfFavoritesLabel(), gbc_numberOfFavoritesLabel);
      GridBagConstraints gbc_favoritesSpinner = new GridBagConstraints();
      gbc_favoritesSpinner.anchor = GridBagConstraints.WEST;
      gbc_favoritesSpinner.weightx = 1.0;
      gbc_favoritesSpinner.insets = new Insets(5, 5, 5, 0);
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

  private SaveStatePrefPanel getSaveStatePrefPanel()
  {
    if (saveStatePrefPanel == null)
    {
      saveStatePrefPanel = new SaveStatePrefPanel(this.model);
      saveStatePrefPanel.setBorder(new TitledBorder(null,
                                                    "Saved states preferences",
                                                    TitledBorder.LEADING,
                                                    TitledBorder.TOP,
                                                    null,
                                                    null));
    }
    return saveStatePrefPanel;
  }

  private JPanel getInstallPanel()
  {
    if (installPanel == null)
    {
      installPanel = new JPanel();
      installPanel.setBorder(new TitledBorder(
                                              new EtchedBorder(EtchedBorder.LOWERED,
                                                               new Color(255, 255, 255),
                                                               new Color(160, 160, 160)),
                                              "PCUAE Install preferences",
                                              TitledBorder.LEADING,
                                              TitledBorder.TOP,
                                              null,
                                              new Color(0, 0, 0)));
      GridBagLayout gbl_installPanel = new GridBagLayout();
      installPanel.setLayout(gbl_installPanel);
      GridBagConstraints gbc_deleteOldInstallsCheckBox = new GridBagConstraints();
      gbc_deleteOldInstallsCheckBox.fill = GridBagConstraints.HORIZONTAL;
      gbc_deleteOldInstallsCheckBox.insets = new Insets(5, 0, 5, 5);
      gbc_deleteOldInstallsCheckBox.weightx = 1.0;
      gbc_deleteOldInstallsCheckBox.anchor = GridBagConstraints.NORTHWEST;
      gbc_deleteOldInstallsCheckBox.gridx = 0;
      gbc_deleteOldInstallsCheckBox.gridy = 0;
      installPanel.add(getDeleteOldInstallsCheckBox(), gbc_deleteOldInstallsCheckBox);
    }
    return installPanel;
  }

  private JCheckBox getDeleteOldInstallsCheckBox()
  {
    if (deleteOldInstallsCheckBox == null)
    {
      deleteOldInstallsCheckBox =
        new JCheckBox("<html>Delete old install files from pcuae-install folder after downloading a new version.</html>");
      deleteOldInstallsCheckBox.setVerticalTextPosition(SwingConstants.TOP);
      deleteOldInstallsCheckBox
        .addItemListener((e) -> model.setDeleteOldInstallfilesAfterDownload(deleteOldInstallsCheckBox.isSelected()));
    }
    return deleteOldInstallsCheckBox;
  }
}
