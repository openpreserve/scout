package eu.scape_project.watch.rest;

import com.sun.jersey.api.core.PackagesResourceConfig;

public class WatchApplication extends PackagesResourceConfig {

  public WatchApplication() {
    super("eu.scape_project.watch.rest.resource");
  }

}
