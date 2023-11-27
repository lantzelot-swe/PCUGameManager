package se.lantz.gui;

import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.JComboBox;

import se.lantz.util.GenreMap;

public class GenreComboBox extends JComboBox<String>
{

  /**
   * 
   */
  private static final long serialVersionUID = 8793039092191107043L;

  GenreMap genreMap = new GenreMap();

  public GenreComboBox()
  {
    setupItems();
  }

  private void setupItems()
  {
    this.addItem("Adventure");
    this.addItem("Driving");
    this.addItem("Maze");
    this.addItem("Platform");
    this.addItem("Programming");
    this.addItem("Puzzle");
    this.addItem("Shoot'em up");
    this.addItem("Simulation");
    this.addItem("Sport");
  }

  public void setSelectedGenre(String genre)
  {
    String item = genreMap.get(genre);
    if (item != null)
    {
      this.setSelectedItem(item);
    }
    else
    {
      this.addItem(genre);
      this.setSelectedItem(genre);
    }
  }

  public String getSelectedGenre()
  {
    for (Entry<String, String> entry : genreMap.entrySet())
    {
      if (Objects.equals(getSelectedItem(), entry.getValue()))
      {
        return entry.getKey();
      }
    }
    return getSelectedItem().toString();
  }
}
