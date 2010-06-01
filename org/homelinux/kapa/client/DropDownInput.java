package org.homelinux.kapa.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;

public class DropDownInput extends ListBox implements WidgetNavi, Observer {

  private WidgetNavigation                            navi                    = null;
  private ControllerServiceAsync                      controllerSrvs          = GWT.create(ControllerService.class);
  private AsyncCallback<ArrayList<OWLInstanceItem>>   callback                = null;
  private ArrayList<OWLInstanceItem>                  items                   = null; 

  
  
  public DropDownInput() {
    this.callback = new AsyncCallback<ArrayList<OWLInstanceItem>>() {
      public void onFailure(Throwable caught) {
        Window.alert("Problems with RPC while getting instances for a DropDownInput, we could do something fancy \n" +
                     "and call a method of GWTLexikonGUI which deals with that problem.\n" +
                     "Maybe an analysis, what went wrong?");
      }
      public void onSuccess(ArrayList<OWLInstanceItem> result) {
        fillDropDown(result);
      }
    };
    this.setStylePrimaryName("drop-down-input");
  }
  
  protected void onAttach() {
    super.onAttach();
    this.navi = new WidgetNavigation(this);
    Specifications.getProfileAndLanguageChanger().registerObserver(this); // we register to the observerable object
    Property prop = this.getWrapper().getPropertyDropDown().getSelectedProperty();
    // special treatment for rdf:type
    if ( prop.getName().matches(".*rdf.*#type$") ) {
      if ( this.navi.getLocalRoot() != this.navi.getRoot() ) {
        // if we are not in the root Specifications object, we have to change the Range of the Property object
        Property previousProperty = this.navi.getLocalRoot().getWrapper().getPropertyDropDown().getSelectedProperty();
        Property propCopy = new Property();
        propCopy.setBehavioralType(prop.getBehaviorType());
        // we'll set another range.
        propCopy.setRange(previousProperty.getRange());
        prop = propCopy;
      }
    }
    controllerSrvs.getOWLInstanceItems(prop, this.callback);
  }

  public Specification getWrapper() {
    return navi.getWrapper();
  }

  private void fillDropDown(ArrayList<OWLInstanceItem> items) {
    if ( items != null ) {
      this.items = items;
    }
    String selectedItemURI = ""; // declaring a var for currently selected index
    if ( this.getItemCount() > 0 ) {
       selectedItemURI = this.getValue(this.getSelectedIndex()); // if we have element in this then we get the uri of the selected item
    }
    this.clear(); // clear the list
    Collections.sort(this.items);
    for (Iterator<OWLInstanceItem> iterator = this.items.iterator(); iterator.hasNext();) {
      OWLInstanceItem item = iterator.next();
      if ( item.isVisible() ) { // add item only if it is visible (access status is set)
        this.addItem(item.getLabel(), item.getURI());
        if ( item.getURI().equals(selectedItemURI) ) { // if we are dealing with an item whichs uri equals selectedItemURI, we set it to be the selected one
          this.setSelectedIndex(this.getItemCount()-1);
        }
      }
    }
    this.setTitle("Items: #"+this.items.size());
  }

  public void update(Object observerable) {
    // we simply call fillDropDown to update the list of instances
    this.fillDropDown(null);
  }

}
