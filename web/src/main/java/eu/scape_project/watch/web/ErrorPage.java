package eu.scape_project.watch.web;

import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;

import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/error/([^/]*)")
@TemplateSource("error")
@HttpMethod(HttpMethod.Type.GET)
public class ErrorPage extends TemplateContext {

	@Inject
	Matcher m;

	public String getErrorCode() {
		return m.group(1);
	}

	public boolean isPageMissing() {
		return getErrorCode().equals("404");
	}

	public boolean isClientError() {
		return getErrorCode().equals("400");
	}
	
	public boolean isInternalError() {
		return getErrorCode().equals("500");
	}

	@Inject
	private HttpServletRequest request;

	public String getOriginalUri() {
		return (String) request.getAttribute("javax.servlet.error.request_uri");
	}
	
	public String getMessage() {
		return request.getParameter("message");
	}
	
	public String getBack() {
		return request.getParameter("back");
	}
	
	public String getDetails() {
		return request.getParameter("details");
	}
}
