package org.homelinux.kapa.client;

public interface Observerable {
  
  /**
   * Registers an Observer object.
   * @param o   Object to be added (Must have implemented the Observer Interface)
   */
  public void registerObserver(Object o);
  
  /**
   * Removes an Observer object from the List of Observers.
   * @param o Object to be removed
   */
  public void removeObserver(Object o);
  
  /**
   * Calls the update() Method on all registered observers.
   */
  public void notifyObservers();
}
