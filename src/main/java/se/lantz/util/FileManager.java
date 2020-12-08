package se.lantz.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.InfoModel;

public class FileManager
{
  private static final String GAMES = "./games/";
  private static final String SCREENS = "./screens/";
  private static final String COVERS = "./covers/";

  private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

  private InfoModel model;

  public FileManager(InfoModel model)
  {
    this.model = model;
  }
  
  public void saveFiles()
  {
    //Check if title is different that in db, then rename existing files!
    if (model.isTitleChanged())
    {
      //Rename existing covers and screens and game file
      renameFiles();     
    }
    
    //Fetch images that has been added
    BufferedImage cover = model.getCoverImage();
    BufferedImage screen1 = model.getScreen1Image();
    BufferedImage screen2 = model.getScreen2Image();
    //TODO: game file
    String coverFileName = model.getCoverFile();
    String screen1FileName = model.getScreens1File();
    String screen2FileName = model.getScreens2File();
    
    //Store on disk with the name in the models. The UI must make sure the names is according to the Maxi format.
    
    //Resize the files, cover size = 122x175
    if (cover != null)
    {
      try
      {      
        Image coverToSave = cover.getScaledInstance(122, 175, Image.SCALE_DEFAULT);
        BufferedImage copyOfImage =
          new BufferedImage(coverToSave.getWidth(null), coverToSave.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(coverToSave, 0, 0, null);
        g.dispose();
        
        File outputfile = new File(COVERS + coverFileName);
        ImageIO.write(copyOfImage, "png", outputfile);
      }
      catch (IOException e)
      {
        logger.error("Could not store cover", e);
      }
    }
    
    if (screen1 != null)
    {
      try
      {  
        File outputfile = new File(SCREENS + screen1FileName);
        ImageIO.write(screen1, "png", outputfile);
      }
      catch (IOException e)
      {
        logger.error("Could not store screen1", e);
      }
    }
    
    if (screen2 != null)
    {
      try
      {  
        File outputfile = new File(SCREENS + screen2FileName);
        ImageIO.write(screen2, "png", outputfile);
      }
      catch (IOException e)
      {
        logger.error("Could not store screen2", e);
      }
    }
    //TODO: game file
  }
  
  private void renameFiles()
  {
    String oldTitle = generateFileNameFromTitle(model.getTitleInDb());
    if (!model.getCoverFile().isEmpty())
    {
      File oldCover = new File(COVERS + oldTitle + "-cover.png");
      File newCover = new File(COVERS + model.getCoverFile());
      if (oldCover.renameTo(newCover))
      {
        logger.debug("Renamed cover {} to {}", oldCover.getName(), newCover.getName());
      }
      else
      {
        logger.debug("Could NOT rename cover {} to {}", oldCover.getName(), newCover.getName());
      }
    }
    if (!model.getScreens1File().isEmpty())
    {
      File oldScreen1 = new File(SCREENS + oldTitle + "-00.png");
      File newScreen1 = new File(SCREENS + model.getScreens1File());
      if (oldScreen1.renameTo(newScreen1))
      {
        logger.debug("Renamed screen1 {} to {}", oldScreen1.getName(), newScreen1.getName());
      }
      else
      {
        logger.debug("Could NOT rename screen1 {} to {}", oldScreen1.getName(), newScreen1.getName());
      }
    }
    if (!model.getScreens2File().isEmpty())
    {
      File oldScreen2 = new File(SCREENS + oldTitle + "-01.png");
      File newScreen2 = new File(SCREENS + model.getScreens2File());
      if (oldScreen2.renameTo(newScreen2))
      {
        logger.debug("Renamed screen2 {} to {}", oldScreen2.getName(), newScreen2.getName());
      }
      else
      {
        logger.debug("Could NOT rename screen2 {} to {}", oldScreen2.getName(), newScreen2.getName());
      }
    }
    if (!model.getGamesFile().isEmpty())
    {
      String fileEnding = model.getGamesFile().substring(model.getGamesFile().indexOf("."));
      File oldGame = new File(GAMES + oldTitle + fileEnding);
      File newGame = new File(GAMES + model.getGamesFile());
      if (oldGame.renameTo(newGame))
      {
        logger.debug("Renamed game {} to {}", oldGame.getName(), newGame.getName());
      }
      else
      {
        logger.debug("Could NOT rename game {} to {}", oldGame.getName(), newGame.getName());
      }
    }  
  }
  
  public static String generateFileNameFromTitle(String title) {
    // All uppercase letters
    // No spaces or special characters
    //The maxi game tool seems to work like this: truncate to 23 characters and then remove all special characters.
    if (title.length() > 23)
    {
      title = title.substring(0, 23);
      logger.debug("FileName: truncating to : {}", title);
    }
    // System.out.println("Game title: " + fileNameList.get(0));
    // Do the conversion
    List<Character> forbiddenCharsList = " ,:'’-.!+*<>()".chars().mapToObj(item -> (char) item)
        .collect(Collectors.toList());

    List<Character> newName = title.chars().mapToObj(item -> (char) item)
        .filter(character -> !forbiddenCharsList.contains(character))
        .map(character -> Character.toUpperCase(character)).collect(Collectors.toList());
    String newNameString = newName.stream().map(String::valueOf).collect(Collectors.joining());
    logger.debug("Game title: \"{}\" ---- New fileName: \"{}\"", title, newNameString);
    return newNameString;
  }
}
