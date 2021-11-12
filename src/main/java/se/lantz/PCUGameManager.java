package se.lantz;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import se.lantz.gui.MainWindow;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.TopLevelExceptionHandler;

public class PCUGameManager
{

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
  {
    try
    {
      //Set Exceptionhandler
      Thread.setDefaultUncaughtExceptionHandler(new TopLevelExceptionHandler());
      // Set System L&F
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e)
    {
      ExceptionHandler.handleException(e, "Startup failure");
    }

    SwingUtilities.invokeLater(() -> {

      MainWindow mainWindow = MainWindow.getInstance();
      
      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      int width = gd.getDisplayMode().getWidth();
      int height = gd.getDisplayMode().getHeight();
      
      mainWindow.setSize(Math.min(width, 1500), Math.min(height-40, 825));
      mainWindow.setMinimumSize(new Dimension(Math.min(width, 1300), Math.min(height-40, 700)));
      mainWindow.setVisible(true);
      mainWindow.setLocationRelativeTo(null);
      mainWindow.initialize();
      
      //Make sure all folders are available
      try
      {
        Files.createDirectories(Paths.get("./screens/"));
        Files.createDirectories(Paths.get("./covers/"));
        Files.createDirectories(Paths.get("./games/"));
        Files.createDirectories(Paths.get("./saves/"));
      }
      catch (IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

}
