package se.lantz.gui.preferences;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.Beans;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import se.lantz.model.PreferencesModel;

public class MiscPanel extends JPanel
{
  private JPanel startupPanel;
  private JCheckBox managerVersionCheckBox;
  private JCheckBox pcuaeVersionCheckBox;

  private PreferencesModel model;
  private SaveStatePrefPanel saveStatePrefPanel;
  private JPanel installPanel;
  private JCheckBox deleteOldInstallsCheckBox;
  private JPanel screenshotsPanel;
  private JCheckBox cropScreenCheckBox;
  private JCheckBox cropCoverCheckBox;

  public MiscPanel(PreferencesModel model)
  {
    this.model = model;

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0 };
    gridBagLayout.columnWeights = new double[] { 1.0 };
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
    gbc_installPanel.insets = new Insets(5, 5, 5, 3);
    gbc_installPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_installPanel.gridx = 0;
    gbc_installPanel.gridy = 1;
    add(getInstallPanel(), gbc_installPanel);
    GridBagConstraints gbc_screenshotsPanel = new GridBagConstraints();
    gbc_screenshotsPanel.weightx = 1.0;
    gbc_screenshotsPanel.insets = new Insets(5, 5, 5, 3);
    gbc_screenshotsPanel.fill = GridBagConstraints.BOTH;
    gbc_screenshotsPanel.gridx = 0;
    gbc_screenshotsPanel.gridy = 3;
    add(getScreenshotsPanel(), gbc_screenshotsPanel);
    GridBagConstraints gbc_saveStatePrefPanel = new GridBagConstraints();
    gbc_saveStatePrefPanel.insets = new Insets(5, 5, 0, 3);
    gbc_saveStatePrefPanel.weighty = 1.0;
    gbc_saveStatePrefPanel.anchor = GridBagConstraints.NORTHWEST;
    gbc_saveStatePrefPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_saveStatePrefPanel.gridx = 0;
    gbc_saveStatePrefPanel.gridy = 4;
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
                                              "Startup",
                                              TitledBorder.LEADING,
                                              TitledBorder.TOP,
                                              null,
                                              new Color(0, 0, 0)));
      GridBagLayout gbl_startupPanel = new GridBagLayout();
      startupPanel.setLayout(gbl_startupPanel);
      GridBagConstraints gbc_managerVersionCheckBox = new GridBagConstraints();
      gbc_managerVersionCheckBox.weightx = 1.0;
      gbc_managerVersionCheckBox.insets = new Insets(5, 0, 0, 0);
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
    getDeleteOldInstallsCheckBox().setSelected(model.isDeleteOldInstallfilesAfterDownload());
    getCropScreenCheckBox().setSelected(model.isCropScreenshots());
    getCropCoverCheckBox().setSelected(model.isShowCropDialogForCover());
  }

  private SaveStatePrefPanel getSaveStatePrefPanel()
  {
    if (saveStatePrefPanel == null)
    {
      saveStatePrefPanel = new SaveStatePrefPanel(this.model);
      saveStatePrefPanel
        .setBorder(new TitledBorder(null, "Saved states", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
                                              "PCUAE Install",
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

  private JPanel getScreenshotsPanel()
  {
    if (screenshotsPanel == null)
    {
      screenshotsPanel = new JPanel();
      screenshotsPanel
        .setBorder(new TitledBorder(null, "Screenshots & Cover", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_screenshotsPanel = new GridBagLayout();
      gbl_screenshotsPanel.columnWidths = new int[] { 0, 0 };
      gbl_screenshotsPanel.rowHeights = new int[] { 0, 0, 0 };
      gbl_screenshotsPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
      gbl_screenshotsPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
      screenshotsPanel.setLayout(gbl_screenshotsPanel);
      GridBagConstraints gbc_cropScreenCheckBox = new GridBagConstraints();
      gbc_cropScreenCheckBox.anchor = GridBagConstraints.NORTHWEST;
      gbc_cropScreenCheckBox.insets = new Insets(5, 0, 0, 0);
      gbc_cropScreenCheckBox.gridx = 0;
      gbc_cropScreenCheckBox.gridy = 0;
      screenshotsPanel.add(getCropScreenCheckBox(), gbc_cropScreenCheckBox);
      GridBagConstraints gbc_cropCoverCheckBox = new GridBagConstraints();
      gbc_cropCoverCheckBox.anchor = GridBagConstraints.NORTHWEST;
      gbc_cropCoverCheckBox.insets = new Insets(0, 0, 5, 0);
      gbc_cropCoverCheckBox.gridx = 0;
      gbc_cropCoverCheckBox.gridy = 1;
      screenshotsPanel.add(getCropCoverCheckBox(), gbc_cropCoverCheckBox);
    }
    return screenshotsPanel;
  }

  private JCheckBox getCropScreenCheckBox()
  {
    if (cropScreenCheckBox == null)
    {
      cropScreenCheckBox = new JCheckBox("Automatically crop screenshots to 320x200 pixels when added.");
      cropScreenCheckBox.setVerticalTextPosition(SwingConstants.TOP);
      cropScreenCheckBox.addItemListener((e) -> model.setCropScreenshots(cropScreenCheckBox.isSelected()));
    }
    return cropScreenCheckBox;
  }
  private JCheckBox getCropCoverCheckBox() {
    if (cropCoverCheckBox == null) {
    	cropCoverCheckBox = new JCheckBox("Show dialog to crop cover when added.");
    	cropCoverCheckBox.setVerticalTextPosition(SwingConstants.TOP);
    	cropCoverCheckBox.addItemListener((e) -> model.setShowCropDialogForCover(cropCoverCheckBox.isSelected()));
    }
    return cropCoverCheckBox;
  }
}
