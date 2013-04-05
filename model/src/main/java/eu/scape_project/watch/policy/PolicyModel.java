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

import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateAction;

import eu.scape_project.watch.domain.Objective;

public class PolicyModel {

  private static final String BASE_URI = "http://scape-project.eu/pw/vocab/";

  private static final String HOME_PATH = System.getProperty("user.home");

  private static final String POLICIES_PATH = HOME_PATH + File.separator + ".scout" + File.separator + "policies";

  private static final String MODEL_PATH = POLICIES_PATH + File.separator + "model";

  // XXX Now loading the policy model into the base model
  // private static final String MODEL_NAME = "policymodel";

  private static final Logger LOG = LoggerFactory.getLogger(PolicyModel.class);

  public PolicyModel() {
    if (!loadFromFileSystem()) {
      loadFromClasspath();
    }

    loadUploadedPolicies();
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
      final Resource measure = next.getResource("measure");
      final Literal name = next.getLiteral("name");
      final Literal desc = next.getLiteral("desc");
      final Resource mod = next.getResource("modality");
      final Literal val = next.getLiteral("value");
      final Resource q = next.getResource("qualifier");

      Objective tmp = new Objective();
      tmp.setUrl(obj.toString());

      if (measure != null) {
        tmp.setMeasure(measure.toString());
      }

      if (name != null) {
        tmp.setMeasureName(name.getString());
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

  public void deleteAllObjectives() {
    final String query = this.getQuery("/queries/delete_all_objectives.txt");

    UpdateAction.parseExecute(query, this.getModel());
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

      if (modelDir == null || !modelDir.exists()) {
        LOG.warn("Could not create policy model directory!");
        LOG.warn("This might lead to unexpected behavior!");
        return false;
      }

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

  private void loadUploadedPolicies() {
    LOG.debug("Loading previously uploaded policies from file system");
    final File uploadDir = new File(POLICIES_PATH);
    final String[] files = uploadDir.list(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String file) {
        return (file.endsWith(".rdf") || file.endsWith(".xml")) ? true : false;
      }
    });

    for (String file : files) {
      try {
        this.readModel(new FileInputStream(new File(POLICIES_PATH + File.separator + file)));
      } catch (FileNotFoundException e) {
        LOG.error("An error occurred, while loading the previously uploaded policies: {}", e.getMessage());
      }
    }

  }

  private void readModel(InputStream file) {
    Model model = getModel();
    model.read(file, BASE_URI, "RDF/XML");
  }

  private Model getModel() {
    // return KBUtils.getNamedModel(MODEL_NAME);
    return Jenabean.instance().model();
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
