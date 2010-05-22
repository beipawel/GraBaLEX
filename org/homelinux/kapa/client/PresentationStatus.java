package org.homelinux.kapa.client;

import java.util.HashMap;

/**
 * Stores the presentation status for each profile.
 * @author Pawel Müller
 *
 */
public class PresentationStatus extends HashMap<String, String> {
  private static final long serialVersionUID = 7653246416605895819L;

  /**
   * Constructs an empty PresentationStatus object
   */
  public PresentationStatus() {
    
  }
  
  /**
   * Constructs a PresentationStatus object with the passed arguments
   * 
   */
  public PresentationStatus(String profile, String status) {
    this.setStatus(profile, status);
  }
  
  public void setStatus(String profile, String status) {
    this.put(profile, status);
  }

  public String getStatus(String profile) {
    if ( this.containsKey(profile) ) {
      return this.get(profile);
    }
    return "";
  }
}
