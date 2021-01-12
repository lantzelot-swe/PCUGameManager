package se.lantz.scraper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import se.lantz.model.data.ScraperFields;

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

  boolean isC64();

}