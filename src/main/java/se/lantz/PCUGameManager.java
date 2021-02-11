package se.lantz;

import java.awt.Dimension;

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
      mainWindow.setSize(1400, 840);
      mainWindow.setMinimumSize(new Dimension(1300, 700));
      mainWindow.setVisible(true);
      mainWindow.setLocationRelativeTo(null);
      mainWindow.initialize();
    });
  }

}
