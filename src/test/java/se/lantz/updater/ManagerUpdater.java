package se.lantz.updater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;

public class ManagerUpdater
{
  public static void main(String[] args)
  {
    Path newFile = new File("./temp/PCUGameManager.exe").toPath().toAbsolutePath();
    Path originalFile = new File("./PCUGameManager.exe").toPath().toAbsolutePath();
    int tries = 0;
    boolean copied = false;
    //Try 3 times with a delay before quitting
    while (tries < 3 && !copied)
    {
      try
      {
        Thread.currentThread().sleep(500);
        Files.copy(newFile, originalFile, StandardCopyOption.REPLACE_EXISTING);
        Files.delete(newFile);
        copied = true;
      }
      catch (IOException | InterruptedException e)
      {
        tries++;
      }
    }
    if (!copied)
    {
      JOptionPane.showMessageDialog(null,
                                    "Something went wrong when updating the manager. Please try again.",
                                    "Could not update",
                                    JOptionPane.ERROR_MESSAGE);
    }
    else
    {
      //Launch the new version
      try
      {
        Runtime.getRuntime().exec("PCUGameManager.exe");
      }
      catch (Exception e)
      {
        JOptionPane
          .showMessageDialog(null,
                             "Something went wrong when launching the manager. Please try to run it manually again.",
                             "Could not restart",
                             JOptionPane.ERROR_MESSAGE);
      }
    }
    System.exit(0);
  }

}
