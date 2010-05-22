package org.homelinux.kapa.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public class ClickableLabel extends Label implements Observerable {
  
  private ArrayList<Observer>   observers   = new ArrayList<Observer>();
  private String                name;
  
  /**
   * Constructs a new ClickableLabel object.
   * @param text Text to display in the GUI
   * @param name Later there may be a different internal Name for that label
   */
  public ClickableLabel(String text, String name) {
    super(text);
    this.name = name;
    this.setStylePrimaryName("clickable-label");
  }
  
  protected void onAttach() {
    super.onAttach();
    // register a Clickhandler
    this.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        onClickHandler(); // we'll call the onClickHandler method of the concrete implementation of ClickableLabel
      }
    });
  }
  
  protected void onClickHandler() {
    this.notifyObservers();
    // setting active / inactive
    if ( this.getStyleName().matches(".*active.*") ) {
      this.removeStyleName("active");
    }
    else {
      this.addStyleName("active");
    }
  }
  
  public void notifyObservers() {
    for (Iterator<Observer> iterator = this.observers.iterator(); iterator.hasNext();) {
      Observer obsrvr = iterator.next();
      obsrvr.update(this);
    }
  }

  public void registerObserver(Object o) {
    this.observers.add(((Observer) o));
  }

  public void removeObserver(Object o) {
    this.observers.remove(o);
    
  }
  
  public String getName() {
    return this.name;
  }
  
}
