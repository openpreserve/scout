package eu.scape_project.watch.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;

import com.google.inject.Inject;

import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/logout")
@TemplateSource("logout")
public class Logout extends TemplateContext {

	@Inject
	private HttpServletResponse response;

	@Controller(HttpMethod.Type.GET)
	boolean redirectPostData() throws IOException {
		SecurityUtils.getSubject().logout();
		response.sendRedirect(getMustacheletPath() + "/");
		return false;
	}

}
