package se.lantz.manager;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dyorgio.runtime.run.as.root.RootExecutor;
import se.lantz.gui.MainWindow;
import se.lantz.util.ExceptionHandler;

public class InstallPCUAEManager implements AWTEventListener
{
  private static final Logger logger = LoggerFactory.getLogger(InstallPCUAEManager.class);

  public static final String INSTALL_FOLDER = "./pcuae-install/";
  private boolean blockEvents = false;
  private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

  private JMenuItem exportMenuItem;

  public InstallPCUAEManager()
  {
    Toolkit.getDefaultToolkit().addAWTEventListener(this,
                                                    AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK |
                                                      AWTEvent.MOUSE_WHEEL_EVENT_MASK |
                                                      AWTEvent.MOUSE_MOTION_EVENT_MASK);
  }

  public void install(JMenuItem exportMenuItem)
  {
    this.exportMenuItem = exportMenuItem;
    switchToBusyCursor(MainWindow.getInstance());
    // Specify JVM options (optional)
    singleThreadExecutor.execute(() -> runAndWaitForInstallation());
  }

  private void runAndWaitForInstallation()
  {

    RootExecutor rootExecutor;
    try
    {
      rootExecutor = new RootExecutor("-Xmx64m");
      // Execute privileged action without return
      int value = rootExecutor.call(() -> {
        int returnValue = 0;
        try
        {
          String filename = "pcuae-setup.exe";
          File fileDir = new File("./pcuae-install/");
          //Read file from dir and see if we can extract it
          Path installFilePath = new File("./pcuae-install/" + filename).toPath();
          returnValue = Runtime.getRuntime().exec(installFilePath.toString(), null, fileDir).waitFor();
          System.out.println("ExitValue = " + returnValue);
        }
        catch (InterruptedException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (IOException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return returnValue;
      });
      //0 -> installed
      //255 -> aborted
      if (value == 0)
      {
        launchExportDialog();
      }
    }
    catch (Exception e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    finally
    {
      SwingUtilities.invokeLater(() -> switchToNormalCursor(MainWindow.getInstance()));
    }
  }

  private void launchExportDialog()
  {
    SwingUtilities.invokeLater(() -> exportMenuItem.doClick());
  }

  public void switchToBusyCursor(final javax.swing.JFrame frame)
  {
    startEventTrap(frame);
    frame.getGlassPane().setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
  }

  public void switchToNormalCursor(final javax.swing.JFrame frame)
  {
    frame.getGlassPane().setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
    stopEventTrap(frame);
  }

  private void startEventTrap(javax.swing.JFrame frame)
  {
    blockEvents = true;
    frame.getGlassPane().setVisible(true);
  }

  private void stopEventTrap(javax.swing.JFrame frame)
  {
    blockEvents = false;
    java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue();
    frame.getGlassPane().setVisible(false);
  }

  @Override
  public void eventDispatched(AWTEvent event)
  {
    if (blockEvents)
    {
      if (event instanceof InputEvent)
      {
        ((InputEvent) event).consume();
      }
      else if (event instanceof MouseEvent)
      {
        ((MouseEvent) event).consume();
      }
    }

  }

  public void downloadTest(final JProgressBar progressBar)
  {
    
    singleThreadExecutor.execute(() -> startDownload(progressBar));
  }
  
  private void startDownload(final JProgressBar progressBar)
  {
    progressBar.setMaximum(100000);
    URL url;
    try
    {
      url = new URL("https://github.com/lantzelot-swe/PCUGameManager/releases/download/1.11.1/PcuGameManager.exe");

      HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
      long completeFileSize = httpConnection.getContentLength();
      BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
      FileOutputStream fos = new FileOutputStream(INSTALL_FOLDER + "test.exe");
      BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
      byte[] data = new byte[1024];
      long downloadedFileSize = 0;
      int x = 0;
      while ((x = in.read(data, 0, 1024)) >= 0)
      {
        downloadedFileSize += x;
        // calculate progress
        final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100000d);
        // update progress bar
        SwingUtilities.invokeLater(new Runnable()
          {

            @Override
            public void run()
            {
              progressBar.setValue(currentProgress);
            }
          });
        bout.write(data, 0, x);
      }
      bout.close();
      in.close();
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "SOmething went wrong during download");
    }
  }
}
