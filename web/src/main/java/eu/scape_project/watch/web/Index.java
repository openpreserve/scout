package eu.scape_project.watch.web;

import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;


@Path("/(index.html)?")
@Template("index.html")
public class Index extends Mustachelet {

  boolean page_index() {
    return true;
  }
}
