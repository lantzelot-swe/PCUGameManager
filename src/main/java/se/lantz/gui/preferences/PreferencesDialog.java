package se.lantz.gui.preferences;

import java.awt.Dimension;
import java.awt.Frame;

import se.lantz.gui.BaseDialog;

public class PreferencesDialog extends BaseDialog
{
  private PreferencesPanel panel;

  public PreferencesDialog(Frame owner)
  {
    super(owner);
    setTitle("PCUAE Manager preferences");
    addContent(getPreferencesPanel());
    getOkButton().setText("Save");
    getOkButton().setPreferredSize(null);
    this.setPreferredSize(new Dimension(372, 570));
    this.setResizable(false);
  }

  private PreferencesPanel getPreferencesPanel()
  {
    if (panel == null)
    {
      panel = new PreferencesPanel();
    }
    return panel;
  }

  public void savePreferences()
  {
    getPreferencesPanel().savePreferences();
  }
}
