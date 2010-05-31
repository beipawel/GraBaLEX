package org.homelinux.kapa.client;

import java.io.Serializable;

public class OWLInstanceItem implements Serializable, Comparable<OWLInstanceItem> {
  private static final long           serialVersionUID      = -6520273054375196745L;
  private String                      label                   = "";
  private String                      uri                     = "";
  private ProfileDependentLabel       profileDependentLabel   = new ProfileDependentLabel();
  private AccessStatus                accessStatus;
  
  public OWLInstanceItem() {
    
  }
  public OWLInstanceItem(String label, String uri) {
    this.setLabel(label);
    this.setURI(uri);
  }
  
  /**
   * Constructs a OWLInstanceItem with profile dependent label
   * @param label
   * @param uri
   */
  public OWLInstanceItem(ProfileDependentLabel label, String uri) {
    this.setLabel(label);
    this.setURI(uri);
  }

  /**
   * Returns the Label for this OWLInstanceItem for the current profile.
   * @return
   */
  private String getProfileDependentLabelValue () {
    if ( this.profileDependentLabel != null ) {
      return this.profileDependentLabel.getLabel(ProfileAndLanguageChanger.getStaticProfile(), ProfileAndLanguageChanger.getStaticLanguage());
    }
    return "";
  }

  private String getProfileIndependentLabelValue () {
    if ( this.label != null ) {
      return this.label;
    }
    return "";
  }
  
  // SETTERS
  public void setLabel(String l) {
    this.label = l;
  }
  
  /**
   * Sets the label.
   * If <code>null</code> is passed no changes will be done, and hence the default instanciated, empty ProfileDependentLabel will be used further.
   * 
   * @param label
   */
  public void setLabel(ProfileDependentLabel label) {
    if ( label != null ) {
      this.profileDependentLabel = label;
    }
  }
  
  public void setURI(String u) {
    this.uri = u;
  }
  
  public void setAccessStatus(AccessStatus access_status) {
    this.accessStatus = access_status;
  }
  
  // GETTERS
  /**
   * Return a Label.
   * @return   If the label was <code>null</code> or empty a part of the URI will be returned
   */
  public String getLabel() {
    String label = "";
    if ( this.getProfileIndependentLabelValue().length() != 0 ) {
      label = this.getProfileIndependentLabelValue();
    }
    else if ( this.getProfileDependentLabelValue().length() != 0 ) {
      label = this.getProfileDependentLabelValue();
    }
    if ( label.length() > 0 ) {
      return label;
    }
    return this.getURI().replaceAll(".*#(.*)", "$1")+" (uri)";
  }
  
  public String getURI() {
    return this.uri;
  }
  
  public String toString() {
    return "OWLInstanceItem <"+this.getURI()+">";
  }
 
  /**
   * Checks if this should be displayed in the current profile.
   */
  public Boolean isVisible() {
    // if the AccessStatus for the current item in the current profile is set to ignore, we'll return false
    if ( this.getAccessStatus() != null && this.getAccessStatus(ProfileAndLanguageChanger.getStaticProfile()).contentEquals("ignore") ) {
      return false;
    }
    return true;
  }
  
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
  
  public int compareTo(OWLInstanceItem owlItem) {
    return this.getLabel().compareToIgnoreCase(owlItem.getLabel());
  }
  
}
