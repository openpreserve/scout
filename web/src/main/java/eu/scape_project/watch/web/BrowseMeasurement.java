package eu.scape_project.watch.web;

import java.util.regex.Matcher;

import com.google.inject.Inject;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Measurement;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;

@Path("/browse/measurement/([^/]*)")
@TemplateSource("browseMeasurement")
public class BrowseMeasurement extends TemplateContext {


  @Inject
  Matcher m;

  public String getId() {
    return m.group(1);
  }

  public Measurement getMeasurement() {
    return DAO.MEASUREMENT.findById(getId());
  }
}
