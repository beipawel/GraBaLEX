package org.homelinux.kapa.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class PropertyDropDown extends ListBox implements WidgetNavi, Observer {

  private WidgetNavigation        navi                    = null;
  private Properties              allProps;
  private ArrayList<Property>     props                   = null;
  private int                     leftPropertyIndex       = -1;
  private String                  currentLanguage;
  private String                  currentProfile;
  private Boolean                 triggerOnChange         = true;
  
  public PropertyDropDown(Properties props) {
    this.allProps = props;  // each instance should have a reference to the big list of properties
  }
  
  protected void onAttach() {
    super.onAttach();
    this.navi = new WidgetNavigation(this);
    this.navi.getLocalRoot().registerObserver(this); // we register to the observerable object
    this.currentLanguage = ProfileAndLanguageChanger.getStaticLanguage();
    this.currentProfile = ProfileAndLanguageChanger.getStaticProfile();
    this.fillDropDown();
    this.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        onChangeHandler(event);  // we forward this a local method
      }
    });
    
  }
  
  private void onChangeHandler(ChangeEvent event) {
    if ( this.triggerOnChange ) {
      // If there is a Subspecification object and the user changes the selected property, we'll remove the sub object
      if (navi.getWrapper().getSubSpecifications() != null ) { 
        navi.getWrapper().getSubSpecifications().removeFromParent();
      }
      // say the wrapper to display the corresponding input widget (text box or drop-down)
      if ( (this.getSelectedProperty().getBehaviorType() instanceof NoEndPoint) && event == null ) {
        // if we have a NoEndPoint Property and the onChangeHandler was artificially triggert (that means, 
        // not by a real click event, but rather by the first load of _this_ (event == null) ),
        // we do not want it to extend() automatically.
        // that's why we use the overloaded method setApropriateWidget with a second argument
        navi.getWrapper().setAppropriateWidget(this.getSelectedProperty(),false);
      }
      else {
        navi.getWrapper().setAppropriateWidget(this.getSelectedProperty(),true);
      }
//      Window.alert("What's the path of "+getSelectedProperty().getName()+"?\n- "+getSelectedProperty().getPath().toString());
      navi.getWrapper().adjustSubspecificationButton(this.getSelectedProperty());
    }
  }
  
  public Specification getWrapper() {
    return navi.getWrapper();
  }

  private void fillDropDown() {
    Property selectedProperty = null; // declaring a new Property. Need that for profile/language change
    if ( this.getItemCount() > 0 ) {
      selectedProperty = this.getSelectedProperty(); // storing the currently selected Property
    }
    this.clear();
    this.props = this.navi.getLocalRoot().getLocalProperties();
    for (Iterator<Property> iterator = this.props.iterator(); iterator.hasNext();) {
      Property prop = iterator.next();
      this.addItem(prop.getLabel(), new Integer(this.allProps.indexOf(prop)).toString());
//      this.addItem(prop.getLabel(this.currentProfile, this.currentLanguage)+" ("+prop.getBehaviorType().getName()+")", new Integer(this.allProps.indexOf(prop)).toString());
    }
    if ( selectedProperty != null ) {
      // after changing to another profile, the order of the displayed properties might change
      // that's why we want the property selected before the profile or language change to be the one displayed
      // after the profile/language change, even if it label changed
      // that what happens in this IF block
      if ( this.props.contains(selectedProperty) ) {
        this.setSelectedIndex(this.props.indexOf(selectedProperty)); // resetting the previously selected Property as selected Property
      }
      else {
        // if the previously selected Property was not in the list of properties, all Subspecifications should be closed.
        // we can achieve this by setting triggerOnChange to true, so that the call of the onChangeHandler() method
        // at the end of this method call will actually trigger a change.
        this.triggerOnChange = true;
      }
    }
    this.setTitle("Properties: #"+this.props.size());
    // after filling the dropdown, the preselected item should invoke the behavior any other item would when clicked on it.
    // If we don't do that, the preselected item will not have the appropriate widget selected, as well as it might have
    // a subspecification arrow, although it might be an EndPoint.
    this.setFocus(true);  // giving focus to _this_ for easier keyboard maneuvering 
    this.onChangeHandler(null); 
  }
  
  public Property getSelectedProperty() {
    int i = this.getSelectedIndex();
    try {
//      System.err.println("getSelectedProperty: this.getSelectedIndex():"+i);
      return this.props.get(i);
    }
    catch(Exception e) {
      e.printStackTrace();
      return this.props.get(0);
    }
    
  }
  
  /**
   * @deprecated not needed
   */
  private void getLeftPropertyIndex() {
    if ( this.leftPropertyIndex == -1 ) {
      if ( this.navi.getWrapper().getWrapper().isRoot() ) {
        System.out.println("Wir sind bei root und haben daher eine leere range");
      }
      else {
        System.out.println("Wir sind nicht bei root und gehen daher noch eine hirarchiestufe höher, zum übergeordnetem Specification objekt");
        Property p = this.navi.getWrapper().getWrapper().getWrapper().getPropertyDropDown().getSelectedProperty();
        this.leftPropertyIndex = this.allProps.indexOf(p); // well, this one could be improved later
      }
    }
  }

  public void update(Object o) {
    if (o instanceof Specifications) {
      this.currentLanguage = ProfileAndLanguageChanger.getStaticLanguage();
      this.currentProfile = ProfileAndLanguageChanger.getStaticProfile();
      this.triggerOnChange = false; // we disable any onChange behavior
      this.fillDropDown(); // fill the dropdown with the new labels
      this.triggerOnChange = true; // restore onChange behavior
    }
  }
  
}
