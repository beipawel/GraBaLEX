package org.homelinux.kapa.client;

import java.util.Iterator;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchResultPanel extends VerticalPanel {
  
  public SearchResultPanel() {
    super();
    this.setStylePrimaryName("search-result-panel");
  }

  public void diplayResult(LexItems items) {
    if ( ! this.isAttached() ) {
      return;
    }
    this.clear();
    
    // displaying the query used to produce the results
    if ( items != null ) {
      String queryUsed = items.getQuery().replaceAll("\\s*PREFIX.*\n", "").replaceAll("<http[^>]*medic\\.owl#([^>]+)>","medic:$1").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>");
      HTML query = new HTML("Benutzte Query für dieses Ergebnis:<br>\n"+queryUsed);
      query.setStyleName("search-result-query");
      DisclosurePanel queryPanel = new DisclosurePanel("SPARQL Query");
      queryPanel.setAnimationEnabled(true);
      queryPanel.ensureDebugId("query-panel");
      queryPanel.setContent(query);
      this.add(queryPanel);
    }
    
    if ( items != null && items.size() > 0 ) {
      String s = "";
      if ( items.size() > 1 ) {
        s = "Es wurden "+items.size()+" Einträge gefunden.";
      }
      else {
        s = "Es wurde ein Eintrag gefunden. ";
      }
      
      HTML summary = new HTML();
      summary.setStyleName("search-result-summary");
      summary.setHTML(s);
      this.add(summary);
      
      for (Iterator<LexItem> itemsIterator = items.iterator(); itemsIterator.hasNext();) {
        LexItem lexItem = (LexItem) itemsIterator.next();
        Anchor a = new Anchor(lexItem.getLabel(), "/GraBaLEX/displayLexiconItem?item="+URL.encodeComponent(lexItem.getName()));
        a.setStyleName("lex-item-link");
        a.setTarget("Lexikoneintrag");
        this.add(a);
      }
    }
    else {
      // in this case we have no results
      String s = "Keine Lexikoneinträge gefunden.";
      
      HTML summary = new HTML();
      summary.setStyleName("search-result-summary");
      summary.setHTML(s);
      this.add(summary);
    }
  }
  
}
