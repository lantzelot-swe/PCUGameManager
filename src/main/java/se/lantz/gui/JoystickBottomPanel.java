package se.lantz.gui;

import javax.swing.JPanel;

import se.lantz.model.JoystickModel;

import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.Beans;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JoystickBottomPanel extends JPanel
{
  private KeySelectionComboBox aComboBox;
  private KeySelectionComboBox bComboBox;
  private KeySelectionComboBox cComboBox;
  private JLabel menuLabel;
  private JoystickModel model;

  public JoystickBottomPanel(JoystickModel model)
  {
    this.model = model;
    this.setPreferredSize(new Dimension(300, 50));
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_aComboBox = new GridBagConstraints();
    gbc_aComboBox.anchor = GridBagConstraints.NORTH;
    gbc_aComboBox.weighty = 1.0;
    gbc_aComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_aComboBox.gridx = 0;
    gbc_aComboBox.gridy = 0;
    add(getAComboBox(), gbc_aComboBox);
    GridBagConstraints gbc_bComboBox = new GridBagConstraints();
    gbc_bComboBox.anchor = GridBagConstraints.NORTH;
    gbc_bComboBox.weighty = 1.0;
    gbc_bComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_bComboBox.gridx = 1;
    gbc_bComboBox.gridy = 0;
    add(getBComboBox(), gbc_bComboBox);
    GridBagConstraints gbc_cComboBox = new GridBagConstraints();
    gbc_cComboBox.anchor = GridBagConstraints.NORTH;
    gbc_cComboBox.weighty = 1.0;
    gbc_cComboBox.insets = new Insets(0, 0, 5, 0);
    gbc_cComboBox.gridx = 2;
    gbc_cComboBox.gridy = 0;
    add(getCComboBox(), gbc_cComboBox);
    GridBagConstraints gbc_menuLabel = new GridBagConstraints();
    gbc_menuLabel.weighty = 1.0;
    gbc_menuLabel.anchor = GridBagConstraints.NORTH;
    gbc_menuLabel.insets = new Insets(3, 15, 0, 5);
    gbc_menuLabel.gridx = 3;
    gbc_menuLabel.gridy = 0;
    add(getMenuLabel(), gbc_menuLabel);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener((e) -> modelChanged());
    }
  }

  private void modelChanged()
  {
    getAComboBox().setSelectedCode(model.getA());
    getBComboBox().setSelectedCode(model.getB());
    getCComboBox().setSelectedCode(model.getC());
  }

  private KeySelectionComboBox getAComboBox()
  {
    if (aComboBox == null)
    {
      aComboBox = new KeySelectionComboBox(this.model);
      aComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            model.setA(aComboBox.getSelectedCode());
          }
        });
    }
    return aComboBox;
  }

  private KeySelectionComboBox getBComboBox()
  {
    if (bComboBox == null)
    {
      bComboBox = new KeySelectionComboBox(this.model);
      bComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            model.setB(bComboBox.getSelectedCode());
          }
        });
    }
    return bComboBox;
  }

  private KeySelectionComboBox getCComboBox()
  {
    if (cComboBox == null)
    {
      cComboBox = new KeySelectionComboBox(this.model);
      cComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            model.setC(cComboBox.getSelectedCode());
          }
        });
    }
    return cComboBox;
  }

  private JLabel getMenuLabel()
  {
    if (menuLabel == null)
    {
      menuLabel = new JLabel("Menu");
    }
    return menuLabel;
  }
}
