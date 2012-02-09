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
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.rest.exception.ApiException;
import eu.scape_project.watch.core.rest.exception.NotFoundException;

/**
 * @author lfaria
 * 
 */
public class PropertyResource extends JavaHelp {

	private static final Logger logger = Logger
			.getLogger(PropertyResource.class);

	private Property getPropertyByNameImpl(String type, String name) {
		// TODO find property of a specific type
		return KBUtils.find(name, Property.class, "name");
	}

	@GET
	@Path("/{type}/{name}")
	@ApiOperation(value = "Find Property by type and name", notes = "")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Property or Entity Type not found") })
	public Response getEntityByName(
			@ApiParam(value = "Name of the Entity Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "Name of the Property", required = true) @PathParam("name") String name)
			throws NotFoundException {
		Property property = getPropertyByNameImpl(type, name);

		if (property != null) {
			return Response.ok().entity(property).build();
		} else {
			throw new NotFoundException("Property id not found: " + name);
		}
	}

	@GET
	@Path("/list")
	@ApiOperation(value = "List all properties", notes = "")
	// @ApiErrors(value = { @ApiError(code = 500, reason =
	// "Error connecting to persistence layer") })
	public Response listProperty() {
		// TODO list properties of a type
		Collection<Property> list = KB.getInstance().getReader()
				.load(Property.class);
		return Response.ok()
				.entity(new GenericEntity<Collection<Property>>(list) {
				}).build();
	}

	@POST
	@Path("/{type}/{name}")
	@ApiOperation(value = "Create Entity Type", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity type does not exist") })
	public Response createProperty(
			@ApiParam(value = "Entity type name (must exist)", required = true) @PathParam("type") String type,
			@ApiParam(value = "Property name (must be unique)", required = true) @PathParam("name") String name,
			@ApiParam(value = "Property description", required = false) String description) {
		// TODO support data type
		logger.debug("Create property name=" + name + " description="
				+ description + " in type=" + type);
		EntityType entitytype = KBUtils.find(type, EntityType.class, "name");

		if (entitytype != null) {
			Property property = new Property(name, description);
			property.save();

			entitytype.getProperties().add(property);
			entitytype.save();
			return Response.ok().entity(property).build();
		} else {
			throw new NotFoundException("Entity type not found: " + type);
		}

	}

	@PUT
	@Path("/{type}/{name}")
	@ApiOperation(value = "Update Property of Type", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "Invalid Property supplied"),
			@ApiError(code = 404, reason = "Entity Type not found") })
	public Response updateProperty(
			@ApiParam(value = "Entity type that owns Property", required = true) @PathParam("type") String type,
			@ApiParam(value = "Name that needs to be deleted", required = true) @PathParam("name") String name,
			@ApiParam(value = "Updated property object", required = true) Property property) {

		EntityType entitytype = KBUtils.find(type, EntityType.class, "name");

		if (entitytype != null) {
			Property original = getPropertyByNameImpl(type, name);
			if (original != null) {
				entitytype.getProperties().remove(original);
				original.delete();

				property.save();
				entitytype.getProperties().add(property);
				entitytype.save();
				return Response.ok().entity(property).build();
			} else {
				throw new NotFoundException("Property not found: " + name);
			}
		} else {
			throw new NotFoundException("Entity type not found: " + type);
		}
	}

	@DELETE
	@Path("/{type}/{name}")
	@ApiOperation(value = "Delete Property", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Property or Entity Type not found") })
	public Response deleteEntity(
			@ApiParam(value = "Entity type that owns Property", required = true) @PathParam("type") String type,
			@ApiParam(value = "The name of the Entity Type to be deleted", required = true) @PathParam("name") String name)
			throws ApiException {
		EntityType entitytype = KBUtils.find(type, EntityType.class, "name");

		if (entitytype != null) {
			Property property = getPropertyByNameImpl(type, name);
			if (property != null) {
				entitytype.getProperties().remove(property);
				property.delete();
				return Response.ok().entity(property).build();
			} else {
				throw new NotFoundException("Property not found: " + name);
			}
		} else {
			throw new NotFoundException("Entity type not found: " + type);
		}
	}

}
