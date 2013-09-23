package eu.scape_project.watch.web;

import org.apache.shiro.SecurityUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class TemplateContext {

	private static final int PAGE_SIZE = 100;
	private static final String ROLE_ADMIN = "administrator";

	@Inject
	@Named("contextPath")
	private String contextPath;

	@Inject
	@Named("mustacheletPath")
	private String mustacheletPath;

	public int getPageSize() {
		return PAGE_SIZE;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getMustacheletPath() {
		return mustacheletPath;
	}

	public boolean isLogged() {
		return SecurityUtils.getSubject().isAuthenticated();
	}

	public boolean isAdmin() {
		return isLogged() && SecurityUtils.getSubject().hasRole(ROLE_ADMIN);
	}

	public String getUsername() {
		if (isLogged()) {
			return SecurityUtils.getSubject().getPrincipal().toString();
		} else {
			return null;
		}
	}

}
