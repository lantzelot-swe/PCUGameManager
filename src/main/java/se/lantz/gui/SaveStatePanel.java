package se.lantz.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.MainViewModel;
import se.lantz.model.SavedStatesModel;
import se.lantz.model.SavedStatesModel.SAVESTATE;
import se.lantz.util.FileDrop;
import se.lantz.util.FileManager;

public class SaveStatePanel extends JPanel
{
  private static final Logger logger = LoggerFactory.getLogger(SaveStatePanel.class);
  private JLabel screenshotLabel;
  private JTextField gameTextField;
  private MainViewModel model;
  private SavedStatesModel stateModel;
  private JButton gameButton;
  private BufferedImage currentScreen1Image = null;
  private String currentScreenFile = "";
  private ImageIcon missingSceenshotIcon = null;
  private JLabel timeLabel;
  private JLabel lblNewLabel;
  private JFormattedTextField timeField;
  private JButton runButton;
  private boolean gamesFileUpdated = false;
  private SAVESTATE saveState;

  public SaveStatePanel(MainViewModel model, SAVESTATE saveState)
  {
    this.model = model;
    this.saveState = saveState;
    if (!Beans.isDesignTime())
    {
      this.stateModel = model.getSavedStatesModel();
    }
    

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_screenshotLabel = new GridBagConstraints();
    gbc_screenshotLabel.weighty = 1.0;
    gbc_screenshotLabel.gridheight = 4;
    gbc_screenshotLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_screenshotLabel.insets = new Insets(5, 5, 0, 5);
    gbc_screenshotLabel.gridx = 0;
    gbc_screenshotLabel.gridy = 0;
    add(getScreenshotLabel(), gbc_screenshotLabel);
    GridBagConstraints gbc_timeTextField = new GridBagConstraints();
    gbc_timeTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_timeTextField.anchor = GridBagConstraints.NORTHWEST;
    gbc_timeTextField.weighty = 1.0;
    gbc_timeTextField.insets = new Insets(0, 5, 0, 5);
    gbc_timeTextField.gridx = 1;
    gbc_timeTextField.gridy = 3;
    add(getTimeField(), gbc_timeTextField);
    GridBagConstraints gbc_gameTextField = new GridBagConstraints();
    gbc_gameTextField.gridwidth = 2;
    gbc_gameTextField.weightx = 1.0;
    gbc_gameTextField.anchor = GridBagConstraints.NORTHWEST;
    gbc_gameTextField.insets = new Insets(0, 5, 5, 5);
    gbc_gameTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_gameTextField.gridx = 1;
    gbc_gameTextField.gridy = 1;
    add(getGameTextField(), gbc_gameTextField);
    GridBagConstraints gbc_gameButton = new GridBagConstraints();
    gbc_gameButton.insets = new Insets(0, 0, 5, 5);
    gbc_gameButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_gameButton.gridx = 3;
    gbc_gameButton.gridy = 1;
    add(getGameButton(), gbc_gameButton);
    GridBagConstraints gbc_timeLabel = new GridBagConstraints();
    gbc_timeLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_timeLabel.insets = new Insets(0, 5, 0, 5);
    gbc_timeLabel.gridx = 1;
    gbc_timeLabel.gridy = 2;
    add(getTimeLabel(), gbc_timeLabel);
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.insets = new Insets(5, 5, 0, 5);
    gbc_lblNewLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_lblNewLabel.gridx = 1;
    gbc_lblNewLabel.gridy = 0;
    add(getLblNewLabel(), gbc_lblNewLabel);
    GridBagConstraints gbc_runButton = new GridBagConstraints();
    gbc_runButton.weighty = 1.0;
    gbc_runButton.insets = new Insets(0, 0, 0, 5);
    gbc_runButton.anchor = GridBagConstraints.NORTHEAST;
    gbc_runButton.gridwidth = 2;
    gbc_runButton.gridx = 2;
    gbc_runButton.gridy = 3;
    add(getRunButton(), gbc_runButton);

    if (!Beans.isDesignTime())
    {
      stateModel.addPropertyChangeListener((e) -> modelChanged());
    }
  }

