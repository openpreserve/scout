/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.Jenabean;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.utils.exception.BadRequestException;
import eu.scape_project.watch.utils.exception.NotFoundException;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * REST API for {@link PropertyValue} operations.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class PropertyValueResource extends JavaHelp {

  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(PropertyValueResource.class);

  /**
   * The servlet context to get context attributes.
   */
  @Context
  private ServletContext context;

  /**
   * Get an existing {@link PropertyValue}.
   * 
   * @param entityName
   *          The name of the {@link Entity} this property value refers to.
   * @param typeName
   *          The name of the {@link EntityType} this property value refers to.
   * @param propertyName
   *          The name of the {@link Property} this property value refers to.
   * @return The {@link PropertyValue} or throws {@link NotFoundException} if
   *         not found.
   */
  @GET
  @Path("/{type}/{entity}/{property}")
  @ApiOperation(value = "Find property value by entity and property", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Property value not found")})
  public Response getPropertyValueByName(
    @ApiParam(value = "Name of the entity type", required = true) @PathParam("type") final String typeName,
    @ApiParam(value = "Name of the entity name", required = true) @PathParam("entity") final String entityName,
    @ApiParam(value = "Name of the property", required = true) @PathParam("property") final String propertyName) {
    final PropertyValue propertyValue = DAO.PROPERTY_VALUE.find(entityName, typeName, propertyName);

    if (propertyValue != null) {
      return Response.ok().entity(propertyValue).build();
    } else {
      throw new NotFoundException("Property value not found, entity=" + entityName + ", property=" + propertyName);
    }
  }

  /**
   * Ge a list with all existing {@link PropertyValue}, independently of the
   * {@link Entity} or {@link Property} they refer to.
   * 
   * @return The complete list of {@link PropertyValue} in the KB
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all property values", notes = "")
  public Response listPropertyValue() {
    // TODO list property values of an entity
    // TODO list property values of an entity and property
    final Collection<PropertyValue> list = Jenabean.instance().reader().load(PropertyValue.class);
    return Response.ok().entity(new GenericEntity<Collection<PropertyValue>>(list) {
    }).build();
  }

  /**
   * Create or update a property value.
   * 
   * @param sourceAdaptorInstance
   *          The unique instance name to identify source adaptor
   * @param pv
   *          The created or updated property value
   * @return The committed {@link PropertyValue}.
   */
  @POST
  @Path("/new")
  @ApiOperation(value = "Create property value", notes = "This can only be done by a logged user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Entity, property or source adaptor not found")})
  public Response createPropertyValue(
    @ApiParam(value = "Source adaptor instance (must exist)", required = true) @QueryParam("sourceAdaptor") final String sourceAdaptorInstance,
    @ApiParam(value = "New property value", required = true) final PropertyValue pv) {

    // check if referred entity and property exist
    final Entity entity = DAO.ENTITY.findById(pv.getEntity().getName());

    if (entity == null) {
      throw new NotFoundException("Related entity not found: " + pv.getEntity().getName());
    }

    final Property property = DAO.PROPERTY.findByEntityTypeAndName(pv.getProperty().getType().getName(), pv
      .getProperty().getName());

    if (property == null) {
      throw new NotFoundException("Related property not found: " + pv.getProperty().getId());
    }

    final AdaptorManager manager = ContextUtil.getAdaptorManager(context);
    final SourceAdaptor adaptor = manager.getSourceAdaptor(sourceAdaptorInstance);

    if (adaptor == null) {
      throw new NotFoundException("Source adaptor not found: " + sourceAdaptorInstance);
    }

    DAO.PROPERTY_VALUE.save(adaptor, pv);
    return Response.ok().entity(pv).build();
  }

  /**
   * Delete an existing {@link PropertyValue}.
   * 
   * @param entityName
   *          The name of the related {@link Entity}.
   * @param propertyName
   *          The name of the related {@link Property}.
   * @return The deleted {@link PropertyValue} or throws
   *         {@link NotFoundException} if not found.
   */
  @DELETE
  @Path("/{type}/{entity}/{property}")
  @ApiOperation(value = "Delete property value", notes = "This can only be done by an admin user (TODO)")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Property value not found")})
  public Response deletePropertyValue(
    @ApiParam(value = "Name of the entity type", required = true) @PathParam("type") final String typeName,
    @ApiParam(value = "Entity related with the property value", required = true) @PathParam("entity") final String entityName,
    @ApiParam(value = "Property related with the property value", required = true) @PathParam("property") final String propertyName) {
    final PropertyValue propertyValue = DAO.PROPERTY_VALUE.find(entityName, typeName, propertyName);

    if (propertyValue != null) {
      DAO.delete(propertyValue);
      propertyValue.delete();
      return Response.ok().entity(propertyValue).build();
    } else {
      throw new NotFoundException("Property value not found entity=" + entityName + ", property=" + propertyName);
    }
  }
}
