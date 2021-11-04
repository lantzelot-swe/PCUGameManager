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
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.InfoModel;
import se.lantz.model.MainViewModel;
import se.lantz.model.SavedStatesModel;
import se.lantz.model.SavedStatesModel.SAVESTATE;
import se.lantz.util.FileDrop;
import se.lantz.util.FileManager;

public class SaveStatePanel extends JPanel
{
  private static final Logger logger = LoggerFactory.getLogger(SaveStatePanel.class);
  private JLabel screenshotLabel;
  private JTextField snapshotTextField;
  private MainViewModel model;
  private SavedStatesModel stateModel;
  private JButton snapshotButton;
  private BufferedImage currentScreen1Image = null;
  private String currentGameFile = "";
  private ImageIcon missingSceenshotIcon = null;
  private JLabel timeLabel;
  private JLabel snapshotLabel;
  private JFormattedTextField timeField;
  private JButton runButton;
  private boolean gamesFileUpdated = false;
  private SAVESTATE saveState;
  private JSeparator separator;
  private JButton screenshotButton;

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
    gbc_screenshotLabel.gridheight = 3;
    gbc_screenshotLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_screenshotLabel.insets = new Insets(1, 1, 0, 1);
    gbc_screenshotLabel.gridx = 0;
    gbc_screenshotLabel.gridy = 0;
    add(getScreenshotLabel(), gbc_screenshotLabel);
    GridBagConstraints gbc_timeTextField = new GridBagConstraints();
    gbc_timeTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_timeTextField.anchor = GridBagConstraints.NORTHWEST;
    gbc_timeTextField.weighty = 1.0;
    gbc_timeTextField.insets = new Insets(0, 5, 5, 5);
    gbc_timeTextField.gridx = 3;
    gbc_timeTextField.gridy = 1;
    add(getTimeField(), gbc_timeTextField);
    GridBagConstraints gbc_snapshotTextField = new GridBagConstraints();
    gbc_snapshotTextField.weightx = 1.0;
    gbc_snapshotTextField.anchor = GridBagConstraints.NORTHWEST;
    gbc_snapshotTextField.insets = new Insets(0, 0, 5, 5);
    gbc_snapshotTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_snapshotTextField.gridx = 1;
    gbc_snapshotTextField.gridy = 1;
    add(getSnapshotTextField(), gbc_snapshotTextField);
    GridBagConstraints gbc_snapshotButton = new GridBagConstraints();
    gbc_snapshotButton.insets = new Insets(-1, 0, 5, 5);
    gbc_snapshotButton.anchor = GridBagConstraints.NORTHWEST;
    gbc_snapshotButton.gridx = 2;
    gbc_snapshotButton.gridy = 1;
    add(getGameButton(), gbc_snapshotButton);
    GridBagConstraints gbc_timeLabel = new GridBagConstraints();
    gbc_timeLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_timeLabel.insets = new Insets(5, 5, 0, 0);
    gbc_timeLabel.gridx = 3;
    gbc_timeLabel.gridy = 0;
    add(getTimeLabel(), gbc_timeLabel);
    GridBagConstraints gbc_snapshotLabel = new GridBagConstraints();
    gbc_snapshotLabel.insets = new Insets(5, 0, 0, 5);
    gbc_snapshotLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_snapshotLabel.gridx = 1;
    gbc_snapshotLabel.gridy = 0;
    add(getSnapshotLabel(), gbc_snapshotLabel);
    GridBagConstraints gbc_screenshotButton = new GridBagConstraints();
    gbc_screenshotButton.anchor = GridBagConstraints.SOUTHWEST;
    gbc_screenshotButton.insets = new Insets(0, 0, 0, 5);
    gbc_screenshotButton.gridx = 1;
    gbc_screenshotButton.gridy = 2;
    add(getScreenshotButton(), gbc_screenshotButton);
    GridBagConstraints gbc_runButton = new GridBagConstraints();
    gbc_runButton.insets = new Insets(0, 0, 0, 5);
    gbc_runButton.weighty = 1.0;
    gbc_runButton.anchor = GridBagConstraints.SOUTHEAST;
    gbc_runButton.gridx = 3;
    gbc_runButton.gridy = 2;
    add(getRunButton(), gbc_runButton);
    if (saveState != SAVESTATE.Save3)
    {
      GridBagConstraints gbc_separator = new GridBagConstraints();
      gbc_separator.fill = GridBagConstraints.HORIZONTAL;
      gbc_separator.insets = new Insets(5, 20, 0, 20);
      gbc_separator.gridwidth = 4;
      gbc_separator.gridx = 0;
      gbc_separator.gridy = 3;
      add(getSeparator(), gbc_separator);
    }

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
    getSnapshotTextField().setText(getSnapshotFileName());
    getTimeField().setText(getPlayTime());
    reloadScreen();
    getRunButton().setEnabled(!getSnapshotTextField().getText().isEmpty());
    getTimeField().setEnabled(!getSnapshotTextField().getText().isEmpty());
  }
  
  
  private String getPlayTime()
  {
    String returnValue = "";
    switch (saveState)
    {
    case Save0:
      returnValue = stateModel.getState1time();
      break;
    case Save1:
      returnValue = stateModel.getState2time();
      break;
    case Save2:
      returnValue = stateModel.getState3time();
      break;
    case Save3:
      returnValue = stateModel.getState4time();
      break;
    default:
      break;
    }
    return returnValue;
  }

  private String getSnapshotFileName()
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
        getScreenshotLabel().setIcon(getMissingScreenshotImageIcon());
      }
      else if (!model.getInfoModel().getGamesFile().equals(currentGameFile))
      {
        currentScreen1Image = loadScreen(modelScreenFile, getScreenshotLabel());
      }
      //Keep track of which game is selected
      currentGameFile = model.getInfoModel().getGamesFile();
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
        Image newImage = image.getScaledInstance(130, 82, Image.SCALE_SMOOTH);
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
        image = ImageIO.read(FileManager.class.getResource("/se/lantz/EmptySaveSlot.png"));
        Image newImage = image.getScaledInstance(130, 82, Image.SCALE_SMOOTH);
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
      new FileDrop(screenshotLabel, new FileDrop.Listener()
      {
        public void filesDropped(java.io.File[] files)
        {
          //TODO
//          if (files.length > 0)
//          {
//            gamesFileUpdated = true;
//            switch (saveState)
//            {
//            case Save0:
//              stateModel.setState1Path(files[0].toPath());
//              break;
//            case Save1:
//              stateModel.setState2Path(files[0].toPath());
//              break;
//            case Save2:
//              stateModel.setState3Path(files[0].toPath());
//              break;
//            case Save3:
//              stateModel.setState4Path(files[0].toPath());
//              break;
//            default:
//              break;
//            }
//          }
        }
      });
    }
    return screenshotLabel;
  }

  private JTextField getSnapshotTextField()
  {
    if (snapshotTextField == null)
    {
      snapshotTextField = new JTextField();
      snapshotTextField.setEditable(false);
      snapshotTextField.setPreferredSize(new Dimension(50, 20));
      new FileDrop(snapshotTextField, new FileDrop.Listener()
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
    return snapshotTextField;
  }

  private JButton getGameButton()
  {
    if (snapshotButton == null)
    {
      snapshotButton = new JButton("...");
      snapshotButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            selectSnapshotFile();
          }
        });
      snapshotButton.setMargin(new Insets(1, 3, 1, 3));
    }
    return snapshotButton;
  }

  private void selectSnapshotFile()
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

  private JLabel getSnapshotLabel()
  {
    if (snapshotLabel == null)
    {
      snapshotLabel = new JLabel("Vice snapshot");
    }
    return snapshotLabel;
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

  private JSeparator getSeparator()
  {
    if (separator == null)
    {
      separator = new JSeparator();
    }
    return separator;
  }

  private JButton getScreenshotButton()
  {
    if (screenshotButton == null)
    {
      screenshotButton = new JButton("...");
      screenshotButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            //TODO
          }
        });
      screenshotButton.setMargin(new Insets(1, 3, 1, 3));
    }
    return screenshotButton;
  }
}
