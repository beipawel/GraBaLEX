package org.homelinux.kapa.client;

import java.io.Serializable;

public class LexItem implements Serializable, Comparable<LexItem>{
  
  private static final long                       serialVersionUID  = 6906801341330805419L;
  private String                                  name              = "";
  private String                                  label             = ""; // there's only one label for a LexItem
  
  public LexItem () {
    
  }
  
  // SETTERS
  public void setLabel(String label) {
    this.label = label;
  }
  
  public void setName(String n) {
    this.name = n;
  }
  
  // GETTERS
  public String getLabel() {
    /**
     * If there is no label, we'll return a part of the uri
     */
    if ( this.label == null || this.label.length() == 0 ) {
      return this.getName().replaceAll(".*#(.*)", "$1")+" (uri)";
    }
    return this.label;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String toString() {
    return this.getName();
  }

  public int compareTo(LexItem lexItem) {
    return this.getLabel().compareToIgnoreCase(lexItem.getLabel());
  }
  
}
