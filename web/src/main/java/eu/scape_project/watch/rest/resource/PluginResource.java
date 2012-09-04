/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.core.JavaHelp;
import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.plugin.PluginInfo;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.utils.exception.BadRequestException;
import eu.scape_project.watch.utils.exception.NotFoundException;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import thewebsemantic.binding.Jenabean;

/**
 * 
 * REST API for {@link PluginInfo} listings.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class PluginResource extends JavaHelp {

  /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(PluginResource.class);

  /**
   * Get all versions of a {@link PluginInfo}.
   * 
   * @param name
   *          The name of the plug-in.
   * @return A list of plug-ins.
   */
  @GET
  @Path("/{name}")
  @ApiOperation(value = "Find plug-ins by name (all versions)", notes = "")
  @ApiErrors(value = {})
  public Response getPluginByName(
    @ApiParam(value = "Name of the plug-in", required = true) @PathParam("name") final String name) {

    final List<PluginInfo> pluginInfos = PluginManager.getDefaultPluginManager().getPluginInfo(name);
    return Response.ok().entity(new GenericEntity<Collection<PluginInfo>>(pluginInfos) {
    }).build();

  }

  /**
   * List all available plug-ins.
   * 
   * @param pluginType
   *          The type of the plugin, according to {@link PluginType}, or
   *          <code>null</code> to select all types.
   * 
   * @return A list of {@link PluginInfo}.
   */
  @GET
  @Path("/list")
  @ApiOperation(value = "List all plug-ins", notes = "")
  @ApiErrors(value = {@ApiError(code = BadRequestException.CODE, reason = "Invalid plugin type")})
  public Response listPlugin(
    @ApiParam(value = "Plug-in type (adaptor, notification)", required = false) @QueryParam("type") final String pluginType) {
    PluginType type;
    if (pluginType != null) {
      try {
        type = PluginType.valueOf(pluginType.toUpperCase());
      } catch (final IllegalArgumentException e) {
        throw new BadRequestException(e.getMessage());
      }
    } else {
      type = null;
    }

    final List<PluginInfo> pluginInfos = PluginManager.getDefaultPluginManager().getPluginInfo(type);
    return Response.ok().entity(new GenericEntity<Collection<PluginInfo>>(pluginInfos) {
    }).build();
  }

  // TODO get a plugin based on name and version

}
