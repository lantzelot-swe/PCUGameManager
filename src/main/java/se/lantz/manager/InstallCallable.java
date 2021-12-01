package se.lantz.manager;

import java.io.File;
import java.io.IOException;
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
    try
    {
      File fileDir = new File("./pcuae-install/");
      //Read file from dir and see if we can extract it
      Path installFilePath = new File("./pcuae-install/" + installFileName).toPath();
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
  }
}