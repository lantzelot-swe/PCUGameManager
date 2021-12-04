package se.lantz.manager.pcuae;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import dyorgio.runtime.out.process.CallableSerializable;
import dyorgio.runtime.run.as.root.RootExecutor;
import se.lantz.gui.MainWindow;
import se.lantz.gui.download.DownloadDialog;
import se.lantz.util.ExceptionHandler;

public abstract class BaseInstallManger implements AWTEventListener
{
  public static final String INSTALL_FOLDER = "./pcuae-install/";

  private boolean blockEvents = false;
  protected ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

  protected volatile boolean downloadIterrupted = false;

  public BaseInstallManger()
  {
    Toolkit.getDefaultToolkit().addAWTEventListener(this,
                                                    AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK |
                                                      AWTEvent.MOUSE_WHEEL_EVENT_MASK |
                                                      AWTEvent.MOUSE_MOTION_EVENT_MASK);
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

  protected void startEventTrap(javax.swing.JFrame frame)
  {
    blockEvents = true;
    frame.getGlassPane().setVisible(true);
  }

  protected void stopEventTrap(javax.swing.JFrame frame)
  {
    blockEvents = false;
    java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue();
    frame.getGlassPane().setVisible(false);
  }

  protected String readVersionFromInstallFolder(String installFileName)
  {
    String latestInInstallFolder = "";
    FilenameFilter filter = new FilenameFilter()
      {
        @Override
        public boolean accept(File f, String name)
        {
          return name.contains(installFileName) && name.endsWith(".exe");
        }
      };
    File installFolder = new File(INSTALL_FOLDER);
    File[] availableFiles = installFolder.listFiles(filter);
    //Check the timestamp and take the last modified as the current one
    File latestFile = null;
    long lastModified = 0L;
    for (File file : availableFiles)
    {
      if (file.lastModified() > lastModified)
      {
        latestFile = file;
        lastModified = file.lastModified();
      }
    }

    if (latestFile != null)
    {
      latestInInstallFolder = latestFile.getName();
    }
    return latestInInstallFolder;
  }

  public GithubAssetInformation fetchLatestVersionFromGithub(String assetsName)
  {
    GithubAssetInformation githubInfo = new GithubAssetInformation();
    try
    {
      //TODO: To get all releases, use "https://CommodoreOS@api.github.com/repos/CommodoreOS/PCUAE/releases"-
      //Get all releases, check which one contains the latest file with assetName (part of the name)

      URL url = new URL("https://CommodoreOS@api.github.com/repos/CommodoreOS/PCUAE/releases/latest");

      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestProperty("accept", "application/vnd.github.v3+json");
      con.setRequestMethod("GET");
      StringBuilder builder = new StringBuilder();
      Scanner scanner = new Scanner(url.openStream());
      while (scanner.hasNext())
      {
        builder.append(scanner.nextLine() + "/n");
      }
      scanner.close();
      con.disconnect();

      JsonReader reader = new JsonReader(new StringReader(builder.toString()));
      reader.setLenient(true);
      JsonElement root = new JsonParser().parse(reader);
      JsonObject jsonObject = root.getAsJsonObject();
      githubInfo.setLatestVersion(jsonObject.get("tag_name").getAsString());
      githubInfo.setReleaseTagUrl(jsonObject.get("html_url").getAsString());
      //TODO: fix
      String downloadUrl =
        jsonObject.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
      githubInfo.setDownloadUrl(downloadUrl);
      githubInfo.setInstallFile(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1));
    }
    catch (IOException ex)
    {
      ExceptionHandler.handleException(ex, "Could not check version");
    }
    return githubInfo;
  }

  protected void runAndWaitForInstallation(String fileToInstall)
  {
    switchToBusyCursor(MainWindow.getInstance());
    RootExecutor rootExecutor;
    try
    {
      rootExecutor = new RootExecutor("-Xmx64m");
      // Execute privileged action without return
      CallableSerializable<Integer> installCallable = new InstallCallable(fileToInstall);
      int value = rootExecutor.call(installCallable);

      //0 -> installed
      //255 -> aborted
      if (value == 0)
      {
        executeAfterInstallation();
      }
    }
    catch (Exception e1)
    {
      ExceptionHandler.logException(e1, "Could not execute RootExecutor");
    }
    finally
    {
      SwingUtilities.invokeLater(() -> switchToNormalCursor(MainWindow.getInstance()));
    }
  }

  public void startDownload(final DownloadDialog downloadDialog, GithubAssetInformation assetInfo)
  {
    downloadIterrupted = false;
    JProgressBar progressBar = downloadDialog.getProgressBar();
    progressBar.setMaximum(100000);
    URL url;
    try
    {
      url = new URL(assetInfo.getDownloadUrl());
      Files.createDirectories(new File(INSTALL_FOLDER).toPath());

      HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
      long completeFileSize = httpConnection.getContentLength();
      BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
      FileOutputStream fos = new FileOutputStream(INSTALL_FOLDER + assetInfo.getInstallFile());
      BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
      byte[] data = new byte[1024];
      long downloadedFileSize = 0;
      int x = 0;
      while ((x = in.read(data, 0, 1024)) >= 0 && !downloadIterrupted)
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
      ExceptionHandler.handleException(e, "Something went wrong during download");
    }
    finally
    {
      downloadDialog.closeWhenComplete();
    }
  }

  protected void cleanupInterruptedDownload(String fileName)
  {
    downloadIterrupted = true;
    //Make sure this executes after the downloading has been interrupted
    singleThreadExecutor.execute(() -> {
      try
      {
        Files.deleteIfExists(new File(INSTALL_FOLDER + fileName).toPath());
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not delete partially downloaded file");
      }
    });
  }

  protected abstract void executeAfterInstallation();
}