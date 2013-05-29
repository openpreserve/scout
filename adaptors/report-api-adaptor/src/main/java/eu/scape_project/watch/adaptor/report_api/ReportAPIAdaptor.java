package eu.scape_project.watch.adaptor.report_api;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.util.DateParser;
import org.w3c.util.InvalidDateException;

import com.rdksys.oai.Harvester;
import com.rdksys.oai.data.Record;
import com.rdksys.oai.data.RecordIterator;
import com.rdksys.oai.repository.Identify;

import eu.scape_project.watch.common.DefaultResult;
import eu.scape_project.watch.domain.DataType;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

/**
 * The Report API Adaptor queries a repository Report API and parses the events.
 * For more information about Report API consult
 * http://www.scape-project.eu/deliverable
 * /d4-1-architecture-design-first-version.
 * 
 * @author Rui Castro <rcastro@keep.pt>
 */
public class ReportAPIAdaptor implements AdaptorPluginInterface {

	/**
	 * A default logger.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(ReportAPIAdaptor.class);

	/**
	 * The current version of this plugin.
	 */
	private static final String VERSION = "0.0.1";

	/**
	 * The name of this plugin.
	 */
	private static final String NAME = "Report API Adaptor";

	/**
	 * The description of this plugin
	 */
	private static final String DESCRIPTION = "A Scout adaptor for a repository Report API.";

	/**
	 * The name of Entity type: Repository
	 */
	private static final String ENTITY_TYPE_REPOSITORY_NAME = "Repository";

	/**
	 * The description of Entity type: Repository
	 */
	private static final String ENTITY_TYPE_REPOSITORY_DESCRIPTION = "Repository events";

	/**
	 * The name of property: Ingest average time
	 */
	private static final String PROPERTY_INGEST_AVG_TIME_NAME = "Ingest average time";

	/**
	 * The description of property: Ingest average time
	 */
	private static final String PROPERTY_INGEST_AVG_TIME_DESCRIPTION = "The average time that takes for a SIP to be ingested";

	/**
	 * The default config of this plugin.
	 */
	private List<ConfigParameter> defaultConfig;

	/**
	 * The config to be used.
	 */
	private Map<String, String> config;

	/**
	 * The OAI harvester.
	 */
	private Harvester harvester;

	/**
	 * The repository identify returned by harvester verb "Identify".
	 */
	private Identify repositoryIdentity;

	/**
	 * A map of property values collected from the repository.
	 */
	private HashMap<String, PropertyValue> properties;

	private Iterator<PropertyValue> iterator;

