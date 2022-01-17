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

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import dyorgio.runtime.out.process.CallableSerializable;
import dyorgio.runtime.run.as.root.RootExecutor;
import se.lantz.gui.MainWindow;
import se.lantz.gui.download.DownloadDialog;
import se.lantz.gui.install.PCUAEProductDownloadDialog;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;
import se.lantz.util.ManagerVersionChecker;

public abstract class BaseInstallManager implements AWTEventListener
{
  public static final String INSTALL_FOLDER = "./pcuae-install/";

  protected static final String PCUAE_INSTALL_NAME = "pcuae";
  protected static final String AMIGA_MODE_INSTALL_NAME = "amiga";
  protected static final String ATARI_MODE_INSTALL_NAME = "atari";
  protected static final String LINUX_MODE_INSTALL_NAME = "linux";
  protected static final String RETROARCH_MODE_INSTALL_NAME = "retroarch";
  protected static final String VICE_MODE_INSTALL_NAME = "vice";

  private boolean blockEvents = false;
  protected ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

  protected String latestInInstallFolder = "";

  protected GithubAssetInformation gitHubReleaseInformation = new GithubAssetInformation();

  protected volatile boolean downloadIterrupted = false;

  public BaseInstallManager()
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

  protected void readVersionFromInstallFolder(String installFileName)
  {
    latestInInstallFolder = "";
    FilenameFilter filter = getFileNameFilter(installFileName);
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
  }

  protected void deleteOldInstallFiles(String installFileName)
  {
    FilenameFilter filter = getFileNameFilter(installFileName);
    File installFolder = new File(INSTALL_FOLDER);
    File[] availableFiles = installFolder.listFiles(filter);
    for (File file : availableFiles)
    {
      if (!file.getName().equals(latestInInstallFolder))
      {
        file.delete();
      }
    }
  }
  
  private FilenameFilter getFileNameFilter(String installFileName)
  {
    FilenameFilter filter = new FilenameFilter()
    {
      @Override
      public boolean accept(File f, String name)
      {
        if (PCUAE_INSTALL_NAME.equals(installFileName))
        {
          //Check so that no other is part of the name
          return !(name.contains(AMIGA_MODE_INSTALL_NAME) || name.contains(ATARI_MODE_INSTALL_NAME) ||
            name.contains(LINUX_MODE_INSTALL_NAME) || name.contains(RETROARCH_MODE_INSTALL_NAME) ||
            name.contains(VICE_MODE_INSTALL_NAME)) && name.endsWith(".exe");
        }
        else
        {
          return name.contains(installFileName) && name.endsWith(".exe");
        }
      }
    };
    return filter;
  }

  public GithubAssetInformation fetchLatestVersionFromGithub(String assetsName)
  {
    GithubAssetInformation githubInfo = new GithubAssetInformation();
    try
    {
      //TODO: To get all releases, use "https://CommodoreOS@api.github.com/repos/CommodoreOS/PCUAE/releases"-
      //Get all releases, check which one contains the latest file with assetName (part of the name)

      URL url = new URL("https://CommodoreOS@api.github.com/repos/CommodoreOS/PCUAE/releases");

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
      JsonArray releases = root.getAsJsonArray();
      //Find the latest one containing in assetName. Releases seem to be return in the right order (latest first)
      boolean foundRelease = false;
      for (JsonElement release : releases)
      {
        if (foundRelease)
        {
          break;
        }
        JsonObject jsonObject = release.getAsJsonObject();
        JsonArray assets = jsonObject.get("assets").getAsJsonArray();
        String downloadUrl = "";

        for (JsonElement asset : assets)
        {
          String assetName = asset.getAsJsonObject().get("name").getAsString();
          switch (assetsName)
          {
          case AMIGA_MODE_INSTALL_NAME:
          {
            if (assetName.contains(AMIGA_MODE_INSTALL_NAME))
            {
              downloadUrl = asset.getAsJsonObject().get("browser_download_url").getAsString();
            }
            break;
          }
          case ATARI_MODE_INSTALL_NAME:
          {
            if (assetName.contains(ATARI_MODE_INSTALL_NAME))
            {
              downloadUrl = asset.getAsJsonObject().get("browser_download_url").getAsString();
            }
            break;
          }
          case LINUX_MODE_INSTALL_NAME:
          {
            if (assetName.contains(LINUX_MODE_INSTALL_NAME))
            {
              downloadUrl = asset.getAsJsonObject().get("browser_download_url").getAsString();
            }
            break;
          }
          case RETROARCH_MODE_INSTALL_NAME:
          {
            if (assetName.contains(RETROARCH_MODE_INSTALL_NAME))
            {
              downloadUrl = asset.getAsJsonObject().get("browser_download_url").getAsString();
            }
            break;
          }
          case VICE_MODE_INSTALL_NAME:
          {
            if (assetName.contains(VICE_MODE_INSTALL_NAME))
            {
              downloadUrl = asset.getAsJsonObject().get("browser_download_url").getAsString();
            }
            break;
          }
          case PCUAE_INSTALL_NAME:
          {
            if (!(assetName.contains(AMIGA_MODE_INSTALL_NAME) || assetName.contains(ATARI_MODE_INSTALL_NAME) ||
              assetName.contains(LINUX_MODE_INSTALL_NAME) || assetName.contains(RETROARCH_MODE_INSTALL_NAME) ||
              assetName.contains(VICE_MODE_INSTALL_NAME)))
            {
              downloadUrl = asset.getAsJsonObject().get("browser_download_url").getAsString();
            }
            break;
          }
          default:
            throw new IllegalArgumentException("Unexpected value: " + assetsName);
          }

          if (!downloadUrl.isEmpty())
          {
            githubInfo.setLatestVersion(release.getAsJsonObject().get("tag_name").getAsString());
            githubInfo.setReleaseTagUrl(release.getAsJsonObject().get("html_url").getAsString());
            githubInfo.setInstallFile(assetName);
            githubInfo.setDownloadUrl(downloadUrl);
            foundRelease = true;
            break;
          }
        }
      }
    }
    catch (IOException ex)
    {
      ExceptionHandler.handleException(ex, "Could not check version");
    }
    return githubInfo;
  }

