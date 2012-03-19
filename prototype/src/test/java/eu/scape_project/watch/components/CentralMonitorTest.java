package eu.scape_project.watch.components;

import org.junit.Test;
import org.mockito.Mockito;

import eu.scape_project.watch.components.interfaces.IMonitor;
import eu.scape_project.watch.core.dao.AsyncRequestDAO;

public class CentralMonitorTest {

  @Test 
  public void testAddMonitor() {
    CentralMonitor cm = new CentralMonitor();
    IMonitor mockMonitor = Mockito.mock(IMonitor.class);
    cm.addMonitor(mockMonitor);
    Mockito.verify(mockMonitor).registerCentralMonitor(cm);
  }
  
  @Test
  public void testRegisterToAsyncRequest() {
    CentralMonitor cm = new CentralMonitor();
    AsyncRequestDAO mockAR = Mockito.mock(AsyncRequestDAO.class);
    cm.registerToAsyncRequest(mockAR);
    Mockito.verify(mockAR).addDOListener(cm);
  }
}
