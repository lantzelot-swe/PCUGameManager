package se.lantz.gui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import se.lantz.model.InfoModel;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class TranslationPanel extends JPanel
{
  private static final String EN = "English (en)";
  private static final String DE = "German (de)";
  private static final String FR = "French (fr)";
  private static final String ES = "Spanish (es)";
  private static final String IT = "Italian (it)";
  private JLabel selectFromLabel;
  private JComboBox fromComboBox;
  private JLabel toLanguageLabel;
  private JPanel checkBoxPanel;
  private JCheckBox enCheckBox;
  private JCheckBox deCheckBox;
  private JCheckBox frCheckBox;
  private JCheckBox esCheckBox;
  private JCheckBox itCheckBox;
  private InfoModel model;

  public TranslationPanel(InfoModel model)
  {
    this.model = model;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_selectFromLabel = new GridBagConstraints();
    gbc_selectFromLabel.anchor = GridBagConstraints.WEST;
    gbc_selectFromLabel.insets = new Insets(10, 5, 5, 5);
    gbc_selectFromLabel.gridx = 0;
    gbc_selectFromLabel.gridy = 0;
    add(getSelectFromLabel(), gbc_selectFromLabel);
    GridBagConstraints gbc_fromComboBox = new GridBagConstraints();
    gbc_fromComboBox.anchor = GridBagConstraints.WEST;
    gbc_fromComboBox.insets = new Insets(10, 0, 5, 0);
    gbc_fromComboBox.gridx = 1;
    gbc_fromComboBox.gridy = 0;
    add(getFromComboBox(), gbc_fromComboBox);
    GridBagConstraints gbc_toLanguageLabel = new GridBagConstraints();
    gbc_toLanguageLabel.anchor = GridBagConstraints.WEST;
    gbc_toLanguageLabel.gridwidth = 2;
    gbc_toLanguageLabel.insets = new Insets(5, 5, 5, 0);
    gbc_toLanguageLabel.gridx = 0;
    gbc_toLanguageLabel.gridy = 1;
    add(getToLanguageLabel(), gbc_toLanguageLabel);
    GridBagConstraints gbc_checkBoxPanel = new GridBagConstraints();
    gbc_checkBoxPanel.fill = GridBagConstraints.BOTH;
    gbc_checkBoxPanel.gridwidth = 2;
    gbc_checkBoxPanel.weighty = 1.0;
    gbc_checkBoxPanel.weightx = 1.0;
    gbc_checkBoxPanel.insets = new Insets(0, 0, 0, 5);
    gbc_checkBoxPanel.gridx = 0;
    gbc_checkBoxPanel.gridy = 2;
    add(getCheckBoxPanel(), gbc_checkBoxPanel);
    // TODO Auto-generated constructor stub
  }
  private JLabel getSelectFromLabel() {
    if (selectFromLabel == null) {
    	selectFromLabel = new JLabel("Select language to generate translations from: ");
    }
    return selectFromLabel;
  }
  private JComboBox getFromComboBox() {
    if (fromComboBox == null) {
    	fromComboBox = new JComboBox();
    	fromComboBox.addActionListener(new ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    	    enableCheckBoxes();
    	  }
    	});
    	fromComboBox.addItem(EN);
    	fromComboBox.addItem(DE);
    	fromComboBox.addItem(FR);
    	fromComboBox.addItem(ES);
    	fromComboBox.addItem(IT);
    }
    return fromComboBox;
  }
  private JLabel getToLanguageLabel() {
    if (toLanguageLabel == null) {
    	toLanguageLabel = new JLabel("Generate translations for the following languages:");
    }
    return toLanguageLabel;
  }
  private JPanel getCheckBoxPanel() {
    if (checkBoxPanel == null) {
    	checkBoxPanel = new JPanel();
    	GridBagLayout gbl_checkBoxPanel = new GridBagLayout();
    	gbl_checkBoxPanel.columnWidths = new int[]{0, 0};
    	gbl_checkBoxPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
    	gbl_checkBoxPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
    	gbl_checkBoxPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    	checkBoxPanel.setLayout(gbl_checkBoxPanel);
    	GridBagConstraints gbc_enCheckBox = new GridBagConstraints();
    	gbc_enCheckBox.insets = new Insets(0, 5, 5, 0);
    	gbc_enCheckBox.anchor = GridBagConstraints.WEST;
    	gbc_enCheckBox.gridx = 0;
    	gbc_enCheckBox.gridy = 0;
    	checkBoxPanel.add(getEnCheckBox(), gbc_enCheckBox);
    	GridBagConstraints gbc_deCheckBox = new GridBagConstraints();
    	gbc_deCheckBox.anchor = GridBagConstraints.WEST;
    	gbc_deCheckBox.insets = new Insets(0, 5, 5, 0);
    	gbc_deCheckBox.gridx = 0;
    	gbc_deCheckBox.gridy = 1;
    	checkBoxPanel.add(getDeCheckBox(), gbc_deCheckBox);
    	GridBagConstraints gbc_frCheckBox = new GridBagConstraints();
    	gbc_frCheckBox.anchor = GridBagConstraints.WEST;
    	gbc_frCheckBox.insets = new Insets(0, 5, 5, 0);
    	gbc_frCheckBox.gridx = 0;
    	gbc_frCheckBox.gridy = 2;
    	checkBoxPanel.add(getFrCheckBox(), gbc_frCheckBox);
    	GridBagConstraints gbc_esCheckBox = new GridBagConstraints();
    	gbc_esCheckBox.anchor = GridBagConstraints.WEST;
    	gbc_esCheckBox.insets = new Insets(0, 5, 5, 0);
    	gbc_esCheckBox.gridx = 0;
    	gbc_esCheckBox.gridy = 3;
    	checkBoxPanel.add(getEsCheckBox(), gbc_esCheckBox);
    	GridBagConstraints gbc_itCheckBox = new GridBagConstraints();
    	gbc_itCheckBox.anchor = GridBagConstraints.WEST;
    	gbc_itCheckBox.insets = new Insets(0, 5, 0, 0);
    	gbc_itCheckBox.gridx = 0;
    	gbc_itCheckBox.gridy = 4;
    	checkBoxPanel.add(getItCheckBox(), gbc_itCheckBox);
    }
    return checkBoxPanel;
  }
  private JCheckBox getEnCheckBox() {
    if (enCheckBox == null) {
    	enCheckBox = new JCheckBox(EN);
    }
    return enCheckBox;
  }
  private JCheckBox getDeCheckBox() {
    if (deCheckBox == null) {
    	deCheckBox = new JCheckBox(DE);
    }
    return deCheckBox;
  }
  private JCheckBox getFrCheckBox() {
    if (frCheckBox == null) {
    	frCheckBox = new JCheckBox(FR);
    }
    return frCheckBox;
  }
  private JCheckBox getEsCheckBox() {
    if (esCheckBox == null) {
    	esCheckBox = new JCheckBox(ES);
    }
    return esCheckBox;
  }
  private JCheckBox getItCheckBox() {
    if (itCheckBox == null) {
    	itCheckBox = new JCheckBox(IT);
    }
    return itCheckBox;
  }
  
  private void enableCheckBoxes()
  {
    String selectedFromLanguage = getFromComboBox().getSelectedItem().toString();
    getEnCheckBox().setEnabled(!getEnCheckBox().getText().equals(selectedFromLanguage));
    getDeCheckBox().setEnabled(!getDeCheckBox().getText().equals(selectedFromLanguage));
    getFrCheckBox().setEnabled(!getFrCheckBox().getText().equals(selectedFromLanguage));
    getEsCheckBox().setEnabled(!getEsCheckBox().getText().equals(selectedFromLanguage));
    getItCheckBox().setEnabled(!getItCheckBox().getText().equals(selectedFromLanguage));
  }
  
  String getSelectedFromLanguage()
  {
    String selectedFromLanguage = getFromComboBox().getSelectedItem().toString();
    String returnString = selectedFromLanguage.substring(selectedFromLanguage.indexOf("(") +1, selectedFromLanguage.lastIndexOf(")"));
    return returnString;
  }
  
  List<String> getSelectedToLanguages()
  {
    List<String> returnList = new ArrayList<>();
    if (getEnCheckBox().isSelected() && getEnCheckBox().isEnabled())
    {
      returnList.add("en");
    }
    if (getDeCheckBox().isSelected() && getDeCheckBox().isEnabled())
    {
      returnList.add("de");
    }
    if (getFrCheckBox().isSelected() && getFrCheckBox().isEnabled())
    {
      returnList.add("fr");
    }
    if (getEsCheckBox().isSelected() && getEsCheckBox().isEnabled())
    {
      returnList.add("es");
    }
    if (getItCheckBox().isSelected() && getItCheckBox().isEnabled())
    {
      returnList.add("it");
    }
    return returnList;
  }
}
