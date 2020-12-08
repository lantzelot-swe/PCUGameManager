package se.lantz.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.swing.JComboBox;

public class GenreComboBox extends JComboBox<String>
{

  /**
   * 
   */
  private static final long serialVersionUID = 8793039092191107043L;

  Map<String, String> valueMap = new HashMap<>();

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

    valueMap.put("", "----");
    valueMap.put("adventure", "Adventure");
    valueMap.put("driving", "Driving");
    valueMap.put("maze", "Maze");
    valueMap.put("platform", "Platform");
    valueMap.put("programming", "Programming");
    valueMap.put("puzzle", "Puzzle");
    valueMap.put("shoot", "Shoot'em up");
    valueMap.put("simulation", "Simulation");
    valueMap.put("sport", "Sport");
  }

  public void setSelectedGenre(String genre)
  {
    String item = valueMap.get(genre);
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
    for (Entry<String, String> entry : valueMap.entrySet())
    {
      if (Objects.equals(getSelectedItem(), entry.getValue()))
      {
        return entry.getKey();
      }
    }
    return getSelectedItem().toString();
  }
}
