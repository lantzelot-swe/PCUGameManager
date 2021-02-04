package se.lantz.gui.translation;

import java.io.IOException;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import se.lantz.model.InfoModel;
import se.lantz.util.DescriptionTranslater;
import se.lantz.util.ExceptionHandler;

public class TranslationWorker extends SwingWorker<Void, String>
{
  private TranslationProgressDialog dialog;
  private InfoModel model;
  private String fromLanguage;
  private List<String> toLanguageList;

  public TranslationWorker(InfoModel model,
                           TranslationProgressDialog dialog,
                           String fromLanguage,
                           List<String> toLanguageList)
  {
    this.model = model;
    this.dialog = dialog;
    this.fromLanguage = fromLanguage;
    this.toLanguageList = toLanguageList;
  }

  @Override
  protected Void doInBackground() throws Exception
  {
    String textToTranslate = "";
    switch (fromLanguage)
    {
    case "en":
      textToTranslate = model.getDescription();
      break;
    case "de":
      textToTranslate = model.getDescriptionDe();
      break;
    case "fr":
      textToTranslate = model.getDescriptionFr();
      break;
    case "es":
      textToTranslate = model.getDescriptionEs();
      break;
    case "it":
      textToTranslate = model.getDescriptionIt();
      break;
    default:
      break;
    }
    if (textToTranslate.isEmpty())
    {
      //Nothing to translate
      return null;
    }
    for (String toLanguage : toLanguageList)
    {
      try
      {
        SwingUtilities.invokeLater(() -> {
          dialog.updateProgress(toLanguage);
        });
        String translatedText = DescriptionTranslater.translate(fromLanguage, toLanguage, textToTranslate);
        publish(toLanguage + "¤" + translatedText);
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not translate description");
      }
    }
    return null;
  }

  @Override
  protected void process(List<String> chunks)
  {
    for (String result : chunks)
    {
      String[] split = result.split("¤");
      switch (split[0])
      {
      case "en":
        model.setDescription(split[1]);
        break;
      case "de":
        model.setDescriptionDe(split[1]);
        break;
      case "fr":
        model.setDescriptionFr(split[1]);
        break;
      case "es":
        model.setDescriptionEs(split[1]);
        break;
      case "it":
        model.setDescriptionIt(split[1]);
        break;
      default:
        break;
      }
    }
  }

  @Override
  protected void done()
  {
    dialog.finish();
  }
}
