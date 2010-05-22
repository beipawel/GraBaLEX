package org.homelinux.kapa.server;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



/**
 * Application Lifecycle Listener implementation class MyServletContextListener
 *
 */
public class MyServletContextListener implements ServletContextListener {
    private DataModel dm = null;
  /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {
      Date t1 = new Date();
      ServletContext sc = event.getServletContext();
      String model_file = sc.getInitParameter("model_file");
      String repository_name = sc.getInitParameter("repository_name");
      String sesame_server_name = sc.getInitParameter("sesame_server_name");
      this.dm = new DataModel(model_file, repository_name, sesame_server_name);
      this.dm.getProperties();
      Date t2 = new Date();
      Long l1 = t1.getTime();
      Long l2 = t2.getTime();
      Long elapsed_time = l2-l1;      
      System.out.println("dataModel set! (Time: "+formatElapsedTime(elapsed_time)+")");
      sc.setAttribute("dataModel", dm);
    }

    private String formatElapsedTime(Long t) {
      long m, s, ms;
      long t_in_sec = t / 1000;
      m = t_in_sec / 60;
      s = t_in_sec - (60*m);
      ms = t - m*60*1000 - s*1000;
      return m+"m "+s+"s "+ms+"ms";
    }
  /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {
      // here I could destroy the data model
    }
  
}
