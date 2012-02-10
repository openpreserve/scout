package eu.scape_project.watch.core;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Sparql;
import thewebsemantic.binding.RdfBean;

import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

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
				LOG.debug("({}, {}, {})", new Object[] { sinfo, pinfo, oinfo });
			}
		} catch (Throwable e) {
			LOG.info(e.getMessage());
		} finally {
			statements.close();
		}
	}
}
