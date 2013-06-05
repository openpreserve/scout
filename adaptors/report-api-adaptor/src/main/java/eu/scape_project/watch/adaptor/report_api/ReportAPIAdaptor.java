package eu.scape_project.watch.adaptor.report_api;

import info.lc.xmlns.premis_v2.EventComplexType;
import info.lc.xmlns.premis_v2.EventOutcomeDetailComplexType;
import info.lc.xmlns.premis_v2.EventOutcomeInformationComplexType;
import info.lc.xmlns.premis_v2.ExtensionComplexType;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.math.NumberUtils;
import org.openarchives.oai._2.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.util.DateParser;
import org.w3c.util.InvalidDateException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
	private static final String VERSION = "0.0.2";

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
	private static final String PROPERTY_INGEST_AVG_TIME_NAME = "Ingest average time (ms)";

	/**
	 * The description of property: Ingest average time
	 */
	private static final String PROPERTY_INGEST_AVG_TIME_DESCRIPTION = "The average time, in milliseconds, that takes for a SIP to be ingested";

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
	 * The repository name returned by harvester verb "Identify".
	 */
	private String repositoryName;

	private Entity entity = null;
	private EntityType entityType = null;

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

		// initializeProperties();
	}

	@Override
	public boolean hasNext() throws PluginException {
		LOG.debug("hasNext()");

		if (this.iterator == null) {

			updateIngestAverageTime();
			updatePlanExecutionDetailProperties();

			this.iterator = getProperties().values().iterator();
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

			DefaultResult result = new DefaultResult(this.iterator.next());

			LOG.info("Returning next property '{}' with value {}", result
					.getProperty().getName(), result.getValue().getValue());

			return result;

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

	private Entity getEntity() {
		if (this.entity == null) {
			this.entity = new Entity(getEntityType(), this.repositoryName);
		}
		return this.entity;
	}

	private EntityType getEntityType() {
		if (this.entityType == null) {
			this.entityType = new EntityType(ENTITY_TYPE_REPOSITORY_NAME,
					ENTITY_TYPE_REPOSITORY_DESCRIPTION);
		}
		return this.entityType;
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

	// private void initializeProperties() {
	// LOG.debug("initializeProperties()");
	//
	// if (getProperties().containsKey(PROPERTY_INGEST_AVG_TIME_NAME)) {
	// LOG.debug("Property " + PROPERTY_INGEST_AVG_TIME_NAME
	// + " already exists.");
	// } else {
	//
	// if (this.repositoryName == null) {
	// LOG.debug("Couldn't add properties because repository identity is not available yet.");
	// } else {
	//
	// try {
	//
	// Property property = new Property(getEntityType(),
	// PROPERTY_INGEST_AVG_TIME_NAME,
	// PROPERTY_INGEST_AVG_TIME_DESCRIPTION,
	// DataType.FLOAT);
	//
	// this.properties.put(property.getName(), new PropertyValue(
	// getEntity(), property, 0f));
	//
	// LOG.info("Added property " + property.getName()
	// + " with ID " + property.getId()
	// + " to table of properties");
	//
	// } catch (UnsupportedDataTypeException e) {
	// LOG.error("Couln't create property - " + e.getMessage(), e);
	// } catch (InvalidJavaClassForDataTypeException e) {
	// LOG.error("Couln't create property - " + e.getMessage(), e);
	// }
	// }
	//
	// }
	//
	// }

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

				this.harvester = new Harvester(address);

				this.repositoryName = this.harvester.getRepositoryName();

				LOG.info("Repository name: " + this.repositoryName);

			} catch (IOException e) {
				LOG.error(
						"Couldn't get the repository identify - "
								+ e.getMessage(), e);
				throw new PluginException(
						"Couldn't get the repository identify - "
								+ e.getMessage(), e);
			} catch (ParserConfigurationException e) {
				LOG.error(
						"Couldn't get the repository identify - "
								+ e.getMessage(), e);
				throw new PluginException(
						"Couldn't get the repository identify - "
								+ e.getMessage(), e);
			} catch (SAXException e) {
				LOG.error(
						"Couldn't get the repository identify - "
								+ e.getMessage(), e);
				throw new PluginException(
						"Couldn't get the repository identify - "
								+ e.getMessage(), e);
			} catch (TransformerException e) {
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

	/**
	 * 
	 */
	private void updateIngestAverageTime() {
		LOG.debug("updateIngestAverageTime()");

		if (this.harvester == null) {
			LOG.warn("Cannot update IngestAverageTime because Harvester is null");
		} else {

			try {
				Map<String, SIPIngestTime> mapIngestTime = new HashMap<String, ReportAPIAdaptor.SIPIngestTime>();

				Iterator<RecordType> recordIterator = this.harvester
						.getRecordIterator(null, null, null, "premis-event-v2");

				while (recordIterator.hasNext()) {
					RecordType record = recordIterator.next();

					LOG.debug("Processing record "
							+ record.getHeader().getIdentifier());

					@SuppressWarnings("unchecked")
					JAXBElement<EventComplexType> premisEventElement = (JAXBElement<EventComplexType>) record
							.getMetadata().getAny();
					EventComplexType premisEvent = premisEventElement
							.getValue();

					if ("IngestStarted".equalsIgnoreCase(premisEvent
							.getEventType())
							|| "IngestFinished".equalsIgnoreCase(premisEvent
									.getEventType())) {

						String sipID = this.harvester
								.getPremisLinkingObjectSipID(premisEvent);

						LOG.debug("SIP ID is " + sipID);

						SIPIngestTime sipIngestTime = mapIngestTime.get(sipID);
						if (sipIngestTime == null) {
							sipIngestTime = new SIPIngestTime();
							mapIngestTime.put(sipID, sipIngestTime);
						}

						if ("IngestStarted".equals(premisEvent.getEventType())) {
							try {

								sipIngestTime.startTime = DateParser
										.parse(premisEvent.getEventDateTime());

								LOG.debug(
										"SIP {} IngestStarted EventDateTime is {}",
										sipID, premisEvent.getEventDateTime());

								LOG.debug("SIP {} Ingest startTime is {}",
										sipID, sipIngestTime.startTime);

							} catch (InvalidDateException e) {
								LOG.warn(
										"Couldn't parse date " + e.getMessage(),
										e);
							}
						} else if ("IngestFinished".equals(premisEvent
								.getEventType())) {
							try {

								sipIngestTime.finishTime = DateParser
										.parse(premisEvent.getEventDateTime());

								LOG.debug(
										"SIP {} IngestFinished EventDateTime is {}",
										sipID, premisEvent.getEventDateTime());

								LOG.debug("SIP {} Ingest finishTime is {}",
										sipID, sipIngestTime.finishTime);

							} catch (InvalidDateException e) {
								LOG.warn(
										"Couldn't parse date " + e.getMessage(),
										e);
							}
						}
					}

				}

				LOG.info("Collected ingest time information for "
						+ mapIngestTime.size() + " SIPs");

				long totalDurationMillis = 0;
				long numOfValidIntervals = 0;

				for (SIPIngestTime ingestTime : mapIngestTime.values()) {

					if (ingestTime.startTime != null
							&& ingestTime.finishTime != null) {

						numOfValidIntervals++;

						totalDurationMillis += ingestTime.finishTime.getTime()
								- ingestTime.startTime.getTime();

						LOG.debug(
								"Added {} - total duration={}ms, number of intervals={}",
								new Object[] { ingestTime, totalDurationMillis,
										numOfValidIntervals });

					} else {
						LOG.debug("Ignoring incomplete interval {}", ingestTime);
					}
				}

				if (numOfValidIntervals > 0) {

					LOG.debug(
							"Calculating average - total duration={}ms, number of intervals={}",
							totalDurationMillis, numOfValidIntervals);

					float average = (float) totalDurationMillis
							/ numOfValidIntervals;

					LOG.debug("Average duration={}ms", average);

					addFloatProperty(PROPERTY_INGEST_AVG_TIME_NAME,
							PROPERTY_INGEST_AVG_TIME_DESCRIPTION, average);

				} else {
					LOG.debug("Not calculating average because there's no valid intervals");
				}

			} catch (Exception e) {
				LOG.error("Couldn't get records - " + e.getMessage(), e);
			}

		}
	}

	private void updatePlanExecutionDetailProperties() {
		LOG.debug("updatePlanExecutionDetailProperties()");

		if (this.repositoryName == null) {
			LOG.debug("Couldn't add properties because repository identity is not available yet.");
		} else {

			Map<String, List<String>> aggregatedProperties = getAgregatedPlanExecutionDetails();

			for (String propertyName : aggregatedProperties.keySet()) {

				List<String> values = aggregatedProperties.get(propertyName);

				if (values != null && values.size() > 0) {

					String firstValue = values.get(0);

					if (NumberUtils.isNumber(firstValue)) {

						float min = Float.MAX_VALUE;
						float max = Float.MIN_VALUE;
						float total = 0;
						for (String value : values) {
							float number = NumberUtils.createFloat(value);
							min = min > number ? number : min;
							max = max < number ? number : max;
							total += number;
						}
						float average = total / values.size();

						addFloatProperty("Minimum "
								+ getPropertyName(propertyName),
								getPropertyName(propertyName), min);

						addFloatProperty("Maximum "
								+ getPropertyName(propertyName),
								getPropertyName(propertyName), max);

						addFloatProperty("Average "
								+ getPropertyName(propertyName),
								getPropertyName(propertyName), average);

					} else {

						Map<String, Integer> hist = new HashMap<String, Integer>();
						for (String value : values) {
							Integer count = hist.get(value);
							if (count == null) {
								count = 0;
							}
							count++;
							hist.put(value, count);
						}

						addHistogramProperty(getPropertyName(propertyName),
								getPropertyDescription(propertyName), hist);
					}

				}
			}

		}
	}

	private void addFloatProperty(String name, String description, float value) {

		try {
			Property property = new Property(getEntityType(), name,
					description, DataType.FLOAT);

			getProperties().put(property.getName(),
					new PropertyValue(getEntity(), property, value));

			LOG.info("Added property " + property.getName() + " with ID "
					+ property.getId() + " to table of properties");

		} catch (UnsupportedDataTypeException e) {
			LOG.error(
					"Couln't add property '" + name + "' - " + e.getMessage(),
					e);
		} catch (InvalidJavaClassForDataTypeException e) {
			LOG.error(
					"Couln't add property '" + name + "' - " + e.getMessage(),
					e);
		}

	}

	private void addHistogramProperty(String name, String description,
			Map<String, Integer> value) {

		try {
			Property property = new Property(getEntityType(), name,
					description, DataType.STRING_DICTIONARY);

			getProperties().put(property.getName(),
					new PropertyValue(getEntity(), property, value));

			LOG.info("Added property " + property.getName() + " with ID "
					+ property.getId() + " to table of properties");

		} catch (UnsupportedDataTypeException e) {
			LOG.error(
					"Couln't add property '" + name + "' - " + e.getMessage(),
					e);
		} catch (InvalidJavaClassForDataTypeException e) {
			LOG.error(
					"Couln't add property '" + name + "' - " + e.getMessage(),
					e);
		}

	}

	private String getPropertyName(String propertyName) {
		if ("action.efficiency.timeBehaviour.timePerSample"
				.equals(propertyName)) {
			propertyName = "preservation action execution time";
		}
		return propertyName;
	}

	private String getPropertyDescription(String propertyName) {
		String description = propertyName;
		if ("action.efficiency.timeBehaviour.timePerSample"
				.equals(propertyName)) {
			description = propertyName;
		}
		return description;
	}

	private Map<String, List<String>> getAgregatedPlanExecutionDetails() {

		Map<String, List<String>> aggregatedProperties = new HashMap<String, List<String>>();

		try {

			Iterator<RecordType> recordIterator = this.harvester
					.getRecordIterator(null, null, "PlanExecuted",
							"premis-event-v2");

			while (recordIterator.hasNext()) {
				RecordType record = recordIterator.next();

				LOG.debug("Processing record "
						+ record.getHeader().getIdentifier());

				@SuppressWarnings("unchecked")
				JAXBElement<EventComplexType> premisEventElement = (JAXBElement<EventComplexType>) record
						.getMetadata().getAny();
				EventComplexType premisEvent = premisEventElement.getValue();

				Map<String, String> planExecutionDetails = getPlanExecutionDetails(premisEvent
						.getEventOutcomeInformation());

				for (String property : planExecutionDetails.keySet()) {

					List<String> values = aggregatedProperties.get(property);
					if (values == null) {
						values = new ArrayList<String>();
						aggregatedProperties.put(property, values);
					}

					values.add(planExecutionDetails.get(property));
				}
			}

		} catch (IOException e) {
			LOG.error(
					"Couln't harvest PlanExecuted events - " + e.getMessage(),
					e);
		} catch (ParserConfigurationException e) {
			LOG.error(
					"Couln't harvest PlanExecuted events - " + e.getMessage(),
					e);
		} catch (TransformerException e) {
			LOG.error(
					"Couln't harvest PlanExecuted events - " + e.getMessage(),
					e);
		} catch (JAXBException e) {
			LOG.error(
					"Couln't harvest PlanExecuted events - " + e.getMessage(),
					e);
		} catch (SAXException e) {
			LOG.error(
					"Couln't harvest PlanExecuted events - " + e.getMessage(),
					e);
		}

		return aggregatedProperties;
	}

	private Map<String, String> getPlanExecutionDetails(
			List<EventOutcomeInformationComplexType> eventOutcomeInformation) {

		Map<String, String> planExecutionDetailProperties = new HashMap<String, String>();

		try {

			if (eventOutcomeInformation != null
					&& eventOutcomeInformation.size() > 0) {

				EventOutcomeInformationComplexType outcomeInfo = eventOutcomeInformation
						.get(0);

				LOG.debug("eventOutcomeInformation[0]: " + outcomeInfo);

				EventOutcomeDetailComplexType eventOutcomeDetail = null;

				if (outcomeInfo.getContent() != null) {

					for (JAXBElement<?> content : outcomeInfo.getContent()) {

						if ("eventOutcomeDetail".equals(content.getName()
								.getLocalPart())) {

							eventOutcomeDetail = (EventOutcomeDetailComplexType) content
									.getValue();
						} else {
							LOG.debug("Ignoring content value with name "
									+ content.getName());
						}
					}

				}

				LOG.debug("eventOutcomeInformation[0].eventOutcomeDetail: "
						+ eventOutcomeDetail);

				if (eventOutcomeDetail != null) {

					List<ExtensionComplexType> detailExtensions = eventOutcomeDetail
							.getEventOutcomeDetailExtension();

					if (detailExtensions != null && detailExtensions.size() > 0) {

						List<Object> extension = detailExtensions.get(0)
								.getAny();

						if (extension != null && extension.size() > 0) {

							Object object = extension.get(0);

							LOG.debug("eventOutcomeInformation[0].eventOutcomeDetail[0].eventOutcomeDetailExtension[0]: "
									+ object);

							if (object != null && object instanceof Node) {
								Node nodeP = (Node) object;

								if (nodeP.getFirstChild() != null) {

									LOG.debug("planExecutionDetails text => "
											+ nodeP.getFirstChild()
													.getTextContent());

									Document planExecutionDetails = loadXMLFromString(nodeP
											.getFirstChild().getTextContent());

									NodeList elementsQA = planExecutionDetails
											.getElementsByTagName("qa");

									LOG.debug("<planExecutionDetails> has "
											+ elementsQA.getLength()
											+ " elements");

									for (int i = 0; i < elementsQA.getLength(); i++) {
										Node elementQA = elementsQA.item(i);
										Node property = elementQA
												.getAttributes().getNamedItem(
														"property");
										String value = elementQA
												.getTextContent();

										planExecutionDetailProperties.put(
												property.getNodeValue(), value);

										LOG.debug(String.format(
												"QA property %s=%s",
												property.getNodeValue(), value));
									}

								} else {
									LOG.debug("extension Node first child is null");
								}

							} else {

								LOG.debug("eventOutcomeInformation[0].eventOutcomeDetail[0].eventOutcomeDetailExtension[0] is null or isn't a Node");
							}

						} else {
							LOG.debug("eventOutcomeInformation[0].eventOutcomeDetail[0].eventOutcomeDetailExtension has no sub-elements");
						}

					} else {
						LOG.debug("eventOutcomeInformation[0].eventOutcomeDetail[0].eventOutcomeDetailExtension has no sub-elements");
					}

				}

			} else {
				LOG.debug("eventOutcomeInformation has no sub-elements");
			}

		} catch (DOMException e) {
			LOG.error(
					"Couldn't parse planExecutionDetails from event outcome details - "
							+ e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LOG.error(
					"Couldn't parse planExecutionDetails from event outcome details - "
							+ e.getMessage(), e);
		} catch (SAXException e) {
			LOG.error(
					"Couldn't parse planExecutionDetails from event outcome details - "
							+ e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(
					"Couldn't parse planExecutionDetails from event outcome details - "
							+ e.getMessage(), e);
		}

		return planExecutionDetailProperties;
	}

	private Document loadXMLFromString(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	class SIPIngestTime {
		Date startTime;
		Date finishTime;

		@Override
		public String toString() {
			return String.format("%s (startTime: %s, finishTime: %s)",
					getClass().getSimpleName(), startTime, finishTime);
		}
	}

}
