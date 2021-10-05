package se.lantz.gui.checkdescriptions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckDescrProgressPanel extends JPanel
{
  private static final Logger logger = LoggerFactory.getLogger(CheckDescrProgressPanel.class);
  private JProgressBar progressBar;
  private JTextArea textArea;
  private JScrollPane textScrollPane;
  private JButton closeButton;

  public CheckDescrProgressPanel()
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

  public void finish(int count, Exception e)
  {
    getCloseButton().setEnabled(true);
    getProgressBar().setIndeterminate(false);
    getProgressBar().setValue(getProgressBar().getMaximum());
    if (e != null)
    {
      logger.error("description check failed", e);
      getTextArea().append("\nDescription check failed: " + e.getMessage());
    }
    else if (count == 0)
    {
      getTextArea().append("\nNothing to fix, all up to date.");
    }
    else
    {
      getTextArea().append("\nFixed descriptions for " + count + " games successfully.");
    }
  }
}
