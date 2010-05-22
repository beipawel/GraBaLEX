package org.homelinux.kapa.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("controller")
public interface ControllerService extends RemoteService {
  Properties getProperties();
  LexItems getLexikonItems(String query);
  ArrayList<OWLInstanceItem> getOWLInstanceItems(Property property);
  ArrayList<SimpleLabel> getModelLanguages();
  ArrayList<SimpleLabel> getModelProfiles();
}
