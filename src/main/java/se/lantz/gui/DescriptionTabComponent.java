package se.lantz.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class DescriptionTabComponent extends JPanel
{
  private JLabel descrLabel;
  private JLabel languageLabel;

  public DescriptionTabComponent(String descrText)
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
    add(getDescrLabel(), gbc_descrLabel);
    GridBagConstraints gbc_languageLabel = new GridBagConstraints();
    gbc_languageLabel.gridx = 1;
    gbc_languageLabel.gridy = 0;
    add(getLanguageLabel(), gbc_languageLabel);
    getDescrLabel().setText(descrText);
    this.setOpaque(false);
  }

  private JLabel getDescrLabel()
  {
    if (descrLabel == null)
    {
      descrLabel = new JLabel("Description:");
    }
    return descrLabel;
  }

  private JLabel getLanguageLabel()
  {
    if (languageLabel == null)
    {
      languageLabel = new JLabel("en");
    }
    return languageLabel;
  }

  public void setText(String text)
  {
    getLanguageLabel().setText(text);
  }

  public void setBold(boolean bold)
  {
    getLanguageLabel().setFont(getLanguageLabel().getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN));
  }
}
