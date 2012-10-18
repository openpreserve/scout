package eu.scape_project.watch.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import eu.scape_project.watch.rest.resource.AsyncRequestResourceJSON;
import eu.scape_project.watch.rest.resource.AsyncRequestResourceXML;
import eu.scape_project.watch.rest.resource.EntityResourceJSON;
import eu.scape_project.watch.rest.resource.EntityResourceXML;
import eu.scape_project.watch.rest.resource.EntityTypeResourceJSON;
import eu.scape_project.watch.rest.resource.EntityTypeResourceXML;
import eu.scape_project.watch.rest.resource.MeasurementResourceJSON;
import eu.scape_project.watch.rest.resource.MeasurementResourceXML;
import eu.scape_project.watch.rest.resource.PluginResourceJSON;
import eu.scape_project.watch.rest.resource.PluginResourceXML;
import eu.scape_project.watch.rest.resource.PropertyResourceJSON;
import eu.scape_project.watch.rest.resource.PropertyResourceXML;
import eu.scape_project.watch.rest.resource.PropertyValueResourceJSON;
import eu.scape_project.watch.rest.resource.PropertyValueResourceXML;
import eu.scape_project.watch.rest.resource.RequestResourceJSON;
import eu.scape_project.watch.rest.resource.RequestResourceXML;
import eu.scape_project.watch.rest.resource.SourceAdaptorResourceJSON;
import eu.scape_project.watch.rest.resource.SourceAdaptorResourceXML;
import eu.scape_project.watch.rest.resource.SourceResourceJSON;
import eu.scape_project.watch.rest.resource.SourceResourceXML;

/**
 * 
 * JAX-RS Application that lists all the resources. This is necessary for the
 * Jersey Test Framework.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class WatchApplication extends Application {
  @Override
  public Set<Class<?>> getClasses() {

    final Set<Class<?>> classes = new HashSet<Class<?>>();

    // register resources
    classes.add(EntityResourceJSON.class);
    classes.add(EntityResourceXML.class);
    classes.add(EntityTypeResourceJSON.class);
    classes.add(EntityTypeResourceXML.class);
    classes.add(PropertyResourceJSON.class);
    classes.add(PropertyResourceXML.class);
    classes.add(PropertyValueResourceJSON.class);
    classes.add(PropertyValueResourceXML.class);
    classes.add(RequestResourceJSON.class);
    classes.add(RequestResourceXML.class);
    classes.add(AsyncRequestResourceJSON.class);
    classes.add(AsyncRequestResourceXML.class);
    classes.add(PluginResourceJSON.class);
    classes.add(PluginResourceXML.class);
    classes.add(SourceAdaptorResourceJSON.class);
    classes.add(SourceAdaptorResourceXML.class);
    classes.add(SourceResourceJSON.class);
    classes.add(SourceResourceXML.class);
    classes.add(MeasurementResourceXML.class);
    classes.add(MeasurementResourceJSON.class);

    return classes;
  }
}
