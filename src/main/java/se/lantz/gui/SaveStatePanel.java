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
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.manager.SavedStatesManager;
import se.lantz.model.MainViewModel;
import se.lantz.model.SavedStatesModel;
import se.lantz.model.SavedStatesModel.SAVESTATE;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileDrop;
import se.lantz.util.FileManager;

public class SaveStatePanel extends JPanel
{
  private static final String SNAPSHOT_DIR_PROPERTY = "snapshotDir";
  private static final Logger logger = LoggerFactory.getLogger(SaveStatePanel.class);
  private JLabel screenshotLabel;
  private JTextField snapshotTextField;
  private MainViewModel model;
  private SavedStatesModel stateModel;
  private JButton snapshotButton;
  private BufferedImage currentScreenImage = null;
  private String currentGameFile = "";
  private ImageIcon missingSceenshotIcon = null;
  private ImageIcon noSceenshotIcon = null;
  private JLabel timeLabel;
  private JLabel snapshotLabel;
  private JFormattedTextField timeField;
  private JButton runButton;
  private boolean gamesFileUpdated = false;
  private SAVESTATE saveState;
  private JSeparator separator;
  private JButton screenshotButton;

  private FileNameExtensionFilter imagefilter =
    new FileNameExtensionFilter("png, gif, jpeg, bmp", "png", "gif", "jpg", "jpeg", "bmp");
  private JButton deleteButton;

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
    gbc_screenshotLabel.insets = new Insets(1, 1, 0, 5);
    gbc_screenshotLabel.gridx = 0;
    gbc_screenshotLabel.gridy = 0;
    add(getScreenshotLabel(), gbc_screenshotLabel);
    GridBagConstraints gbc_timeTextField = new GridBagConstraints();
    gbc_timeTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_timeTextField.anchor = GridBagConstraints.NORTHWEST;
    gbc_timeTextField.weighty = 1.0;
    gbc_timeTextField.insets = new Insets(0, 0, 5, 5);
    gbc_timeTextField.gridx = 4;
    gbc_timeTextField.gridy = 1;
    add(getTimeField(), gbc_timeTextField);
    GridBagConstraints gbc_snapshotTextField = new GridBagConstraints();
    gbc_snapshotTextField.gridwidth = 2;
    gbc_snapshotTextField.weightx = 1.0;
    gbc_snapshotTextField.anchor = GridBagConstraints.NORTHWEST;
    gbc_snapshotTextField.insets = new Insets(0, 0, 5, 5);
    gbc_snapshotTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_snapshotTextField.gridx = 1;
    gbc_snapshotTextField.gridy = 1;
    add(getSnapshotTextField(), gbc_snapshotTextField);
    GridBagConstraints gbc_snapshotButton = new GridBagConstraints();
    gbc_snapshotButton.insets = new Insets(-1, 0, 5, 5);
    gbc_snapshotButton.anchor = GridBagConstraints.NORTHEAST;
    gbc_snapshotButton.gridx = 3;
    gbc_snapshotButton.gridy = 1;
    add(getGameButton(), gbc_snapshotButton);
    GridBagConstraints gbc_timeLabel = new GridBagConstraints();
    gbc_timeLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_timeLabel.insets = new Insets(5, 5, 5, 0);
    gbc_timeLabel.gridx = 4;
    gbc_timeLabel.gridy = 0;
    add(getTimeLabel(), gbc_timeLabel);
    GridBagConstraints gbc_snapshotLabel = new GridBagConstraints();
    gbc_snapshotLabel.gridwidth = 2;
    gbc_snapshotLabel.weightx = 1.0;
    gbc_snapshotLabel.insets = new Insets(5, 0, 5, 5);
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
    GridBagConstraints gbc_deleteButton = new GridBagConstraints();
    gbc_deleteButton.gridwidth = 2;
    gbc_deleteButton.anchor = GridBagConstraints.SOUTHEAST;
    gbc_deleteButton.insets = new Insets(0, 0, 0, 5);
    gbc_deleteButton.gridx = 2;
    gbc_deleteButton.gridy = 2;
    add(getDeleteButton(), gbc_deleteButton);
    GridBagConstraints gbc_runButton = new GridBagConstraints();
    gbc_runButton.insets = new Insets(0, 0, 0, 5);
    gbc_runButton.weighty = 1.0;
    gbc_runButton.anchor = GridBagConstraints.SOUTHEAST;
    gbc_runButton.gridx = 4;
    gbc_runButton.gridy = 2;
    add(getRunButton(), gbc_runButton);
    if (saveState != SAVESTATE.Save3)
    {
      GridBagConstraints gbc_separator = new GridBagConstraints();
      gbc_separator.fill = GridBagConstraints.HORIZONTAL;
      gbc_separator.insets = new Insets(5, 5, 0, 5);
      gbc_separator.gridwidth = 5;
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
    reloadScreen();
    getRunButton().setEnabled(!getSnapshotTextField().getText().isEmpty());
    getDeleteButton().setEnabled(!getSnapshotTextField().getText().isEmpty());
    getTimeField().setEnabled(!getSnapshotTextField().getText().isEmpty());
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
      if (!screen1Image.equals(currentScreenImage))
      {
        logger.debug("SETTING SCREEN 1 IMAGE");
        Image newImage = screen1Image.getScaledInstance(130, 82, Image.SCALE_SMOOTH);
        getScreenshotLabel().setIcon(new ImageIcon(newImage));
        currentScreenImage = screen1Image;
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
        if (getSnapshotTextField().getText().isEmpty())
        {
          getScreenshotLabel().setIcon(getMissingScreenshotImageIcon());
        }
        else
        {
          getScreenshotLabel().setIcon(getNoScreenshotImageIcon());
        }
      }
      else if (!model.getInfoModel().getGamesFile().equals(currentGameFile))
      {
        currentScreenImage = loadScreen(modelScreenFile, getScreenshotLabel());
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
      String fileName = SavedStatesManager.getGameFolderName(model.getInfoModel().getGamesFile());
      logger.debug(fileName.toString());

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
        screenLabel.setIcon(getNoScreenshotImageIcon());
      }
    }
    else
    {
      screenLabel.setIcon(getMissingScreenshotImageIcon());
    }
    return image;
  }

  private ImageIcon getNoScreenshotImageIcon()
  {
    if (noSceenshotIcon == null)
    {
      BufferedImage image = null;
      try
      {
        image = ImageIO.read(FileManager.class.getResource("/se/lantz/NoScreenSaveSlot.png"));
        Image newImage = image.getScaledInstance(130, 82, Image.SCALE_SMOOTH);
        noSceenshotIcon = new ImageIcon(newImage);
      }
      catch (IOException e)
      {
        logger.error("can't read missing icon", e);

      }
    }
    return noSceenshotIcon;
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
            setScreenshotFileToModel(files);
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
      snapshotTextField.setPreferredSize(new Dimension(30, 20));
      new FileDrop(snapshotTextField, new FileDrop.Listener()
        {
          public void filesDropped(java.io.File[] files)
          {
            if (files.length > 0)
            {
              PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{vsf,vsz}");
              if (!matcher.matches(files[0].toPath()))
              {
                JOptionPane.showMessageDialog(getSnapshotTextField(),
                                              "Invalid file format, it must be a Vice snapshot file (vsf or vsz)",
                                              "Snapshot file",
                                              JOptionPane.ERROR_MESSAGE);
              }
              else
              {
                gamesFileUpdated = true;
                setSnapshotFileToModel(files[0]);
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
    fileChooser.setDialogTitle("Select a Vice snapsot file for " + model.getInfoModel().getTitle());
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    String gameDir = FileManager.getConfiguredProperties().getProperty(SNAPSHOT_DIR_PROPERTY);
    if (gameDir == null)
    {
      gameDir = ".";
    }
    fileChooser.setCurrentDirectory(new File(gameDir));

    FileNameExtensionFilter vicefilter = new FileNameExtensionFilter("Vice snapshot (vsf, vsz)", "vsf", "vsz");
    fileChooser.addChoosableFileFilter(vicefilter);
    fileChooser.setFileFilter(vicefilter);
    int value = fileChooser.showOpenDialog(MainWindow.getInstance());
    if (value == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = fileChooser.getSelectedFile();
      FileManager.getConfiguredProperties().put(SNAPSHOT_DIR_PROPERTY, selectedFile.toPath().getParent().toString());
      gamesFileUpdated = true;
      setSnapshotFileToModel(selectedFile);
    }
  }

  private void setSnapshotFileToModel(File file)
  {
    switch (saveState)
    {
    case Save0:
      stateModel.setState1File(SavedStatesManager.VSZ0);
      stateModel.setState1Path(file.toPath());
      break;
    case Save1:
      stateModel.setState2File(SavedStatesManager.VSZ1);
      stateModel.setState2Path(file.toPath());
      break;
    case Save2:
      stateModel.setState3File(SavedStatesManager.VSZ2);
      stateModel.setState3Path(file.toPath());
      break;
    case Save3:
      stateModel.setState4File(SavedStatesManager.VSZ3);
      stateModel.setState4Path(file.toPath());
      break;
    default:
      break;
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
          /**
           * Handles increase/decrease of time value with arrow buttons
           * 
           * @param e keyevent
           */
          @Override
          public void keyPressed(KeyEvent e)
          {
            if (e.getKeyCode() == KeyEvent.VK_UP)
            {
              increaseTime(timeField.getCaretPosition());
            }
            else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            {
              decreaseTime(timeField.getCaretPosition());
            }
          }

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
              ExceptionHandler.handleException(e1, "");
            }
            setTimeToModel();
          }
        });
      timeField.setPreferredSize(new Dimension(55, 20));
      timeField.setMinimumSize(new Dimension(55, 20));
    }
    return timeField;
  }

  private void increaseTime(int caretPosition)
  {
    String currentTime = timeField.getText().toString();
    String[] splitTime = currentTime.split(":");
    if (caretPosition < 2)
    {
      int newValue = Integer.parseInt(splitTime[0]) + 1;
      if (newValue < 10)
      {
        splitTime[0] = "0" + Integer.toString(newValue);
      }
      else if (newValue < 100)
      {
        splitTime[0] = Integer.toString(newValue);
      }
    }
    else if (caretPosition < 6)
    {
      int newValue = Integer.parseInt(splitTime[1]) + 1;
      if (newValue < 10)
      {
        splitTime[1] = "0" + Integer.toString(newValue);
      }
      else if (newValue < 60)
      {
        splitTime[1] = Integer.toString(newValue);
      }
    }
    else if (caretPosition < 9)
    {
      int newValue = Integer.parseInt(splitTime[2]) + 1;
      if (newValue < 10)
      {
        splitTime[2] = "0" + Integer.toString(newValue);
      }
      else if (newValue < 60)
      {
        splitTime[2] = Integer.toString(newValue);
      }
    }

    timeField.setText(splitTime[0] + ":" + splitTime[1] + ":" + splitTime[2]);
    timeField.setCaretPosition(caretPosition);
  }

  private void decreaseTime(int caretPosition)
  {
    String currentTime = timeField.getText().toString();
    String[] splitTime = currentTime.split(":");
    if (caretPosition < 2)
    {
      int newValue = Integer.parseInt(splitTime[0]) - 1;
      if (newValue < 10)
      {
        splitTime[0] = "0" + Integer.toString(newValue);
      }
      else if (newValue > 0)
      {
        splitTime[0] = Integer.toString(newValue);
      }
    }
    else if (caretPosition < 6)
    {
      int newValue = Integer.parseInt(splitTime[1]) - 1;
      if (newValue < 10)
      {
        splitTime[1] = "0" + Integer.toString(newValue);
      }
      else if (newValue > 0)
      {
        splitTime[1] = Integer.toString(newValue);
      }
    }
    else if (caretPosition < 9)
    {
      int newValue = Integer.parseInt(splitTime[2]) - 1;
      if (newValue < 10)
      {
        splitTime[2] = "0" + Integer.toString(newValue);
      }
      else if (newValue > 0)
      {
        splitTime[2] = Integer.toString(newValue);
      }
    }

    timeField.setText(splitTime[0] + ":" + splitTime[1] + ":" + splitTime[2]);
    timeField.setCaretPosition(caretPosition);
  }

  private void setTimeToModel()
  {
    switch (saveState)
    {
    case Save0:
      stateModel.setState1time(timeField.getText().toString());
      break;
    case Save1:
      stateModel.setState2time(timeField.getText().toString());
      break;
    case Save2:
      stateModel.setState3time(timeField.getText().toString());
      break;
    case Save3:
      stateModel.setState4time(timeField.getText().toString());
      break;
    default:
      break;
    }
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
            selectScreenshotFile();
          }
        });
      screenshotButton.setMargin(new Insets(1, 3, 1, 3));
    }
    return screenshotButton;
  }

  private void selectScreenshotFile()
  {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select screenshot for " + model.getInfoModel().getTitle() + " snapshot");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    String screensDir = FileManager.getConfiguredProperties().getProperty(SNAPSHOT_DIR_PROPERTY);
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
      FileManager.getConfiguredProperties().put(SNAPSHOT_DIR_PROPERTY, selectedFile.toPath().getParent().toString());
      setScreenshotFileToModel(new File[] { selectedFile });
    }
  }

  private void setScreenshotFileToModel(File[] files)
  {
    switch (saveState)
    {
    case Save0:
      stateModel.setState1PngImage(handleScreenFileDrop(files, getScreenshotLabel()));
      break;
    case Save1:
      stateModel.setState2PngImage(handleScreenFileDrop(files, getScreenshotLabel()));
      break;
    case Save2:
      stateModel.setState3PngImage(handleScreenFileDrop(files, getScreenshotLabel()));
      break;
    case Save3:
      stateModel.setState4PngImage(handleScreenFileDrop(files, getScreenshotLabel()));
      break;
    default:
      break;
    }
  }

  private BufferedImage handleScreenFileDrop(File[] files, JLabel imageLabel)
  {
    BufferedImage returnImage = null;
    if (files.length > 0)
    {
      try
      {
        returnImage = ImageIO.read(files[0]);
        if (returnImage == null)
        {
          JOptionPane.showMessageDialog(this,
                                        "Invalid file format, it must be a png, gif, jpeg or bmp file.",
                                        "Screenshot file",
                                        JOptionPane.ERROR_MESSAGE);
          return null;
        }
        Image newImage = returnImage.getScaledInstance(130, 82, Image.SCALE_SMOOTH);
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

  void commitEdits()
  {
    try
    {
      timeField.commitEdit();
    }
    catch (ParseException e1)
    {
      ExceptionHandler.handleException(e1, "");
    }
    setTimeToModel();
  }

  private JButton getDeleteButton()
  {
    if (deleteButton == null)
    {
      deleteButton = new JButton("Delete");
      deleteButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            int value = JOptionPane
              .showConfirmDialog(SaveStatePanel.this,
                                 "Are you sure you want to delete the saved state?\nIt will be deleted when you save the changes for the current game.",
                                 "Delete saved state",
                                 JOptionPane.YES_NO_OPTION);
            if (value == JOptionPane.YES_OPTION)
            {
              gamesFileUpdated = false;
              switch (saveState)
              {
              case Save0:
                stateModel.setState1Deleted(true);
                break;
              case Save1:
                stateModel.setState2Deleted(true);
                break;
              case Save2:
                stateModel.setState3Deleted(true);
                break;
              case Save3:
                stateModel.setState4Deleted(true);
                break;
              default:
                break;
              }
            }
          }
        });
    }
    return deleteButton;
  }
  
  void resetCurrentGameReference()
  {
    this.currentGameFile = "";
  }
}
