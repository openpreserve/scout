package eu.scape_project.watch.web;

import com.google.inject.Inject;

public abstract class Mustachelet {
  
  public static final int PAGE_SIZE = 100;

  @Inject
  String basePath;
}
