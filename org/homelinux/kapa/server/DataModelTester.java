package org.homelinux.kapa.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import org.homelinux.kapa.client.ProfileDependentLabel;
import org.homelinux.kapa.client.Properties;
import org.homelinux.kapa.client.Property;

public class DataModelTester {
  public static void main(String[] args) {
    Date t1 = new Date(); // for measuring the time needed to instantiate the model
    DataModel dm = new DataModel("/tmp/medic_model.owl", "091204_data", "http://localhost:8080/sesame_2.3.0/");
    Properties props = dm.getProperties();
    Date t2 = new Date(); // for measuring the time needed to instantiate the model
    
    System.out.println("Size of Properties: "+props.size());
    System.out.println("property 40: "+props.get(40).getLabels().toString());
    
    DataModelTester dmt = new DataModelTester();
    dmt.writeRootPropertiesToFile(props);
    dmt.writeAllPropertiesToFile(props);
    dmt.writeSomeStatsAboutProperties(props,t1,t2);
    dmt.writeProfileDependentLabelsToFile(dm);
    dmt.writeProfileIndependentLabelsToFile(dm);
    
    System.out.println("we're done.");
  }
  
  /**
   * writes all profile dependent labels to a file
   * @param dm
   */
  private void writeProfileDependentLabelsToFile (DataModel dm) {
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter("/tmp/profileDependentLabels.log"));
      System.out.println("dm.getProfileDependentLabelDB().size(): "+dm.getProfileDependentLabelDB().size());
      for (Iterator<Entry<String,ProfileDependentLabel>> iterator = dm.getProfileDependentLabelDB().entrySet().iterator(); iterator.hasNext();) {
        Entry<String,ProfileDependentLabel> entry = iterator.next();
        out.write(entry.getKey()+" "+entry.getValue().toString()+"\n");
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.err.println("/tmp/profileDependentLabels.log");
      e.printStackTrace();
    }
    finally {
      try {
        out.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  /**
   * writes all profile independent labels to a file
   * @param dm
   */
  private void writeProfileIndependentLabelsToFile (DataModel dm) {
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter("/tmp/profileIndependentLabels.log"));
      System.out.println("dm.getProfileIndependentLabelDB().size(): "+dm.getProfileIndependentLabelDB().size());
      for (Iterator<Entry<String,String>> iterator = dm.getProfileIndependentLabelDB().entrySet().iterator(); iterator.hasNext();) {
        Entry<String,String> entry = iterator.next();
        String label = entry.getValue().replaceAll("\\s", "_");
        out.write(entry.getKey()+" "+label+"\n");
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.err.println("/tmp/profileIndependentLabels.log");
      e.printStackTrace();
    }
    finally {
      try {
        out.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  private void writeRootPropertiesToFile(Properties props) {
    BufferedWriter out = null;
    /**
     * Write the Properties which will be displayed in the Root Specification Object
     * (That means the will be filtered with .filter())
     */
    try {
      out = new BufferedWriter(new FileWriter("/tmp/rootProperties.log"));
      for (Iterator<Property> iterator = props.filter().iterator(); iterator.hasNext();) {
        Property property = iterator.next();
        out.write(property.getName()+" "+property.getBehaviorType().getName()+"\n");
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.err.println("/tmp/rootProperties.log");
      e.printStackTrace();
    }
    finally {
      try {
        out.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  private void writeAllPropertiesToFile(Properties props) {
    BufferedWriter out = null;
    /**
     * Writes all Properties (each per line) with some informations about it to a file 
     */
    try {
      out = new BufferedWriter(new FileWriter("/tmp/allProperties.log"));
      for (Iterator<Property> iterator = props.iterator(); iterator.hasNext();) {
        Property property = iterator.next();
        out.write(property.getName()+" "+property.getLabels().toString()+" "+property.getBehaviorType().getName()+"\n");
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.err.println("/tmp/allProperties.log");
      e.printStackTrace();
    }
    finally {
      try {
        out.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  private String formatElapsedTime(Long t) {
    long m, s, ms;
    long t_in_sec = t / 1000;
    m = t_in_sec / 60;
    s = t_in_sec - (60*m);
    ms = t - m*60*1000 - s*1000;
    return m+"m "+s+"s "+ms+"ms";
  }
  
  private void writeSomeStatsAboutProperties(Properties props, Date t1, Date t2) {
    /**
     * We write some Infos About the Properties in the model to a file
     */
    BufferedWriter out = null;
    
    /**
     * Time to instantiate the DataModel 
     */
    Long l1 = t1.getTime();
    Long l2 = t2.getTime();
    Long elapsed_time = l2-l1;
    
    
    Integer r_sum = 0;
    Integer r_cnt = 0;
    Integer r_max = 0;
    Integer r_min = 0;
    Integer d_sum = 0;
    Integer d_cnt = 0;
    Integer d_max = 0;
    Integer d_min = 0;
    ArrayList<Property> none_range_props = new ArrayList<Property>();
    ArrayList<Property> props_with_empty_domain = new ArrayList<Property>();
    Property prop_with_large_domain = new Property();
    Integer props_with_large_domain_cnt = 0;
    Integer props_with_empty_domain_cnt = 0;
    
    
    for (Iterator<Property> iterator = props.iterator(); iterator.hasNext();) {
      Property prop = iterator.next();
//      System.out.println("Property Name: "+prop.getName()+"\n" +
//          "Domain Size: "+prop.getDomain().size()+"\n" +
//          "Range Size: "+prop.getRange().size()+"\n" +
//          "Domain: "+prop.getDomain().toString());
      r_sum += prop.getRange().size();
      d_sum += prop.getDomain().size();
      r_cnt++;
      d_cnt++;
      if ( r_max <= prop.getRange().size() ) {
        r_max = prop.getRange().size();
      }
      if ( r_min >= prop.getRange().size() ) {
        r_min = prop.getRange().size();
      }
      if ( d_max <= prop.getDomain().size() ) {
        d_max = prop.getDomain().size();
      }
      if ( d_min >= prop.getDomain().size() ) {
        d_min = prop.getDomain().size();
      }
      if ( prop.getRange().size() == 0 ) {
        none_range_props.add(prop);
      }
      if ( prop.getDomain().size() > 1000 ) {
        prop_with_large_domain = prop;
        props_with_large_domain_cnt++;
      }
      if ( prop.getDomain().size() == 0 ) {
        props_with_empty_domain.add(prop);
        props_with_empty_domain_cnt++;
      }
    }
    
    // durchschnittswerte für die Range und Domain Länge
    float r_mean = r_sum / r_cnt;
    float d_mean = d_sum / d_cnt;
    
    
    try {
      out = new BufferedWriter(new FileWriter("/tmp/DataModelTesterOutput.log"));
//      out.write("BEGIN\n");
      out.write("Zeit für die Inizierung von DataModel: "+formatElapsedTime(elapsed_time));
      out.write("\nAnzahl aller Properties: "+props.size());
      out.write("\nDomain durchschnittslänge: "+d_mean);
      out.write("\nDomain Max: "+d_max);
      out.write("\nDomain Min: "+d_min);
      out.write("\nRange durchschnittslänge: "+r_mean);
      out.write("\nRange Max: "+r_max);
      out.write("\nRange Min: "+r_min);
      out.write("\nProperties ohne Range ("+none_range_props.size()+"): \n"+none_range_props.toString());
      out.write("\nProperties ohne Domain ("+props_with_empty_domain_cnt+"): \n"+props_with_empty_domain.toString());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.err.println("/tmp/DataModelTesterOutput.log");
      e.printStackTrace();
    }
    finally {
      try {
        out.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
}
