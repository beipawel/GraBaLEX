package org.homelinux.kapa.client;

import java.util.Date;

/**
 * Gives us some Tools to measure time
 * @author Pawel Müller
 *
 */
public class Timer {

  public static String getTimeDiff(Date t_start, Date t_end) {
    Long l1 = t_start.getTime();
    Long l2 = t_end.getTime();
    Long elapsed_time = l2-l1;
    return formatElapsedTime(elapsed_time);
  }
  
  public static String formatElapsedTime(Long t) {
    long m, s, ms;
    long t_in_sec = t / 1000;
    m = t_in_sec / 60;
    s = t_in_sec - (60*m);
    ms = t - m*60*1000 - s*1000;
    return m+"m "+s+"s "+ms+"ms";
  }
  
}
