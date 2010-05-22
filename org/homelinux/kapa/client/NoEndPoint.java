package org.homelinux.kapa.client;

public class NoEndPoint extends BehaviorType {

  private static final long serialVersionUID = -1711176391177308329L;
  private Boolean           roleFlag         = false;

  public NoEndPoint () {
    this.setName("NoEndPoint");
  }
  
  public void setRoleFlag (Boolean b) {
    this.roleFlag = b;
  }
  
  public Boolean isRoleProperty () {
    return this.roleFlag;
  }
  
}
