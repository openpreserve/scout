package eu.scape_project.watch.adaptor.pronom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import eu.scape_project.watch.adaptor.pronom.common.JSONResultParser;

import org.apache.commons.io.IOUtils;
import org.openjena.atlas.json.io.parser.JSONParser;

public class Test {

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    FileInputStream stream = new FileInputStream("src/test/resources/response.txt");
    
    String json = IOUtils.toString(stream);

    JSONResultParser parser = new JSONResultParser();
    parser.parse(json);
  }

}
