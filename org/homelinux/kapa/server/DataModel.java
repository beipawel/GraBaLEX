package org.homelinux.kapa.server;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.homelinux.kapa.client.BehaviorType;
import org.homelinux.kapa.client.ComplexEndPoint;
import org.homelinux.kapa.client.Dynamic;
import org.homelinux.kapa.client.LexItem;
import org.homelinux.kapa.client.LexItems;
import org.homelinux.kapa.client.NoEndPoint;
import org.homelinux.kapa.client.OWLInstanceItem;
import org.homelinux.kapa.client.Properties;
import org.homelinux.kapa.client.Property;
import org.homelinux.kapa.client.SimpleEndPoint;
import org.homelinux.kapa.client.SimpleLabel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * This class connects to an OWL-ontology via Sesame and Protege API. 
 * It prepares data on initialization, and gives access to data on runtime via its public methods.
 * @author Pawel Müller
 */
public class DataModel {
  // This Data Model defines some useful Methods and Variables for Data needed by a GUI
  // I'll try to keep it as GUI unspecific as possible
  
  private Properties                properties                        = null;
  private String                    repositoryId                      = "";
  private String                    sesameServer                      = "";
  private Repository                repository                        = null;
  private RepositoryConnection      connection                        = null;
  private String                    modelNS                           = "http://www.ims.uni-stuttgart.de/~spohrds/medic/medic.owl#";
  private JenaOWLModel              owlModel;
  private ProfileDependentLabelDB   profileDependentLabelDB           = new ProfileDependentLabelDB();
  private ProfileIndependentLabelDB profileIndependentLabelDB         = new ProfileIndependentLabelDB();
//  private PresentationStatusDB      presentaionStatusDB               = new PresentationStatusDB();
  private AccessStatusDB            accessStatusDB                    = new AccessStatusDB();
  private ArrayList<String>         defaultPath                       = new ArrayList<String>();
  private Collection<RDFSClass>     allRDFSClasses                    = null; // this will store all the classes of the model 
  private String                    prefixes      = "PREFIX owl2xml:<http://www.w3.org/2006/12/owl2-xml#>" + "\n" +
  		                                              "PREFIX ub:<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>" + "\n" +
  		                                              "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>" + "\n" +
  		                                              "PREFIX recl1:<http://www.ims.uni-stuttgart.de/~spohrds/medic/recl1.owl#>" + "\n" +
  		                                              "PREFIX protege:<http://protege.stanford.edu/plugins/owl/protege#>" + "\n" +
  		                                              "PREFIX swrlb:<http://www.w3.org/2003/11/swrlb#>" + "\n" +
  		                                              "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" + "\n" +
  		                                              "PREFIX protonkm:<http://proton.semanticweb.org/2005/04/protonkm#>" + "\n" +
  		                                              "PREFIX owl:<http://www.w3.org/2002/07/owl#>" + "\n" +
  		                                              "PREFIX protont:<http://proton.semanticweb.org/2005/04/protont#>" + "\n" +
  		                                              "PREFIX prodl2:<http://www.ims.uni-stuttgart.de/~spohrds/medic/prodl2.owl#>" + "\n" +
  		                                              "PREFIX sumo:<http://reliant.teknowledge.com/DAML/SUMO.owl#>" + "\n" +
  		                                              "PREFIX protons:<http://proton.semanticweb.org/2005/04/protons#>" + "\n" +
  		                                              "PREFIX german:<http://www.ims.uni-stuttgart.de/~spohrds/medic/german.owl#>" + "\n" +
  		                                              "PREFIX xsp:<http://www.owl-ontologies.com/2005/08/07/xsp.owl#>" + "\n" +
  		                                              "PREFIX swrl:<http://www.w3.org/2003/11/swrl#>" + "\n" +
  		                                              "PREFIX protonu:<http://proton.semanticweb.org/2005/04/protonu#>" + "\n" +
  		                                              "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n" +
  		                                              "PREFIX medic:<http://www.ims.uni-stuttgart.de/~spohrds/medic/medic.owl#>" + "\n";
  
