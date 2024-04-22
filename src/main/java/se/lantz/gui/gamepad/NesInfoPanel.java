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

public class NesInfoPanel extends JPanel
{
  private static final String dbControllerString = "030000001008000001e5000010010000,NEXT SNES Controller,lefttrigger:b2,righttrigger:b1,b:b9,dpdown:+a1,dpleft:-a0,dpright:+a0,dpup:-a1,leftshoulder:b4,rightshoulder:b6,start:b8,x:b3,y:b0,platform:Linux,";

  private JLabel infoLabel;
  private JTextArea controlerConfTextField;
  private JLabel extraButtonsInfoLabel;
  private JButton copyButton;

  public NesInfoPanel()
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
        new JLabel("<html><h3>Nintendo NES USB controller</h3><p>Inspired by original consoles that changed gaming history forever, these NES-inspired USB button-bashers work great with TheC64 and TheVIC20.<p><p>The following string in gamecontrollerdb.txt maps the buttons below:<br><br></html>");
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
