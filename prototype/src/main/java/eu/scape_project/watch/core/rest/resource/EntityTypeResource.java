/**
 * 
 */
package eu.scape_project.watch.core.rest.resource;

import java.util.Collection;
import java.util.LinkedList;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import thewebsemantic.Sparql;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.rest.exception.ApiException;
import eu.scape_project.watch.core.rest.exception.NotFoundException;

/**
 * @author lfaria
 * 
 */
public class EntityTypeResource extends JavaHelp {

	private static final Logger logger = Logger
			.getLogger(EntityTypeResource.class);

	private EntityType getEntityTypeByNameImpl(String name) {
		EntityType ret = null;
		String query = "SELECT ?s WHERE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://watch.scape-project.eu/EntityType> ."
				+ " ?s <http://watch.scape-project.eu/name> \"" + name + "\"}";
		LinkedList<EntityType> results = Sparql.exec(KB.getInstance()
				.getReader(), EntityType.class, query, new QuerySolutionMap(),
				0, 1);
		if (results.size() > 1) {
			logger.warn("Got more than one result when getting an entity type by name, name="
					+ name);
		}
		if (results.size() > 0) {
			ret = results.getFirst();
		}

		return ret;
	}

	@GET
	@Path("/{name}")
	@ApiOperation(value = "Find Entity Type by name", notes = "")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity Type not found") })
	public Response getEntityByName(
			@ApiParam(value = "Name of the Entity Type", required = true) @PathParam("name") String name)
			throws NotFoundException {
		EntityType entitytype = getEntityTypeByNameImpl(name);

		if (entitytype != null) {
			return Response.ok().entity(entitytype).build();
		} else {
			throw new NotFoundException(404, "Entity Type id not found");
		}
	}

	@GET
	@Path("/list")
	@ApiOperation(value = "List all entity types", notes = "")
	// @ApiErrors(value = { @ApiError(code = 500, reason =
	// "Error connecting to persistence layer") })
	public Response listEntity() {
		Collection<EntityType> list = KB.getInstance().getReader()
				.load(EntityType.class);
		return Response.ok().entity(list).build();
	}

	@POST
	@Path("/{name}")
	@ApiOperation(value = "Create Entity Type", notes = "This can only be done by an admin user (TODO)")
	// @ApiErrors(value = { @ApiError(code = 500, reason =
	// "Unexpected internal error") })
	public Response createEntityType(
			@ApiParam(value = "Entity type name (must be unique)", required = true)  @PathParam("name") String name,
			@ApiParam(value = "Entity type description", required = false) String description)
			throws ApiException {
		logger.info("creating entity name: " + name);
		KB.getInstance();
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
		EntityType original = getEntityTypeByNameImpl(entitytype.getName());
		if (original != null) {
			original.delete();
			entitytype.save();
			return Response.ok().entity(entitytype).build();
		} else {
			throw new NotFoundException(404, "Entity type '"
					+ entitytype.getName() + "' not found");
		}
	}

	@DELETE
	@Path("/{name}")
	@ApiOperation(value = "Delete Entity Type", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity Type not found") })
	public Response deleteEntity(
			@ApiParam(value = "The name of the Entity Type to be deleted", required = true) @PathParam("name") String name)
			throws ApiException {
		logger.info("deleting entity type name: " + name);

		EntityType entitytype = getEntityTypeByNameImpl(name);
		if (entitytype != null) {
			entitytype.delete();
			return Response.ok().entity(entitytype).build();
		} else {
			throw new NotFoundException(404, "Entity type '" + name
					+ "' not found");
		}
	}

}
