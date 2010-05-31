package org.homelinux.kapa.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


/** 
 * This Class is a model of a property.
**/
public class Property implements Serializable, Comparable<Property> {
  
  private static final long                       serialVersionUID = -2967380990140255132L;
  private String                                  name;
  private ArrayList<String>                       path = new ArrayList<String>();
  private ProfileDependentLabel                   label = new ProfileDependentLabel();
  private ArrayList<String>                       domain = new ArrayList<String>();
  private ArrayList<String>                       range = new ArrayList<String>();
  private BehaviorType                            bType = null;
  private AccessStatus                            accessStatus;
  
  /**
   * Public constructor.
   */
  public Property () {}
  
  /**
   * Returns <code>true</code> if the passed <code>uri</code> was found in the Domain of <code>this</code>.
   * @param uri
   * @return
   */
  public Boolean hasInDomain(String uri) {
    for (Iterator<String> iterator = this.domain.iterator(); iterator.hasNext();) {
      if ( iterator.next().contentEquals(uri) ) {
        return true;
      }
    }
    return false;
  }
  
  // SETTERS
  public void setName(String s) {
    this.name = s;
  }
  
  public void setBehavioralType(BehaviorType btype) {
    this.bType = btype;
  }
  
  public void setLabel(String profile,String lang, String label) {
    this.label.setLabel(profile, lang, label);
  }
  
  /**
   * Sets a value for the access status of this.
   * @param status
   */
  public void setAccessStatus(AccessStatus status) {
    this.accessStatus = status;
  }
  
  /**
   * Sets the label.
   * If <code>null</code> is passed no changes will be done, and hence the default instantiated, 
   * empty ProfileDependentLabel will be used.
   * 
   * @param label
   */
  public void setLabel(ProfileDependentLabel label) {
    if ( label != null ) {
      this.label = label;
    }
  }
  
  /**
   * Sets the domain of <code>this</code>.
   * @param domain
   */
  public void setDomain(ArrayList<String> domain) {
    this.domain = domain;
  }
  
  /**
   * Sets the range of <code>this</code>.
   * @param range
   */
  public void setRange(ArrayList<String> range) {
    this.range = range;
  }
  
  public void setPath(ArrayList<String> p) {
    this.path = p;
  }
  
  /**
   * Adds an edge to the path.
   * @param e
   * @deprecated not needed.
   */
  public void addEdge(String e) {
    /** This adds an edge at the end of the path ArrayList **/
    this.path.add(e);
  }
  
  /**
   * Adds a hole path.
   * @param p
   * @deprecated not needed.
   */
  public void addPath(ArrayList<String> p) {
    this.path.addAll(p);
  }
  
  // GETTERS
  public String getLabel(String profile, String lang) {
    String label = this.label.getLabel(profile, lang);
    if ( label == null || label.length() == 0 ) {
      return this.name.replaceAll(".*#[^-]*-(.*)", "$1")+" (uri)";
    }
    return label;
  }
  
  public String getLabel() {
    return this.getLabel(ProfileAndLanguageChanger.getStaticProfile(), ProfileAndLanguageChanger.getStaticLanguage());
  }
  
  /**
   * Return a ProfileDependenLabel Object
   * @return
   */
  public ProfileDependentLabel getLabels() {
    return this.label;
  }
  
  public BehaviorType getBehaviorType() {
    return this.bType;
  }
  public ArrayList<String> getPath() {
    return this.path;
  }
  public String getName() {
    return this.name;
  }
  public ArrayList<String> getDomain() {
    return this.domain;
  }
  public ArrayList<String> getRange() {
    return this.range;
  }
  
  /**
   * get's the currently set label.
   * @return
   * @deprecated not needed
   */
  public String getCurrentLabel() {
    return this.getLabel(ProfileAndLanguageChanger.getStaticProfile(), ProfileAndLanguageChanger.getStaticLanguage());
//    return this.currentLabel;
  }
  
  /**
   * Checks if this should be displayed in the current profile.
   * 
   */
  public Boolean isVisible() {
    // if the AccessStatus for the current item in the current profile is set to ignore, we'll return false
    if ( this.getAccessStatus() != null && this.getAccessStatus(ProfileAndLanguageChanger.getStaticProfile()).contentEquals("ignore") ) {
      return false;
    }
    return true;
  }
  
  /**
   * Gets the AccessStatus object
   */
  public AccessStatus getAccessStatus() {
    return this.accessStatus;
  }
  
  /**
   * Gets the access status String for a profile name.
   * <p>
   * This is a shortcut for {@link Property#getAccessStatus()#getStatus(profile)}
   * @param profile
   */
  public String getAccessStatus(String profile) {
    if ( this.getAccessStatus() != null ) {
      return this.getAccessStatus().getStatus(profile);
    }
    else {
      return "";
    }
    
  }
  
  
  public String toString() {
    String s = "";
    s += "Name: "+this.name;
    s += "\nPath: "+this.path.toString();
    s += "\nDomain: "+this.domain.toString();
    s += "\nRange: "+this.range.toString();
    s += "\nLabels: "+this.label.toString();
    s += "\nBehavioralType: "+this.bType.getName();
    return s;
  }

  public int compareTo(Property prop) {
    /**
     * we sort on the currentLabel if one is set otherwise we use the URI (getName()).
     */
    return this.getLabel().compareToIgnoreCase(prop.getLabel());
  }
}
