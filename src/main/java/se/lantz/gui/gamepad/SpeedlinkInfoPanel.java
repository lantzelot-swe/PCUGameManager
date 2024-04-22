package se.lantz.gui.gamepad;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class SpeedlinkInfoPanel extends JPanel
{
  private static final String dbControllerString = "03000000790000001c18000011010000,SPEEDLINK COMPETITION PRO Game Controller for Android,a:b0,b:b4,lefttrigger:b3,leftx:a0,lefty:a1,start:b1,platform:Linux,";

  private JLabel infoLabel;
  private JTextArea controlerConfTextField;
  private JLabel extraButtonsInfoLabel;
  private JButton copyButton;

  public SpeedlinkInfoPanel()
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
    gbc_compImageLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_compImageLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_compImageLabel.insets = new Insets(0, 10, 5, 10);
    gbc_compImageLabel.weightx = 1.0;
    gbc_compImageLabel.gridx = 0;
    gbc_compImageLabel.gridy = 1;
    add(getCompImageLabel(), gbc_compImageLabel);
    GridBagConstraints gbc_copyButton = new GridBagConstraints();
    gbc_copyButton.weighty = 1.0;
    gbc_copyButton.anchor = GridBagConstraints.NORTHEAST;
    gbc_copyButton.insets = new Insets(0, 5, 5, 5);
    gbc_copyButton.gridx = 0;
    gbc_copyButton.gridy = 2;
    add(getCopyButton(), gbc_copyButton);
    GridBagConstraints gbc_extraButtonsInfoLabel = new GridBagConstraints();
    gbc_extraButtonsInfoLabel.insets = new Insets(0, 10, 10, 10);
    gbc_extraButtonsInfoLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_extraButtonsInfoLabel.gridx = 0;
    gbc_extraButtonsInfoLabel.gridy = 3;
    add(getExtraButtonsInfoLabel(), gbc_extraButtonsInfoLabel);
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel =
        new JLabel("<html><h3>Speedlink Competition Pro Extra</h3><p>The COMPETITION PRO EXTRA USB joystick is an ode to nostalgic computer games. Its design with its unmistakable arcade machine stick and technology – such as the iconic micro switches with their loud click – is a throwback to the original COMPETITION PRO from the 80s, the famously popular joystick for home computers.<p><p>The following string in gamecontrollerdb.txt maps the buttons below:<br><br></html>");
    }
    return infoLabel;
  }

  private JTextArea getCompImageLabel()
  {
    if (controlerConfTextField == null)
    {
      controlerConfTextField = new JTextArea();
      controlerConfTextField.setBackground(Color.WHITE);
      controlerConfTextField.setEditable(false);
      controlerConfTextField.setText(dbControllerString);
      controlerConfTextField.setLineWrap(true);
    }
    return controlerConfTextField;
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
  private JButton getCopyButton() {
    if (copyButton == null) {
    	copyButton = new JButton("Copy to clipboard");
    	copyButton.addActionListener(e -> {
    	  StringSelection stringSelection = new StringSelection(dbControllerString);
    	  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	  clipboard.setContents(stringSelection, null);
    	});
    }
    return copyButton;
  }
}
