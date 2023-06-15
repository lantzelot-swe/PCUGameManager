package se.lantz.gui.scraper;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;

import se.lantz.manager.ScraperManager;
import se.lantz.model.InfoModel;
import se.lantz.model.data.ScraperFields;
import se.lantz.util.ExceptionHandler;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class MobyGamesOptionsPanel extends JPanel
{
  private JEditorPane infoEditorPane;
  private JTextField urlTextField;
  private JPanel fieldsPanel;
  private JLabel fieldsInfoLabel;
  private JCheckBox titleCheckBox;
  private JCheckBox authorCheckBox;
  private JCheckBox yearCheckBox;
  private JCheckBox descriptionCheckBox;
  private JCheckBox coverCheckBox;
  private JCheckBox screensCheckBox;
  private ScraperManager scraper;
  private JButton connectButton;
  private JLabel connectionStatusLabel;

  private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
  private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  private JButton okButton;
  private JCheckBox genreCheckBox;
  private JCheckBox composerCheckBox;
  private InfoModel model;
  private JPanel platformPanel;
  private JRadioButton c64RadioButton;
  private JRadioButton vic20RadioButton;
  private final ButtonGroup platformButtonGroup = new ButtonGroup();

  public MobyGamesOptionsPanel(ScraperManager scraper, JButton okButton, InfoModel model)
  {
    this.scraper = scraper;
    this.okButton = okButton;
    this.model = model;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0};
    gridBagLayout.columnWeights = new double[]{1.0, 0.0};
    setLayout(gridBagLayout);
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.gridwidth = 2;
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_infoLabel.insets = new Insets(10, 10, 0, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 0;
    add(getInfoEditorPane(), gbc_infoLabel);
    GridBagConstraints gbc_platformPanel = new GridBagConstraints();
    gbc_platformPanel.anchor = GridBagConstraints.NORTH;
    gbc_platformPanel.insets = new Insets(0, 0, 10, 0);
    gbc_platformPanel.weightx = 1.0;
    gbc_platformPanel.gridwidth = 2;
    gbc_platformPanel.fill = GridBagConstraints.BOTH;
    gbc_platformPanel.gridx = 0;
    gbc_platformPanel.gridy = 1;
    add(getPlatformPanel(), gbc_platformPanel);
    GridBagConstraints gbc_urlTextField = new GridBagConstraints();
    gbc_urlTextField.weightx = 1.0;
    gbc_urlTextField.insets = new Insets(0, 10, 10, 5);
    gbc_urlTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_urlTextField.gridx = 0;
    gbc_urlTextField.gridy = 2;
    add(getUrlTextField(), gbc_urlTextField);
    GridBagConstraints gbc_connectButton = new GridBagConstraints();
    gbc_connectButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_connectButton.insets = new Insets(0, 0, 10, 10);
    gbc_connectButton.gridx = 1;
    gbc_connectButton.gridy = 2;
    add(getConnectButton(), gbc_connectButton);
    GridBagConstraints gbc_connectionStatusLabel = new GridBagConstraints();
    gbc_connectionStatusLabel.gridwidth = 2;
    gbc_connectionStatusLabel.insets = new Insets(0, 10, 10, 0);
    gbc_connectionStatusLabel.gridx = 0;
    gbc_connectionStatusLabel.gridy = 3;
    add(getConnectionStatusLabel(), gbc_connectionStatusLabel);
    GridBagConstraints gbc_fieldsPanel = new GridBagConstraints();
    gbc_fieldsPanel.insets = new Insets(0, 0, 10, 0);
    gbc_fieldsPanel.gridwidth = 2;
    gbc_fieldsPanel.anchor = GridBagConstraints.NORTH;
    gbc_fieldsPanel.weighty = 1.0;
    gbc_fieldsPanel.weightx = 1.0;
    gbc_fieldsPanel.gridx = 0;
    gbc_fieldsPanel.gridy = 4;
    add(getFieldsPanel(), gbc_fieldsPanel);
    okButton.setEnabled(false);
  }

  private JEditorPane getInfoEditorPane()
  {
    if (infoEditorPane == null)
    {
      String info = "<html>To scrape information from mobygames.com you need to specify the URL for a specific game." +
        "<ol><li>Go to <a href='https:/www.mobygames.com/'>https:/www.mobygames.com/</a> and search for the game you want to<br>scrape information for.</li>" +
        "<li>Go to the game and copy the URL to the field below.<br>" +
        "(Example: https://www.mobygames.com/game/1087/arkanoid/)</li>" +
        "<li>Choose the platform you want to scrape for below:</li></ol></html>";

      infoEditorPane = new JEditorPane("text/html", info);
      infoEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
      infoEditorPane.setFont(UIManager.getDefaults().getFont("Label.font"));
      infoEditorPane.setEditable(false);
      infoEditorPane.setOpaque(false);
      infoEditorPane.addHyperlinkListener((hle) -> {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()) && Desktop.isDesktopSupported() &&
          Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
        {
          try
          {
            Desktop.getDesktop().browse(hle.getURL().toURI());
          }
          catch (IOException | URISyntaxException e)
          {
            ExceptionHandler.handleException(e, "Could not open default browser");
          }
        }
      });
    }
    return infoEditorPane;
  }

  JTextField getUrlTextField()
  {
    if (urlTextField == null)
    {
      urlTextField = new JTextField();
      urlTextField.addFocusListener(new FocusListener() {

        @Override
        public void focusGained(FocusEvent e) {
          urlTextField.select(0, urlTextField.getText().length());
        }

        @Override
        public void focusLost(FocusEvent e) {
          urlTextField.select(0, 0);
        }
        });

      urlTextField.setText("https://www.mobygames.com/game/");
      urlTextField.addKeyListener(new KeyAdapter()
      {
        @Override
        public void keyPressed(KeyEvent e)
        {
          if (e.getKeyCode() == KeyEvent.VK_ENTER)
          {
            getConnectButton().doClick();
          }
        }
      });
    }
    return urlTextField;
  }

  private JPanel getFieldsPanel()
  {
    if (fieldsPanel == null)
    {
      fieldsPanel = new JPanel();
      GridBagLayout gbl_fieldsPanel = new GridBagLayout();
      fieldsPanel.setLayout(gbl_fieldsPanel);
      GridBagConstraints gbc_fieldsInfoLabel = new GridBagConstraints();
      gbc_fieldsInfoLabel.gridwidth = 3;
      gbc_fieldsInfoLabel.weightx = 1.0;
      gbc_fieldsInfoLabel.insets = new Insets(0, 0, 5, 0);
      gbc_fieldsInfoLabel.anchor = GridBagConstraints.NORTH;
      gbc_fieldsInfoLabel.gridx = 0;
      gbc_fieldsInfoLabel.gridy = 0;
      fieldsPanel.add(getFieldsInfoLabel(), gbc_fieldsInfoLabel);
      GridBagConstraints gbc_titleCheckBox = new GridBagConstraints();
      gbc_titleCheckBox.insets = new Insets(0, 0, 5, 5);
      gbc_titleCheckBox.anchor = GridBagConstraints.WEST;
      gbc_titleCheckBox.gridx = 0;
      gbc_titleCheckBox.gridy = 1;
      fieldsPanel.add(getTitleCheckBox(), gbc_titleCheckBox);
      GridBagConstraints gbc_authorCheckBox = new GridBagConstraints();
      gbc_authorCheckBox.insets = new Insets(0, 0, 5, 5);
      gbc_authorCheckBox.anchor = GridBagConstraints.WEST;
      gbc_authorCheckBox.gridx = 0;
      gbc_authorCheckBox.gridy = 2;
      fieldsPanel.add(getAuthorCheckBox(), gbc_authorCheckBox);
      GridBagConstraints gbc_composerCheckBox = new GridBagConstraints();
      gbc_composerCheckBox.anchor = GridBagConstraints.WEST;
      gbc_composerCheckBox.insets = new Insets(0, 0, 5, 5);
      gbc_composerCheckBox.gridx = 1;
      gbc_composerCheckBox.gridy = 2;
      fieldsPanel.add(getComposerCheckBox(), gbc_composerCheckBox);
      GridBagConstraints gbc_coverCheckBox = new GridBagConstraints();
      gbc_coverCheckBox.anchor = GridBagConstraints.WEST;
      gbc_coverCheckBox.insets = new Insets(0, 0, 5, 5);
      gbc_coverCheckBox.gridx = 1;
      gbc_coverCheckBox.gridy = 3;
      fieldsPanel.add(getCoverCheckBox(), gbc_coverCheckBox);
      GridBagConstraints gbc_yearCheckBox = new GridBagConstraints();
      gbc_yearCheckBox.insets = new Insets(0, 0, 5, 5);
      gbc_yearCheckBox.anchor = GridBagConstraints.WEST;
      gbc_yearCheckBox.gridx = 0;
      gbc_yearCheckBox.gridy = 3;
      fieldsPanel.add(getYearCheckBox(), gbc_yearCheckBox);
      GridBagConstraints gbc_descriptionCheckBox = new GridBagConstraints();
      gbc_descriptionCheckBox.insets = new Insets(0, 0, 5, 5);
      gbc_descriptionCheckBox.anchor = GridBagConstraints.WEST;
      gbc_descriptionCheckBox.gridx = 1;
      gbc_descriptionCheckBox.gridy = 1;
      fieldsPanel.add(getDescriptionCheckBox(), gbc_descriptionCheckBox);
      GridBagConstraints gbc_genreCheckBox = new GridBagConstraints();
      gbc_genreCheckBox.insets = new Insets(0, 0, 0, 5);
      gbc_genreCheckBox.anchor = GridBagConstraints.WEST;
      gbc_genreCheckBox.gridx = 0;
      gbc_genreCheckBox.gridy = 4;
      fieldsPanel.add(getGenreCheckBox(), gbc_genreCheckBox);
      GridBagConstraints gbc_screensCheckBox = new GridBagConstraints();
      gbc_screensCheckBox.insets = new Insets(0, 0, 0, 5);
      gbc_screensCheckBox.anchor = GridBagConstraints.WEST;
      gbc_screensCheckBox.gridx = 1;
      gbc_screensCheckBox.gridy = 4;
      fieldsPanel.add(getScreensCheckBox(), gbc_screensCheckBox);
    }
    return fieldsPanel;
  }

  private JLabel getFieldsInfoLabel()
  {
    if (fieldsInfoLabel == null)
    {
      fieldsInfoLabel = new JLabel("Select fields to scrape:");
    }
    return fieldsInfoLabel;
  }

  private JCheckBox getTitleCheckBox()
  {
    if (titleCheckBox == null)
    {
      titleCheckBox = new JCheckBox("Title");
      titleCheckBox.setSelected(true);
      titleCheckBox.setEnabled(false);
    }
    return titleCheckBox;
  }

  private JCheckBox getAuthorCheckBox()
  {
    if (authorCheckBox == null)
    {
      authorCheckBox = new JCheckBox("Author");
      authorCheckBox.setSelected(true);
      authorCheckBox.setEnabled(false);
    }
    return authorCheckBox;
  }

  private JCheckBox getYearCheckBox()
  {
    if (yearCheckBox == null)
    {
      yearCheckBox = new JCheckBox("Year");
      yearCheckBox.setSelected(true);
      yearCheckBox.setEnabled(false);
    }
    return yearCheckBox;
  }

  private JCheckBox getDescriptionCheckBox()
  {
    if (descriptionCheckBox == null)
    {
      descriptionCheckBox = new JCheckBox("Description");
      descriptionCheckBox.setSelected(true);
      descriptionCheckBox.setEnabled(false);
    }
    return descriptionCheckBox;
  }

  private JCheckBox getCoverCheckBox()
  {
    if (coverCheckBox == null)
    {
      coverCheckBox = new JCheckBox("Cover");
      coverCheckBox.setSelected(true);
      coverCheckBox.setEnabled(false);
    }
    return coverCheckBox;
  }

  private JCheckBox getScreensCheckBox()
  {
    if (screensCheckBox == null)
    {
      screensCheckBox = new JCheckBox("Screenshots");
      screensCheckBox.setSelected(true);
      screensCheckBox.setEnabled(false);
    }
    return screensCheckBox;
  }

  private void enableCheckBoxes(boolean enable)
  {
    titleCheckBox.setEnabled(enable);
    authorCheckBox.setEnabled(enable);
    yearCheckBox.setEnabled(enable);
    descriptionCheckBox.setEnabled(enable);
    coverCheckBox.setEnabled(enable);
    screensCheckBox.setEnabled(enable);
    genreCheckBox.setEnabled(enable);
    composerCheckBox.setEnabled(enable);
  }

  public ScraperFields getScraperFields()
  {
    ScraperFields returnValue = new ScraperFields();
    returnValue.setTitle(titleCheckBox.isSelected());
    returnValue.setAuthor(authorCheckBox.isSelected());
    returnValue.setYear(yearCheckBox.isSelected());
    returnValue.setGenre(genreCheckBox.isSelected());
    returnValue.setDescription(descriptionCheckBox.isSelected());
    returnValue.setCover(coverCheckBox.isSelected());
    returnValue.setScreenshots(screensCheckBox.isSelected());
    returnValue.setComposer(composerCheckBox.isSelected());
    returnValue.setPlatform(c64RadioButton.isSelected());
    return returnValue;
  }

  JButton getConnectButton()
  {
    if (connectButton == null)
    {
      connectButton = new JButton("Connect");
      connectButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            try
            {
              MobyGamesOptionsPanel.this.setCursor(waitCursor);
              scraper.connectScraper(urlTextField.getText());
              getConnectionStatusLabel().setText("Connection status: OK");
              getConnectionStatusLabel().setIcon(new ImageIcon(this.getClass().getResource("/se/lantz/check.png")));
              enableCheckBoxes(true);
              okButton.setEnabled(true);
              MobyGamesOptionsPanel.this.setCursor(defaultCursor);
              SwingUtilities.invokeLater(() -> SwingUtilities.getRootPane(connectButton).setDefaultButton(okButton));
            }
            catch (Exception e2)
            {
              getConnectionStatusLabel().setText("Connection status: Error. Invalid URL?");
              getConnectionStatusLabel().setIcon(new ImageIcon(this.getClass().getResource("/se/lantz/failure.png")));
              enableCheckBoxes(false);
              okButton.setEnabled(false);
              MobyGamesOptionsPanel.this.setCursor(defaultCursor);
              SwingUtilities.invokeLater(() -> SwingUtilities.getRootPane(connectButton).setDefaultButton(connectButton));
            }
          }
        });
    }
    return connectButton;
  }

  private JLabel getConnectionStatusLabel()
  {
    if (connectionStatusLabel == null)
    {
      connectionStatusLabel = new JLabel(" ");
      connectionStatusLabel.setFont(connectionStatusLabel.getFont().deriveFont(Font.BOLD));
      connectionStatusLabel.setHorizontalTextPosition(SwingConstants.LEADING);
    }
    return connectionStatusLabel;
  }

  private JCheckBox getGenreCheckBox()
  {
    if (genreCheckBox == null)
    {
      genreCheckBox = new JCheckBox("Genre");
      genreCheckBox.setSelected(true);
      genreCheckBox.setEnabled(false);
    }
    return genreCheckBox;
  }

  private JCheckBox getComposerCheckBox()
  {
    if (composerCheckBox == null)
    {
      composerCheckBox = new JCheckBox("Composer");
      composerCheckBox.setSelected(true);
      composerCheckBox.setEnabled(false);
    }
    return composerCheckBox;
  }
  
  public void preSelectFields()
  {
    getTitleCheckBox().setSelected(model.getTitle().isEmpty());
    getAuthorCheckBox().setSelected(model.getAuthor().isEmpty());
    getYearCheckBox().setSelected(model.getYear() == 1986 && model.isNewGame());
    getGenreCheckBox().setSelected(model.getGenre().isEmpty());
    getDescriptionCheckBox().setSelected(model.getDescription().isEmpty());
    getComposerCheckBox().setSelected(model.getComposer().isEmpty());
    getCoverCheckBox().setSelected(model.getCoverFile().isEmpty() && model.getCoverImage() == null);
    getScreensCheckBox().setSelected((model.getScreens1File().isEmpty() && model.getScreen1Image() == null) || (model.getScreens1File().isEmpty() && model.getScreen1Image() == null));
  }
  private JPanel getPlatformPanel() {
    if (platformPanel == null) {
    	platformPanel = new JPanel();
    	GridBagLayout gbl_platformPanel = new GridBagLayout();
    	gbl_platformPanel.columnWidths = new int[]{0, 0};
    	gbl_platformPanel.rowHeights = new int[]{0, 0, 0};
    	gbl_platformPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
    	gbl_platformPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
    	platformPanel.setLayout(gbl_platformPanel);
    	GridBagConstraints gbc_c64RadioButton = new GridBagConstraints();
    	gbc_c64RadioButton.weightx = 1.0;
    	gbc_c64RadioButton.gridx = 0;
    	gbc_c64RadioButton.gridy = 0;
    	platformPanel.add(getC64RadioButton(), gbc_c64RadioButton);
    	GridBagConstraints gbc_vic20RadioButton = new GridBagConstraints();
    	gbc_vic20RadioButton.weightx = 1.0;
    	gbc_vic20RadioButton.gridx = 0;
    	gbc_vic20RadioButton.gridy = 1;
    	platformPanel.add(getVic20RadioButton(), gbc_vic20RadioButton);
    }
    return platformPanel;
  }

  private JRadioButton getC64RadioButton()
  {
    if (c64RadioButton == null)
    {
    	c64RadioButton = new JRadioButton("C64");
    	platformButtonGroup.add(c64RadioButton);
    	c64RadioButton.setSelected(true);
    }
    return c64RadioButton;
  }
  private JRadioButton getVic20RadioButton() {
    if (vic20RadioButton == null) {
    	vic20RadioButton = new JRadioButton("VIC-20");
    	platformButtonGroup.add(vic20RadioButton);
    }
    return vic20RadioButton;
  }
}
