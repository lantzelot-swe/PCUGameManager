package se.lantz.gui.install;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;

import se.lantz.util.ExceptionHandler;
import se.lantz.util.ManagerVersionChecker;

public class VersionDownloadPanel extends JPanel
{
  private JEditorPane editorPane;
  private String message;

  public VersionDownloadPanel(String message)
  {
    this.message = message;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_editorPane = new GridBagConstraints();
    gbc_editorPane.weighty = 1.0;
    gbc_editorPane.weightx = 1.0;
    gbc_editorPane.insets = new Insets(10, 10, 10, 10);
    gbc_editorPane.fill = GridBagConstraints.BOTH;
    gbc_editorPane.gridx = 0;
    gbc_editorPane.gridy = 0;
    add(getEditorPane(), gbc_editorPane);
  }

  private JEditorPane getEditorPane()
  {
    if (editorPane == null)
    {
      editorPane = new JEditorPane("text/html", message);
      editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
      editorPane.setFont(UIManager.getDefaults().getFont("Label.font"));
      editorPane.setEditable(false);
      editorPane.setOpaque(false);
      editorPane.addHyperlinkListener((hle) -> {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()) && Desktop.isDesktopSupported() &&
          Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
        {
          try
          {
            Desktop.getDesktop().browse(hle.getURL().toURI());
          }
          catch (IOException | URISyntaxException e)
          {
            ExceptionHandler.handleException(e, "Could not open default browser");
          }
        }
      });
    }
    return editorPane;
  }
}
