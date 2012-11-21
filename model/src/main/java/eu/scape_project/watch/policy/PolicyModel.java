package eu.scape_project.watch.policy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import eu.scape_project.watch.domain.Objective;
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

  public boolean loadPolicies(String file) {
    boolean loaded = false;

    if (file == null) {
      LOG.warn("Cannot load policies. Provided file does not exist");
      return loaded;
    }

    final File policies = new File(file);

    if (policies.exists() && policies.isFile() && (file.endsWith(".rdf") || file.endsWith(".xml"))) {
      try {
        this.readModel(new FileInputStream(file));
        loaded = true;
      } catch (FileNotFoundException e) {
        LOG.error("An error occurred while loading the policies file {}: {}", file, e.getMessage());
      }
    } else {
      LOG.warn("The provided file either does not exist or is not a valid rdf/xml file: {}", file);
    }

    return loaded;
  }

  public List<Objective> listAllObjectives() {
    final List<Objective> result = new ArrayList<Objective>();
    final String query = this.getQuery("/queries/query_all_objectives.txt");
    final QueryExecution qe = QueryExecutionFactory.create(query, this.getModel()); // getBaseModel?
    final ResultSet set = qe.execSelect();

    while (set.hasNext()) {
      final QuerySolution next = set.nextSolution();
      final Resource obj = next.getResource("objective");
      final Literal name = next.getLiteral("name");
      final Literal desc = next.getLiteral("desc");
      final Resource mod = next.getResource("modality");
      final Literal val = next.getLiteral("value");
      final Resource q = next.getResource("qualifier");

      Objective tmp = new Objective();
      tmp.setUrl(obj.toString());

      if (name != null) {
        tmp.setMeasure(name.getString());
      }

      if (q != null) {
        tmp.setQualifier(q.getLocalName());
      }

      if (desc != null) {
        tmp.setMeasureDescription(desc.getString());
      }

      if (mod != null) {
        tmp.setModality(mod.getLocalName());
      }

      if (val != null) {
        tmp.setValue(val.getString());
      }

      result.add(tmp);
    }

    return result;
  }

  private boolean loadFromClasspath() {
    boolean loaded = isModelLoaded();

    if (!loaded) {
      LOG.debug("Loading policy model from classpath");

      final String policyModelFile = "/model/policy_model.rdf";
      final String controlPolicyFile = "/model/control-policy.rdf";
      final String modalitiesFile = "/model/modalities.rdf";
      final String qualifiersFile = "/model/qualifiers.rdf";
      final String scalesFile = "/model/scales.rdf";
      final String scopesFile = "/model/scopes.rdf";
      final String attributesFile = "/model/attributes_measures.rdf";

      this.readModel(getClass().getResourceAsStream(policyModelFile));
      this.readModel(getClass().getResourceAsStream(controlPolicyFile));
      this.readModel(getClass().getResourceAsStream(modalitiesFile));
      this.readModel(getClass().getResourceAsStream(qualifiersFile));
      this.readModel(getClass().getResourceAsStream(scalesFile));
      this.readModel(getClass().getResourceAsStream(scopesFile));
      this.readModel(getClass().getResourceAsStream(attributesFile));

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
    model.read(file, BASE_URI, "RDF/XML");
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