  /**
   * An Instance of that class holds informations served via its method to a GUI.
   * @param model_file        Where the medic.owl file is (the main ontology model for the lexicon)
   * @param repository_name   The repositories name at a sesame server
   * @param sesame_server_name  The address to a sesame server, where <code>repository_name</code> should be found.
   */
  @SuppressWarnings("unchecked")
  public DataModel (String model_file, String repository_name, String sesame_server_name) {
    this.repositoryId = repository_name;
    this.sesameServer = sesame_server_name;
    System.out.println("Sesame Server: "+sesame_server_name+"\nRepository ID: "+repository_name);
    this.repository = new HTTPRepository(this.sesameServer, this.repositoryId);
    try {
      repository.initialize();
      this.connection = repository.getConnection();
      // if we have the connection, we can build the labels DBs
      this.profileDependentLabelDB.buildDB(connection,this.modelNS);
      System.out.println("this.profileDependentLabelDB.size(): "+this.profileDependentLabelDB.size());
      this.profileIndependentLabelDB.buildDB(connection, this.modelNS);
//      this.presentaionStatusDB.buildDB(connection, this.modelNS);
      this.accessStatusDB.buildDB(connection, this.modelNS);
      System.out.println("this.profileIndependentLabelDB.size(): "+this.profileIndependentLabelDB.size());
    } catch (RepositoryException e) {
      // TODO: handle exception
      e.printStackTrace();
      System.out.println("DataModel#init(): Something's wrong");
    }
    // we load the model as .owl file to use the protege API
    try {
      this.owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(new FileInputStream(model_file));
      // getting all classes
      this.allRDFSClasses = (Collection<RDFSClass>) this.owlModel.getRDFSClasses();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (OntologyLoadException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    // setting the defaultPath (it's the underspecified path for a property that should match to a String)
    defaultPath.add(modelNS+"has-sense-or-form-relation-to");
    defaultPath.add(modelNS+"has-form-description");
    defaultPath.add(modelNS+"has-orthographic-form");
    // in the pdf file from Dennis, there is also a filter on the output of :has-orthographic-form, but I think I could simply say, that 
    // such queries has always a Filter on the output of the path, when there should be a match on a String
  }
  
  /**
   * Returns a {@link Properties} object which stores all properties in the MLR Model.
   * @return
   */
  public Properties getProperties() {
    if ( this.properties != null ) {
      // If this.properties != null, it means, we have the results already, 
      // so we don't have to extract them from the models a second time
      return this.properties;
    }
    Properties props = new Properties();
    
//    ArrayList<Property> props = new ArrayList<Property>();
//    props.addAll(this.getSpecialProperties()); // first the special properties
//    props.addAll(this.getSimpleProperties().subList(0, 151)); // only for debugging
    props.addAll(this.getSimpleProperties());
    ArrayList<String> defaultDomain = this.getSubclassesOf("LexicalEntity",true);
    this.writeToFile("/tmp/pawel_default_domain", "default domain --- "+defaultDomain.toString());
    props.setDefaultDomain(defaultDomain);
    Collections.sort(props, new Comparator<Property>() {
      public int compare (Property p1, Property p2) {
        if ( p1.getBehaviorType() instanceof NoEndPoint ) {
          if ( ((NoEndPoint) p1.getBehaviorType()).isRoleProperty() ) {
            return 1;
          }
        }
        return -1;
      }
    });
 // ONLY FOR DEBUGGING START
//    System.out.println("Looping over all Props");
//    String s = "";;
//    for (Iterator<Property> iterator = props.iterator(); iterator.hasNext();) {
//      Property px = iterator.next();
//      s = s.concat(px.getName()+";"+px.getBehaviorType().getName());
//      if ( px.getBehaviorType() instanceof NoEndPoint ) {
//        s = s.concat(";"+((NoEndPoint) px.getBehaviorType()).isRoleProperty().toString() );
//      }
//      s = s.concat("\n");
//    }
//    System.out.println("Writing some stuff to a file");
//    this.writeToFile("/tmp/pawel_property__behavioralType", s);
  

  // ONLY FOR DEBUGGING END
    this.properties = props; // we'll store the props List in a instance variable for future requests
    return props;
  }
  
  
  @SuppressWarnings("unchecked")
  private ArrayList<String> getDomainOfProperty(RDFProperty property) {
    ArrayList<String> domain = new ArrayList<String>();
//    System.out.println("property name: "+property.getName());
    for (Iterator<Object> iterator = property.getUnionDomain(true).iterator(); iterator.hasNext();) {
//    for (Iterator<RDFSClass> iterator = property.getDomains(false).iterator(); iterator.hasNext();) {
      Object rdfCls = iterator.next();
      if ( ! (rdfCls instanceof RDFSNamedClass) ) {
        continue;
      }
      RDFSClass rdfsClassName = (RDFSClass) rdfCls;
//      System.err.println("rdfsClassName ("+property.getName()+"): "+rdfsClassName.getName());
      if ( rdfsClassName == null ) {
//        System.err.println("rdfsClassName == null for "+property.getName());
      }
      else if ( rdfsClassName.toString().matches(".*Thing.*") ) {
        // TODO: There should be a comment on that in my log file
        continue;
      }
      else {
        domain.add(rdfsClassName.getName());
        for (Iterator<String> subclassIterator = this.getSubclassesOf(rdfsClassName.getName(), true).iterator(); subclassIterator.hasNext();) {
          String subCls = subclassIterator.next();
          if ( ! domain.contains(subCls) ) {
            domain.add(subCls);
          }
        }
      }
    }
    return domain;
  }
  
  @SuppressWarnings("unchecked")
  private ArrayList<String> getRangeOfProperty(RDFProperty property) {
    ArrayList<String> range = new ArrayList<String>();
//    System.out.println("Länge von property.getRanges(true): "+property.getRanges(true).size());
    for (Iterator<RDFSClass> iterator = property.getUnionRangeClasses().iterator(); iterator.hasNext();) {
//    for (Iterator<Object> iterator = property.getRanges(true).iterator(); iterator.hasNext();) {
      Object rdfCls = iterator.next();
      if ( ! (rdfCls instanceof RDFSNamedClass) ) {
        continue;
      }
      RDFSClass rdfsClassName = (RDFSClass) rdfCls;
      if ( rdfsClassName == null ) {
        System.err.println("getRangeOfProperty: rdfsClassName == null for "+property.getName());
      }
      else {
        range.add(rdfsClassName.getName());
        for (Iterator<String> subclassIterator = this.getSubclassesOf(rdfsClassName.getName(), true).iterator(); subclassIterator.hasNext();) {
          String subCls = subclassIterator.next();
          if ( ! range.contains(subCls) ) {
            range.add(subCls);
          }
        }
      }
    }
    return range;
  }
  
  /**
   * Returns a List of RDFSClass Names for className. 
   * @param className       Classname for which to extract the List
   * @param withSubClasses  Include Subclasses
   * @return
   */
  @SuppressWarnings("unchecked")
  private ArrayList<String> getSubclassesOf(String className, Boolean withSubClasses) {
    ArrayList<String> al = new ArrayList<String>();
    RDFSClass cls = this.owlModel.getRDFSNamedClass(className); // let's get a RDFSClass object for className
    if ( cls == null ) {
      return al;
    }
    al.add(cls.getName());
    for (Iterator<RDFSClass> iterator = cls.getSubclasses(withSubClasses).iterator(); iterator.hasNext();) {
      RDFSClass subCls = iterator.next();
      if ( ! (subCls instanceof RDFSNamedClass) ) {
        continue;
      }
      al.add(subCls.getName());
    }
    return al;
  }
  
  @SuppressWarnings("unused")
  private ArrayList<Property> getSpecialProperties() {
    // here we'll get the Properties from ... well, I don't know excatly from where right now.
    // I thought about some kind of model, but it also could be all written here in this method.
    // The model would be cooler, as I could then connect it to a database or some other storage
    // so that one would not to have to write Java code, just to add, remove or edit special properties
    ArrayList<Property> props = new ArrayList<Property>();
    return props;
  }

  @SuppressWarnings({ "unchecked"})
  private ArrayList<Property> getSimpleProperties() {
    /**
     * There are 3 main categories of Properties
     *   - Is End Point: ((subproperty of :has-descriptive-ralation-to && subclass of LinguisticFeature in domain) || rdf:type)
     *      - no sub-specification possible
     *      - user get's a drop-down list of instances found ( TODO what if the list is long???)
     *      - if the property is rdf:type (no it's not in the set of properties, but maybe it will be), we should also mark the Property somehow
     *        so we can list only types of the range of the previous Property (the one to the left)
     *   - Is NOT End Point: (if subproperty of :has-descriptive-relation-to)
     *      - such properties won't be given a input field, but instead the sub-specification will be automatically invoked
     *   - Dynamic: (if subproperty of :has-lexical-relation-to)
     *      - such properties will have a path generated out of a underspecified path given
     *      - in the GUI, such properties should act like this:
     *        - text box will be displayed
     *        - if text box is empty, then sub-specification is possible and the path is not used
     *        - however, if the user types something into the text box, sub-specification should not be allowed, and the string typed into the text box
     *          should be matched against that one at the end of the path (which is :has-orthographic-form now). 
     */
    ArrayList<Property> props = new ArrayList<Property>();
    for (Iterator<RDFProperty> iterator = owlModel.getRDFProperties().iterator(); iterator.hasNext();) {
      RDFProperty p = iterator.next();
      Property prop = new Property();
      String propertyName = p.getName();
      
      if ( ! p.getName().matches(".*medic.*") && !p.getName().matches(".*rdf.*type$") ) { // quick hack for filtering out none medic properties
//        System.out.println("filtered out: "+p.getName());
        continue;
      }
      
//      this.shouldBePresented(p);
      
      // DEBUG ONLY!!!
//      if ( this.isSubpropertyOf(p.getName(), this.modelNS+"has-semantic-argument") ) { // dirty, but works (filtering some properties out)
//        System.out.println("DEBUG: filtered out "+p.getName());
//        continue;
//      }
      
      // we'll get domain,range,labels first, then we'll set the BehavioralType
      prop.setName(propertyName);
      prop.setLabel(this.profileDependentLabelDB.getLabel(propertyName)); // assigning the property a ProfileDependentLabel object
      prop.setDomain(this.getDomainOfProperty(p));
      prop.setRange(this.getRangeOfProperty(p));
//      prop.setPresentationStatus(this.presentaionStatusDB.getStatus(propertyName));
      prop.setAccessStatus(this.accessStatusDB.getStatus(propertyName));
      ArrayList<String> path = new ArrayList<String>();
      path.add(propertyName);
      prop.setPath(path); // here we set the path to an ArrayList with one element. It will be overridden later in some cases
      
      
      // now we'll set the BehavioralType
      if ( isSubpropertyOf(propertyName, this.modelNS+"has-descriptive-relation-to") ) {
        if ( hasInRange(prop, "LinguisticFeature") ) { // if that's true, we have an ComplexEndPoint type
          prop.setBehavioralType((new ComplexEndPoint()));
//          System.out.println("subproperty von :has-descriptive-relation-to: TYPE ComplexEndPoint");
        }
        else { // otherwise it's a NoEndPoint type
          NoEndPoint nep = new NoEndPoint();
          // we want to mark Role properties
          if ( this.isSubpropertyOf(p.getName(), this.modelNS+"has-semantic-argument") ) {
            nep.setRoleFlag(true);
            // TODO: FOR DEBUG ONLY
//            continue;
            // END: FOR DEBUG ONLY
          }
          prop.setBehavioralType(nep);
//          System.out.println("subproperty von :has-descriptive-relation-to: TYPE NoEndPoint");
        }
      }
      else if ( (isSubpropertyOf(propertyName, this.modelNS+"has-lexical-relation-to")) || (isSubpropertyOf(propertyName, this.modelNS+"has-sense-or-form-relation-to")) ) { // dynamic type
//        System.out.println("subproperty von :has-lexical-ralation-to: TYPE Dynamic");
        prop.setBehavioralType((new Dynamic()));
        ArrayList<String> tmp_path = new ArrayList<String>(); 
        tmp_path.add(propertyName);
        tmp_path.addAll(defaultPath);
        prop.setPath(tmp_path);
      }
      else if ( p.hasDatatypeRange() ) {
        // If the Property is a Datatype Property, we will classify it as a SimpleEndPoint.
        // A Property is a Datatype Property if it has a simple Datatype in it's range, like string, int, ...
        prop.setBehavioralType((new SimpleEndPoint()));
      }
      else if ( propertyName.matches(".*rdf.*#type$") ) {
        // Special treatment for rdf:type
        // eventually we could do that in the getSpecialProperties Method
        
        // The rdf:type property gets special treatment.
        // This property has to be visible in each context, regardless which property is selected in the wrapper specification object,
        // or if it is in the root specifications object.
        // To achieve that, we'll construct a domain, which is an ArrayList holding every OWL-class URI in it, while
        // the range holds only all subclasses of LexicalEntity.
        // The range should be changed by the GUI, so that it holds the Range of the property selected in the wrapper specification object,
        // but if there is no wrapper, and we're in the root specifications object (or whatever the gui, which connects to the DataModel class calls it) then we can use the range
        // for a default behavior.
        System.out.println("dealing with rdf:type");
        ArrayList<String> dom = new ArrayList<String>();
        ArrayList<String> ran = new ArrayList<String>();
        ran.addAll(this.getSubclassesOf(this.owlModel.getRDFSNamedClass("LexicalEntity").getName(), true));  // range
        dom.addAll(this.getSubclassesOf(this.owlModel.getRDFSNamedClass("DescriptiveEntity").getName(), true)); // domain
        dom.addAll(ran); // domain should also have all subclasses of LexicalEntity in it, so we simply append the ran object
        prop.setDomain(dom);
        prop.setRange(ran); 
        ComplexEndPoint cep = new ComplexEndPoint();
        cep.setRDFType(true);
        prop.setBehavioralType(cep);
      }
      else {
        prop.setBehavioralType((new BehaviorType()));
        System.out.println("!!! WE HAVE NO TYPE ("+propertyName.replaceAll(".*#(.*)\\)?", "$1")+") !!!");
      }
//      prop.setCurrentLabel(this.defaultProfile, this.defaultLanguage); // TODO this should be set to the default language
      props.add(prop); // we add the property to the List
    }
    
    // ONLY FOR DEBUGGING START
//    System.out.println("Looping over all Props");
//    String s = "";;
//    for (Iterator<Property> iterator = props.iterator(); iterator.hasNext();) {
//      Property px = iterator.next();
//      s = s.concat(px.getName()+" --- ");
//      if ( px.getDomain() != null ) {
//        s = s.concat(px.getDomain().toString()+"\n");
//      }
//      else {
//        s = s.concat("NULL\n");
//      }
//    }
//    System.out.println("Writing some stuff to a file");
//    this.writeToFile("/tmp/pawel_property__domain", s);
    

    // ONLY FOR DEBUGGING END
    
    return props;
  }
  
  /**
   * Returns true if <code>prop</code> has <code>className</code> or a subclass of <code>className</code> in its domain
   * @param prop
   * @param className
   * @return
   */
  @SuppressWarnings({ "unchecked", "unused" })
  private Boolean hasInDomain(Property prop, String className) {
    /**
     * this method will check if prop has a className or a subclass of className in its domain
     */
    if ( this.allRDFSClasses == null ) {
      this.allRDFSClasses = this.owlModel.getRDFSClasses();
    }
    RDFSClass rdfsSuperclass = this.owlModel.getRDFSNamedClass(className); // let's get a RDFSClass object for the String className
    Collection<RDFSClass> allSubclasses = rdfsSuperclass.getSubclasses(true);
    for (Iterator<String> iterator = prop.getDomain().iterator(); iterator.hasNext();) {
      String cln = iterator.next();
//      System.out.println("hasInDomain: Finding Class in Model for '"+cln+"'");
      RDFSClass rdfsClass = (RDFSClass) this.owlModel.getRDFSNamedClass(cln);
      if ( rdfsClass == null ) {
//        System.err.println("hasInDomain: rdfsClass == null for '"+cln+"'");
      }
      else {
//        System.out.println("hasInDomain: found this class in the model for '"+cln+"': "+rdfsClass.toString());
        if ( allSubclasses.contains(rdfsClass) ) {
          /**
           * There is a Method RDFSClass.isSubclassOf(Superclass) but it only return true if this is a direct Subclass of Superclass, that's why we use the contains Method here.
           */
//          System.out.println("hasInDomain: YESSSS!!!! Property "+prop.getName().replaceAll(".*#(.*)\\)?", "$1")+" seems to have a subclass ("+rdfsClass.getName().replaceAll(".*#(.*)\\)?", "$1")+") of "+className.replaceAll(".*#(.*)\\)?", "$1")+" in its domain");
          return true;
        }
      }
    }
    return false;
  }
  
  
  /**
   * Returns true if <code>prop</code> has <code>className</code> or a subclass of <code>className</code> in its range
   * @param prop
   * @param className
   * @return
   */
  @SuppressWarnings("unchecked")
  private Boolean hasInRange(Property prop, String className) {
    if ( this.allRDFSClasses == null ) {
      this.allRDFSClasses = this.owlModel.getRDFSClasses();
    }
    RDFSClass rdfsSuperclass = this.owlModel.getRDFSNamedClass(className); // let's get a RDFSClass object for the String className
    Collection<RDFSClass> allSubclasses = rdfsSuperclass.getSubclasses(true);
    
    for (Iterator<String> iterator = prop.getRange().iterator(); iterator.hasNext();) {
      String cln = iterator.next();
//      System.out.println("hasInRange: Finding Class in Model for '"+cln+"'");
      RDFSClass rdfsClass = (RDFSClass) this.owlModel.getRDFSNamedClass(cln);
      if ( rdfsClass == null ) {
//        System.err.println("hasInRange: rdfsClass == null for '"+cln+"'");
      }
      else {
//        System.out.println("hasInRange: found this class in the model for '"+cln+"': "+rdfsClass.toString());
        if ( allSubclasses.contains(rdfsClass) ) {
          /**
           * There is a Method RDFSClass.isSubclassOf(Superclass) but it only return true if this is a direct Subclass of Superclass, that's why we use the contains Method here.
           */
//          System.out.println("hasInRange: YESSSS!!!! Property "+prop.getName().replaceAll(".*#(.*)\\)?", "$1")+" seems to have a subclass ("+rdfsClass.getName().replaceAll(".*#(.*)\\)?", "$1")+") of "+className.replaceAll(".*#(.*)\\)?", "$1")+" in its range");
          return true;
        }
        else if ( rdfsSuperclass.equals(rdfsClass) ) {
          return true;
        }
      }
    }
    return false;
  }
  
  private Boolean isSubpropertyOf(String sub_property, String super_property) {
    return owlModel.getRDFProperty(sub_property).isSubpropertyOf(owlModel.getRDFProperty(super_property), true);
  }
  
  public ArrayList<SimpleLabel> getModelLanguages() {
    ArrayList<SimpleLabel> modelLanguages = new ArrayList<SimpleLabel>();
    for (Iterator<String> iterator = this.profileDependentLabelDB.getModelLanguages().iterator(); iterator.hasNext();) {
      String lang = iterator.next();
      String langLabel = lang;
      if ( lang.contentEquals("de") ) {
        langLabel = "Deutsch";
      }
      else if ( lang.contentEquals("en") ) {
        langLabel = "Englisch";
      }
      SimpleLabel sl = new SimpleLabel(lang,langLabel); // for now the name and the label are the same
      modelLanguages.add(sl);
    }
    Collections.sort(modelLanguages); // we sort the SimpleLabels, otherwise the order will always be different, as we derive the Data out of a Set
    return modelLanguages;
  }
  
  public ArrayList<SimpleLabel> getModelProfiles() {
    ArrayList<SimpleLabel> modelProfiles = new ArrayList<SimpleLabel>();
    for (Iterator<String> iterator = this.profileDependentLabelDB.getModelProfiles().iterator(); iterator.hasNext();) {
      String prof = iterator.next();
      String profLabel = prof;
      if ( prof.contentEquals("prodl2") ) {
        profLabel = "Produktion in L2";
      }
      else if ( prof.contentEquals("recl1") ) {
        profLabel = "Perzeption in L1";
      }
      SimpleLabel sl = new SimpleLabel(prof, profLabel); // for now the name and the label are the same
      modelProfiles.add(sl);
    }
    Collections.sort(modelProfiles); // we sort the SimpleLabels, otherwise the order will always be different, as we derive the Data out of a Set
    return modelProfiles;
  }
  
  public ArrayList<OWLInstanceItem> getOWLInstanceItems(Property property) {
    ArrayList<OWLInstanceItem> items = new ArrayList<OWLInstanceItem>();
    ArrayList<Instance> waste = new ArrayList<Instance>(); // needed to ensure that each OWLInstanceItem will be returned only once (ugly!)
    ArrayList<String> waste2 = new ArrayList<String>(); // also not very nice
    for (Iterator<String> iterator = property.getRange().iterator(); iterator.hasNext();) {
      String clsName = iterator.next();
      RDFSClass cls = this.owlModel.getRDFSNamedClass(clsName); // get an object for clsName
      if ( cls != null ) {
        if ( property.getBehaviorType() instanceof ComplexEndPoint  ) {
          ComplexEndPoint cep = (ComplexEndPoint) property.getBehaviorType();
          if ( cep.isRDFType() ) {
            for (Iterator<String> iterator2 = this.getSubclassesOf(clsName, true).iterator(); iterator2.hasNext();) {
              String type = iterator2.next();
              if ( waste2.contains(type) ) {
                // very ugly implementation to avoid more then one occurrence of an item
                continue;
              }
              waste2.add(type);
              OWLInstanceItem item = null;
              // checking the profile dependent DB for an entry for uri
              // the independent DB is smaller and simpler to search, but generally an OWLInstanceItem get's a profile dependent label
              // and therefore the extraction is faster that way
              if ( this.profileDependentLabelDB.containsKey(type) ) {
                item = new OWLInstanceItem(this.profileDependentLabelDB.getLabel(type) , type);
              }
              else {
                item = new OWLInstanceItem(this.profileIndependentLabelDB.getLabel(type) , type);
              }
              if ( ! items.contains(item) ) {
                // for some reason this is not doing what I think it should, that's why I used waste2 variable.
                // I guess the problem is, that the item instances are really not equal, as it's always a new item, but
                // how to check for that? I would have to extend ArrayList and override the contains, and/or equal method.
                item.setAccessStatus(this.accessStatusDB.getStatus(item.getURI()));
                items.add(item);
              }
            }
          }
          else {
//        Collection<Instance> instances = this.owlModel.getInstances(cls);
            Collection<Instance> instances = cls.getInstances(true);
            for (Iterator<Instance> iterator2 = instances.iterator(); iterator2.hasNext();) {
              Instance instance = iterator2.next();
              if ( waste.contains(instance) ) {
                // very ugly implementation to avoid more then one occurrence of an item
                continue;
              }
              waste.add(instance);
              // Instances can have either a profileDependent or a profileIndependent Label
              // So first we check in which DB object the uri is in, then we set the label
              OWLInstanceItem item = null;
              // checking the profile dependent DB for an entry for uri
              // the independent DB is smaller and simpler to search, but generally an OWLInstanceItem get's a profile dependent label
              // and therefore the extraction is faster that way
              if ( this.profileDependentLabelDB.containsKey(instance.getName()) ) {
                item = new OWLInstanceItem(this.profileDependentLabelDB.getLabel(instance.getName()) , instance.getName());
                item.setAccessStatus(this.accessStatusDB.getStatus(item.getURI()));
              }
              else {
                item = new OWLInstanceItem(this.profileIndependentLabelDB.getLabel(instance.getName()) , instance.getName());
                item.setAccessStatus(this.accessStatusDB.getStatus(item.getURI()));
              }
              items.add(item);
            }
          }
        }
      }
    }
    return items;
  }
  
  /**
   * Returns the hole profileDependentLabelDB. Comes in handy when we want to get the information in a tester class.
   * @return
   */
  public ProfileDependentLabelDB getProfileDependentLabelDB () {
    return this.profileDependentLabelDB;
  }
  
  /**
   * Returns the hole profileIndependentLabelDB. Comes in handy when we want to get the information in a tester class.
   * @return
   */
  public ProfileIndependentLabelDB getProfileIndependentLabelDB () {
    return this.profileIndependentLabelDB;
  }
  
  public LexItems getLexiconItems(String q) {
    LexItems lis = new LexItems();
    // get the first variable from the passed query q
    if ( q.length() == 0 ) {
      return lis;  // we return an empty list, if the query was empty
    }
    System.err.println("q: "+q);
    // a check of q should be made in the future (security reasons)
    String v = q.trim().split("\\s+")[0]; // extracting the name of the first variable.
    System.out.println("v: "+v);
    String query = this.prefixes+"\nselect distinct "+v+" \n" +
    		"where {\n" +
    		" "+v+" rdf:type <" + this.modelNS+"Lexeme>. \n" +
    		" "+ q + "\n"+
    		"}"; 
    lis.setQuery(query); // setting the query, so we can display it on the client side
    System.out.println("getLexiconItems(): Query:\n"+query.replaceAll("<http[^>]+#([a-z0-9-]+)>", "medic:$1"));
    try {
      TupleQueryResult res = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
      while ( res != null && res.hasNext() ) {
        BindingSet bindingSet = res.next();
        LexItem lxItem = new LexItem();
//        System.err.println("bindings: "+bindingSet.getBindingNames().toString());
//        System.err.println("v replaced: "+v.replaceAll("\\?", ""));
        String uri = bindingSet.getValue(v.replaceAll("^\\?(.*)", "$1")).stringValue(); // this is the URI
        lxItem.setName(uri);
        lxItem.setLabel(this.profileIndependentLabelDB.getLabel(uri));
        lis.add(lxItem);
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
//    System.err.println("lis("+lis.size()+"): "+lis.toString());
    Collections.sort(lis);
    return lis;
  }

  /**
   * Returns a RDF as String for <code>uri</code>.
   * @param uri
   * @return
   */
  public String getLexiconItem(String uri) {
    System.out.println("Umlaute: äöüß");
    System.out.println("DataModel#getLexiconItem: item passed: "+uri);
    String query = this.prefixes +
    		"CONSTRUCT { ?s ?p ?o }\n" +
    		"WHERE {" +
    		"  {\n" +
    		"    <"+uri+"> medic:has-sense-or-form-relation-to ?s \n" +
    		"  }\n" +
    		"  UNION { " +
    		"    <"+uri+"> medic:has-sense-or-form-relation-to ?x .\n" +
    		"    { ?x medic:has-descriptive-relation-to ?s }\n" +
    		"    UNION { ?x medic:has-lexical-relation-to ?s }\n" +
    		"    UNION { ?x medic:has-valence-frame [ medic:has-descriptive-relation-to ?s ] }\n" +
    		"    UNION { ?x medic:has-valence-frame [ medic:uses-frame [ medic:has-descriptive-relation-to ?s ] ] }\n" +
    		"  } . \n" +
    		"  ?s ?p ?o\n" +
    		"}\n";
    System.out.println("Quer\n"+query);
    try {
      CharArrayWriter writer = new CharArrayWriter();
      RDFXMLPrettyWriter rdfWriter = new RDFXMLPrettyWriter(writer);
      GraphQuery graphQuery = this.connection.prepareGraphQuery(QueryLanguage.SPARQL, query);
      graphQuery.setIncludeInferred(true);
      graphQuery.evaluate(rdfWriter);
//      System.out.println(writer.toString());
      return writer.toString();
    } catch (RepositoryException e) {
      e.printStackTrace();
    } catch (MalformedQueryException e) {
      e.printStackTrace();
    } catch (QueryEvaluationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (RDFHandlerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "somethings's wrong";
  }
  
  private void writeToFile(String file_name, String text) {
    // ONLY FOR DEBUGGING START
    System.out.println("Start writing to "+file_name+".");
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter(file_name));
      out.write(text);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.err.println("/tmp/profileDependentLabels.log");
      e.printStackTrace();
    }
    finally {
      try {
        out.close();
        System.out.println("Writing to "+file_name+" done.");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    // ONLY FOR DEBUGGING END
  }
}
