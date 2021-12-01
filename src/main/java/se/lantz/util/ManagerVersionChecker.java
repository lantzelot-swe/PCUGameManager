package se.lantz.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import se.lantz.gui.MainWindow;
import se.lantz.gui.download.DownloadDialog;

public class ManagerVersionChecker
{
  public static final String TEMP_FOLDER = "./temp/";
  private static String latestVersion = "";
  private static String tagloadUrl = "";
  private static String downloadUrl = "";
  private static String managerMainInstallFile;
  private static boolean downloadIterrupted = false;
  
  private static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
  private ManagerVersionChecker()
  {
    //Empty
  }
  
  public static void fetchLatestVersionFromGithub()
  {
    try
    {
      URL url = new URL("https://lantzelot-swe@api.github.com/repos/lantzelot-swe/PCUGameManager/releases/latest");
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
      latestVersion = root.getAsJsonObject().get("tag_name").getAsString();
      tagloadUrl = root.getAsJsonObject().get("html_url").getAsString();
      downloadUrl = root.getAsJsonObject().get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
      managerMainInstallFile = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
    }
    catch (IOException ex)
    {
      ExceptionHandler.handleException(ex, "Could not check version");
    }
  }
  
  public static void updateVersion()
  {  
    DownloadDialog progressDialog = new DownloadDialog("Downloading version " + latestVersion);
    singleThreadExecutor.execute(() -> startDownload(progressDialog));  
    progressDialog.pack();
    progressDialog.setLocationRelativeTo(MainWindow.getInstance());
    if (progressDialog.showDialog())
    {
      //Copy the updater jar to temp
      try
      {
        InputStream updaterFileStream = ManagerVersionChecker.class.getResourceAsStream("/se/lantz/updater.jar");
        Path tempDestination = new File(TEMP_FOLDER + "updater.jar").toPath();
        Files.copy(updaterFileStream, tempDestination, StandardCopyOption.REPLACE_EXISTING);
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Fail");
      }
      //Launch new JRE that will copy over the existing file
      String command = ".\\jre\\bin\\java -cp temp\\updater.jar se.lantz.updater.ManagerUpdater";
      
      try
      {
        Runtime.getRuntime().exec(command);
        //Shut down...
        System.exit(0);
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Fail");
      }
    }
    else
    {
      downloadIterrupted = true;
      //Make sure this executes after the downloading has been interrupted
      singleThreadExecutor.execute(() -> {
        try
        {
          Files.deleteIfExists(new File(TEMP_FOLDER + managerMainInstallFile).toPath());
        }
        catch (IOException e)
        {
          ExceptionHandler.handleException(e, "Could not delete partially downloaded file");
        }
      });
    }
  }
  
  
  public static void startDownload(final DownloadDialog downloadDialog)
  {
    downloadIterrupted = false;
    JProgressBar progressBar = downloadDialog.getProgressBar();
    progressBar.setMaximum(100000);
    URL url;
    try
    {
      url = new URL(downloadUrl);
      Files.createDirectories(new File(TEMP_FOLDER).toPath());
      
      HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
      long completeFileSize = httpConnection.getContentLength();
      BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
      FileOutputStream fos = new FileOutputStream(TEMP_FOLDER + managerMainInstallFile);
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
  
  public static boolean isNewVersionAvailable()
  {
    //Ignore all versions that starts with 1
    return !latestVersion.startsWith("1") && !FileManager.getPcuVersionFromManifest().equals(latestVersion);
  }

  public static String getLatestVersion()
  {
    return latestVersion;
  }

  public static String getDownloadUrl()
  {
    return tagloadUrl;
  }

}
