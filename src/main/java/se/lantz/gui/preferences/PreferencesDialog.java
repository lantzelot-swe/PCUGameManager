package se.lantz.gui.preferences;

import java.awt.Dimension;
import java.awt.Frame;

import se.lantz.gui.BaseDialog;

public class PreferencesDialog extends BaseDialog
{
  private PreferencesTabPanel panel;

  public PreferencesDialog(Frame owner)
  {
    super(owner);
    setTitle("PCUAE Manager preferences");
    addContent(getPreferencesTabPanel());
    getOkButton().setText("Save");
    getOkButton().setPreferredSize(null);
    this.setPreferredSize(new Dimension(366, 550));
    this.setResizable(false);
  }

  private PreferencesTabPanel getPreferencesTabPanel()
  {
    if (panel == null)
    {
      panel = new PreferencesTabPanel();
    }
    return panel;
  }

  public void savePreferences()
  {
    getPreferencesTabPanel().savePreferences();
  }
}
