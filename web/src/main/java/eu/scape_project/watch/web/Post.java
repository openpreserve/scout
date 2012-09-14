package eu.scape_project.watch.web;

import static mustachelet.annotations.HttpMethod.Type.GET;
import static mustachelet.annotations.HttpMethod.Type.POST;

import java.io.IOException;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mustachelet.annotations.Controller;
import mustachelet.annotations.HttpMethod;
import mustachelet.annotations.Path;
import mustachelet.annotations.Template;

import com.google.inject.Inject;

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