  protected void runAndWaitForInstallation()
  {
    switchToBusyCursor(MainWindow.getInstance());
    RootExecutor rootExecutor;
    try
    {
      rootExecutor = new RootExecutor("-Xmx64m");
      // Execute privileged action without return
      CallableSerializable<Integer> installCallable = new InstallCallable(latestInInstallFolder);
      int value = rootExecutor.call(installCallable);

      //0 -> installed
      //255 -> aborted
      if (value == 0)
      {
        SwingUtilities.invokeLater(() -> executeAfterInstallation());
      }
    }
    catch (Exception e1)
    {
      ExceptionHandler.handleException(e1, "Something went wrong during install.");
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

  protected void cleanupInterruptedDownload()
  {
    downloadIterrupted = true;
    //Make sure this executes after the downloading has been interrupted
    singleThreadExecutor.execute(() -> {
      try
      {
        Files.deleteIfExists(new File(INSTALL_FOLDER + gitHubReleaseInformation.getInstallFile()).toPath());
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not delete partially downloaded file");
      }
    });
  }

  protected void askToInstallExistingVersion(String productName)
  {
    int value =
      JOptionPane.showConfirmDialog(MainWindow.getInstance(),
                                    "Do you want to install " + productName + " (" + latestInInstallFolder + ") now?",
                                    "Install " + productName,
                                    JOptionPane.YES_NO_OPTION);
    if (value == JOptionPane.YES_OPTION)
    {
      singleThreadExecutor.execute(() -> runAndWaitForInstallation());
    }
  }

  protected boolean isNewVersionAvailable(String installName)
  {
    gitHubReleaseInformation = fetchLatestVersionFromGithub(installName);
    return ManagerVersionChecker.getIntVersion(latestInInstallFolder) < ManagerVersionChecker
      .getIntVersion(gitHubReleaseInformation.getInstallFile());
  }

  public String getLatestInInstallFolder()
  {
    return latestInInstallFolder;
  }

  public String getLatestVersion()
  {
    return gitHubReleaseInformation.getLatestVersion();
  }

  public String getDownloadUrl()
  {
    return gitHubReleaseInformation.getDownloadUrl();
  }

  public String getReleaseTagUrl()
  {
    return gitHubReleaseInformation.getReleaseTagUrl();
  }

  protected void askAndStartDownloadAtStartup(String productName, String installName)
  {
    PCUAEProductDownloadDialog dialog = new PCUAEProductDownloadDialog(false, this, productName, true);
    dialog.pack();
    dialog.setLocationRelativeTo(MainWindow.getInstance());
    if (dialog.showDialog())
    {
      downloadLatestVersion(productName, installName);
    }
  }

  protected void askAndStartDownload(String productName, String installName)
  {
    PCUAEProductDownloadDialog dialog =
      new PCUAEProductDownloadDialog(latestInInstallFolder.isEmpty(), this, productName, false);
    dialog.pack();
    dialog.setLocationRelativeTo(MainWindow.getInstance());
    if (dialog.showDialog())
    {
      downloadLatestVersion(productName, installName);
    }
    else if (!latestInInstallFolder.isEmpty())
    {
      askToInstallExistingVersion(productName);
    }
  }

  protected void downloadLatestVersion(String productName, String installName)
  {
    DownloadDialog progressDialog =
      new DownloadDialog("Downloading " + productName + " version " + gitHubReleaseInformation.getLatestVersion());
    singleThreadExecutor.execute(() -> startDownload(progressDialog, gitHubReleaseInformation));
    progressDialog.pack();
    progressDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (progressDialog.showDialog())
    {
      latestInInstallFolder = gitHubReleaseInformation.getInstallFile();
      
      if (FileManager.isConfiguredDeleteOldInstallfilesAfterDownload())
      {
        deleteOldInstallFiles(installName);
      }
      
      
      int value = JOptionPane.showConfirmDialog(MainWindow.getInstance(),
                                                "Download completed, do you want to install " + productName + " " +
                                                  gitHubReleaseInformation.getLatestVersion() + " now?",
                                                "Download Complete",
                                                JOptionPane.YES_NO_OPTION);
      if (value == JOptionPane.YES_OPTION)
      {
        singleThreadExecutor.execute(() -> runAndWaitForInstallation());
      }
    }
    else
    {
      cleanupInterruptedDownload();
    }
  }

  protected abstract void executeAfterInstallation();
}
