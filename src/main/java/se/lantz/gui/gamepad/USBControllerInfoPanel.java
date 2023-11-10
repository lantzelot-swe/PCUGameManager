package se.lantz.gui.gamepad;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;

public class USBControllerInfoPanel extends JPanel
{
  ImageIcon compImage = new ImageIcon(getClass().getResource("/se/lantz/joystick-comp.png"));
  
  private JLabel infoLabel;
  private JLabel compImageLabel;
  private JLabel extraButtonsInfoLabel;

  public USBControllerInfoPanel()
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
    gbc_compImageLabel.insets = new Insets(0, 0, 5, 0);
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
    // TODO Auto-generated constructor stub
  }
  private JLabel getInfoLabel() {
    if (infoLabel == null) {
    	infoLabel = new JLabel("<html><h3>Alternative USB controllers</h3><p>THEC64/THEVIC20 is compatible with a wide range of other modern USB controllers, which you use as either the primary or the secondary controller, where applicable. They need to have a minimum of eight buttons to be able to replicate the joystick’s full functionality. Using standard modern USB controller terms, joystick functions translate as follows:</html>");
    }
    return infoLabel;
  }
  private JLabel getCompImageLabel() {
    if (compImageLabel == null) {
    	compImageLabel = new JLabel();
    	compImageLabel.setBackground(Color.WHITE);
    	compImageLabel.setIcon(compImage);
    }
    return compImageLabel;
  }
  private JLabel getExtraButtonsInfoLabel() {
    if (extraButtonsInfoLabel == null) {
    	extraButtonsInfoLabel = new JLabel("<html>Notice the extra buttons <b>left shoulder</b>, <b>right shoulder</b>, <b>left stick</b> and <b>right stick</b>. \r\n" + 
    	  "They are not available on the joystick but are common on alternative USB controllers. \r\n" + 
    	  "The latter two are for controllers with two sticks that press down for additional button functions. It’s up to you what you map to those buttons (if anything). Below are the functions for THEC64 Joystick shown in parenthesis for comparison.</html>");
    }
    return extraButtonsInfoLabel;
  }
}
