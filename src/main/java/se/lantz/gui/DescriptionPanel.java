package se.lantz.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import se.lantz.model.CommonInfoModel;
import se.lantz.util.CustomUndoPlainDocument;

public class DescriptionPanel extends JPanel
{
  public enum Language
  {
    en, de, fr, es, it
  }
  private ImageIcon warningIcon = new ImageIcon(getClass().getResource("/se/lantz/warning-icon.png"));
  
  private JTextArea descriptionTextArea;
  private JScrollPane descriptionScrollPane;
  private JLabel charCountLabel;
  private CommonInfoModel model;
  private Language language;
  private ActionListener listener;

  public DescriptionPanel(CommonInfoModel model, Language language)
  {
    this.model = model;
    this.language = language;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_descriptionScrollPane = new GridBagConstraints();
    gbc_descriptionScrollPane.weighty = 1.0;
    gbc_descriptionScrollPane.weightx = 1.0;
    gbc_descriptionScrollPane.insets = new Insets(0, 0, 5, 0);
    gbc_descriptionScrollPane.fill = GridBagConstraints.BOTH;
    gbc_descriptionScrollPane.gridx = 0;
    gbc_descriptionScrollPane.gridy = 0;
    add(getDescriptionScrollPane(), gbc_descriptionScrollPane);
    GridBagConstraints gbc_charCountLabel = new GridBagConstraints();
    gbc_charCountLabel.insets = new Insets(0, 5, 0, 5);
    gbc_charCountLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_charCountLabel.gridx = 0;
    gbc_charCountLabel.gridy = 1;
    add(getCharCountLabel(), gbc_charCountLabel);

  }

  public JTextArea getDescriptionTextArea()
  {
    if (descriptionTextArea == null)
    {
      descriptionTextArea = new JTextArea();
      descriptionTextArea.setBorder(null);
      descriptionTextArea.setFont(new JLabel().getFont().deriveFont(11.0f));
      descriptionTextArea.setWrapStyleWord(true);
      descriptionTextArea.setLineWrap(true);

      CustomUndoPlainDocument doc = new CustomUndoPlainDocument()
        {
          @Override
          public void updateModel()
          {
            updateDescriptionInModel();
          }
        };
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

      // Setup tab and shift tab to transfer focus
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
            updateDescriptionInModel();
          }
        });
      descriptionTextArea.addFocusListener(new FocusAdapter()
        {
          @Override
          public void focusLost(FocusEvent e)
          {
            // Read the text from the model again to "format" it
            JTextArea textField = (JTextArea) e.getSource();
            switch (language)
            {
            case de:
              if (!textField.getText().equals(model.getDescriptionDe()))
              {
                textField.setText(model.getDescriptionDe());
              }
              break;
            case en:
              if (!textField.getText().equals(model.getDescription()))
              {
                textField.setText(model.getDescription());
              }
              break;
            case es:
              if (!textField.getText().equals(model.getDescriptionEs()))
              {
                textField.setText(model.getDescriptionEs());
              }
              break;
            case fr:
              if (!textField.getText().equals(model.getDescriptionFr()))
              {
                textField.setText(model.getDescriptionFr());
              }
              break;
            case it:
              if (!textField.getText().equals(model.getDescriptionIt()))
              {
                textField.setText(model.getDescriptionIt());
              }
              break;
            default:
              break;
            }
          }
        });
    }
    return descriptionTextArea;
  }

  private void updateDescriptionInModel()
  {
    JTextArea textField = getDescriptionTextArea();
    switch (language)
    {
    case de:
      model.setDescriptionDe(textField.getText());
      break;
    case en:
      model.setDescription(textField.getText());
      break;
    case es:
      model.setDescriptionEs(textField.getText());
      break;
    case fr:
      model.setDescriptionFr(textField.getText());
      break;
    case it:
      model.setDescriptionIt(textField.getText());
      break;
    default:
      break;
    }
  }

  private JScrollPane getDescriptionScrollPane()
  {
    if (descriptionScrollPane == null)
    {
      descriptionScrollPane = new JScrollPane();
      descriptionScrollPane.setBorder(null);
      descriptionScrollPane.setPreferredSize(new Dimension(290, 150));
      descriptionScrollPane.setViewportView(getDescriptionTextArea());
    }
    return descriptionScrollPane;
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
      getCharCountLabel().setIcon(warningIcon);
    }
    else
    {
      getCharCountLabel().setIcon(null);
    }
    getCharCountLabel().setText(length + " characters (recommended max: 512)");
    if (listener != null)
    {
      listener.actionPerformed(new ActionEvent(this, 0, Integer.toString(length)));
    }
  }

  public void addCharCountChangeListener(ActionListener listener)
  {
    this.listener = listener;
  }
}
