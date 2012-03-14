package eu.scape_project.watch.components.interfaces;

import org.quartz.Job;

import eu.scape_project.watch.components.elements.Result;

public interface IAdaptorJob extends Job {

  public Result getResult();
  
  public void setAdaptor(String className);
}
