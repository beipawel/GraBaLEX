package org.homelinux.kapa.client;

import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchMask extends VerticalPanel implements Queryable {

  private Specifications              extendedSearchWidget  = null; // will hold a reference to the root Specifications object
  private SimpleSearch                simpleSearchWidget    = null; // reference to the SimpleSearch object
  private Properties                  props                 = null;
  private GWTLexikonGUI               wrapper               = null;
  
  public SearchMask(GWTLexikonGUI wrapper) {
    super();
    this.wrapper = wrapper;
  }
  
  protected void onAttach() {
    super.onAttach();
    this.simpleSearchWidget = new SimpleSearch();
    this.simpleSearchWidget.registerObserver(this.wrapper);
    this.simpleSearch(true);
    this.add(simpleSearchWidget);
  }
  
  public void setProperties(Properties props) {
    this.props = props;
  }
  
  public void extendedSearch(Boolean extend) {
    if ( extend ) {
      this.extendedSearchWidget = new Specifications(null, this.props);
      this.extendedSearchWidget.registerObserver(this.wrapper);
      this.add(extendedSearchWidget);
      this.simpleSearchWidget.withExtendedSearch(true);
    }
    else {
      if ( this.extendedSearchWidget != null && this.extendedSearchWidget.isAttached() ) {
        this.extendedSearchWidget.removeFromParent();
      }
      this.extendedSearchWidget = null;
      this.simpleSearchWidget.withExtendedSearch(false);
    }
  }
  
  public void simpleSearch(Boolean simple) {
    this.extendedSearch(!simple);
  }
  
  /**
   * Resets the SearchMask object
   */
  public void reset() {
    this.extendedSearchWidget.removeFromParent();
    this.extendedSearch(true); // we only want to reset the extended search inputs
  }
  
  public String toSPARQLQuery(String var) {
    // TODO Auto-generated method stub
    return null;
  }

  public String toSPARQLQuery() {
    String q = this.simpleSearchWidget.toSPARQLQuery();
    if ( this.extendedSearchWidget != null ) {
      q = q + this.extendedSearchWidget.toSPARQLQuery();
    }
    return q;
    
  }

}
