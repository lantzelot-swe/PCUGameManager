package se.lantz.gui.imports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ImportProgressPanel extends JPanel
{
  private JProgressBar progressBar;
  private JTextArea textArea;
  private JScrollPane textScrollPane;
  private JButton cancelButton;

  public ImportProgressPanel()
  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_progressBar = new GridBagConstraints();
    gbc_progressBar.weightx = 1.0;
    gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
    gbc_progressBar.insets = new Insets(10, 5, 5, 5);
    gbc_progressBar.gridx = 0;
    gbc_progressBar.gridy = 0;
    add(getProgressBar(), gbc_progressBar);
    GridBagConstraints gbc_textScrollPane = new GridBagConstraints();
    gbc_textScrollPane.insets = new Insets(0, 5, 5, 5);
    gbc_textScrollPane.weighty = 1.0;
    gbc_textScrollPane.weightx = 1.0;
    gbc_textScrollPane.fill = GridBagConstraints.BOTH;
    gbc_textScrollPane.gridx = 0;
    gbc_textScrollPane.gridy = 1;
    add(getTextScrollPane(), gbc_textScrollPane);
    GridBagConstraints gbc_cancelButton = new GridBagConstraints();
    gbc_cancelButton.insets = new Insets(0, 5, 5, 5);
    gbc_cancelButton.gridx = 0;
    gbc_cancelButton.gridy = 2;
    add(getCancelButton(), gbc_cancelButton);
  }

  private JProgressBar getProgressBar()
  {
    if (progressBar == null)
    {
      progressBar = new JProgressBar();
      progressBar.setIndeterminate(true);
      progressBar.setStringPainted(true);
    }
    return progressBar;
  }

  private JTextArea getTextArea()
  {
    if (textArea == null)
    {
      textArea = new JTextArea();
      textArea.setEditable(false);
    }
    return textArea;
  }

  private JScrollPane getTextScrollPane()
  {
    if (textScrollPane == null)
    {
      textScrollPane = new JScrollPane();
      textScrollPane.setViewportView(getTextArea());
    }
    return textScrollPane;
  }

  JButton getCancelButton()
  {
    if (cancelButton == null)
    {
      cancelButton = new JButton("Cancel");
    }
    return cancelButton;
  }
  
  void updateProgress(String infoText)
  {
    getTextArea().append(infoText);
  }
  
  void updateProgressBar(String valuestring, int maximum, int value)
  {
    getProgressBar().setString(valuestring);
    getProgressBar().setIndeterminate(false);
    getProgressBar().setMaximum(maximum);
    getProgressBar().setValue(value);
  }
  
  public void finish()
  {
    getProgressBar().setIndeterminate(false);
    getProgressBar().setValue(getProgressBar().getMaximum());
    //Check for errors
    String text = getTextArea().getText();
    int count = text.length() - text.replace("ERROR:", "").length();
    int ignoreCount = text.length() - text.replace("Ignoring", "").length();
    if (ignoreCount > 0)
    {
      getTextArea().append("\n" + ignoreCount/8 + " games ignored (missing or corrupted game files).\n");
    }
    if (count > 0)
    {
      getTextArea().append("\nImport ended with " + count/6 + " errors. See log file for details.");
    }
    else
    {
      getTextArea().append("\nImport completed.");
    }
  }
}
