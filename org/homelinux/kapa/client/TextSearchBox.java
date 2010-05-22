package org.homelinux.kapa.client;


//import com.google.gwt.event.dom.client.KeyUpEvent;
//import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextBox;

public class TextSearchBox extends TextBox implements WidgetNavi {
  private static final long     serialVersionUID = 2964457725059995076L;
  private WidgetNavigation      navi = null;
  private String                text = "";

  public TextSearchBox() {
    // TODO Auto-generated constructor stub
  }
  
  protected void onAttach() {
    super.onAttach();
    this.navi = new WidgetNavigation(this);
    // adding a keyup handler
//    this.addKeyUpHandler(new KeyUpHandler() {
//      public void onKeyUp(KeyUpEvent event) {
//        onKeyUpHandler(event);
//      }
//    });
  }
  
//  private void onKeyUpHandler(KeyUpEvent event) {
//    // TODO: check for event == null
//    // TODO: hide sub-specification button/label if text is typed in
//    if ( this.getText().length() == 0 ) {
//      navi.getWrapper().getExtendButton().setVisible(true);
//    }
//    else {
//      navi.getWrapper().getExtendButton().setVisible(false);
//    }
//  }

  public Specification getWrapper() {
    return navi.getWrapper();
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if ( enabled ) {
      this.removeStyleName("disabled");  // removes the css class mark
      this.setTitle("");
      this.setText(this.text);
      this.selectAll();
      this.setFocus(true);
    }
    else {
      this.text = this.getText();
      this.setText("");  // we delete the textbox
      this.addStyleName("disabled");  // add a css class name, so we can mark it somehow visually
      this.setTitle("Sub-Spezifikation entfernen um hier schreiben zu können");
    }
  }
}
