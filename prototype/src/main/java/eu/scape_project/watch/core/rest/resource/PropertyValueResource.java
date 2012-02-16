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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.dao.EntityDAO;
import eu.scape_project.watch.core.dao.PropertyDAO;
import eu.scape_project.watch.core.dao.PropertyValueDAO;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;
import eu.scape_project.watch.core.rest.exception.ApiException;
import eu.scape_project.watch.core.rest.exception.NotFoundException;

/**
 * @author lfaria
 * 
 */
public class PropertyValueResource extends JavaHelp {

	private static final Logger LOG = LoggerFactory
			.getLogger(PropertyValueResource.class);

	@GET
	@Path("/{entity}/{property}")
	@ApiOperation(value = "Find property value by entity and property", notes = "")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Property value not found") })
	public Response getPropertyValueByName(
			@ApiParam(value = "Name of the entity type", required = true) @PathParam("entity") String entityName,
			@ApiParam(value = "Name of the property", required = true) @PathParam("property") String propertyName)
			throws NotFoundException {
		PropertyValue propertyValue = PropertyValueDAO.findByEntityAndName(
				entityName, propertyName);

		if (propertyValue != null) {
			return Response.ok().entity(propertyValue).build();
		} else {
			throw new NotFoundException("Property value not found, entity="
					+ entityName + ", property=" + propertyName);
		}
	}

	@GET
	@Path("/list")
	@ApiOperation(value = "List all property values", notes = "")
	public Response listPropertyValue() {
		// TODO list property values of an entity
		// TODO list property values of an entity and property
		Collection<PropertyValue> list = KB.getInstance().getReader()
				.load(PropertyValue.class);
		return Response.ok()
				.entity(new GenericEntity<Collection<PropertyValue>>(list) {
				}).build();
	}

	@POST
	@Path("/{entity}/{property}")
	@ApiOperation(value = "Create property value", notes = "This can only be done by a logged user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity or property does not exist") })
	public Response createPropertyValue(
			@ApiParam(value = "Entity name (must exist)", required = true) @PathParam("entity") String entityName,
			@ApiParam(value = "Property name (must exist)", required = true) @PathParam("property") String propertyName,
			@ApiParam(value = "Property value", required = false) String value) {

		Entity entity = EntityDAO.findById(entityName);

		if (entity != null) {
			String typeName = entity.getEntityType().getName();
			Property property = PropertyDAO.findByEntityTypeAndName(typeName,
					propertyName);

			if (property != null) {
				PropertyValue propertyValue = new PropertyValue(entity,
						property, value);
				propertyValue.save();
				return Response.ok().entity(propertyValue).build();
			} else {
				throw new NotFoundException("Property not found type="
						+ typeName + ", name=" + propertyName);
			}
		} else {
			throw new NotFoundException("Entity not found: " + entityName);
		}

	}

	@PUT
	@Path("/{entity}/{property}")
	@ApiOperation(value = "Update property value", notes = "This can only be done by a logged user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Entity or property not found") })
	public Response updatePropertyValue(
			@ApiParam(value = "Entity related to the property value", required = true) @PathParam("entity") String entityName,
			@ApiParam(value = "Property related to the property value", required = true) @PathParam("property") String propertyName,
			@ApiParam(value = "Updated value", required = true) String value) {
		// TODO several property values will be supported
		PropertyValue propertyValue = PropertyValueDAO.findByEntityAndName(
				entityName, propertyName);

		if (propertyValue != null) {
			propertyValue.setValue(value);
			propertyValue.save();
			return Response.ok().entity(propertyName).build();
		} else {
			throw new NotFoundException("Property value not found entity="
					+ entityName + ", property=" + propertyName);
		}
	}

	@DELETE
	@Path("/{entity}/{property}")
	@ApiOperation(value = "Delete property value", notes = "This can only be done by an admin user (TODO)")
	@ApiErrors(value = { @ApiError(code = 404, reason = "Property value not found") })
	public Response deletePropertyValue(
			@ApiParam(value = "Entity related with the property value", required = true) @PathParam("entity") String entityName,
			@ApiParam(value = "Property related with the property value", required = true) @PathParam("property") String propertyName)
			throws ApiException {
		PropertyValue propertyValue = PropertyValueDAO.findByEntityAndName(
				entityName, propertyName);

		if (propertyValue != null) {
			propertyValue.delete();
			return Response.ok().entity(propertyValue).build();
		} else {
			throw new NotFoundException("Property value not found entity="
					+ entityName + ", property=" + propertyName);
		}
	}
}
