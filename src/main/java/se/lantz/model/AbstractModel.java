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
  
  public void addPropertyChangeListener(String property, PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(property, listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
  
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
  }

  protected void notifyChange()
  {
    if (!disable)
    {
      dataChanged = true;
      propertyChangeSupport.firePropertyChange("notify", null, "");
    }
  }
  
  protected void notifyChange(String property, boolean oldValue, boolean newValue)
  {
    if (!disable)
    {
      dataChanged = true;
      propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
    }
  }
  
  protected void notifyChange(String property, String oldValue, String newValue)
  {
    if (!disable)
    {
      dataChanged = true;
      propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
    }
  }
  
  protected void notifyChange(String property)
  {
    if (!disable)
    {
      dataChanged = true;
      propertyChangeSupport.firePropertyChange(property, null, null);
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
