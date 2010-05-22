package org.homelinux.kapa.server;

import java.util.HashMap;

import org.homelinux.kapa.client.PresentationStatus;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class PresentationStatusDB extends HashMap<String, PresentationStatus>{
  private static final long serialVersionUID = -6501377215734531237L;

  public PresentationStatus getStatus(String uri) {
    if ( this.containsKey(uri) ) {
      return this.get(uri);
    }
    return null;
  }
  
  public void addStatus(String uri, String profile, String status) {
    PresentationStatus ps = new PresentationStatus();
    if ( this.containsKey(uri) ) {
      ps = this.get(uri);
    }
    ps.setStatus(profile, status);
    this.put(uri, ps);
  }
  
  /**
   * Extracts all Presentation Status from a Sesame Repository.
   * <p>
   * For now this only builds a DB for Properties
   * @param connection
   * @param modelNS
   */
  public void buildDB(RepositoryConnection connection, String modelNS) {
    // the following query is a little bit complicated due to some problems with OWLIM or Sesame. I could not
    // retrieve all Properties using "?property rdf:type rdf:Property" because of that unknown problem.
    String q =    "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                  "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
                  "PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" +
                  "PREFIX medic:<"+modelNS+">\n" +
                  "\n" +
                  "select distinct ?property ?status ?profile\n" +
                  "where {\n" +
                  "  {\n" +
                  "    ?property rdf:type rdf:Property.\n" +
                  "  }\n" +
                  "  UNION {\n" +
                  "    ?property rdf:type owl:ObjectProperty.\n" +
                  "  }\n" +
                  "  UNION {\n" +
                  "    ?property rdf:type owl:DatatypeProperty.\n" +
                  "  }\n" +
                  "  ?profile rdfs:subPropertyOf medic:has-presentation-status.\n" +
                  "  ?property ?profile ?status.\n" +
                  "  FILTER ( regex (str(?property),\"medic\") )\n" +
                  "  FILTER ( ?profile != medic:has-presentation-status)\n" +
                  "}";
    try {
      System.err.println("PresentationStatusDB#buildDB(): QUERY: \n"+q);
      TupleQueryResult res = connection.prepareTupleQuery(QueryLanguage.SPARQL, q).evaluate();
      
      while ( res != null && res.hasNext() ) {
        BindingSet row = res.next();
        String status = row.getValue("status").stringValue();
        String uri = row.getValue("property").toString();
        String profile = row.getValue("profile").stringValue().replaceAll(".*/([^\\.]*).*$", "$1"); // this should be solved in a more elegant way
        System.out.println("Adding Statuses: URI: "+uri+"; Profile: "+profile+"; Status: "+status);
        if ( uri.matches(".*has-phonetic-form.*") ) {
          System.err.println("dealing with has-phonetic-form");
        }
        this.addStatus(uri, profile, status);
      }
    } catch (QueryEvaluationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (RepositoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (MalformedQueryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
