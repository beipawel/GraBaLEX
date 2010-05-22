package org.homelinux.kapa.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;

public class ExtendButton extends Anchor implements WidgetNavi {

  private WidgetNavigation          navi                = null;
//  private String                    openCharacter       = "→";
//  private String                    closeCharacter      = "←";
  
  public ExtendButton() {
    super("→");
    this.setStylePrimaryName("specification-button");
  }

  protected void onAttach () {
    super.onAttach();
    this.navi = new WidgetNavigation(this);
    this.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        navi.getWrapper().extend();
      }
    });
//    this.setTabIndex(GWTLexikonGUI.getNextTabIndex());
  }
  
  public Specification getWrapper() {
    return navi.getWrapper();
  }

}
