package eu.scape_project.watch.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.policy.PolicyModel;
import eu.scape_project.watch.web.annotations.Controller;
import eu.scape_project.watch.web.annotations.HttpMethod;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/upload/policy/new")
@TemplateSource("uploadObjective")
@HttpMethod({ HttpMethod.Type.GET, HttpMethod.Type.POST })
public class UploadObjective extends TemplateContext {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private HttpServletResponse response;

	@Inject
	private HttpServletRequest request;

	private String uploadPath = System.getProperty("user.home")
			+ File.separator + ".scout" + File.separator + "policies";

	public UploadObjective() {
		File uploadDir = new File(this.uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
	}

	@Controller(HttpMethod.Type.POST)
	boolean redirectPostData() throws IOException, ServletException {

		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new IllegalArgumentException(
					"Request is not multipart, please 'multipart/form-data' enctype for your form.");
		}

		final ServletContext context = this.request.getSession()
				.getServletContext();
		final PolicyModel policyModel = ContextUtil.getPolicyModel(context);
		final ServletFileUpload uploadHandler = new ServletFileUpload(
				new DiskFileItemFactory());

		try {
			List<FileItem> items = uploadHandler.parseRequest(this.request);
			for (FileItem item : items) {
				if (!item.isFormField()) {
					File file = new File(this.uploadPath, item.getName());
					item.write(file);

					// process objectives
					policyModel.deleteAllObjectives();
					boolean loaded = policyModel.loadPolicies(file
							.getAbsolutePath());
					log.info("Loaded file {}: {}", file.getAbsolutePath(),
							loaded + "");
					if (loaded) {
						response.sendRedirect(getMustacheletPath()
								+ "/dashboard");
					} else {
						response.sendRedirect(getMustacheletPath()
								+ "/error/400?message=Could not load the selected policy&back=/upload/policy/new");
					}
				}
			}
		} catch (FileUploadException e) {
			response.sendRedirect(getMustacheletPath()
					+ "/error/500?message=Could not load the selected policy&back=/upload/policy/new&details="
					+ e.getMessage());

			response.sendError(500,
					"Could not upload the file, " + e.getMessage());
		} catch (Exception e) {
			response.sendRedirect(getMustacheletPath()
					+ "/error/500?message=An unexpected error occured when trying to load the policy&back=/upload/policy/new&details="
					+ e.getMessage());
		}

		return false;
	}

}
