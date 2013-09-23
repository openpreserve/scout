package eu.scape_project.watch.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.util.WebUtils;

import com.google.inject.Inject;

import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/login")
@TemplateSource("login")
@HttpMethod({ HttpMethod.Type.GET, HttpMethod.Type.POST })
public class Login extends TemplateContext {

	private static final String INCORRECT_CREDENTIALS_ERROR = "IncorrectCredentials";
	private static final String UNKNOWN_ACCOUNT_ERROR = "UnknownAccount";
	private static final String LOCKED_ACCOUNT_ERROR = "LockedAccount";
	private static final String UNEXPECTED_ERROR = "Unexpected";

	@Inject
	private HttpServletResponse response;

	@Inject
	private HttpServletRequest request;

	@Controller(HttpMethod.Type.POST)
	boolean redirectPostData() throws IOException {
		final String username = request.getParameter("username");
		final String password = request.getParameter("password");
		final boolean rememberMe = Boolean.parseBoolean(request
				.getParameter("rememberMe"));

		UsernamePasswordToken token = new UsernamePasswordToken(username,
				password);
		token.setRememberMe(rememberMe);

		try {
			SecurityUtils.getSubject().login(token);
			WebUtils.redirectToSavedRequest(request, response,
					request.getServletPath() + "/dashboard");

		} catch (IncorrectCredentialsException e) {
			response.sendRedirect(getMustacheletPath() + "/login?error="
					+ INCORRECT_CREDENTIALS_ERROR);
		} catch (UnknownAccountException e) {
			response.sendRedirect(getMustacheletPath() + "/login?error="
					+ UNKNOWN_ACCOUNT_ERROR + "&message=" + username);
		} catch (LockedAccountException e) {
			response.sendRedirect(getMustacheletPath() + "/login?error="
					+ LOCKED_ACCOUNT_ERROR);
		} catch (AuthenticationException e) {
			response.sendRedirect(getMustacheletPath() + "/login?error=+"
					+ UNEXPECTED_ERROR + "&message=" + e.getMessage());
		}

		return false;
	}

	public boolean isLoginError() {
		return isIncorrectCredentialsError() || isUnknownAccountError()
				|| isLockedAccountError() || isUnexpectedError();
	}

	public boolean isIncorrectCredentialsError() {
		return INCORRECT_CREDENTIALS_ERROR
				.equals(request.getParameter("error"));
	}

	public boolean isUnknownAccountError() {
		return UNKNOWN_ACCOUNT_ERROR.equals(request.getParameter("error"));
	}

	public boolean isLockedAccountError() {
		return LOCKED_ACCOUNT_ERROR.equals(request.getParameter("error"));
	}

	public boolean isUnexpectedError() {
		return UNEXPECTED_ERROR.equals(request.getParameter("error"));
	}

	public String getMessage() {
		return request.getParameter("message");
	}
}
