package se.lantz.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.Beans;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import se.lantz.model.JoystickModel;
import se.lantz.util.CustomUndoPlainDocument;
import se.lantz.util.TextComponentSupport;

public class JoystickPanel extends JPanel
{
	private JCheckBox primaryJoyCheckBox;
	private JoystickStickPanel joystickStickPanel;
	private KeySelectionComboBox leftFireComboBox;
	private JLabel imageLabel;
	private KeySelectionComboBox rightFireComboBox;
	private KeySelectionComboBox tlComboBox;
	private KeySelectionComboBox trComboBox;
	private JoystickBottomPanel joystickBottomPanel;
	private int portnumber;
	private JLabel configLabel;
	private JTextField configTextField;

	private JoystickModel model;
	private JPanel configPanel;

	public JoystickPanel(int portnumber, JoystickModel model)
	{
		this.portnumber = portnumber;
		this.model = model;
		this.setPreferredSize(new Dimension(390, 510));
		this.setMinimumSize(new Dimension(390, 510));
//		setBorder(new TitledBorder(null, "Port " + portnumber, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]
		{ 0.0, 1.0, 0.0 };
		setLayout(gridBagLayout);
		GridBagConstraints gbc_primaryJoyCheckBox = new GridBagConstraints();
		gbc_primaryJoyCheckBox.weightx = 1.0;
		gbc_primaryJoyCheckBox.gridwidth = 3;
		gbc_primaryJoyCheckBox.anchor = GridBagConstraints.WEST;
		gbc_primaryJoyCheckBox.insets = new Insets(5, 0, 5, 0);
		gbc_primaryJoyCheckBox.gridx = 0;
		gbc_primaryJoyCheckBox.gridy = 1;
		add(getPrimaryJoyCheckBox(), gbc_primaryJoyCheckBox);
		GridBagConstraints gbc_joystickStickPanel = new GridBagConstraints();
		gbc_joystickStickPanel.weightx = 1.0;
		gbc_joystickStickPanel.gridwidth = 3;
		gbc_joystickStickPanel.insets = new Insets(0, 0, 5, 0);
		gbc_joystickStickPanel.fill = GridBagConstraints.BOTH;
		gbc_joystickStickPanel.gridx = 0;
		gbc_joystickStickPanel.gridy = 2;
		add(getJoystickStickPanel(), gbc_joystickStickPanel);
		GridBagConstraints gbc_leftFireComboBox = new GridBagConstraints();
		gbc_leftFireComboBox.anchor = GridBagConstraints.WEST;
		gbc_leftFireComboBox.insets = new Insets(45, 5, 5, 5);
		gbc_leftFireComboBox.gridx = 0;
		gbc_leftFireComboBox.gridy = 3;
		add(getLeftFireComboBox(), gbc_leftFireComboBox);
		GridBagConstraints gbc_imageLabel = new GridBagConstraints();
		gbc_imageLabel.gridheight = 3;
		gbc_imageLabel.weightx = 1.0;
		gbc_imageLabel.fill = GridBagConstraints.BOTH;
		gbc_imageLabel.insets = new Insets(0, 0, 5, 5);
		gbc_imageLabel.gridx = 1;
		gbc_imageLabel.gridy = 3;
		add(getImageLabel(), gbc_imageLabel);
		GridBagConstraints gbc_rightFireComboBox = new GridBagConstraints();
		gbc_rightFireComboBox.anchor = GridBagConstraints.EAST;
		gbc_rightFireComboBox.insets = new Insets(45, 0, 5, 5);
		gbc_rightFireComboBox.gridx = 2;
		gbc_rightFireComboBox.gridy = 3;
		add(getRightFireComboBox(), gbc_rightFireComboBox);
		GridBagConstraints gbc_tlComboBox = new GridBagConstraints();
		gbc_tlComboBox.anchor = GridBagConstraints.WEST;
		gbc_tlComboBox.insets = new Insets(30, 5, 5, 5);
		gbc_tlComboBox.gridx = 0;
		gbc_tlComboBox.gridy = 4;
		add(getTlComboBox(), gbc_tlComboBox);
		GridBagConstraints gbc_trComboBox = new GridBagConstraints();
		gbc_trComboBox.anchor = GridBagConstraints.EAST;
		gbc_trComboBox.insets = new Insets(30, 0, 5, 5);
		gbc_trComboBox.gridx = 2;
		gbc_trComboBox.gridy = 4;
		add(getTrComboBox(), gbc_trComboBox);
		GridBagConstraints gbc_joystickBottomPanel = new GridBagConstraints();
		gbc_joystickBottomPanel.weighty = 1.0;
		gbc_joystickBottomPanel.insets = new Insets(0, 0, 5, 0);
		gbc_joystickBottomPanel.gridwidth = 3;
		gbc_joystickBottomPanel.fill = GridBagConstraints.BOTH;
		gbc_joystickBottomPanel.gridx = 0;
		gbc_joystickBottomPanel.gridy = 6;
		add(getJoystickBottomPanel(), gbc_joystickBottomPanel);
		GridBagConstraints gbc_configPanel = new GridBagConstraints();
		gbc_configPanel.anchor = GridBagConstraints.NORTH;
		gbc_configPanel.weighty = 0.0;
		gbc_configPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_configPanel.gridx = 0;
		gbc_configPanel.gridwidth = 3;
		gbc_configPanel.gridy = 0;
		add(getConfigPanel(), gbc_configPanel);
		if (!Beans.isDesignTime())
		{
			model.addPropertyChangeListener((e) -> modelChanged());
		}
	}

