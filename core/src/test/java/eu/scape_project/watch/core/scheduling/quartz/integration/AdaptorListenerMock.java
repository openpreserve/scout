package eu.scape_project.watch.core.scheduling.quartz.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorListenerInterface;
import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.scheduling.quartz.QuartzAdaptorJob;

public class AdaptorListenerMock implements AdaptorListenerInterface{

  private static final Logger LOG = LoggerFactory.getLogger(AdaptorListenerMock.class);
  
  @Override
  public void notify(AdaptorPluginInterface adaptor, ResultInterface result) {
    LOG.info("Saving some data from " + adaptor.getName());
  }

}
