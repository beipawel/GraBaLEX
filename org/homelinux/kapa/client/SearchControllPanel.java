package org.homelinux.kapa.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class SearchControllPanel extends HorizontalPanel implements Observerable {

  private Button                submitButton            = new Button();
  private Button                resetButton             = new Button();
  private Label                 simpleSearchLabel       = new Label("Zur einfachen Suche");
  private Label                 extSearchLabel          = new Label("Zur erweiterten Suche");
  private ArrayList<Object>     observers               = new ArrayList<Object>();
  private Boolean               resetAction             = false;
  private Boolean               searchAction            = false;
  private Boolean               wantsSimpleControlls    = false;
  private Boolean               wantsExtendedControlls  = false;
  
  public SearchControllPanel() {
    super();
    this.submitButton.setHTML("Suche");
//    this.submitButton.setTabIndex(100);
    this.submitButton.addStyleName("search-button");
    this.submitButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        searchAction = true;
        notifyObservers();
      }
    });
    
    this.resetButton.setHTML("Reset");
//    this.resetButton.setTabIndex(101);
    this.resetButton.addStyleName("reset-button");
    this.resetButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        resetAction = true;
        notifyObservers();
      }
    });
    
    this.extSearchLabel.addStyleName("to-search-label");
    this.extSearchLabel.setVisible(false);
    this.extSearchLabel.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
//        setToExtendedControlls();
        wantsExtendedControlls = true;
        notifyObservers();
      }
    });
    
    this.simpleSearchLabel.addStyleName("to-search-label");
    this.simpleSearchLabel.setVisible(false);
    this.simpleSearchLabel.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
//        setToSimpleControlls();
        wantsSimpleControlls = true;
        notifyObservers();
      }
    });
  }
  
  protected void onAttach() {
    super.onAttach();
    this.add(this.submitButton);
    this.add(this.resetButton);
    this.add(this.extSearchLabel);
    this.add(this.simpleSearchLabel);
  }

  public void notifyObservers() {
    for (Iterator<Object> iterator = this.observers.iterator(); iterator.hasNext();) {
      Observer obsrvr = (Observer) iterator.next();
      if ( obsrvr != null ) {
        obsrvr.update(this);
      }
      else {
        this.removeObserver(obsrvr); // removing the observer if the reference is null
      }
    }
  }

  public void registerObserver(Object o) {
    // adding o as observer if not already in the list
    if ( ! this.observers.contains(o) ) {
      this.observers.add(o);
    }
  }

  public void removeObserver(Object o) {
    // we remove the observer from the list
    this.observers.remove(o);
  }
  
  /**
   * Displays the Label for simple search
   */
  public void setToSimpleControlls() {
    this.simpleSearchLabel.setVisible(false);
    this.extSearchLabel.setVisible(true);
    this.resetButton.setVisible(false);
  }

  /**
   * Displays the label for extended search
   */
  public void setToExtendedControlls() {
    this.extSearchLabel.setVisible(false);
    this.simpleSearchLabel.setVisible(true);
    this.resetButton.setVisible(true);
  }
  
  /**
   * Returns <code>true</code> if the reset Button has been clicked.
   * After returning the value the intern value is set to false again,
   * so store it locally if you need it more often.
   * @return true if the Reset button was clicked, otherwise false
   */
  public Boolean wasReset() {
    Boolean b = this.resetAction;
    this.resetAction = false;
    return b;
  }
  
  /**
   * Returns <code>true</code> if the search Button has been clicked.
   * After returning the value the intern value is set to false again,
   * so store it locally if you need it more often.
   * @return true if the search button was clicked, otherwise false
   */
  public Boolean wasSearch() {
    Boolean b = this.searchAction;
    this.searchAction = false;
    return b;
  }
  
  /**
   * Returns <code>true</code> if the label for simple search has been clicked.
   * After returning the value the intern value is set to false again,
   * so store it locally if you need it more often.
   * @return true if the label for simple search was clicked, otherwise false
   */
  public Boolean wantsSimpleSearch() {
    Boolean b = this.wantsSimpleControlls;
    this.wantsSimpleControlls = false;
    return b;
  }
  
  /**
   * Returns <code>true</code> if the label for extended search has been clicked.
   * After returning the value the intern value is set to false again,
   * so store it locally if you need it more often.
   * @return true if the label for extended search was clicked, otherwise false
   */
  public Boolean wantsExtendedSearch() {
    Boolean b = this.wantsExtendedControlls;
    this.wantsExtendedControlls = false;
    return b;
  }
  
}
