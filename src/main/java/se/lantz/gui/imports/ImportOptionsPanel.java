package se.lantz.gui.imports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import se.lantz.gamebase.GamebaseOptions;
import se.lantz.gui.SelectDirPanel;
import se.lantz.gui.SelectDirPanel.Mode;
import se.lantz.manager.ImportManager;
import se.lantz.util.FileManager;

public class ImportOptionsPanel extends JPanel
{
  private JLabel infoLabel;
  private JRadioButton overwriteRadioButton;
  private JRadioButton skipRadioButton;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JLabel matchLabel;
  private JCheckBox favoriteCheckBox;
  private SelectDirPanel selectDirPanel;
  private GameBaseOptionsPanel gbOptionsPanel;
  private JLabel selectDirLabel;
  private JRadioButton addRadioButton;
  private JPanel selectionPanel;
  private boolean isCarouselImport;
  private JComboBox<String> favoriteComboBox;
  private JCheckBox viewTagCheckBox;
  private JTextField viewTagTextField;
  private JPanel radioButtonPanel;
  private JRadioButton createGameViewButton;
  private JRadioButton noGameviewRadioButton;
  private final ButtonGroup gameViewGroup = new ButtonGroup();
  private JLabel viewTagInfoLabel;

  public ImportOptionsPanel()
  {
    this(true);
  }

  public ImportOptionsPanel(boolean isCarouselImport)
  {
    this.isCarouselImport = isCarouselImport;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0};
    gridBagLayout.columnWeights = new double[]{1.0};
    setLayout(gridBagLayout);
    GridBagConstraints gbc_selectDirPanel = new GridBagConstraints();
    gbc_selectDirPanel.weightx = 1.0;
    gbc_selectDirPanel.insets = new Insets(0, 5, 5, 10);
    gbc_selectDirPanel.fill = GridBagConstraints.BOTH;
    gbc_selectDirPanel.gridx = 0;
    gbc_selectDirPanel.gridy = 1;
    if (isCarouselImport)
    {
      add(getSelectDirPanel(), gbc_selectDirPanel);
      GridBagConstraints gbc_radioButtonPanel = new GridBagConstraints();
      gbc_radioButtonPanel.insets = new Insets(0, 10, 5, 10);
      gbc_radioButtonPanel.fill = GridBagConstraints.BOTH;
      gbc_radioButtonPanel.gridx = 0;
      gbc_radioButtonPanel.gridy = 2;
      add(getRadioButtonPanel(), gbc_radioButtonPanel);
    }
    else
    {
      add(getGbOptionsPanel(), gbc_selectDirPanel);
    }
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.insets = new Insets(15, 10, 5, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 3;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_selectionPanel = new GridBagConstraints();
    gbc_selectionPanel.weightx = 1.0;
    gbc_selectionPanel.insets = new Insets(0, 0, 5, 0);
    gbc_selectionPanel.gridx = 0;
    gbc_selectionPanel.gridy = 4;
    add(getSelectionPanel(), gbc_selectionPanel);

    GridBagConstraints gbc_matchLabel = new GridBagConstraints();
    gbc_matchLabel.insets = new Insets(5, 10, 15, 10);
    gbc_matchLabel.anchor = GridBagConstraints.NORTH;
    gbc_matchLabel.weighty = 1.0;
    gbc_matchLabel.weightx = 1.0;
    gbc_matchLabel.gridx = 0;
    gbc_matchLabel.gridy = 5;
    add(getMatchLabel(), gbc_matchLabel);
    GridBagConstraints gbc_selectDirLabel = new GridBagConstraints();
    gbc_selectDirLabel.weightx = 1.0;
    gbc_selectDirLabel.insets = new Insets(10, 10, 5, 10);
    gbc_selectDirLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_selectDirLabel.gridx = 0;
    gbc_selectDirLabel.gridy = 0;
    add(getSelectDirLabel(), gbc_selectDirLabel);
    if (isCarouselImport)
    {
      setStateForCreateViewRadioButton();
    }
  }

  private JLabel getInfoLabel()
  {
    if (infoLabel == null)
    {
      infoLabel = new JLabel("Select how to handle games where title matches an existing game in the database:");
    }
    return infoLabel;
  }

