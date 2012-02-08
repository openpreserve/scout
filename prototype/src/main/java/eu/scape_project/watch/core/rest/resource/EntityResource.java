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
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.rest.exception.ApiException;
import eu.scape_project.watch.core.rest.exception.NotFoundException;

/**
 * @author lfaria
 * 
 */
public class EntityResource extends JavaHelp {

	private static final Logger logger = Logger.getLogger(EntityResource.class);

	private Entity getEntityByNameImpl(String name) {
//		Entity entity = KB.getInstance().findByProperty(Entity.class, "name", name);
////		do we need a null check here?
//		return entity;
	  return null;
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
			//GenericEntity<Entity> t = new GenericEntity<Entity>(entity, Entity.class);
			return Response.ok().entity(entity).build();
		} else {
			throw new NotFoundException(404, "Entity not found");
		}
	}

	@GET
	@Path("/list")
	@ApiOperation(value = "List all entities", notes = "")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Error connecting to persistence layer") })
	public Response listEntity() throws ApiException {
		Response response;
//		try {
//			Query query = KB.getInstance().getEntityManager()
//					.createNativeQuery("where {?result rdf:type kb:Entity}");
//			query.setHint(RdfQuery.HINT_ENTITY_CLASS, Entity.class);
//			List<Entity> list = query.getResultList();
//			response = Response.ok().entity(list).build();
//		} catch (PersistenceException e) {
//			e.printStackTrace();
//			throw new ApiException(500, e.getMessage());
//		}
//		return response;
		
		
		
		return null;
	}

	@POST
	@ApiOperation(value = "Create Entity", notes = "This can only be done by a logged user (TODO)")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unexpected internal error") })
	public Response createEntity(
			@ApiParam(value = "Entity object", required = true) Entity entity)
			throws ApiException {
		logger.info("creating entity name: " + entity.getName());
		try {
//			KB.getInstance().getEntityManager().persist(entity);
			
			entity.save();
			return Response.ok().entity(entity).build();
		} catch (Throwable e) {
			logger.error("Unexpected error", e);
			throw new ApiException(500, e);
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
//		Entity mergedEntity = KB.getInstance().getEntityManager().merge(entity);
//		return Response.ok().entity(mergedEntity).build();
	  return null;
	}

	@DELETE
	@Path("/{name}")
	@ApiOperation(value = "Delete Entity", notes = "This can only be done by a logged user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity not found"),
			@ApiError(code = 500, reason = "Unexpected internal error") })
	public Response deleteEntity(
			@ApiParam(value = "The name of the Entity to be deleted", required = true) @PathParam("name") String name)
			throws ApiException {
		logger.info("deleting entity name: " + name);
		try {
			Entity entity = getEntityByNameImpl(name);
//			KB.getInstance().getEntityManager().remove(entity);
			return Response.ok().entity(entity).build();
		} catch (Throwable e) {
			logger.error("Unexpected error", e);
			throw new ApiException(500, e);
		}
	}

}
