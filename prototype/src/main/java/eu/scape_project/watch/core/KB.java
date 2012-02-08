package eu.scape_project.watch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

public class KB {

	private static final Logger LOGGER = LoggerFactory.getLogger(KB.class);

	private static final String DATA_FOLDER = "./data/tdb";
	// private static final String UNION_GRAPH = "urn:x-arq:UnionGraph";

	private static KB UNIQUE_INSTANCE;

	private static Dataset dataset;
	private static Model model;

	public static synchronized KB getInstance() {
		if (KB.UNIQUE_INSTANCE == null) {
			KB.UNIQUE_INSTANCE = new KB();

			dataset = TDBFactory.createDataset(DATA_FOLDER);

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					dataset.close();
					KB.LOGGER.info("closing dataset");
				}
			}));

			// model = ModelFactory.createOntologyModel(
			// OntModelSpec.OWL_MEM_MICRO_RULE_INF,
			// dataset.getNamedModel(UNION_GRAPH));
			model = TDBFactory.createModel(DATA_FOLDER);

			Jenabean.instance().bind(model);

			KB.LOGGER.info("KB manager created");
		}

		return KB.UNIQUE_INSTANCE;
	}

	public Dataset getDataset() {
		return KB.dataset;
	}

	public Model getModel() {
		return model;
	}

	public RDF2Bean getReader() {
		return Jenabean.instance().reader();
	}

	public Bean2RDF getWriter() {
		return Jenabean.instance().writer();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Singletons cannot be cloned");
	}

	private KB() {

	}
}
