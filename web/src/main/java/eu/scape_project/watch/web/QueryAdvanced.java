package eu.scape_project.watch.web;

import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.web.annotations.Path;
import eu.scape_project.watch.web.annotations.TemplateSource;


@Path("/query/advanced")
@TemplateSource("queryAdvanced")
public class QueryAdvanced extends TemplateContext {

  public String getRdfPrefixes() {
    return KBUtils.PREFIXES_DECL;
  }
  
}
