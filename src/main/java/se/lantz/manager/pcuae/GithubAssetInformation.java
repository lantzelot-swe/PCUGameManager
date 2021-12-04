package se.lantz.manager.pcuae;

public class GithubAssetInformation
{
  String latestVersion = "";
  String releaseTagUrl = "";
  String downloadUrl = "";
  String installFile = "";

  public GithubAssetInformation()
  {
  }

  public String getLatestVersion()
  {
    return latestVersion;
  }

  public void setLatestVersion(String latestVersion)
  {
    this.latestVersion = latestVersion;
  }

  public String getReleaseTagUrl()
  {
    return releaseTagUrl;
  }

  public void setReleaseTagUrl(String releaseTagUrl)
  {
    this.releaseTagUrl = releaseTagUrl;
  }

  public String getDownloadUrl()
  {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl)
  {
    this.downloadUrl = downloadUrl;
  }

  public String getInstallFile()
  {
    return installFile;
  }

  public void setInstallFile(String installFile)
  {
    this.installFile = installFile;
  }

}
