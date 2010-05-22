package org.homelinux.kapa.client;

public interface Observer {
  /**
   * Tells the implementing Object that there is an update.
   * @param observerable The Observerable Object
   */
  public void update(Object observerable);
}
