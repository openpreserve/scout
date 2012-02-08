package eu.scape_project.watch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KB {

  private static final Logger LOGGER = LoggerFactory.getLogger(KB.class);

  private static KB UNIQUE_INSTANCE;

  public static synchronized KB getInstance() {
    if (KB.UNIQUE_INSTANCE == null) {
      KB.UNIQUE_INSTANCE = new KB();
      KB.LOGGER.info("KB manager created");
    }

    return KB.UNIQUE_INSTANCE;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("Singletons cannot be cloned");
  };

  private KB() {

  }
}
