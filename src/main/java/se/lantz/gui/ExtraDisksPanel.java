package se.lantz.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import se.lantz.model.InfoModel;

public class ExtraDisksPanel extends JPanel
{

  private InfoModel infoModel;
  private ExtraDiskFileChooserPanel extraDiskFileChooserPanel1;
  private ExtraDiskFileChooserPanel extraDiskFileChooserPanel2;
  private ExtraDiskFileChooserPanel extraDiskFileChooserPanel3;
  private ExtraDiskFileChooserPanel extraDiskFileChooserPanel4;
  private ExtraDiskFileChooserPanel extraDiskFileChooserPanel5;

  public ExtraDisksPanel(InfoModel infoModel)
  {
    this.infoModel = infoModel;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_extraDiskFileChooserPanel1 = new GridBagConstraints();
    gbc_extraDiskFileChooserPanel1.anchor = GridBagConstraints.NORTHWEST;
    gbc_extraDiskFileChooserPanel1.insets = new Insets(0, 0, 5, 0);
    gbc_extraDiskFileChooserPanel1.weightx = 1.0;
    gbc_extraDiskFileChooserPanel1.fill = GridBagConstraints.HORIZONTAL;
    gbc_extraDiskFileChooserPanel1.gridx = 0;
    gbc_extraDiskFileChooserPanel1.gridy = 0;
    add(getExtraDiskFileChooserPanel1(), gbc_extraDiskFileChooserPanel1);
    GridBagConstraints gbc_extraDiskFileChooserPanel2 = new GridBagConstraints();
    gbc_extraDiskFileChooserPanel2.insets = new Insets(0, 0, 5, 0);
    gbc_extraDiskFileChooserPanel2.weightx = 1.0;
    gbc_extraDiskFileChooserPanel2.anchor = GridBagConstraints.NORTHWEST;
    gbc_extraDiskFileChooserPanel2.fill = GridBagConstraints.HORIZONTAL;
    gbc_extraDiskFileChooserPanel2.gridx = 0;
    gbc_extraDiskFileChooserPanel2.gridy = 1;
    add(getExtraDiskFileChooserPanel2(), gbc_extraDiskFileChooserPanel2);
    GridBagConstraints gbc_extraDiskFileChooserPanel3 = new GridBagConstraints();
    gbc_extraDiskFileChooserPanel3.insets = new Insets(0, 0, 5, 0);
    gbc_extraDiskFileChooserPanel3.anchor = GridBagConstraints.NORTHWEST;
    gbc_extraDiskFileChooserPanel3.weightx = 1.0;
    gbc_extraDiskFileChooserPanel3.fill = GridBagConstraints.HORIZONTAL;
    gbc_extraDiskFileChooserPanel3.gridx = 0;
    gbc_extraDiskFileChooserPanel3.gridy = 2;
    add(getExtraDiskFileChooserPanel3(), gbc_extraDiskFileChooserPanel3);
    GridBagConstraints gbc_extraDiskFileChooserPanel4 = new GridBagConstraints();
    gbc_extraDiskFileChooserPanel4.weightx = 1.0;
    gbc_extraDiskFileChooserPanel4.anchor = GridBagConstraints.NORTHWEST;
    gbc_extraDiskFileChooserPanel4.insets = new Insets(0, 0, 5, 0);
    gbc_extraDiskFileChooserPanel4.fill = GridBagConstraints.HORIZONTAL;
    gbc_extraDiskFileChooserPanel4.gridx = 0;
    gbc_extraDiskFileChooserPanel4.gridy = 3;
    add(getExtraDiskFileChooserPanel4(), gbc_extraDiskFileChooserPanel4);
    GridBagConstraints gbc_extraDiskFileChooserPanel5 = new GridBagConstraints();
    gbc_extraDiskFileChooserPanel5.insets = new Insets(0, 0, 5, 0);
    gbc_extraDiskFileChooserPanel5.weightx = 1.0;
    gbc_extraDiskFileChooserPanel5.weighty = 1.0;
    gbc_extraDiskFileChooserPanel5.anchor = GridBagConstraints.NORTHWEST;
    gbc_extraDiskFileChooserPanel5.fill = GridBagConstraints.HORIZONTAL;
    gbc_extraDiskFileChooserPanel5.gridx = 0;
    gbc_extraDiskFileChooserPanel5.gridy = 4;
    add(getExtraDiskFileChooserPanel5(), gbc_extraDiskFileChooserPanel5);
  }

  private ExtraDiskFileChooserPanel getExtraDiskFileChooserPanel1()
  {
    if (extraDiskFileChooserPanel1 == null)
    {
      extraDiskFileChooserPanel1 = new ExtraDiskFileChooserPanel(this.infoModel, 2);
    }
    return extraDiskFileChooserPanel1;
  }

  private ExtraDiskFileChooserPanel getExtraDiskFileChooserPanel2()
  {
    if (extraDiskFileChooserPanel2 == null)
    {
      extraDiskFileChooserPanel2 = new ExtraDiskFileChooserPanel(this.infoModel, 3);
    }
    return extraDiskFileChooserPanel2;
  }

  private ExtraDiskFileChooserPanel getExtraDiskFileChooserPanel3()
  {
    if (extraDiskFileChooserPanel3 == null)
    {
      extraDiskFileChooserPanel3 = new ExtraDiskFileChooserPanel(this.infoModel, 4);
    }
    return extraDiskFileChooserPanel3;
  }
  private ExtraDiskFileChooserPanel getExtraDiskFileChooserPanel4() {
    if (extraDiskFileChooserPanel4 == null) {
    	extraDiskFileChooserPanel4 = new ExtraDiskFileChooserPanel(this.infoModel, 5);
    }
    return extraDiskFileChooserPanel4;
  }
  private ExtraDiskFileChooserPanel getExtraDiskFileChooserPanel5() {
    if (extraDiskFileChooserPanel5 == null) {
    	extraDiskFileChooserPanel5 = new ExtraDiskFileChooserPanel(this.infoModel, 6);
    }
    return extraDiskFileChooserPanel5;
  }
}
