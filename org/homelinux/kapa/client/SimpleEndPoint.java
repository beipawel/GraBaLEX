package org.homelinux.kapa.client;

public class SimpleEndPoint extends EndPoint {

  /**
   * This is a BehavioralType subclass. Properties which has an instance of this attached to it 
   * are EndPoint properties with an simple Datatype in its Range, like string, int, boolean, ...
   */
  private static final long serialVersionUID = -6040976508452225415L;

  public SimpleEndPoint() {
    this.setName("SimpleEndPoint");
  }

}
