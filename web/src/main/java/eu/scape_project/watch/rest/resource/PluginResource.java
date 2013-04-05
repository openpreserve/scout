/**
 * 
 */
package eu.scape_project.watch.rest.resource;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.plugin.PluginInfo;
import eu.scape_project.watch.plugin.PluginManager;
import eu.scape_project.watch.utils.exception.BadRequestException;
import eu.scape_project.watch.utils.exception.NotFoundException;

/**
 * 
 * REST API for {@link PluginInfo} listings.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class PluginResource {

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

  /**
   * Get a {@link PluginInfo}.
   * 
   * @param name
   *          The name of the plug-in.
   * @param version
   *          The version of the plug-in.
   * @return A list of plug-ins.
   */
  @GET
  @Path("/{name}/{version}")
  @ApiOperation(value = "Find a plug-in", notes = "")
  @ApiErrors(value = {@ApiError(code = NotFoundException.CODE, reason = "Plug-in not found")})
  public Response getPluginByNameAndVersion(
    @ApiParam(value = "Name of the plug-in", required = true) @PathParam("name") final String name,
    @ApiParam(value = "Version of the plug-in", required = true) @PathParam("version") final String version) {

    final PluginInfo pluginInfo = PluginManager.getDefaultPluginManager().getPluginInfo(name, version);

    if (pluginInfo != null) {
      return Response.ok().entity(pluginInfo).build();
    } else {
      throw new NotFoundException("Plug-in not found, name=" + name + ", version=" + version);
    }

  }

}
