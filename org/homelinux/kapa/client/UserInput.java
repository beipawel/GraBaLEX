package org.homelinux.kapa.client;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class will choose, which Widget should be used for getting a user input.
 *  
 */
public class UserInput {
  
  private Widget                  chosenWidget    = null; // this will store a reference to the actually chosen widget
  
  public UserInput() {}
  
  public Widget getApropriateWidget(Property prop) {
    dealWithPrevChosenWidget();
    if ( prop.getBehaviorType() instanceof Dynamic ) {
      this.chosenWidget = new TextSearchBox();
    }
    else if ( prop.getBehaviorType() instanceof ComplexEndPoint ) {
      this.chosenWidget = new DropDownInput();
    }
    else if ( prop.getBehaviorType() instanceof SimpleEndPoint ) {
      //        The TextSearchBox automatically registeres an KeyUpHandler, which is not really 
      //        needed in the case of SimpleEndPoint, so we could use a different widget here,
      //        or give the TextSearchBox a additional constructor which doesn't register the Handler.
      //        Even better would be to have a hierarchy of TextSearchBox classes like
      //          ... -> TextSearchBox -> DynamicTextSearchBox
      //          ... -> TextSearchBox -> EndPointOnlyTextSearchBox
      //        If the current implementation results in no problems, we'll leave it as is.
      this.chosenWidget = new TextSearchBox();
    }
    else if ( prop.getBehaviorType() instanceof NoEndPoint ) {
      this.chosenWidget = null;
    }
    else {
      // as a default we'll display a text box
      this.chosenWidget = new TextSearchBox();
    }
    
    // giving the Widget a tabindex
    if ( this.chosenWidget != null && (this.chosenWidget instanceof FocusWidget) ) {
//      ((FocusWidget) this.chosenWidget).setTabIndex(GWTLexikonGUI.getNextTabIndex());
    }
    return this.chosenWidget;
  }
  
  private void dealWithPrevChosenWidget() {
    // we'll unAttach the Widget and then delete the reference, so it will be cleaned from the Heap (hopefully also in the JavaScript version)
    if ( this.chosenWidget != null ) {
      this.chosenWidget.removeFromParent();
      this.chosenWidget = null;
    }
  }
  
  public String getInput() {
    if ( this.chosenWidget instanceof TextSearchBox ) {
      TextSearchBox tsb = (TextSearchBox) this.chosenWidget;
      if ( tsb.isEnabled() ) { // the tsb is enabled we'll return the content
        return tsb.getText();
      }
      else {  // otherwise we'll return a empty string
        return "";
      }
    }
    else if ( this.chosenWidget instanceof DropDownInput ) {
      DropDownInput ddi = (DropDownInput) this.chosenWidget;
      return "<"+ddi.getValue(ddi.getSelectedIndex())+">";
    }
    return "";
  }
  
  public Widget getChosenWidget() {
    return this.chosenWidget;
  }
}