  private JRadioButton getOverwriteRadioButton()
  {
    if (overwriteRadioButton == null)
    {
      overwriteRadioButton = new JRadioButton("Overwrite with imported game");
      buttonGroup.add(overwriteRadioButton);
      if (!this.isCarouselImport)
      {
        //Too complex to overwrite several duplicates, don't allow it.
        overwriteRadioButton.setVisible(false);
      }
    }
    return overwriteRadioButton;
  }

  private JRadioButton getSkipRadioButton()
  {
    if (skipRadioButton == null)
    {
      skipRadioButton = new JRadioButton("Skip game from import");
      buttonGroup.add(skipRadioButton);
    }
    return skipRadioButton;
  }

  public ImportManager.Options getSelectedOption()
  {
    ImportManager.Options returnValue = ImportManager.Options.SKIP;
    if (getOverwriteRadioButton().isSelected())
    {
      returnValue = ImportManager.Options.OVERWRITE;
    }
    else if (getAddRadioButton().isSelected())
    {
      returnValue = ImportManager.Options.ADD;
    }
    return returnValue;
  }

  public int getMarkAsFavorite()
  {
    int returnValue = 0;
    if (getFavoriteCheckBox().isSelected())
    {
      returnValue = getFavoriteComboBox().getSelectedIndex()+1;
    }
    return returnValue;
  }

  private JLabel getMatchLabel()
  {
    if (matchLabel == null)
    {
      matchLabel = new JLabel("Games are matched by title, case insensitive.");
    }
    return matchLabel;
  }

