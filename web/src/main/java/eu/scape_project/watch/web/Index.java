package eu.scape_project.watch.web;

import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

@Path("/")
@Template("index.html")
public class Index {
  @Controller
  boolean exists() {
    return true;
  }

  String name() {
    return "World";
  }
}
