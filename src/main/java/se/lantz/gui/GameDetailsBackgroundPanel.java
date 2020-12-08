package se.lantz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.Beans;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameListData;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GameDetailsBackgroundPanel extends JPanel
{
  private static final Logger logger = LoggerFactory.getLogger(GameDetailsBackgroundPanel.class);
  private MainViewModel model;
  private JPanel settingsPanel;
  private InfoPanel infoPanel;
  private CombinedJoystickPanel joystickPanel;
  private SystemPanel systemPanel;
  private JPanel buttonPanel;
  private JButton saveButton;

  public GameDetailsBackgroundPanel(MainViewModel model)
  {
    this.model = model;
    this.setMinimumSize(new Dimension(1250, 800));
    setLayout(new BorderLayout(0, 0));
    add(getSettingsPanel(), BorderLayout.CENTER);
    add(getInfoPanel(), BorderLayout.NORTH);
    add(getButtonPanel(), BorderLayout.SOUTH);

  }
  
  void focusTitleField()
  {
    getInfoPanel().focusTitleField();
  }

  void updateSelectedGame(GameListData data)
  {
    if (data != null)
    {
      model.readGameDetails(data);
    }
  }

  private JPanel getSettingsPanel()
  {
    if (settingsPanel == null)
    {
      settingsPanel = new JPanel();
      GridBagLayout gbl_settingsPanel = new GridBagLayout();
      settingsPanel.setLayout(gbl_settingsPanel);
      GridBagConstraints gbc_systemPanel = new GridBagConstraints();
      gbc_systemPanel.weightx = 1.0;
      gbc_systemPanel.weighty = 1.0;
      gbc_systemPanel.anchor = GridBagConstraints.NORTH;
      gbc_systemPanel.fill = GridBagConstraints.BOTH;
      gbc_systemPanel.insets = new Insets(0, 0, 0, 5);
      gbc_systemPanel.gridx = 1;
      gbc_systemPanel.gridy = 0;
      settingsPanel.add(getSystemPanel(), gbc_systemPanel);
      GridBagConstraints gbc_joystickPanel = new GridBagConstraints();
      gbc_joystickPanel.insets = new Insets(0, 10, 0, 0);
      gbc_joystickPanel.weighty = 1.0;
      gbc_joystickPanel.anchor = GridBagConstraints.NORTH;
      gbc_joystickPanel.fill = GridBagConstraints.BOTH;
      gbc_joystickPanel.gridx = 0;
      gbc_joystickPanel.gridy = 0;
      settingsPanel.add(getCombinedJoystickPanel(), gbc_joystickPanel);
    }
    return settingsPanel;
  }

  private InfoPanel getInfoPanel()
  {
    if (infoPanel == null)
    {
      infoPanel = new InfoPanel(model.getInfoModel());
    }
    return infoPanel;
  }

  private CombinedJoystickPanel getCombinedJoystickPanel()
  {
    if (joystickPanel == null)
    {
      joystickPanel = new CombinedJoystickPanel(model);
    }
    return joystickPanel;
  }

  private SystemPanel getSystemPanel()
  {
    if (systemPanel == null)
    {
      systemPanel = new SystemPanel(model.getSystemModel());
      systemPanel
        .setBorder(new TitledBorder(null, "System Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    }
    return systemPanel;
  }

  private JPanel getButtonPanel()
  {
    if (buttonPanel == null)
    {
      buttonPanel = new JPanel();
      GridBagLayout gbl_buttonPanel = new GridBagLayout();
      gbl_buttonPanel.columnWidths = new int[] { 0, 0 };
      gbl_buttonPanel.rowHeights = new int[] { 0, 0 };
      gbl_buttonPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
      gbl_buttonPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
      buttonPanel.setLayout(gbl_buttonPanel);
      GridBagConstraints gbc_saveButton = new GridBagConstraints();
      gbc_saveButton.weighty = 1.0;
      gbc_saveButton.weightx = 1.0;
      gbc_saveButton.anchor = GridBagConstraints.SOUTHEAST;
      gbc_saveButton.insets = new Insets(5, 5, 5, 6);
      gbc_saveButton.gridx = 0;
      gbc_saveButton.gridy = 0;
      buttonPanel.add(getSaveButton(), gbc_saveButton);
    }
    return buttonPanel;
  }

  private JButton getSaveButton()
  {
    if (saveButton == null)
    {
      model.addSaveChangeListener(e -> {
        logger.debug("SaveButton isDataChanged = {}", model.isDataChanged());
        saveButton.setEnabled(model.isDataChanged());
      });
      saveButton = new JButton("Save");
      saveButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          model.saveData();
        }
      });
    }
    return saveButton;
  }
}
