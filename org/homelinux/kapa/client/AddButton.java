package org.homelinux.kapa.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;

/**
 * Instances of that Widget will add new {@link Specification} objects to the GUI (a new row).
 * It is supposed to be used only in a {@link Specification} object.
 * @author Pawel Müller
 *
 */
public class AddButton extends Anchor implements WidgetNavi {

  private WidgetNavigation navi = null;

  /**
   * Public Constructor.
   */
  public AddButton() {
    super("+");
    this.setStylePrimaryName("specification-button");
  }

  /**
   * When the object is attached to the DOM a {@link ClickHandler} will be registered.
   */
  protected void onAttach () {
    super.onAttach();
    this.navi = new WidgetNavigation(this);
    this.addClickHandler(new ClickHandler() {
      
      public void onClick(ClickEvent event) {
        navi.getLocalRoot().extend();
        event.stopPropagation();
      }
    });
//    this.setHref("#");
//    this.setTabIndex(GWTLexikonGUI.getNextTabIndex());
  }
  
  /**
   * Returns the {@link Specification} object to which <code>this</code> is attached. 
   */
  public Specification getWrapper() {
    return navi.getWrapper();
  }

}
