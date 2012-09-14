package eu.scape_project.watch.web;

import mustachelet.annotations.Controller;
import mustachelet.annotations.Path;
import mustachelet.annotations.Template;

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
