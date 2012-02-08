/**
 * 
 */
package eu.scape_project.watch.core.rest.resource;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

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
//		EntityType entitytype = KB.getInstance().findByProperty(
//				EntityType.class, "name", name);
//		// do we need a null check here?
//		return entitytype;
	  return null;
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
	@ApiErrors(value = { @ApiError(code = 500, reason = "Error connecting to persistence layer") })
	public Response listEntity() throws ApiException {
		Response response;
//		try {
//			Query query = KB
//					.getInstance()
//					.getEntityManager()
//					.createNativeQuery("where {?result rdf:type kb:EntityType}");
//			query.setHint(RdfQuery.HINT_ENTITY_CLASS, EntityType.class);
//			List<EntityType> list = query.getResultList();
//			response = Response.ok().entity(list).build();
//		} catch (PersistenceException e) {
//			e.printStackTrace();
//			throw new ApiException(500, e.getMessage());
//		}
//		return response;
		return null;
	}

	@POST
	@ApiOperation(value = "Create Entity Type", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unexpected internal error") })
	public Response createEntityType(
			@ApiParam(value = "Entity Type object", required = true) EntityType entitytype)
			throws ApiException {
		logger.info("creating entity name: " + entitytype.getName());
		try {
//			KB.getInstance().getEntityManager().persist(entitytype);
			return Response.ok().entity(entitytype).build();
		} catch (Throwable e) {
			logger.error("Unexpected error", e);
			throw new ApiException(500, e);
		}
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
//		EntityType mergedEntityType = KB.getInstance().getEntityManager()
//				.merge(entitytype);
//		return Response.ok().entity(mergedEntityType).build();
	  return null;
	}

	@DELETE
	@Path("/{name}")
	@ApiOperation(value = "Delete Entity Type", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = {
			@ApiError(code = 404, reason = "Entity Type not found"),
			@ApiError(code = 500, reason = "Unexpected internal error") })
	public Response deleteEntity(
			@ApiParam(value = "The name of the Entity Type to be deleted", required = true) @PathParam("name") String name)
			throws ApiException {
		logger.info("deleting entity type name: " + name);
		try {
			EntityType entitytype = getEntityTypeByNameImpl(name);
//			KB.getInstance().getEntityManager().remove(entitytype);
			return Response.ok().entity(entitytype).build();
		} catch (Throwable e) {
			logger.error("Unexpected error", e);
			throw new ApiException(500, e);
		}
	}

}
