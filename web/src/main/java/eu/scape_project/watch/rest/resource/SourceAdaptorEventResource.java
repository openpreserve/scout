/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import eu.scape_project.watch.adaptor.AdaptorManager;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.SourceAdaptorEvent;
import eu.scape_project.watch.listener.ContextUtil;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * 
 * REST API for {@link SourceAdaptor} management.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class SourceAdaptorEventResource {

  /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(SourceAdaptorEventResource.class);

  /**
   * The servlet context to get context attributes.
   */
  @Context
  private ServletContext context;

  /**
   * Get a {@link SourceAdaptorEvent}.
   * 
   * @param instance
   *          The unique name for the source adaptor instance.
   * @param timestamp
   *          The date on which the event has happened.
   * @return the source adaptor event or not found error.
   */
  @GET
  @Path("/{instance}/{timestamp}")
  @ApiOperation(value = "Get a source adaptor event based on the instance unique identifier and event timestamp", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Source adaptor or event not found")})
  public Response getSourceAdaptorEvent(
    @ApiParam(value = "Source adaptor instance UID", required = true) @PathParam("instance") final String instance,
    @ApiParam(value = "Event timestamp", required = true) @PathParam("timestamp") final Date timestamp) {

    final AdaptorManager manager = ContextUtil.getAdaptorManager(context);
    final SourceAdaptor adaptor = manager.getSourceAdaptor(instance);

    if (adaptor != null) {
      final SourceAdaptorEvent event = DAO.SOURCE_ADAPTOR_EVENT.findById(adaptor, timestamp);

      if (event != null) {
        return Response.ok().entity(event).build();
      } else {
        throw new NotFoundException("Source adaptor event not found at: " + timestamp);
      }

    } else {
      throw new NotFoundException("Source adaptor not found: " + instance);
    }

  }

  /**
   * List all available source adaptor events.
   * 
   * @param adaptor
   *          Optional filter by adaptor instance.
   * 
   * @return A list of {@link SourceAdaptorEvent}.
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all source adaptor events", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Source adaptor not found")})
  public Response listSourceAdaptorEvent(
    @ApiParam(value = "Source adaptor", required = false) @QueryParam("adaptor") final String id,
    @ApiParam(value = "Index offset of the list to return", required = false) @QueryParam("start") final int start,
    @ApiParam(value = "Max number of items of the list to return", required = false) @QueryParam("max") final int max) {

    List<SourceAdaptorEvent> events;

    if (StringUtils.isBlank(id)) {
      // no adaptor filter
      events = DAO.SOURCE_ADAPTOR_EVENT.query("", start, max);
      return Response.ok().entity(new GenericEntity<Collection<SourceAdaptorEvent>>(events) {
      }).build();
    } else {

      final SourceAdaptor adaptor = DAO.SOURCE_ADAPTOR.findById(id);

      if (adaptor != null) {
        events = DAO.SOURCE_ADAPTOR_EVENT.listByAdaptor(adaptor, start, max);
        return Response.ok().entity(new GenericEntity<Collection<SourceAdaptorEvent>>(events) {
        }).build();
      } else {
        throw new NotFoundException("Source adaptor not found: " + id);
      }
    }

  }

}
