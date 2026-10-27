package se.lantz.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import se.lantz.gui.carousel.CarouselPreviewDialog;

public class InputController
{
  private static final Logger logger = LoggerFactory.getLogger(InputController.class);

  private static final float POSITIVE_VALUE = 1.0f;
  private static final float NEGATIVE_VALUE = -1.0f;

  private static final long REPEAT_EVENT_TIME = 350;

  /* Create an event object for the underlying plugin to populate */
  Event event = new Event();
  private static volatile List<Controller> controllers = Collections.synchronizedList(new ArrayList<>());
  private volatile boolean poll = true;

  private CarouselPreviewDialog dialog;

  private static ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
  private Future<String> runningTask;

  public InputController(CarouselPreviewDialog dialog)
  {
    this.dialog = dialog;
    scanAvailableControllers();
  }

  /**
   * Stops the joystick polling thread if running and scans for available controllers. The joystick pooling thread is
   * started after the scan.
   */
  public void scanAvailableControllers()
  {
    try
    {
      this.poll = false;
      //Wait for the running task to complete
      if (runningTask != null)
      {
        runningTask.get();
      }
      logger.debug("Scan for available controllers...");
      // Find the default environment constructor (it's package-private)
      Class<?> clazz = Class.forName("net.java.games.input.DefaultControllerEnvironment");
      Constructor<?> constructor = clazz.getDeclaredConstructors()[0];

      // Deactivate access control checks to allow instantiation
      constructor.setAccessible(true);

      // Create a brand new environment instance and fetch updated controllers
      ControllerEnvironment env = (ControllerEnvironment) constructor.newInstance();
      controllers.clear();
      for (Controller controller : env.getControllers())
      {
        logger.debug(controller.getName() + ":" + controller.getType());
        if (controller.getType().equals(Type.STICK) || controller.getType().equals(Type.GAMEPAD))
        {
          controllers.add(controller);
        }
      }
      this.poll = true;
      runningTask = threadExecutor.submit(() -> processEvents(), "");
    }
    catch (Exception e)
    {
      logger.error("rescan of controllers failed.", e);
    }
  }

  public void stop()
  {
    this.poll = false;
    logger.debug("Stopping joystick polling thread");
  }

  private void processEvents()
  {
    long lastRight = 0L;
    long lastLeft = 0L;
    long lastUp = 0L;
    long lastDown = 0L;

    while (poll)
    {
      for (Controller joystick : controllers)
      {
        joystick.poll();
        /* Get the controllers event queue */
        EventQueue queue = joystick.getEventQueue();
        //booleans to filter out multiple repeated events for analog joystick adapters
        boolean buttonPressed = false;
        boolean rightDetected = false;
        boolean leftDetected = false;
        boolean upDetected = false;
        boolean downDetected = false;
        boolean stopDetected = false;

        /* For each object in the queue */
        while (queue.getNextEvent(event))
        {
          /* Get event component */
          Component comp = event.getComponent();

          Identifier id = comp.getIdentifier();
          logger.debug("Comp identifier = " + comp.getIdentifier().getName());
          if (id == Component.Identifier.Axis.X)
          {
            logger.debug("value = " + event.getValue());
            if (event.getValue() == POSITIVE_VALUE)
            {
              logger.debug("Event detected! right!");
              rightDetected = true;
            }
            else if (event.getValue() == NEGATIVE_VALUE)
            {
              logger.debug("Event detected! left!");
              leftDetected = true;
            }
            else
            {
              logger.debug("Event detected! stop!");
              stopDetected = true;
            }
          }

          if (id == Component.Identifier.Axis.Y)
          {
            logger.debug("value = " + event.getValue());
            if (event.getValue() == POSITIVE_VALUE)
            {
              logger.debug("Event detected! down!");
              downDetected = true;

            }
            else if (event.getValue() == NEGATIVE_VALUE)
            {
              logger.debug("Event detected! up!");
              upDetected = true;
            }
            else
            {
              logger.debug("Event detected! stop!");
              stopDetected = true;
            }
          }

          if (id instanceof Component.Identifier.Button)
          {
            if (event.getValue() == POSITIVE_VALUE)
            {
              buttonPressed = true;
              logger.debug("Event detected! Button pressed!");
            }
            else
            {
              logger.debug("Event detected! Button Released!");
            }
          }
        }

        //React to detected events, filter out events that occurs within REPEAT_EVENT_TIME
        if (rightDetected && (System.currentTimeMillis() - lastRight) > REPEAT_EVENT_TIME)
        {
          logger.debug("*** scrolling right!");
          lastRight = System.currentTimeMillis();
          SwingUtilities.invokeLater(() -> dialog.scrollRight());
        }
        if (leftDetected && (System.currentTimeMillis() - lastLeft) > REPEAT_EVENT_TIME)
        {
          logger.debug("*** scrolling left!");
          lastLeft = System.currentTimeMillis();
          SwingUtilities.invokeLater(() -> dialog.scrollLeft());
        }
        if (upDetected && (System.currentTimeMillis() - lastUp) > REPEAT_EVENT_TIME)
        {
          logger.debug("*** scrolling Up!");
          lastUp = System.currentTimeMillis();
          SwingUtilities.invokeLater(() -> dialog.scrollUp());
        }
        if (downDetected && (System.currentTimeMillis() - lastDown) > REPEAT_EVENT_TIME)
        {
          logger.debug("*** scrolling Down!");
          lastDown = System.currentTimeMillis();
          SwingUtilities.invokeLater(() -> dialog.scrollDown());
        }

        if (stopDetected)
        {
          if (event.getValue() != POSITIVE_VALUE && event.getValue() != NEGATIVE_VALUE)
          {
            //Last event was a stop, lets stop
            SwingUtilities.invokeLater(() -> dialog.stopScroll());
            logger.debug("*** stop scroll!");
          }
        }
        else if (buttonPressed)
        {
          logger.debug("*** Button pressed!");
          SwingUtilities.invokeLater(() -> dialog.runGame());
        }
        try
        {
          Thread.sleep(20);
        }
        catch (InterruptedException ex)
        {
          logger.error("Joystick thread interrupted!", ex);
        }
      }
    }
    logger.debug("Exiting joystick pooling task.");
  }
}
