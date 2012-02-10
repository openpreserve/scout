package eu.scape_project.watch.core.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import eu.scape_project.watch.core.rest.resource.EntityResourceJSON;
import eu.scape_project.watch.core.rest.resource.EntityResourceXML;
import eu.scape_project.watch.core.rest.resource.EntityTypeResourceJSON;
import eu.scape_project.watch.core.rest.resource.EntityTypeResourceXML;
import eu.scape_project.watch.core.rest.resource.PropertyResourceJSON;
import eu.scape_project.watch.core.rest.resource.PropertyResourceXML;
import eu.scape_project.watch.core.rest.resource.PropertyValueResourceJSON;
import eu.scape_project.watch.core.rest.resource.PropertyValueResourceXML;

public class WatchApplication extends Application {
	@Override
	public Set<Class<?>> getClasses() {

		final Set<Class<?>> classes = new HashSet<Class<?>>();

		// register root resources
		classes.add(EntityResourceJSON.class);
		classes.add(EntityResourceXML.class);
		classes.add(EntityTypeResourceJSON.class);
		classes.add(EntityTypeResourceXML.class);
		classes.add(PropertyResourceJSON.class);
		classes.add(PropertyResourceXML.class);
		classes.add(PropertyValueResourceJSON.class);
		classes.add(PropertyValueResourceXML.class);

		return classes;
	}
}
