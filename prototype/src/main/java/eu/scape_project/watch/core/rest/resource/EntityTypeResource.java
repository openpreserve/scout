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
import eu.scape_project.watch.core.dao.EntityTypeDAO;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.rest.exception.ApiException;
import eu.scape_project.watch.core.rest.exception.NotFoundException;

/**
 * @author lfaria
 * 
 */
public class EntityTypeResource extends JavaHelp {

	static {
		// Binding KB with Jenabean
		KB.getInstance();
	}

	private static final Logger logger = Logger
			.getLogger(EntityTypeResource.class);

	@GET
	@Path("/{name}")
	@ApiOperation(value = "Find Entity Type by name", notes = "")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity Type not found") })
	public Response getEntityTypeByName(
			@ApiParam(value = "Name of the Entity Type", required = true) @PathParam("name") String name)
			throws NotFoundException {
		EntityType entitytype = EntityTypeDAO.findById(name);

		if (entitytype != null) {
			return Response.ok().entity(entitytype).build();
		} else {
			throw new NotFoundException("Entity Type id not found: " + name);
		}
	}

	@GET
	@Path("/list")
	@ApiOperation(value = "List all entity types", notes = "")
	public Response listEntityType() {
		Collection<EntityType> list = KB.getInstance().getReader()
				.load(EntityType.class);
		return Response.ok()
				.entity(new GenericEntity<Collection<EntityType>>(list) {
				}).build();
	}

	@POST
	@Path("/{name}")
	@ApiOperation(value = "Create Entity Type", notes = "This can only be done by an admin user (TODO)")
	public Response createEntityType(
			@ApiParam(value = "Entity type name (must be unique)", required = true) @PathParam("name") String name,
			@ApiParam(value = "Entity type description", required = false) String description)
			throws ApiException {
		logger.info("creating entity name: " + name);
		EntityType entitytype = new EntityType(name, description);
		entitytype.save();
		return Response.ok().entity(entitytype).build();

	}

	@PUT
	@Path("/{name}")
	@ApiOperation(value = "Update Entity Type", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "Invalid Entity Type supplied"),
			@ApiError(code = 404, reason = "Entity Type not found") })
	public Response updateEntityType(
			@ApiParam(value = "Name that need to be deleted", required = true) @PathParam("name") String name,
			@ApiParam(value = "Updated Entity Type object", required = true) EntityType entitytype) {
		EntityType original = EntityTypeDAO.findById(name);
		if (original != null) {
			original.delete();
			entitytype.save();
			return Response.ok().entity(entitytype).build();
		} else {
			throw new NotFoundException("Entity type not found: " + name);
		}
	}

	@DELETE
	@Path("/{name}")
	@ApiOperation(value = "Delete Entity Type", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity Type not found") })
	public Response deleteEntityType(
			@ApiParam(value = "The name of the Entity Type to be deleted", required = true) @PathParam("name") String name)
			throws ApiException {
		logger.info("deleting entity type name: " + name);

		EntityType entitytype = EntityTypeDAO.findById(name);
		if (entitytype != null) {
			entitytype.delete();
			return Response.ok().entity(entitytype).build();
		} else {
			throw new NotFoundException("Entity type not found: " + name);
		}
	}

}
