package org.homelinux.kapa.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * This class wraps up Property objects and give us some methods like sort, filter,...
 * so that we can do something useful very easy with them
 * 
 */
public class Properties extends ArrayList<Property> implements Serializable {
  
  
  private static final long             serialVersionUID = -3626428152581623437L;
  private ArrayList<String>             defaultDomain = new ArrayList<String>();
  
  public Properties () {
    /* constructor */
  }
  
  public ArrayList<Property> filter(ArrayList<String> range) {
    Date t1 = new Date();
    ArrayList<Property> f = new ArrayList<Property>();
    for (Iterator<Property> propsIterator = this.iterator(); propsIterator.hasNext();) {
      Property property = propsIterator.next();
      if ( ! property.isVisible() ) {
        continue;
      }
      for (Iterator<String> rangeIterator = range.iterator(); rangeIterator.hasNext();) {
        String r = rangeIterator.next();
        if ( property.hasInDomain(r) ) {
          if ( ! f.contains(property) ) {
            f.add(property);
          }
          break;
        }
      }
    }
    Collections.sort(f); // we sort the list here
    Date t2 = new Date();
    System.out.println("Properties#filter(range): Time needed: "+Timer.getTimeDiff(t1, t2));
    return f;
  }
  
  public ArrayList<Property> filter(Property root_prop) {
    return this.filter(root_prop.getRange());
  }
  
  /**
   * Returns a Set of Property objects filtered on {@link #defaultDomain}
   * @return
   */
  public ArrayList<Property> filter() {
    return this.filter(this.defaultDomain);
  }
  
  public ArrayList<Property> filter(int root_prop_index) {
    if ( root_prop_index < 0 ) { // this means, we have to filter on default domain
      return this.filter(this.defaultDomain);
    }
    return this.filter(this.get(root_prop_index));
  }

  public void setDefaultDomain(ArrayList<String> defDom) {
    this.defaultDomain = defDom;
  }
  
  public ArrayList<String> getDefaultDomain() {
    return this.defaultDomain;
  }
  
}