	/**
	 * Inits the default config and the config, as well as the client.
	 * 
	 * @throws PluginException
	 *             when the url endpoint is malformed.
	 */
	@Override
	public void init() throws PluginException {
		LOG.info("initializing {}-{}", this.getName(), this.getVersion());
		initializeConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() throws PluginException {
		LOG.info("shutting down {}-{}", this.getName(), this.getVersion());
		// nothing to do here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		LOG.debug("getName()");
		return ReportAPIAdaptor.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getVersion() {
		LOG.debug("getVersion()");
		return ReportAPIAdaptor.VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		LOG.debug("getDescription()");
		return ReportAPIAdaptor.DESCRIPTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PluginType getPluginType() {
		LOG.debug("getPluginType()");
		return PluginType.ADAPTOR;
	}

	/**
	 * Returns the default config of this adaptor.
	 */
	@Override
	public List<ConfigParameter> getParameters() {
		LOG.debug("getParameters()");
		return this.defaultConfig;
	}

	/**
	 * Retrieves the used configuration.
	 */
	@Override
	public Map<String, String> getParameterValues() {
		LOG.debug("getParameterValues()");
		return this.config;
	}

	/**
	 * {@inheritDoc}. Sets a config parameter.
	 */
	@Override
	public void setParameterValues(Map<String, String> values)
			throws InvalidParameterException {

		LOG.debug("setParameterValues(values={})", values);

		final Set<String> keys = values.keySet();

		if (this.defaultConfig != null) {
			for (ConfigParameter cp : this.defaultConfig) {
				final String key = cp.getKey();
				if (cp.isRequired()
						&& (!keys.contains(key) || values.get(key) == null)) {
					throw new InvalidParameterException(
							"No value set for the required config parameter: "
									+ key);
				}
			}
		}

		this.config = values;

		try {
			initializeHarvester();
		} catch (PluginException e) {
			LOG.error("Couldn't initialize harvester - " + e.getMessage(), e);
		}

		initializeProperties();
	}

	@Override
	public boolean hasNext() throws PluginException {
		LOG.debug("hasNext()");

		if (this.iterator == null) {
			calculateIngestAverageTime();
		}

		try {

			boolean hasNext = false;

			if (this.iterator != null) {
				hasNext = this.iterator.hasNext();
				if (!hasNext) {
					this.iterator = null;
				}
			}

			return hasNext;

		} catch (Throwable e) {
			LOG.error("Unexpected error - " + e.getMessage(), e);
			throw new PluginException(e);
		}
	}

	@Override
	public ResultInterface next() {
		LOG.debug("next()");
		try {

			return new DefaultResult(this.iterator.next());

		} catch (Throwable e) {
			LOG.error("Unexpected error - " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public ResultInterface execute(Map<Entity, List<Property>> config)
			throws PluginException {
		throw new PluginException("This method is not supported anymore!");
	}

	/**
	 * Scrapes the PRONOM service.
	 */
	@Override
	public ResultInterface execute() throws PluginException {
		throw new PluginException("This method is not supported anymore!");
	}

	/**
	 * Initializes the default and current configuration.
	 */
	private void initializeConfiguration() {
		LOG.debug("initializeConfiguration()");
		final ConfigParameter host = new ConfigParameter("Address", null,
				"The URL of the Report API", true, false);
		final ConfigParameter username = new ConfigParameter("Username", null,
				"The username to access the Report API", false, false);
		final ConfigParameter password = new ConfigParameter("Password", null,
				"The password to access the Report API", false, true);

		// this.defaultConfig = Arrays.asList(host, username, password);
		this.defaultConfig = Arrays.asList(host);
		this.config = new HashMap<String, String>();
	}

	private void initializeProperties() {
		LOG.debug("initializeProperties()");

		if (getProperties().containsKey(PROPERTY_INGEST_AVG_TIME_NAME)) {
			LOG.debug("Property " + PROPERTY_INGEST_AVG_TIME_NAME
					+ " already exists.");
		} else {

			if (this.repositoryIdentity == null) {
				LOG.debug("Couldn't add properties because repository identity is not available yet.");
			} else {

				EntityType type = new EntityType(ENTITY_TYPE_REPOSITORY_NAME,
						ENTITY_TYPE_REPOSITORY_DESCRIPTION);
				Entity entity = new Entity(type,
						this.repositoryIdentity.getRepositoryName());
				try {

					Property property = new Property(type,
							PROPERTY_INGEST_AVG_TIME_NAME,
							PROPERTY_INGEST_AVG_TIME_DESCRIPTION,
							DataType.FLOAT);

					this.properties.put(property.getName(), new PropertyValue(
							entity, property, 0f));

					LOG.debug("Added property " + property.getName()
							+ " with ID " + property.getId()
							+ " to table of properties");

				} catch (UnsupportedDataTypeException e) {
					LOG.error("Couln't create property - " + e.getMessage(), e);
				} catch (InvalidJavaClassForDataTypeException e) {
					LOG.error("Couln't create property - " + e.getMessage(), e);
				}
			}

		}

	}

	private HashMap<String, PropertyValue> getProperties() {
		LOG.debug("getProperties()");
		if (this.properties == null) {
			this.properties = new HashMap<String, PropertyValue>();
		}
		return this.properties;
	}

	/**
	 * Initializes the OAI harvester.
	 * 
	 * @throws PluginException
	 */
	private void initializeHarvester() throws PluginException {
		LOG.debug("initializeHarvester()");
		String address = getAddress();

		if (address == null) {
			this.harvester = null;
		} else {

			try {
				// this.harvester = new Harvester(address, getUsername(),
				// getPassword());
				this.harvester = new Harvester(address);

				this.repositoryIdentity = this.harvester.identify();

				LOG.debug("Repository Identify repositoryName: "
						+ repositoryIdentity.getRepositoryName());

			} catch (Exception e) {
				LOG.error(
						"Couldn't get the repository identify - "
								+ e.getMessage(), e);
				throw new PluginException(
						"Couldn't get the repository identify - "
								+ e.getMessage(), e);
			}
		}

	}

	private String getAddress() {
		LOG.debug("getAddress()");
		if (getParameterValues() != null
				&& getParameterValues().containsKey("Address")) {
			return getParameterValues().get("Address");
		} else {
			return null;
		}
	}

	private String getUsername() {
		LOG.debug("getUsername()");
		if (getParameterValues() != null
				&& getParameterValues().containsKey("Username")) {
			return getParameterValues().get("Username");
		} else {
			return null;
		}
	}

	private String getPassword() {
		LOG.debug("getPassword()");
		if (getParameterValues() != null
				&& getParameterValues().containsKey("Password")) {
			return getParameterValues().get("Password");
		} else {
			return null;
		}
	}

	private void calculateIngestAverageTime() {
		LOG.debug("calculateIngestAverageTime()");
		if (this.harvester == null) {
		} else {

			try {
				RecordIterator listRecords = this.harvester.listRecords(null,
						null, null);

				Map<String, SIPIngestTime> mapIngestTime = new HashMap<String, ReportAPIAdaptor.SIPIngestTime>();

				while (listRecords.hasNext()) {

					Record record = listRecords.next();
					String[] idParts = record.getHeader().getIdentifier()
							.split(":");

					SIPIngestTime sipIngestTime = mapIngestTime.get(idParts[1]);
					if (sipIngestTime == null) {
						sipIngestTime = new SIPIngestTime();
						mapIngestTime.put(idParts[1], sipIngestTime);
					}

					if ("IngestStarted".equals(idParts[0])) {
						try {
							sipIngestTime.startTime = DateParser.parse(record
									.getMetadata().getDateList().get(0));
						} catch (InvalidDateException e) {
							LOG.warn("Couldn't parse date " + e.getMessage(), e);
						}
					} else if ("IngestFinish".equals(idParts[0])) {
						try {
							sipIngestTime.finishTime = DateParser.parse(record
									.getMetadata().getDateList().get(0));
						} catch (InvalidDateException e) {
							LOG.warn("Couldn't parse date " + e.getMessage(), e);
						}
					}

				}

				long durationMillis = 0;
				long numOfSips = 0;
				for (SIPIngestTime ingestTime : mapIngestTime.values()) {
					if (ingestTime.startTime != null
							&& ingestTime.finishTime != null) {

						numOfSips++;
						durationMillis += ingestTime.finishTime.getTime()
								- ingestTime.startTime.getTime();

					}
				}

				if (numOfSips > 0) {
					PropertyValue propertyValue = this.properties
							.get(PROPERTY_INGEST_AVG_TIME_NAME);
					try {

						propertyValue.setValue((float) durationMillis
								/ numOfSips, Float.class);

						this.iterator = this.properties.values().iterator();

					} catch (UnsupportedDataTypeException e) {
						LOG.error("Couldn't set value - " + e.getMessage(), e);
					} catch (InvalidJavaClassForDataTypeException e) {
						LOG.error("Couldn't set value - " + e.getMessage(), e);
					}
				}

			} catch (Exception e) {
				LOG.error("Couldn't get records - " + e.getMessage(), e);
			}
		}

	}

	class SIPIngestTime {
		Date startTime;
		Date finishTime;
	}
}
