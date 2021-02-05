package se.lantz.gui.convertscreens;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import se.lantz.util.FileManager;

public class ConvertWorker extends SwingWorker<Integer, String>
{
  private ConvertProgressDialog dialog;

  public ConvertWorker(ConvertProgressDialog dialog)
  {
    this.dialog = dialog;
  }

  @Override
  protected Integer doInBackground() throws Exception
  {
    publish("Reading screnshots...");
    List<String> convertionList = FileManager.convertAllScreenshotsTo32Bit();
    for (String screenshot : convertionList)
    {
      publish(screenshot);
    }
    return convertionList.size();
  }

  @Override
  protected void process(List<String> chunks)
  {
    for (String value : chunks)
    {
      dialog.updateProgress(value + "\n");
    }
  }

  @Override
  protected void done()
  {
    try
    {
      dialog.finish(this.get(), null);
    }
    catch (InterruptedException | ExecutionException e)
    {
      dialog.finish(0, e);
    }
  }
}
