package se.lantz.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.menu.InsetsMenuItem;
import se.lantz.gui.screenshot.EditScreenshotDialog;
import se.lantz.gui.screenshot.cover.EditCoverDialog;
import se.lantz.model.InfoModel;
import se.lantz.model.MainViewModel;
import se.lantz.util.CustomUndoPlainDocument;
import se.lantz.util.FileDrop;
import se.lantz.util.FileManager;

public class ScreenshotsPanel extends JPanel
{
  private static final Logger logger = LoggerFactory.getLogger(ScreenshotsPanel.class);
  private static final String GAME_DIR_PROPERTY = "gamesDir";
  private static final String COVER_DIR_PROPERTY = "coversDir";
  private static final String SCREENS_DIR_PROPERTY = "screensDir";
  private JPanel coverPanel;
  private JLabel coverImageLabel;
  private JButton changeCoverButton;
  private JPanel screenshotPanel;
  private JLabel screen1ImageLabel;
  private JButton screen1Button;
  private JLabel screen2ImageLabel;
  private JButton screen2Button;
  private JPanel gamePanel;
  private JLabel gameLabel;
  private JTextField gameTextField;
  private JButton gameButton;
  private InfoModel infomodel;

  private String currentCoverFile = "";
  private String currentScreen1File = "";
  private String currentScreen2File = "";
  private BufferedImage currentCoverImage = null;
  private BufferedImage currentScreen1Image = null;
  private BufferedImage currentScreen2Image = null;

  private ImageIcon missingSceenshotIcon = null;
  private ImageIcon missingC64CoverIcon = null;
  private ImageIcon missingVic20CoverIcon = null;
  private JButton edit1Button;
  private JButton edit2Button;

  private ImageIcon warningIcon = new ImageIcon(getClass().getResource("/se/lantz/warning-icon.png"));
  private String editTooltip =
    "<html>Optimal resolution for the carousel is 320x200.<br>Press to edit the image.<br>It will be resized when the game is saved but it might make it blurry.</html>";
  private boolean gamesFileUpdated = false;

  FileNameExtensionFilter imagefilter =
    new FileNameExtensionFilter("png, gif, jpeg, bmp", "png", "gif", "jpg", "jpeg", "bmp");
  private JLabel resolution1Label;
  private JLabel resolution2Label;
  private MainViewModel model;
  private JLabel viewTagLabel;
  private JTextField viewTagTextField;

