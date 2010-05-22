package org.homelinux.kapa.server;

import java.util.ArrayList;

import org.homelinux.kapa.client.ControllerService;
import org.homelinux.kapa.client.LexItems;
import org.homelinux.kapa.client.OWLInstanceItem;
import org.homelinux.kapa.client.Properties;
import org.homelinux.kapa.client.Property;
import org.homelinux.kapa.client.SimpleLabel;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ControllerServiceImpl extends RemoteServiceServlet implements ControllerService {
  private static final long serialVersionUID = 4309651920389143042L;
  
  public Properties getProperties() {
    // not nice, as we should keep the model in the memory, but ok for now
    DataModel dm = (DataModel) getServletContext().getAttribute("dataModel");
    System.out.println("dm ok");
    Properties props = dm.getProperties();
    System.out.println("returning #"+props.size()+" properties to the client.");
//    System.out.println(dm.getProperties().get(0).toString());
    return props;
  }
  

  public LexItems getLexikonItems(String query) {
    System.err.println("entering Controllers getLexikonItems() method");
    DataModel dm = (DataModel) getServletContext().getAttribute("dataModel");
    System.err.println("got the model");
    LexItems lis = dm.getLexiconItems(query);
    System.out.println("returning #"+lis.size()+" LexItems to the client.");
    return lis;
  }
  
  public ArrayList<SimpleLabel> getModelLanguages() {
    DataModel dm = (DataModel) getServletContext().getAttribute("dataModel");
    ArrayList<SimpleLabel> model_languages = dm.getModelLanguages();
    System.out.println("returning following model languages to the client: "+model_languages.toString());
    return model_languages;
  }
  
  public ArrayList<SimpleLabel> getModelProfiles() {
    DataModel dm = (DataModel) getServletContext().getAttribute("dataModel");
    ArrayList<SimpleLabel> model_profiles = dm.getModelProfiles();
    System.out.println("returning following model profiles to the client: "+model_profiles.toString());
    return model_profiles;
  }


  public ArrayList<OWLInstanceItem> getOWLInstanceItems(Property property) {
    DataModel dm = (DataModel) getServletContext().getAttribute("dataModel");
    ArrayList<OWLInstanceItem> items = dm.getOWLInstanceItems(property);
    System.out.println("returning #"+items.size()+" OWLInstanceItems to the client");
    return items;
  }
}
