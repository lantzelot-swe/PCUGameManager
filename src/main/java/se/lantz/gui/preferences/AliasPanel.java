package se.lantz.gui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;

public class AliasPanel extends JPanel
{
  private JLabel favLabel;
  private JTextField textField;
  private int number;

  public AliasPanel(int number)
  {
    this.number = number;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_favLabel = new GridBagConstraints();
    gbc_favLabel.weighty = 1.0;
    gbc_favLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_favLabel.insets = new Insets(7, 10, 0, 5);
    gbc_favLabel.gridx = 0;
    gbc_favLabel.gridy = 0;
    add(getFavLabel(), gbc_favLabel);
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.anchor = GridBagConstraints.NORTHEAST;
    gbc_textField.weighty = 1.0;
    gbc_textField.weightx = 1.0;
    gbc_textField.insets = new Insets(5, 5, 0, 10);
    gbc_textField.gridx = 1;
    gbc_textField.gridy = 0;
    add(getTextField(), gbc_textField);
  }

  private JLabel getFavLabel()
  {
    if (favLabel == null)
    {
      favLabel = new JLabel("Favorites " + number + ":");
    }
    return favLabel;
  }

  protected JTextField getTextField()
  {
    if (textField == null)
    {
      textField = new JTextField();
      textField.setPreferredSize(new Dimension(250, 20));
    }
    return textField;
  }
}
