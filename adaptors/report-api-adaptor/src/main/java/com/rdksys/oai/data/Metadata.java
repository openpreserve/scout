
package com.rdksys.oai.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author David Uvalle, david.uvalle@gmail.com
 * @version 0.1
 * 
 */
public class Metadata implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<String>  titleList;
	private ArrayList<String> creatorList;
	private ArrayList<String> subjectList;
	private ArrayList<String> descriptionList;
	private ArrayList<String> contributorList;
	private ArrayList<String> dateList;
	private ArrayList<String> typeList;
	private ArrayList<String> formatList;
	private ArrayList<String> identifierList;
	private ArrayList<String> sourceList;
	private ArrayList<String> languageList;
	private ArrayList<String> relationList;
	private ArrayList<String> coverageList;
	private ArrayList<String> rightsList;
	private ArrayList<String> publisherList;
	
	public Metadata() {
		titleList = new ArrayList<String>();
		creatorList = new ArrayList<String>();
		subjectList = new ArrayList<String>();
		descriptionList = new ArrayList<String>();
		contributorList = new ArrayList<String>();
		dateList = new ArrayList<String>();
		typeList = new ArrayList<String>();
		formatList = new ArrayList<String>();
		identifierList = new ArrayList<String>();
		sourceList = new ArrayList<String>();
		languageList = new ArrayList<String>();
		relationList = new ArrayList<String>();
		coverageList = new ArrayList<String>();
		rightsList = new ArrayList<String>();
		publisherList = new ArrayList<String>();
	}
	
	public void addPublisher(String publisher) {
		publisherList.add(publisher);
	}
	
	public ArrayList<String> getPublisher() {
		return publisherList;
	}
	
	public ArrayList<String> getTitleList() {
		return titleList;
	}



	public ArrayList<String> getCreatorList() {
		return creatorList;
	}



	public ArrayList<String> getSubjectList() {
		return subjectList;
	}



	public ArrayList<String> getDescriptionList() {
		return descriptionList;
	}



	public ArrayList<String> getContributorList() {
		return contributorList;
	}



	public ArrayList<String> getDateList() {
		return dateList;
	}



	public ArrayList<String> getTypeList() {
		return typeList;
	}



	public ArrayList<String> getFormatList() {
		return formatList;
	}



	public ArrayList<String> getIdentifierList() {
		return identifierList;
	}



	public ArrayList<String> getSourceList() {
		return sourceList;
	}



	public ArrayList<String> getLanguageList() {
		return languageList;
	}



	public ArrayList<String> getRelationList() {
		return relationList;
	}



	public ArrayList<String> getCoverageList() {
		return coverageList;
	}



	public ArrayList<String> getRightsList() {
		return rightsList;
	}



	public void addTitle(String title) {
		titleList.add(title);
	}
	
	public void addCreator(String creator) {
		creatorList.add(creator);
	}
	
	public void addSubject(String subject) {
		subjectList.add(subject);
	}
	
	public void addDescription(String description) {
		descriptionList.add(description);
	}
	
	public void addContributor(String contributor) {
		contributorList.add(contributor);
	}
	
	public void addDate(String date) {
		dateList.add(date);
	}
	
	public void addType(String type) {
		typeList.add(type);
	}
	
	public void addFormat(String format) {
		formatList.add(format);
	}
	
	public void addIdentifier(String identifier) {
		identifierList.add(identifier);
	}
	
	public void addSource(String source) {
		sourceList.add(source);
	}
	
	public void addLanguage(String language) {
		languageList.add(language);
	}
	
	public void addRelation(String relation) {
		relationList.add(relation);
	}
	
	public void addCoverage(String coverage) {
		coverageList.add(coverage);
	}
	
	public void addRights(String rights) {
		rightsList.add(rights);
	}
	
	/*
	private String title;
	private String creator;
	private String subject;
	private String description;
	private String date;
	private String type;
	private String identifier;
	private String contributor;
	private String source;
	private String language;
	private String publisher;
	private String format;
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getContributor() {
		return contributor;
	}
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	*/
	
	
	
	
}