  private void modelChanged()
  {
    if (!stateModel.isDataChanged())
    {
      gamesFileUpdated = false;
    }
    switch (saveState)
    {
    case Save0:
      if (!stateModel.getState1time().equals(getTimeField().getValue()))
      {
        getTimeField().setValue(stateModel.getState1time());
      }
      break;
    case Save1:
      if (!stateModel.getState2time().equals(getTimeField().getValue()))
      {
        getTimeField().setValue(stateModel.getState2time());
      }
      break;
    case Save2:
      if (!stateModel.getState3time().equals(getTimeField().getValue()))
      {
        getTimeField().setValue(stateModel.getState3time());
      }
      break;
    case Save3:
      if (!stateModel.getState4time().equals(getTimeField().getValue()))
      {
        getTimeField().setValue(stateModel.getState4time());
      }
      break;
    default:
      break;

    }
    // Read from model   
    getGameTextField().setText(getGameFileName());

    reloadScreen();
  }

  private String getGameFileName()
  {
    String returnValue = "";
    switch (saveState)
    {
    case Save0:
      returnValue = stateModel.getState1File();
      break;
    case Save1:
      returnValue = stateModel.getState2File();
      break;
    case Save2:
      returnValue = stateModel.getState3File();
      break;
    case Save3:
      returnValue = stateModel.getState4File();
      break;
    default:
      break;
    }
    if (gamesFileUpdated)
    {
      returnValue = returnValue + " (updated)";
    }
    return returnValue;
  }

  private void reloadScreen()
  {
    BufferedImage screen1Image = null;
    switch (saveState)
    {
    case Save0:
      screen1Image = stateModel.getState1PngImage();
      break;
    case Save1:
      screen1Image = stateModel.getState2PngImage();
      break;
    case Save2:
      screen1Image = stateModel.getState3PngImage();
      break;
    case Save3:
      screen1Image = stateModel.getState4PngImage();
      break;
    default:
      break;
    
    }
    if (screen1Image != null)
    {
      if (!screen1Image.equals(currentScreen1Image))
      {
        logger.debug("SETTING SCREEN 1 IMAGE");
        getScreenshotLabel().setIcon(new ImageIcon(FileManager.scaleImageTo320x200x32bit(screen1Image)));
        currentScreen1Image = screen1Image;
      }
    }
    else
    {
      String modelScreenFile = "";
      switch (saveState)
      {
      case Save0:
        modelScreenFile = stateModel.getState1PngFile();
        break;
      case Save1:
        modelScreenFile = stateModel.getState2PngFile();
        break;
      case Save2:
        modelScreenFile = stateModel.getState3PngFile();
        break;
      case Save3:
        modelScreenFile = stateModel.getState4PngFile();
        break;
      default:
        break;
      
      }
      if (modelScreenFile.isEmpty())
      {
        currentScreenFile = "";
        getScreenshotLabel().setIcon(getMissingScreenshotImageIcon());
      }
      else if (!modelScreenFile.equals(currentScreenFile))
      {
        currentScreen1Image = loadScreen(modelScreenFile, getScreenshotLabel());
        currentScreenFile = modelScreenFile;
      }
    }
  }

