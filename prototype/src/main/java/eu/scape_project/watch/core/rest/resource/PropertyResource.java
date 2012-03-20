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

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.core.rest.exception.NotFoundException;
import eu.scape_project.watch.dao.EntityTypeDAO;
import eu.scape_project.watch.dao.PropertyDAO;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;

import org.apache.log4j.Logger;

import thewebsemantic.binding.Jenabean;

/**
 * 
 * REST API for {@link Property} operations.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class PropertyResource extends JavaHelp {

  /**
   * The logger.
   */
  private static final Logger LOG = Logger.getLogger(PropertyResource.class);

  /**
   * Get a Property.
   * 
   * @param type
   *          The name of the {@link EntityType} this property belongs to
   * @param name
   *          The name of the property
   * @return The {@link Property} or throws {@link NotFoundException} if not
   *         found
   */
  @GET
  @Path("/{type}/{name}")
  @ApiOperation(value = "Find Property by type and name", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Property or Entity Type not found")})
  public Response getPropertyByName(
    @ApiParam(value = "Name of the Entity Type", required = true) @PathParam("type") final String type,
    @ApiParam(value = "Name of the Property", required = true) @PathParam("name") final String name) {
    final Property property = PropertyDAO.getInstance().findByEntityTypeAndName(type, name);

    if (property != null) {
      return Response.ok().entity(property).build();
    } else {
      throw new NotFoundException("Property id not found: " + name);
    }
  }

  /**
   * List all Properties independently of the {@link EntityType}.
   * 
   * @return The complete list of Properties within the KB
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all properties", notes = "")
  public Response listProperty() {
    final Collection<Property> list = Jenabean.instance().reader().load(Property.class);
    return Response.ok().entity(new GenericEntity<Collection<Property>>(list) {
    }).build();
  }

  /**
   * List all properties of a {@link EntityType}.
   * 
   * @param type
   *          The name of the {@link EntityType}
   * @param start
   *          The index of the first item to retrieve
   * @param max
   *          The maximum number of items to retrieve
   * @return A list of {@link Property} filtered by the constraints above.
   */
  @GET
  @Path("/list/{type}/{start}/{max}")
  @ApiOperation(value = "List properties of a type", notes = "")
  public Response listEntityOfType(
    @ApiParam(value = "Entity type", required = true) @PathParam("type") final String type,
    @ApiParam(value = "Index of first item to retrieve", required = true, defaultValue = "0") @PathParam("start") final int start,
    @ApiParam(value = "Maximum number of items to retrieve", required = true, defaultValue = "100") @PathParam("max") final int max) {
    final Collection<Property> list = PropertyDAO.getInstance().listWithType(type, start, max);
    return Response.ok().entity(new GenericEntity<Collection<Property>>(list) {
    }).build();
  }

  /**
   * Create a new {@link Property}.
   * 
   * @param type
   *          The name of the {@link EntityType} that this property belongs to.
   * @param name
   *          The name of the property, unique within the {@link EntityType}
   * @param description
   *          Descriptive information about the property
   * @return The newly created Property or throws {@link NotFoundException} if
   *         the {@link EntityType} is not found.
   */
  @POST
  @Path("/{type}/{name}")
  @ApiOperation(value = "Create Entity Type", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Entity type does not exist")})
  public Response createProperty(
    @ApiParam(value = "Entity type name (must exist)", required = true) @PathParam("type") final String type,
    @ApiParam(value = "Property name (must be unique)", required = true) @PathParam("name") final String name,
    @ApiParam(value = "Property description", required = false) final String description) {
    // TODO support data type
    LOG.debug("Create property name=" + name + " description=" + description + " in type=" + type);
    final EntityType entityType = EntityTypeDAO.getInstance().findById(type);

    if (entityType != null) {
      final Property property = new Property(entityType, name, description);
      property.save();
      return Response.ok().entity(property).build();
    } else {
      throw new NotFoundException("Entity type not found: " + type);
    }

  }

  /**
   * Update an existing {@link Property}.
   * 
   * @param type
   *          The name of the {@link EntityType} the property belongs to
   * @param name
   *          The name of the property
   * @param property
   *          The new Property that should replace the existing one
   * @return The new Property merged into the KB
   */
  @PUT
  @Path("/{type}/{name}")
  @ApiOperation(value = "Update Property of Type", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Property not found")})
  public Response updateProperty(
    @ApiParam(value = "Entity type that owns Property", required = true) @PathParam("type") final String type,
    @ApiParam(value = "Name that needs to be deleted", required = true) @PathParam("name") final String name,
    @ApiParam(value = "Updated property object", required = true) final Property property) {
    final Property original = PropertyDAO.getInstance().findByEntityTypeAndName(type, name);
    if (original != null) {
      original.delete();
      property.save();
      return Response.ok().entity(property).build();
    } else {
      throw new NotFoundException("Property not found: " + name);
    }
  }

  /**
   * Delete an existing {@link Property}.
   * 
   * @param type
   *          The name of the {@link EntityType} the property belongs to
   * @param name
   *          The name of the property
   * @return The deleted {@link Property} or throws {@link NotFoundException} if
   *         not found.
   */
  @DELETE
  @Path("/{type}/{name}")
  @ApiOperation(value = "Delete property", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Property or Entity Type not found")})
  public Response deleteProperty(
    @ApiParam(value = "Entity type that owns property", required = true) @PathParam("type") final String type,
    @ApiParam(value = "The name of the property to be deleted", required = true) @PathParam("name") final String name) {
    final Property property = PropertyDAO.getInstance().findByEntityTypeAndName(type, name);

    if (property != null) {
      property.delete();
      return Response.ok().entity(property).build();
    } else {
      throw new NotFoundException("Property not found: " + name);
    }

  }

}
