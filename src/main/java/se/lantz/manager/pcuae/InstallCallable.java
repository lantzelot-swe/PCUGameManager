package se.lantz.manager.pcuae;

import java.io.File;
import java.nio.file.Path;

import dyorgio.runtime.out.process.CallableSerializable;

class InstallCallable implements CallableSerializable<Integer>
{
  private static final long serialVersionUID = -5798028775642035604L;
  String installFileName;

  InstallCallable(String installFileName)
  {
    this.installFileName = installFileName;
  }

  @Override
  public Integer call() throws Exception
  {
    int returnValue = 0;
    File fileDir = new File("./pcuae-install/");
    //Read file from dir and see if we can extract it
    Path installFilePath = new File("./pcuae-install/" + installFileName).toPath();
    returnValue = Runtime.getRuntime().exec(installFilePath.toString(), null, fileDir).waitFor();
    return returnValue;
  }
}