  private BufferedImage loadScreen(String filename, JLabel screenLabel)
  {
    BufferedImage image = null;
    if (!filename.isEmpty())
    {
      String fileName = model.getInfoModel().getGamesFile();
      System.out.println(fileName.toString());

      Path saveFolderPath = new File("./saves/" + fileName).toPath();
      File imagefile = saveFolderPath.resolve(filename).toFile();
      try
      {
        image = ImageIO.read(imagefile);
        Image newImage = image.getScaledInstance(160, 100, Image.SCALE_SMOOTH);
        screenLabel.setIcon(new ImageIcon(newImage));
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
      BufferedImage image = null;
      try
      {
        image = ImageIO.read(FileManager.class.getResource("/se/lantz/MissingScreenshot-C64.png"));
        Image newImage = image.getScaledInstance(160, 100, Image.SCALE_SMOOTH);
        missingSceenshotIcon = new ImageIcon(newImage);
      }
      catch (IOException e)
      {
        logger.error("can't read missing icon", e);

      }
    }
    return missingSceenshotIcon;
  }

  private JLabel getScreenshotLabel()
  {
    if (screenshotLabel == null)
    {
      screenshotLabel = new JLabel("");
    }
    return screenshotLabel;
  }

  private JTextField getGameTextField()
  {
    if (gameTextField == null)
    {
      gameTextField = new JTextField();
      gameTextField.setEditable(false);
      gameTextField.setPreferredSize(new Dimension(50, 20));
      new FileDrop(gameTextField, new FileDrop.Listener()
        {
          public void filesDropped(java.io.File[] files)
          {
            if (files.length > 0)
            {
              gamesFileUpdated = true;
              switch (saveState)
              {
              case Save0:
                stateModel.setState1Path(files[0].toPath());
                break;
              case Save1:
                stateModel.setState2Path(files[0].toPath());
                break;
              case Save2:
                stateModel.setState3Path(files[0].toPath());
                break;
              case Save3:
                stateModel.setState4Path(files[0].toPath());
                break;
              default:
                break; 
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

  private void selectGameFile()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select a valid game file for " + model.getInfoModel().getTitle());
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    String gameDir = null; //FileManager.getConfiguredProperties().getProperty(GAME_DIR_PROPERTY);
    if (gameDir == null)
    {
      gameDir = ".";
    }
    fileChooser.setCurrentDirectory(new File(gameDir));

    FileNameExtensionFilter vicefilter = new FileNameExtensionFilter("Vice snapshot", "vsf");
    fileChooser.addChoosableFileFilter(vicefilter);
    fileChooser.setFileFilter(vicefilter);
    int value = fileChooser.showOpenDialog(MainWindow.getInstance());
    if (value == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = fileChooser.getSelectedFile();
      //      FileManager.getConfiguredProperties().put(GAME_DIR_PROPERTY, selectedFile.toPath().getParent().toString());
      gamesFileUpdated = true;
      switch (saveState)
      {
      case Save0:
        stateModel.setState1Path(selectedFile.toPath());
        break;
      case Save1:
        stateModel.setState2Path(selectedFile.toPath());
        break;
      case Save2:
        stateModel.setState3Path(selectedFile.toPath());
        break;
      case Save3:
        stateModel.setState4Path(selectedFile.toPath());
        break;
      default:
        break;     
      }
    }
  }

  private JLabel getTimeLabel()
  {
    if (timeLabel == null)
    {
      timeLabel = new JLabel("Play time");
    }
    return timeLabel;
  }

  private JLabel getLblNewLabel()
  {
    if (lblNewLabel == null)
    {
      lblNewLabel = new JLabel("Vice snapshot");
    }
    return lblNewLabel;
  }

  private JFormattedTextField getTimeField()
  {
    if (timeField == null)
    {
      MaskFormatter formatter = null;
      try
      {
        formatter = new MaskFormatter("##:##:##");
      }
      catch (java.text.ParseException exc)
      {
        System.err.println("formatter is bad: " + exc.getMessage());
        System.exit(-1);
      }
      formatter.setPlaceholderCharacter('0');
      timeField = new JFormattedTextField(formatter);

      timeField.addKeyListener(new KeyAdapter()
        {
          @Override
          public void keyReleased(KeyEvent e)
          {
            //Only notify of change when text is altered: value is not committed before focus is lost.
            if (!timeField.getText().equals(timeField.getValue()))
            {
              stateModel.notifyChange();
            }
          }
        });

      timeField.addFocusListener(new FocusAdapter()
        {
          @Override
          public void focusLost(FocusEvent e)
          {
            try
            {
              timeField.commitEdit();
            }
            catch (ParseException e1)
            {
              e1.printStackTrace();
            }
            switch (saveState)
            {
            case Save0:
              stateModel.setState1time(timeField.getValue().toString());
              break;
            case Save1:
              stateModel.setState2time(timeField.getValue().toString());
              break;
            case Save2:
              stateModel.setState3time(timeField.getValue().toString());
              break;
            case Save3:
              stateModel.setState4time(timeField.getValue().toString());
              break;
            default:
              break;            
            }           
          }
        });
      timeField.setPreferredSize(new Dimension(55, 20));
    }
    return timeField;
  }

  private JButton getRunButton()
  {
    if (runButton == null)
    {
      runButton = new JButton("Run");
      runButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            model.runSnapshotInVice(saveState);
          }
        });
    }
    return runButton;
  }
}
