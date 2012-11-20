/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * 
 * REST API for {@link Source} operations.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class SourceResource extends JavaHelp {

  /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(SourceResource.class);

  /**
   * Get an existing {@link Source}.
   * 
   * @param name
   *          The source name.
   * @return The source or throws {@link NotFoundException} if not found.
   */
  @GET
  @Path("/{name}")
  @ApiOperation(value = "Find source by name", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Source not found")})
  public Response getSourceByName(
    @ApiParam(value = "Name of the source", required = true) @PathParam("name") final String name) {
    final Source source = DAO.SOURCE.findByName(name);

    if (source != null) {
      return Response.ok().entity(source).build();
    } else {
      throw new NotFoundException("Source not found: " + name);
    }
  }

  /**
   * List all sources in the KB.
   * 
   * @return The complete list of sources.
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all sources", notes = "")
  public Response listEntityType() {
    final int count = DAO.SOURCE.count("");
    final List<Source> sources = DAO.SOURCE.query("", 0, count);
    return Response.ok().entity(new GenericEntity<Collection<Source>>(sources) {
    }).build();
  }

  /**
   * Create a new {@link Source}.
   * 
   * @param source
   *          the source to be created.
   * 
   * @return The committed source.
   */
  @POST
  @Path("/new")
  @ApiOperation(value = "Create a new source", notes = "This can only be done by an admin user (TODO)")
  public Response createEntityType(@ApiParam(value = "The new source", required = true) final Source source) {
    DAO.save(source);
    return Response.ok().entity(source).build();
  }

  /**
   * Update an existing {@link Source}.
   * 
   * @param source
   *          The updated source
   * @return The the commited updated source.
   */
  @PUT
  @Path("/update")
  @ApiOperation(value = "Update source", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Source not found")})
  public Response updateEntityType(@ApiParam(value = "Updated Source", required = true) final Source source) {
    final Source original = DAO.SOURCE.findByName(source.getName());
    if (original != null) {
      DAO.SOURCE.save(source);
      return Response.ok().entity(source).build();
    } else {
      throw new NotFoundException("Source not found: " + source.getName());
    }
  }

  /**
   * Delete an existing source.
   * 
   * @param name
   *          The name of the {@link Source} to delete.
   * @return The deleted {@link Source} or throws {@link NotFoundException} if
   *         not found
   */
  @DELETE
  @Path("/{name}")
  @ApiOperation(value = "Delete source", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Source not found")})
  public Response deleteEntityType(
    @ApiParam(value = "The source name", required = true) @PathParam("name") final String name) {
    final Source source = DAO.SOURCE.findByName(name);
    if (source != null) {
      DAO.delete(source);
      return Response.ok().entity(source).build();
    } else {
      throw new NotFoundException("Source not found: " + name);
    }
  }

}
