package se.lantz.util;

import java.io.IOException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;

public class DescriptionTranslater
{
  //Just for testing
  public static void main(String[] args)
  {
    try
    {
      String englishText =
        "The year is 1942, and you are a daring fighter pilot \"Super Ace\". You begin and end each of the 24 levels on an aircraft carrier and then fly your plane into battle against the enemy over both sea and land. After destroying certain plane formations you can collect several different power-ups to increase your fire power and chances of survival. You can also make your plane roll by pressing space to avoid enemy attacks 3 times per life.";
      String translatedText = translate("en", "fr", englishText);
      System.out.println("fr = " + translatedText);
      translatedText = translate("en", "de", englishText);
      System.out.println("de = " + translatedText);
      translatedText = translate("en", "es", englishText);
      System.out.println("es = " + translatedText);
      translatedText = translate("en", "it", englishText);
      System.out.println("it = " + translatedText);
      translatedText = translate("en", "nl", englishText);
      System.out.println("nl = " + translatedText);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public static String translate(String langFrom, String langTo, String text) throws IOException
  {
    String urlStr =
      "https://script.google.com/macros/s/AKfycbwWWRYA_3Pr4Y0i9m7UCJgQakU_lHAPhPTpEv-REMSFVF1Aj5jx80d0ig/exec" + "?q=" +
        URLEncoder.encode(text, "UTF-8") + "&target=" + langTo + "&source=" + langFrom;
    return Jsoup.connect(urlStr).ignoreContentType(true).execute().body();
  }

}
