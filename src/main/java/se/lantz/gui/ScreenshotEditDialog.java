package se.lantz.gui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class ScreenshotEditDialog extends JDialog
{
  private JPanel backroundPanel;
  private JLabel imageLabel;
  private JPanel buttonPanel;
  private JButton okButton;
  private JButton cancelButton;
  private JCheckBox cropCheckBox;

  public ScreenshotEditDialog(Frame owner)
  {
    super(owner, "Edit screenshot", true);
    getContentPane().add(getBackroundPanel(), BorderLayout.CENTER);
    getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel getBackroundPanel()
  {
    if (backroundPanel == null)
    {
      backroundPanel = new JPanel();
      GridBagLayout gbl_backroundPanel = new GridBagLayout();
      gbl_backroundPanel.columnWidths = new int[] { 0, 0 };
      gbl_backroundPanel.rowHeights = new int[] { 0, 0, 0 };
      gbl_backroundPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
      gbl_backroundPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
      backroundPanel.setLayout(gbl_backroundPanel);
      GridBagConstraints gbc_imageLabel = new GridBagConstraints();
      gbc_imageLabel.weighty = 1.0;
      gbc_imageLabel.weightx = 1.0;
      gbc_imageLabel.anchor = GridBagConstraints.NORTH;
      gbc_imageLabel.fill = GridBagConstraints.BOTH;
      gbc_imageLabel.insets = new Insets(10, 10, 10, 0);
      gbc_imageLabel.gridx = 0;
      gbc_imageLabel.gridy = 0;
      backroundPanel.add(getImageLabel(), gbc_imageLabel);
      GridBagConstraints gbc_cropCheckBox = new GridBagConstraints();
      gbc_cropCheckBox.anchor = GridBagConstraints.WEST;
      gbc_cropCheckBox.weightx = 1.0;
      gbc_cropCheckBox.gridx = 0;
      gbc_cropCheckBox.gridy = 1;
      backroundPanel.add(getCropCheckBox(), gbc_cropCheckBox);
    }
    return backroundPanel;
  }

  private JLabel getImageLabel()
  {
    if (imageLabel == null)
    {
      imageLabel = new JLabel("New label");
    }
    return imageLabel;
  }

  private JPanel getButtonPanel()
  {
    if (buttonPanel == null)
    {
      buttonPanel = new JPanel();
      buttonPanel.add(getOkButton());
      buttonPanel.add(getCancelButton());
    }
    return buttonPanel;
  }

  private JButton getOkButton()
  {
    if (okButton == null)
    {
      okButton = new JButton("OK");
    }
    return okButton;
  }

  private JButton getCancelButton()
  {
    if (cancelButton == null)
    {
      cancelButton = new JButton("Cancel");
    }
    return cancelButton;
  }

  private JCheckBox getCropCheckBox()
  {
    if (cropCheckBox == null)
    {
      cropCheckBox = new JCheckBox("Crop screenshot to 320x200 pixels");
    }
    return cropCheckBox;
  }
}
