package org.homelinux.kapa.client;

import java.util.HashMap;

/**
 * Stores a profile and language dependent label.
 * 
 * @author Pawel Müller
 *
 */
public class ProfileDependentLabel extends HashMap<String, HashMap<String,String>> {
  private static final long serialVersionUID = 6381416355539598011L;

  /**
   * No-ARG Constructor
   */
  public ProfileDependentLabel () {
    
  }
  
  /**
   * Constructs a Label with passed arguments.
   * 
   * @param profile
   * @param lang
   * @param label
   */
  public ProfileDependentLabel (String profile, String lang, String label) {
    this.setLabel(profile, lang, label);
  }
  
  public void setLabel(String profile, String lang, String label) {
    HashMap<String,String> h = new HashMap<String,String>();
    if ( this.containsKey(profile) ) {
      h = this.get(profile);
    }
    h.put(lang, label);
    this.put(profile, h);
  }
  
  /**
   * Returns a Label String for the passed arguments
   * @param profile
   * @param lang
   * @return String is empty, no label was found
   */
  public String getLabel(String profile, String lang) {
    if ( this.isEmpty() ) {
      return "";
    }
    else if ( this.get(profile) == null ) {
      return "";
    }
    else if ( this.get(profile).get(lang) == null ) {
      return "";
    }
    return this.get(profile).get(lang);
  }
  
}
