package se.lantz.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.Calendar;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;

import se.lantz.model.InfoModel;
import se.lantz.util.TextComponentSupport;

public class InfoPanel extends JPanel
{
	private JTextField titleField;
	private JTextField authorField;
	private JTextField composerField;
	JTabbedPane descriptionTabbedPane;
	private DescriptionPanel descriptionPanel;
	private DescriptionPanel descriptionDePanel;
	private DescriptionPanel descriptionFrPanel;
	private DescriptionPanel descriptionEsPanel;
	private DescriptionPanel descriptionItPanel;
	private InfoModel model;
	JSpinner yearField;
	GenreComboBox genreComboBox;

	public InfoPanel(InfoModel model)
	{
		this.model = model;
		this.setPreferredSize(new Dimension(330, 275));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]
		{ 1.0, 0.0 };
		gridBagLayout.columnWidths = new int[]
		{ 0, 0 };
		setLayout(gridBagLayout);

		JLabel titleLabel = new JLabel("Game title");
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.anchor = GridBagConstraints.WEST;
		gbc_titleLabel.gridwidth = 2;
		gbc_titleLabel.insets = new Insets(5, 5, 0, 0);
		gbc_titleLabel.gridx = 0;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);

		GridBagConstraints gbc_titleField = new GridBagConstraints();
		gbc_titleField.gridwidth = 2;
		gbc_titleField.insets = new Insets(0, 5, 5, 0);
		gbc_titleField.fill = GridBagConstraints.HORIZONTAL;
		gbc_titleField.gridx = 0;
		gbc_titleField.gridy = 1;
		add(getTitleField(), gbc_titleField);

		JLabel authorLabel = new JLabel("Author");
		GridBagConstraints gbc_authorLabel = new GridBagConstraints();
		gbc_authorLabel.anchor = GridBagConstraints.WEST;
		gbc_authorLabel.insets = new Insets(0, 5, 0, 0);
		gbc_authorLabel.gridx = 0;
		gbc_authorLabel.gridy = 2;
		add(authorLabel, gbc_authorLabel);

		GridBagConstraints gbc_authorField = new GridBagConstraints();
		gbc_authorField.insets = new Insets(0, 5, 5, 5);
		gbc_authorField.fill = GridBagConstraints.HORIZONTAL;
		gbc_authorField.gridx = 0;
		gbc_authorField.gridy = 3;
		add(getAuthorField(), gbc_authorField);

		JLabel composerLabel = new JLabel("Composer");
		GridBagConstraints gbc_composerLabel = new GridBagConstraints();
		gbc_composerLabel.anchor = GridBagConstraints.WEST;
		gbc_composerLabel.insets = new Insets(0, 5, 0, 0);
		gbc_composerLabel.gridx = 0;
		gbc_composerLabel.gridy = 4;
		add(composerLabel, gbc_composerLabel);

		GridBagConstraints gbc_composerField = new GridBagConstraints();
		gbc_composerField.insets = new Insets(0, 5, 5, 5);
		gbc_composerField.fill = GridBagConstraints.HORIZONTAL;
		gbc_composerField.gridx = 0;
		gbc_composerField.gridy = 5;
		add(getComposerField(), gbc_composerField);

		JLabel yearLabel = new JLabel("Year");
		GridBagConstraints gbc_yearLabel = new GridBagConstraints();
		gbc_yearLabel.anchor = GridBagConstraints.WEST;
		gbc_yearLabel.gridx = 1;
		gbc_yearLabel.gridy = 4;
		add(yearLabel, gbc_yearLabel);

		GridBagConstraints gbc_yearField = new GridBagConstraints();
		gbc_yearField.anchor = GridBagConstraints.WEST;
		gbc_yearField.insets = new Insets(0, 0, 5, 0);
		gbc_yearField.gridx = 1;
		gbc_yearField.gridy = 5;
		add(getYearField(), gbc_yearField);

		JLabel genreLabel = new JLabel("Genre");
		GridBagConstraints gbc_genreLabel = new GridBagConstraints();
		gbc_genreLabel.anchor = GridBagConstraints.WEST;
		gbc_genreLabel.gridx = 1;
		gbc_genreLabel.gridy = 2;
		add(genreLabel, gbc_genreLabel);

		GridBagConstraints gbc_genreCombobox = new GridBagConstraints();
		gbc_genreCombobox.anchor = GridBagConstraints.WEST;
		gbc_genreCombobox.insets = new Insets(0, 0, 5, 0);
		gbc_genreCombobox.fill = GridBagConstraints.HORIZONTAL;
		gbc_genreCombobox.gridx = 1;
		gbc_genreCombobox.gridy = 3;
		add(getGenreComboBox(), gbc_genreCombobox);

		descriptionTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		descriptionTabbedPane.addTab("Description: en", null, getDescriptionPanel(), null);
		descriptionTabbedPane.addTab("de", null, getDescriptionDePanel(), null);
		descriptionTabbedPane.addTab("fr", null, getDescriptionFrPanel(), null);
		descriptionTabbedPane.addTab("es", null, getDescriptionEsPanel(), null);
		descriptionTabbedPane.addTab("it", null, getDescriptionItPanel(), null);
		GridBagConstraints gbc_descriptionTabbedPane = new GridBagConstraints();
		gbc_descriptionTabbedPane.insets = new Insets(0, 5, 10, 0);
		gbc_descriptionTabbedPane.gridwidth = 2;
		gbc_descriptionTabbedPane.weighty = 1.0;
		gbc_descriptionTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_descriptionTabbedPane.gridx = 0;
		gbc_descriptionTabbedPane.gridy = 6;
		add(descriptionTabbedPane, gbc_descriptionTabbedPane);

		TextComponentSupport.setupPopupAndUndoable(getTitleField(), getAuthorField(), getComposerField(),
				getDescriptionPanel().getDescriptionTextArea(), getDescriptionDePanel().getDescriptionTextArea(),
				getDescriptionFrPanel().getDescriptionTextArea(), getDescriptionEsPanel().getDescriptionTextArea(),
				getDescriptionItPanel().getDescriptionTextArea());
		if (!Beans.isDesignTime())
		{
			model.addPropertyChangeListener(e -> modelChanged());
		}
	}

	private void modelChanged()
	{
		// Read from model
		if (!getTitleField().hasFocus() && !getTitleField().getText().equals(model.getTitle()))
		{
			getTitleField().setText(model.getTitle());
		}
		JTextArea descriptionTextArea = getDescriptionPanel().getDescriptionTextArea();
		if (!descriptionTextArea.hasFocus() && !descriptionTextArea.getText().equals(model.getDescription()))
		{
			descriptionTextArea.setText(model.getDescription());
		}
		JTextArea descriptionDeTextArea = getDescriptionDePanel().getDescriptionTextArea();
		if (!descriptionDeTextArea.hasFocus() && !descriptionDeTextArea.getText().equals(model.getDescriptionDe()))
		{
			descriptionDeTextArea.setText(model.getDescriptionDe());
		}
		JTextArea descriptionFrTextArea = getDescriptionFrPanel().getDescriptionTextArea();
		if (!descriptionFrTextArea.hasFocus() && !descriptionFrTextArea.getText().equals(model.getDescriptionFr()))
		{
			descriptionFrTextArea.setText(model.getDescriptionFr());
		}
		JTextArea descriptionEsTextArea = getDescriptionEsPanel().getDescriptionTextArea();
		if (!descriptionEsTextArea.hasFocus() && !descriptionEsTextArea.getText().equals(model.getDescriptionEs()))
		{
			descriptionEsTextArea.setText(model.getDescriptionEs());
		}
		JTextArea descriptionItTextArea = getDescriptionItPanel().getDescriptionTextArea();
		if (!descriptionItTextArea.hasFocus() && !descriptionItTextArea.getText().equals(model.getDescriptionIt()))
		{
			descriptionItTextArea.setText(model.getDescriptionIt());
		}
		if (!getYearField().hasFocus() && !getYearField().getValue().equals(model.getYear()))
		{
			getYearField().setValue(model.getYear());
		}
		if (!getGenreComboBox().hasFocus() && !getGenreComboBox().getSelectedGenre().equals(model.getGenre()))
		{
			getGenreComboBox().setSelectedGenre(model.getGenre());
		}
		if (!getAuthorField().hasFocus() && !getAuthorField().getText().equals(model.getAuthor()))
		{
			getAuthorField().setText(model.getAuthor());
		}
		if (!getComposerField().hasFocus() && !getComposerField().getText().equals(model.getComposer()))
		{
			getComposerField().setText(model.getComposer());
		}
	}

	private JTextField getTitleField()
	{
		if (titleField == null)
		{
			titleField = new JTextField();
			titleField.addKeyListener(new KeyAdapter()
			{
				@Override
				public void keyReleased(KeyEvent e)
				{
					JTextField textField = (JTextField) e.getSource();
					model.setTitle(textField.getText());
				}
			});
		}
		return titleField;
	}

	private JSpinner getYearField()
	{
		if (yearField == null)
		{
			SpinnerModel spinnerModel = new SpinnerNumberModel(1986, // initial value
					1978, // min
					Calendar.getInstance().get(Calendar.YEAR), // max, no need to add more than current year
					1);
			yearField = new JSpinner(spinnerModel);
			JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(yearField, "####");
			yearField.setEditor(numberEditor);
			// Select all when gaining focus
			numberEditor.getTextField().addFocusListener(new FocusAdapter()
			{
				@Override
				public void focusGained(final FocusEvent e)
				{
					SwingUtilities.invokeLater(() ->
					{
						JTextField tf = (JTextField) e.getSource();
						tf.selectAll();
					});
				}
			});

			yearField.addChangeListener(e ->
			{
				JSpinner textField = (JSpinner) e.getSource();
				model.setYear(Integer.parseInt(textField.getValue().toString()));

			});
		}
		return yearField;
	}

	private GenreComboBox getGenreComboBox()
	{
		if (genreComboBox == null)
		{
			genreComboBox = new GenreComboBox();
			genreComboBox.addActionListener(e -> model.setGenre(genreComboBox.getSelectedGenre()));
		}
		return genreComboBox;
	}

	private JTextField getAuthorField()
	{
		if (authorField == null)
		{
			authorField = new JTextField();
			authorField.addKeyListener(new KeyAdapter()
			{
				@Override
				public void keyReleased(KeyEvent e)
				{
					JTextField textField = (JTextField) e.getSource();
					model.setAuthor(textField.getText());
				}
			});
		}
		return authorField;
	}

	private JTextField getComposerField()
	{
		if (composerField == null)
		{
			composerField = new JTextField();
			composerField.addKeyListener(new KeyAdapter()
			{
				@Override
				public void keyReleased(KeyEvent e)
				{
					JTextField textField = (JTextField) e.getSource();
					model.setComposer(textField.getText());
				}
			});
		}
		return composerField;
	}

	private DescriptionPanel getDescriptionPanel()
	{
		if (descriptionPanel == null)
		{
			descriptionPanel = new DescriptionPanel(model, DescriptionPanel.Language.en);
		}
		return descriptionPanel;
	}

	private DescriptionPanel getDescriptionDePanel()
	{
		if (descriptionDePanel == null)
		{
			descriptionDePanel = new DescriptionPanel(model, DescriptionPanel.Language.de);
		}
		return descriptionDePanel;
	}

	private DescriptionPanel getDescriptionFrPanel()
	{
		if (descriptionFrPanel == null)
		{
			descriptionFrPanel = new DescriptionPanel(model, DescriptionPanel.Language.fr);
		}
		return descriptionFrPanel;
	}

	private DescriptionPanel getDescriptionEsPanel()
	{
		if (descriptionEsPanel == null)
		{
			descriptionEsPanel = new DescriptionPanel(model, DescriptionPanel.Language.es);
		}
		return descriptionEsPanel;
	}

	private DescriptionPanel getDescriptionItPanel()
	{
		if (descriptionItPanel == null)
		{
			descriptionItPanel = new DescriptionPanel(model, DescriptionPanel.Language.it);
		}
		return descriptionItPanel;
	}

	public void selectEnDescriptionTab()
	{
		descriptionTabbedPane.setSelectedIndex(0);
	}

	void focusTitleField()
	{
		getTitleField().requestFocus();
	}
}
