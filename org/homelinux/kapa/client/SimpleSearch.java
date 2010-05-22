package org.homelinux.kapa.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleSearch extends VerticalPanel implements Queryable, Observerable {
  
  private TextBox                 input             = new TextBox();
  private ArrayList<Observer>     observers         = new ArrayList<Observer>();
  
  public SimpleSearch () {
    super();
    this.input.setStyleName("simple-search-input");
    this.addStyleName("search-box");
    this.input.addKeyPressHandler(new KeyPressHandler() {
      
      public void onKeyPress(KeyPressEvent event) {
        if ( event.getCharCode() == 13 ) {
          notifyObservers();
        }
      }
    });
  }
  
  protected void onAttach() {
    super.onAttach();
    this.getElement().setId("simple-search-box");
    this.add(this.input);
    this.input.setFocus(true);
  }
  
  /**
   * Calling this method makes no sense in <code>this</code>.
   * It's implemented because it's in the Interface Queryable
   * More sense makes the {@link #toSPARQLQuery()} method.
   * @return Returns an empty String.
   */
  public String toSPARQLQuery(String var) {
    return "";
  }
  
  /**
   * Returns a Query as String.
   * In <code>this</code> it's a hard coded query.
   */
  public String toSPARQLQuery() {
    // the root variable
    String main_var = "?lexeme";
    
    if ( this.getInput().length() == 0 || this.getInput().matches("^\\s+$") ) {
      return ""; // returning an empty string if no input made
    }
    // the rest of the query is added on the server side
    // like ?lexeme being mapped to medic:Lexeme type
    String query = "" +
    		"  ?lexeme medic:has-sense-or-form-relation-to [ " +
    		"  medic:has-form-description [ " +
    		"  medic:has-orthographic-form ?oform ]].\n" +
    		"  FILTER (regex(str(?oform),\"\\\\b"+this.getInput()+"\\\\b\",\"i\"))\n";
    System.out.println("QUERY:\n"+query);
    return query;
  }
  
  private String getInput() {
    return this.input.getValue();
  }
  
  /**
   * Adds or Removes an additional CSS class selector to <code>this</code>.
   * The CSS selector <code>with-extended</code> will be added, if <code>true</code> is passed, otherwise
   * that selector will be removed, if present.
   * @param with_extended
   */
  public void withExtendedSearch(Boolean with_extended) {
    if ( with_extended ) {
      this.addStyleName("with-extended");
    }
    else {
      this.removeStyleName("with-extended");
    }
  }
  
  // START OBSERVERABLE INTERFACE METHODS 
  public void notifyObservers() {
    for (Iterator<Observer> observerIterator = this.observers.iterator(); observerIterator.hasNext();) {
      Observer obsrvr = observerIterator.next();
      if ( obsrvr != null ) {
        if ( obsrvr instanceof Widget ) {
          Widget w = (Widget) obsrvr;
          if ( w.isAttached() ) {
            obsrvr.update(this);
          }
          else {
            this.removeObserver(obsrvr);
          }
        }
        else {
          obsrvr.update(this);
        }
      }
      else {
        this.removeObserver(obsrvr);
      }
    }
  }

  public void registerObserver(Object o) {
    if ( ! this.observers.contains(o) ) {
      this.observers.add(((Observer) o));
    }
  }

  public void removeObserver(Object o) {
    this.observers.remove(o);
  }
  // END OBSERVERABLE INTERFACE METHODS
  
}
