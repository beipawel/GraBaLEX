package org.homelinux.kapa.client;

import com.google.gwt.user.client.ui.Widget;

public class WidgetNavigation {
  /**
   * This will hold references to all elements in a Specification object,
   * as well as the wrapper object which will be different for each instance
   */

  private Specification         wrapper     = null;
  private Specifications        localRoot   = null;
  private Specifications        root        = null;
  
  public WidgetNavigation(Widget w) {
//    System.out.println("w: "+w.toString());
    while ( !( w instanceof Specification ) && w.getParent() != null ) {
      w = w.getParent();
    }
    this.wrapper = (Specification) w;
    this.localRoot = this.wrapper.getWrapper();
  }
  
  public Specification getWrapper() {
    return this.wrapper;
  }
  public Specifications getLocalRoot() {
    return this.localRoot;
  }
  public Specifications getRoot() {
    if ( this.root == null ) {
      Specifications s = this.localRoot;
      while ( ! s.isRoot() ) {
        s = s.getWrapper().getWrapper();
      }
      this.root = s;
    }
    return this.root;
  }
}
