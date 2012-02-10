package eu.scape_project.watch.core;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;

import eu.scape_project.watch.core.model.Entity;
import eu.scape_project.watch.core.model.EntityType;
import eu.scape_project.watch.core.model.Property;
import eu.scape_project.watch.core.model.PropertyValue;

public class KB {

	private static final Logger LOGGER = LoggerFactory.getLogger(KB.class);

	// private static final String UNION_GRAPH = "urn:x-arq:UnionGraph";

	private static KB UNIQUE_INSTANCE;

	private static Dataset dataset;
	private static Model model;

	public static final String WATCH_NS = "http://watch.scape-project.eu/";
	public static final String RDFS_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	public static final String ENTITY = "entity";
	public static final String ENTITY_TYPE = "entitytype";
	public static final String PROPERTY = "property";
	public static final String PROPERTY_VALUE = "propertyvalue";
	public static final String MEASUREMENT = "measurement";

	// TODO get data folder from config
	private static String DATA_FOLDER = "/usr/local/watch/data/tdb";

	public static void setDataFolder(String dataFolder) {
		DATA_FOLDER = dataFolder;
	}

	public static synchronized KB getInstance() {
		if (KB.UNIQUE_INSTANCE == null) {
			KB.UNIQUE_INSTANCE = new KB();

			File dataFolderFile = new File(DATA_FOLDER);
			try {
				boolean initdata = false;
				if (!dataFolderFile.exists()) {
					FileUtils.forceMkdir(dataFolderFile);
					initdata = true;
				}
				dataset = TDBFactory.createDataset(DATA_FOLDER);
				model = TDBFactory.createModel(DATA_FOLDER);
				Jenabean.instance().bind(model);

				if (initdata) {
					createInitialData();
				}

				LOGGER.info("KB manager created at {}", DATA_FOLDER);

			} catch (IOException e) {
				LOGGER.error("Data folder {} could not be created",
						e.getMessage());
			}

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					if (dataset != null) {
						model.close();
						dataset.close();
					}
					KB.LOGGER.info("closing dataset");
				}
			}));

		}

		return KB.UNIQUE_INSTANCE;
	}

	private static void createInitialData() {

		EntityType formats = new EntityType("format", "File format");
		Property formatPUID = new Property(formats, "PUID", "PRONOM Id");
		Property formatMimetype = new Property(formats, "MIME", "MIME type");

		formats.save();
		formatPUID.save();
		formatMimetype.save();

		EntityType tools = new EntityType("tools",
				"Applications that read and/or write into diferent file formats");
		Property toolVersion = new Property(tools, "version", "Tool version");

		tools.save();
		toolVersion.save();

		Entity pdf17Format = new Entity(formats, "PDF-v1.7");
		Entity tiffFormat = new Entity(formats, "TIFF");
		Entity imageMagickTool = new Entity(tools, "ImageMagick-v6.6.0");

		// save entities
		pdf17Format.save();
		tiffFormat.save();
		imageMagickTool.save();

		// property value construction also binds to entity
		PropertyValue pdfPUID = new PropertyValue(pdf17Format, formatPUID,
				"fmt/276");
		PropertyValue pdfMime = new PropertyValue(pdf17Format, formatMimetype,
				"application/pdf");

		PropertyValue tiffPUID = new PropertyValue(tiffFormat, formatPUID,
				"fmt/353");
		PropertyValue tiffMime = new PropertyValue(tiffFormat, formatMimetype,
				"image/tiff");

		PropertyValue imageMagickVersion = new PropertyValue(imageMagickTool,
				toolVersion, "6.6.0");

		// save property values
		pdfPUID.save();
		pdfMime.save();
		tiffPUID.save();
		tiffMime.save();
		imageMagickVersion.save();

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
