package se.lantz.gui.translation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import se.lantz.model.InfoModel;
import se.lantz.util.FileManager;

public class TranslationPanel extends JPanel
{
	private static final String CUSTOM_LANGUAGE_PROPERTY = "customLanguage";
	private static final String EN = "English (en)";
	private static final String DE = "German (de)";
	private static final String FR = "French (fr)";
	private static final String ES = "Spanish (es)";
	private static final String IT = "Italian (it)";
	private JLabel selectFromLabel;
	private JComboBox<String> fromComboBox;
	private JLabel toLanguageLabel;
	private JPanel checkBoxPanel;
	private JCheckBox enCheckBox;
	private JCheckBox deCheckBox;
	private JCheckBox frCheckBox;
	private JCheckBox esCheckBox;
	private JCheckBox itCheckBox;
	private InfoModel model;
	private JPanel customPanel;
	private JCheckBox customCheckBox;
	private JTextField customTextField;
	private JLabel infoLabel;
	private String customLanguage;

	public TranslationPanel(InfoModel model)
	{
		this.model = model;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]
		{ 0.0, 0.0, 0.0 };
		gridBagLayout.columnWeights = new double[]
		{ 1.0, 0.0 };
		setLayout(gridBagLayout);
		GridBagConstraints gbc_selectFromLabel = new GridBagConstraints();
		gbc_selectFromLabel.anchor = GridBagConstraints.WEST;
		gbc_selectFromLabel.insets = new Insets(10, 10, 5, 5);
		gbc_selectFromLabel.gridx = 0;
		gbc_selectFromLabel.gridy = 0;
		add(getSelectFromLabel(), gbc_selectFromLabel);
		GridBagConstraints gbc_fromComboBox = new GridBagConstraints();
		gbc_fromComboBox.anchor = GridBagConstraints.WEST;
		gbc_fromComboBox.insets = new Insets(10, 0, 5, 10);
		gbc_fromComboBox.gridx = 1;
		gbc_fromComboBox.gridy = 0;
		add(getFromComboBox(), gbc_fromComboBox);
		GridBagConstraints gbc_toLanguageLabel = new GridBagConstraints();
		gbc_toLanguageLabel.anchor = GridBagConstraints.WEST;
		gbc_toLanguageLabel.gridwidth = 2;
		gbc_toLanguageLabel.insets = new Insets(5, 10, 5, 0);
		gbc_toLanguageLabel.gridx = 0;
		gbc_toLanguageLabel.gridy = 1;
		add(getToLanguageLabel(), gbc_toLanguageLabel);
		GridBagConstraints gbc_checkBoxPanel = new GridBagConstraints();
		gbc_checkBoxPanel.insets = new Insets(0, 0, 5, 0);
		gbc_checkBoxPanel.fill = GridBagConstraints.BOTH;
		gbc_checkBoxPanel.gridwidth = 2;
		gbc_checkBoxPanel.weighty = 1.0;
		gbc_checkBoxPanel.weightx = 1.0;
		gbc_checkBoxPanel.gridx = 0;
		gbc_checkBoxPanel.gridy = 2;
		add(getCheckBoxPanel(), gbc_checkBoxPanel);

