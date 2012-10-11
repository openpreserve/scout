package eu.scape_project.watch.web;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class TemplateContext {

  private static final int PAGE_SIZE = 100;

  @Inject
  @Named("contextPath")
  private String contextPath;

  @Inject
  @Named("mustacheletPath")
  private String mustacheletPath;
  
  public int getPageSize() {
    return PAGE_SIZE;
  }

  public String getContextPath() {
    return contextPath;
  }

  public String getMustacheletPath() {
    return mustacheletPath;
  }
  

}
