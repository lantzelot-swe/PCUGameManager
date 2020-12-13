package se.lantz.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.InfoModel;
import se.lantz.model.data.GameDetails;

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
    String coverFileName = model.getCoverFile();
    String screen1FileName = model.getScreens1File();
    String screen2FileName = model.getScreens2File();

    String gameName = model.getGamesFile();
    Path gamePath = model.getGamesPath();

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

    if (gamePath != null)
    {
      Path source = gamePath;
      Path target = new File(GAMES + gameName).toPath();

      if (Files.notExists(source))
      {
        System.err.printf("The path %s doesn't exist!", source);
        return;
      }

      try
      {

        compressGzip(source, target);

      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  private void compressGzip(Path source, Path target) throws IOException
  {

    try (GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(target.toFile()));
      FileInputStream fis = new FileInputStream(source.toFile()))
    {
      // copy file
      byte[] buffer = new byte[1024];
      int len;
      while ((len = fis.read(buffer)) > 0)
      {
        gos.write(buffer, 0, len);
      }
    }
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

  public static String generateFileNameFromTitle(String title)
  {
    // All uppercase letters
    // No spaces or special characters
    //The maxi game tool seems to work like this: truncate to 23 characters and then remove all special characters.
    if (title.length() > 23)
    {
      title = title.substring(0, 23);
      logger.debug("FileName: truncating to : {}", title);
    }
    // Do the conversion
    List<Character> forbiddenCharsList =
      " ,:'’-.!+*<>()".chars().mapToObj(item -> (char) item).collect(Collectors.toList());

    List<Character> newName =
      title.chars().mapToObj(item -> (char) item).filter(character -> !forbiddenCharsList.contains(character))
        .map(Character::toUpperCase).collect(Collectors.toList());
    String newNameString = newName.stream().map(String::valueOf).collect(Collectors.joining());
    logger.debug("Game title: \"{}\" ---- New fileName: \"{}\"", title, newNameString);
    return newNameString;
  }

  public void exportGame(GameDetails gameDetails, File targetDir, boolean favFormat, StringBuilder infoBuilder)
  {
    try
    {
      String fileName = generateFileNameFromTitle(gameDetails.getTitle());

      infoBuilder.append("Creating game info file for " + gameDetails.getTitle() + "\n");
      
      String filename = fileName;
      if (favFormat)
      {     
        filename = fileName + ".tsg";
      }
      else
      {
        //Add -ms to comply with the maxi game tool.
        filename = fileName + "-ms.tsg";
      }
      writeGameInfoFile(filename, targetDir, gameDetails, favFormat);
    }
    catch (Exception e)
    {
      String message = "Could not create file for: " + gameDetails.getTitle();
      logger.error(message, e);
      infoBuilder.append(message);
    }

    //TODO copy the other files into the right location

  }

  public static void writeGameInfoFile(String fileName, File targetDir, GameDetails gameDetails, boolean favFormat)
    throws IOException
  {
    Path outDirPath = targetDir.toPath();
    Path filePath = outDirPath.resolve(fileName);
    filePath.toFile().createNewFile();
    FileWriter fw = new FileWriter(filePath.toFile());

    fw.write("T:" + gameDetails.getTitle() + "\n");
    fw.write("X:" + gameDetails.getSystem() + "\n");
    fw.write("D:en:" + gameDetails.getDescription() + "\n");
    if (!gameDetails.getAuthor().isEmpty())
    {
      fw.write("A:" + gameDetails.getAuthor() + "\n");
    }
    if (!gameDetails.getComposer().isEmpty())
    {
      fw.write("M:" + gameDetails.getComposer() + "\n");
    }
    fw.write("E:" + gameDetails.getGenre() + "\n");
    if (favFormat)
    {
      String folderName = generateFileNameFromTitle(gameDetails.getTitle());
      Path gamesfolderPath = outDirPath.resolve(folderName);
      Files.createDirectories(gamesfolderPath);
      fw.write("F:" + folderName + "/" + gameDetails.getGame() + "\n");
      fw.write("C:" + folderName + "/" + gameDetails.getCover() + "\n");
      fw.write("G:" + folderName + "/" + gameDetails.getScreen1() + "\n");
      fw.write("G:" + folderName + "/" + gameDetails.getScreen2() + "\n");
    }
    else
    {
      fw.write("F:" + "games/" + gameDetails.getGame() + "\n");
      fw.write("C:" + "covers/" + gameDetails.getCover() + "\n");
      fw.write("G:" + "screens/" + gameDetails.getScreen1() + "\n");
      fw.write("G:" + "screens/" + gameDetails.getScreen2() + "\n");
    }
    if (!gameDetails.getJoy1().isEmpty())
    {
      fw.write(gameDetails.getJoy1() + "\n");
    }
    if (!gameDetails.getJoy2().isEmpty())
    {
      fw.write(gameDetails.getJoy2() + "\n");
    }
    if (gameDetails.getVerticalShift() != 0)
    {
      fw.write("V:" + gameDetails.getVerticalShift() + "\n");
    }
    fw.close();
  }
}
