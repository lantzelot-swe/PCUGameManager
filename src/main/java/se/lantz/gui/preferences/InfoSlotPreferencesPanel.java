package se.lantz.gui.preferences;

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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import se.lantz.gui.DescriptionPanel;
import se.lantz.gui.DescriptionTabComponent;
import se.lantz.gui.GenreComboBox;
import se.lantz.model.PreferencesModel;
import se.lantz.util.CustomUndoPlainDocument;
import se.lantz.util.TextComponentSupport;

public class InfoSlotPreferencesPanel extends JPanel
{
  private JTextField authorField;
  private JTextField composerField;
  JTabbedPane descriptionTabbedPane;
  private DescriptionPanel descriptionPanel;
  private DescriptionPanel descriptionDePanel;
  private DescriptionPanel descriptionFrPanel;
  private DescriptionPanel descriptionEsPanel;
  private DescriptionPanel descriptionItPanel;
  private DescriptionTabComponent enDescrTabComponent;
  private DescriptionTabComponent deDescrTabComponent;
  private DescriptionTabComponent frDescrTabComponent;
  private DescriptionTabComponent esDescrTabComponent;
  private DescriptionTabComponent itDescrTabComponent;
  private PreferencesModel model;
  JSpinner yearField;
  GenreComboBox genreComboBox;

