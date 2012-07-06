package eu.scape_project.watch.adaptor.pronom.common;

import java.io.FileInputStream;
import java.util.List;

import junit.framework.Assert;
import net.sf.json.JSONException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import eu.scape_project.watch.domain.PropertyValue;

/**
 * Tests the result parser.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class JSONResultParserTest {

  private JSONResultParser parser;

  @Before
  public void setup() {
    final ResultProcessingDispatcher dispatcher = Mockito.mock(ResultProcessingDispatcher.class);
    Mockito.when(dispatcher.process(Mockito.anyString())).thenReturn(true);

    this.parser = new JSONResultParser(dispatcher);
  }

  /**
   * if the response is valid the parser should extract some values.
   * 
   * @throws Exception
   */
  @Test
  public void onValidResponse() throws Exception {
    final String response = this.getResponse(true);

    final List<PropertyValue> parse = this.parser.parse(response);

    Assert.assertNotNull(parse);
    Assert.assertFalse(parse.isEmpty());
  }

  /**
   * If the response is not valid the parser should throw a
   * {@link JSONException}.
   * 
   * @throws Exception
   */
  @Test(expected = JSONException.class)
  public void onNonValidResponse() throws Exception {
    final String response = this.getResponse(false);

    this.parser.parse(response);

    Assert.fail("This code should not have been reached");
  }

  /**
   * Simulates the response to be parsed.
   * 
   * @param valid
   *          if it should be valid or not
   * @return the simlated response.
   * @throws Exception
   */
  private String getResponse(boolean valid) throws Exception {
    String json = "gibberish";
    if (valid) {
      final FileInputStream stream = new FileInputStream("src/test/resources/response.txt");
      json = IOUtils.toString(stream);
      return json;
    }

    return json;
  }
}
