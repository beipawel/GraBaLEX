package org.homelinux.kapa.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class TestVPanel extends HorizontalPanel {
  
  private Label     label = new Label();
  private Specifications          wrapper               = null; // this will hold a reference to the object containing THIS
  private Specifications          subSpecifications     = null; // this will hold a reference to the Specifications object (if present) which sub-specifies THIS
  private ExtendButton            extButton             = new ExtendButton();
  private AddButton               addButton             = new AddButton();
  private MinusButton             minButton             = new MinusButton();
//  private TextSearchBox           textInput             = new TextSearchBox();
  private UserInput               userInput             = new UserInput(); // this is an object, which will deside which widget to choose for input
  private DropDownInput           dropDownInput         = new DropDownInput(); // if the property selected in PropertyDropDown is EndPoint, we'll need this Widget
  private PropertyDropDown        dropDown              = null;
  private SpecificationWrapper    tranparentWrapper     = null;
  
  public TestVPanel (Specifications wrapper, SpecificationWrapper transparentWrapper) {
    super();
//    this.setStylePrimaryName("test-vpanel");
    this.wrapper = wrapper;
    this.tranparentWrapper = transparentWrapper;
    this.dropDown = new PropertyDropDown(this.wrapper.getProperties());
  }
  
  protected void onAttach() {
    super.onAttach();
    this.setStyleName("test-vpanel");
    this.setStylePrimaryName("test-vpanel");
    this.label.setText("Hello World");
    this.add(label);
  }
}
