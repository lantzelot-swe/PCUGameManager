package se.lantz.model.carousel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

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
  public static final String CLEAR_SELECTION = "clearSelection";
  private MainViewModel mainModel;
  //Keep track of 10 games as "scroll window" and update when scrolling or a new game is selected 
  private List<GameDetails> dataList = new ArrayList<>();

  private GameDetails selectedGame = null;

  private PropertyChangeListener reloadCarouselListener = e -> SwingUtilities.invokeLater(() -> reloadCarousel());
  private PropertyChangeListener selectedGameListener = e -> setSelectedGame(mainModel.getCurrentGameDetails());
  private PropertyChangeListener gameSavedListener = e -> gameSaved();

  public CarouselPreviewModel(MainViewModel mainModel)
  {
    this.mainModel = mainModel;
    mainModel.addPropertyChangeListener("selectedGamelistView", reloadCarouselListener);
    mainModel.addPropertyChangeListener("databaseSelected", reloadCarouselListener);
    mainModel.addPropertyChangeListener("gameSelected", selectedGameListener);
    mainModel.addPropertyChangeListener("gameSaved", gameSavedListener);

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

  public void reloadCarousel()
  {
    logger.debug("RELOAD carousel in preview");
    if (mainModel.getCurrentGameViewGameCount() < 10)
    {
      this.notifyChange(CLOSE_PREVIEW);
    }
    else
    {
      this.dataList = mainModel.readGameDetailsForCarouselPreview();
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

  public String getNextGameToSelectWhenScrollingRight()
  {
    int index = dataList.indexOf(selectedGame) + 1;
    return dataList.get(index).getGameId();
  }

  public String getNextGameToSelectWhenScrollingLeft()
  {
    int index = dataList.indexOf(selectedGame) - 1;
    return dataList.get(index).getGameId();
  }

  public String getGameIdForPageUp()
  {
    int index = dataList.indexOf(selectedGame) - 4;
    return dataList.get(index).getGameId();
  }

  public String getGameIdForPageDown()
  {
    int index = dataList.indexOf(selectedGame) + 4;
    return dataList.get(index).getGameId();
  }

  public void setSelectedGame(GameDetails selectedGame)
  {
    logger.debug("setSelectedGame: " + selectedGame);

    //A new game is added
    if (selectedGame.getGameId().isEmpty())
    {
      this.notifyChange(CLEAR_SELECTION);
      return;
    }
    //Update the entire data list
    dataList = mainModel.readGameDetailsForCarouselPreview();
    if (dataList.size() < 10)
    {
      this.notifyChange(CLOSE_PREVIEW);
    }
    else
    {
      this.selectedGame = dataList.get(4);
      this.notifyChange(SELECTED_GAME);
    }
  }
  
  public void runCurrentGame()
  {
    mainModel.runGameInVice(false);
  }

  private void gameSaved()
  {
    GameDetails details = mainModel.getCurrentGameDetails();
    setSelectedGame(details);
  }

  public void dispose()
  {
    mainModel.removePropertyChangeListener("selectedGamelistView", reloadCarouselListener);
    mainModel.removePropertyChangeListener("databaseSelected", reloadCarouselListener);
    mainModel.removePropertyChangeListener("gameSelected", selectedGameListener);
    mainModel.removePropertyChangeListener("gameSaved", gameSavedListener);
  }
}
