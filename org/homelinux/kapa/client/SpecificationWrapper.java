package org.homelinux.kapa.client;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Wraps up the Specification object and a sub-Specifications (note the "s" at the end).
 * That way the sub-Specifications object is attached below the Specification object it's
 * specifying.
 * <p>
 * This widget extends the VerticalPanel widget.
 * <p>
 * This widget holds max. 2 other widget, namely a Specification and, if selected by the user
 * a Specifications object.
 * <p>
 * This widget should be absolutely transparent, so it won't brake the current implementation.
 * As I see it, it should be possible to avoid ever calling its methods from another object. 
 * <p>
 * Most of it methods will simply forward to the methods of the containing Specification object,
 * or the Specifications object wrapping this. 
 * TODO: check if that is true after implementation!
 *   
 * @author Pawel Müller
 *
 */
public class SpecificationWrapper extends VerticalPanel {
  
  private Specifications                 wrapper           = null;  // will hold a reference to the wrapper object
  private Specification                  specification     = null;  // will hold a reference to the Specification itself
  private Specifications                 subSpecifications = null;  // will hold a reference to the sub-Specifications object
  private TestVPanel                     testVPanel        = null;
  
  public SpecificationWrapper (Specifications specifications) {
    super();
    this.wrapper = specifications;
    this.specification = new Specification(this.wrapper,this);
//    this.testVPanel = new TestVPanel(this.wrapper,this);
  }
  
  protected void onAttach() {
    super.onAttach();
    this.setStylePrimaryName("specification-wrapper");
    // Add a Specification Object
    this.add(this.specification);
//    this.add(this.testVPanel);
    System.out.println("onAttach(): onAttach was called in specWrapper");
  }
  
  public void add(Widget w) {
    super.add(w);
    if ( w instanceof Specifications ) {
      this.subSpecifications = (Specifications) w;
    }
    System.out.println("add(): Added something to SpecificationWrapper widget");
  }
  
  public void removeFromParent() {
    super.removeFromParent();
    this.wrapper.toggleMinusButtonVisibility();
  }
  
  // GETTERS
  public Specification getSpecification() {
    return this.specification;
  }
  
  public Specifications getSubSpecifications() {
    return this.subSpecifications;
  }

  
}
