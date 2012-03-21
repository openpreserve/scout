package eu.scape_project.watch.components;

import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.monitor.CentralMonitor;

import org.junit.Test;
import org.mockito.Mockito;

public class CentralMonitorTest {

  @Test 
  public void testAddMonitor() {
    CentralMonitor cm = new CentralMonitor();
    MonitorInterface mockMonitor = Mockito.mock(MonitorInterface.class);
    cm.addMonitor(mockMonitor);
    Mockito.verify(mockMonitor).registerCentralMonitor(cm);
  }
  

}
