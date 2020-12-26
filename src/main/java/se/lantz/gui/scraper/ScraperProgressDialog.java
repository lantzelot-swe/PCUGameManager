package se.lantz.gui.scraper;

import java.awt.Frame;

import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;

public class ScraperProgressDialog extends JDialog
{
  private static final long serialVersionUID = 1L;
  private JProgressBar progressBar;
  private JLabel textLabel;

  public ScraperProgressDialog(Frame frame)
  {
    super(frame,"Scraping", true);
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

  public void updateProgress()
  {
    getTextLabel().setText("Fetching Screenshots...");
    this.repaint();
  }
  
  public void finish()
  {
    progressBar.setIndeterminate(false);
    getProgressBar().setValue( getProgressBar().getMaximum());
    dispose();
  }
  
  private JProgressBar getProgressBar() {
    if (progressBar == null) {
    	progressBar = new JProgressBar();
    	progressBar.setIndeterminate(true);
    }
    return progressBar;
  }
  private JLabel getTextLabel() {
    if (textLabel == null) {
    	textLabel = new JLabel("Fetching games information...");
    }
    return textLabel;
  }
}
