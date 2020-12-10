package se.lantz.gui;

import javax.swing.JPanel;

import se.lantz.model.JoystickModel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.Beans;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JoystickStickPanel extends JPanel
{
  private KeySelectionComboBox upComboBox;
  private KeySelectionComboBox leftComboBox;
  private KeySelectionComboBox rightComboBox;
  private KeySelectionComboBox downComboBox;
  private JoystickModel model;

  public JoystickStickPanel(JoystickModel model)
  {
    this.model = model;
    this.setPreferredSize(new Dimension(420, 160));
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_upComboBox = new GridBagConstraints();
    gbc_upComboBox.insets = new Insets(0, 10, 5, 10);
    gbc_upComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_upComboBox.gridx = 1;
    gbc_upComboBox.gridy = 0;
    add(getUpComboBox(), gbc_upComboBox);
    GridBagConstraints gbc_leftComboBox = new GridBagConstraints();
    gbc_leftComboBox.insets = new Insets(0, 5, 5, 0);
    gbc_leftComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_leftComboBox.gridx = 0;
    gbc_leftComboBox.gridy = 1;
    add(getLeftComboBox(), gbc_leftComboBox);
    GridBagConstraints gbc_rightComboBox = new GridBagConstraints();
    gbc_rightComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_rightComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_rightComboBox.gridx = 2;
    gbc_rightComboBox.gridy = 1;
    add(getRightComboBox(), gbc_rightComboBox);
    GridBagConstraints gbc_downComboBox = new GridBagConstraints();
    gbc_downComboBox.insets = new Insets(0, 10, 0, 10);
    gbc_downComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_downComboBox.gridx = 1;
    gbc_downComboBox.gridy = 2;
    add(getDownComboBox(), gbc_downComboBox);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener((e) -> modelChanged());
    }
  }

  private void modelChanged()
  {
    getUpComboBox().setSelectedCode(model.getUp());
    getDownComboBox().setSelectedCode(model.getDown());
    getLeftComboBox().setSelectedCode(model.getLeft());
    getRightComboBox().setSelectedCode(model.getRight());
  }

  private KeySelectionComboBox getUpComboBox()
  {
    if (upComboBox == null)
    {
      upComboBox = new KeySelectionComboBox(this.model);
      upComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            model.setUp(upComboBox.getSelectedCode());
          }
        });
    }
    return upComboBox;
  }

  private KeySelectionComboBox getLeftComboBox()
  {
    if (leftComboBox == null)
    {
      leftComboBox = new KeySelectionComboBox(this.model);
      leftComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            model.setLeft(leftComboBox.getSelectedCode());
          }
        });
    }
    return leftComboBox;
  }

  private KeySelectionComboBox getRightComboBox()
  {
    if (rightComboBox == null)
    {
      rightComboBox = new KeySelectionComboBox(this.model);
      rightComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            model.setRight(rightComboBox.getSelectedCode());
          }
        });
    }
    return rightComboBox;
  }

  private KeySelectionComboBox getDownComboBox()
  {
    if (downComboBox == null)
    {
      downComboBox = new KeySelectionComboBox(this.model);
      downComboBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            model.setDown(downComboBox.getSelectedCode());
          }
        });
    }
    return downComboBox;
  }
}
