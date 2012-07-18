package eu.scape_project.watch.scheduling.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.interfaces.AdaptorPluginInterface;
import eu.scape_project.watch.interfaces.ResultInterface;
import eu.scape_project.watch.utils.exceptions.PluginException;

public abstract class QuartzAdaptorJob implements Job {

	private static final Logger LOG = LoggerFactory
			.getLogger(QuartzAdaptorJob.class);

	/*
	 * private String adaptorClassName;
	 * 
	 * private String adaptorVersion;
	 * 
	 * private Properties properties;
	 * 
	 * private String adaptorProperties;
	 */

	private QuartzScheduler scheduler;

	private String adaptorId;

	private QuartzListenerManager lManager;

	AdaptorPluginInterface adaptor;
	
	public void setScheduler(QuartzScheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setAdaptorId(String adaptorId) {
		this.adaptorId = adaptorId;
	}

	public void setlManager(QuartzListenerManager lManager) {
		this.lManager = lManager;
	}
	
	public AdaptorPluginInterface getAdaptorPlugin() {
		return adaptor;
	}

	@Override
	public void execute(final JobExecutionContext jec){

	    adaptor = scheduler.getAdaptorPluginInterface(adaptorId);
		
		try {
			while (adaptor.hasNext()){
				ResultInterface result = adaptor.next();
				lManager.notify(adaptor, result);
			}
		} catch (PluginException e) {
			
		}
		
		
		
		
		
		/*
		 * 
		 * // TODO integrate this part with PluginManager final
		 * AdaptorPluginInterface adaptor = (AdaptorPluginInterface)
		 * PluginManager.getDefaultPluginManager().getPlugin( adaptorClassName,
		 * adaptorVersion);
		 * 
		 * if (adaptor == null) {
		 * LOG.warn("No adaptor found in the plugin manager, skipping."); } else
		 * { try { LOG.trace("properties: {}", adaptorProperties); properties =
		 * new Properties(); properties.load(new
		 * ByteArrayInputStream(adaptorProperties.getBytes("UTF-8")));
		 * LOG.trace("properties size {}", properties.keySet().size());
		 * Map<String, String> map = new HashMap<String, String>(); for (Object
		 * key : properties.keySet()) { map.put(key.toString(),
		 * properties.getProperty(key.toString()));
		 * LOG.trace("key {}, value {}", key.toString(),
		 * properties.getProperty(key.toString())); }
		 * 
		 * //TODO improve this part adaptor.setParameterValues(map);
		 * jec.setResult(adaptor.execute()); } catch (PluginException e) {
		 * 
		 * } catch (IOException e) {
		 * 
		 * } catch (InvalidParameterException e) {
		 * 
		 * } }
		 */
	}

	/*
	 * public void setAdaptorClassName(String className) { this.adaptorClassName
	 * = className; }
	 * 
	 * public String getAdaptorClassName() { return this.adaptorClassName; }
	 * 
	 * 
	 * public void setAdaptorVersion(String version) { this.adaptorVersion =
	 * version; }
	 * 
	 * 
	 * public String getAdaptorVersion() { return this.adaptorVersion; }
	 * 
	 * 
	 * public void setAdaptorProperties(String properties) {
	 * this.adaptorProperties = properties; }
	 * 
	 * 
	 * public void initialize(Properties properties) { this.properties =
	 * properties; this.initialize(); }
	 * 
	 * protected Properties getProperties() { return properties; }
	 * 
	 * protected abstract void initialize();
	 */
	/*
	 * @Override public boolean equals(Object object) { if (object==null) {
	 * return false; } if (this == object){ return true; } if (this.getClass()
	 * != object.getClass()){ return false; } QuartzAdaptorJob aJob =
	 * (QuartzAdaptorJob) object; return
	 * this.getAdaptorClassName().equals(aJob.getAdaptorClassName()) &&
	 * this.getAdaptorVersion().equals(aJob.getAdaptorVersion()); }
	 * 
	 * @Override public int hashCode() { final int prime = 31; int result = 1;
	 * result = prime * result + ((this.adaptorClassName == null) ? 0 :
	 * this.adaptorClassName.hashCode()); result = prime * result +
	 * ((this.adaptorVersion == null) ? 0 : this.adaptorVersion.hashCode());
	 * result = prime * result + ((this.adaptorProperties == null) ? 0 :
	 * this.adaptorProperties.hashCode()); result = prime * result +
	 * ((this.properties == null) ? 0 : this.properties.hashCode()); return
	 * result; }
	 */
}
