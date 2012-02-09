/**
 * 
 */
package eu.scape_project.watch.core.rest.resource;

import java.util.Collection;

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

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.KBUtils;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.rest.exception.ApiException;
import eu.scape_project.watch.core.rest.exception.NotFoundException;

/**
 * @author lfaria
 * 
 */
public class EntityResource extends JavaHelp {

	private static final Logger logger = Logger.getLogger(EntityResource.class);

	private Entity getEntityByNameImpl(String name) {
		return KBUtils.find(name, Entity.class, "name");
	}

	@GET
	@Path("/{name}")
	@ApiOperation(value = "Find Entity by name", notes = "")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity not found") })
	public Response getEntityByName(
			@ApiParam(value = "Name of the Entity", required = true) @PathParam("name") String name)
			throws NotFoundException {
		Entity entity = getEntityByNameImpl(name);

		if (entity != null) {
			return Response.ok().entity(entity).build();
		} else {
			throw new NotFoundException("Entity not found: " + name);
		}
	}

	@GET
	@Path("/list")
	@ApiOperation(value = "List all entities", notes = "")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Error connecting to persistence layer") })
	public Response listEntity() throws ApiException {
		Collection<Entity> list = KB.getInstance().getReader()
				.load(Entity.class);
		return Response.ok()
				.entity(new GenericEntity<Collection<Entity>>(list) {
				}).build();
	}

	@POST
	@Path("/{name}")
	@ApiOperation(value = "Create Entity", notes = "This can only be done by a logged user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity type not found") })
	public Response createEntity(
			@ApiParam(value = "Entity name (must be unique)", required = true) @PathParam("name") String name,
			@ApiParam(value = "Entity Type (must exist)", required = true) String type)
			throws ApiException {

		EntityType entitytype = KBUtils.find(type, EntityType.class, "name");

		if (entitytype != null) {
			Entity entity = new Entity(entitytype, name);
			entity.save();
			return Response.ok().entity(entity).build();
		} else {
			throw new NotFoundException("Entity type not found: " + type);
		}

	}

	@PUT
	@Path("/{name}")
	@ApiOperation(value = "Update Entity", notes = "This can only be done by a logged user (TODO)")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "Invalid Entity supplied"),
			@ApiError(code = 404, reason = "Entity not found") })
	public Response updateEntity(
			@ApiParam(value = "Name that need to be deleted", required = true) @PathParam("name") String name,
			@ApiParam(value = "Updated Entity object", required = true) Entity entity) {
		Entity original = getEntityByNameImpl(name);
		if (original != null) {
			original.delete();
			entity.save();
			return Response.ok().entity(entity).build();
		} else {
			throw new NotFoundException("Entity type not found: " + name);
		}
	}

	@DELETE
	@Path("/{name}")
	@ApiOperation(value = "Delete Entity", notes = "This can only be done by a logged user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity not found") })
	public Response deleteEntity(
			@ApiParam(value = "The name of the Entity to be deleted", required = true) @PathParam("name") String name)
			throws ApiException {
		logger.info("deleting entity name: " + name);
		Entity entity = getEntityByNameImpl(name);
		if (entity != null) {
			entity.delete();
			return Response.ok().entity(entity).build();
		} else {
			throw new NotFoundException("Entity type not found: " + name);
		}
	}

}