  private JCheckBox getFavoriteCheckBox()
  {
    if (favoriteCheckBox == null)
    {
      favoriteCheckBox = new JCheckBox("Mark imported games as");
      favoriteCheckBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            getFavoriteComboBox().setEnabled(favoriteCheckBox.isSelected());
          }
        });
    }
    return favoriteCheckBox;
  }

  private SelectDirPanel getSelectDirPanel()
  {
    if (selectDirPanel == null)
    {
      selectDirPanel = new SelectDirPanel(Mode.CAROUSEL_IMPORT);
    }
    return selectDirPanel;
  }

  protected GameBaseOptionsPanel getGbOptionsPanel()
  {
    if (gbOptionsPanel == null)
    {
      gbOptionsPanel = new GameBaseOptionsPanel();
    }
    return gbOptionsPanel;
  }

  private JLabel getSelectDirLabel()
  {
    if (selectDirLabel == null)
    {
      String text =
        isCarouselImport ? "Select a directory containing one or several game carousels:" : "Select a gamebase database file (.mdb):";
      selectDirLabel = new JLabel(text);
    }
    return selectDirLabel;
  }

  public Path getImportDirectory()
  {
    return selectDirPanel.getTargetDirectory().toPath();
  }

  private JRadioButton getAddRadioButton()
  {
    if (addRadioButton == null)
    {
      addRadioButton = new JRadioButton("Add new duplicate game entry");
      addRadioButton.setSelected(true);
      buttonGroup.add(addRadioButton);
    }
    return addRadioButton;
  }

  private JPanel getSelectionPanel()
  {
    if (selectionPanel == null)
    {
      selectionPanel = new JPanel();
      GridBagLayout gbl_selectionPanel = new GridBagLayout();
      gbl_selectionPanel.columnWeights = new double[] { 1.0, 0.0 };
      selectionPanel.setLayout(gbl_selectionPanel);
      GridBagConstraints gbc_skipRadioButton = new GridBagConstraints();
      gbc_skipRadioButton.gridwidth = 2;
      gbc_skipRadioButton.anchor = GridBagConstraints.WEST;
      gbc_skipRadioButton.weightx = 1.0;
      gbc_skipRadioButton.insets = new Insets(0, 5, 0, 0);
      gbc_skipRadioButton.gridx = 0;
      gbc_skipRadioButton.gridy = 0;
      selectionPanel.add(getSkipRadioButton(), gbc_skipRadioButton);
      GridBagConstraints gbc_overwriteRadioButton = new GridBagConstraints();
      gbc_overwriteRadioButton.gridwidth = 2;
      gbc_overwriteRadioButton.anchor = GridBagConstraints.WEST;
      gbc_overwriteRadioButton.weightx = 1.0;
      gbc_overwriteRadioButton.insets = new Insets(0, 5, 0, 0);
      gbc_overwriteRadioButton.gridx = 0;
      gbc_overwriteRadioButton.gridy = 1;
      selectionPanel.add(getOverwriteRadioButton(), gbc_overwriteRadioButton);
      GridBagConstraints gbc_addRadioButton = new GridBagConstraints();
      gbc_addRadioButton.weighty = 1.0;
      gbc_addRadioButton.gridwidth = 2;
      gbc_addRadioButton.anchor = GridBagConstraints.NORTHWEST;
      gbc_addRadioButton.insets = new Insets(0, 5, 10, 0);
      gbc_addRadioButton.gridx = 0;
      gbc_addRadioButton.gridy = 2;
      selectionPanel.add(getAddRadioButton(), gbc_addRadioButton);
      GridBagConstraints gbc_viewTagInfoLabel = new GridBagConstraints();
      gbc_viewTagInfoLabel.gridwidth = 2;
      gbc_viewTagInfoLabel.fill = GridBagConstraints.HORIZONTAL;
      gbc_viewTagInfoLabel.insets = new Insets(0, 5, 5, 5);
      gbc_viewTagInfoLabel.gridx = 0;
      gbc_viewTagInfoLabel.gridy = 5;
      selectionPanel.add(getViewTagInfoLabel(), gbc_viewTagInfoLabel);
      GridBagConstraints gbc_favoriteCheckBox = new GridBagConstraints();
      gbc_favoriteCheckBox.anchor = GridBagConstraints.WEST;
      gbc_favoriteCheckBox.insets = new Insets(0, 5, 5, 5);
      gbc_favoriteCheckBox.gridx = 0;
      gbc_favoriteCheckBox.gridy = 3;
      selectionPanel.add(getFavoriteCheckBox(), gbc_favoriteCheckBox);
      GridBagConstraints gbc_favoriteComboBox = new GridBagConstraints();
      gbc_favoriteComboBox.insets = new Insets(0, 0, 5, 0);
      gbc_favoriteComboBox.fill = GridBagConstraints.HORIZONTAL;
      gbc_favoriteComboBox.gridx = 1;
      gbc_favoriteComboBox.gridy = 3;
      selectionPanel.add(getFavoriteComboBox(), gbc_favoriteComboBox);
      GridBagConstraints gbc_viewTagCheckBox = new GridBagConstraints();
      gbc_viewTagCheckBox.anchor = GridBagConstraints.NORTHWEST;
      gbc_viewTagCheckBox.insets = new Insets(0, 5, 0, 5);
      gbc_viewTagCheckBox.gridx = 0;
      gbc_viewTagCheckBox.gridy = 4;
      selectionPanel.add(getViewTagCheckBox(), gbc_viewTagCheckBox);
      GridBagConstraints gbc_viewTagTextField = new GridBagConstraints();
      gbc_viewTagTextField.fill = GridBagConstraints.HORIZONTAL;
      gbc_viewTagTextField.gridx = 1;
      gbc_viewTagTextField.gridy = 4;
      selectionPanel.add(getViewTagTextField(), gbc_viewTagTextField);
    }
    return selectionPanel;
  }

  public GamebaseOptions getSelectedGbOptions()
  {
    return getGbOptionsPanel().getSelectedGbOptions();
  }

  private JComboBox<String> getFavoriteComboBox()
  {
    if (favoriteComboBox == null)
    {
      favoriteComboBox = new JComboBox<String>();
      DefaultComboBoxModel<String> favoriteComboModel = new DefaultComboBoxModel<>();
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(1));
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(2));
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(3));
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(4));
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(5));
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(6));
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(7));
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(8));
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(9));
      favoriteComboModel.addElement(FileManager.getConfiguredFavGameViewName(10));
      favoriteComboBox.setModel(favoriteComboModel);
      favoriteComboBox.setEnabled(false);
    }
    return favoriteComboBox;
  }

  private JCheckBox getViewTagCheckBox()
  {
    if (viewTagCheckBox == null)
    {
      viewTagCheckBox = new JCheckBox("Add view tag to games");
      viewTagCheckBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            getViewTagTextField().setEnabled(viewTagCheckBox.isSelected());
            getViewTagInfoLabel().setText(viewTagCheckBox.isSelected() ? "(A new game view for the view tag is created)" : " ");
          }
        });
    }
    return viewTagCheckBox;
  }

  private JTextField getViewTagTextField()
  {
    if (viewTagTextField == null)
    {
      viewTagTextField = new JTextField();
      viewTagTextField.setColumns(10);
      viewTagTextField.setEnabled(false);
    }
    return viewTagTextField;
  }

  public String getViewTag()
  {
    return viewTagCheckBox.isSelected() ? viewTagTextField.getText() : "";
  }
  
  private void setStateForCreateViewRadioButton()
  {
    boolean gameViewSelected = getCreateGameViewButton().isSelected();
    getViewTagCheckBox().setEnabled(!gameViewSelected);
    getFavoriteCheckBox().setEnabled(!gameViewSelected);
    getViewTagInfoLabel().setEnabled(!gameViewSelected);
    getAddRadioButton().setEnabled(!gameViewSelected);
    getOverwriteRadioButton().setEnabled(!gameViewSelected);
    getSkipRadioButton().setEnabled(!gameViewSelected);
    getInfoLabel().setEnabled(!gameViewSelected);
    getMatchLabel().setEnabled(!gameViewSelected);
    getFavoriteComboBox().setEnabled(!gameViewSelected && getFavoriteCheckBox().isSelected());
    getViewTagTextField().setEnabled(getViewTagCheckBox().isSelected() && !gameViewSelected);
  }
  
  public boolean isCreateGameViews()
  {
    return getCreateGameViewButton().isSelected();
  }
  private JPanel getRadioButtonPanel() {
    if (radioButtonPanel == null) {
    	radioButtonPanel = new JPanel();
    	GridBagLayout gbl_radioButtonPanel = new GridBagLayout();
    	gbl_radioButtonPanel.columnWidths = new int[]{0, 0};
    	gbl_radioButtonPanel.rowHeights = new int[]{0, 0, 0};
    	gbl_radioButtonPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
    	gbl_radioButtonPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
    	radioButtonPanel.setLayout(gbl_radioButtonPanel);
    	GridBagConstraints gbc_createGameViewButton = new GridBagConstraints();
    	gbc_createGameViewButton.fill = GridBagConstraints.HORIZONTAL;
    	gbc_createGameViewButton.anchor = GridBagConstraints.WEST;
    	gbc_createGameViewButton.insets = new Insets(0, 0, 0, 0);
    	gbc_createGameViewButton.gridx = 0;
    	gbc_createGameViewButton.gridy = 0;
    	radioButtonPanel.add(getCreateGameViewButton(), gbc_createGameViewButton);
    	GridBagConstraints gbc_noGameviewRadioButton = new GridBagConstraints();
    	gbc_noGameviewRadioButton.anchor = GridBagConstraints.WEST;
    	gbc_noGameviewRadioButton.fill = GridBagConstraints.HORIZONTAL;
    	gbc_noGameviewRadioButton.gridx = 0;
    	gbc_noGameviewRadioButton.gridy = 1;
    	radioButtonPanel.add(getNoGameviewRadioButton(), gbc_noGameviewRadioButton);
    }
    return radioButtonPanel;
  }
  private JRadioButton getCreateGameViewButton() {
    if (createGameViewButton == null) {
    	createGameViewButton = new JRadioButton("Create a gameview for each imported folder");
    	gameViewGroup.add(createGameViewButton);
    	createGameViewButton.setSelected(true);
    	createGameViewButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setStateForCreateViewRadioButton();
        }
      });
    }
    return createGameViewButton;
  }
  private JRadioButton getNoGameviewRadioButton() {
    if (noGameviewRadioButton == null) {
    	noGameviewRadioButton = new JRadioButton("Use the following settings:");
    	gameViewGroup.add(noGameviewRadioButton);
    	noGameviewRadioButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setStateForCreateViewRadioButton();
        }
      });
    }
    return noGameviewRadioButton;
  }
  private JLabel getViewTagInfoLabel() {
    if (viewTagInfoLabel == null) {
    	viewTagInfoLabel = new JLabel(" ");
    }
    return viewTagInfoLabel;
  }
}
