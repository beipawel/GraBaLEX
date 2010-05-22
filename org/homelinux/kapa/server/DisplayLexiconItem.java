package org.homelinux.kapa.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.http.client.URL;

public class DisplayLexiconItem extends HttpServlet {

  private static final long serialVersionUID = -7191809171484709063L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // get the DataModel
//    try {
//      System.out.println("DisplayLexiconItem#doGet: item: "+URL.decodeComponent(request.getParameter("item")));
//    } catch (UnsatisfiedLinkError e) {
//      System.out.println("Die Google URL.decodeComponent Methode geht nicht. Irgendwas ist da faul. \n" +
//      		"Die Methode URLDecoder.decode von java.net liefert: "+URLDecoder.decode(request.getParameter("item"), "UTF-8"));
//    }
    System.out.println("Die Google URL.decodeComponent Methode geht nicht. Irgendwas ist da faul. \n" +
        "Die Methode URLDecoder.decode von java.net liefert: "+URLDecoder.decode(request.getParameter("item"), "UTF-8"));
    String result = "";
    DataModel dm = (DataModel) getServletContext().getAttribute("dataModel");
    if ( request.getParameter("item") != null ) {
      response.setContentType("application/rdf+xml;charset=UTF-8");
      result = dm.getLexiconItem(URLDecoder.decode(request.getParameter("item"), "UTF-8"));
    }
    else {
      response.setContentType("text/html;charset=UTF-8");
      result = "Nothing to display";
    }
    PrintWriter out = response.getWriter();
//    out.println(result);
    out.println(result.replaceAll("<rdf:type rdf:nodeID=[^>]+>", ""));
  }
  
}
