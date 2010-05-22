package org.homelinux.kapa.server;

import java.util.ArrayList;

public class MolekularPropertiesModel {
  /**
   * This is the Model which holds the info needed to define molekular Property objects
   * In the Future this class could be extended, so that the info is stored in a Database,
   * but for now it will be stored here.
   * The most important method would be the get() method which signature shouldn't change much
   */
  
  private ArrayList<MolekularPropertyInfo> propertyInfos = new ArrayList<MolekularPropertyInfo>();
  
  public  MolekularPropertiesModel() {
    /**
     * Once a Model is initialized, we should have a List of infos prepared
     */
    // TODO: loading the info List
    this.writePropertyInfos();
  }
  
  public ArrayList<MolekularPropertyInfo> get() {
    return this.propertyInfos;
  }
  
  private void writePropertyInfos () {
    /**
     * Here we'll define the molekular properties
     */
    // only a few for the beginning
    
    // has-synonym:
    // ?a :has-synonym ?b :is-sense-of ?x :has-form ?y :has-form-description ?c :has-orthographic-form ?d
    ArrayList<String>     hasSynonymPath = new ArrayList<String>();
    hasSynonymPath.add("medic:has-synonym");
    hasSynonymPath.add("medic:is-sense-of");
    hasSynonymPath.add("medic:has-form");
    hasSynonymPath.add("medic:has-form-description");
    hasSynonymPath.add("medic:has-orthographic-form");
    MolekularPropertyInfo hasSynonym = new MolekularPropertyInfo(0, 0, 0, hasSynonymPath);
    this.propertyInfos.add(hasSynonym);
    
    // has-
  }
  
}
