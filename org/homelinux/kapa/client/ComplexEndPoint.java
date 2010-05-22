package org.homelinux.kapa.client;

public class ComplexEndPoint extends EndPoint {

  /**
   * This is a BehavioralType subclass. Properties which has an instance of this attached to it 
   * are EndPoint properties with a OWL Class in its Range
   */
  private static final long         serialVersionUID    = 3306436112852091396L;
  private Boolean                   isRDFType           = false;
  
  public ComplexEndPoint() {
    this.setName("ComplexEndPoint");
  }

  // SETTERS
  public void setRDFType(Boolean tf) {
    this.isRDFType = tf;
  }
  
  // GETTERS
  public Boolean isRDFType() {
    return this.isRDFType;
  }
  
}