	private void modelChanged()
	{
		// Read from model
		getPrimaryJoyCheckBox().setSelected(model.isPrimary());
		getLeftFireComboBox().setSelectedCode(model.getLeftFire());
		getRightFireComboBox().setSelectedCode(model.getRightFire());
		getTlComboBox().setSelectedCode(model.getTl());
		getTrComboBox().setSelectedCode(model.getTr());
		if (!getConfigTextField().getText().equals(model.getConfigString()))
		{
			getConfigTextField().setText(model.getConfigString());
		}
	}

	private JCheckBox getPrimaryJoyCheckBox()
	{
		if (primaryJoyCheckBox == null)
		{
			String text = "Use port " + portnumber + " as Primary joystick";
			if (portnumber == 2)
			{
				text = text + " (Default setting)";
			}
			primaryJoyCheckBox = new JCheckBox(text);
			primaryJoyCheckBox.addItemListener((e) -> model.setPrimary(primaryJoyCheckBox.isSelected()));
		}
		return primaryJoyCheckBox;
	}

	private JoystickStickPanel getJoystickStickPanel()
	{
		if (joystickStickPanel == null)
		{
			joystickStickPanel = new JoystickStickPanel(model);
		}
		return joystickStickPanel;
	}

	private KeySelectionComboBox getLeftFireComboBox()
	{
		if (leftFireComboBox == null)
		{
			leftFireComboBox = new KeySelectionComboBox(this.model);
			leftFireComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					model.setLeftFire(leftFireComboBox.getSelectedCode());
				}
			});
		}
		return leftFireComboBox;
	}

	private JLabel getImageLabel()
	{
		if (imageLabel == null)
		{
			imageLabel = new JLabel();
			imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
			ImageIcon joyImage = new ImageIcon(getClass().getResource("/se/lantz/joystick.png"));
			imageLabel.setIcon(joyImage);
		}
		return imageLabel;
	}

	private KeySelectionComboBox getRightFireComboBox()
	{
		if (rightFireComboBox == null)
		{
			rightFireComboBox = new KeySelectionComboBox(this.model);
			rightFireComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					model.setRightFire(rightFireComboBox.getSelectedCode());
				}
			});
		}
		return rightFireComboBox;
	}

	private KeySelectionComboBox getTlComboBox()
	{
		if (tlComboBox == null)
		{
			tlComboBox = new KeySelectionComboBox(this.model);
			tlComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					model.setTl(tlComboBox.getSelectedCode());
				}
			});
		}
		return tlComboBox;
	}

	private KeySelectionComboBox getTrComboBox()
	{
		if (trComboBox == null)
		{
			trComboBox = new KeySelectionComboBox(this.model);
			trComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					model.setTr(trComboBox.getSelectedCode());
				}
			});
		}
		return trComboBox;
	}

	private JoystickBottomPanel getJoystickBottomPanel()
	{
		if (joystickBottomPanel == null)
		{
			joystickBottomPanel = new JoystickBottomPanel(model);
		}
		return joystickBottomPanel;
	}

	private JLabel getConfigLabel()
	{
		if (configLabel == null)
		{
			configLabel = new JLabel("Port " + portnumber + ":");
		}
		return configLabel;
	}

	private JTextField getConfigTextField()
	{
		if (configTextField == null)
		{
			configTextField = new JTextField();
			configTextField.setDocument(new CustomUndoPlainDocument()
			{
        @Override
        public void updateModel()
        {
          //Nothing
        }
      });
			configTextField.setColumns(10);
			configTextField.addFocusListener(new FocusListener()
			{

				@Override
				public void focusGained(FocusEvent arg0)
				{
				  //Nothing
				}

				@Override
				public void focusLost(FocusEvent arg0)
				{
					try
					{
						model.setConfigString(configTextField.getText());
					} catch (Exception e)
					{
						configTextField.setText(model.getConfigString());
					}
				}
			});
			TextComponentSupport.setupPopupAndUndoable(configTextField);
		}
		return configTextField;
	}

	private JPanel getConfigPanel()
	{
		if (configPanel == null)
		{
			configPanel = new JPanel();
			GridBagLayout gbl_configPanel = new GridBagLayout();
			configPanel.setLayout(gbl_configPanel);
			GridBagConstraints gbc_configLabel = new GridBagConstraints();
			gbc_configLabel.anchor = GridBagConstraints.WEST;
			gbc_configLabel.insets = new Insets(4, 5, 0, 5);
			gbc_configLabel.gridx = 0;
			gbc_configLabel.gridy = 0;
			configPanel.add(getConfigLabel(), gbc_configLabel);
			GridBagConstraints gbc_configTextField = new GridBagConstraints();
			gbc_configTextField.insets = new Insets(3, 0, 0, 5);
			gbc_configTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_configTextField.weightx = 1.0;
			gbc_configTextField.anchor = GridBagConstraints.NORTHWEST;
			gbc_configTextField.gridx = 1;
			gbc_configTextField.gridy = 0;
			configPanel.add(getConfigTextField(), gbc_configTextField);
		}
		return configPanel;
	}
}
