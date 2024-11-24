package se.lantz.util;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.games.input.Component;
import net.java.games.input.Controller;
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
  Controller[] controllers;
  private volatile boolean poll = true;

  private CarouselPreviewDialog dialog;

  public InputController(CarouselPreviewDialog dialog)
  {
    this.dialog = dialog;
    /* Get the available controllers, this includes mouse and keyboards, but that doesn't really matter */
    controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
    Thread thread = new Thread(() -> processEvents(), "Joystick polling thread");
    thread.start();
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

          if (comp.getName().equals("X Axis"))
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

          if (comp.getName().equals("Y Axis"))
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

          if (comp.getName().contains("Button"))
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
  }
}
