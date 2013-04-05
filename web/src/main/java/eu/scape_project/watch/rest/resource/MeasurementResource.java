/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Collection;

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

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * REST API for {@link Measurement} operations.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class MeasurementResource {

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
    @ApiParam(value = "Property Value ID", required = false) @QueryParam("value") final String valueId,
    @ApiParam(value = "Entity ID", required = false) @QueryParam("entity") final String entityId,
    @ApiParam(value = "Property ID", required = false) @QueryParam("property") final String propertyId,
    @ApiParam(value = "Only show significant measurements", required = false) @QueryParam("significant") final boolean showSignificantOnly,
    @ApiParam(value = "Index of first item to retrieve", required = true) @QueryParam("start") final int start,
    @ApiParam(value = "Maximum number of items to retrieve", required = true) @QueryParam("max") final int max) {

    final Collection<Measurement> list;
    if (StringUtils.isNotBlank(valueId)) {
      final PropertyValue propertyValue = DAO.PROPERTY_VALUE.findById(valueId);
      if (propertyValue != null) {
        list = DAO.MEASUREMENT.listByPropertyValue(propertyValue, showSignificantOnly, start, max);
      } else {
        throw new NotFoundException("Property value not found:" + valueId);
      }
    } else if (StringUtils.isNotBlank(entityId) && StringUtils.isNotBlank(propertyId)) {
      list = DAO.MEASUREMENT.listByEntityAndProperty(entityId, propertyId, showSignificantOnly, start, max);
    } else {
      list = DAO.MEASUREMENT.query("", start, max);
    }

    return Response.ok().entity(new GenericEntity<Collection<Measurement>>(list) {
    }).build();

  }
}
