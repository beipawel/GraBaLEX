package org.homelinux.kapa.client;

import java.util.HashMap;

/**
 * Stores the access status for each profile.
 * @author Pawel Müller
 *
 */
public class AccessStatus extends HashMap<String, String> {
  private static final long serialVersionUID = -9009210580292602748L;

  /**
   * Constructs an empty PresentationStatus object
   */
  public AccessStatus() {}
  
  /**
   * Constructs a PresentationStatus object with the passed arguments
   * 
   */
  public AccessStatus(String profile, String status) {
    this.setStatus(profile, status);
  }
  
  public void setStatus(String profile, String status) {
    this.put(profile, status);
  }

  /**
   * Returns the access status for a given profile.
   * @param   profile
   * @return  returns an empty String if there was no entry in this for profile.
   */
  public String getStatus(String profile) {
    if ( this.containsKey(profile) ) {
      String p = this.get(profile);
      return this.get(profile);
    }
    return "";
  }
  
}
