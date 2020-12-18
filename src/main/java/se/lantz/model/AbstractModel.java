package se.lantz.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Adds propertyChange support
 * 
 * @author Mikael
 *
 */
public abstract class AbstractModel
{

  protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  protected boolean dataChanged = false;

  private boolean disable;

  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  protected void notifyChange()
  {
    if (!disable)
    {
      dataChanged = true;
      propertyChangeSupport.firePropertyChange("notify", null, "");
    }
  }

  public boolean isDataChanged()
  {
    return dataChanged;
  }

  public void resetDataChanged()
  {
    dataChanged = false;
    propertyChangeSupport.firePropertyChange("notify", null, "");
  }
  
  public void resetDataChangedAfterInit()
  {
    dataChanged = false;
  }

  public void disableChangeNotification(boolean disable)
  {
    this.disable = disable;
  }
  
  public boolean isDisableChangeNotifcation()
  {
    return disable;
  }
}
