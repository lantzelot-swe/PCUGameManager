package se.lantz.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.gui.MainWindow;

public class ExceptionHandler extends JDialog
{
  private static final long serialVersionUID = 8120908659146305672L;
  private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
  private JTextArea stacktraceArea;
  private JScrollPane scrollPane;
  private JButton copyButton;
  private Window owner;

  public ExceptionHandler(Window owner)
  {
    super(owner);
    this.owner = owner;
    initDialog();
  }

  private void initDialog()
  {
    setTitle("Unexpected error");
    setMinimumSize(new Dimension(250, 200));
    setSize(new Dimension(700, 700));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    GridBagLayout gridBagLayout = new GridBagLayout();
    getContentPane().setLayout(gridBagLayout);
    GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
    scrollPaneConstraints.insets = new Insets(5, 5, 5, 0);
    scrollPaneConstraints.weighty = 1.0;
    scrollPaneConstraints.weightx = 1.0;
    scrollPaneConstraints.fill = GridBagConstraints.BOTH;
    scrollPaneConstraints.gridx = 0;
    scrollPaneConstraints.gridy = 0;
    getContentPane().add(getScrollPane(), scrollPaneConstraints);
    GridBagConstraints copyButtonConstraints = new GridBagConstraints();
    copyButtonConstraints.weightx = 1.0;
    copyButtonConstraints.anchor = GridBagConstraints.EAST;
    copyButtonConstraints.insets = new Insets(5, 5, 5, 5);
    copyButtonConstraints.gridx = 0;
    copyButtonConstraints.gridy = 1;
    getContentPane().add(getCopyButton(), copyButtonConstraints);

    ActionListener escapeAction = e -> getCopyButton().doClick();
    getRootPane().registerKeyboardAction(escapeAction,
                                         KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                                         JComponent.WHEN_IN_FOCUSED_WINDOW);

    getStacktraceArea()
      .registerKeyboardAction(escapeAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);
    setLocationRelativeTo(owner);
    stacktraceArea.setEditable(false);
  }

  private JTextArea getStacktraceArea()
  {
    if (stacktraceArea == null)
    {
      stacktraceArea = new JTextArea();

    }
    return stacktraceArea;
  }

  private JScrollPane getScrollPane()
  {
    if (scrollPane == null)
    {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getStacktraceArea());
    }
    return scrollPane;
  }

  private JButton getCopyButton()
  {
    if (copyButton == null)
    {
      copyButton = new JButton("Copy to clipboard and close");
      copyButton.addActionListener(e -> copyToClipboardAndClose());
    }
    return copyButton;
  }

  private void copyToClipboardAndClose()
  {
    //Copy to clipboard
    final StringSelection selection = new StringSelection(stacktraceArea.getText());

    final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(selection, selection);
    setVisible(false);
  }

  public static void handleException(final Throwable ex, final String message)
  {
    logger.error("message", ex);
    Window owner = null;
    if (MainWindow.isInitialized())
    {
      owner = MainWindow.getInstance();
    }
    ExceptionHandler dialog = new ExceptionHandler(owner);
    if (owner != null)
    {
      dialog.setLocationRelativeTo(owner);
    }
    StringBuilder builder = new StringBuilder();
    if (message != null)
    {
      builder.append(message + "\n\n");
    }
    builder.append(getStackTraceAsString(ex));

    dialog.stacktraceArea.setText(builder.toString());
    dialog.stacktraceArea.setCaretPosition(0);
    dialog.setVisible(true);
  }

  private static String getStackTraceAsString(Throwable throwable)
  {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
}
