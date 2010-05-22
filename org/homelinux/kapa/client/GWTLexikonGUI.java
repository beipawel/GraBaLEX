package org.homelinux.kapa.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTLexikonGUI implements EntryPoint, Observer {
  
  private SearchResultPanel             searchResultsPanel      = new SearchResultPanel();
  private ProfileAndLanguageChanger     profLangChanger         = new ProfileAndLanguageChanger();
  private ControllerServiceAsync        controllerSrvs          = GWT.create(ControllerService.class);
  private Properties                    props                   = null;
  private static Label                  loadStatus              = new Label("loading...");
  private static Label                  searchStatus            = new Label("searching...");
  private SearchControllPanel           controllPanel           = new SearchControllPanel();
  private SearchMask                    searchMask              = new SearchMask(this);
  private Date                          t1;
  private Date                          t2;
  
  private static int                    tabindex                = 0;
  private static Properties             globalProperties        = null;
  
  public GWTLexikonGUI () {}
  
  public void onModuleLoad() {
    loadStatus.setStylePrimaryName("status-info");
    searchStatus.setStylePrimaryName("search-info");
    searchStatus.setVisible(false); // set to invisible at startup
    RootPanel.get("top-bar").add(loadStatus);
    RootPanel.get("top-bar").add(searchStatus);
    RootPanel.get("top-bar").add(profLangChanger);
    RootPanel.get("search-box-wrapper").add(searchMask);
    RootPanel.get("searchResults").add(searchResultsPanel);
    RootPanel.get("search-controlls").add(controllPanel);
//    RootPanel.get("info-bar").add(testButton);
    this.controllPanel.registerObserver(this);
    this.showSearchMask();
  }
  
  /**
   * Finds whether to display the simple or the extended search.
   */
  private void showSearchMask() {
    // in this method we could do something like reading a Cookie from the user which keeps information about if the
    // user likes the simple or the extended search.
    // TODO: implement choosing the right search mask
    // now we'll start with simply starting with the simple search
    this.showSimpleSearch();
//    this.prepareExtendedSearch(); // preparing extended search
  }
  
  private void loadStatusOn(Boolean tf) {
    if ( tf ) {
      loadStatus.setVisible(true);
    }
    else {
      loadStatus.setVisible(false);
    }
  }
  
  private void searchStatusOn(Boolean tf) {
    if ( tf ) {
      searchStatus.setVisible(true);
    }
    else {
      searchStatus.setVisible(false);
    }
  }
  
  private void showSimpleSearch() {
    this.loadStatusOn(true);
    this.searchMask.simpleSearch(true);
    this.controllPanel.setToSimpleControlls();
    this.loadStatusOn(false);
  }
  
  private void showExtendedSearch() {
    this.loadStatusOn(true);
    if ( this.props == null ) {
      System.out.println("Starting timer...");
      this.t1 = new Date();
      // we have to get the properties first
      this.prepareExtendedSearch();
    }
    else {
      this.t2 = new Date();
//      Window.alert("GWTLexikonGUI#showExtendedSearch: Time needed: "+Timer.getTimeDiff(this.t1, this.t2));
      System.out.println("GWTLexikonGUI#showExtendedSearch: Time needed: "+Timer.getTimeDiff(this.t1, this.t2));
      System.out.println(".... Timer stopped.");
      // we have the properties, so we can go on
      Specifications.setProfileAndLanguageChanger(this.profLangChanger);
      this.searchMask.setProperties(this.props);
      this.searchMask.extendedSearch(true);
      this.controllPanel.setToExtendedControlls();
      this.loadStatusOn(false);
    }
    
  }
  
  private void prepareExtendedSearch() {
    // first we need the properties list, 'though kind of status bar while loading it wouldn't be that bad
    // Set up the callback object.
    AsyncCallback<Properties> callback = new AsyncCallback<Properties>() {
      public void onFailure(Throwable caught) {
        Window.alert("Problems  with RPC while getting the Properties, we could do something fancy \n" +
                     "and call a method of GWTLexikonGUI which deals with that problem.\n" +
                     "Maybe an analysis, what went wrong?");
      }
      public void onSuccess(Properties result) {
        props = result;
        globalProperties = props;
        showExtendedSearch();
      }
    };
    controllerSrvs.getProperties(callback); // making RPC
  }
  
  private void resetSearchMask() {
    this.searchMask.reset();
    this.searchResultsPanel.clear();
//    if ( this.mainPanel.getWidgetCount() > 0 ) {
//      if (this.searchMask instanceof Specifications) {
//        this.showExtendedSearch();
//      }
//      else if ( this.searchMask instanceof SimpleSearch ) {
//        this.showSimpleSearch();
//      }
//    }
  }
  
  private void performSearch() {
    this.searchStatusOn(true);
    AsyncCallback<LexItems> getLexItemsCallback = new AsyncCallback<LexItems>() {
      public void onFailure(Throwable caught) {
        Window.alert("Problems with RPC while getting the LexItems. We could do something fancy \n" +
                     "and call a method of GWTLexikonGUI which deals with that problem.\n" +
                     "Maybe an analysis, what went wrong?");
      }
      public void onSuccess(LexItems result) {
        // setted the props, we can go on
        searchResultsPanel.diplayResult(result);
        searchStatusOn(false);
      }
    };
    String queryString = this.searchMask.toSPARQLQuery();
    if ( queryString.length() == 0 ) {
      this.searchResultsPanel.diplayResult(null);
      searchStatusOn(false);
    }
    else {
      controllerSrvs.getLexikonItems(queryString, getLexItemsCallback);
    }
  }
  
  public void update(Object observerable) {
    if (observerable instanceof SearchControllPanel) {
      if ( this.controllPanel.wantsExtendedSearch() ) {
        this.showExtendedSearch();
      }
      else if ( this.controllPanel.wantsSimpleSearch() ) {
        this.showSimpleSearch();
      }
      else if ( this.controllPanel.wasReset() ) {
        this.resetSearchMask();
      }
      else if ( this.controllPanel.wasSearch() ) {
        this.performSearch();
      }
    }
    else if ( observerable instanceof SimpleSearch ) {
      this.performSearch();  // if SimpleSearch updates _this_ we know we have to search
    }
  }

  // static methods
  /**
   * Returns the last used tab index in the document
   */
  public static int getLastTabIndex() {
    return tabindex;
  }
  
  /**
   * Increases the global tabindex by 1 and returns the new value.
   * @return
   */
  public static int getNextTabIndex() {
    return ++tabindex;
  }
  
  public static Properties getGlobalProperties() {
    return globalProperties;
  }
  
}
