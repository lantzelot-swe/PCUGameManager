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
  private static final float OFF_VALUE = -1.5258789E-5f;
  private static final float OFF_THEC64_VALUE = -0.007827878f;

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
    while (poll)
    {
      for (Controller joystick : controllers)
      {
        joystick.poll();
        /* Get the controllers event queue */
        EventQueue queue = joystick.getEventQueue();

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
              SwingUtilities.invokeLater(() -> dialog.scrollRight());
            }
            else if (event.getValue() == OFF_VALUE || event.getValue() == OFF_THEC64_VALUE)
            {
              logger.debug("Event detected! stop!");
              SwingUtilities.invokeLater(() -> dialog.stopScroll());
            }
            else if (event.getValue() == NEGATIVE_VALUE)
            {
              logger.debug("Event detected! left!");
              SwingUtilities.invokeLater(() -> dialog.scrollLeft());
            }
          }

          if (comp.getName().equals("Y Axis"))
          {
            logger.debug("value = " + event.getValue());
            if (event.getValue() == POSITIVE_VALUE)
            {
              logger.debug("Event detected! down!");
              SwingUtilities.invokeLater(() -> dialog.scrollDown());

            }
            else if (event.getValue() == OFF_VALUE || event.getValue() == OFF_THEC64_VALUE)
            {
              logger.debug("Event detected! stop!");
              SwingUtilities.invokeLater(() -> dialog.stopScroll());
            }
            else if (event.getValue() == NEGATIVE_VALUE)
            {
              logger.debug("Event detected! up!");
              SwingUtilities.invokeLater(() -> dialog.scrollUp());
            }
          }

          if (comp.getName().contains("Button"))
          {
            if (event.getValue() == POSITIVE_VALUE)
            {
              logger.debug("Event detected! Button pressed!");
              SwingUtilities.invokeLater(() -> dialog.runGame());
            }
          }
        }
        try
        {
          Thread.sleep(20); //IS this an OK delay?
        }
        catch (InterruptedException ex)
        {
          logger.error("Joystick thread interrupted!", ex);
        }
      }
    }
  }
}
