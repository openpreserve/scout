/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.binding.Jenabean;

import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.exception.BadRequestException;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * REST API for {@link Measurement} operations.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class MeasurementResource extends JavaHelp {

  /**
   * The logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(MeasurementResource.class);

  /**
   * The servlet context to get context attributes.
   */
  @Context
  private ServletContext context;

  /**
   * List measurements, optionally filtering by property value.
   * 
   * @return A list of measurements filtered by above constraints.
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List measurements", notes = "")
  public Response listMeasurement(
    @ApiParam(value = "Entity type name", required = true) @QueryParam("type") final String typeName,
    @ApiParam(value = "Entity name", required = true) @QueryParam("entity") final String entityName,
    @ApiParam(value = "Property name", required = true) @QueryParam("property") final String propertyName,
    @ApiParam(value = "Property value version", required = false) @QueryParam("version") final String versionString,
    @ApiParam(value = "Only show significant measurements", required = false) @QueryParam("significant") final boolean showSignificantOnly,
    @ApiParam(value = "Index of first item to retrieve", required = true) @QueryParam("start") final int start,
    @ApiParam(value = "Maximum number of items to retrieve", required = true) @QueryParam("max") final int max) {

    if (StringUtils.isNotBlank(typeName) && StringUtils.isNotBlank(entityName) && StringUtils.isNotBlank(propertyName)) {
      final Collection<Measurement> list;
      if (StringUtils.isNotBlank(versionString)) {
        int version = Integer.parseInt(versionString);
        final PropertyValue propertyValue = DAO.PROPERTY_VALUE.find(typeName, entityName, propertyName, version);
        if (propertyValue != null) {
          list = DAO.MEASUREMENT.listByPropertyValue(propertyValue, showSignificantOnly, start, max);
        } else {
          throw new NotFoundException("Property value not found type=" + typeName + " entity=" + entityName
            + " property=" + propertyName + " version=" + version);
        }
      } else {
        list = DAO.MEASUREMENT.listByEntityAndProperty(typeName, entityName, propertyName, showSignificantOnly, start, max);
      }
      return Response.ok().entity(new GenericEntity<Collection<Measurement>>(list) {
      }).build();
    } else {
      throw new BadRequestException("Please provide all required fields: type, entity, property. Parameter version is optional.");
    }

  }
}
