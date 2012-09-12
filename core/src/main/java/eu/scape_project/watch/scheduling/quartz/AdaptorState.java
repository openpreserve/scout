package eu.scape_project.watch.scheduling.quartz;

import java.util.UUID;

import org.quartz.JobKey;

/**
 * 
 * @author Kresimir Duretec <duretec@ifs.tuwien.ac.at>
 *
 */
public class AdaptorState {

	private String id;
	
	private JobKey jobKey;
	
	public AdaptorState() {
		id = UUID.randomUUID().toString();
	}
	
	public AdaptorState(JobKey jk) {
		this();
		jobKey = jk;
	}

	public String getId() {
		return id;
	}

	public JobKey getJobKey() {
		return jobKey;
	}
	
	public void setJobKey(JobKey jk){
		jobKey = jk;
	}
	
}
