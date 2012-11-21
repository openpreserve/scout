package eu.scape_project.watch.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/upload/policy/new")
@TemplateSource("uploadPolicy")
@HttpMethod({HttpMethod.Type.GET, HttpMethod.Type.POST})
public class UploadPolicy extends TemplateContext {

  @Inject
  private HttpServletResponse response;

  @Inject
  private HttpServletRequest request;

  @Controller(HttpMethod.Type.POST)
  boolean redirectPostData() throws IOException {

    return false;
  }

  public String getRedirect() {
    return "/web";
  }

}
