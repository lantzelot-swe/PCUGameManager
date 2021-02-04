package se.lantz.gui;

import java.util.List;

import se.lantz.model.InfoModel;

public class TranslationDialog extends BaseDialog
{
  private TranslationPanel panel;
  private InfoModel model;

  public TranslationDialog(InfoModel model)
  {
    super(MainWindow.getInstance());
    this.model = model;
    setTitle("Translate description");
    addContent(getTranslationPanel());
    this.setResizable(false);
  }

  private TranslationPanel getTranslationPanel() {
    if (panel == null) {
    	panel = new TranslationPanel(model);
    }
    return panel;
  }
  
  String getSelectedFromLanguage()
  {
    return getTranslationPanel().getSelectedFromLanguage();
  }
  
  List<String> getSelectedToLanguages()
  {
    return getTranslationPanel().getSelectedToLanguages();
  }
}
