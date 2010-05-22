package org.homelinux.kapa.client;

import java.io.Serializable;

/**
 * This class represents something like a tuple, but without an order.
 * It binds two Strings together. The intention is, that we often have a short name, e.g. "en"
 * but we also want to store a long version of that name, e.g. "English".
 * @author Pawel Müller
 *
 */
public class SimpleLabel implements Serializable, Comparable<SimpleLabel> {

  private static final long       serialVersionUID = -4649226613008615008L;
  private String                  name;    // name can be a short form of label. e.g. prodl2
  private String                  label;   // label is a nice version of the name e.g. .... TODO
  
  
  /**
   * Constructs an empty SimpleLabel object.
   */
  public SimpleLabel() {
    
  }
  
  /**
   * Constructs an SimpleLabel object and also set's name and label.
   * @param name
   * @param label
   */
  public SimpleLabel(String name, String label) {
    this.setName(name);
    this.setLabel(label);
  }
  
  // SETTERS
  public void setName(String name) {
    this.name = name;
  }
  public void setLabel(String label) {
    this.label = label;
  }
  
  // GETTERS
  public String getName() {
    return this.name;
  }
  public String getLabel() {
    return this.label;
  }
  
  public String toString() {
    return "("+this.getName()+","+this.getLabel()+")";
  }

  public int compareTo(SimpleLabel o) {
    return this.getLabel().compareToIgnoreCase(o.getLabel());
  }
  
}
