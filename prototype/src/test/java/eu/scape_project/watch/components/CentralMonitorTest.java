package eu.scape_project.watch.components;

import org.junit.Test;
import org.mockito.Mockito;

import eu.scape_project.watch.components.interfaces.IMonitor;

public class CentralMonitorTest {

  @Test 
  public void testAddMonitor() {
    CentralMonitor cm = new CentralMonitor();
    IMonitor mockMonitor = Mockito.mock(IMonitor.class);
    cm.addMonitor(mockMonitor);
    Mockito.verify(mockMonitor).registerCentralMonitor(cm);
  }
  

}
