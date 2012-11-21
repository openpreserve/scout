package eu.scape_project.watch.policy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import eu.scape_project.watch.utils.KBUtils;

public class PolicyModel {

  private static final String BASE_URI = "http://scape-project.eu/pw/vocab/";

  private static final String HOME_PATH = System.getProperty("user.home");

  private static final String POLICIES_PATH = ".scout" + File.separator + "policies" + File.separator + "model";

  private static final String MODEL_PATH = HOME_PATH + File.separator + POLICIES_PATH;

  private static final String MODEL_NAME = "policymodel";

  private static final Logger LOG = LoggerFactory.getLogger(PolicyModel.class);

  public PolicyModel() {
    if (!loadFromFileSystem()) {
      loadFromClasspath();
    }
  }

  private boolean loadFromClasspath() {
    boolean loaded = isModelLoaded();

    if (!loaded) {
      LOG.debug("Loading policy model from classpath");

      final String policyModelFile = "/model/policy_model.rdf";
      final String attributes = "/model/attributes_measures.rdf";

      this.readModel(getClass().getResourceAsStream(policyModelFile));
      this.readModel(getClass().getResourceAsStream(attributes));

      loaded = true;

    } else {
      LOG.debug("Policy model is already loaded");
    }

    return loaded;
  }

  private boolean loadFromFileSystem() {

    boolean loaded = isModelLoaded();
    if (!loaded) {
      LOG.debug("Loading policy model from file system");
      final File modelDir = this.getModelDir();
      final String[] files = modelDir.list(new FilenameFilter() {

        @Override
        public boolean accept(File dir, String file) {
          return (file.endsWith(".rdf") || file.endsWith(".xml")) ? true : false;
        }
      });

      for (String file : files) {
        
        try {
          this.readModel(new FileInputStream(new File(MODEL_PATH + File.separator + file)));
        } catch (FileNotFoundException e) {
          LOG.error("An error occurred, while loading the policy model: {}", e.getMessage());
        }
      }

      if (files.length > 0) {
        loaded = true;
      }
    } else {
      LOG.debug("Policy model is already loaded");
    }

    return loaded;

  }

  private void readModel(InputStream file) {
    Model model = getModel();
    model.read(file, BASE_URI);
  }

  private Model getModel() {
    return KBUtils.getNamedModel(MODEL_NAME);
  }

  private boolean isModelLoaded() {
    final String query = getQuery("/queries/query_all.txt") + " LIMIT 10";
    final QueryExecution qe = QueryExecutionFactory.create(query, this.getModel());
    final ResultSet set = qe.execSelect();

    return set.hasNext();
  }

  private File getModelDir() {
    final File file = new File(MODEL_PATH);
    if (!file.exists()) {
      LOG.debug("Policy model folder on file system does not exist, creating... {}", MODEL_PATH);
      file.mkdirs();
    }

    return file;
  }

  private String getQuery(String filepath) {
    String query = "";
    try {
      query = IOUtils.toString(getClass().getResourceAsStream(filepath));
    } catch (FileNotFoundException e) {
      LOG.error("An error occurred, while loading query: {}", e.getMessage());
    } catch (IOException e) {
      LOG.error("An error occurred, while loading query: {}", e.getMessage());
    }

    return query;
  }

}
