package eu.scape_project.watch.adaptor.c3po;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import eu.scape_project.watch.common.ConfigParameter;
import eu.scape_project.watch.utils.exceptions.InvalidParameterException;
import eu.scape_project.watch.utils.exceptions.PluginException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C3POAdaptorTest {

  private static final Logger LOG = LoggerFactory.getLogger(C3POAdaptorTest.class);

  private C3POAdaptor adaptor;

  @Before
  public void setup() {
    this.adaptor = new C3POAdaptor();
    try {
      this.adaptor.init();
    } catch (final PluginException e) {
      LOG.error("This should not occur: {}", e.getMessage());
    }

  }

  @Test
  public void onParameterValuesSetupSuccess() throws Exception {
    final List<ConfigParameter> parameters = this.adaptor.getParameters();
    Map<String, String> values = new HashMap<String, String>();

    for (final ConfigParameter cp : parameters) {
      values.put(cp.getKey(), cp.getValue());
    }

    this.adaptor.setParameterValues(values);

    values = this.adaptor.getParameterValues();
    Assert.assertEquals(parameters.size(), values.keySet().size());
    Assert.assertFalse(values.keySet().isEmpty());
  }

  @Test(expected = InvalidParameterException.class)
  public void onParameterValuesSetupFailure() throws Exception {
    final Map<String, String> values = new HashMap<String, String>();
    this.adaptor.setParameterValues(values);

    Assert.fail("This code should not have been reached.");
  }
  
}
