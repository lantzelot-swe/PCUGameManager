package se.lantz.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import se.lantz.db.DbConnector;
import se.lantz.manager.SavedStatesManager;
import se.lantz.model.InfoModel;
import se.lantz.model.MainViewModel;
import se.lantz.model.SavedStatesModel;
import se.lantz.model.SavedStatesModel.SAVESTATE;
import se.lantz.model.SystemModel;
import se.lantz.model.data.GameDetails;
import se.lantz.model.data.GameValidationDetails;

public class FileManager
{
  public static BufferedImage emptyC64Cover;
  public static BufferedImage emptyVic20Cover;
  public static BufferedImage emptyC64Screenshot;
  public static BufferedImage emptyVic20Screenshot;

  public static final String GAMES = "./games/";
  private static final String SCREENS = "./screens/";
  private static final String COVERS = "./covers/";
  private static final String SAVES = "./saves/";
  private static final String BACKUP = "./backup/";

  private static final Path TEMP_PATH = Paths.get("./temp");
  private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

  private static Properties fileProperties;

  private MainViewModel model;
  private InfoModel infoModel;
  private SystemModel systemModel;
  private SavedStatesModel savedStatesModel;
  private static ExecutorService executor = Executors.newSingleThreadExecutor();

  private static List<String> validFileEndingList =
    Arrays.asList("d64", "t64", "prg", "p00", "d81", "d71", "x64", "g64", "tap", "crt", "vsf");

  static
  {
    try
    {
      emptyC64Cover = ImageIO.read(FileManager.class.getResource("/se/lantz/CoverMissing-C64.png"));
      emptyVic20Cover = ImageIO.read(FileManager.class.getResource("/se/lantz/CoverMissing-VIC20.png"));
      emptyC64Screenshot = ImageIO.read(FileManager.class.getResource("/se/lantz/MissingScreenshot-C64.png"));
      emptyVic20Screenshot = ImageIO.read(FileManager.class.getResource("/se/lantz/MissingScreenshot-VIC20.png"));
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not read missing cover images.");
    }
  }

  public FileManager(MainViewModel model)
  {
    this.infoModel = model.getInfoModel();
    this.systemModel = model.getSystemModel();
    this.savedStatesModel = model.getSavedStatesModel();
    this.model = model;
  }

  public static InputStream getMissingC64GameFile() throws URISyntaxException
  {
    return FileManager.class.getResourceAsStream("/se/lantz/MissingGame-C64.vsf.gz");
  }

  public static InputStream getMissingVIC20GameFile() throws URISyntaxException
  {
    return FileManager.class.getResourceAsStream("/se/lantz/MissingGame-Vic20.vsf.gz");
  }

  public static BufferedImage getInfoSlotCoverImage()
  {
    BufferedImage coverImage = null;
    try
    {
      coverImage = ImageIO.read(FileManager.class.getResource("/se/lantz/InfoSlotCover.png"));
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not read cover image.");
    }
    return coverImage;
  }

  public static BufferedImage getInfoSlotScreenImage(boolean first)
  {
    BufferedImage coverImage = null;
    try
    {
      if (first)
      {
        coverImage = ImageIO.read(FileManager.class.getResource("/se/lantz/InfoSlotScreen1.png"));
      }
      else
      {
        coverImage = ImageIO.read(FileManager.class.getResource("/se/lantz/InfoSlotScreen2.png"));
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not read info slot screen image.");
    }
    return coverImage;
  }

  public void saveFiles()
  {
    //Rename existing covers and screens and game file if needed
    renameFiles();

    //Fetch images that has been added
    BufferedImage cover = infoModel.getCoverImage();
    BufferedImage screen1 = infoModel.getScreen1Image();
    BufferedImage screen2 = infoModel.getScreen2Image();
    String coverFileName = infoModel.getCoverFile();
    String screen1FileName = infoModel.getScreens1File();
    String screen2FileName = infoModel.getScreens2File();

    String gameName = infoModel.getGamesFile();
    Path gamePath = infoModel.getGamesPath();

    //Store on disk with the name in the models. The UI must make sure the names is according to the Maxi format.

    //Resize the files, cover size = 122x175
    if (cover != null)
    {
      try
      {
        BufferedImage imageToSave = cover;
        if (cover.getWidth() != 122 || cover.getHeight() != 175)
        {
          Image scaledCoverImage = cover.getScaledInstance(122, 175, Image.SCALE_SMOOTH);
          imageToSave = new BufferedImage(scaledCoverImage.getWidth(null),
                                          scaledCoverImage.getHeight(null),
                                          BufferedImage.TYPE_INT_ARGB);
          Graphics g = imageToSave.createGraphics();
          g.drawImage(scaledCoverImage, 0, 0, null);
          g.dispose();
        }
        File outputfile = new File(COVERS + coverFileName);
        ImageIO.write(imageToSave, "png", outputfile);
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not store cover");
      }
    }

    if (screen1 != null)
    {
      try
      {
        if (screen1FileName.isEmpty())
        {
          screen1FileName = generateFileNameFromTitle(infoModel.getTitle(), infoModel.getDuplicateIndex()) + "-00.png";
        }
        File outputfile = new File(SCREENS + screen1FileName);
        //Scale if not the right size
        screen1 = scaleImageTo320x200x32bit(screen1);
        ImageIO.write(screen1, "png", outputfile);
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not store screen1");
      }
    }

    if (screen2 != null)
    {
      try
      {
        if (screen2FileName.isEmpty())
        {
          screen2FileName = generateFileNameFromTitle(infoModel.getTitle(), infoModel.getDuplicateIndex()) + "-01.png";
        }
        File outputfile = new File(SCREENS + screen2FileName);
        //Scale if not the right size
        screen2 = scaleImageTo320x200x32bit(screen2);
        ImageIO.write(screen2, "png", outputfile);
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not store screen2");
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
        if (shouldCompressFile(source.toString()))
        {
          compressGzip(source, target);
        }
        else
        {
          //Just copy
          Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not copy game file from " + source.toString());
      }
    }
  }

  public static boolean shouldCompressFile(String filePath)
  {
    String lowerCasePath = filePath.toLowerCase();
    return !(lowerCasePath.endsWith(".gz") || lowerCasePath.endsWith(".d81") || lowerCasePath.endsWith(".prg") ||
      lowerCasePath.endsWith(".p00"));
  }

  public static void compressGzip(Path source, Path target) throws IOException
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

  public static void decompressGzip(Path source, Path target) throws IOException
  {
    try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(source.toFile()));
      FileOutputStream fos = new FileOutputStream(target.toFile()))
    {
      // copy GZIPInputStream to FileOutputStream
      byte[] buffer = new byte[1024];
      int len;
      while ((len = gis.read(buffer)) > 0)
      {
        fos.write(buffer, 0, len);
      }
    }
  }

