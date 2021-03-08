package se.lantz.gui.imports;

import java.util.List;

import javax.swing.SwingWorker;

import se.lantz.util.ExceptionHandler;

public abstract class AbstractImportWorker extends SwingWorker<Void, String>
{
  volatile String progressValueString = "";
  volatile int progressMaximum = 0;
  volatile int progressValue = 0;
  ImportProgressDialog dialog;
  
  public AbstractImportWorker(ImportProgressDialog dialog)
  {
    this.dialog = dialog;
    //Empty
  }

  @Override
  protected void process(List<String> chunks)
  {
    for (String value : chunks)
    {
      if (value.isEmpty())
      {
        dialog.updateProgress("");
      }
      else
      {
        dialog.updateProgress(value + "\n");
      }
      if (!progressValueString.isEmpty())
      {
        dialog.updateProgressBar(progressValueString, progressMaximum, progressValue);
      }
    }
  }
  
  @Override
  protected void done()
  {
    try
    {
      get();
    }
    catch (Exception e)
    {
      ExceptionHandler.handleException(e, "Error during import");
    }
    dialog.finish();
  }
}
