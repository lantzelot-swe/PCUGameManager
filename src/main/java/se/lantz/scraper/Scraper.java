package se.lantz.scraper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import se.lantz.model.data.ScraperFields;
/**
 * Common interface implemented by all scrapers.
 */
public interface Scraper
{
  void connect(String url) throws IOException;

  void scrapeInformation(ScraperFields fields);

  String getTitle();

  String getAuthor();

  int getYear();

  String getDescription();

  String getGenre();

  String getComposer();

  BufferedImage getCover();

  List<BufferedImage> scrapeScreenshots();
  
  List<BufferedImage> scrapeCovers();

  boolean isC64();
  
  File getGameFile();
}