  public InfoSlotPreferencesPanel(PreferencesModel model)
  {
    this.model = model;
    this.setPreferredSize(new Dimension(335, 310));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWeights = new double[] { 1.0, 0.0 };
    gridBagLayout.columnWidths = new int[] { 0, 0 };
    setLayout(gridBagLayout);

    JLabel infoLabel = new JLabel("<html>Edit the fields below to specify how an infoslot is generated.</html>");
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_infoLabel.anchor = GridBagConstraints.WEST;
    gbc_infoLabel.gridwidth = 2;
    gbc_infoLabel.insets = new Insets(5, 5, 15, 0);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(infoLabel, gbc_infoLabel);

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

    GridBagConstraints gbc_descriptionTabbedPane = new GridBagConstraints();
    gbc_descriptionTabbedPane.insets = new Insets(0, 5, 9, 0);
    gbc_descriptionTabbedPane.gridwidth = 2;
    gbc_descriptionTabbedPane.weighty = 1.0;
    gbc_descriptionTabbedPane.fill = GridBagConstraints.BOTH;
    gbc_descriptionTabbedPane.gridx = 0;
    gbc_descriptionTabbedPane.gridy = 6;
    add(getDescriptionTabbedPane(), gbc_descriptionTabbedPane);

    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(e -> modelChanged());
    }
  }
  
  protected void init()
  {
    modelChanged();
    TextComponentSupport.setupPopupAndUndoable(getAuthorField(),
                                               getComposerField(),
                                               getDescriptionPanel().getDescriptionTextArea(),
                                               getDescriptionDePanel().getDescriptionTextArea(),
                                               getDescriptionFrPanel().getDescriptionTextArea(),
                                               getDescriptionEsPanel().getDescriptionTextArea(),
                                               getDescriptionItPanel().getDescriptionTextArea());
  }

  private void modelChanged()
  {
    // Read from model
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

  private JTabbedPane getDescriptionTabbedPane()
  {
    if (descriptionTabbedPane == null)
    {
      descriptionTabbedPane = new JTabbedPane(JTabbedPane.TOP);
      descriptionTabbedPane.addTab("", null, getDescriptionPanel(), "English");
      descriptionTabbedPane.addTab("", null, getDescriptionDePanel(), "German");
      descriptionTabbedPane.addTab("", null, getDescriptionFrPanel(), "French");
      descriptionTabbedPane.addTab("", null, getDescriptionEsPanel(), "Spanish");
      descriptionTabbedPane.addTab("", null, getDescriptionItPanel(), "Italian");

      descriptionTabbedPane.setTabComponentAt(0, getEnDescrTabComponent());
      descriptionTabbedPane.setTabComponentAt(1, getDeDescrTabComponent());
      descriptionTabbedPane.setTabComponentAt(2, getFrDescrTabComponent());
      descriptionTabbedPane.setTabComponentAt(3, getEsDescrTabComponent());
      descriptionTabbedPane.setTabComponentAt(4, getItDescrTabComponent());
    }
    return descriptionTabbedPane;
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
      authorField.setDocument(new CustomUndoPlainDocument()
        {
          @Override
          public void updateModel()
          {
            model.setAuthor(authorField.getText());
          }
        });
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
      composerField.setDocument(new CustomUndoPlainDocument()
        {
          @Override
          public void updateModel()
          {
            model.setComposer(composerField.getText());
          }
        });
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
      descriptionPanel.addCharCountChangeListener(e -> {
        getEnDescrTabComponent().setBold(Integer.parseInt(e.getActionCommand()) > 0);
      });
    }
    return descriptionPanel;
  }

  private DescriptionPanel getDescriptionDePanel()
  {
    if (descriptionDePanel == null)
    {
      descriptionDePanel = new DescriptionPanel(model, DescriptionPanel.Language.de);
      descriptionDePanel.addCharCountChangeListener(e -> {
        getDeDescrTabComponent().setBold(Integer.parseInt(e.getActionCommand()) > 0);
      });
    }
    return descriptionDePanel;
  }

  private DescriptionPanel getDescriptionFrPanel()
  {
    if (descriptionFrPanel == null)
    {
      descriptionFrPanel = new DescriptionPanel(model, DescriptionPanel.Language.fr);
      descriptionFrPanel.addCharCountChangeListener(e -> {
        getFrDescrTabComponent().setBold(Integer.parseInt(e.getActionCommand()) > 0);
      });
    }
    return descriptionFrPanel;
  }

  private DescriptionPanel getDescriptionEsPanel()
  {
    if (descriptionEsPanel == null)
    {
      descriptionEsPanel = new DescriptionPanel(model, DescriptionPanel.Language.es);
      descriptionEsPanel.addCharCountChangeListener(e -> {
        getEsDescrTabComponent().setBold(Integer.parseInt(e.getActionCommand()) > 0);
      });
    }
    return descriptionEsPanel;
  }

  private DescriptionPanel getDescriptionItPanel()
  {
    if (descriptionItPanel == null)
    {
      descriptionItPanel = new DescriptionPanel(model, DescriptionPanel.Language.it);
      descriptionItPanel.addCharCountChangeListener(e -> {
        getItDescrTabComponent().setBold(Integer.parseInt(e.getActionCommand()) > 0);
      });
    }
    return descriptionItPanel;
  }

  private DescriptionTabComponent getEnDescrTabComponent()
  {
    if (enDescrTabComponent == null)
    {
      enDescrTabComponent = new DescriptionTabComponent("Description:");
      enDescrTabComponent.setText("en");
    }
    return enDescrTabComponent;
  }

  private DescriptionTabComponent getDeDescrTabComponent()
  {
    if (deDescrTabComponent == null)
    {
      deDescrTabComponent = new DescriptionTabComponent("");
      deDescrTabComponent.setText("de");
    }
    return deDescrTabComponent;
  }

  private DescriptionTabComponent getFrDescrTabComponent()
  {
    if (frDescrTabComponent == null)
    {
      frDescrTabComponent = new DescriptionTabComponent("");
      frDescrTabComponent.setText("fr");
    }
    return frDescrTabComponent;
  }

  private DescriptionTabComponent getEsDescrTabComponent()
  {
    if (esDescrTabComponent == null)
    {
      esDescrTabComponent = new DescriptionTabComponent("");
      esDescrTabComponent.setText("es");
    }
    return esDescrTabComponent;
  }

  private DescriptionTabComponent getItDescrTabComponent()
  {
    if (itDescrTabComponent == null)
    {
      itDescrTabComponent = new DescriptionTabComponent("");
      itDescrTabComponent.setText("it");
    }
    return itDescrTabComponent;
  }

  public void selectEnDescriptionTab()
  {
    descriptionTabbedPane.setSelectedIndex(0);
  }
}
