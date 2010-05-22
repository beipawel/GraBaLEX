package org.homelinux.kapa.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;

public class ShrinkButton extends Anchor implements WidgetNavi {

  private WidgetNavigation          navi                = null;
  
  public ShrinkButton () {
    super("←");
    this.setVisible(false); // this is invisible in the beginning
    this.setStylePrimaryName("specification-button");
  }
  
  protected void onAttach () {
    super.onAttach();
    this.navi = new WidgetNavigation(this);
    this.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        navi.getWrapper().shrink();
      }
    });
//    this.setTabIndex(GWTLexikonGUI.getNextTabIndex());
  }
  
  public Specification getWrapper() {
    return navi.getWrapper();
  }

}
