package se.lantz.model.carousel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lantz.model.AbstractModel;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameDetails;

public class CarouselPreviewModel extends AbstractModel
{
  private static final Logger logger = LoggerFactory.getLogger(CarouselPreviewModel.class);
  public static final String CLOSE_PREVIEW = "closePreview";
  public static final String SELECTED_GAME = "selectedGame";
  public static final String RELOAD_CAROUSEL = "reloadCarousel";
  private MainViewModel mainModel;
  //Keep track of 10 games as "scroll window" and update when scrolling or a new game is selected 
  private List<GameDetails> dataList = new ArrayList<>();

  private GameDetails selectedGame = null;

  public CarouselPreviewModel(MainViewModel mainModel)
  {
    this.mainModel = mainModel;
    mainModel.addPropertyChangeListener("selectedGamelistView", e -> reloadCarousel());
    mainModel.addPropertyChangeListener("gameSelected", e -> setSelectedGame(mainModel.getCurrentGameDetails()));
    dataList = mainModel.readGameDetailsForCarouselPreview();
    if (dataList.size() < 10)
    {
      this.notifyChange(CLOSE_PREVIEW);
    }
    else
    {
      //Select the middle one
      selectedGame = dataList.get(4);
    }
  }

  private void reloadCarousel()
  {
    this.dataList = mainModel.readGameDetailsForCarouselPreview();
    if (dataList.size() < 10)
    {
      this.notifyChange(CLOSE_PREVIEW);
    }
    else
    {
      this.notifyChange(RELOAD_CAROUSEL);
    }
  }

  public List<GameDetails> getGameDetails()
  {
    return dataList;
  }

  public GameDetails getSelectedGame()
  {
    return selectedGame;
  }

  public GameDetails getNextGameToSelectWhenScrollingRight()
  {
    int index = dataList.indexOf(selectedGame) + 1;
    return dataList.get(index);
  }

  public GameDetails getNextGameToSelectWhenScrollingLeft()
  {
    int index = dataList.indexOf(selectedGame) - 1;
    return dataList.get(index);
  }

  public void setSelectedGame(GameDetails selectedGame)
  {
    logger.debug("setSelectedGame: " + selectedGame);
    //Update the entire data list
    dataList = mainModel.readGameDetailsForCarouselPreview();
    this.selectedGame = dataList.get(4);
    this.notifyChange(SELECTED_GAME);
  }
}
