package eu.scape_project.watch.web;

import java.io.IOException;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.Template;

import static eu.scape_project.watch.web.annotations.HttpMethod.Type.*;

/**
 * Post / redirect handling
 * <p/>
 * User: sam Date: 12/21/10 Time: 3:49 PM
 */
@Path("/post(/(.*))?")
@Template("post.html")
@HttpMethod({GET, POST})
public class Post {
  @Inject
  HttpServletResponse response;

  @Inject
  HttpServletRequest request;

  @Controller(POST)
  boolean redirectPostData() throws IOException {
    response.sendRedirect("/post/" + request.getParameter("value"));
    return false;
  }

  @Inject
  Matcher m;

  String value() {
    return m.group(2);
  }
}