		customLanguage = FileManager.getConfiguredProperties().getProperty(CUSTOM_LANGUAGE_PROPERTY);
		if (customLanguage == null)
		{
			customLanguage = "";
		}
		getCustomTextField().setText(customLanguage);
	}

	private JLabel getSelectFromLabel()
	{
		if (selectFromLabel == null)
		{
			selectFromLabel = new JLabel("Select language to generate translation from:");
		}
		return selectFromLabel;
	}

	private JComboBox<String> getFromComboBox()
	{
		if (fromComboBox == null)
		{
			fromComboBox = new JComboBox<>();
			fromComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					enableCheckBoxes();
				}
			});
			fromComboBox.addItem(EN);
			fromComboBox.addItem(DE);
			fromComboBox.addItem(FR);
			fromComboBox.addItem(ES);
			fromComboBox.addItem(IT);
		}
		return fromComboBox;
	}

	private JLabel getToLanguageLabel()
	{
		if (toLanguageLabel == null)
		{
			toLanguageLabel = new JLabel("Generate translations for the following languages:");
		}
		return toLanguageLabel;
	}

	private JPanel getCheckBoxPanel()
	{
		if (checkBoxPanel == null)
		{
			checkBoxPanel = new JPanel();
			GridBagLayout gbl_checkBoxPanel = new GridBagLayout();
			gbl_checkBoxPanel.columnWeights = new double[]
			{ 0.0, 1.0 };
			gbl_checkBoxPanel.columnWidths = new int[]
			{ 0, 0 };
			checkBoxPanel.setLayout(gbl_checkBoxPanel);
			GridBagConstraints gbc_enCheckBox = new GridBagConstraints();
			gbc_enCheckBox.anchor = GridBagConstraints.WEST;
			gbc_enCheckBox.insets = new Insets(0, 5, 5, 0);
			gbc_enCheckBox.gridx = 0;
			gbc_enCheckBox.gridy = 0;
			checkBoxPanel.add(getEnCheckBox(), gbc_enCheckBox);
			GridBagConstraints gbc_deCheckBox = new GridBagConstraints();
			gbc_deCheckBox.anchor = GridBagConstraints.WEST;
			gbc_deCheckBox.insets = new Insets(0, 5, 5, 0);
			gbc_deCheckBox.gridx = 0;
			gbc_deCheckBox.gridy = 1;
			checkBoxPanel.add(getDeCheckBox(), gbc_deCheckBox);
			GridBagConstraints gbc_frCheckBox = new GridBagConstraints();
			gbc_frCheckBox.weighty = 1.0;
			gbc_frCheckBox.anchor = GridBagConstraints.NORTHWEST;
			gbc_frCheckBox.insets = new Insets(0, 5, 5, 0);
			gbc_frCheckBox.gridx = 0;
			gbc_frCheckBox.gridy = 2;
			checkBoxPanel.add(getFrCheckBox(), gbc_frCheckBox);
			GridBagConstraints gbc_esCheckBox = new GridBagConstraints();
			gbc_esCheckBox.anchor = GridBagConstraints.WEST;
			gbc_esCheckBox.insets = new Insets(0, 5, 5, 0);
			gbc_esCheckBox.gridx = 1;
			gbc_esCheckBox.gridy = 0;
			checkBoxPanel.add(getEsCheckBox(), gbc_esCheckBox);
			GridBagConstraints gbc_itCheckBox = new GridBagConstraints();
			gbc_itCheckBox.anchor = GridBagConstraints.NORTHWEST;
			gbc_itCheckBox.insets = new Insets(0, 5, 0, 0);
			gbc_itCheckBox.gridx = 1;
			gbc_itCheckBox.gridy = 1;
			checkBoxPanel.add(getItCheckBox(), gbc_itCheckBox);
			GridBagConstraints gbc_customCheckBox = new GridBagConstraints();
			gbc_customCheckBox.weighty = 1.0;
			gbc_customCheckBox.anchor = GridBagConstraints.NORTHWEST;
			gbc_customCheckBox.insets = new Insets(0, 5, 10, 0);
			gbc_customCheckBox.gridx = 1;
			gbc_customCheckBox.gridy = 2;
			checkBoxPanel.add(getCustomPanel(), gbc_customCheckBox);
		}
		return checkBoxPanel;
	}

	private JCheckBox getEnCheckBox()
	{
		if (enCheckBox == null)
		{
			enCheckBox = new JCheckBox(EN);
			enCheckBox.setSelected(model.getDescription().length() == 0);
		}
		return enCheckBox;
	}

	private JCheckBox getDeCheckBox()
	{
		if (deCheckBox == null)
		{
			deCheckBox = new JCheckBox(DE);
			deCheckBox.setSelected(model.getDescriptionDe().length() == 0);
		}
		return deCheckBox;
	}

	private JCheckBox getFrCheckBox()
	{
		if (frCheckBox == null)
		{
			frCheckBox = new JCheckBox(FR);
			frCheckBox.setSelected(model.getDescriptionFr().length() == 0);
		}
		return frCheckBox;
	}

	private JCheckBox getEsCheckBox()
	{
		if (esCheckBox == null)
		{
			esCheckBox = new JCheckBox(ES);
			esCheckBox.setSelected(model.getDescriptionEs().length() == 0);
		}
		return esCheckBox;
	}

	private JCheckBox getItCheckBox()
	{
		if (itCheckBox == null)
		{
			itCheckBox = new JCheckBox(IT);
			itCheckBox.setSelected(model.getDescriptionIt().length() == 0);
		}
		return itCheckBox;
	}

	private void enableCheckBoxes()
	{
		String selectedFromLanguage = getFromComboBox().getSelectedItem().toString();
		getEnCheckBox().setEnabled(!getEnCheckBox().getText().equals(selectedFromLanguage));
		getDeCheckBox().setEnabled(!getDeCheckBox().getText().equals(selectedFromLanguage));
		getFrCheckBox().setEnabled(!getFrCheckBox().getText().equals(selectedFromLanguage));
		getEsCheckBox().setEnabled(!getEsCheckBox().getText().equals(selectedFromLanguage));
		getItCheckBox().setEnabled(!getItCheckBox().getText().equals(selectedFromLanguage));
	}

	private void disableCheckBoxes()
	{
		getEnCheckBox().setEnabled(false);
		getDeCheckBox().setEnabled(false);
		getFrCheckBox().setEnabled(false);
		getEsCheckBox().setEnabled(false);
		getItCheckBox().setEnabled(false);
	}

	String getSelectedFromLanguage()
	{
		String selectedFromLanguage = getFromComboBox().getSelectedItem().toString();
		String returnString = selectedFromLanguage.substring(selectedFromLanguage.indexOf("(") + 1,
				selectedFromLanguage.lastIndexOf(")"));
		return returnString;
	}

	List<String> getSelectedToLanguages()
	{
		List<String> returnList = new ArrayList<>();
		if (getCustomCheckBox().isSelected())
		{
			returnList.add(getCustomTextField().getText());
		} else
		{
			if (getEnCheckBox().isSelected() && getEnCheckBox().isEnabled())
			{
				returnList.add("en");
			}
			if (getDeCheckBox().isSelected() && getDeCheckBox().isEnabled())
			{
				returnList.add("de");
			}
			if (getFrCheckBox().isSelected() && getFrCheckBox().isEnabled())
			{
				returnList.add("fr");
			}
			if (getEsCheckBox().isSelected() && getEsCheckBox().isEnabled())
			{
				returnList.add("es");
			}
			if (getItCheckBox().isSelected() && getItCheckBox().isEnabled())
			{
				returnList.add("it");
			}
		}

		// Store custom language if entered
		if (!getCustomTextField().getText().isEmpty())
		{
			FileManager.getConfiguredProperties().put(CUSTOM_LANGUAGE_PROPERTY, getCustomTextField().getText());
		}
		return returnList;
	}

	private JPanel getCustomPanel()
	{
		if (customPanel == null)
		{
			customPanel = new JPanel();
			GridBagLayout gbl_customPanel = new GridBagLayout();
			customPanel.setLayout(gbl_customPanel);
			GridBagConstraints gbc_customCheckBox = new GridBagConstraints();
			gbc_customCheckBox.anchor = GridBagConstraints.WEST;
			gbc_customCheckBox.gridx = 0;
			gbc_customCheckBox.gridy = 0;
			customPanel.add(getCustomCheckBox(), gbc_customCheckBox);
			GridBagConstraints gbc_customTextField = new GridBagConstraints();
			gbc_customTextField.anchor = GridBagConstraints.WEST;
			gbc_customTextField.weightx = 1.0;
			gbc_customTextField.insets = new Insets(0, 0, 0, 5);
			gbc_customTextField.gridx = 1;
			gbc_customTextField.gridy = 0;
			customPanel.add(getCustomTextField(), gbc_customTextField);
			GridBagConstraints gbc_infoLabel = new GridBagConstraints();
			gbc_infoLabel.gridwidth = 2;
			gbc_infoLabel.gridx = 0;
			gbc_infoLabel.gridy = 1;
			customPanel.add(getInfoLabel(), gbc_infoLabel);
		}
		return customPanel;
	}

	private JCheckBox getCustomCheckBox()
	{
		if (customCheckBox == null)
		{
			customCheckBox = new JCheckBox("Language:");
			customCheckBox.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if (customCheckBox.isSelected())
					{
						disableCheckBoxes();
						getInfoLabel().setEnabled(true);
						getCustomTextField().setEnabled(true);
					} else
					{
						enableCheckBoxes();
						getInfoLabel().setEnabled(false);
						getCustomTextField().setEnabled(false);
					}
				}
			});

		}
		return customCheckBox;
	}

	private JTextField getCustomTextField()
	{
		if (customTextField == null)
		{
			customTextField = new JTextField();
			customTextField.setToolTipText("Add two-letter language code (ISO 639-1)");
			customTextField.setColumns(2);
			customTextField.setEnabled(false);
			customTextField.addKeyListener(new KeyAdapter()
			{
				public void keyTyped(KeyEvent e)
				{
					char c = e.getKeyChar();

					if (c != KeyEvent.VK_BACK_SPACE)
					{
						if (customTextField.getText().length() > 1 && (customTextField.getSelectedText() == null || customTextField.getSelectedText().length() == 0))
						{
							e.consume(); // ignore event
						}
					}
				}
			});
		}
		return customTextField;
	}

	private JLabel getInfoLabel()
	{
		if (infoLabel == null)
		{
			infoLabel = new JLabel("(Translation will be added to the English field)");
			infoLabel.setEnabled(false);
		}
		return infoLabel;
	}
}
