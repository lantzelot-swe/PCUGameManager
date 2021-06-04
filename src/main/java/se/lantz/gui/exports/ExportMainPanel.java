package se.lantz.gui.exports;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import se.lantz.model.data.GameListData;
import se.lantz.model.data.GameView;

public class ExportMainPanel extends JPanel
{
  private JPanel optionPanel;
  private JLabel infoLabel;
  private JRadioButton viewRadioButton;
  private JRadioButton gamesRadioButton;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JPanel cardPanel;
  private ExportGameViewsSelectionPanel gameViewsSelectionPanel;
  private OutputDirPanel outputDirPanel;
  private JButton exportButton;
  private ExportGamesSelectionPanel exportGamesSelectionPanel;
  private CardLayout cardLayout = new CardLayout();
  private boolean carouselMode;

  public ExportMainPanel(JButton exportButton, boolean carouselMode)
  {
    this.exportButton = exportButton;
    this.carouselMode = carouselMode;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_optionPanel = new GridBagConstraints();
    gbc_optionPanel.insets = new Insets(0, 0, 5, 0);
    gbc_optionPanel.fill = GridBagConstraints.BOTH;
    gbc_optionPanel.weightx = 1.0;
    gbc_optionPanel.anchor = GridBagConstraints.NORTH;
    gbc_optionPanel.gridx = 0;
    gbc_optionPanel.gridy = 0;
    add(getOptionPanel(), gbc_optionPanel);
    GridBagConstraints gbc_cardPanel = new GridBagConstraints();
    gbc_cardPanel.weighty = 1.0;
    gbc_cardPanel.insets = new Insets(0, 0, 5, 0);
    gbc_cardPanel.fill = GridBagConstraints.BOTH;
    gbc_cardPanel.weightx = 1.0;
    gbc_cardPanel.gridx = 0;
    gbc_cardPanel.gridy = 1;
    add(getCardPanel(), gbc_cardPanel);
    GridBagConstraints gbc_outputDirPanel = new GridBagConstraints();
    gbc_outputDirPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_outputDirPanel.weightx = 1.0;
    gbc_outputDirPanel.gridx = 0;
    gbc_outputDirPanel.gridy = 2;
    add(getOutputDirPanel(), gbc_outputDirPanel);
  }

  private JPanel getOptionPanel()
  {
    if (optionPanel == null)
    {
      optionPanel = new JPanel();
      GridBagLayout gbl_optionPanel = new GridBagLayout();
      optionPanel.setLayout(gbl_optionPanel);
      GridBagConstraints gbc_infoLabel = new GridBagConstraints();
      gbc_infoLabel.weightx = 1.0;
      gbc_infoLabel.fill = GridBagConstraints.HORIZONTAL;
      gbc_infoLabel.insets = new Insets(10, 10, 0, 10);
      gbc_infoLabel.gridx = 0;
      gbc_infoLabel.gridy = 0;
      optionPanel.add(getInfoLabel(), gbc_infoLabel);
      GridBagConstraints gbc_viewRadioButton = new GridBagConstraints();
      gbc_viewRadioButton.weightx = 1.0;
      gbc_viewRadioButton.insets = new Insets(10, 5, 0, 0);
      gbc_viewRadioButton.anchor = GridBagConstraints.WEST;
      gbc_viewRadioButton.gridx = 0;
      gbc_viewRadioButton.gridy = 1;
      optionPanel.add(getViewRadioButton(), gbc_viewRadioButton);
      GridBagConstraints gbc_gamesRadioButton = new GridBagConstraints();
      gbc_gamesRadioButton.insets = new Insets(0, 5, 0, 0);
      gbc_gamesRadioButton.weightx = 1.0;
      gbc_gamesRadioButton.anchor = GridBagConstraints.WEST;
      gbc_gamesRadioButton.gridx = 0;
      gbc_gamesRadioButton.gridy = 2;
      optionPanel.add(getGamesRadioButton(), gbc_gamesRadioButton);
    }
    return optionPanel;
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      StringBuilder infoBuilder = new StringBuilder();
      if (this.carouselMode)
      {
        infoBuilder.append("<html>When exporting to Carousel a TSG file is generated and the game files are copied into the selected target folder.<br>");
        infoBuilder.append("Copy the folder into Project Carousel USB on a USB stick.</html>");
      }
      else
      {
        infoBuilder.append("<html>When exporting to File loader a CJM file is generated and the game file is copied into the target folder. ");
        infoBuilder.append("The files are named after the game title.<br>");
        infoBuilder.append("Copy the folder to a USB stick and load the games using the File loader.</html>");
      }
      infoLabel =
        new JLabel(infoBuilder.toString());
    }
    return infoLabel;
  }

  private JRadioButton getViewRadioButton()
  {
    if (viewRadioButton == null)
    {
      viewRadioButton = new JRadioButton("Export gamelist views. A subfolder is created in the target folder for each list.");
      viewRadioButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            showGameViewSelectionPanel();
          }
        });
      buttonGroup.add(viewRadioButton);
      viewRadioButton.setSelected(true);
    }
    return viewRadioButton;
  }

  private JRadioButton getGamesRadioButton()
  {
    if (gamesRadioButton == null)
    {
      gamesRadioButton = new JRadioButton("Export individual games to the target folder");
      gamesRadioButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            showGameSelectionPanel();
          }
        });
      buttonGroup.add(gamesRadioButton);
    }
    return gamesRadioButton;
  }

  private JPanel getCardPanel()
  {
    if (cardPanel == null)
    {
      cardPanel = new JPanel();
      cardPanel.setLayout(cardLayout);
      cardPanel.add(getGameViewsSelectionPanel(), "views");
      cardPanel.add(getGamesSelectionPanel(), "games");
    }
    return cardPanel;
  }

  private void showGameViewSelectionPanel()
  {
    cardLayout.show(getCardPanel(), "views");
    getGameViewsSelectionPanel().setExportButtonEnablement();
    
  }

  private void showGameSelectionPanel()
  {
    cardLayout.show(getCardPanel(), "games");
    getGamesSelectionPanel().setExportButtonEnablement();
  }

  private ExportGameViewsSelectionPanel getGameViewsSelectionPanel()
  {
    if (gameViewsSelectionPanel == null)
    {
      gameViewsSelectionPanel = new ExportGameViewsSelectionPanel(exportButton);
    }
    return gameViewsSelectionPanel;
  }

  private OutputDirPanel getOutputDirPanel()
  {
    if (outputDirPanel == null)
    {
      outputDirPanel = new OutputDirPanel(carouselMode);
    }
    return outputDirPanel;
  }

  File getTargetDirectory()
  {
    return getOutputDirPanel().getSelectDirPanel().getTargetDirectory();
  }

  boolean deleteBeforeExport()
  {
    return getOutputDirPanel().getDeleteCheckBox().isSelected();
  }

  List<GameListData> getSelectedGames()
  {
    return getGamesSelectionPanel().getSelectedGames();
  }

  List<GameView> getSelectedGameViews()
  {
    return getGameViewsSelectionPanel().getSelectedViews();
  }
  
  boolean isExportGameViews()
  {
    return getViewRadioButton().isSelected();
  }

  private ExportGamesSelectionPanel getGamesSelectionPanel()
  {
    if (exportGamesSelectionPanel == null)
    {
      exportGamesSelectionPanel = new ExportGamesSelectionPanel(exportButton);
    }
    return exportGamesSelectionPanel;
  }
}
