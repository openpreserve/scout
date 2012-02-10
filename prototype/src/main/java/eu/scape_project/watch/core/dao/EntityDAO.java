package eu.scape_project.watch.core.dao;

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Sparql;

import com.hp.hpl.jena.query.QuerySolutionMap;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;

public class EntityDAO extends AbstractDAO {

	private static Logger LOG = LoggerFactory.getLogger(EntityDAO.class);

	public static Entity findById(String entityName) {
		return findById(entityName, Entity.class);
	}

	public static Collection<Entity> listWithType(String type, int start,
			int max) {
		String rdfstype = KB.RDFS_NS + "type";
		String idtype = KB.WATCH_NS + Entity.class.getSimpleName();
		String typeRlsType = KB.WATCH_NS + "type";
		String typeType = KB.WATCH_NS + EntityType.class.getSimpleName() + "/"
				+ type;

		String query = String
				.format("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
						+ "SELECT ?s WHERE { ?s <%1$s> <%2$s> . ?s <%3$s> <%4$s>}",
						rdfstype, idtype, typeRlsType, typeType);

		LOG.debug("Query: {}", query);

		LinkedList<Entity> results = Sparql.exec(KB.getInstance().getReader(),
				Entity.class, query, new QuerySolutionMap(), start, max);
		return results;
	}
}
