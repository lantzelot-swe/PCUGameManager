package se.lantz.gui.translation;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class TranslationProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;
  private JProgressBar progressBar;
  private JLabel textLabel;

  public TranslationProgressDialog(Frame frame)
  {
    super(frame, "Translating", true);
    setAlwaysOnTop(true);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setResizable(false);
    GridBagLayout gridBagLayout = new GridBagLayout();
    getContentPane().setLayout(gridBagLayout);
    GridBagConstraints gbc_progressBar = new GridBagConstraints();
    gbc_progressBar.weightx = 1.0;
    gbc_progressBar.anchor = GridBagConstraints.NORTH;
    gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
    gbc_progressBar.insets = new Insets(5, 5, 5, 5);
    gbc_progressBar.gridx = 0;
    gbc_progressBar.gridy = 0;
    getContentPane().add(getProgressBar(), gbc_progressBar);
    GridBagConstraints gbc_textLabel = new GridBagConstraints();
    gbc_textLabel.insets = new Insets(5, 5, 5, 5);
    gbc_textLabel.anchor = GridBagConstraints.NORTH;
    gbc_textLabel.weightx = 1.0;
    gbc_textLabel.weighty = 1.0;
    gbc_textLabel.gridx = 0;
    gbc_textLabel.gridy = 1;
    getContentPane().add(getTextLabel(), gbc_textLabel);
  }

  public void updateProgress(String lang)
  {
    String language = "";
    switch (lang)
    {
    case "en":
      language = "English";
      break;
    case "de":
      language = "German";
      break;
    case "fr":
      language = "French";
      break;
    case "es":
      language = "Spanish";
      break;
    case "it":
      language = "Italian";
      break;
    default:
      language = lang;
      break;
    }
    getTextLabel().setText("Translating description to " + language + "...");
    this.repaint();
  }

  public void finish()
  {
    progressBar.setIndeterminate(false);
    getProgressBar().setValue(getProgressBar().getMaximum());
    dispose();
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

  private JLabel getTextLabel()
  {
    if (textLabel == null)
    {
      textLabel = new JLabel("Translating description to English......");
    }
    return textLabel;
  }
}
