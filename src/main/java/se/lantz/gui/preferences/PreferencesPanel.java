package se.lantz.gui.preferences;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.Beans;
import java.util.Calendar;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import se.lantz.model.PreferencesModel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class PreferencesPanel extends JPanel
{
  private JPanel startupPanel;
  private JPanel infoSlotPanel;
  private JCheckBox managerVersionCheckBox;
  private JCheckBox pcuaeVersionCheckBox;

  private PreferencesModel model;
  private InfoSlotPreferencesPanel infoSlotPreferencesPanel;
  private JPanel favoritesPanel;
  private JLabel numberOfFavoritesLabel;
  private JSpinner favoritesSpinner;

  public PreferencesPanel()
  {
    model = new PreferencesModel();
    
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_startupPanel = new GridBagConstraints();
    gbc_startupPanel.insets = new Insets(5, 5, 5, 0);
    gbc_startupPanel.weightx = 1.0;
    gbc_startupPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_startupPanel.fill = GridBagConstraints.BOTH;
    gbc_startupPanel.gridx = 0;
    gbc_startupPanel.gridy = 0;
    add(getStartupPanel(), gbc_startupPanel);
    GridBagConstraints gbc_favoritesPanel = new GridBagConstraints();
    gbc_favoritesPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_favoritesPanel.weightx = 1.0;
    gbc_favoritesPanel.insets = new Insets(0, 0, 5, 0);
    gbc_favoritesPanel.fill = GridBagConstraints.BOTH;
    gbc_favoritesPanel.gridx = 0;
    gbc_favoritesPanel.gridy = 1;
    add(getFavoritesPanel(), gbc_favoritesPanel);
    GridBagConstraints gbc_infoSlotPanel = new GridBagConstraints();
    gbc_infoSlotPanel.weightx = 1.0;
    gbc_infoSlotPanel.weighty = 1.0;
    gbc_infoSlotPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_infoSlotPanel.insets = new Insets(5, 5, 0, 0);
    gbc_infoSlotPanel.fill = GridBagConstraints.BOTH;
    gbc_infoSlotPanel.gridx = 0;
    gbc_infoSlotPanel.gridy = 2;
    add(getInfoSlotPanel(), gbc_infoSlotPanel);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(e -> modelChanged());
      //Trigger an initial read from the model
      modelChanged();
      getInfoSlotPreferencesPanel().init();
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

  private JPanel getInfoSlotPanel()
  {
    if (infoSlotPanel == null)
    {
      infoSlotPanel = new JPanel();
      infoSlotPanel
        .setBorder(new TitledBorder(null, "Infoslot preferences", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_infoSlotPanel = new GridBagLayout();
      gbl_infoSlotPanel.columnWidths = new int[] { 0, 0 };
      gbl_infoSlotPanel.rowHeights = new int[] { 0, 0 };
      gbl_infoSlotPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
      gbl_infoSlotPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
      infoSlotPanel.setLayout(gbl_infoSlotPanel);
      GridBagConstraints gbc_infoSlotPreferencesPanel = new GridBagConstraints();
      gbc_infoSlotPreferencesPanel.fill = GridBagConstraints.BOTH;
      gbc_infoSlotPreferencesPanel.weighty = 1.0;
      gbc_infoSlotPreferencesPanel.weightx = 1.0;
      gbc_infoSlotPreferencesPanel.anchor = GridBagConstraints.NORTHWEST;
      gbc_infoSlotPreferencesPanel.insets = new Insets(0, 0, 0, 5);
      gbc_infoSlotPreferencesPanel.gridx = 0;
      gbc_infoSlotPreferencesPanel.gridy = 0;
      infoSlotPanel.add(getInfoSlotPreferencesPanel(), gbc_infoSlotPreferencesPanel);
    }
    return infoSlotPanel;
  }

  private JCheckBox getManagerVersionCheckBox()
  {
    if (managerVersionCheckBox == null)
    {
      managerVersionCheckBox = new JCheckBox("Check for new version of PCUAE Manager at startup.");
      managerVersionCheckBox.addItemListener((e) -> model.setCheckManagerVersionAtStartup(managerVersionCheckBox.isSelected()));
    }
    return managerVersionCheckBox;
  }

  private JCheckBox getPcuaeVersionCheckBox()
  {
    if (pcuaeVersionCheckBox == null)
    {
      pcuaeVersionCheckBox = new JCheckBox("Check for new version of PCUAE main install at startup.");
      pcuaeVersionCheckBox.addItemListener((e) -> model.setCheckPCUAEVersionAtStartup(pcuaeVersionCheckBox.isSelected()));
    }
    return pcuaeVersionCheckBox;
  }

  public void savePreferences()
  {
    model.savePreferences();
  }

  private void modelChanged()
  {
    getManagerVersionCheckBox().setSelected(model.isCheckManagerVersionAtStartup());
    getPcuaeVersionCheckBox().setSelected(model.isCheckPCUAEVersionAtStartup());
    if (!getFavoritesSpinner().hasFocus() && !getFavoritesSpinner().getValue().equals(model.getFavoritesCount()))
    {
      getFavoritesSpinner().setValue(model.getFavoritesCount());
    }
  }
  private InfoSlotPreferencesPanel getInfoSlotPreferencesPanel() {
    if (infoSlotPreferencesPanel == null) {
    	infoSlotPreferencesPanel = new InfoSlotPreferencesPanel(model);
    }
    return infoSlotPreferencesPanel;
  }
  private JPanel getFavoritesPanel() {
    if (favoritesPanel == null) {
    	favoritesPanel = new JPanel();
    	favoritesPanel.setBorder(new TitledBorder(null, "Favorites preferences", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
  private JLabel getNumberOfFavoritesLabel() {
    if (numberOfFavoritesLabel == null) {
    	numberOfFavoritesLabel = new JLabel("Number of favorites lists");
    }
    return numberOfFavoritesLabel;
  }
  private JSpinner getFavoritesSpinner() {
    if (favoritesSpinner == null) {
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
}
