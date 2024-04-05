package se.lantz.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SystemTabComponent extends JPanel
{
  private JLabel textLabel;
  private JLabel numberLabel;

  public SystemTabComponent(String text)
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
    gridBagLayout.rowHeights = new int[] { 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);
    GridBagConstraints gbc_descrLabel = new GridBagConstraints();
    gbc_descrLabel.insets = new Insets(0, 0, 0, 5);
    gbc_descrLabel.gridx = 0;
    gbc_descrLabel.gridy = 0;
    add(getTextLabel(), gbc_descrLabel);
    GridBagConstraints gbc_languageLabel = new GridBagConstraints();
    gbc_languageLabel.gridx = 1;
    gbc_languageLabel.gridy = 0;
    add(getNumberLabel(), gbc_languageLabel);
    getTextLabel().setText(text);
    this.setOpaque(false);
  }

  private JLabel getTextLabel()
  {
    if (textLabel == null)
    {
      textLabel = new JLabel("");
    }
    return textLabel;
  }

  private JLabel getNumberLabel()
  {
    if (numberLabel == null)
    {
      numberLabel = new JLabel("");
      numberLabel.setFont(getNumberLabel().getFont().deriveFont(Font.BOLD));
    }
    return numberLabel;
  }
 
  public void setNumber(String text)
  {
    if (text.isBlank())
    {
      getNumberLabel().setText("");
    }
    else
    {
      getNumberLabel().setText("(" + text + ")");
    }
  }
}
