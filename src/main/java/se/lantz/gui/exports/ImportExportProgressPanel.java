package se.lantz.gui.exports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import se.lantz.gui.exports.ImportExportProgressDialog.DIALOGTYPE;

public class ImportExportProgressPanel extends JPanel
{
  private JProgressBar progressBar;
  private JTextArea textArea;
  private JScrollPane textScrollPane;
  private JButton closeButton;

  public ImportExportProgressPanel()
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
    GridBagConstraints gbc_closeButton = new GridBagConstraints();
    gbc_closeButton.insets = new Insets(0, 5, 5, 5);
    gbc_closeButton.gridx = 0;
    gbc_closeButton.gridy = 2;
    add(getCloseButton(), gbc_closeButton);
  }

  private JProgressBar getProgressBar()
  {
    if (progressBar == null)
    {
      progressBar = new JProgressBar();
      progressBar.setIndeterminate(true);
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

  JButton getCloseButton()
  {
    if (closeButton == null)
    {
      closeButton = new JButton("Close");
      closeButton.setEnabled(false);
    }
    return closeButton;
  }
  
  void updateProgress(String infoText)
  {
    getTextArea().append(infoText);
  }
  
  public void finish(DIALOGTYPE type)
  {
    getCloseButton().setEnabled(true);
    getProgressBar().setIndeterminate(false);
    getProgressBar().setValue(getProgressBar().getMaximum());
    //Check for errors
    String text = getTextArea().getText();
    int count = text.length() - text.replace("ERROR:", "").length();
    switch (type)
    {
    case IMPORT:
    {
      getTextArea().append("\nImport ");
      break;
    }
    case EXPORT:
    {
      getTextArea().append("\nExport ");
      break;
    }
    case FIX:
    {
      getTextArea().append("\nFix ");
      break;
    }
    default:
      //Do nothing
    }

    if (count > 0)
    {
      getTextArea().append("ended with " + count/6 + " errors. See pcusb.log for details.");
    }
    else
    {
      getTextArea().append("completed successfully.");
    }
  }
}
