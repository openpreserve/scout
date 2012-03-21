package eu.scape_project.watch.core;

import eu.scape_project.watch.interfaces.MonitorInterface;
import eu.scape_project.watch.monitor.CentralMonitor;
import eu.scape_project.watch.scheduling.CoreScheduler;
import eu.scape_project.watch.utils.AdaptorLoader;
import eu.scape_project.watch.utils.ComponentContainer;

import org.junit.Test;
import org.mockito.Mockito;

public class ComponentContainerTest {

  @Test
  public void testInit() {
    ComponentContainer cc = new ComponentContainer();
    CoreScheduler mockCS = Mockito.mock(CoreScheduler.class);
    AdaptorLoader mockAL = Mockito.mock(AdaptorLoader.class);

    cc.setCoreScheduler(mockCS);
    cc.setAdaptorLoader(mockAL);

    cc.init();

    Mockito.verify(mockCS).start();
    Mockito.verify(mockAL).startLoader();
  }

  @Test
  public void testDestroy() {
    ComponentContainer cc = new ComponentContainer();
    CoreScheduler mockCS = Mockito.mock(CoreScheduler.class);
    AdaptorLoader mockAL = Mockito.mock(AdaptorLoader.class);

    cc.setCoreScheduler(mockCS);
    cc.setAdaptorLoader(mockAL);

    cc.destroy();

    Mockito.verify(mockCS).shutdown();
    Mockito.verify(mockAL).cancelLoader();
    
  }

  @Test
  public void testAddMonitor() {
    ComponentContainer cc = new ComponentContainer();
    CentralMonitor mockCM = Mockito.mock(CentralMonitor.class);
    CoreScheduler mockCS = Mockito.mock(CoreScheduler.class);
    
    cc.setCentralMonitor(mockCM);
    cc.setCoreScheduler(mockCS);
    
    MonitorInterface mockMonitor = Mockito.mock(MonitorInterface.class);
    Mockito.when(mockMonitor.getGroup()).thenReturn("test group");
    
    cc.addMonitor(mockMonitor);
    
    Mockito.verify(mockCM).addMonitor(mockMonitor);
    Mockito.verify(mockCS).adddGroupJobListener(mockMonitor, "test group");
    Mockito.verify(mockMonitor).getGroup();
    
    
    
  }
}
