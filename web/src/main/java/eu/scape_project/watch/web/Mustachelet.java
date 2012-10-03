package eu.scape_project.watch.web;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class Mustachelet {

  public static final int PAGE_SIZE = 100;

  @Inject
  @Named("contextPath")
  protected String contextPath;

  @Inject
  @Named("mustacheletPath")
  protected String mustacheletPath;

}
