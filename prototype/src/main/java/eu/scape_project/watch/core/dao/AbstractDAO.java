package eu.scape_project.watch.core.dao;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.NotFoundException;
import thewebsemantic.Sparql;
import thewebsemantic.binding.RdfBean;

import com.hp.hpl.jena.query.QuerySolutionMap;

import eu.scape_project.watch.core.KB;

public abstract class AbstractDAO {
	private static Logger LOG = LoggerFactory.getLogger(AbstractDAO.class);

	protected static <T extends RdfBean<T>> T findByAttr(String id,
			Class<T> typeClass, String idAttribute) {
		T ret = null;

		String rdfstype = KB.RDFS_NS + "type";
		String idtype = KB.WATCH_NS + typeClass.getSimpleName();
		String attrtype = KB.WATCH_NS + idAttribute;

		String query = String
				.format("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
						+ "SELECT ?s WHERE { ?s <%1$s> <%2$s> . ?s <%3$s> \"%4$s\"^^xsd:string}",
						rdfstype, idtype, attrtype, id);

		LOG.debug("Query: {}", query);

		LinkedList<T> results = Sparql.exec(KB.getInstance().getReader(),
				typeClass, query, new QuerySolutionMap(), 0, 1);
		if (results.size() > 1) {
			LOG.warn("Find got more than one result for {}: {}={}",
					new Object[] { typeClass.getSimpleName(), idAttribute, id });
		}
		if (results.size() > 0) {
			ret = results.getFirst();
		}
		return ret;
	}

	protected static <T extends RdfBean<T>> T findById(String id,
			Class<T> typeClass) {
		try {
			return KB.getInstance().getReader().load(typeClass, id);
		} catch (NotFoundException e) {
			return null;
		}
	}
}
