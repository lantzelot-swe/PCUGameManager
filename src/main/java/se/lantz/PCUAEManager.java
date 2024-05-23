package se.lantz;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import se.lantz.gui.MainWindow;
import se.lantz.gui.install.ManagerDownloadDialog;
import se.lantz.model.PreferencesModel;
import se.lantz.util.ExceptionHandler;
import se.lantz.util.FileManager;
import se.lantz.util.ManagerVersionChecker;
import se.lantz.util.TopLevelExceptionHandler;

public class PCUAEManager
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
      ToolTipManager.sharedInstance().setDismissDelay(10000);
      //Setup C64 font
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      InputStream fontStream = PCUAEManager.class.getResourceAsStream("/se/lantz/C64_Pro-STYLE.ttf");
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, fontStream));
    }
    catch (Exception e)
    {
      ExceptionHandler.handleException(e, "Startup failure");
    }

    //Make sure all folders are available
    try
    {
      Files.createDirectories(Paths.get("./databases/"));
      Files.createDirectories(Paths.get("./pcuae-install/"));

      if (!Files.list(Paths.get("./databases/")).findAny().isPresent() && Paths.get("./games/").toFile().exists())
      {
        //Migrate to version 3.x
        migrateDirectories();
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> {

      MainWindow mainWindow = MainWindow.getInstance();

      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      int width = gd.getDisplayMode().getWidth();
      int height = gd.getDisplayMode().getHeight();

      mainWindow.setSize(Math.min(width, 1500), Math.min(height - 40, 850));
      mainWindow.setMinimumSize(new Dimension(Math.min(width, 1300), Math.min(height - 40, 725)));
      mainWindow.setLocationRelativeTo(null);
      mainWindow.initialize();
      mainWindow.setVisible(true);

      //Check for new versions at startup, but only when running stand-alone, not during development.
      if (!FileManager.getPcuVersionFromManifest().isEmpty())
      {
        PreferencesModel prefModel = new PreferencesModel();

        if (prefModel.isCheckManagerVersionAtStartup())
        {
          ManagerVersionChecker.fetchLatestVersionFromGithub();
          if (ManagerVersionChecker.isNewVersionAvailable())
          {
            ManagerDownloadDialog dialog = new ManagerDownloadDialog(MainWindow.getInstance());
            dialog.pack();
            dialog.setLocationRelativeTo(MainWindow.getInstance());
            if (dialog.showDialog())
            {
              ManagerVersionChecker.updateVersion();
              return;
            }
          }
        }
        if (prefModel.isCheckPCUAEVersionAtStartup())
        {
          //Check main PCUAE install file version
          mainWindow.checkForNewPCUAEVersionAtStartup();
        }
      }
    });
  }

  private static void migrateDirectories()
  {
    try
    {
      Path mainDb = Paths.get("./databases/MainDb");
      Files.createDirectories(mainDb);
      Files.move(Paths.get("./pcusb.db"), mainDb.resolve("pcusb.db"), StandardCopyOption.ATOMIC_MOVE);
      Files.move(Paths.get("./games/"), mainDb.resolve("games"), StandardCopyOption.ATOMIC_MOVE);
      Files.move(Paths.get("./screens/"), mainDb.resolve("screens"), StandardCopyOption.ATOMIC_MOVE);
      Files.move(Paths.get("./covers/"), mainDb.resolve("covers"), StandardCopyOption.ATOMIC_MOVE);
      Files.move(Paths.get("./saves/"), mainDb.resolve("saves"), StandardCopyOption.ATOMIC_MOVE);
      Files.move(Paths.get("./extradisks/"), mainDb.resolve("extradisks"), StandardCopyOption.ATOMIC_MOVE);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not move main Db");
    }
  }

}
