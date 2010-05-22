package org.homelinux.kapa.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.homelinux.kapa.client.ProfileDependentLabel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Delivers and stores all profile dependent labels
 * 
 * @author Pawel Müller
 * 
 */
public class ProfileDependentLabelDB extends HashMap<String,ProfileDependentLabel> {
  private static final long     serialVersionUID      = -6031318735141073986L;
  private Set<String>           modelLanguages        = new HashSet<String>();
  private Set<String>           modelProfiles         = new HashSet<String>();

  /**
   * No-ARG Constructor
   */
  public ProfileDependentLabelDB () {
    
  }
  
  /**
   * Adds an entry to the DB.
   * @param uri
   * @param profile
   * @param lang
   * @param label
   */
  public void addLabel(String uri, String profile,String lang, String label) {
    ProfileDependentLabel pdl = new ProfileDependentLabel();
    HashMap<String,String> hm = new HashMap<String,String>();
    if ( this.containsKey(uri) ) {
      pdl = this.get(uri);
      if ( pdl.containsKey(profile) ) {
        hm = pdl.get(profile);
      }
    }
    hm.put(lang, label);
    pdl.put(profile, hm);
    this.put(uri, pdl);
  }
  
  /**
   * Returns a Set of Strings, each representing a language in the model, found during a run of {@link #buildDB(RepositoryConnection, String)}.
   * @return
   */
  public Set<String> getModelLanguages() {
    return this.modelLanguages;
  }
  
  /**
   * Returns a Set of Strings, each representing a profile in the model, found during a run of {@link #buildDB(RepositoryConnection, String)}.
   * @return
   */
  public Set<String> getModelProfiles() {
    return this.modelProfiles;
  }
  
  /**
   * returns a ProfileDependentLabel object for the given URI
   * @param   uri
   * @return  return value is <code>null</code> if there was no entry in the DB
   */
  public ProfileDependentLabel getLabel(String uri) {
    if ( this.containsKey(uri) ) {
      return this.get(uri);
    }
    return null;
  }
  
  /**
   * Extracts all profile dependent labels out of a Sesame repository.
   * @param connection
   * @param modelNS     default namespace of the OWL Model
   */
  public void buildDB(RepositoryConnection connection,String modelNS) {
    String q = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
    		"select distinct ?uri ?profile ?label\n" +
        "where {\n" +
        "  ?profile rdfs:subPropertyOf <"+modelNS+"has-label>.\n" +
        "  ?uri ?profile ?label.\n" +
        "  FILTER (?profile != <"+modelNS+"has-label>).\n" +
        "}";
      
    try {
//      System.out.println("ProfileDependentLabelDB#buildDB: modelNS: "+modelNS);
      System.out.println("ProfileDependentLabelDB#buildDB:   QUERY: \n"+q);
//      System.err.println("ProfileDependentLabelDB#buildDB: connection: "+connection.isOpen());
      TupleQueryResult res = connection.prepareTupleQuery(QueryLanguage.SPARQL, q).evaluate();
//      System.out.println("ProfileDependentLabelDB#buildDB: TupleQuery prepared");
      while ( res != null && res.hasNext() ) {
        BindingSet row = res.next();
        String profile = row.getValue("profile").stringValue().replaceAll(".*/([^\\.]*).*$", "$1"); // this should be solved in a more elegant way
        String lang = row.getValue("label").toString().split("@")[1]; // this too should be solved more elegantly
        String label = row.getValue("label").stringValue();
        String uri = row.getValue("uri").toString();  
//        System.out.println("ProfileDependentLabelDB#buildDB: Setting uri("+uri+"), profile("+profile+"), lang("+lang+") and label("+label+")");
        this.modelLanguages.add(lang);
        this.modelProfiles.add(profile);
        this.addLabel(uri, profile, lang, label);
      }
      System.out.println("ProfileDependentLabelDB#buildDB: All Labels extracted.");
    } catch (QueryEvaluationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println("ProfileDependentLabelDB#buildDB: Something's wrong");
    } catch (RepositoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println("ProfileDependentLabelDB#buildDB: Something's wrong");
    } catch (MalformedQueryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println("ProfileDependentLabelDB#buildDB: Something's wrong");
    }
  }
}
