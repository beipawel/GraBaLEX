package org.homelinux.kapa.client;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An ArrayList for LexItem objects.
 * It has an additional instance variable which keeps the SPARQL Query with which this list was generated.
 * 
 * @author Pawel Müller
 */

public class LexItems extends ArrayList<LexItem> implements Serializable {

  private static final long       serialVersionUID = -5450666951454152260L;
  private String                  query;                                      // Will keep the query with which this was generated
  
  //SETTERS
  
  /**
   * Sets the query instance variable to the passed value
   * @param query 
   */
  public void setQuery(String query) {
    this.query = query;
  }
  
  /**
   * Returns the query with which this list was generated.
   * @return
   */
  public String getQuery() {
    return this.query;
  }
  
}
