package org.homelinux.kapa.server;

import java.util.HashMap;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Delivers and stores all profile independent labels
 * 
 * @author Pawel Müller
 * 
 */
public class ProfileIndependentLabelDB extends HashMap<String, String> {
  private static final long serialVersionUID = -7833775262175931175L;

  /**
   * No-ARG Constructor
   */
  public ProfileIndependentLabelDB () {
    
  }
  
  /**
   * Adds a URI and its label to the DB.
   *  
   * @param uri
   * @param label
   */
  public void addLabel(String uri, String label) {
    this.put(uri, label);
  }
  
  /**
   * Returns the label for a given URI.
   * 
   * @param uri the URI for which you want to get the label
   * @return If the URI is not registered in the DB the return value is an empty String
   */
  public String getLabel(String uri) {
    if ( this.containsKey(uri) ) {
      return this.get(uri);
    }
    return "";
  }
  
  /**
   * Extracts all profile independent labels out of a Sesame repository.
   * @param connection
   * @param modelNS     default namespace of the OWL Model
   */
  public void buildDB (RepositoryConnection connection,String modelNS) {
    String q = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
    "select distinct ?uri ?label\n" +
    "where {\n" +
    "  ?uri rdf:type <"+modelNS+"LexicalEntity>.\n" +
    "  ?uri <"+modelNS+"has-label> ?label.\n" +
    "}";
    try {
      System.err.println("QUERY: \n"+q);
      TupleQueryResult res = connection.prepareTupleQuery(QueryLanguage.SPARQL, q).evaluate();
      
      while ( res != null && res.hasNext() ) {
        BindingSet row = res.next();
        String label = row.getValue("label").stringValue();
        String uri = row.getValue("uri").toString();
        
        this.put(uri, label);
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
