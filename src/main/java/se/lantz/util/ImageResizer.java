package se.lantz.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * This program demonstrates how to resize an image.
 *
 * @author www.codejava.net
 *
 */
public class ImageResizer
{

  public static BufferedImage scale(BufferedImage before, double scale)
  {
    int w = before.getWidth();
    int h = before.getHeight();
    // Create a new image of the proper size
    int w2 = (int) (w * scale);
    int h2 = (int) (h * scale);
    BufferedImage after = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
    AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
    AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);

    scaleOp.filter(before, after);
    return after;
  }

  /**
   * Resizes an image to a absolute width and height (the image may not be proportional)
   * 
   * @param inputImagePath Path of the original image
   * @param outputImagePath Path to save the resized image
   * @param scaledWidth absolute width in pixels
   * @param scaledHeight absolute height in pixels
   * @throws IOException
   */
  public static ImageIcon resize(String inputImagePath, int scaledWidth, int scaledHeight) throws IOException
  {
    // reads input image
    File inputFile = new File(inputImagePath);
    BufferedImage inputImage = ImageIO.read(inputFile);
    // creates output image
    BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

    // scales the input image to the output image
    Graphics2D g2d = outputImage.createGraphics();
    g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
    g2d.dispose();
    return new ImageIcon(outputImage);
  }

  /**
   * Resizes an image by a percentage of original size (proportional).
   * 
   * @param inputImagePath Path of the original image
   * @param outputImagePath Path to save the resized image
   * @param percent a double number specifies percentage of the output image over the input image.
   * @throws IOException
   */
  public static void resize(String inputImagePath, double percent) throws IOException
  {
    File inputFile = new File(inputImagePath);
    BufferedImage inputImage = ImageIO.read(inputFile);
    int scaledWidth = (int) (inputImage.getWidth() * percent);
    int scaledHeight = (int) (inputImage.getHeight() * percent);
    resize(inputImagePath, scaledWidth, scaledHeight);
  }
}
