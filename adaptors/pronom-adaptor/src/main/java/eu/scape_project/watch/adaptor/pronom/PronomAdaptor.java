package eu.scape_project.watch.adaptor.pronom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.scape_project.watch.adaptor.pronom.client.PronomClient;
import eu.scape_project.watch.adaptor.pronom.client.PronomServiceCommunicator;
import eu.scape_project.watch.adaptor.pronom.common.CommunicationException;
import eu.scape_project.watch.adaptor.pronom.common.JSONResultParser;
import eu.scape_project.watch.adaptor.pronom.common.OutputFormat;
import eu.scape_project.watch.adaptor.pronom.common.PronomResult;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.PluginType;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.utils.ConfigParameter;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Pronom Adaptor queries a PRONOM registry and parses the formats and their
 * properties. For more information about pronom consult the National Archives
 * and http://www.nationalarchives.gov.uk/PRONOM/ .
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class PronomAdaptor implements AdaptorPluginInterface {

  /**
   * A default logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(PronomAdaptor.class);

  /**
   * The host of the service.
   */
  private static final String HOST = "http://test.linkeddatapronom.nationalarchives.gov.uk";

  /**
   * The relative endpoint to the service.
   */
  public static final String ENDPOINT = HOST + "/sparql/endpoint.php";

  /**
   * The key to the batch property. Limits the size of the query response.
   */
  private static final String CNF_PRONOM_BATCH = "pronom.batch";

  /**
   * The default batch.
   */
  private static final String CNF_PRONOM_BATCH_DEFAULT = "100";

  /**
   * The description of the default batch property.
   */
  private static final String CNF_PRONOM_BATCH_DESCRIPTION = "The default batch with which the data is queried";

  /**
   * The current version of this plugin.
   */
  private static final String VERSION = "0.0.1";

  /**
   * The name of this plugin.
   */
  private static final String NAME = "Pronom";

  /**
   * The description of this plugin>
   */
  private static final String DESCRIPTION = "A Scout adaptor for the PRONOM registry.";

  /**
   * The PRONOM client used to build and submit queries.
   */
  private PronomClient client;

  /**
   * The default config of this plugin.
   */
  private List<ConfigParameter> defaultConfig;

  /**
   * The config to be used.
   */
  private Map<String, String> config;

  /**
   * Inits the default config and the config, as well as the client.
   * 
   * @throws PluginException
   *           when the url endpoint is malformed.
   */
  @Override
  public void init() throws PluginException {
    LOG.info("initializing pronom adaptor");
    try {
      // init default config
      this.config = new HashMap<String, String>();
      this.defaultConfig = new ArrayList<ConfigParameter>();
      this.defaultConfig.add(new ConfigParameter(CNF_PRONOM_BATCH, CNF_PRONOM_BATCH_DEFAULT,
        CNF_PRONOM_BATCH_DESCRIPTION, false));

      for (final ConfigParameter cp : this.defaultConfig) {
        this.config.put(cp.getKey(), cp.getValue());
      }

      // init client
      final PronomServiceCommunicator comm = new PronomServiceCommunicator(PronomAdaptor.ENDPOINT);
      this.client = new PronomClient(comm);

    } catch (MalformedURLException e) {
      throw new PluginException("An error occurred while creating the pronom communicatior", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown() throws PluginException {
    LOG.info("shutting down pronom adaptor.");
    // nothing to do here
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return PronomAdaptor.NAME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getVersion() {
    return PronomAdaptor.VERSION;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return PronomAdaptor.DESCRIPTION;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PluginType getPluginType() {
    return PluginType.ADAPTOR;
  }

  /**
   * Returns the default config of this adaptor.
   */
  @Override
  public List<ConfigParameter> getParameters() {
    return this.defaultConfig;
  }

  /**
   * Retrieves the used configuration.
   */
  @Override
  public Map<String, String> getParameterValues() {
    return this.config;
  }

  /**
   * {@inheritDoc}. Sets a config parameter.
   */
  @Override
  public void setParameterValues(Map<String, String> values) throws InvalidParameterException {
    final Set<String> keys = values.keySet();

    for (ConfigParameter cp : this.defaultConfig) {
      final String key = cp.getKey();
      if (cp.isRequired() && (!keys.contains(key) || values.get(key) == null)) {
        throw new InvalidParameterException("No value set for the required config parameter: " + key);
      }
    }

    this.config = values;

  }

  // TODO
  @Override
  public ResultInterface execute(Map<Entity, List<Property>> config) throws PluginException {
    throw new PluginException("This method is not yet implemented!");
  }

  /**
   * Scrapes the PRONOM service.
   */
  @Override
  public ResultInterface execute() throws PluginException {
    final String l = this.getParameterValues().get(PronomAdaptor.CNF_PRONOM_BATCH);
    String query = "";
    int offset = 0;
    final int limit = Integer.parseInt(l);

    try {
      query = this.getPreparedQuery();

    } catch (FileNotFoundException e) {
      LOG.error("Could not read the query '{}'", e.getMessage());
      throw new PluginException(e);

    } catch (IOException e) {
      LOG.error("An IO error occurred while reading the query '{}'", e.getMessage());
      throw new PluginException(e);
    }

    final List<PropertyValue> result = new ArrayList<PropertyValue>();
    final JSONResultParser parser = new JSONResultParser();

    try {
      List<PropertyValue> parsed = null;

      do {
        LOG.info("Getting next batch of '{}' with offset '{}'", limit, offset);
        final String response = this.client.query(query, OutputFormat.JSON, limit, offset);
        parsed = parser.parse(response);
        result.addAll(parsed);
        offset += limit;
      } while (!parsed.isEmpty());

      return new PronomResult(result);

    } catch (CommunicationException e) {
      LOG.error("A communication exception occurred while querying the pronom service '{}'", e.getMessage());
      throw new PluginException(e);
    }

  }

  /**
   * Retrieves the prepared query for getting all the data.
   * 
   * @return the query.
   * @throws FileNotFoundException
   *           if the file is not found.
   * @throws IOException
   *           if an exception occurs while reading the file.
   */
  private String getPreparedQuery() throws FileNotFoundException, IOException {
    return IOUtils.toString(new FileInputStream("src/main/resources/query.txt"));
  }

}
