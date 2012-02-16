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
import eu.scape_project.watch.core.dao.EntityTypeDAO;
import eu.scape_project.watch.core.dao.PropertyDAO;
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

	@GET
	@Path("/{type}/{name}")
	@ApiOperation(value = "Find Property by type and name", notes = "")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Property or Entity Type not found") })
	public Response getPropertyByName(
			@ApiParam(value = "Name of the Entity Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "Name of the Property", required = true) @PathParam("name") String name)
			throws NotFoundException {
		Property property = PropertyDAO.findByEntityTypeAndName(type, name);

		if (property != null) {
			return Response.ok().entity(property).build();
		} else {
			throw new NotFoundException("Property id not found: " + name);
		}
	}

	@GET
	@Path("/list")
	@ApiOperation(value = "List all properties", notes = "")
	public Response listProperty() {
		Collection<Property> list = KB.getInstance().getReader()
				.load(Property.class);
		return Response.ok()
				.entity(new GenericEntity<Collection<Property>>(list) {
				}).build();
	}

	@GET
	@Path("/list/{type}/{start}/{max}")
	@ApiOperation(value = "List properties of a type", notes = "")
	public Response listEntityOfType(
			@ApiParam(value = "Entity type", required = true) @PathParam("type") String type,
			@ApiParam(value = "Index of first item to retrieve", required = true, defaultValue = "0") @PathParam("start") int start,
			@ApiParam(value = "Maximum number of items to retrieve", required = true, defaultValue = "100") @PathParam("max") int max)
			throws ApiException {
		Collection<Property> list = PropertyDAO.listWithType(type, start, max);
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
		EntityType entityType = EntityTypeDAO.findById(type);

		if (entityType != null) {
			Property property = new Property(entityType, name, description);
			property.save();
			KBUtils.printStatements();
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
			@ApiError(code = 404, reason = "Property not found") })
	public Response updateProperty(
			@ApiParam(value = "Entity type that owns Property", required = true) @PathParam("type") String type,
			@ApiParam(value = "Name that needs to be deleted", required = true) @PathParam("name") String name,
			@ApiParam(value = "Updated property object", required = true) Property property) {
		Property original = PropertyDAO.findByEntityTypeAndName(type, name);
		if (original != null) {
			original.delete();
			property.save();
			return Response.ok().entity(property).build();
		} else {
			throw new NotFoundException("Property not found: " + name);
		}
	}

	@DELETE
	@Path("/{type}/{name}")
	@ApiOperation(value = "Delete property", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Property or Entity Type not found") })
	public Response deleteProperty(
			@ApiParam(value = "Entity type that owns property", required = true) @PathParam("type") String type,
			@ApiParam(value = "The name of the property to be deleted", required = true) @PathParam("name") String name)
			throws ApiException {
		Property property = PropertyDAO.findByEntityTypeAndName(type, name);

		if (property != null) {
			property.delete();
			return Response.ok().entity(property).build();
		} else {
			throw new NotFoundException("Property not found: " + name);
		}

	}

}
