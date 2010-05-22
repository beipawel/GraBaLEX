package org.homelinux.kapa.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Specifications extends VerticalPanel implements Queryable, Observer, Observerable {
  /**
   * Instances of this class holds a list of Specification objects
   */
  
  private Specification         wrapper                   = null; // this will be set to a reference to the object containing THIS
  private ArrayList<Property>   localProps                = null; // Local List of properties for all specification objects at that level under this specifications object (kind of caching)
  private Properties            props                     = null; // this will hold a reference to the Properties List
  private Boolean               root                      = false; // marks an instance as root
  private SpecificationWrapper  firstSpecWrapper          = null; // the very first Specification object in this Widget
  private String                main_var                  = "";   // this will hold a variable name for the toSPARQLQuery() method
  private static Observerable   profileAndLanguageChanger = null; // this will hold a reference to the ProfileAndLanguageChanger object
  private static Label          loadStatusLabel           = null; // a reference to a load status label, so that the uses sees if a widget is occupied
  private ArrayList<Observer>   observers                 = new ArrayList<Observer>();
  
  public Specifications(Specification wrapper, Properties props) {
    super(); // calling the super constructor
    if ( wrapper == null ) {
      this.root = true;
      this.addStyleName("search-box");
    }
    this.wrapper = wrapper;
    this.props = props;
//    this.extend(); // adding the first Specification object NOTE 10-01-18: now in the onAttach() Method
    
  }
  
  protected void onAttach() {
    super.onAttach();
    Specifications.getProfileAndLanguageChanger().registerObserver(this);
    this.setStyleName("specifications");
    this.computeLocalProperties();  // computing the local properties for that level
    this.extend();
  }
  
  public void extend() {
    /**
     * This method will add a new Specification object
     */
    this.add((new SpecificationWrapper(this))); // is it really that simple?
  }
  
  /**
   * Computes the local Properties for that level
   */
  private void computeLocalProperties() {
    if ( this.isRoot() ) {
      // if we are in the root element, we filter on the default.
      // The Properties object is doing it for us
      this.setLocalProps(this.props.filter());
    }
    else {
      // if we are NOT root, we have to find out the selected Property to the left
      Property p = this.getWrapper().getPropertyDropDown().getSelectedProperty();
      this.setLocalProps(this.props.filter(this.props.indexOf(p)));
    }
  }
  
  private void setFirstSpecificationWrapperObject() {
    if ( this.getWidgetCount() > 0 ) {
      if ( this.getWidget(0) != null ) {
        this.firstSpecWrapper = (SpecificationWrapper) this.getWidget(0);
      }
    }
  }
  
  public void toggleMinusButtonVisibility() {
//    System.out.println("widgetCount(): "+this.getWidgetCount()); // TODO: diese methode wird bei einem removeFromParent des Specification objekts zwei mal aufgerufen. Warum?
    this.setFirstSpecificationWrapperObject();
    if ( this.getWidgetCount() == 1 ) {
      this.firstSpecWrapper.getSpecification().hideMinusButton();
    }
    else if ( this.getWidgetCount() > 1 ){
      this.firstSpecWrapper.getSpecification().getMinusButton().setVisible(true);
    }
  }
  
  public String toSPARQLQuery() {
    /**
     * To call this method makes only sense if you are calling it on the root Specifications object
     */
    if ( ! this.isRoot() ) {
      System.err.println("Calling the noARG toSPARQLQuery() on a none root Specifications object");
      return "none";
    }
    // the root variable
    String v = "?lexeme";
    return this.toSPARQLQuery(v);
//    return this.toSPARQLQuery("?lexeme");
  }
  
  @SuppressWarnings("unchecked")
  public String toSPARQLQuery(String main_var) {
    this.main_var = main_var; // maybe in the future we want to be able to set the variable name manually
    String s = ""; // this will hold the query for this specifications object
    for (Iterator iterator = this.iterator(); iterator.hasNext();) {
      SpecificationWrapper spWrapper = (SpecificationWrapper) iterator.next();
      Specification sp = spWrapper.getSpecification();
      s += sp.toSPARQLQuery(this.main_var);
      if ( s.length() > 0 ) {
        s += "\n";
      }
    }
    return s;
  }
  
  // SETTERS
  public void setLocalProps(ArrayList<Property> props) {
    this.localProps = props;
  }
  
  public static void setProfileAndLanguageChanger(Observerable o) {
    profileAndLanguageChanger = o;
  }
  
  // GETTERS
  public Properties getProperties() {
    return this.props;
  }
  public ArrayList<Property> getLocalProperties() {
    return this.localProps;
  }
  public Boolean isRoot() {
    return this.root;
  }
  public Specification getWrapper() {
    return this.wrapper;
  }
  public String getMainVar() {
    return this.main_var;
  }
  public static Observerable getProfileAndLanguageChanger() {
    return profileAndLanguageChanger;
  }

  public void update(Object o) {
    if (o instanceof ProfileAndLanguageChanger) {
      // if we're being updated by the ProfileAndLanguageChanger, we have to make a new computation of the local properties
      this.computeLocalProperties();
      this.notifyObservers();
    }
  }

  public void notifyObservers() {
    for (Iterator<Observer> iterator = this.observers.iterator(); iterator.hasNext();) {
      Observer obsrv = iterator.next();
      obsrv.update(this);
    }
  }

  public void registerObserver(Object o) {
    if ( ! this.observers.contains(o)) {
      this.observers.add(((Observer) o));
    }
  }

  public void removeObserver(Object o) {
    this.observers.remove(o);
  }
  
}
