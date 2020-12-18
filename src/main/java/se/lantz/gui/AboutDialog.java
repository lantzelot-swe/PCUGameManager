package se.lantz.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import se.lantz.util.FileManager;
import java.awt.Font;

public class AboutDialog extends JDialog
{
  private JLabel imageLabel;
  private JLabel titleLabel;
  private JLabel versionLabel;
  private JLabel programmedLabel;
  private JLabel thanksTo1Label;
  private JLabel thanks2Label;

  KeyStroke escKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

  Action escAction = new AbstractAction()
    {
      private static final long serialVersionUID = 1756396251349970052L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        setVisible(false);
      }
    };
  private JButton closeButton;
  private JLabel jreVersionLabel;

  public AboutDialog()
  {
    super(MainWindow.getInstance(), "About", true);
    GridBagLayout gridBagLayout = new GridBagLayout();
    getContentPane().setLayout(gridBagLayout);
    GridBagConstraints gbc_imageLabel = new GridBagConstraints();
    gbc_imageLabel.weightx = 1.0;
    gbc_imageLabel.insets = new Insets(0, 0, 5, 0);
    gbc_imageLabel.gridx = 0;
    gbc_imageLabel.gridy = 0;
    getContentPane().add(getImageLabel(), gbc_imageLabel);
    GridBagConstraints gbc_titleLabel = new GridBagConstraints();
    gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
    gbc_titleLabel.gridx = 0;
    gbc_titleLabel.gridy = 1;
    getContentPane().add(getTitleLabel(), gbc_titleLabel);
    GridBagConstraints gbc_versionLabel = new GridBagConstraints();
    gbc_versionLabel.insets = new Insets(0, 0, 5, 0);
    gbc_versionLabel.gridx = 0;
    gbc_versionLabel.gridy = 2;
    getContentPane().add(getVersionLabel(), gbc_versionLabel);
    GridBagConstraints gbc_jreVersionLabel = new GridBagConstraints();
    gbc_jreVersionLabel.insets = new Insets(0, 0, 5, 0);
    gbc_jreVersionLabel.gridx = 0;
    gbc_jreVersionLabel.gridy = 3;
    getContentPane().add(getJreVersionLabel(), gbc_jreVersionLabel);
    GridBagConstraints gbc_programmedLabel = new GridBagConstraints();
    gbc_programmedLabel.insets = new Insets(0, 0, 5, 0);
    gbc_programmedLabel.gridx = 0;
    gbc_programmedLabel.gridy = 4;
    getContentPane().add(getProgrammedLabel(), gbc_programmedLabel);
    GridBagConstraints gbc_thanksTo1Label = new GridBagConstraints();
    gbc_thanksTo1Label.insets = new Insets(10, 0, 5, 0);
    gbc_thanksTo1Label.gridx = 0;
    gbc_thanksTo1Label.gridy = 5;
    getContentPane().add(getThanksTo1Label(), gbc_thanksTo1Label);
    GridBagConstraints gbc_closeButton = new GridBagConstraints();
    gbc_closeButton.anchor = GridBagConstraints.SOUTH;
    gbc_closeButton.insets = new Insets(5, 5, 10, 5);
    gbc_closeButton.gridx = 0;
    gbc_closeButton.gridy = 7;
    getContentPane().add(getCloseButton(), gbc_closeButton);
    GridBagConstraints gbc_thanks2Label = new GridBagConstraints();
    gbc_thanks2Label.insets = new Insets(0, 0, 5, 0);
    gbc_thanks2Label.anchor = GridBagConstraints.NORTH;
    gbc_thanks2Label.weighty = 1.0;
    gbc_thanks2Label.gridx = 0;
    gbc_thanks2Label.gridy = 6;
    getContentPane().add(getThanks2Label(), gbc_thanks2Label);

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    //Register esc as closing the window
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escKeyStroke, "closeDialog");
    getRootPane().getActionMap().put("closeDialog", escAction);
    this.setResizable(false);
  }

  private JLabel getImageLabel()
  {
    if (imageLabel == null)
    {
      imageLabel = new JLabel("");
      imageLabel.setIcon(new ImageIcon(getClass().getResource("/se/lantz/PCUGameManager.png")));
    }
    return imageLabel;
  }

  private JLabel getTitleLabel()
  {
    if (titleLabel == null)
    {
      titleLabel = new JLabel("Project Carousel USB Game Manager");
    }
    return titleLabel;
  }

  private JLabel getVersionLabel()
  {
    if (versionLabel == null)
    {
      versionLabel = new JLabel("Version: " + FileManager.getPcuVersionFromManifest());
      versionLabel.setFont(new Font("Tahoma", Font.BOLD, 11));

    }
    return versionLabel;
  }

  private JLabel getProgrammedLabel()
  {
    if (programmedLabel == null)
    {
      programmedLabel = new JLabel("Coded by Lantzelot");
    }
    return programmedLabel;
  }

  private JLabel getThanksTo1Label()
  {
    if (thanksTo1Label == null)
    {
      thanksTo1Label =
        new JLabel("Thanks to Spannernick for the Project Carousel USB initiative and idea for this application.");
    }
    return thanksTo1Label;
  }

  private JLabel getThanks2Label()
  {
    if (thanks2Label == null)
    {
      thanks2Label = new JLabel("");
    }
    return thanks2Label;
  }

  private JButton getCloseButton()
  {
    if (closeButton == null)
    {
      closeButton = new JButton("Close");
      closeButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            setVisible(false);
          }
        });
    }
    return closeButton;
  }
  private JLabel getJreVersionLabel() {
    if (jreVersionLabel == null) {
    	jreVersionLabel = new JLabel("JRE version:" + System.getProperty("java.runtime.version"));
    }
    return jreVersionLabel;
  }
}