  public ScreenshotsPanel(MainViewModel model)
  {
    this.model = model;
    this.infomodel = model.getInfoModel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0 };
    gridBagLayout.rowWeights = new double[] { 0.0, 1.0 };
    setLayout(gridBagLayout);
    GridBagConstraints gbc_coverPanel = new GridBagConstraints();
    gbc_coverPanel.fill = GridBagConstraints.BOTH;
    gbc_coverPanel.insets = new Insets(5, 0, 0, 0);
    gbc_coverPanel.gridx = 1;
    gbc_coverPanel.gridy = 0;
    add(getCoverPanel(), gbc_coverPanel);
    GridBagConstraints gbc_screenshotPanel = new GridBagConstraints();
    gbc_screenshotPanel.insets = new Insets(5, 0, 0, 5);
    gbc_screenshotPanel.anchor = GridBagConstraints.WEST;
    gbc_screenshotPanel.fill = GridBagConstraints.VERTICAL;
    gbc_screenshotPanel.gridx = 2;
    gbc_screenshotPanel.gridy = 0;
    add(getScreenshotPanel(), gbc_screenshotPanel);
    GridBagConstraints gbc_gamePanel = new GridBagConstraints();
    gbc_gamePanel.anchor = GridBagConstraints.NORTH;
    gbc_gamePanel.gridwidth = 2;
    gbc_gamePanel.insets = new Insets(0, 0, 0, 5);
    gbc_gamePanel.fill = GridBagConstraints.BOTH;
    gbc_gamePanel.gridx = 1;
    gbc_gamePanel.gridy = 1;
    add(getGamePanel(), gbc_gamePanel);
    if (!Beans.isDesignTime())
    {
      infomodel.addPropertyChangeListener(e -> modelChanged());
      //React on changes to system, C64 or Vic-20
      model.getSystemModel().addPropertyChangeListener("c64", e -> systemChanged(e));
      //React on when a new game is selected
      model.addPropertyChangeListener("gameSelected", e -> resetWhenSavedOrNewGameSelected());
    }
  }

  public void resetWhenSavedOrNewGameSelected()
  {
    currentCoverFile = "";
    currentScreen1File = "";
    currentScreen2File = "";
    currentCoverImage = null;
    currentScreen1Image = null;
    currentScreen2Image = null;
    // Trigger a reload from the model
    modelChanged();
  }

  private void modelChanged()
  {
    if (!infomodel.isDataChanged())
    {
      gamesFileUpdated = false;
    }
    // Read from model
    getGameTextField().setText(getGameFileName());
    getViewTagTextField().setText(infomodel.getViewTag());
    reloadScreens();
  }

  private void systemChanged(PropertyChangeEvent e)
  {
    //Update cover info slot when the system type changes, but only for custom views and all games (id > -2)
    if (infomodel.isInfoSlot() && model.getSelectedGameView().getGameViewId() > -2)
    {
      infomodel
        .setCoverImage((boolean) e.getNewValue() ? FileManager.infoSlotC64Cover : FileManager.infoSlotVic20Cover);
    }
  }

  private String getGameFileName()
  {
    String returnValue = infomodel.getGamesFile();
    if (gamesFileUpdated)
    {
      returnValue = returnValue + " (updated)";
    }
    return returnValue;
  }

  private void reloadScreens()
  {
    BufferedImage coverImage = infomodel.getCoverImage();
    if (coverImage != null)
    {
      if (!coverImage.equals(currentCoverImage))
      {
        logger.debug("SETTING NEW COVER IMAGE");
        Image newImage = coverImage.getScaledInstance(130, 200, Image.SCALE_SMOOTH);
        getCoverImageLabel().setIcon(new ImageIcon(newImage));
        currentCoverImage = coverImage;
      }
    }
    else
    {
      String modelCoverFile = infomodel.getCoverFile();
      if (modelCoverFile.isEmpty())
      {
        currentCoverFile = "";
        getCoverImageLabel().setIcon(getDefaultCoverImageIcon());
      }
      else if (!modelCoverFile.equals(currentCoverFile))
      {
        loadCover(modelCoverFile);
        currentCoverFile = modelCoverFile;
      }
    }

    // Screen 1
    BufferedImage screen1Image = infomodel.getScreen1Image();
    if (screen1Image != null)
    {
      if (!screen1Image.equals(currentScreen1Image))
      {
        logger.debug("SETTING SCREEN 1 IMAGE");
        getScreen1ImageLabel().setIcon(new ImageIcon(FileManager.scaleImageTo320x200x32bit(screen1Image)));
        setEditButtonVisibilityAndResolution(screen1Image, getResolution1Label(), getEdit1Button());
        currentScreen1Image = screen1Image;
      }
    }
    else
    {
      String modelScreen1File = infomodel.getScreens1File();
      if (modelScreen1File.isEmpty())
      {
        currentScreen1File = "";
        getScreen1ImageLabel().setIcon(getMissingScreenshotImageIcon());
      }
      else if (!infomodel.getScreens1File().equals(currentScreen1File))
      {
        currentScreen1Image =
          loadScreen(modelScreen1File, getScreen1ImageLabel(), getResolution1Label(), getEdit1Button());
        if (currentScreen1Image != null)
        {
          setEditButtonVisibilityAndResolution(currentScreen1Image, getResolution1Label(), getEdit1Button());
        }
        currentScreen1File = modelScreen1File;
      }
    }
    // Screen 2
    BufferedImage screen2Image = infomodel.getScreen2Image();
    if (screen2Image != null)
    {
      if (!screen2Image.equals(currentScreen2Image))
      {
        logger.debug("SETTING SCREEN 2 IMAGE");
        getScreen2ImageLabel().setIcon(new ImageIcon(FileManager.scaleImageTo320x200x32bit(screen2Image)));
        setEditButtonVisibilityAndResolution(screen2Image, getResolution2Label(), getEdit2Button());
        currentScreen2Image = screen2Image;
      }
    }
    else
    {
      String modelScreen2File = infomodel.getScreens2File();
      if (modelScreen2File.isEmpty())
      {
        currentScreen2File = "";
        getScreen2ImageLabel().setIcon(getMissingScreenshotImageIcon());
      }
      else if (!modelScreen2File.equals(currentScreen2File))
      {
        currentScreen2Image =
          loadScreen(modelScreen2File, getScreen2ImageLabel(), getResolution2Label(), getEdit2Button());
        if (currentScreen2Image != null)
        {
          setEditButtonVisibilityAndResolution(currentScreen2Image, getResolution2Label(), getEdit2Button());
        }
        currentScreen2File = modelScreen2File;
      }
    }
  }

  private void setEditButtonVisibilityAndResolution(BufferedImage image, JLabel resolutionLabel, JButton editButton)
  {
    resolutionLabel.setText(image.getWidth() + "x" + image.getHeight());
    setEditButtonVisibility(image, editButton);
  }

  private void loadCover(String filename)
  {
    if (!filename.isEmpty())
    {
      File imagefile = new File(FileManager.COVERS + filename);
      try
      {
        BufferedImage image = ImageIO.read(imagefile);
        Image newImage = image.getScaledInstance(130, 200, Image.SCALE_SMOOTH);
        getCoverImageLabel().setIcon(new ImageIcon(newImage));
      }
      catch (IOException e)
      {
        logger.error("can't read file: " + filename, e);
        getCoverImageLabel().setIcon(getDefaultCoverImageIcon());
      }
    }
    else
    {
      getCoverImageLabel().setIcon(getDefaultCoverImageIcon());
    }
  }

  private BufferedImage loadScreen(String filename, JLabel screenLabel, JLabel resolutionLabel, JButton editButton)
  {
    BufferedImage image = null;
    if (!filename.isEmpty())
    {
      File imagefile = new File(FileManager.SCREENS + filename);
      try
      {
        image = ImageIO.read(imagefile);
        Image newImage = image.getScaledInstance(320, 200, Image.SCALE_SMOOTH);
        screenLabel.setIcon(new ImageIcon(newImage));
        setEditButtonVisibilityAndResolution(image, resolutionLabel, editButton);
      }
      catch (IOException e)
      {
        logger.error("can't read file: " + filename, e);
        screenLabel.setIcon(getMissingScreenshotImageIcon());
      }
    }
    else
    {
      screenLabel.setIcon(getMissingScreenshotImageIcon());
    }
    return image;
  }

  private ImageIcon getMissingScreenshotImageIcon()
  {
    if (missingSceenshotIcon == null)
    {
      missingSceenshotIcon = new ImageIcon(getClass().getResource("/se/lantz/DropScreenshot.png"));
    }
    return missingSceenshotIcon;
  }

  private ImageIcon getDefaultCoverImageIcon()
  {
    if (missingC64CoverIcon == null)
    {
      Image newImage = FileManager.emptyC64Cover.getScaledInstance(130, 200, Image.SCALE_SMOOTH);
      missingC64CoverIcon = new ImageIcon(newImage);
      newImage = FileManager.emptyVic20Cover.getScaledInstance(130, 200, Image.SCALE_SMOOTH);
      missingVic20CoverIcon = new ImageIcon(newImage);
    }
    return model.getSystemModel().isC64() ? missingC64CoverIcon : missingVic20CoverIcon;
  }

  private JPanel getCoverPanel()
  {
    if (coverPanel == null)
    {
      coverPanel = new JPanel();
      coverPanel.setMinimumSize(new Dimension(154, 255));
      coverPanel.setPreferredSize(new Dimension(154, 255));
      coverPanel.setBorder(new TitledBorder(null, "Cover", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_coverPanel = new GridBagLayout();
      coverPanel.setLayout(gbl_coverPanel);
      GridBagConstraints gbc_CoverImageLabel = new GridBagConstraints();
      gbc_CoverImageLabel.weighty = 1.0;
      gbc_CoverImageLabel.weightx = 1.0;
      gbc_CoverImageLabel.anchor = GridBagConstraints.WEST;
      gbc_CoverImageLabel.insets = new Insets(0, 5, 0, 5);
      gbc_CoverImageLabel.gridx = 0;
      gbc_CoverImageLabel.gridy = 1;
      coverPanel.add(getCoverImageLabel(), gbc_CoverImageLabel);
      GridBagConstraints gbc_changeCoverButton = new GridBagConstraints();
      gbc_changeCoverButton.weightx = 1.0;
      gbc_changeCoverButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_changeCoverButton.insets = new Insets(0, 5, 0, 5);
      gbc_changeCoverButton.gridx = 0;
      gbc_changeCoverButton.gridy = 2;
      coverPanel.add(getChangeCoverButton(), gbc_changeCoverButton);
    }
    return coverPanel;
  }

  private JLabel getCoverImageLabel()
  {
    if (coverImageLabel == null)
    {
      coverImageLabel = new JLabel("");
      new FileDrop(coverImageLabel, new FileDrop.Listener()
        {
          public void filesDropped(java.io.File[] files)
          {
            // handle file drop
            logger.debug("File dropped for cover!");
            infomodel.setCoverImage(handleCoverFileDrop(files, coverImageLabel));
          }
        });
    }
    return coverImageLabel;
  }

  private JButton getChangeCoverButton()
  {
    if (changeCoverButton == null)
    {
      changeCoverButton = new JButton("...");
      changeCoverButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            selectCoverFile();
          }
        });
      changeCoverButton.setMargin(new Insets(1, 3, 1, 3));
    }
    return changeCoverButton;
  }

  private JPanel getScreenshotPanel()
  {
    if (screenshotPanel == null)
    {
      screenshotPanel = new JPanel();
      screenshotPanel.setMinimumSize(new Dimension(672, 255));
      screenshotPanel.setPreferredSize(new Dimension(672, 255));
      screenshotPanel
        .setBorder(new TitledBorder(null, "Screenshots", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_screenshotPanel = new GridBagLayout();
      screenshotPanel.setLayout(gbl_screenshotPanel);
      GridBagConstraints gbc_screen1ImageLabel = new GridBagConstraints();
      gbc_screen1ImageLabel.gridwidth = 3;
      gbc_screen1ImageLabel.weightx = 0.5;
      gbc_screen1ImageLabel.weighty = 1.0;
      gbc_screen1ImageLabel.anchor = GridBagConstraints.WEST;
      gbc_screen1ImageLabel.insets = new Insets(0, 5, 0, 0);
      gbc_screen1ImageLabel.gridx = 0;
      gbc_screen1ImageLabel.gridy = 0;
      screenshotPanel.add(getScreen1ImageLabel(), gbc_screen1ImageLabel);
      GridBagConstraints gbc_screen1Button = new GridBagConstraints();
      gbc_screen1Button.weightx = 0.5;
      gbc_screen1Button.insets = new Insets(0, 5, 0, 5);
      gbc_screen1Button.anchor = GridBagConstraints.NORTHWEST;
      gbc_screen1Button.gridx = 0;
      gbc_screen1Button.gridy = 1;
      screenshotPanel.add(getScreen1Button(), gbc_screen1Button);
      GridBagConstraints gbc_screen2ImageLabel = new GridBagConstraints();
      gbc_screen2ImageLabel.gridwidth = 3;
      gbc_screen2ImageLabel.weighty = 1.0;
      gbc_screen2ImageLabel.anchor = GridBagConstraints.WEST;
      gbc_screen2ImageLabel.weightx = 0.5;
      gbc_screen2ImageLabel.insets = new Insets(0, 5, 0, 0);
      gbc_screen2ImageLabel.gridx = 3;
      gbc_screen2ImageLabel.gridy = 0;
      screenshotPanel.add(getScreen2ImageLabel(), gbc_screen2ImageLabel);
      GridBagConstraints gbc_resolution1Label = new GridBagConstraints();
      gbc_resolution1Label.insets = new Insets(0, 0, 0, 5);
      gbc_resolution1Label.gridx = 1;
      gbc_resolution1Label.gridy = 1;
      screenshotPanel.add(getResolution1Label(), gbc_resolution1Label);
      GridBagConstraints gbc_crop1Button = new GridBagConstraints();
      gbc_crop1Button.anchor = GridBagConstraints.NORTHEAST;
      gbc_crop1Button.insets = new Insets(0, 0, 3, 0);
      gbc_crop1Button.gridx = 2;
      gbc_crop1Button.gridy = 1;
      screenshotPanel.add(getEdit1Button(), gbc_crop1Button);
      GridBagConstraints gbc_screen2Button = new GridBagConstraints();
      gbc_screen2Button.weightx = 0.5;
      gbc_screen2Button.anchor = GridBagConstraints.NORTHWEST;
      gbc_screen2Button.insets = new Insets(0, 5, 0, 5);
      gbc_screen2Button.gridx = 3;
      gbc_screen2Button.gridy = 1;
      screenshotPanel.add(getScreen2Button(), gbc_screen2Button);
      GridBagConstraints gbc_resolution2Label = new GridBagConstraints();
      gbc_resolution2Label.insets = new Insets(0, 0, 0, 5);
      gbc_resolution2Label.gridx = 4;
      gbc_resolution2Label.gridy = 1;
      screenshotPanel.add(getResolution2Label(), gbc_resolution2Label);
      GridBagConstraints gbc_crop2Button = new GridBagConstraints();
      gbc_crop2Button.anchor = GridBagConstraints.NORTHEAST;
      gbc_crop2Button.insets = new Insets(0, 0, 3, 0);
      gbc_crop2Button.gridx = 5;
      gbc_crop2Button.gridy = 1;
      screenshotPanel.add(getEdit2Button(), gbc_crop2Button);
    }
    return screenshotPanel;
  }

  private JLabel getScreen1ImageLabel()
  {
    if (screen1ImageLabel == null)
    {
      screen1ImageLabel = new JLabel("");
      new FileDrop(screen1ImageLabel, new FileDrop.Listener()
        {
          public void filesDropped(java.io.File[] files)
          {
            infomodel.setScreen1Image(handleScreenFileDrop(files, screen1ImageLabel, edit1Button, true));
          }
        });
    }
    return screen1ImageLabel;
  }

  private JButton getScreen1Button()
  {
    if (screen1Button == null)
    {
      screen1Button = new JButton("...");
      screen1Button.setMargin(new Insets(1, 3, 1, 3));
      screen1Button.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            selectScreenshotFile(true);
          }
        });
    }
    return screen1Button;
  }

  private JLabel getScreen2ImageLabel()
  {
    if (screen2ImageLabel == null)
    {
      screen2ImageLabel = new JLabel("");
      new FileDrop(screen2ImageLabel, new FileDrop.Listener()
        {
          public void filesDropped(java.io.File[] files)
          {
            infomodel.setScreen2Image(handleScreenFileDrop(files, screen2ImageLabel, edit2Button, false));
          }
        });
    }
    return screen2ImageLabel;
  }

  private JButton getScreen2Button()
  {
    if (screen2Button == null)
    {
      screen2Button = new JButton("...");
      screen2Button.setMargin(new Insets(1, 3, 1, 3));
      screen2Button.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            selectScreenshotFile(false);
          }
        });
    }
    return screen2Button;
  }

  private JPanel getGamePanel()
  {
    if (gamePanel == null)
    {
      gamePanel = new JPanel();
      GridBagLayout gbl_gamePanel = new GridBagLayout();
      gamePanel.setLayout(gbl_gamePanel);
      GridBagConstraints gbc_gameLabel = new GridBagConstraints();
      gbc_gameLabel.weightx = 1.0;
      gbc_gameLabel.anchor = GridBagConstraints.WEST;
      gbc_gameLabel.insets = new Insets(12, 2, 0, 5);
      gbc_gameLabel.gridx = 0;
      gbc_gameLabel.gridy = 0;
      gamePanel.add(getGameLabel(), gbc_gameLabel);
      GridBagConstraints gbc_viewTagLabel = new GridBagConstraints();
      gbc_viewTagLabel.anchor = GridBagConstraints.WEST;
      gbc_viewTagLabel.insets = new Insets(12, 0, 0, 3);
      gbc_viewTagLabel.gridx = 2;
      gbc_viewTagLabel.gridy = 0;
      gamePanel.add(getViewTagLabel(), gbc_viewTagLabel);
      GridBagConstraints gbc_gameTextField = new GridBagConstraints();
      gbc_gameTextField.anchor = GridBagConstraints.NORTHWEST;
      gbc_gameTextField.weighty = 1.0;
      gbc_gameTextField.insets = new Insets(1, 2, 10, 5);
      gbc_gameTextField.fill = GridBagConstraints.HORIZONTAL;
      gbc_gameTextField.gridx = 0;
      gbc_gameTextField.gridy = 1;
      gamePanel.add(getGameTextField(), gbc_gameTextField);
      GridBagConstraints gbc_gameButton = new GridBagConstraints();
      gbc_gameButton.weightx = 1.0;
      gbc_gameButton.weighty = 1.0;
      gbc_gameButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_gameButton.insets = new Insets(0, 0, 10, 5);
      gbc_gameButton.gridx = 1;
      gbc_gameButton.gridy = 1;
      gamePanel.add(getGameButton(), gbc_gameButton);
      GridBagConstraints gbc_viewTagTextField = new GridBagConstraints();
      gbc_viewTagTextField.insets = new Insets(0, 0, 0, 3);
      gbc_viewTagTextField.anchor = GridBagConstraints.NORTHWEST;
      gbc_viewTagTextField.fill = GridBagConstraints.HORIZONTAL;
      gbc_viewTagTextField.gridx = 2;
      gbc_viewTagTextField.gridy = 1;
      gamePanel.add(getViewTagTextField(), gbc_viewTagTextField);
    }
    return gamePanel;
  }

  private JLabel getGameLabel()
  {
    if (gameLabel == null)
    {
      gameLabel = new JLabel("Game file");
    }
    return gameLabel;
  }

  private JTextField getGameTextField()
  {
    if (gameTextField == null)
    {
      gameTextField = new JTextField();
      gameTextField.setEditable(false);
      gameTextField.setPreferredSize(new Dimension(155, 20));
      new FileDrop(gameTextField, new FileDrop.Listener()
        {
          public void filesDropped(java.io.File[] files)
          {
            if (files.length > 0)
            {
              //Matcher for valid files
              PathMatcher matcher =
                FileSystems.getDefault().getPathMatcher("glob:**.{d64,t64,tap,vsf,vsz,gz,crt,prg,g64,zip,d81,d82}");
              if (!matcher.matches(files[0].toPath()))
              {
                JOptionPane
                  .showMessageDialog(getGameTextField(),
                                     "Invalid file format, it must be a d64, t64, tap, vsf, vsz, crt, prg, g64, d81, d82, gz or zip file.",
                                     "Game file",
                                     JOptionPane.ERROR_MESSAGE);
              }
              else
              {
                gamesFileUpdated = true;
                infomodel.setGamesPath(files[0]);
              }
            }
          }
        });
    }
    return gameTextField;
  }

  private JButton getGameButton()
  {
    if (gameButton == null)
    {
      gameButton = new JButton("...");
      gameButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            selectGameFile();
          }
        });
      gameButton.setMargin(new Insets(1, 3, 1, 3));
    }
    return gameButton;
  }

  private JButton getEdit1Button()
  {
    if (edit1Button == null)
    {
      edit1Button = new JButton(warningIcon);
      edit1Button.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            JPopupMenu menu = new JPopupMenu();
            InsetsMenuItem autoEditItem = new InsetsMenuItem("Crop automatically to 320x200");
            autoEditItem.addActionListener(e -> {
              BufferedImage croppedImage = FileManager.cropImageTo320x200(currentScreen1Image);
              getScreen1ImageLabel().setIcon(new ImageIcon(croppedImage));
              infomodel.setScreen1Image(croppedImage);
              edit1Button.setVisible(false);
            });
            menu.add(autoEditItem);

            InsetsMenuItem manEditItem = new InsetsMenuItem("Crop manually...");
            manEditItem.addActionListener(e -> {
              EditScreenshotDialog dialog = new EditScreenshotDialog(currentScreen1Image);
              dialog.pack();
              dialog.setLocationRelativeTo(MainWindow.getInstance());
              if (dialog.showDialog())
              {
                BufferedImage croppedImage = dialog.getEditedImage();
                getScreen1ImageLabel().setIcon(new ImageIcon(croppedImage));
                infomodel.setScreen1Image(croppedImage);
                edit1Button.setVisible(false);
              }
            });
            menu.add(manEditItem);
            menu.show(edit1Button, 15, 15);
          }
        });
      edit1Button.setMargin(new Insets(1, 3, 1, 3));
      edit1Button.setToolTipText(editTooltip);
      edit1Button.setVisible(false);
    }
    return edit1Button;
  }

  private JButton getEdit2Button()
  {
    if (edit2Button == null)
    {
      edit2Button = new JButton(warningIcon);
      edit2Button.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            JPopupMenu menu = new JPopupMenu();
            InsetsMenuItem autoEditItem = new InsetsMenuItem("Crop automatically to 320x200");
            autoEditItem.addActionListener(e -> {
              BufferedImage croppedImage = FileManager.cropImageTo320x200(currentScreen2Image);
              getScreen2ImageLabel().setIcon(new ImageIcon(croppedImage));
              infomodel.setScreen2Image(croppedImage);
              edit2Button.setVisible(false);
            });
            menu.add(autoEditItem);

            InsetsMenuItem manEditItem = new InsetsMenuItem("Crop manually...");
            manEditItem.addActionListener(e -> {
              EditScreenshotDialog dialog = new EditScreenshotDialog(currentScreen2Image);
              dialog.pack();
              dialog.setLocationRelativeTo(MainWindow.getInstance());
              if (dialog.showDialog())
              {
                BufferedImage croppedImage = dialog.getEditedImage();
                getScreen2ImageLabel().setIcon(new ImageIcon(croppedImage));
                infomodel.setScreen2Image(croppedImage);
                edit2Button.setVisible(false);
              }
            });
            menu.add(manEditItem);
            menu.show(edit2Button, 15, 15);
          }
        });
      edit2Button.setMargin(new Insets(1, 3, 1, 3));
      edit2Button.setToolTipText(editTooltip);
      edit2Button.setVisible(false);
    }
    return edit2Button;
  }

  private BufferedImage handleCoverFileDrop(File[] files, JLabel imageLabel)
  {
    BufferedImage returnImage = null;
    if (files.length > 0)
    {
      try
      {
        returnImage = ImageIO.read(files[0]);
        if (returnImage == null)
        {
          JOptionPane.showMessageDialog(getCoverImageLabel(),
                                        "Invalid file format, it must be a png, gif, jpeg or bmp file.",
                                        "Cover file",
                                        JOptionPane.ERROR_MESSAGE);
          return null;
        }
        if (FileManager.isShowCropDialogForCover())
        {
          //Show a edit dialog
          EditCoverDialog dialog = new EditCoverDialog(returnImage);
          dialog.pack();
          dialog.setLocationRelativeTo(MainWindow.getInstance());
          if (dialog.showDialog())
          {
            returnImage = dialog.getEditedImage();
          }
          else
          {
            return null;
          }
        }
        Image newImage = returnImage.getScaledInstance(130, 200, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(newImage));
      }
      catch (IOException e)
      {
        logger.error("can't read file: " + files[0].getName(), e);
        imageLabel.setIcon(getMissingScreenshotImageIcon());
      }
    }
    return returnImage;
  }

  private BufferedImage handleScreenFileDrop(File[] files, JLabel imageLabel, JButton editButton, boolean first)
  {
    BufferedImage returnImage = null;
    if (files.length > 0)
    {
      try
      {
        returnImage = ImageIO.read(files[0]);
        if (returnImage == null)
        {
          JOptionPane.showMessageDialog(getScreenshotPanel(),
                                        "Invalid file format, it must be a png, gif, jpeg or bmp file.",
                                        "Screenshot file",
                                        JOptionPane.ERROR_MESSAGE);
          return null;
        }
        if (infomodel.isInfoSlot())
        {
          //Ask if text shall be added
          int value = JOptionPane.showConfirmDialog(getScreenshotPanel(),
                                                    "Do you want to add the gamelist view name to the screenshot?",
                                                    "Screenshot file",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.INFORMATION_MESSAGE);
          if (value == JOptionPane.YES_OPTION)
          {
            //Make sure the image is of right size before adding text to it
            returnImage = FileManager.scaleImageTo320x200x32bit(returnImage);
            model.writeGameViewTextOnScreen(returnImage, first ? Color.yellow : Color.red);
          }
        }
        if (FileManager.isCropScreenshots())
        {
          editButton.setVisible(false);
          returnImage = FileManager.cropImageTo320x200(returnImage);
        }
        else
        {
          setEditButtonVisibility(returnImage, editButton);
        }
        imageLabel.setIcon(new ImageIcon(FileManager.scaleImageTo320x200x32bit(returnImage)));
      }
      catch (IOException e)
      {
        logger.error("can't read file: " + files[0].getName(), e);
        imageLabel.setIcon(getMissingScreenshotImageIcon());
      }
    }
    return returnImage;
  }

  private void setEditButtonVisibility(BufferedImage image, JButton editButton)
  {
    if (image.getHeight() != 200 || image.getWidth() != 320)
    {
      editButton.setVisible(true);
    }
    else
    {
      editButton.setVisible(false);
    }
  }

  private void selectGameFile()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select a valid game file for " + infomodel.getTitle());
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    String gameDir = FileManager.getConfiguredProperties().getProperty(GAME_DIR_PROPERTY);
    if (gameDir == null)
    {
      gameDir = ".";
    }
    fileChooser.setCurrentDirectory(new File(gameDir));

    FileNameExtensionFilter vicefilter = new FileNameExtensionFilter("Vice runnable files",
                                                                     "d64",
                                                                     "t64",
                                                                     "tap",
                                                                     "VSF",
                                                                     "VSZ",
                                                                     "GZ",
                                                                     "crt",
                                                                     "prg",
                                                                     "g64",
                                                                     "d81",
                                                                     "d82");
    fileChooser.addChoosableFileFilter(vicefilter);
    fileChooser.setFileFilter(vicefilter);
    int value = fileChooser.showOpenDialog(MainWindow.getInstance());
    if (value == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = fileChooser.getSelectedFile();
      FileManager.getConfiguredProperties().put(GAME_DIR_PROPERTY, selectedFile.toPath().getParent().toString());
      gamesFileUpdated = true;
      infomodel.setGamesPath(selectedFile);
    }
  }

  private void selectCoverFile()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select a cover image for " + infomodel.getTitle());
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    String coverDir = FileManager.getConfiguredProperties().getProperty(COVER_DIR_PROPERTY);
    if (coverDir == null)
    {
      coverDir = ".";
    }
    fileChooser.setCurrentDirectory(new File(coverDir));
    fileChooser.addChoosableFileFilter(imagefilter);
    fileChooser.setFileFilter(imagefilter);
    int value = fileChooser.showOpenDialog(MainWindow.getInstance());
    if (value == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = fileChooser.getSelectedFile();
      FileManager.getConfiguredProperties().put(COVER_DIR_PROPERTY, selectedFile.toPath().getParent().toString());
      infomodel.setCoverImage(handleCoverFileDrop(new File[] { selectedFile }, coverImageLabel));
    }
  }

  private void selectScreenshotFile(boolean first)
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select screenshot image for " + infomodel.getTitle());
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    String screensDir = FileManager.getConfiguredProperties().getProperty(SCREENS_DIR_PROPERTY);
    if (screensDir == null)
    {
      screensDir = ".";
    }
    fileChooser.setCurrentDirectory(new File(screensDir));
    fileChooser.addChoosableFileFilter(imagefilter);
    fileChooser.setFileFilter(imagefilter);
    int value = fileChooser.showOpenDialog(MainWindow.getInstance());
    if (value == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = fileChooser.getSelectedFile();
      FileManager.getConfiguredProperties().put(SCREENS_DIR_PROPERTY, selectedFile.toPath().getParent().toString());
      if (first)
      {
        infomodel
          .setScreen1Image(handleScreenFileDrop(new File[] { selectedFile }, screen1ImageLabel, edit1Button, true));
      }
      else
      {
        infomodel
          .setScreen2Image(handleScreenFileDrop(new File[] { selectedFile }, screen2ImageLabel, edit2Button, false));
      }
    }
  }

  private JLabel getResolution1Label()
  {
    if (resolution1Label == null)
    {
      resolution1Label = new JLabel(" ");
    }
    return resolution1Label;
  }

  private JLabel getResolution2Label()
  {
    if (resolution2Label == null)
    {
      resolution2Label = new JLabel(" ");
    }
    return resolution2Label;
  }

  private JLabel getViewTagLabel()
  {
    if (viewTagLabel == null)
    {
      viewTagLabel = new JLabel("View tag");
    }
    return viewTagLabel;
  }

  private JTextField getViewTagTextField()
  {
    if (viewTagTextField == null)
    {
      viewTagTextField = new JTextField();
      viewTagTextField.setColumns(25);
      viewTagTextField.setDocument(new CustomUndoPlainDocument()
        {
          @Override
          public void updateModel()
          {
            infomodel.setViewTag(viewTagTextField.getText());
          }
        });
      viewTagTextField.addKeyListener(new KeyAdapter()
        {
          @Override
          public void keyReleased(KeyEvent e)
          {
            JTextField textField = (JTextField) e.getSource();
            infomodel.setViewTag(textField.getText());
          }
        });
    }
    return viewTagTextField;
  }
}
