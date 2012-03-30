package eu.scape_project.watch.monitor;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.monitor.CentralMonitor;

public class CentralMonitorTest {

  @Test 
  public void testAddMonitor() {
    
    CentralMonitor cm = new CentralMonitor();
    MonitorInterface mockMonitor = Mockito.mock(MonitorInterface.class);
    cm.addMonitor(mockMonitor);
    Mockito.verify(mockMonitor).registerCentralMonitor(cm);
  
  }
  
  @Test 
  public void testAddingRemovingAsyncRequest() {
    
    CentralMonitor cm = new CentralMonitor();
    AsyncRequest mocRequest = Mockito.mock(AsyncRequest.class);
    
    cm.onUpdated(mocRequest);
    Collection<AsyncRequest> list = cm.getAllRequests();
    Assert.assertTrue(list.size()==1 && list.contains(mocRequest));
    
    cm.onUpdated(mocRequest);
    list = cm.getAllRequests();
    Assert.assertTrue(list.size()==1 && list.contains(mocRequest));
    
    cm.onRemoved(mocRequest);
    list = cm.getAllRequests();
    Assert.assertTrue(list.size()==0 && !list.contains(mocRequest));
    
    
  }
  

}
