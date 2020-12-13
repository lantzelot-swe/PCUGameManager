package se.lantz;

import java.awt.Dimension;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
      
//      UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
      // Set System L&F
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
    {
      ExceptionHandler.handleException(e, "Startup failure");
    }

    SwingUtilities.invokeLater(() -> {

      MainWindow mainWindow = MainWindow.getInstance();
      mainWindow.setSize(1500, 960);
      mainWindow.setMinimumSize(new Dimension(1400, 960));
      mainWindow.setVisible(true);
      mainWindow.setLocationRelativeTo(null);
      mainWindow.initialize();
    });
  }

}
