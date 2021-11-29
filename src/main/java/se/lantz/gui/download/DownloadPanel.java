package se.lantz.gui.download;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JProgressBar;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;

public class DownloadPanel extends JPanel
{
  private JProgressBar progressBar;
  private JLabel infoLabel;
  public DownloadPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_progressBar = new GridBagConstraints();
    gbc_progressBar.insets = new Insets(10, 10, 5, 10);
    gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
    gbc_progressBar.weightx = 1.0;
    gbc_progressBar.gridx = 0;
    gbc_progressBar.gridy = 0;
    add(getProgressBar(), gbc_progressBar);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.insets = new Insets(5, 10, 10, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 1;
    add(getInfoLabel(), gbc_infoLabel);
  }

  protected JProgressBar getProgressBar() {
    if (progressBar == null) {
    	progressBar = new JProgressBar();
    	progressBar.setStringPainted(true);
    }
    return progressBar;
  }
  private JLabel getInfoLabel() {
    if (infoLabel == null) {
    	infoLabel = new JLabel("New label");
    }
    return infoLabel;
  }
}
