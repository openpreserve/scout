/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.utils.exception.BadRequestException;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * 
 * REST API for {@link SourceAdaptor} management.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class SourceAdaptorResource extends JavaHelp {

  /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(SourceAdaptorResource.class);

  /**
   * The servlet context to get context attributes.
   */
  @Context
  private ServletContext context;

  /**
   * Get a {@link SourceAdaptor}.
   * 
   * @param instance
   *          The unique name for the source adaptor instance.
   * @return the source adaptor or not found error.
   */
  @GET
  @Path("/{instance}")
  @ApiOperation(value = "Get a source adaptor based on the instance unique identifier", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Source adaptor not found")})
  public Response getPluginByName(
    @ApiParam(value = "Source adaptor instance UID", required = true) @PathParam("instance") final String instance) {

    final AdaptorManager manager = ContextUtil.getAdaptorManager(context);

    final SourceAdaptor adaptor = manager.getSourceAdaptor(instance);

    if (adaptor != null) {
      return Response.ok().entity(adaptor).build();
    } else {
      throw new NotFoundException("Source adaptor not found: " + instance);
    }

  }

  /**
   * List all available source adaptors.
   * 
   * @param active
   *          Optional filter by active state.
   * 
   * @return A list of {@link SourceAdaptor}.
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all source adaptors", notes = "")
  public Response listSourceAdaptor(
    @ApiParam(value = "Source adaptor active state", required = false) @QueryParam("active") final Boolean active) {

    List<SourceAdaptor> sourceAdaptors;

    if (active == null) {
      final int count = DAO.SOURCE_ADAPTOR.countAll();
      sourceAdaptors = DAO.SOURCE_ADAPTOR.queryAll(0, count);
    } else {
      final int count = DAO.SOURCE_ADAPTOR.countActive(active);
      sourceAdaptors = DAO.SOURCE_ADAPTOR.queryActive(active, 0, count);
    }

    return Response.ok().entity(new GenericEntity<Collection<SourceAdaptor>>(sourceAdaptors) {
    }).build();
  }

  /**
   * Create a new {@link Source Adaptor}.
   * 
   * @param name
   *          The related plug-in name
   * @param version
   *          The related plug-in version
   * @param instance
   *          The adaptor instance unique identifier
   * @param sourceName
   *          The name of the related source
   * @return The created source adaptor.
   */
  @POST
  @Path("/new")
  @ApiOperation(value = "Create and register a new Source Adaptor", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Related plug-in not found"),
    @ApiError(code = BadRequestException.CODE, reason = "Instance already exists")})
  public Response createSourceAdaptor(
    @ApiParam(value = "Related plug-in name", required = true) @QueryParam("name") final String name,
    @ApiParam(value = "Related plug-in version", required = true) @QueryParam("version") final String version,
    @ApiParam(value = "Adaptor instance unique identifier", required = true) @QueryParam("instance") final String instance,
    @ApiParam(value = "Related source name", required = true) @QueryParam("source") final String sourceName) {

    final AdaptorManager manager = ContextUtil.getAdaptorManager(context);
    final Source source = DAO.SOURCE.findById(sourceName);
    final SourceAdaptor adaptor = manager.createAdaptor(name, version, instance, source);
    // TODO catch exception related to plug-in not found and instance already
    // exists to send correct service exceptions.

    return Response.ok().entity(adaptor).build();
  }

  /**
   * Update an existing {@link Source Adaptor}.
   * 
   * @param updatedSourceAdaptor
   *          The updated source adaptor
   * @return The updated source adaptor.
   */
  @PUT
  @Path("/update")
  @ApiOperation(value = "Update an existing Source Adaptor", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Source Adaptor not found")})
  public Response updateSourceAdaptor(
    @ApiParam(value = "Updated source adaptor", required = true) final SourceAdaptor updatedSourceAdaptor) {

    final AdaptorManager manager = ContextUtil.getAdaptorManager(context);
    final SourceAdaptor sourceAdaptor = manager.getSourceAdaptor(updatedSourceAdaptor.getInstance());

    if (sourceAdaptor != null) {
      manager.updateSourceAdaptor(updatedSourceAdaptor);

      return Response.ok().entity(updatedSourceAdaptor).build();
    } else {
      throw new NotFoundException("Source adaptor not found: " + updatedSourceAdaptor.getId());
    }
  }

  // TODO get a source adaptor based on plugin name, and based source.

}
