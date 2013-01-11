/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.QuestionTemplate;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * REST API for {@link QuestionTemplate} operations.
 * 
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class QuestionTemplateResource extends JavaHelp {

  /**
   * Get an existing {@link QuestionTemplate}.
   * 
   * @param questionTemplateId
   *          The {@link QuestionTemplate} id
   * @return The {@link QuestionTemplate} or throws {@link NotFoundException} if
   *         not found.
   */
  @GET
  @Path("/{id}")
  @ApiOperation(value = "Find question template by id", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Question template not found")})
  public Response getById(@ApiParam(value = "Question template id", required = true) @PathParam("id") final String id) {

    final QuestionTemplate template = DAO.QUESTION_TEMPLATE.findById(id);

    if (template != null) {
      return Response.ok().entity(template).build();
    } else {
      throw new NotFoundException("Request not found: " + id);
    }
  }

  /**
   * Create a new {@link QuestionTemplate}.
   * 
   * @param template
   *          The question temaplte to save
   * @return The created question template
   */
  @POST
  @Path("/new")
  @ApiOperation(value = "Create question template", notes = "This can only be done by a logged user (TODO)")
  public Response create(@ApiParam(value = "Question template", required = true) final QuestionTemplate template) {
    final QuestionTemplate commited = DAO.QUESTION_TEMPLATE.save(template);
    return Response.ok().entity(commited).build();
  }

  /**
   * List all.
   * 
   * @param start
   *          The index of the first item to retrieve
   * 
   * @param max
   *          The maximum number of items to retrieve
   * 
   * @return A list filtered by the above constraints.
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all question templates", notes = "")
  public Response list(
    @ApiParam(value = "Index of first item to retrieve", required = true) @QueryParam("start") final int start,
    @ApiParam(value = "Maximum number of items to retrieve", required = true) @QueryParam("max") final int max) {
    final Collection<QuestionTemplate> list = DAO.QUESTION_TEMPLATE.list(start, max);
    return Response.ok().entity(new GenericEntity<Collection<QuestionTemplate>>(list) {
    }).build();
  }

  @DELETE
  @Path("/{id}")
  @ApiOperation(value = "Delete by id", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Resource not found")})
  public Response deleteById(@ApiParam(value = "Id", required = true) @PathParam("id") final String id) {

    final QuestionTemplate template = DAO.QUESTION_TEMPLATE.findById(id);

    if (template != null) {
      DAO.delete(template);
      return Response.ok().entity(template).build();
    } else {
      throw new NotFoundException("Resource not found: " + id);
    }
  }

}
