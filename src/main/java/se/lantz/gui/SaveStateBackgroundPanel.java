package se.lantz.gui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import se.lantz.model.MainViewModel;
import se.lantz.model.SavedStatesModel.SAVESTATE;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class SaveStateBackgroundPanel extends JPanel
{
  private SaveStatePanel saveStatePanel1;
  private MainViewModel model;
  private SaveStatePanel saveStatePanel2;
  private SaveStatePanel saveStatePanel3;
  private SaveStatePanel saveStatePanel4;
  public SaveStateBackgroundPanel(MainViewModel model) {
    this.model = model;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_saveStatePanel1 = new GridBagConstraints();
    gbc_saveStatePanel1.anchor = GridBagConstraints.NORTH;
    gbc_saveStatePanel1.weightx = 1.0;
    gbc_saveStatePanel1.fill = GridBagConstraints.BOTH;
    gbc_saveStatePanel1.gridx = 0;
    gbc_saveStatePanel1.gridy = 0;
    add(getSaveStatePanel1(), gbc_saveStatePanel1);
    GridBagConstraints gbc_saveStatePanel2 = new GridBagConstraints();
    gbc_saveStatePanel2.anchor = GridBagConstraints.NORTH;
    gbc_saveStatePanel2.weightx = 1.0;
    gbc_saveStatePanel2.fill = GridBagConstraints.BOTH;
    gbc_saveStatePanel2.gridx = 0;
    gbc_saveStatePanel2.gridy = 1;
    add(getSaveStatePanel2(), gbc_saveStatePanel2);
    GridBagConstraints gbc_saveStatePanel3 = new GridBagConstraints();
    gbc_saveStatePanel3.anchor = GridBagConstraints.NORTH;
    gbc_saveStatePanel3.weightx = 1.0;
    gbc_saveStatePanel3.fill = GridBagConstraints.BOTH;
    gbc_saveStatePanel3.gridx = 0;
    gbc_saveStatePanel3.gridy = 2;
    add(getSaveStatePanel3(), gbc_saveStatePanel3);
    GridBagConstraints gbc_saveStatePanel4 = new GridBagConstraints();
    gbc_saveStatePanel4.anchor = GridBagConstraints.NORTH;
    gbc_saveStatePanel4.weightx = 1.0;
    gbc_saveStatePanel4.weighty = 1.0;
    gbc_saveStatePanel4.fill = GridBagConstraints.HORIZONTAL;
    gbc_saveStatePanel4.gridx = 0;
    gbc_saveStatePanel4.gridy = 3;
    add(getSaveStatePanel4(), gbc_saveStatePanel4);
  }
  private SaveStatePanel getSaveStatePanel1() {
    if (saveStatePanel1 == null) {
    	saveStatePanel1 = new SaveStatePanel(model, SAVESTATE.Save0);
    }
    return saveStatePanel1;
  }
  private SaveStatePanel getSaveStatePanel2() {
    if (saveStatePanel2 == null) {
    	saveStatePanel2 = new SaveStatePanel(model, SAVESTATE.Save1);
    }
    return saveStatePanel2;
  }
  private SaveStatePanel getSaveStatePanel3() {
    if (saveStatePanel3 == null) {
    	saveStatePanel3 = new SaveStatePanel(model, SAVESTATE.Save2);
    }
    return saveStatePanel3;
  }
  private SaveStatePanel getSaveStatePanel4() {
    if (saveStatePanel4 == null) {
    	saveStatePanel4 = new SaveStatePanel(model, SAVESTATE.Save3);
    }
    return saveStatePanel4;
  }
  
  void commitEdits()
  {
    getSaveStatePanel1().commitEdits();
    getSaveStatePanel2().commitEdits();
    getSaveStatePanel3().commitEdits();
    getSaveStatePanel4().commitEdits();
  }
  
  void resetCurrentGameReference()
  {
    getSaveStatePanel1().resetCurrentGameReference();
    getSaveStatePanel2().resetCurrentGameReference();
    getSaveStatePanel3().resetCurrentGameReference();
    getSaveStatePanel4().resetCurrentGameReference();
  }
}
