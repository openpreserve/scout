package eu.scape_project.watch.interfaces;

import org.quartz.JobListener;

import eu.scape_project.watch.components.CentralMonitor;
import eu.scape_project.watch.core.CoreScheduler;

public interface IMonitor extends JobListener {

  public void registerCentralMonitor(CentralMonitor cm);
  
  public void registerScheduler(CoreScheduler cs);
  
  public String getGroup();
}
