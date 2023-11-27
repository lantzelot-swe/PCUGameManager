package se.lantz.util;

import java.util.HashMap;

/**
 * Map holding genres supported by the Carousel as keys and Visual strings in the UI as values
 * 
 */
public class GenreMap extends HashMap<String, String>
{
  public GenreMap()
  {
    this.put("", "----");
    this.put("adventure", "Adventure");
    this.put("driving", "Driving");
    this.put("maze", "Maze");
    this.put("platform", "Platform");
    this.put("programming", "Programming");
    this.put("puzzle", "Puzzle");
    this.put("shoot", "Shoot'em up");
    this.put("simulation", "Simulation");
    this.put("sport", "Sport");
  }
}
