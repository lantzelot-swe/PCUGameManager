package se.lantz.gui.exports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.lantz.gui.SelectDirPanel;
import se.lantz.gui.SelectDirPanel.Mode;

public class OutputDirPanel extends JPanel
{
  private JLabel outputDirLabel;
  private SelectDirPanel selectDirPanel;
  private JCheckBox deleteCheckBox;
  private boolean carouselMode;
  
  public OutputDirPanel(boolean carouselMode) {
    this.carouselMode = carouselMode;
    GridBagLayout gbl_outputDirPanel = new GridBagLayout();
    gbl_outputDirPanel.columnWidths = new int[] { 0, 0 };
    gbl_outputDirPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
    gbl_outputDirPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_outputDirPanel.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
    setLayout(gbl_outputDirPanel);
    GridBagConstraints gbc_outputDirLabel = new GridBagConstraints();
    gbc_outputDirLabel.anchor = GridBagConstraints.WEST;
    gbc_outputDirLabel.insets = new Insets(0, 10, 0, 0);
    gbc_outputDirLabel.gridx = 0;
    gbc_outputDirLabel.gridy = 0;
    add(getOutputDirLabel(), gbc_outputDirLabel);
    GridBagConstraints gbc_selectDirPanel = new GridBagConstraints();
    gbc_selectDirPanel.insets = new Insets(0, 5, 5, 5);
    gbc_selectDirPanel.fill = GridBagConstraints.BOTH;
    gbc_selectDirPanel.gridx = 0;
    gbc_selectDirPanel.gridy = 1;
    add(getSelectDirPanel(), gbc_selectDirPanel);
    GridBagConstraints gbc_deleteCheckBox = new GridBagConstraints();
    gbc_deleteCheckBox.insets = new Insets(0, 10, 0, 0);
    gbc_deleteCheckBox.anchor = GridBagConstraints.WEST;
    gbc_deleteCheckBox.gridx = 0;
    gbc_deleteCheckBox.gridy = 2;
    add(getDeleteCheckBox(), gbc_deleteCheckBox);
  }
  
  private JLabel getOutputDirLabel()
  {
    if (outputDirLabel == null)
    {
      outputDirLabel = new JLabel("Select target folder to export to:");
    }
    return outputDirLabel;
  }

  SelectDirPanel getSelectDirPanel()
  {
    if (selectDirPanel == null)
    {
      selectDirPanel = new SelectDirPanel(carouselMode ? Mode.CAROUSEL_EXPORT: Mode.FILELOADER_EXPORT);
    }
    return selectDirPanel;
  }

  JCheckBox getDeleteCheckBox()
  {
    if (deleteCheckBox == null)
    {
      deleteCheckBox = new JCheckBox("Delete existing games in the target folder before exporting");
      deleteCheckBox.setSelected(true);
    }
    return deleteCheckBox;
  }

  File getTargetDirectory()
  {
    return getSelectDirPanel().getTargetDirectory();
  }

  boolean deleteBeforeExport()
  {
    return getDeleteCheckBox().isSelected();
  }
}
