package org.homelinux.kapa.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ControllerServiceAsync {
  void getProperties(AsyncCallback<Properties> callback);
  void getLexikonItems(String query, AsyncCallback<LexItems> callback);
  void getOWLInstanceItems(Property property, AsyncCallback<ArrayList<OWLInstanceItem>> callback);
  void getModelLanguages(AsyncCallback<ArrayList<SimpleLabel>> callback);
  void getModelProfiles(AsyncCallback<ArrayList<SimpleLabel>> callback);
}
