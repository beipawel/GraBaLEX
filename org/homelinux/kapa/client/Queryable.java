package org.homelinux.kapa.client;

public interface Queryable {

  public String toSPARQLQuery(String var);
  public String toSPARQLQuery();
  
}
