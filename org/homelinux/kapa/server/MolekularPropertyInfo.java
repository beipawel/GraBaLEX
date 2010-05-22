package org.homelinux.kapa.server;

import java.util.ArrayList;

public class MolekularPropertyInfo {
  /**
   * This class is a model for a molekular property info
   * It holds the information needed to construct a Property object
   * 
   * As the label, domain and range donators are already in the path ArrayList, there's
   * no need, to specify them as a String. Instead I'll just keep the index of their
   * occurence in that ArrayList.
   */
  
  private int                 labelDonatorIndex;
  private int                 domainDonatorIndex;
  private int                 rangeDonatorIndex;
  private ArrayList<String>   path = new ArrayList<String>();
  
  public MolekularPropertyInfo () {
    
  }
  
  public MolekularPropertyInfo (int l, int d, int r, ArrayList<String> p ) {
    /** constructor with parameters **/
    
  }
  
  // SETTERS
  public void setLabelDonator(int l_index) {
    this.labelDonatorIndex = l_index;
  }
  
  public void setDomainDonator(int d_index) {
    this.domainDonatorIndex = d_index;
  }
  
  public void setRangeDonator(int r_index) {
    this.rangeDonatorIndex = r_index;
  }
  
  public void setPath(ArrayList<String> p) {
    this.path = p;
  }
  
  // GETTERS
  public int getLabelDonatorIndex() {
    return this.labelDonatorIndex;
  }
  public int getDomainDonatorIndex() {
    return this.domainDonatorIndex;
  }
  public int getRangeDonatorIndex() {
    return this.rangeDonatorIndex;
  }
  public ArrayList<String> getPath() {
    return this.path;
  }
  public String getLabelDonator() {
    return path.get(this.labelDonatorIndex);
  }
  public String getDomainDonator() {
    return path.get(this.domainDonatorIndex);
  }
  public String getRangeDonator() {
    return path.get(this.rangeDonatorIndex);
  }
}
