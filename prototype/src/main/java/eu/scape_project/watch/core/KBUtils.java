package eu.scape_project.watch.core;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Namespace;
import thewebsemantic.Sparql;
import thewebsemantic.binding.RdfBean;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.scape_project.watch.core.model.EntityType;

public class KBUtils {
	private static final Logger LOG = LoggerFactory.getLogger(KB.class);

	public static void printStatements() {

		StmtIterator statements = KB.getInstance().getModel().listStatements();
		try {
			while (statements.hasNext()) {
				Statement stmt = statements.next();
				Resource s = stmt.getSubject();
				Resource p = stmt.getPredicate();
				RDFNode o = stmt.getObject();

				String sinfo = "";
				String pinfo = "";
				String oinfo = "";

				if (s.isURIResource()) {
					sinfo = "<" + s.getURI() + ">";
				} else if (s.isAnon()) {
					sinfo = "" + s.getId();
				}

				if (p.isURIResource())
					pinfo = "<" + p.getURI() + ">";

				if (o.isURIResource()) {
					oinfo = "<" + o.toString() + ">";
				} else if (o.isAnon()) {
					oinfo = "" + o.toString();
				} else if (o.isLiteral()) {
					oinfo = "'" + o.asLiteral() + "'";
				}
				System.out.println("(" + sinfo + ", " + pinfo + ", " + oinfo);
			}
		} catch (Throwable e) {
			LOG.info(e.getMessage());
		} finally {
			statements.close();
		}
	}

	public static <T extends RdfBean<T>> T find(String id, Class<T> typeClass,
			String idAttribute) {
		T ret = null;

		String rdfstype = KB.RDFS_NS + "type";
		String idtype = KB.WATCH_NS + typeClass.getSimpleName();
		String attrtype = KB.WATCH_NS + idAttribute;

		String query = String
				.format("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
						+ "SELECT ?s WHERE { ?s <%1$s> <%2$s> . ?s <%3$s> \"%4$s\"^^xsd:string}",
						rdfstype, idtype, attrtype, id);

		LOG.debug("Query: " + query);

		LinkedList<T> results = Sparql.exec(KB.getInstance().getReader(),
				typeClass, query, new QuerySolutionMap(), 0, 1);
		if (results.size() > 1) {
			LOG.warn("Find got more than one result for "
					+ typeClass.getSimpleName() + ": " + idAttribute + "=" + id);
		}
		if (results.size() > 0) {
			ret = results.getFirst();
		}
		return ret;
	}
}
