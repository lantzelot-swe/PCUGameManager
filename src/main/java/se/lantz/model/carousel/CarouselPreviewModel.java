package se.lantz.model.carousel;

import java.util.ArrayList;
import java.util.List;

import se.lantz.model.AbstractModel;
import se.lantz.model.MainViewModel;
import se.lantz.model.data.GameDetails;

public class CarouselPreviewModel extends AbstractModel
{
  public static final String SELECTED_GAME = "selectedGame";
  public static final String RELOAD_CAROUSEL = "reloadCarousel";
  private MainViewModel mainModel;
  private List<GameDetails> dataList = new ArrayList<>();

  private GameDetails selectedGame = null;

  public CarouselPreviewModel(MainViewModel mainModel)
  {
    this.mainModel = mainModel;
    mainModel.addPropertyChangeListener("selectedGamelistView", e -> reloadCarousel());
    mainModel.addPropertyChangeListener("gameSelected", e -> setSelectedGame(mainModel.getCurrentGameDetails()));
    dataList = mainModel.readGameDetailsForCarouselPreview();
    //Just to start with something
    selectedGame = dataList.get(0);
  }

  private void reloadCarousel()
  {
    this.dataList = mainModel.readGameDetailsForCarouselPreview();
    this.notifyChange(RELOAD_CAROUSEL, null, null);
  }

  public List<GameDetails> getGameDetails()
  {
    return dataList;
  }

  public GameDetails getSelectedGame()
  {
    return selectedGame;
  }

  public void setSelectedGame(GameDetails selectedGame)
  {
    this.selectedGame = selectedGame;
    this.notifyChange(SELECTED_GAME, null, null);
  }
}
