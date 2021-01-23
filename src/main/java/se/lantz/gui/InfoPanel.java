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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import se.lantz.model.InfoModel;

public class InfoPanel extends JPanel
{
  private InfoModel model;
  private JLabel titleLabel;
  private JTextField titleField;
  private JLabel authorLabel;
  private JTextField authorField;
  private JLabel composerLabel;
  private JTextField composerField;
  private JLabel yearLabel;
  private JSpinner yearField;
  private JLabel genreLabel;
  private GenreComboBox genreComboBox;
  private ScreenshotsPanel screensPanel;
  private JTabbedPane descriptionTabbedPane;
  private DescriptionPanel descriptionPanel;
  private DescriptionPanel descriptionDePanel;
  private DescriptionPanel descriptionFrPanel;
  private DescriptionPanel descriptionEsPanel;
  private DescriptionPanel descriptionItPanel;

  public InfoPanel(InfoModel model)
  {
    this.model = model;
    this.setPreferredSize(new Dimension(729, 330));
    GridBagLayout gbl_this = new GridBagLayout();
    this.setLayout(gbl_this);
    GridBagConstraints gbc_titleLabel = new GridBagConstraints();
    gbc_titleLabel.anchor = GridBagConstraints.WEST;
    gbc_titleLabel.insets = new Insets(10, 10, 0, 5);
    gbc_titleLabel.gridx = 0;
    gbc_titleLabel.gridy = 0;
    this.add(getTitleLabel(), gbc_titleLabel);
    GridBagConstraints gbc_titleField = new GridBagConstraints();
    gbc_titleField.insets = new Insets(0, 10, 5, 5);
    gbc_titleField.fill = GridBagConstraints.HORIZONTAL;
    gbc_titleField.gridx = 0;
    gbc_titleField.gridy = 1;
    this.add(getTitleField(), gbc_titleField);
    GridBagConstraints gbc_yearField = new GridBagConstraints();
    gbc_yearField.insets = new Insets(0, 0, 5, 5);
    gbc_yearField.fill = GridBagConstraints.HORIZONTAL;
    gbc_yearField.gridx = 1;
    gbc_yearField.gridy = 1;
    this.add(getYearField(), gbc_yearField);
    GridBagConstraints gbc_authorLabel = new GridBagConstraints();
    gbc_authorLabel.insets = new Insets(0, 10, 0, 5);
    gbc_authorLabel.anchor = GridBagConstraints.WEST;
    gbc_authorLabel.gridx = 0;
    gbc_authorLabel.gridy = 2;
    this.add(getAuthorLabel(), gbc_authorLabel);
    GridBagConstraints gbc_genreLabel = new GridBagConstraints();
    gbc_genreLabel.anchor = GridBagConstraints.WEST;
    gbc_genreLabel.insets = new Insets(0, 0, 0, 5);
    gbc_genreLabel.gridx = 1;
    gbc_genreLabel.gridy = 2;
    this.add(getGenreLabel(), gbc_genreLabel);
    GridBagConstraints gbc_authorField = new GridBagConstraints();
    gbc_authorField.fill = GridBagConstraints.HORIZONTAL;
    gbc_authorField.anchor = GridBagConstraints.WEST;
    gbc_authorField.insets = new Insets(0, 10, 5, 100);
    gbc_authorField.gridx = 0;
    gbc_authorField.gridy = 3;
    this.add(getAuthorField(), gbc_authorField);
    GridBagConstraints gbc_composerLabel = new GridBagConstraints();
    gbc_composerLabel.anchor = GridBagConstraints.WEST;
    gbc_composerLabel.insets = new Insets(0, 10, 0, 5);
    gbc_composerLabel.gridx = 0;
    gbc_composerLabel.gridy = 4;
    this.add(getComposerLabel(), gbc_composerLabel);
    GridBagConstraints gbc_composerField = new GridBagConstraints();
    gbc_composerField.fill = GridBagConstraints.HORIZONTAL;
    gbc_composerField.anchor = GridBagConstraints.WEST;
    gbc_composerField.insets = new Insets(0, 10, 5, 100);
    gbc_composerField.gridx = 0;
    gbc_composerField.gridy = 5;
    this.add(getComposerField(), gbc_composerField);
    GridBagConstraints gbc_yearLabel = new GridBagConstraints();
    gbc_yearLabel.anchor = GridBagConstraints.WEST;
    gbc_yearLabel.insets = new Insets(10, 0, 0, 5);
    gbc_yearLabel.gridx = 1;
    gbc_yearLabel.gridy = 0;
    this.add(getYearLabel(), gbc_yearLabel);
    GridBagConstraints gbc_genreComboBox = new GridBagConstraints();
    gbc_genreComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_genreComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_genreComboBox.gridx = 1;
    gbc_genreComboBox.gridy = 3;
    this.add(getGenreComboBox(), gbc_genreComboBox);
    GridBagConstraints gbc_descriptionTabbedPane = new GridBagConstraints();
    gbc_descriptionTabbedPane.fill = GridBagConstraints.BOTH;
    gbc_descriptionTabbedPane.insets = new Insets(0, 10, 5, 5);
    gbc_descriptionTabbedPane.gridx = 0;
    gbc_descriptionTabbedPane.gridy = 6;
    gbc_descriptionTabbedPane.gridheight = 3;
    add(getDescriptionTabbedPane(), gbc_descriptionTabbedPane);
    GridBagConstraints gbc_screensPanel = new GridBagConstraints();
    gbc_screensPanel.fill = GridBagConstraints.BOTH;
    gbc_screensPanel.weighty = 1.0;
    gbc_screensPanel.anchor = GridBagConstraints.NORTH;
    gbc_screensPanel.weightx = 1.0;
    gbc_screensPanel.insets = new Insets(0, 10, 0, 0);
    gbc_screensPanel.gridheight = 9;
    gbc_screensPanel.gridx = 2;
    gbc_screensPanel.gridy = 0;
    this.add(getScreensPanel(), gbc_screensPanel);

    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener((e) -> modelChanged());
    }
  }

  private void modelChanged()
  {
    // Read from model
    if (!getTitleField().hasFocus())
    {
      getTitleField().setText(model.getTitle());
    }
    if (!getDescriptionPanel().getDescriptionTextArea().hasFocus())
    {
      getDescriptionPanel().getDescriptionTextArea().setText(model.getDescription());
    }
    if (!getDescriptionDePanel().getDescriptionTextArea().hasFocus())
    {
      getDescriptionDePanel().getDescriptionTextArea().setText(model.getDescriptionDe());
    }
    if (!getDescriptionFrPanel().getDescriptionTextArea().hasFocus())
    {
      getDescriptionFrPanel().getDescriptionTextArea().setText(model.getDescriptionFr());
    }
    if (!getDescriptionEsPanel().getDescriptionTextArea().hasFocus())
    {
      getDescriptionEsPanel().getDescriptionTextArea().setText(model.getDescriptionEs());
    }
    if (!getDescriptionItPanel().getDescriptionTextArea().hasFocus())
    {
      getDescriptionItPanel().getDescriptionTextArea().setText(model.getDescriptionIt());
    }
    if (!getYearField().hasFocus())
    {
      getYearField().setValue(model.getYear());
    }
    if (!getGenreComboBox().hasFocus())
    {
      getGenreComboBox().setSelectedGenre(model.getGenre());
    }
    if (!getAuthorField().hasFocus())
    {
      getAuthorField().setText(model.getAuthor());
    }
    if (!getComposerField().hasFocus())
    {
      getComposerField().setText(model.getComposer());
    }
  }

  private JLabel getTitleLabel()
  {
    if (titleLabel == null)
    {
      titleLabel = new JLabel("Game title");
    }
    return titleLabel;
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

  private JLabel getAuthorLabel()
  {
    if (authorLabel == null)
    {
      authorLabel = new JLabel("Author");
      authorLabel.setPreferredSize(new Dimension(145, 14));
    }
    return authorLabel;
  }

  private JTextField getAuthorField()
  {
    if (authorField == null)
    {
      authorField = new JTextField();
      authorField.setPreferredSize(new Dimension(300, 20));
      authorField.setColumns(10);
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

  private JLabel getComposerLabel()
  {
    if (composerLabel == null)
    {
      composerLabel = new JLabel("Composer");
      composerLabel.setPreferredSize(new Dimension(140, 14));
    }
    return composerLabel;
  }

  private JTextField getComposerField()
  {
    if (composerField == null)
    {
      composerField = new JTextField();
      composerField.setPreferredSize(new Dimension(200, 20));
      composerField.setColumns(10);
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

  private JLabel getYearLabel()
  {
    if (yearLabel == null)
    {
      yearLabel = new JLabel("Year");
    }
    return yearLabel;
  }

  private JSpinner getYearField()
  {
    if (yearField == null)
    {
      SpinnerModel spinnerModel = new SpinnerNumberModel(1986, //initial value
                                                         1978, //min
                                                         Calendar.getInstance().get(Calendar.YEAR), //max, no need to add more than current year
                                                         1);
      yearField = new JSpinner(spinnerModel);
      JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(yearField, "####");
      yearField.setEditor(numberEditor);
      //Select all when gaining focus
      numberEditor.getTextField().addFocusListener(new FocusAdapter()
        {
          @Override
          public void focusGained(final FocusEvent e)
          {
            SwingUtilities.invokeLater(() -> {
              JTextField tf = (JTextField) e.getSource();
              tf.selectAll();
            });
          }
        });

      yearField.addChangeListener(e -> {
        JSpinner textField = (JSpinner) e.getSource();
        model.setYear(Integer.parseInt(textField.getValue().toString()));

      });
    }
    return yearField;
  }

  private JLabel getGenreLabel()
  {
    if (genreLabel == null)
    {
      genreLabel = new JLabel("Genre");
    }
    return genreLabel;
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

  public ScreenshotsPanel getScreensPanel()
  {
    if (screensPanel == null)
    {
      screensPanel = new ScreenshotsPanel(this.model);
    }
    return screensPanel;
  }

  void focusTitleField()
  {
    getTitleField().requestFocus();
  }

  private JTabbedPane getDescriptionTabbedPane()
  {
    if (descriptionTabbedPane == null)
    {
      descriptionTabbedPane = new JTabbedPane(JTabbedPane.TOP);
      descriptionTabbedPane.setPreferredSize(new Dimension(290, 150));
      descriptionTabbedPane.addTab("Description: en", null, getDescriptionPanel(), null);
      descriptionTabbedPane.addTab("de", null, getDescriptionDePanel(), null);
      descriptionTabbedPane.addTab("fr", null, getDescriptionFrPanel(), null);
      descriptionTabbedPane.addTab("es", null, getDescriptionEsPanel(), null);
      descriptionTabbedPane.addTab("it", null, getDescriptionItPanel(), null);
    }
    return descriptionTabbedPane;
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
}
