package org.homelinux.kapa.client;

import java.io.Serializable;

public class BehaviorType implements Serializable {
  /**
   * this is a superclass of the specific behavioral tape classes
   * if needed, we can implement some methods here later
   */
  
  private static final long serialVersionUID = -6980806854510114130L;
  private String            name             = "";
  
  public BehaviorType () {
    this.name = "none";
  }

  public String getName() {
    return this.name;
  }
  
  protected void setName(String n) {
    this.name = n;
  }
}
