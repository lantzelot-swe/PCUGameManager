package se.lantz.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import se.lantz.model.MainViewModel;
import se.lantz.util.FileManager;

public final class MainWindow extends JFrame
{
  /**
   * 
   */
  private static final long serialVersionUID = 2359068353897458894L;

  private MainPanel mainPanel;
  private JMenuBar menuBar;
  private final MainViewModel uiModel;
  private final MenuManager menuManager;

  private static MainWindow instance = null;

  private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
  private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

  public static MainWindow getInstance()
  {
    if (instance == null)
    {
      instance = new MainWindow();
    }
    return instance;
  }

  public static boolean isInitialized()
  {
    return instance != null;
  }

  private MainWindow()
  {
    this.setIconImage(new ImageIcon(getClass().getResource("/se/lantz/FrameIcon.png")).getImage());
    this.setTitle("PCU Game Manager");
    uiModel = new MainViewModel();
    menuManager = new MenuManager(uiModel, this);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(java.awt.event.WindowEvent e)
        {
          menuManager.triggerExit();
        }
      });
    getContentPane().add(getMainPanel(), BorderLayout.CENTER);

    this.setJMenuBar(getMainMenuBar());

    //Update title with version if available
    String versionValue = FileManager.getPcuVersionFromManifest();
    if (!versionValue.isEmpty())
    {
      setTitle("PCU Game Manager v." + versionValue);
    }
  }

  public void initialize()
  {
    getMainPanel().initialize();
    menuManager.intialize();
  }

  public MainPanel getMainPanel()
  {
    if (mainPanel == null)
    {
      mainPanel = new MainPanel(uiModel);
    }
    return mainPanel;
  }

  private JMenuBar getMainMenuBar()
  {
    if (menuBar == null)
    {
      menuBar = new JMenuBar();
      for (JMenu menu : menuManager.getMenues())
      {
        menuBar.add(menu);
      }
    }
    return menuBar;
  }

  public void reloadCurrentGameView()
  {
    getMainPanel().reloadCurrentGameView();
  }

  public void repaintAfterModifications()
  {
    getMainPanel().repaintAfterModifications();
  }

  public void selectViewAfterRestore()
  {
    getMainPanel().selectViewAfterRestore();
  }

  public void setWaitCursor(boolean wait)
  {
    if (wait)
    {
      this.setCursor(waitCursor);
    }
    else
    {
      this.setCursor(defaultCursor);
    }
  }
}
