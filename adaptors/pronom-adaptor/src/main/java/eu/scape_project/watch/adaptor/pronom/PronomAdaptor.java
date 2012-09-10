package eu.scape_project.watch.adaptor.pronom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.scape_project.watch.adaptor.pronom.client.PronomClient;
import eu.scape_project.watch.adaptor.pronom.client.PronomServiceCommunicator;
import eu.scape_project.watch.adaptor.pronom.common.CommunicationException;
import eu.scape_project.watch.adaptor.pronom.common.JSONResultParser;
import eu.scape_project.watch.adaptor.pronom.common.OutputFormat;
import eu.scape_project.watch.adaptor.pronom.common.ResultProcessingDispatcher;
import eu.scape_project.watch.common.DefaultResult;
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
  private static final String CNF_PRONOM_BATCH = "pronom.batch.size";

  /**
   * The default batch.
   */
  private static final String CNF_PRONOM_BATCH_DEFAULT = "100";

  /**
   * The description of the default batch property.
   */
  private static final String CNF_PRONOM_BATCH_DESCRIPTION = "The default batch with which the data is queried";

  /**
   * The key to the cache file property. Provides the path the to cache file.
   */
  private static final String CNF_CACHE_FILE_PATH = "pronom.cache.file.path";

  /**
   * The default path of the cache file. Currently a file called pronomcache.txt
   * in hidden scout folder in the home directory.
   */
  private static final String CNF_CACHE_FILE_PATH_DEFAULT = System.getProperty("user.home") + File.separator + ".scout"
    + File.separator + "pronomcache.txt";

  /**
   * The description of the file cache property.
   */
  private static final String CNF_CACHE_FILE_PATH_DESCRIPTION = "The default path to a file used by "
    + "this adaptor for partial result caching. The default is /user_dir/.scout/pronomcache.txt";

  /**
   * The current version of this plugin.
   */
  private static final String VERSION = "0.0.4";

  /**
   * The name of this plugin.
   */
  private static final String NAME = "Pronom Adaptor";

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

  private List<PropertyValue> currentBatch;

  private int offset;

  private String query;

  /**
   * Inits the default config and the config, as well as the client.
   * 
   * @throws PluginException
   *           when the url endpoint is malformed.
   */
  @Override
  public void init() throws PluginException {
    LOG.info("initializing {}-{}", this.getName(), this.getVersion());
    this.initializeConfiguration();
    this.initialzeCommunicator();
    this.initializePreparedQuery();
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

  @Override
  public boolean hasNext() throws PluginException {
    if (this.currentBatch.isEmpty()) {
      this.currentBatch = this.getNextBatch();
    }
    
    final boolean hasNext = !this.currentBatch.isEmpty();
    
    if (!hasNext) {
      this.reset();
    }

    return hasNext;
  }

  @Override
  public ResultInterface next() {
    final PropertyValue pv = this.currentBatch.remove(0);
    final DefaultResult result = new DefaultResult(pv);

    return result;
  }

  @Override
  public ResultInterface execute(Map<Entity, List<Property>> config) throws PluginException {
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
   * Resets the offset. This method should be called after
   * all the data has been retrieved and iterated.
   */
  private void reset() {
    this.offset = 0;
  }

  // TODO cache the dispatcher and the parser.
  private List<PropertyValue> getNextBatch() throws PluginException {
    final int limit = this.getBatchSizeFromConfig();
    final String cache = this.getCacheFileFromConfig();
    final ResultProcessingDispatcher dispatcher = new ResultProcessingDispatcher(cache);
    final JSONResultParser parser = new JSONResultParser(dispatcher);

    // fetch data
    try {
      LOG.info("Getting next batch of '{}' with offset '{}'", limit, this.offset);
      final String response = this.client.query(this.query, OutputFormat.JSON, limit, this.offset);
      this.offset += limit;
      return parser.parse(response);

    } catch (CommunicationException e) {
      LOG.error("A communication exception occurred while querying the pronom service '{}'", e.getMessage());
      throw new PluginException(e);
    }
  }

  /**
   * Retrieves the {@link PronomAdaptor#CNF_PRONOM_BATCH} config parameter
   * value. If the provided value is not valid a the default is used silently.
   * 
   * @return the batch size to use.
   */
  private int getBatchSizeFromConfig() {
    final String l = this.getParameterValues().get(PronomAdaptor.CNF_PRONOM_BATCH);
    int limit = Integer.parseInt(CNF_PRONOM_BATCH_DEFAULT);
    try {
      limit = Integer.parseInt(l);
    } catch (final NumberFormatException e) {
      LOG.warn("There was a problem with the provided batch size '{}'; using the default batch size", e.getMessage());
    }

    return limit;
  }

  /**
   * Retrieves the file path to the cache for this plugin.
   * 
   * @return the file path.
   * @throws PluginException
   *           if the path is not provided.
   */
  private String getCacheFileFromConfig() throws PluginException {
    final String cache = this.getParameterValues().get(PronomAdaptor.CNF_CACHE_FILE_PATH);
    if (cache == null) {
      LOG.error("No cache file provided; {}-{} will now quit", this.getName(), this.getVersion());
      throw new PluginException("The cache file was not provided");
    }

    return cache;
  }

  /**
   * Initializes the prepared query that is submitted to the source.
   * 
   * @throws PluginException
   *           of the query file cannot be read or found.
   */
  private void initializePreparedQuery() throws PluginException {
    try {
      this.query = IOUtils.toString(new FileInputStream("src/main/resources/query.txt"));

    } catch (FileNotFoundException e) {
      LOG.error("Could not read the query '{}'", e.getMessage());
      throw new PluginException(e);

    } catch (IOException e) {
      LOG.error("An IO error occurred while reading the query '{}'", e.getMessage());
      throw new PluginException(e);
    }
  }

  /**
   * Initializes the default and current configuration.
   */
  private void initializeConfiguration() {
    final ConfigParameter batch = new ConfigParameter(CNF_PRONOM_BATCH, CNF_PRONOM_BATCH_DEFAULT,
      CNF_PRONOM_BATCH_DESCRIPTION, false);
    final ConfigParameter cache = new ConfigParameter(CNF_CACHE_FILE_PATH, CNF_CACHE_FILE_PATH_DEFAULT,
      CNF_CACHE_FILE_PATH_DESCRIPTION, true);

    this.defaultConfig = Arrays.asList(batch, cache);
    this.config = new HashMap<String, String>();
    this.currentBatch = new ArrayList<PropertyValue>();
    this.offset = 0;
  }

  /**
   * Initializes the source client.
   * 
   * @throws PluginException
   *           if an error occurs.
   */
  private void initialzeCommunicator() throws PluginException {
    try {
      final PronomServiceCommunicator comm = new PronomServiceCommunicator(PronomAdaptor.ENDPOINT);
      this.client = new PronomClient(comm);

    } catch (MalformedURLException e) {
      throw new PluginException("An error occurred while creating the pronom communicatior", e);
    }
  }
}
