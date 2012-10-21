package eu.scape_project.watch.web;

import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;


@Path("/error/([^/]*)")
@TemplateSource("error")
public class ErrorPage extends TemplateContext {

  
  
  @Inject
  Matcher m;

  public String getErrorCode() {
    return m.group(1);
  }
}
