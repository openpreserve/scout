
package com.rdksys.oai.repository;

import java.util.ArrayList;

/**
 * @author David Uvalle, david.uvalle@gmail.com
 * @version 0.1
 * 
 */
public class Identify {
	private String repositoryName;
	private String baseUrl;
	private String protocolVersion;
	private String earliestDateStamp;
	private String deletedRecord;
	private String granularity;
	private ArrayList<String> adminEmails;
	
	public Identify()
	{
		adminEmails = new ArrayList<String>();
	}
	
	public void addAdminEmail(String email) {
		adminEmails.add(email);
	}
	
	public ArrayList<String> getAdminEmails() {
		return adminEmails;
	}
	
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getProtocolVersion() {
		return protocolVersion;
	}
	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	public String getEarliestDateStamp() {
		return earliestDateStamp;
	}
	public void setEarliestDateStamp(String earliestDateStamp) {
		this.earliestDateStamp = earliestDateStamp;
	}
	public String getDeletedRecord() {
		return deletedRecord;
	}
	public void setDeletedRecord(String deletedRecord) {
		this.deletedRecord = deletedRecord;
	}
	public String getGranularity() {
		return granularity;
	}
	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}
	
	

}
