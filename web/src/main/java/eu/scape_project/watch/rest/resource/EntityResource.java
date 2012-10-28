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
import eu.scape_project.watch.dao.EntityDAO;
import eu.scape_project.watch.domain.Entity;
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

/**
 * 
 * REST API for {@link Entity} operations.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class EntityResource extends JavaHelp {

  /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(EntityResource.class);

  /**
   * Get a {@link Entity} by its unique name. REST interface for
   * {@link EntityDAO#findById(String)}.
   * 
   * @param name
   *          The name of the Entity
   * @return The Entity object with that name or throws
   *         {@link NotFoundException} if none found.
   */
  @GET
  @Path("/{type}/{name}")
  @ApiOperation(value = "Find Entity by name", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Entity not found")})
  public Response getEntityByName(
    @ApiParam(value = "Name of the Entity", required = true) @PathParam("type") final String type,
    @ApiParam(value = "Name of the Entity", required = true) @PathParam("name") final String name) {

    final Entity entity = DAO.ENTITY.findById(type, name);

    if (entity != null) {
      return Response.ok().entity(entity).build();
    } else {
      throw new NotFoundException("Entity not found: " + name);
    }
  }

  /**
   * Get the list of Entities of a defined {@link EntityType}.
   * 
   * @param type
   *          The name of the {@link EntityType}
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return The list of entities filtered by the above constraints
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List entities", notes = "")
  public Response listEntityOfType(
    @ApiParam(value = "Entity type", required = true) @QueryParam("type") final String type,
    @ApiParam(value = "Index of first item to retrieve", required = true) @QueryParam("start") final int start,
    @ApiParam(value = "Maximum number of items to retrieve", required = true) @QueryParam("max") final int max) {
    final Collection<Entity> list = DAO.ENTITY.listWithType(type, start, max);
    return Response.ok().entity(new GenericEntity<Collection<Entity>>(list) {
    }).build();
  }

  /**
   * Create a new {@link Entity}.
   * 
   * @param name
   *          The name of the entity
   * @param type
   *          the name of the {@link EntityType} this entity belongs to
   * @return The created Entity or throws {@link NotFoundException} if the type
   *         is not found.
   */
  @POST
  @Path("/new")
  @ApiOperation(value = "Create Entity", notes = "This can only be done by a logged user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Entity type not found")})
  public Response createEntity(@ApiParam(value = "Entity", required = true) final Entity entity) {

    final EntityType entitytype = DAO.ENTITY_TYPE.findById(entity.getType().getId());

    if (entitytype != null) {
      final Entity commitedEntity = DAO.ENTITY.save(entity);
      return Response.ok().entity(commitedEntity).build();
    } else {
      throw new NotFoundException("Entity type not found: " + entity.getType());
    }

  }

  /**
   * Update an existing entity.
   * 
   * @param name
   *          The name of the existing entity
   * @param entity
   *          The new entity that will replace the existing one.
   * @return The entity merged into the KB.
   */
  @PUT
  @Path("/{type}/{name}")
  @ApiOperation(value = "Update Entity", notes = "This can only be done by a logged user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Entity not found")})
  public Response updateEntity(
    @ApiParam(value = "Type of the entity to be updated", required = true) @PathParam("type") final String type,
    @ApiParam(value = "Name of the entity to be updated", required = true) @PathParam("name") final String name,
    @ApiParam(value = "Updated Entity object", required = true) final Entity entity) {
    final Entity original = DAO.ENTITY.findById(type, name);
    if (original != null) {
      original.delete();
      entity.save();
      return Response.ok().entity(entity).build();
    } else {
      throw new NotFoundException("Entity not found: " + name);
    }
  }

  /**
   * Delete an existing entity.
   * 
   * @param name
   *          the name of the entity
   * @return The deleted entity or throws {@link NotFoundException} if the
   *         entity to delete is not found
   * 
   */
  @DELETE
  @Path("/{type}/{name}")
  @ApiOperation(value = "Delete Entity", notes = "This can only be done by a logged user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Entity not found")})
  public Response deleteEntity(
    @ApiParam(value = "The type of the Entity to be deleted", required = true) @PathParam("type") final String type,
    @ApiParam(value = "The name of the Entity to be deleted", required = true) @PathParam("name") final String name) {
    LOG.info("deleting entity name: " + name);
    final Entity entity = DAO.ENTITY.findById(type, name);
    if (entity != null) {
      entity.delete();
      return Response.ok().entity(entity).build();
    } else {
      throw new NotFoundException("Entity type not found: " + name);
    }
  }

}
