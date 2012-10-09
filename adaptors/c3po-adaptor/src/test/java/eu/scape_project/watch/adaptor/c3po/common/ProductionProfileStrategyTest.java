package eu.scape_project.watch.adaptor.c3po.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductionProfileStrategyTest {
  
  private ProductionProfileStrategy reader;

  @Before
  public void setup() {
    try {
      this.reader = new ProductionProfileStrategy();
      this.reader.setStream(new FileInputStream(new File("src/test/resources/profiles/test-profile.xml")));
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void shouldGetDistribution() throws Exception {
    Map<String, String> distribution = this.reader.getDistribution("format");
    assertNotNull(distribution);
    assertFalse(distribution.keySet().isEmpty());
  }
}
