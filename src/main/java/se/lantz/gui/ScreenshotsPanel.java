package se.lantz.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.InfoModel;
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
  private InfoModel model;

  private String currentCoverFile = "";
  private String currentScreen1File = "";
  private String currentScreen2File = "";
  private BufferedImage currentCoverImage = null;
  private BufferedImage currentScreen1Image = null;
  private BufferedImage currentScreen2Image = null;

  private ImageIcon missingSceenshotIcon = null;
  private ImageIcon missingCoverIcon = null;
  private JButton edit1Button;
  private JButton edit2Button;

  private ImageIcon warningIcon = new ImageIcon(getClass().getResource("/se/lantz/warning-icon.png"));
  private String editTooltip =
    "<html>Optimal resolution for the carousel is 320x200.<br>Press to crop the image to this size (the border will be removed).<br>Otherwise it will be resized when saving which might make it blurry.</html>";
  private boolean gamesFileUpdated = false;

  FileNameExtensionFilter imagefilter =
    new FileNameExtensionFilter("png, gif, jpeg, bmp", "png", "gif", "jpg", "jpeg", "bmp");
  private JLabel resolution1Label;
  private JLabel resolution2Label;

  public ScreenshotsPanel(InfoModel model)
  {
    this.model = model;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0 };
    gridBagLayout.rowWeights = new double[] { 0.0, 1.0 };
    setLayout(gridBagLayout);
    GridBagConstraints gbc_coverPanel = new GridBagConstraints();
    gbc_coverPanel.fill = GridBagConstraints.BOTH;
    gbc_coverPanel.insets = new Insets(10, 0, 5, 5);
    gbc_coverPanel.gridx = 1;
    gbc_coverPanel.gridy = 0;
    add(getCoverPanel(), gbc_coverPanel);
    GridBagConstraints gbc_screenshotPanel = new GridBagConstraints();
    gbc_screenshotPanel.insets = new Insets(10, 0, 5, 5);
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
      model.addPropertyChangeListener((e) -> modelChanged());
    }
  }

  private void modelChanged()
  {
    if (!model.isDataChanged())
    {
      gamesFileUpdated = false;
    }
    // Read from model
    getGameTextField().setText(getGameFileName());
    reloadScreens();
  }

  private String getGameFileName()
  {
    String returnValue = model.getGamesFile();
    if (gamesFileUpdated)
    {
      returnValue = returnValue + " (updated)";
    }
    return returnValue;
  }

  private void reloadScreens()
  {
    //Cover
    BufferedImage coverImage = model.getCoverImage();
    if (coverImage != null)
    {
      if (!coverImage.equals(currentCoverImage))
      {
        logger.debug("SETTING NEW COVER IMAGE");
        Image newImage = coverImage.getScaledInstance(130, 200, Image.SCALE_DEFAULT);
        getCoverImageLabel().setIcon(new ImageIcon(newImage));
        currentCoverImage = coverImage;
      }
    }
    else
    {
      String modelCoverFile = model.getCoverFile();
      if (modelCoverFile.isEmpty())
      {
        currentCoverFile = "";
        getCoverImageLabel().setIcon(getMissingCoverImageIcon());
      }
      else if (!modelCoverFile.equals(currentCoverFile))
      {
        loadCover(modelCoverFile);
        currentCoverFile = modelCoverFile;
      }
    }
    //Screen 1
    BufferedImage screen1Image = model.getScreen1Image();
    if (screen1Image != null)
    {
      if (!screen1Image.equals(currentScreen1Image))
      {
        logger.debug("SETTING SCREEN 1 IMAGE");
        Image newImage = screen1Image.getScaledInstance(320, 200, Image.SCALE_DEFAULT);
        getScreen1ImageLabel().setIcon(new ImageIcon(newImage));
        setEditButtonVisibilityAndResolution(screen1Image, getResolution1Label(), getEdit1Button());
        currentScreen1Image = screen1Image;
      }
    }
    else
    {
      String modelScreen1File = model.getScreens1File();
      if (modelScreen1File.isEmpty())
      {
        currentScreen1File = "";
        getScreen1ImageLabel().setIcon(getMissingScreenshotImageIcon());
      }
      else if (!model.getScreens1File().equals(currentScreen1File))
      {
        currentScreen1Image =
          loadScreen(modelScreen1File, getScreen1ImageLabel(), getResolution1Label(), getEdit1Button());
        currentScreen1File = modelScreen1File;
      }
    }
    //Screen 2
    BufferedImage screen2Image = model.getScreen2Image();
    if (screen2Image != null)
    {
      if (!screen2Image.equals(currentScreen2Image))
      {
        logger.debug("SETTING SCREEN 2 IMAGE");
        Image newImage = screen2Image.getScaledInstance(320, 200, Image.SCALE_DEFAULT);
        getScreen2ImageLabel().setIcon(new ImageIcon(newImage));
        setEditButtonVisibilityAndResolution(screen2Image, getResolution2Label(), getEdit2Button());
        currentScreen2Image = screen2Image;
      }
    }
    else
    {
      String modelScreen2File = model.getScreens2File();
      if (modelScreen2File.isEmpty())
      {
        currentScreen2File = "";
        getScreen2ImageLabel().setIcon(getMissingScreenshotImageIcon());
      }
      else if (!modelScreen2File.equals(currentScreen2File))
      {
        currentScreen2Image =
          loadScreen(modelScreen2File, getScreen2ImageLabel(), getResolution2Label(), getEdit2Button());
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
      File imagefile = new File("./covers/" + filename);
      try
      {
        BufferedImage image = ImageIO.read(imagefile);
        Image newImage = image.getScaledInstance(130, 200, Image.SCALE_DEFAULT);
        getCoverImageLabel().setIcon(new ImageIcon(newImage));
      }
      catch (IOException e)
      {
        logger.error("can't read file: " + filename, e);
        getCoverImageLabel().setIcon(getMissingCoverImageIcon());
      }
    }
    else
    {
      getCoverImageLabel().setIcon(getMissingCoverImageIcon());
    }
  }

  private BufferedImage loadScreen(String filename, JLabel screenLabel, JLabel resolutionLabel, JButton editButton)
  {
    BufferedImage image = null;
    if (!filename.isEmpty())
    {
      File imagefile = new File("./screens/" + filename);
      try
      {
        image = ImageIO.read(imagefile);
        Image newImage = image.getScaledInstance(320, 200, Image.SCALE_DEFAULT);
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
      missingSceenshotIcon = new ImageIcon(getClass().getResource("/se/lantz/MissingScreenshot.png"));
    }
    return missingSceenshotIcon;
  }

  private ImageIcon getMissingCoverImageIcon()
  {
    if (missingCoverIcon == null)
    {
      missingCoverIcon = new ImageIcon(getClass().getResource("/se/lantz/MissingCover.png"));
    }
    return missingCoverIcon;
  }

  private JPanel getCoverPanel()
  {
    if (coverPanel == null)
    {
      coverPanel = new JPanel();
      coverPanel.setMinimumSize(new Dimension(152, 255));
      coverPanel.setPreferredSize(new Dimension(152, 255));
      coverPanel.setBorder(new TitledBorder(null, "Cover", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_coverPanel = new GridBagLayout();
      coverPanel.setLayout(gbl_coverPanel);
      GridBagConstraints gbc_CoverImageLabel = new GridBagConstraints();
      gbc_CoverImageLabel.weighty = 1.0;
      gbc_CoverImageLabel.weightx = 1.0;
      gbc_CoverImageLabel.anchor = GridBagConstraints.WEST;
      gbc_CoverImageLabel.insets = new Insets(0, 5, 5, 5);
      gbc_CoverImageLabel.gridx = 0;
      gbc_CoverImageLabel.gridy = 1;
      coverPanel.add(getCoverImageLabel(), gbc_CoverImageLabel);
      GridBagConstraints gbc_changeCoverButton = new GridBagConstraints();
      gbc_changeCoverButton.weightx = 1.0;
      gbc_changeCoverButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_changeCoverButton.insets = new Insets(0, 5, 5, 0);
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
            model.setCoverImage(handleCoverFileDrop(files, coverImageLabel));
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
      gbc_screen1ImageLabel.insets = new Insets(0, 5, 5, 5);
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
      gbc_screen2ImageLabel.insets = new Insets(0, 5, 5, 0);
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
      gbc_crop1Button.insets = new Insets(0, 0, 3, 5);
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
            model.setScreen1Image(handleScreenFileDrop(files, screen1ImageLabel, edit1Button));
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
            model.setScreen2Image(handleScreenFileDrop(files, screen2ImageLabel, edit2Button));
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
      gbc_gameLabel.insets = new Insets(5, 0, 0, 5);
      gbc_gameLabel.gridx = 0;
      gbc_gameLabel.gridy = 0;
      gamePanel.add(getGameLabel(), gbc_gameLabel);
      GridBagConstraints gbc_gameTextField = new GridBagConstraints();
      gbc_gameTextField.anchor = GridBagConstraints.NORTHWEST;
      gbc_gameTextField.weighty = 1.0;
      gbc_gameTextField.insets = new Insets(1, 0, 5, 5);
      gbc_gameTextField.fill = GridBagConstraints.HORIZONTAL;
      gbc_gameTextField.gridx = 0;
      gbc_gameTextField.gridy = 1;
      gamePanel.add(getGameTextField(), gbc_gameTextField);
      GridBagConstraints gbc_gameButton = new GridBagConstraints();
      gbc_gameButton.weighty = 1.0;
      gbc_gameButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_gameButton.weightx = 1.0;
      gbc_gameButton.insets = new Insets(0, 0, 5, 5);
      gbc_gameButton.gridx = 1;
      gbc_gameButton.gridy = 1;
      gamePanel.add(getGameButton(), gbc_gameButton);
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
      gameTextField.setPreferredSize(new Dimension(160, 20));
      new FileDrop(gameTextField, new FileDrop.Listener()
        {
          public void filesDropped(java.io.File[] files)
          {
            if (files.length > 0)
            {
              gamesFileUpdated = true;
              model.setGamesPath(files[0]);
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
            BufferedImage croppedImage = FileManager.cropImageTo320x200(currentScreen1Image);
            getScreen1ImageLabel().setIcon(new ImageIcon(croppedImage));
            model.setScreen1Image(croppedImage);
            edit1Button.setVisible(false);
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
            BufferedImage croppedImage = FileManager.cropImageTo320x200(currentScreen2Image);
            getScreen2ImageLabel().setIcon(new ImageIcon(croppedImage));
            model.setScreen2Image(croppedImage);
            edit2Button.setVisible(false);
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
        Image newImage = returnImage.getScaledInstance(135, 200, Image.SCALE_DEFAULT);
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

  private BufferedImage handleScreenFileDrop(File[] files, JLabel imageLabel, JButton editButton)
  {
    BufferedImage returnImage = null;
    if (files.length > 0)
    {
      try
      {
        returnImage = ImageIO.read(files[0]);

        setEditButtonVisibility(returnImage, editButton);
        Image newImage = returnImage.getScaledInstance(320, 200, Image.SCALE_DEFAULT);
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
    fileChooser.setDialogTitle("Select a valid game file for " + model.getTitle());
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    String gameDir = FileManager.getConfiguredProperties().getProperty(GAME_DIR_PROPERTY);
    if (gameDir == null)
    {
      gameDir = ".";
    }
    fileChooser.setCurrentDirectory(new File(gameDir));

    FileNameExtensionFilter vicefilter =
      new FileNameExtensionFilter("Vice runnable files", "d64", "t64", "tap", "VSF", "GZ", "crt", "prg", "g64");
    fileChooser.addChoosableFileFilter(vicefilter);
    fileChooser.setFileFilter(vicefilter);
    int value = fileChooser.showOpenDialog(MainWindow.getInstance());
    if (value == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = fileChooser.getSelectedFile();
      FileManager.getConfiguredProperties().put(GAME_DIR_PROPERTY, selectedFile.toPath().getParent().toString());
      gamesFileUpdated = true;
      model.setGamesPath(selectedFile);
    }
  }

  private void selectCoverFile()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select a cover image for " + model.getTitle());
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
      model.setCoverImage(handleCoverFileDrop(new File[] { selectedFile }, coverImageLabel));
    }
  }

  private void selectScreenshotFile(boolean first)
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select screenshot image for " + model.getTitle());
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
        model.setScreen1Image(handleScreenFileDrop(new File[] { selectedFile }, screen1ImageLabel, edit1Button));
      }
      else
      {
        model.setScreen2Image(handleScreenFileDrop(new File[] { selectedFile }, screen2ImageLabel, edit2Button));
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
}
