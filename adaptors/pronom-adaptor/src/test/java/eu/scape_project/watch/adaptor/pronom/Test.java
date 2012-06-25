package eu.scape_project.watch.adaptor.pronom;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import eu.scape_project.watch.adaptor.pronom.client.PronomClient;
import eu.scape_project.watch.adaptor.pronom.client.PronomServiceCommunicator;
import eu.scape_project.watch.adaptor.pronom.common.JSONResultParser;

import org.apache.commons.io.IOUtils;

public class Test {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
//    pronom();
    json();
  }

  private static void pronom() {
    try {
      StringWriter writer = new StringWriter();
      IOUtils.copy(new FileInputStream("src/test/resources/query.txt"), writer);
      String query = writer.toString().replace(System.getProperty("line.separator"), "");

      PronomServiceCommunicator comm = new PronomServiceCommunicator(PronomAdaptor.ENDPOINT);
      PronomClient client = new PronomClient(comm);
      
      String result = client.query(query);
      System.out.println(result);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static void json() throws IOException {
    FileInputStream stream = new FileInputStream("src/test/resources/response.txt");

    String json = IOUtils.toString(stream);
    System.out.println(json);

    JSONResultParser parser = new JSONResultParser();
    parser.parse(json);
  }

}
