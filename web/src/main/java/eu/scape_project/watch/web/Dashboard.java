package eu.scape_project.watch.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Objective;
import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.policy.PolicyModel;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/dashboard")
@TemplateSource("dashboard")
public class Dashboard extends TemplateContext {

	@Inject
	private HttpServletRequest request;

	public List<Objective> getObjectives() {
		final ServletContext context = this.request.getSession()
				.getServletContext();
		final PolicyModel policyModel = ContextUtil.getPolicyModel(context);

		List<Objective> ret = null;
		if (policyModel != null) {
			ret = policyModel.listAllObjectives();
		} else {
			ret = new ArrayList<Objective>();
		}
		return ret;

	}

	public List<AsyncRequest> getRequests() {
		return DAO.ASYNC_REQUEST.list(0, getPageSize());
	}

}
