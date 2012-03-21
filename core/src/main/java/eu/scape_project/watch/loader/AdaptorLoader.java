package eu.scape_project.watch.loader;

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

import eu.scape_project.watch.core.CoreScheduler;
import eu.scape_project.watch.interfaces.AdaptorJobInterface;
import eu.scape_project.watch.utils.ConfigUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that enables loading adaptors with their config dynamically.
 * 
 * @author kresimir
 * 
 */
public class AdaptorLoader {

  private static final Logger LOG = LoggerFactory.getLogger(AdaptorLoader.class);

  private static final long LOADER_PERIOD = 2 * 10 * 1000L;

  private static String EXTENSION = ".properties";

  private static final String ADAPTOR_JOB_PACKAGE = "eu.scape_project.watch.components";

  private File adaptorFolder;
  private List<File> adaptorConfigs;

  private List<File> tempAdaptorConfigs;

  private Timer loaderTimer;

  private CoreScheduler coreScheduler;

  public CoreScheduler getCoreScheduler() {
    return coreScheduler;
  }

  public void setCoreScheduler(CoreScheduler coreScheduler) {
    this.coreScheduler = coreScheduler;
  }

  public AdaptorLoader() {
    ConfigUtils conf = new ConfigUtils();
    adaptorFolder = new File(conf.getStringProperty("watch.adaptors.folder"));
    adaptorConfigs = new ArrayList<File>();
    tempAdaptorConfigs = new ArrayList<File>();
    LOG.info("AdaptorLoader initialized");
  }

  public void startLoader() {
    cancelLoader();
    loaderTimer = new Timer();
    loaderTimer.schedule(new LoaderTask(), new Date(), LOADER_PERIOD);
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
    LOG.info(tempAdaptorConfigs.size() + " new adaptors found");
    for (File cf : tempAdaptorConfigs) {
      Properties properties = getProperties(cf);

      String ajName = this.ADAPTOR_JOB_PACKAGE + "." + properties.getProperty("adaptor.adaptorjob.type");

      AdaptorJobInterface adaptorJob = createAdaptorJob(ajName);
      adaptorJob.initialize(properties);

      coreScheduler.scheduleAdaptorJob(adaptorJob);

      adaptorConfigs.add(cf);
    }
    tempAdaptorConfigs.clear();

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

  private AdaptorJobInterface createAdaptorJob(String name) {
    AdaptorJobInterface adaptorJob;
    try {
      adaptorJob = (AdaptorJobInterface) Class.forName(name).newInstance();
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

    public LoaderTask() {

    }

    private void scanForAdaptors(File f) {
      File[] tempList;
      if (f.isDirectory()) {
        tempList = f.listFiles();
        for (File i : tempList)
          scanForAdaptors(i);
      } else {
        if (f.getAbsolutePath().endsWith(EXTENSION)) {
          if (!configFileExist(f))
            addToTempAdaptorConfigs(f);
        }
      }
    }

    @Override
    public void run() {
      LOG.info("Scanning " + adaptorFolder.getAbsolutePath() + " for adaptors");
      scanForAdaptors(adaptorFolder);
      loadAdaptors();
    }

  }

}
