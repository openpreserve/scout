package eu.scape_project.watch.core.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.components.interfaces.IAdaptor;
import eu.scape_project.watch.components.interfaces.IAdaptorJob;
import eu.scape_project.watch.core.CoreScheduler;
import eu.scape_project.watch.core.common.ConfigUtils;

/**
 * Class that enables loading adaptors with their config dynamically
 * @author kresimir
 *
 */
public class AdaptorLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdaptorLoader.class);
  
  private static final long LOADER_PERIOD = 60*10*1000L;
  
  private static final String ADAPTOR_JOB_PACKAGE = "eu.scape_project.watch.components";
  
  private File adaptorFolder ;
  private List<File> adaptorConfigs;
  
  private List<File> tempAdaptorConfigs;
  
  private Timer loaderTimer;
  
  public AdaptorLoader() {
    ConfigUtils conf = new ConfigUtils();
    adaptorFolder = new File(conf.getStringProperty("watch.adaptors.folder"));
    adaptorConfigs = new ArrayList<File>();
    tempAdaptorConfigs = new ArrayList<File>();
    startLoader();
    
  }
  
  public void startLoader() {
    cancelLoader();
    loaderTimer = new Timer();
    loaderTimer.schedule(new LoaderTask(this),new Date(), LOADER_PERIOD);
  }
  
  public void cancelLoader() {
    if (loaderTimer != null) 
      loaderTimer.cancel();
  }
  
  public boolean configFileExist(File f) {
    // WARNING THIS WON'T WORK IN WINDOWS
    return adaptorConfigs.contains(f); 
  }
  
  public void addToTempAdaptorConfigs(File f) {
    tempAdaptorConfigs.add(f);
  }
  
  public File getAdaptorFolder() {
    return this.adaptorFolder;
  }
  
  public void setAdaptorHolder(File f) {
    this.adaptorFolder = f;
  }
  
  public void loadAdaptors() {
    for (File cf: tempAdaptorConfigs) {
      Properties properties = getProperties(cf);
      
      String ajName = this.ADAPTOR_JOB_PACKAGE+"."
                          +properties.getProperty("adaptor.adaptorjob.type");
      
      IAdaptorJob adaptorJob = createAdaptorJob(ajName);
      adaptorJob.initialize(properties);
      
      CoreScheduler cs = CoreScheduler.getCoreScheduler();
      cs.scheduleAdaptorJob(adaptorJob);
      
    }
  }
  
  
  private Properties getProperties(File file) {
    Properties tmp = new Properties();
    try {
      tmp.load(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return tmp;
  }
  
  
  private IAdaptorJob createAdaptorJob(String name) {
    IAdaptorJob adaptorJob;
    try {
       adaptorJob = (IAdaptorJob) Class.forName(name).newInstance();
       return adaptorJob;
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
  
  
  
  class LoaderTask extends TimerTask {

    private AdaptorLoader adaptorLoader;
    
    public LoaderTask(AdaptorLoader aLoader) {
      adaptorLoader = aLoader;
    }
    
    private void scanForAdaptors(File f) {
      File[] tempList; 
      if (f.isDirectory()) {
        tempList = f.listFiles();
        for (File i : tempList)
          scanForAdaptors(i);
        }
      else {
        if (!adaptorLoader.configFileExist(f)) 
          adaptorLoader.addToTempAdaptorConfigs(f);
      }
    }
    
    @Override
    public void run() {
      scanForAdaptors(adaptorLoader.getAdaptorFolder());
      adaptorLoader.loadAdaptors();
    }
    
  }
  
}
