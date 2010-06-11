package org.homelinux.kapa.client;

import java.util.Iterator;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Specification extends HorizontalPanel implements Queryable {
  
  private Specifications          wrapper               = null; // this will hold a reference to the object containing THIS
  private Specifications          subSpecifications     = null; // this will hold a reference to the Specifications object (if present) which sub-specifies THIS
  private ExtendButton            extButton             = new ExtendButton();
  private ShrinkButton            shrinkButton          = new ShrinkButton();
  private AddButton               addButton             = new AddButton();
  private MinusButton             minButton             = new MinusButton();
//  private TextSearchBox           textInput             = new TextSearchBox();
  private UserInput               userInput             = new UserInput(); // this is an object, which will deside which widget to choose for input
  private DropDownInput           dropDownInput         = new DropDownInput(); // if the property selected in PropertyDropDown is EndPoint, we'll need this Widget
  private PropertyDropDown        dropDown              = null;
  private SpecificationWrapper    tranparentWrapper     = null;
  private Boolean                 autoExtend            = true;
  
  public Specification(Specifications wrapper, SpecificationWrapper transparentWrapper) {
    super(); // calling the super constructor
    this.wrapper = wrapper;
    this.tranparentWrapper = transparentWrapper;
    this.dropDown = new PropertyDropDown(this.wrapper.getProperties());
  }

  protected void onAttach() {
    super.onAttach();
    System.out.println("onAttach(): here we are");
    this.setStyleName("specification");
    this.setStylePrimaryName("specification");
//    this.add((new TestVPanel()));
    this.addDefaultWidgets();
  }
  
  /**
   * Adds a sub-Specifications object
   */
  public void extend() {
    if ( (this.subSpecifications == null) || (!this.subSpecifications.isAttached()) ) {
      // we only extend if there is no other Specifications object present
      this.subSpecifications = new Specifications(this,this.getWrapper().getProperties());
      // attaching it
      this.getTransparentWrapper().add(this.subSpecifications);
      this.getExtendButton().setVisible(false);
      this.getShrinkButton().setVisible(true);
      // We disable the TextSearchBox when extending
      if ( this.getPropertyDropDown().getSelectedProperty().getBehaviorType() instanceof Dynamic ) {
        this.getTextSearchBox().setEnabled(false);
      }
    }
  }
  
  /**
   * Removes a sub-Specifications object
   */
  public void shrink() {
    this.subSpecifications.removeFromParent();
    this.subSpecifications = null;
    this.getExtendButton().setVisible(true);
    this.getShrinkButton().setVisible(false);
    // We enable the TextSearchBox when shrinking
    if ( this.getPropertyDropDown().getSelectedProperty().getBehaviorType() instanceof Dynamic ) {
      this.getTextSearchBox().setEnabled(true); // we disable the textbox
    }
  }
  
  /**
   * Chooses the appropriate widget for the selected Property.
   * @param prop          Property for which to choose the Widget
   * @param autoExtend    Whether or not to extend <code>this</code> with a Specifications object.
   */
  public void setAppropriateWidget(Property prop, Boolean autoExtend) {
    this.autoExtend = autoExtend;
    setAppropriateWidget(prop);
  }
  
  private void setAppropriateWidget(Property prop) {
    Widget inputWidget = this.userInput.getApropriateWidget(prop);
    if ( inputWidget == null ) {  // null means, we have a NoEndPoint BehavioralType
      if ( this.autoExtend ) {
        this.extend(); 
      }
    }
    else {
      this.insert(inputWidget, this.getWidgetIndex(this.getPropertyDropDown())+1); // inserting the input widget after the property list
    }
  }
  
  public void removeSpecification() {
    this.tranparentWrapper.removeFromParent();
    this.wrapper.toggleMinusButtonVisibility();
  }
  
  
  protected void onLoad() {
    super.onLoad();
    this.wrapper.toggleMinusButtonVisibility();
  }
  
  private void addDefaultWidgets() {
    this.add(this.dropDown);
    this.add(this.addButton);
    this.add(this.minButton);
    this.add(this.extButton);
    this.add(this.shrinkButton);
    this.setAppropriateWidget(this.getPropertyDropDown().getSelectedProperty()); // at last we decide which widget should be used for user input
    
  }
  
  public void hideMinusButton() {
    this.minButton.setVisible(false);
  }
  
  public void showMinusButton() {
    this.minButton.setVisible(true);
  }
  

  /** 
   * Calling {@link #toSPARQLQuery()} in <code>this</code> makes no sense.
   * This Method is implemented because it's in the Interface Queryable.
   * @return Returns an empty String.
   */
  public String toSPARQLQuery() {
    return "";
  }
  
  
  /**
   * Builds and returns a SPARQL query for this and all subordinated Specification and Specifications objects
   * @param main_var
   * @return
   */
  public String toSPARQLQuery(String main_var) {
    String queryString = "";
    String v = "?v"+(new Integer(Random.nextInt(99999999)).toString()); // we need a variable name, this Random one will do for now
    // example for the variable "v"
    // each Specification object holds a query like that
    //   ?main_var <selected property> ?v
    // our "v" will become the main_var in the subspecifications object attached to THIS
    
    String userInput = this.userInput.getInput();
    Property selectedProperty = this.getPropertyDropDown().getSelectedProperty();
    Boolean wrapInSenseOrForm = this.wrapInSenseOrForm(selectedProperty);
    
    if ( userInput.length() != 0 ) {
      // if the user had typed in some text we have a simple task
      int cnt = 0; // keep count of the squared brackets
      if ( wrapInSenseOrForm ) {
        queryString += " "+main_var+" medic:has-sense-or-form-relation-to [ ";
      }
      else {
        queryString += " "+main_var+" ";
      }
      System.err.println("main_var: "+main_var);
      for (Iterator<String> iterator2 = selectedProperty.getPath().iterator(); iterator2.hasNext();) {
        String p = iterator2.next();
        queryString += "<"+p+">";
        if ( iterator2.hasNext() ) {
          System.out.println("cnt: "+cnt);
          queryString += " [ ";
          cnt++;
        }
        else {
          if ( this.userInput.getChosenWidget() instanceof TextSearchBox ) {
            // if there was a text input, we need the variable here, and then later we do a regex on this variable
            queryString += " "+v+" ";
          }
          else {
            // if there was a dropdown list as input userInput is a URI and we simply add it to "s"
            queryString += " "+userInput+" ";
          }
        }
      }
      for (int i = 0; i < cnt; i++) {
        // closing all squared brackets
        queryString += "]";
      }
      if ( this.userInput.getChosenWidget() instanceof TextSearchBox ) {
        // if there was a text input, we have to do a regex filter on the object (range) of the selected property
        if ( wrapInSenseOrForm ) {
          queryString += "]"; // we need an additional closing bracket in this case.
        }
        queryString += ". FILTER (regex(str("+v+"),\"\\\\b"+userInput+"\\\\b\",\"i\"))\n";
      }
      else {
        if ( wrapInSenseOrForm ) {
          queryString += "].\n";
        }
        else {
          queryString+= ". \n";
        }
      }
    }
    else {
      // if there was no input, we check if we have an sub-Specifications object attached
      if ( this.getSubSpecifications() != null && this.getSubSpecifications().isAttached() ) {
        // we call the toSPARQLQuery() method of the subspecifications object
        String tmpString = this.getSubSpecifications().toSPARQLQuery(v); // passing v as main_var to the next deeper level
        
        if ( tmpString.length() > 0 ) {
          // if there was a sub-Specifications object attached, we add the Query string of this to "s"
          if ( wrapInSenseOrForm ) {
//          s += main_var+" <"+selectedProperty.getName()+"> "+v+".\n";
            queryString += main_var+" medic:has-sense-or-form-relation-to [ <"+selectedProperty.getName()+"> "+v+"].\n";
          }
          else {
            queryString += main_var+" <"+selectedProperty.getName()+"> "+v+".\n";
          }
          // now we call the toSPARQLQuery Method on the sub-Specifications object and adding this also to "s"
          queryString += tmpString; // here we add the subspec Query string to our Query String
          
        }
      }
    }
    return queryString;
  }
  
  // SETTERS

  // GETTERS
  public Specifications getWrapper() {
    return this.wrapper;
  }
  public SpecificationWrapper getTransparentWrapper() {
    return this.tranparentWrapper;
  }
  public Specifications getSubSpecifications() {
    return this.subSpecifications;
  }
  public ExtendButton getExtendButton() {
    return this.extButton;
  }
  public ShrinkButton getShrinkButton() {
    return this.shrinkButton;
  }
  public AddButton getAddButton() {
    return this.addButton;
  }
  public MinusButton getMinusButton() {
    return this.minButton;
  }
  public TextSearchBox getTextSearchBox() {
    if ( this.userInput.getChosenWidget() instanceof TextSearchBox ) {
      return ((TextSearchBox) this.userInput.getChosenWidget());
    }
    return null;
  }
  public DropDownInput getDropDownInput() {
    return this.dropDownInput;
  }
  public PropertyDropDown getPropertyDropDown() {
    return this.dropDown;
  }

  public void adjustSubspecificationButton(Property prop) {
    if ( !(prop.getBehaviorType() instanceof NoEndPoint) ) {  // for all but the NoEndPoint Property we'll have to hide the shrink button
      this.getShrinkButton().setVisible(false);

      if ( prop.getBehaviorType() instanceof EndPoint ) { // there should be no extend button for an EndPoint Property 
        this.getExtendButton().setVisible(false);
      }
      else {
        this.getExtendButton().setVisible(true);
      }
    }
  }
  
  private Boolean wrapInSenseOrForm(Property property) {
    // if the Domain of the selected Property has a none empty intersection with Properties.getDefaultDomain(), we want to return true,
    // so that it can be prefixed with medic:has-sense-or-form-relation-to
    // If the passed property is rdf:type, then we don't want it to be prefixed with medic:has-sense-or-form-relation-to, hence we return false.
    // The reason why we have to check for rdf:type separately is, that the properties domain contains all OWL-classes, so that it's visible
    // in each PropertyDropDown object regardless of which property was selected in its wrapper Specification object. Because of that implementation
    // we would return true in the for loop below.
    if ( property.getName().matches(".*rdf.*#type$") ) {
      for (Iterator<String> domIterator = property.getRange().iterator(); domIterator.hasNext();) {
        String cls = domIterator.next();
        if ( this.wrapper.getProperties().getDefaultDomain().contains(cls) ) {
          return true;
        }
      }
      return false;
    }
    for (Iterator<String> domIterator = property.getDomain().iterator(); domIterator.hasNext();) {
      String cls = domIterator.next();
      if ( this.wrapper.getProperties().getDefaultDomain().contains(cls) ) {
        return true;
      }
    }
    return false;
  }
  
}
