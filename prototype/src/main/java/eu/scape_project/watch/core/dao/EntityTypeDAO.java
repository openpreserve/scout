package eu.scape_project.watch.core.dao;

import eu.scape_project.watch.core.model.EntityType;

public class EntityTypeDAO  extends AbstractDAO {

	public static EntityType findById(String entityTypeName) {
		return findById(entityTypeName, EntityType.class);
	}
}
