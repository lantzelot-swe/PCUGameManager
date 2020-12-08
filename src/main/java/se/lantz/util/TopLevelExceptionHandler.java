package se.lantz.util;

import java.awt.IllegalComponentStateException;

import javax.swing.SwingUtilities;

/**
 * An exception handler that replaces the default exception handler in Java. This class catches all uncaught exceptions
 * in the EventDispatchThread. This gives a consistent error handling strategy for the application.
 *
 */
public class TopLevelExceptionHandler implements Thread.UncaughtExceptionHandler
{
  @Override
  public void uncaughtException(Thread t, Throwable e)
  {
    // Workaround for IMOD-86609 (Java bug JDK-8179665)
    if (e instanceof IllegalComponentStateException &&
      "component must be showing on the screen to determine its location".equals(e.getMessage()))
    {
      return;
    }
    SwingUtilities.invokeLater(() -> ExceptionHandler.handleException(e, ""));
  }
}