  private void renameFiles()
  {
    //Cover
    String coverFile = infoModel.getCoverFile();
    String oldCoverFile = infoModel.getOldCoverFile();
    if (!coverFile.isEmpty() && !oldCoverFile.isEmpty() && !coverFile.equals(oldCoverFile))
    {
      File oldCover = new File(COVERS + oldCoverFile);
      File newCover = new File(COVERS + coverFile);
      if (oldCover.renameTo(newCover))
      {
        logger.debug("Renamed cover {} to {}", oldCover.getName(), newCover.getName());
      }
      else
      {
        logger.debug("Could NOT rename cover {} to {}", oldCover.getName(), newCover.getName());
      }
    }
    //Screen 1
    String screens1File = infoModel.getScreens1File();
    String oldScreens1File = infoModel.getOldScreens1File();
    if (!screens1File.isEmpty() && !oldScreens1File.isEmpty() && !screens1File.equals(oldScreens1File))
    {
      File oldScreen1 = new File(SCREENS + oldScreens1File);
      File newScreen1 = new File(SCREENS + screens1File);
      if (oldScreen1.renameTo(newScreen1))
      {
        logger.debug("Renamed screen1 {} to {}", oldScreen1.getName(), newScreen1.getName());
      }
      else
      {
        logger.debug("Could NOT rename screen1 {} to {}", oldScreen1.getName(), newScreen1.getName());
      }
    }
    //Screen 2
    String screens2File = infoModel.getScreens2File();
    String oldScreens2File = infoModel.getOldScreens2File();

    //Special case if the same screen has been used for both screens: Copy first to second in this case
    if (!oldScreens1File.isEmpty() && !oldScreens2File.isEmpty() && oldScreens1File.equals(oldScreens2File))
    {
      try
      {
        Files.copy(new File(SCREENS + screens1File).toPath(), new File(SCREENS + screens2File).toPath());
        logger.debug("Copied {} to {}", screens1File, screens2File);
      }
      catch (IOException e)
      {
        logger.debug("Could not copy screen {} to {}", screens1File, screens2File);
      }
    }
    else
    {
      if (!screens2File.isEmpty() && !oldScreens2File.isEmpty() && !screens2File.equals(oldScreens2File))
      {
        File oldScreen2 = new File(SCREENS + oldScreens2File);
        File newScreen2 = new File(SCREENS + screens2File);
        if (oldScreen2.renameTo(newScreen2))
        {
          logger.debug("Renamed screen2 {} to {}", oldScreen2.getName(), newScreen2.getName());
        }
        else
        {
          logger.debug("Could NOT rename screen2 {} to {}", oldScreen2.getName(), newScreen2.getName());
        }
      }
    }
    //Gamefile
    String gamesFile = infoModel.getGamesFile();
    String oldGamesFile = infoModel.getOldGamesFile();
    if (!gamesFile.isEmpty() && !oldGamesFile.isEmpty() && !gamesFile.equals(oldGamesFile))
    {
      File oldGame = new File(GAMES + oldGamesFile);
      File newGame = new File(GAMES + gamesFile);
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

  public static String generateFileNameFromTitle(String title, int duplicateIndex)
  {
    // All uppercase letters
    // No spaces or special characters, remove them first
    List<Character> forbiddenCharsList =
      " ,:'�-.!+*<>()/[]?|".chars().mapToObj(item -> (char) item).collect(Collectors.toList());

    List<Character> newName =
      title.chars().mapToObj(item -> (char) item).filter(character -> !forbiddenCharsList.contains(character))
        .map(Character::toUpperCase).collect(Collectors.toList());
    String newNameString = newName.stream().map(String::valueOf).collect(Collectors.joining());

    //The maxi game tool seems to truncate to a maximum length of 23 characters. Let's do the same here.
    if (newNameString.length() > 23)
    {
      newNameString = newNameString.substring(0, 23);
      logger.debug("FileName: truncating to : {}", title);
    }

    //Just add the duplicate index if there are several games with the same name
    if (duplicateIndex > 0 && duplicateIndex < 10)
    {
      newNameString = newNameString + "0" + duplicateIndex;
    }
    else if (duplicateIndex > 9)
    {
      newNameString = newNameString + duplicateIndex;
    }

    logger.debug("Game title: \"{}\" ---- New fileName: \"{}\"", title, newNameString);
    return newNameString;
  }

  public static String generateFileNameFromTitleForFileLoader(String title, int duplicateIndex)
  {
    List<Character> forbiddenCharsList =
      ":,'�!+*<>()/[]?|".chars().mapToObj(item -> (char) item).collect(Collectors.toList());

    List<Character> newName = title.chars().mapToObj(item -> (char) item)
      .filter(character -> !forbiddenCharsList.contains(character)).collect(Collectors.toList());
    String newNameString = newName.stream().map(String::valueOf).collect(Collectors.joining());
    if (duplicateIndex > 0)
    {
      //Just add the duplicate index if there are several games with the same name
      newNameString = newNameString + "_" + duplicateIndex;
    }

    logger.debug("Game title: \"{}\" ---- New fileName: \"{}\"", title, newNameString);
    return newNameString;
  }

  public void exportGameInfoFile(GameDetails gameDetails, File targetDir, StringBuilder infoBuilder, boolean fileLoader)
  {
    try
    {
      String filename = "";

      if (fileLoader)
      {
        infoBuilder.append("Creating cjm file for " + gameDetails.getTitle() + "\n");
        filename =
          generateFileNameFromTitleForFileLoader(gameDetails.getTitle(), gameDetails.getDuplicateIndex()) + ".cjm";
      }
      else
      {
        infoBuilder.append("Creating game info file for " + gameDetails.getTitle() + "\n");
        //Add -ms to comply with the maxi game tool.
        filename = generateFileNameFromTitle(gameDetails.getTitle(), gameDetails.getDuplicateIndex()) + "-ms.tsg";
      }

      writeGameInfoFile(filename, targetDir, gameDetails, fileLoader);
    }
    catch (Exception e)
    {
      String message = "Could not create file for: " + gameDetails.getTitle();
      logger.error(message, e);
      infoBuilder.append(message);
    }
  }

  public void writeGameInfoFile(String fileName, File targetDir, GameDetails gameDetails, boolean fileLoader)
    throws IOException
  {
    Path outDirPath = targetDir.toPath();
    Path filePath = outDirPath.resolve(fileName);
    filePath.toFile().createNewFile();
    FileWriter fw = new FileWriter(filePath.toFile(), StandardCharsets.UTF_8);

    if (!fileLoader)
    {
      fw.write("T:" + gameDetails.getTitle() + "\n");
      String description = replaceMinus(gameDetails.getDescription());
      fw.write("D:en:" + description + "\n");
      fw.write("D:de:" +
        (gameDetails.getDescriptionDe().isEmpty() ? description : replaceMinus(gameDetails.getDescriptionDe())) + "\n");
      fw.write("D:fr:" +
        (gameDetails.getDescriptionFr().isEmpty() ? description : replaceMinus(gameDetails.getDescriptionFr())) + "\n");
      fw.write("D:es:" +
        (gameDetails.getDescriptionEs().isEmpty() ? description : replaceMinus(gameDetails.getDescriptionEs())) + "\n");
      fw.write("D:it:" +
        (gameDetails.getDescriptionIt().isEmpty() ? description : replaceMinus(gameDetails.getDescriptionIt())) + "\n");
      if (!gameDetails.getAuthor().isEmpty())
      {
        fw.write("A:" + gameDetails.getAuthor() + "\n");
      }
      if (!gameDetails.getComposer().isEmpty())
      {
        fw.write("M:" + gameDetails.getComposer() + "\n");
      }
      fw.write("E:" + gameDetails.getGenre() + "\n");
      fw.write("Y:" + gameDetails.getYear() + "\n");

      fw.write("F:" + "games/" + gameDetails.getGame() + "\n");
      fw.write("C:" + "covers/" + gameDetails.getCover() + "\n");
      if (!gameDetails.getScreen1().isEmpty())
      {
        fw.write("G:" + "screens/" + gameDetails.getScreen1() + "\n");
      }
      if (!gameDetails.getScreen2().isEmpty())
      {
        fw.write("G:" + "screens/" + gameDetails.getScreen2() + "\n");
      }
    }
    fw.write("X:" + gameDetails.getSystem() + "\n");
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

  private String replaceMinus(String description)
  {
    return description.replaceAll("-", " ");
  }

  public void runGameInVice()
  {
    String gamePathString = "";
    //Use path if available, otherwise the available game in /games.
    Path gamePath = infoModel.getGamesPath();
    if (gamePath != null)
    {
      gamePathString = gamePath.toString();
    }
    else
    {
      gamePathString = GAMES + infoModel.getGamesFile();
    }
    runVice(true, gamePathString);
  }

  public void runViceWithoutGame()
  {
    runVice(false, "");
  }

  public void runSnapshotInVice(SAVESTATE saveState)
  {
    String gamePathString = "";
    Path vsfPath;
    switch (saveState)
    {
    case Save0:
    {
      //Use path if available, otherwise the available game in /games.
      vsfPath = savedStatesModel.getState1Path();
      if (vsfPath != null)
      {
        gamePathString = vsfPath.toString();
      }
      else
      {
        gamePathString = SavedStatesManager.SAVES + infoModel.getGamesFile() + "/" + savedStatesModel.getState1File();
      }
    }
      break;
    case Save1:
      //Use path if available, otherwise the available game in /games.
      vsfPath = savedStatesModel.getState2Path();
      if (vsfPath != null)
      {
        gamePathString = vsfPath.toString();
      }
      else
      {
        gamePathString = SavedStatesManager.SAVES + infoModel.getGamesFile() + "/" + savedStatesModel.getState2File();
      }
      break;
    case Save2:
      //Use path if available, otherwise the available game in /games.
      vsfPath = savedStatesModel.getState3Path();
      if (vsfPath != null)
      {
        gamePathString = vsfPath.toString();
      }
      else
      {
        gamePathString = SavedStatesManager.SAVES + infoModel.getGamesFile() + "/" + savedStatesModel.getState3File();
      }
      break;
    case Save3:
      //Use path if available, otherwise the available game in /games.
      vsfPath = savedStatesModel.getState4Path();
      if (vsfPath != null)
      {
        gamePathString = vsfPath.toString();
      }
      else
      {
        gamePathString = SavedStatesManager.SAVES + infoModel.getGamesFile() + "/" + savedStatesModel.getState4File();
      }
      break;
    default:
      break;
    }
    runVice(true, gamePathString);
  }

  private void runVice(boolean appendGame, String gamePath)
  {
    StringBuilder command = new StringBuilder();
    if (systemModel.isC64())
    {
      command.append("./vice/x64.exe ");
      //SID config
      command.append("-sidenginemodel ");
      if (systemModel.isSid6581())
      {
        command.append("256 ");
      }
      else if (systemModel.isSid8580())
      {
        command.append("257 ");
      }
      else
      {
        command.append("258 ");
      }
    }
    else
    {
      command.append("./vice/xvic.exe ");
      //ViC 20 memory config
      List<String> memoryFlagsList = new ArrayList<>();
      if (systemModel.isBank0())
      {
        memoryFlagsList.add("0");
      }
      if (systemModel.isBank1())
      {
        memoryFlagsList.add("1");
      }
      if (systemModel.isBank2())
      {
        memoryFlagsList.add("2");
      }
      if (systemModel.isBank3())
      {
        memoryFlagsList.add("3");
      }
      if (systemModel.isBank5())
      {
        memoryFlagsList.add("5");
      }
      if (memoryFlagsList.size() > 0)
      {
        String configuredBanks = String.join(",", memoryFlagsList);
        command.append("-memory ");
        command.append(configuredBanks);
        command.append(" ");
      }
    }

    //Append PAL,NTSC
    if (systemModel.isPal())
    {
      command.append("-pal ");
    }
    else
    {
      command.append("-ntsc ");
    }

    //Append game to autostart (not saved snapshots)
    if (appendGame && !gamePath.contains(".vsz"))
    {
      appendCorrectFlagForGameFile(gamePath, command);
    }

    //Append truedrive
    if (systemModel.isC64())
    {
      command.append(" -autostart-handle-tde ");
      if (systemModel.isAccurateDisk())
      {
        command.append("-truedrive ");
      }
      else
      {
        command.append("+truedrive ");
      }
      if (systemModel.isReadOnly())
      {
        command.append("-attach8ro ");
      }
    }

    //Append default joystick port
    if (model.getJoy1Model().isPrimary())
    {
      command.append("-joydev1 1 ");
      if (systemModel.isC64())
      {
        command.append("-joydev2 0");
      }
    }
    else
    {
      command.append("-joydev1 0 ");
      if (systemModel.isC64())
      {
        command.append("-joydev2 1");
      }
    }

    //Used for saved snapshots, must be at the end of the commands
    if (appendGame && gamePath.contains(".vsz"))
    {
      command.append(" ");
      command.append(gamePath);
    }

    //Launch Vice
    try
    {
      logger.debug("Launching VICE with command: {}", command.toString());
      Runtime.getRuntime().exec(command.toString());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not launch Vice with command " + command);
    }
  }

  private void appendCorrectFlagForGameFile(String gameFile, StringBuilder command)
  {
    if (systemModel.isC64())
    {
      //C64
      if (gameFile.toLowerCase().contains("crt"))
      {
        command.append("-cartcrt \"" + decompressIfNeeded(gameFile) + "\"");
      }
      else
      {
        command.append("-autostart \"" + gameFile + "\"");
      }
    }
    else
    {
      //VIC-20
      if (gameFile.endsWith(".zip"))
      {
        //Only carts ends with zip... try to decompress and add to command
        List<String> crtFiles = unzipVic20cart(new File(gameFile));
        for (String file : crtFiles)
        {
          String fileFlag = file.substring(file.lastIndexOf("-") + 1, file.lastIndexOf("-") + 3);
          if (fileFlag.contains("a0"))
          {
            command.append("-cartA \"" + file + "\" ");
          }
          else if (fileFlag.contains("b0"))
          {
            command.append("-cartB \"" + file + "\" ");
          }
          else if (fileFlag.contains("20"))
          {
            command.append("-cart2 \"" + file + "\" ");
          }
          else if (fileFlag.contains("40"))
          {
            command.append("-cart4 \"" + file + "\" ");
          }
          else if (fileFlag.contains("60"))
          {
            command.append("-cart6 \"" + file + "\" ");
          }
        }
      }
      //This is obsolete, all vic-20 carts are zip files now to support multicarts, kept for backwards compatibility
      else if (gameFile.contains("crt"))
      {
        //Get the file flag
        String fileFlag = gameFile.substring(gameFile.lastIndexOf("-") + 1, gameFile.indexOf("crt.gz") - 1);
        if (fileFlag.contains("a0"))
        {
          command.append("-cartA \"" + gameFile + "\" ");
        }
        else if (fileFlag.contains("b0"))
        {
          command.append("-cartB \"" + gameFile + "\" ");
        }
        else if (fileFlag.contains("20"))
        {
          command.append("-cart2 \"" + gameFile + "\" ");
        }
        else if (fileFlag.contains("40"))
        {
          command.append("-cart4 \"" + gameFile + "\" ");
        }
        else if (fileFlag.contains("60"))
        {
          command.append("-cart6 \"" + gameFile + "\" ");
        }
      }
      else
      {
        command.append("-autostart \"" + decompressIfNeeded(gameFile) + "\" ");
      }
    }
  }

  private String decompressIfNeeded(String path)
  {
    String returnPath = path;
    String lowerCasePath = path.toLowerCase();
    if (lowerCasePath.contains("crt.gz") || lowerCasePath.contains("prg.gz"))
    {
      Path targetFile = Paths.get("./temp/" + path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));
      try
      {
        Files.createDirectories(TEMP_PATH);
        decompressGzip(Paths.get(path), targetFile);
        returnPath = targetFile.toString();
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not decompress file: " + path);
      }
    }
    return returnPath;
  }

  public static void storeProperties()
  {
    if (fileProperties != null)
    {
      try (OutputStream output = new FileOutputStream("./pcu.properties"))
      {
        // save properties to project root folder
        fileProperties.store(output, null);
      }
      catch (IOException ex)
      {
        logger.error("Could not save pcu.properties", ex);
      }
    }
  }

  public static Properties getConfiguredProperties()
  {
    if (fileProperties == null)
    {
      fileProperties = new Properties();
      try (InputStream input = new FileInputStream("./pcu.properties"))
      {
        // load a properties file
        fileProperties.load(input);

      }
      catch (IOException ex)
      {
        logger.error("Could not load pcu.properties", ex);
      }
    }
    return fileProperties;
  }

  public static void backupDb(String targetFolderName)
  {
    File outputFolder = new File(BACKUP + "/" + targetFolderName + "/");
    try
    {
      File dbFile = new File("./" + DbConnector.DB_NAME);
      Files.createDirectories(outputFolder.toPath());
      Path targetFile = outputFolder.toPath().resolve(DbConnector.DB_NAME);
      Files.copy(dbFile.toPath(), targetFile);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not create backup of Db");
    }
  }

  public static void backupScreens(String targetFolderName)
  {
    File outputFolder = new File(BACKUP + "/" + targetFolderName + "/");
    try
    {
      Files.createDirectories(outputFolder.toPath());
      Path screens = new File(SCREENS).toPath();
      copyDirectory(screens.toString(), outputFolder.toPath().resolve("screens").toString());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not create backup of screens.");
    }
  }

  public static void backupCovers(String targetFolderName)
  {
    File outputFolder = new File(BACKUP + "/" + targetFolderName + "/");
    try
    {
      Files.createDirectories(outputFolder.toPath());
      Path covers = new File(COVERS).toPath();
      copyDirectory(covers.toString(), outputFolder.toPath().resolve("covers").toString());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not create backup of covers.");
    }
  }

  public static void backupGames(String targetFolderName)
  {
    File outputFolder = new File(BACKUP + "/" + targetFolderName + "/");
    try
    {
      Files.createDirectories(outputFolder.toPath());
      Path games = new File(GAMES).toPath();
      copyDirectory(games.toString(), outputFolder.toPath().resolve("games").toString());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not create backup of games.");
    }
  }

  public static void backupSaves(String targetFolderName)
  {
    File outputFolder = new File(BACKUP + "/" + targetFolderName + "/");
    try
    {
      Files.createDirectories(outputFolder.toPath());
      Path games = new File(SAVES).toPath();
      copyDirectory(games.toString(), outputFolder.toPath().resolve("saves").toString());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not create backup of games.");
    }
  }

  public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
    throws IOException
  {
    Files.walk(Paths.get(sourceDirectoryLocation)).forEach(source -> {
      Path destination =
        Paths.get(destinationDirectoryLocation, source.toString().substring(sourceDirectoryLocation.length()));
      try
      {
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e,
                                         "Could not copy directory " + sourceDirectoryLocation + " to " +
                                           destinationDirectoryLocation);
      }
    });
  }

  public static void deleteAllFolderContent()
  {
    deleteDirContent(new File(COVERS));
    deleteDirContent(new File(SCREENS));
    deleteDirContent(new File(GAMES));
  }

  private static void deleteDirContent(File dir)
  {
    for (File file : dir.listFiles())
    {
      if (!file.isDirectory())
      {
        file.delete();
      }
    }
  }

  private static void deleteSavesDirContent(File dir)
  {
    try
    {
      Files.walk(dir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not delete saves folder");
    }
  }

  public static void deleteFilesForGame(GameDetails details)
  {
    if (!details.getCover().isEmpty())
    {
      File coverFile = new File(COVERS + details.getCover());
      coverFile.delete();
    }
    if (!details.getScreen1().isEmpty())
    {
      File screens1File = new File(SCREENS + details.getScreen1());
      screens1File.delete();
    }
    if (!details.getScreen2().isEmpty())
    {
      File screens2File = new File(SCREENS + details.getScreen2());
      screens2File.delete();
    }
    if (!details.getGame().isEmpty())
    {
      File gameFile = new File(GAMES + details.getGame());
      gameFile.delete();
    }
    //TODO: Shall we delete saved states also?? Or should the saved states be kept?
  }

  public static void restoreDb(String backupFolderName)
  {
    File backupFolder = new File(BACKUP + "/" + backupFolderName + "/");
    try
    {
      Path backupFile = backupFolder.toPath().resolve(DbConnector.DB_NAME);
      Path dbFile = new File("./" + DbConnector.DB_NAME).toPath();
      Files.copy(backupFile, dbFile, StandardCopyOption.REPLACE_EXISTING);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not restore backup of Db");
    }
  }

  public static void restoreCovers(String backupFolderName)
  {
    File backupFolder = new File(BACKUP + "/" + backupFolderName + "/");
    try
    {
      File coversDir = new File(COVERS);
      deleteDirContent(coversDir);
      copyDirectory(backupFolder.toPath().resolve("covers").toString(), coversDir.toPath().toString());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not restore backup of covers.");
    }
  }

  public static void restoreScreens(String backupFolderName)
  {
    File backupFolder = new File(BACKUP + "/" + backupFolderName + "/");
    try
    {
      File screensDir = new File(SCREENS);
      deleteDirContent(screensDir);
      copyDirectory(backupFolder.toPath().resolve("screens").toString(), screensDir.toPath().toString());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not restore backup of screens.");
    }
  }

  public static void restoreGames(String backupFolderName)
  {
    File backupFolder = new File(BACKUP + "/" + backupFolderName + "/");
    try
    {
      File gamesDir = new File(GAMES);
      deleteDirContent(gamesDir);
      copyDirectory(backupFolder.toPath().resolve("games").toString(), gamesDir.toPath().toString());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not restore backup of games.");
    }
  }

  public static void restoreSaves(String backupFolderName)
  {
    File backupFolder = new File(BACKUP + "/" + backupFolderName + "/");
    try
    {
      File savesDir = new File(SAVES);
      deleteSavesDirContent(savesDir);
      copyDirectory(backupFolder.toPath().resolve("saves").toString(), savesDir.toPath().toString());
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not restore backup of games.");
    }
  }

  public static List<String> getAllBackups()
  {
    List<String> returnList = new ArrayList<>();
    File backupFolder = new File(BACKUP);
    if (!backupFolder.exists())
    {
      try
      {
        Files.createDirectory(backupFolder.toPath());
      }
      catch (IOException e)
      {
        ExceptionHandler.handleException(e, "Could not create backup folder");
      }
    }
    for (File file : backupFolder.listFiles())
    {
      if (file.isDirectory())
      {
        returnList.add(file.getName());
      }
    }
    return returnList;
  }

  public static String getPcuVersionFromManifest()
  {
    String returnValue = "";
    Class clazz = FileManager.class;
    String className = clazz.getSimpleName() + ".class";
    String classPath = clazz.getResource(className).toString();
    if (classPath.startsWith("jar"))
    {
      String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
      Manifest manifest;
      try
      {
        manifest = new Manifest(new URL(manifestPath).openStream());
        Attributes attr = manifest.getMainAttributes();
        returnValue = attr.getValue("BuildVersion");
      }
      catch (IOException e1)
      {
        ExceptionHandler.handleException(e1, "Could not read manifest");
      }
    }
    return returnValue;
  }

  public static Future<?> deleteTempFolder()
  {
    //Delete temp folder. It may be very large, do it in a separate thread
    return executor.submit(() -> {
      try
      {
        if (Files.exists(TEMP_PATH))
        {
          Files.walk(TEMP_PATH).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
      }
      catch (IOException e)
      {
        ExceptionHandler.logException(e, "Could not delete temp folder");
      }
    });
  }

  public static void scaleCoverImageAndSave(Path coverImagePath, String gameName)
  {
    try
    {
      BufferedImage coverImage = getScaledCoverImage(ImageIO.read(coverImagePath.toFile()));
      ImageIO.write(coverImage, "png", coverImagePath.toFile());
    }
    catch (IOException e)
    {
      ExceptionHandler
        .logException(e, "Could not scale and store cover for " + gameName + ", using missing cover instead");
      //Use missing file
      try
      {
        ImageIO.write(emptyC64Cover, "png", coverImagePath.toFile());
      }
      catch (IOException e1)
      {
        ExceptionHandler.logException(e1, "Could not store empty cover for " + gameName);
      }
    }
  }

  public static BufferedImage getScaledCoverImage(BufferedImage originalCoverImage)
  {
    Image newCover = originalCoverImage.getScaledInstance(122, 175, Image.SCALE_SMOOTH);
    BufferedImage copyOfImage =
      new BufferedImage(newCover.getWidth(null), newCover.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics g = copyOfImage.createGraphics();
    g.drawImage(newCover, 0, 0, null);
    g.dispose();
    return copyOfImage;
  }

  public static void scaleScreenshotImageAndSave(Path screenshotImagePath, String gameName)
  {
    try
    {
      BufferedImage screenImage = ImageIO.read(screenshotImagePath.toFile());
      ImageIO.write(scaleImageTo320x200x32bit(screenImage), "png", screenshotImagePath.toFile());
    }
    catch (IOException e)
    {
      ExceptionHandler
        .logException(e, "Could not scale and store screenshot for " + gameName + ", using empty screenshot instead");
      //Use missing file
      try
      {
        ImageIO.write(emptyC64Screenshot, "png", screenshotImagePath.toFile());
      }
      catch (IOException e1)
      {
        ExceptionHandler.logException(e1, "Could not store empty screenshot for " + gameName);
      }
    }
  }

  public static BufferedImage scaleImageTo320x200x32bit(BufferedImage originalImage)
  {
    BufferedImage returnImage = originalImage;
    if (originalImage.getWidth() >= 544 && originalImage.getWidth() < 600 && originalImage.getHeight() >= 284)
    {
      //Somewhat standard VIC-20 screenshot from Vice. This is best scaled by first cropping to 448x280
      returnImage = cropImageTo448x280(originalImage);
    }
    if (originalImage.getWidth() != 320 || originalImage.getHeight() != 200)
    {
      // Scale to right size.
      Image newImage = returnImage.getScaledInstance(320, 200, Image.SCALE_SMOOTH);
      BufferedImage copyOfImage =
        new BufferedImage(newImage.getWidth(null), newImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
      Graphics g = copyOfImage.createGraphics();
      g.drawImage(newImage, 0, 0, null);
      return copyOfImage;
    }
    if (returnImage.getType() != BufferedImage.TYPE_INT_ARGB)
    {
      //Convert to 32 bit
      BufferedImage copyOfImage =
        new BufferedImage(returnImage.getWidth(null), returnImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
      Graphics g = copyOfImage.createGraphics();
      g.drawImage(returnImage, 0, 0, null);
      returnImage = copyOfImage;
    }
    return returnImage;
  }

  public static BufferedImage cropImageTo320x200(BufferedImage originalImage)
  {
    // Crop to right size for C64: Remove the border to fit nicely in the carousel.
    BufferedImage newImage = originalImage
      .getSubimage((originalImage.getWidth() - 320) / 2, ((originalImage.getHeight() - 200) / 2) - 1, 320, 200);
    BufferedImage copyOfImage =
      new BufferedImage(newImage.getWidth(), newImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = copyOfImage.createGraphics();
    g.drawImage(newImage, 0, 0, null);
    return newImage;
  }

  private static BufferedImage cropImageTo448x280(BufferedImage originalImage)
  {
    // Crop to right size for Vic-20: Remove the border to fit nicely in the carousel.
    BufferedImage newImage = originalImage
      .getSubimage((originalImage.getWidth() - 448) / 2, ((originalImage.getHeight() - 280) / 2) - 1, 448, 280);
    BufferedImage copyOfImage =
      new BufferedImage(newImage.getWidth(), newImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = copyOfImage.createGraphics();
    g.drawImage(newImage, 0, 0, null);
    return newImage;
  }

  /**
   * Creates a temporary file from inputStream, just leaving the File as-is.
   * 
   * @param inputStream The stream to read from
   * @param gameFilename The name of the file
   * @return The created temporary file
   * @throws IOException if a File cannot be created.
   */
  public static File getTempFileForVic20Cart(BufferedInputStream inputStream, String gameFilename) throws IOException
  {
    Files.createDirectories(TEMP_PATH);
    File file = new File(TEMP_PATH + File.separator + gameFilename);
    FileOutputStream fos = new FileOutputStream(file, false);
    byte[] buffer = new byte[1024];
    int len;
    while ((len = inputStream.read(buffer)) != -1)
    {
      fos.write(buffer, 0, len);
    }
    inputStream.close();
    fos.close();
    return file;
  }

  /**
   * Creates a temporary file from inputStream, unzips the File and picks the first valid entry in the file. Since GB64
   * for example can contain multiple disk images The first one is picked (no support for disk swap in the carousel).
   * For some file a .NFO file is the first entry, which will not work when passing it to the carousel.
   * 
   * @param inputStream The stream to read from
   * @param gameFilename The name of the file
   * @return The first entry in the zip file (unzipped) to be included with the game during import.
   * @throws IOException if a File cannot be created.
   */
  public static File createTempFileForScraper(BufferedInputStream inputStream, String gameFilename) throws IOException
  {
    Files.createDirectories(TEMP_PATH);
    File file = new File(TEMP_PATH + File.separator + gameFilename);
    FileOutputStream fos = new FileOutputStream(file, false);
    byte[] buffer = new byte[1024];
    int len;
    while ((len = inputStream.read(buffer)) != -1)
    {
      fos.write(buffer, 0, len);
    }
    inputStream.close();
    fos.close();
    if (gameFilename.toLowerCase().endsWith(".rar"))
    {
      return unrarAndPickFirstValidEntry(file);
    }
    return unzipAndPickFirstValidEntry(file);
  }

  public static List<String> unzipVic20cart(File file)
  {
    List<String> unzippedFilesList = new ArrayList<>();

    String zipFilePath = file.getAbsolutePath();
    Path filePath = null;
    FileInputStream fis;
    //buffer for read and write data to file
    byte[] buffer = new byte[1024];
    try
    {
      fis = new FileInputStream(zipFilePath);
      ZipArchiveInputStream zis = new ZipArchiveInputStream(fis);
      ZipEntry ze = zis.getNextZipEntry();
      while (ze != null)
      {
        String fileName = ze.getName();
        File newFile = new File(TEMP_PATH + File.separator + fileName);
        //create directories for sub directories in zip
        new File(newFile.getParent()).mkdirs();
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zis.read(buffer)) > 0)
        {
          fos.write(buffer, 0, len);
        }
        fos.close();
        filePath = newFile.toPath();
        unzippedFilesList.add(filePath.toString());
        ze = zis.getNextZipEntry();
      }
      //close this ZipEntry
      zis.close();
      fis.close();
    }
    catch (IOException e)
    {
      ExceptionHandler.logException(e, "Could not unzip file");
    }
    return unzippedFilesList;
  }

  public static File unzipAndPickFirstValidEntry(File file)
  {
    String dirName = file.getName();
    dirName = dirName.replaceAll("\\.", "");

    String unzippedBasePath = TEMP_PATH + File.separator + dirName + File.separator;

    String zipFilePath = file.getAbsolutePath();
    Path filePath = null;
    FileInputStream fis;
    //buffer for read and write data to file
    byte[] buffer = new byte[1024];
    try
    {
      fis = new FileInputStream(zipFilePath);
      ZipArchiveInputStream zis = new ZipArchiveInputStream(fis);
      ZipEntry ze = getFirstMatchingZipEntry(zis);
      if (ze != null)
      {
        String fileName = ze.getName();
        String extension = FilenameUtils.getExtension(fileName);
        fileName = FilenameUtils.removeExtension(fileName);
        //remove all "." in name ...
        fileName = fileName.replaceAll("\\.", "");

        File newFile = new File(unzippedBasePath + fileName + "." + extension);
        //create directories for sub directories in zip
        new File(newFile.getParent()).mkdirs();
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zis.read(buffer)) > 0)
        {
          fos.write(buffer, 0, len);
        }
        fos.close();
        //close this ZipEntry
        zis.close();
        filePath = newFile.toPath();
      }
      fis.close();
    }
    catch (IOException e)
    {
      ExceptionHandler.logMessage("Could not unzip file, using original file");
    }
    //Return original file if no zip entry found, it's not zipped
    return filePath != null ? filePath.toFile() : file;
  }

  public static File unrarAndPickFirstValidEntry(File file)
  {
    String dirName = file.getName();
    dirName = dirName.replaceAll("\\.", "");
    String unzippedBasePath = TEMP_PATH + File.separator + dirName + File.separator;
    Path filePath = null;
    try (Archive archive = new Archive(file))
    {
      archive.getMainHeader().print();
      FileHeader fh = getFirstMatchingRarEntry(archive);
      if (fh != null)
      {
        String fileName = fh.getFileNameString().trim();
        String extension = FilenameUtils.getExtension(fileName);
        fileName = FilenameUtils.removeExtension(fileName);
        //remove all "." in name ...
        fileName = fileName.replaceAll("\\.", "");

        File fileEntry = new File(unzippedBasePath + fileName + "." + extension);
        //create directories for sub directories in rar
        new File(fileEntry.getParent()).mkdirs();

        FileOutputStream os = new FileOutputStream(fileEntry);
        archive.extractFile(fh, os);
        os.close();
        fh = archive.nextFileHeader();
        filePath = fileEntry.toPath();
      }
    }
    catch (Exception e)
    {
      ExceptionHandler.logMessage("Could not unrar file, using original file");
    }
    return filePath != null ? filePath.toFile() : file;
  }

  private static FileHeader getFirstMatchingRarEntry(Archive archive)
  {
    FileHeader fh = archive.nextFileHeader();
    if (fh != null && !isValidFileEnding(fh.getFileNameString().trim().toLowerCase()))
    {
      fh = getFirstMatchingRarEntry(archive);
    }
    return fh;
  }

  private static ZipEntry getFirstMatchingZipEntry(ZipArchiveInputStream zis) throws IOException
  {
    ZipEntry ze = zis.getNextZipEntry();
    if (ze != null && !isValidFileEnding(ze.getName().trim().toLowerCase()))
    {
      ze = getFirstMatchingZipEntry(zis);
    }
    return ze;
  }

  private static boolean isValidFileEnding(String fileName)
  {
    return validFileEndingList.stream().anyMatch(ending -> fileName.endsWith(ending));
  }

  public static List<String> convertAllScreenshotsTo32Bit() throws IOException
  {
    List<String> convertedScreensList = new ArrayList<>();

    Files.walk(Paths.get(SCREENS), 1).filter(Files::isRegularFile).forEach(source -> {
      BufferedImage image = null;

      File currentFile = (source.toFile());
      try
      {
        image = ImageIO.read(currentFile);
        if (image != null && image.getType() != BufferedImage.TYPE_INT_ARGB &&
          image.getType() != BufferedImage.TYPE_4BYTE_ABGR)
        {
          //Convert to 32 bit
          BufferedImage convertedImage =
            new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
          Graphics g = convertedImage.createGraphics();
          g.drawImage(image, 0, 0, null);
          //Write new file
          ImageIO.write(convertedImage, "png", currentFile);
          convertedScreensList.add(source.getFileName().toString());
        }
      }
      catch (Exception e)
      {
        logger.error("can't read file: " + source.toString(), e);
      }
    });

    return convertedScreensList;
  }

  public static List<String> checkAllFilesForDbValidation(List<GameValidationDetails> gameList)
  {
    List<String> fixedGamesList = new ArrayList<>();
    for (GameValidationDetails gameData : gameList)
    {
      File coverFile = Paths.get(COVERS + gameData.getCover()).toFile();
      if (!coverFile.exists())
      {
        saveEmptyCoverImage(gameData.isVic20(), coverFile);
        fixedGamesList.add("missing cover file for " + gameData.getTitle());
      }
      File screens1File = Paths.get(SCREENS + gameData.getScreen1()).toFile();
      File screens2File = Paths.get(SCREENS + gameData.getScreen2()).toFile();
      if (!screens1File.exists() || !screens2File.exists())
      {
        fixScreenImages(gameData.isVic20(), screens1File, screens2File);
        fixedGamesList.add("screenshot files for " + gameData.getTitle());
      }

      //Check game file also
      File gameFile = Paths.get(GAMES + gameData.getGame()).toFile();
      if (!gameFile.exists())
      {
        copyMissingGameFile(gameData.isVic20(), gameFile);
        fixedGamesList.add("missing game file for " + gameData.getTitle());
      }
    }
    return fixedGamesList;
  }

  private static void saveEmptyCoverImage(boolean vic20, File coverFile)
  {
    BufferedImage imageToSave = vic20 ? emptyVic20Cover : emptyC64Cover;
    //Copy the missing cover image
    try
    {
      ImageIO.write(imageToSave, "png", coverFile);
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not store cover");
    }
  }

  private static void fixScreenImages(boolean vic20, File screen1File, File screen2File)
  {
    BufferedImage emptyImage = vic20 ? emptyVic20Screenshot : emptyC64Screenshot;
    //Copy the missing cover image
    try
    {
      if (screen1File.exists())
      {
        //Copy to screen2
        Files.copy(screen1File.toPath(), screen2File.toPath());
      }
      else if (screen2File.exists())
      {
        //Copy to screen1
        Files.copy(screen2File.toPath(), screen1File.toPath());
      }
      else
      {
        //Both are missing, write empty to both
        ImageIO.write(emptyImage, "png", screen1File);
        ImageIO.write(emptyImage, "png", screen2File);
      }
    }
    catch (IOException e)
    {
      ExceptionHandler.handleException(e, "Could not store screens");
    }
  }

  private static void copyMissingGameFile(boolean vic20, File gameFile)
  {
    try
    {
      InputStream missingFileStream =
        vic20 ? FileManager.getMissingVIC20GameFile() : FileManager.getMissingC64GameFile();
      FileUtils.copyInputStreamToFile(missingFileStream, gameFile);
    }
    catch (Exception e)
    {
      ExceptionHandler.handleException(e, "Could not store game file");
    }
  }

}
