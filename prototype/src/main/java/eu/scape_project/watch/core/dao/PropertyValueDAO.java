package eu.scape_project.watch.core.dao;

import eu.scape_project.watch.core.model.PropertyValue;

public class PropertyValueDAO extends AbstractDAO {

	public static PropertyValue findByEntityAndName(String entityName,
			String propertyName) {
		String id = PropertyValue.createId(entityName, propertyName);
		return findById(id, PropertyValue.class);
	}

}
