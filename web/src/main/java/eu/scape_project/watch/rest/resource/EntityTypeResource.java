/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.utils.exception.NotFoundException;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import thewebsemantic.binding.Jenabean;

/**
 * 
 * REST API for {@link EntityType} operations.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class EntityTypeResource extends JavaHelp {

  /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(EntityTypeResource.class);

  /**
   * Get an existing {@link EntityType}.
   * 
   * @param name
   *          The entity type name.
   * @return The entity type or throws {@link NotFoundException} if not found.
   */
  @GET
  @Path("/{name}")
  @ApiOperation(value = "Find Entity Type by name", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Entity Type not found")})
  public Response getEntityTypeByName(
    @ApiParam(value = "Name of the Entity Type", required = true) @PathParam("name") final String name) {
    final EntityType entitytype = DAO.ENTITY_TYPE.findById(name);

    if (entitytype != null) {
      return Response.ok().entity(entitytype).build();
    } else {
      throw new NotFoundException("Entity Type id not found: " + name);
    }
  }

  /**
   * List all entity types in the KB.
   * 
   * @return The complete list of {@link EntityType}.
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all entity types", notes = "")
  public Response listEntityType(
    @ApiParam(value = "Index of first item to retrieve", required = true) @QueryParam("start") final int start,
    @ApiParam(value = "Maximum number of items to retrieve", required = true) @QueryParam("max") final int max) {
    final Collection<EntityType> list = DAO.ENTITY_TYPE.query("", start, max);
    return Response.ok().entity(new GenericEntity<Collection<EntityType>>(list) {
    }).build();
  }

  /**
   * Create a new {@link EntityType}.
   * 
   * @param name
   *          The entity type unique name
   * @param description
   *          A descriptive information about the entity type.
   * @return The created entity type.
   */
  @POST
  @Path("/{name}")
  @ApiOperation(value = "Create Entity Type", notes = "This can only be done by an admin user (TODO)")
  public Response createEntityType(
    @ApiParam(value = "Entity type name (must be unique)", required = true) @PathParam("name") final String name,
    @ApiParam(value = "Entity type description", required = false) final String description) {
    LOG.info("creating entity name: " + name);
    final EntityType entitytype = new EntityType(name, description);
    DAO.save(entitytype);
    return Response.ok().entity(entitytype).build();

  }

  /**
   * Update an existing {@link EntityType}.
   * 
   * @param name
   *          The existing {@link EntityType} name.
   * @param entitytype
   *          The new entity type that should replace the existing one
   * @return The new entity type merged into the KB
   */
  @PUT
  @Path("/{name}")
  @ApiOperation(value = "Update Entity Type", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Entity Type not found")})
  public Response updateEntityType(
    @ApiParam(value = "Name that need to be deleted", required = true) @PathParam("name") final String name,
    @ApiParam(value = "Updated Entity Type object", required = true) final EntityType entitytype) {
    final EntityType original = DAO.ENTITY_TYPE.findById(name);
    if (original != null) {
      DAO.save(entitytype);
      return Response.ok().entity(entitytype).build();
    } else {
      throw new NotFoundException("Entity type not found: " + name);
    }
  }

  /**
   * Delete an existing entity type.
   * 
   * @param name
   *          The name of the {@link EntityType} to delete.
   * @return The deleted {@link EntityType} or throws {@link NotFoundException}
   *         if not found
   */
  @DELETE
  @Path("/{name}")
  @ApiOperation(value = "Delete Entity Type", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Entity Type not found")})
  public Response deleteEntityType(
    @ApiParam(value = "The name of the Entity Type to be deleted", required = true) @PathParam("name") final String name) {
    LOG.info("deleting entity type name: " + name);

    final EntityType entitytype = DAO.ENTITY_TYPE.findById(name);
    if (entitytype != null) {
      DAO.delete(entitytype);
      return Response.ok().entity(entitytype).build();
    } else {
      throw new NotFoundException("Entity type not found: " + name);
    }
  }

}
