package org.homelinux.kapa.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProfileAndLanguageChanger extends HorizontalPanel implements
    Observerable, Observer {
  
  private ArrayList<Observer>         observers         = new ArrayList<Observer>();
  private ArrayList<ClickableLabel>   profileLabels     = new ArrayList<ClickableLabel>();
  private ArrayList<ClickableLabel>   languageLabels    = new ArrayList<ClickableLabel>();
  private Label                       l1                = new Label("Profile: ");
  private Label                       l2                = new Label("Sprachen: ");
  private Boolean                     allLoaded         = false;
  private static String               currentLanguage   = "";
  private static String               currentProfile    = "";
  private ControllerServiceAsync      controllerSrvs    = GWT.create(ControllerService.class);
  
  public ProfileAndLanguageChanger () {
    super();
    // TODO: we could get the preferred Language from the Browser
    this.getAvailableLanguages();
    this.getAvailableProfiles();
    this.setStylePrimaryName("profile-language-changer");
  }
  
  private void getAvailableLanguages() {
    // we have to retrieve all possible languages. We could ask the DataModel via RPC
    AsyncCallback<ArrayList<SimpleLabel>> callback = new AsyncCallback<ArrayList<SimpleLabel>>() {
      public void onFailure(Throwable caught) {
        Window.alert("Problems with RPC while getting ModelLanguages, we could do something fancy \n" +
                     "and call a method of GWTLexikonGUI which deals with that problem.\n" +
                     "Maybe an analysis, what went wrong?");
      }
      public void onSuccess(ArrayList<SimpleLabel> result) {
        onSuccessfullyLoadedLanguagesHandler(result);
      }
    };
    controllerSrvs.getModelLanguages(callback);
    
  }
  
  private void getAvailableProfiles() {
    // retrieve all possible profiles. Asking the DataModel via RPC
    AsyncCallback<ArrayList<SimpleLabel>> callback = new AsyncCallback<ArrayList<SimpleLabel>>() {
      public void onFailure(Throwable caught) {
        Window.alert("Problems with RPC while getting ModelProfiles, we could do something fancy \n" +
                     "and call a method of GWTLexikonGUI which deals with that problem.\n" +
                     "Maybe an analysis, what went wrong?");
      }
      public void onSuccess(ArrayList<SimpleLabel> result) {
        onSuccessfullyLoadedProfilesHandler(result);
      }
    };
    controllerSrvs.getModelProfiles(callback);
  }
  
  private void onSuccessfullyLoadedProfilesHandler(ArrayList<SimpleLabel> label) {
    if ( label.size() > 0 ) {
      currentProfile = label.get(0).getName(); // the first will be the default profile
      for (Iterator<SimpleLabel> iterator = label.iterator(); iterator.hasNext();) {
        SimpleLabel sl = iterator.next();
        addToProfileLabels(sl);
      }
    }
    attachLabels();
  }
  
  private void onSuccessfullyLoadedLanguagesHandler(ArrayList<SimpleLabel> label) {
    if ( label.size() > 0 ) {
      currentLanguage = label.get(0).getName(); // the first will be the default profile
      for (Iterator<SimpleLabel> iterator = label.iterator(); iterator.hasNext();) {
        SimpleLabel sl = iterator.next();
        addToLanguageLabels(sl);
      }
    }
    attachLabels();
  }

  /**
   * Generates a ProfileLabel object for the passed Label and adds it to profileLabels.
   * @param label
   */
  private void addToProfileLabels(SimpleLabel label) {
    // Instantiate a new label object
    ClickableLabel l = new ProfileLabel(label.getLabel(), label.getName());
    // register this as observer
    l.registerObserver(this);
    this.profileLabels.add(l);
  }
  
  /**
   * Generates a LanguageLabel object for the passed Label and adds it to languageLabels.
   * @param label
   */
  private void addToLanguageLabels(SimpleLabel label) {
    // Instantiate a new label object
    ClickableLabel l = new LanguageLabel(label.getLabel(), label.getName());
    // register this as observer
    l.registerObserver(this);
    this.languageLabels.add(l);
  }
  
  protected void onAttach() {
    super.onAttach();
    // set an ID in the DOM
    this.getElement().setId("profile-and-language-changer");
  }
  
  private void attachLabels() {
    if ( this.allLoaded ) {
      // Label for the text "profiles:"
      this.l1.addStyleDependentName("profile-changer");
      // Label for the text "Languages: "
      this.l2.addStyleDependentName("language-changer");
      // adding l1 to this
      this.add(l1);
      // adding all Profile Labels
      for (Iterator<ClickableLabel> profLabelsIter = this.profileLabels.iterator(); profLabelsIter.hasNext();) {
        ClickableLabel l = profLabelsIter.next();
        this.add(l);
      }
      // adding l2 to this
      this.add(l2);
      // adding all language labels
      for (Iterator<ClickableLabel> profLabelsIter = this.languageLabels.iterator(); profLabelsIter.hasNext();) {
        ClickableLabel l = profLabelsIter.next();
        this.add(l);
      }
      // Now that we're done with attaching, we'll inform some registered observers that we're finished.
      // We do that, because it's possible that we actually took too long for getting everything, so that
      // observers may have missed the information we have
      this.notifyObservers();
    }
    else {
      this.allLoaded = true;
    }
    
  }
  
  public void notifyObservers() {
//    Window.alert("Now we would allert all Widgets which needs to change its labels\n" +
//    		"CurrentLanguage: "+this.currentLanguage+"\n" +
//    		"CurrentProfile: "+this.currentProfile);
    for (Iterator<Observer> obsrvrIter = this.observers.iterator(); obsrvrIter.hasNext();) {
      Observer obsrvr = obsrvrIter.next();
      if ( obsrvr != null ) {
        if (obsrvr instanceof Widget) {
          // if the observer is a widget (which always should be true here), we only want to update the
          // widget when it's attached to the DOM
          Widget w = (Widget) obsrvr;
          if ( w.isAttached() ) {
            obsrvr.update(this);
          }
          else {
            this.removeObserver(obsrvr); // clean ourself
            w.removeFromParent();
          }
        }
        else {
          obsrvr.update(this);
        }
        
      }
      else {
        this.removeObserver(obsrvr); // clean ourself
      }
    }
  }

  public void registerObserver(Object o) {
    this.observers.add(((Observer) o));
  }
  
  public void removeObserver(Object o) {
    this.observers.remove(o);
  }
  
  public void update(Object o) {
    if (o instanceof ProfileLabel) {
      ClickableLabel l = (ClickableLabel) o;
      currentProfile = l.getName();
   // We have to update the CSS class mark "active" on each Label
      for (Iterator<ClickableLabel> iterator = this.profileLabels.iterator(); iterator.hasNext();) {
        ClickableLabel cl = iterator.next();
        cl.activate();
      }
    }
    else if (o instanceof LanguageLabel) {
      LanguageLabel l = (LanguageLabel) o;
      currentLanguage = l.getName();
      // We have to update the CSS class mark "active" on each Label
      for (Iterator<ClickableLabel> iterator = this.languageLabels.iterator(); iterator.hasNext();) {
        ClickableLabel cl = iterator.next();
        cl.activate();
      }
    }
    this.notifyObservers();
  }
  
  // GETTERS
  public String getProfile() {
    return currentProfile;
  }
  public String getLanguage() {
    return currentLanguage;
  }
  
  public static String getStaticLanguage() {
    return currentLanguage;
  }
  
  static {
    System.out.println("static init of ProfileAndLanguageChanger Class");
    System.out.println("currentProfile: "+currentProfile);
    System.out.println("currentLanguage: "+currentLanguage);
  }
  
  public static String getStaticProfile() {
    return currentProfile;
  }
  
}
