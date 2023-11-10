package se.lantz.gui.gamepad;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TheGamepadInfoPanel extends JPanel
{
  ImageIcon compImage = new ImageIcon(getClass().getResource("/se/lantz/TheGamepadMapping.png"));

  private JLabel infoLabel;
  private JLabel compImageLabel;
  private JLabel extraButtonsInfoLabel;

  public TheGamepadInfoPanel()
  {
    setBackground(Color.WHITE);
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.insets = new Insets(0, 10, 0, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_compImageLabel = new GridBagConstraints();
    gbc_compImageLabel.insets = new Insets(0, 0, 10, 0);
    gbc_compImageLabel.weighty = 1.0;
    gbc_compImageLabel.weightx = 1.0;
    gbc_compImageLabel.gridx = 0;
    gbc_compImageLabel.gridy = 1;
    add(getCompImageLabel(), gbc_compImageLabel);
    GridBagConstraints gbc_extraButtonsInfoLabel = new GridBagConstraints();
    gbc_extraButtonsInfoLabel.insets = new Insets(0, 10, 10, 10);
    gbc_extraButtonsInfoLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_extraButtonsInfoLabel.gridx = 0;
    gbc_extraButtonsInfoLabel.gridy = 2;
    add(getExtraButtonsInfoLabel(), gbc_extraButtonsInfoLabel);
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel =
        new JLabel("<html><h3>THEGamepad</h3><p>THEGamepad from Retro Games is compatible with THEC64, THEC64 Mini, THEA500 Mini, PC, Mac & Linux. Compared with THEC64 Joystick, functions translate as follows:<br><br></html>");
    }
    return infoLabel;
  }

  private JLabel getCompImageLabel()
  {
    if (compImageLabel == null)
    {
      compImageLabel = new JLabel();
      compImageLabel.setBackground(Color.WHITE);
      compImageLabel.setIcon(compImage);
    }
    return compImageLabel;
  }

  private JLabel getExtraButtonsInfoLabel()
  {
    if (extraButtonsInfoLabel == null)
    {
      extraButtonsInfoLabel =
        new JLabel("<html><br>Below are the functions for THEC64 Joystick shown in parenthesis for comparison.<br></html>");
    }
    return extraButtonsInfoLabel;
  }
}
