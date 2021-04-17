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

  public ImportOptionsPanel()
  {
    this(true);
  }

  public ImportOptionsPanel(boolean isCarouselImport)
  {
    this.isCarouselImport = isCarouselImport;
    GridBagLayout gridBagLayout = new GridBagLayout();
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
    }
    else
    {
      add(getGbOptionsPanel(), gbc_selectDirPanel);
    }
    GridBagConstraints gbc_infoLabel = new GridBagConstraints();
    gbc_infoLabel.weightx = 1.0;
    gbc_infoLabel.insets = new Insets(15, 10, 5, 10);
    gbc_infoLabel.gridx = 0;
    gbc_infoLabel.gridy = 2;
    add(getInfoLabel(), gbc_infoLabel);
    GridBagConstraints gbc_selectionPanel = new GridBagConstraints();
    gbc_selectionPanel.weightx = 1.0;
    gbc_selectionPanel.insets = new Insets(0, 0, 5, 0);
    gbc_selectionPanel.gridx = 0;
    gbc_selectionPanel.gridy = 3;
    add(getSelectionPanel(), gbc_selectionPanel);

    GridBagConstraints gbc_matchLabel = new GridBagConstraints();
    gbc_matchLabel.insets = new Insets(5, 10, 15, 10);
    gbc_matchLabel.anchor = GridBagConstraints.NORTH;
    gbc_matchLabel.weighty = 1.0;
    gbc_matchLabel.weightx = 1.0;
    gbc_matchLabel.gridx = 0;
    gbc_matchLabel.gridy = 4;
    add(getMatchLabel(), gbc_matchLabel);
    GridBagConstraints gbc_selectDirLabel = new GridBagConstraints();
    gbc_selectDirLabel.weightx = 1.0;
    gbc_selectDirLabel.insets = new Insets(10, 10, 0, 10);
    gbc_selectDirLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_selectDirLabel.gridx = 0;
    gbc_selectDirLabel.gridy = 0;
    add(getSelectDirLabel(), gbc_selectDirLabel);
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
      skipRadioButton.setSelected(true);
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
      switch (getFavoriteComboBox().getSelectedItem().toString())
      {
      case "Favorite 1":
        returnValue = 1;
        break;
      case "Favorite 2":
        returnValue = 2;
        break;
      case "Favorite 3":
        returnValue = 3;
        break;
      case "Favorite 4":
        returnValue = 4;
        break;
      case "Favorite 5":
        returnValue = 5;
        break;
      default:
        break;
      }
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

  private GameBaseOptionsPanel getGbOptionsPanel()
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
        isCarouselImport ? "Select a directory containing a game carousel:" : "Select a gamebase database file (.mdb):";
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
      gbc_addRadioButton.gridwidth = 2;
      gbc_addRadioButton.anchor = GridBagConstraints.WEST;
      gbc_addRadioButton.insets = new Insets(0, 5, 10, 0);
      gbc_addRadioButton.gridx = 0;
      gbc_addRadioButton.gridy = 2;
      selectionPanel.add(getAddRadioButton(), gbc_addRadioButton);
      GridBagConstraints gbc_favoriteCheckBox = new GridBagConstraints();
      gbc_favoriteCheckBox.weighty = 1.0;
      gbc_favoriteCheckBox.anchor = GridBagConstraints.NORTHWEST;
      gbc_favoriteCheckBox.insets = new Insets(5, 5, 5, 5);
      gbc_favoriteCheckBox.gridx = 0;
      gbc_favoriteCheckBox.gridy = 3;
      selectionPanel.add(getFavoriteCheckBox(), gbc_favoriteCheckBox);
      GridBagConstraints gbc_favoriteComboBox = new GridBagConstraints();
      gbc_favoriteComboBox.fill = GridBagConstraints.HORIZONTAL;
      gbc_favoriteComboBox.gridx = 1;
      gbc_favoriteComboBox.gridy = 3;
      selectionPanel.add(getFavoriteComboBox(), gbc_favoriteComboBox);
      GridBagConstraints gbc_viewTagCheckBox = new GridBagConstraints();
      gbc_viewTagCheckBox.anchor = GridBagConstraints.NORTHWEST;
      gbc_viewTagCheckBox.insets = new Insets(5, 5, 5, 5);
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
      favoriteComboModel.addElement("Favorite 1");
      favoriteComboModel.addElement("Favorite 2");
      favoriteComboModel.addElement("Favorite 3");
      favoriteComboModel.addElement("Favorite 4");
      favoriteComboModel.addElement("Favorite 5");
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
    }
    return viewTagTextField;
  }

  public String getViewTag()
  {
    return viewTagCheckBox.isSelected() ? viewTagTextField.getText() : "";
  }
}
