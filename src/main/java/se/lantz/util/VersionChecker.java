package se.lantz.util;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class VersionChecker
{
  private static String latestVersion = "";
  private static String downloadUrl = "";
  private VersionChecker()
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
      downloadUrl = root.getAsJsonObject().get("html_url").getAsString();
    }
    catch (IOException ex)
    {
      ExceptionHandler.handleException(ex, "Could not check version");
    }
  }
  
  public static boolean isNewVersionAvailable()
  {
    return !FileManager.getPcuVersionFromManifest().equals(latestVersion);
  }

  public static String getLatestVersion()
  {
    return latestVersion;
  }

  public static String getDownloadUrl()
  {
    return downloadUrl;
  }

}
