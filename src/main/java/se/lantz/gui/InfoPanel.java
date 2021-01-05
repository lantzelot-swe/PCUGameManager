package se.lantz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;

import se.lantz.model.InfoModel;

public class InfoPanel extends JPanel
{
  private InfoModel model;
  private JPanel textPanel;
  private JPanel settingsPanel;
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
  private JLabel descriptionLabel;
  private JScrollPane descriptionScrollPane;
  private JTextArea descriptionTextArea;
  private ScreenshotsPanel screensPanel;
  private JLabel charCountLabel;

  public InfoPanel(InfoModel model)
  {
    this.model = model;
    this.setPreferredSize(new Dimension(729, 330));
    GridBagLayout gbl_this = new GridBagLayout();
    this.setLayout(gbl_this);
    GridBagConstraints gbc_titleLabel = new GridBagConstraints();
    gbc_titleLabel.gridwidth = 2;
    gbc_titleLabel.anchor = GridBagConstraints.WEST;
    gbc_titleLabel.insets = new Insets(10, 10, 0, 5);
    gbc_titleLabel.gridx = 0;
    gbc_titleLabel.gridy = 0;
    this.add(getTitleLabel(), gbc_titleLabel);
    GridBagConstraints gbc_titleField = new GridBagConstraints();
    gbc_titleField.gridwidth = 2;
    gbc_titleField.insets = new Insets(0, 10, 5, 5);
    gbc_titleField.fill = GridBagConstraints.HORIZONTAL;
    gbc_titleField.gridx = 0;
    gbc_titleField.gridy = 1;
    this.add(getTitleField(), gbc_titleField);
    GridBagConstraints gbc_yearField = new GridBagConstraints();
    gbc_yearField.insets = new Insets(0, 0, 5, 5);
    gbc_yearField.fill = GridBagConstraints.HORIZONTAL;
    gbc_yearField.gridx = 2;
    gbc_yearField.gridy = 1;
    this.add(getYearField(), gbc_yearField);
    GridBagConstraints gbc_authorLabel = new GridBagConstraints();
    gbc_authorLabel.gridwidth = 2;
    gbc_authorLabel.insets = new Insets(0, 10, 0, 5);
    gbc_authorLabel.anchor = GridBagConstraints.WEST;
    gbc_authorLabel.gridx = 0;
    gbc_authorLabel.gridy = 2;
    this.add(getAuthorLabel(), gbc_authorLabel);
    GridBagConstraints gbc_genreLabel = new GridBagConstraints();
    gbc_genreLabel.anchor = GridBagConstraints.WEST;
    gbc_genreLabel.insets = new Insets(0, 0, 0, 5);
    gbc_genreLabel.gridx = 2;
    gbc_genreLabel.gridy = 2;
    this.add(getGenreLabel(), gbc_genreLabel);
    GridBagConstraints gbc_authorField = new GridBagConstraints();
    gbc_authorField.fill = GridBagConstraints.HORIZONTAL;
    gbc_authorField.anchor = GridBagConstraints.WEST;
    gbc_authorField.gridwidth = 2;
    gbc_authorField.insets = new Insets(0, 10, 5, 100);
    gbc_authorField.gridx = 0;
    gbc_authorField.gridy = 3;
    this.add(getAuthorField(), gbc_authorField);
    GridBagConstraints gbc_composerLabel = new GridBagConstraints();
    gbc_composerLabel.gridwidth = 2;
    gbc_composerLabel.anchor = GridBagConstraints.WEST;
    gbc_composerLabel.insets = new Insets(0, 10, 0, 5);
    gbc_composerLabel.gridx = 0;
    gbc_composerLabel.gridy = 4;
    this.add(getComposerLabel(), gbc_composerLabel);
    GridBagConstraints gbc_composerField = new GridBagConstraints();
    gbc_composerField.fill = GridBagConstraints.HORIZONTAL;
    gbc_composerField.anchor = GridBagConstraints.WEST;
    gbc_composerField.gridwidth = 2;
    gbc_composerField.insets = new Insets(0, 10, 5, 100);
    gbc_composerField.gridx = 0;
    gbc_composerField.gridy = 5;
    this.add(getComposerField(), gbc_composerField);
    GridBagConstraints gbc_yearLabel = new GridBagConstraints();
    gbc_yearLabel.anchor = GridBagConstraints.WEST;
    gbc_yearLabel.insets = new Insets(10, 0, 0, 5);
    gbc_yearLabel.gridx = 2;
    gbc_yearLabel.gridy = 0;
    this.add(getYearLabel(), gbc_yearLabel);
    GridBagConstraints gbc_genreComboBox = new GridBagConstraints();
    gbc_genreComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_genreComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_genreComboBox.gridx = 2;
    gbc_genreComboBox.gridy = 3;
    this.add(getGenreComboBox(), gbc_genreComboBox);
    GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
    gbc_descriptionLabel.gridwidth = 2;
    gbc_descriptionLabel.insets = new Insets(0, 10, 0, 5);
    gbc_descriptionLabel.anchor = GridBagConstraints.WEST;
    gbc_descriptionLabel.gridx = 0;
    gbc_descriptionLabel.gridy = 6;
    this.add(getDescriptionLabel(), gbc_descriptionLabel);
    GridBagConstraints gbc_descriptionScrollPane = new GridBagConstraints();
    gbc_descriptionScrollPane.anchor = GridBagConstraints.NORTH;
    gbc_descriptionScrollPane.gridwidth = 2;
    gbc_descriptionScrollPane.insets = new Insets(0, 10, 0, 5);
    gbc_descriptionScrollPane.fill = GridBagConstraints.HORIZONTAL;
    gbc_descriptionScrollPane.gridx = 0;
    gbc_descriptionScrollPane.gridy = 7;
    this.add(getDescriptionScrollPane(), gbc_descriptionScrollPane);
    GridBagConstraints gbc_screensPanel = new GridBagConstraints();
    gbc_screensPanel.fill = GridBagConstraints.BOTH;
    gbc_screensPanel.weighty = 1.0;
    gbc_screensPanel.anchor = GridBagConstraints.NORTH;
    gbc_screensPanel.weightx = 1.0;
    gbc_screensPanel.insets = new Insets(0, 10, 0, 0);
    gbc_screensPanel.gridheight = 9;
    gbc_screensPanel.gridx = 3;
    gbc_screensPanel.gridy = 0;
    this.add(getScreensPanel(), gbc_screensPanel);
    GridBagConstraints gbc_charCountLabel = new GridBagConstraints();
    gbc_charCountLabel.gridwidth = 2;
    gbc_charCountLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_charCountLabel.insets = new Insets(5, 10, 0, 5);
    gbc_charCountLabel.gridx = 0;
    gbc_charCountLabel.gridy = 8;
    add(getCharCountLabel(), gbc_charCountLabel);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener((e) -> modelChanged());
    }
  }

  //	private void initFont()
  //	{
  //		try {
  //			c64Font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResource("/se/lantz/C64_Pro-STYLE.ttf").openStream());
  //			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
  //			genv.registerFont(c64Font);
  //			// makesure to derive the size
  //			c64Font = c64Font.deriveFont(12f);
  //		} catch (FontFormatException | IOException e) {
  //			// TODO Auto-generated catch block
  //			e.printStackTrace();
  //		}   
  //	
  //	}

  private void modelChanged()
  {
    // Read from model
    if (!getTitleField().hasFocus())
    {
      getTitleField().setText(model.getTitle());
    }
    if (!getDescriptionTextArea().hasFocus())
    {
      getDescriptionTextArea().setText(model.getDescription());
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

  private JPanel getSettingsPanel()
  {
    if (settingsPanel == null)
    {
      settingsPanel = new JPanel();
      settingsPanel.setLayout(new BorderLayout(0, 0));
    }
    return settingsPanel;
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

  private JLabel getDescriptionLabel()
  {
    if (descriptionLabel == null)
    {
      descriptionLabel = new JLabel("Description");
    }
    return descriptionLabel;
  }

  private JScrollPane getDescriptionScrollPane()
  {
    if (descriptionScrollPane == null)
    {
      descriptionScrollPane = new JScrollPane();
      descriptionScrollPane.setPreferredSize(new Dimension(290, 150));
      descriptionScrollPane.setViewportView(getDescriptionTextArea());
    }
    return descriptionScrollPane;
  }

  private JTextArea getDescriptionTextArea()
  {
    if (descriptionTextArea == null)
    {
      descriptionTextArea = new JTextArea();
      descriptionTextArea.setFont(getTitleField().getFont());
      descriptionTextArea.setWrapStyleWord(true);
      descriptionTextArea.setLineWrap(true);

      DefaultStyledDocument doc = new DefaultStyledDocument();
      doc.addDocumentListener(new DocumentListener()
        {
          @Override
          public void changedUpdate(DocumentEvent e)
          {
            updateDescriptionCharCount(e.getDocument().getLength());
          }

          @Override
          public void insertUpdate(DocumentEvent e)
          {
            updateDescriptionCharCount(e.getDocument().getLength());
          }

          @Override
          public void removeUpdate(DocumentEvent e)
          {
            updateDescriptionCharCount(e.getDocument().getLength());
          }
        });
      descriptionTextArea.setDocument(doc);

      //Setup tab and shift tab to transfer focus
      Set<KeyStroke> strokes = new HashSet<>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
      descriptionTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
      strokes = new HashSet<>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
      descriptionTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);

      getCharCountLabel()
        .setToolTipText("<html>The Carousel description screen can only show a limited number of characters.<br>Consider limiting the text to 512 characters at the most.</html>");

      updateDescriptionCharCount(doc.getLength());

      descriptionTextArea.addKeyListener(new KeyAdapter()
        {
          @Override
          public void keyReleased(KeyEvent e)
          {
            JTextArea textField = (JTextArea) e.getSource();
            model.setDescription(textField.getText());
          }
        });
      descriptionTextArea.addFocusListener(new FocusAdapter()
        {
          @Override
          public void focusLost(FocusEvent e)
          {
            //Read the text from the model again to "format" it
            JTextArea textField = (JTextArea) e.getSource();
            textField.setText(model.getDescription());
          }
        });
    }
    return descriptionTextArea;
  }

  public ScreenshotsPanel getScreensPanel()
  {
    if (screensPanel == null)
    {
      screensPanel = new ScreenshotsPanel(this.model);
    }
    return screensPanel;
  }

  private JLabel getCharCountLabel()
  {
    if (charCountLabel == null)
    {
      charCountLabel = new JLabel("0");
    }
    return charCountLabel;
  }

  private void updateDescriptionCharCount(int length)
  {
    if (length > 512)
    {
      getCharCountLabel().setIcon(UIManager.getIcon("OptionPane.warningIcon"));
    }
    else
    {
      getCharCountLabel().setIcon(null);
    }
    getCharCountLabel().setText(length + " characters (recommended max: 512)");
  }

  void focusTitleField()
  {
    getTitleField().requestFocus();
  }
}
