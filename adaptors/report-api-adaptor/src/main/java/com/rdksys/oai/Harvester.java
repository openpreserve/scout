
package com.rdksys.oai;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.rdksys.oai.data.Metadata;
import com.rdksys.oai.data.Record;
import com.rdksys.oai.data.Header;
import com.rdksys.oai.data.RecordIterator;
import com.rdksys.oai.repository.Identify;
import com.rdksys.oai.repository.MetadataFormat;
import com.rdksys.oai.repository.Set;

/**
 * @author David Uvalle, david.uvalle@gmail.com
 * @version 0.1
 * 
 */
public class Harvester {

	private String baseUrl;
	private boolean useHarvestFromFile = false;
	private String resumptionToken;
	private String identifiersResumptionToken ="";
	private String setResumptionToken = "";
	private List<Record> recordList;
	private boolean hasResumptionToken = false;
	private boolean hasIdentifierResumptionToken = false;
	private boolean hasSetResumptionToken = false;
	private boolean debugging = false;
	
	private static final String version = "0.1";
	
	/**
	 * Constructs the harvester using a repository URL.
	 * @param baseUrl A repository valid URL.
	 */
	public Harvester(String baseUrl) throws Exception {
		if(baseUrl==null || baseUrl.isEmpty())
			throw new Exception("baseUrl cannot be null");
		recordList = new ArrayList<Record>();
		this.baseUrl = baseUrl;
		deleteTmpFile(getMD5Filename(baseUrl));
	}
	
	/**
	 * Constructs the harvester using a repository URL, with a flag to use a harvest temporary file.
	 * @param baseUrl A repository valid URL.
	 * @param useHarvestFromFile A boolean flag.
	 * @throws FileNotFoundException If the file does not exists yet.
	 */
	public Harvester(String baseUrl, boolean useHarvestFromFile) throws FileNotFoundException, Exception  {
		if(baseUrl==null || baseUrl.isEmpty())
			throw new Exception("baseUrl cannot be null");
		recordList = new ArrayList<Record>();
		this.baseUrl = baseUrl;
		this.useHarvestFromFile = useHarvestFromFile;
		if(!fileExists(getMD5Filename(baseUrl))) {
			throw new FileNotFoundException("use the other constructor.");
		}
	}
		
	private OMElement getReaderFromHttpGet(String baseUrl,String verb) throws ClientProtocolException, IOException, XMLStreamException, InterruptedException  {
		HttpClient httpclient = new DefaultHttpClient();		
		HttpGet httpget = new HttpGet(baseUrl+"?verb="+verb);
		httpget.addHeader("User-Agent", "joailib v"+version);
		httpget.addHeader("From", "david.uvalle@gmail.com");
		
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
			StatusLine status = response.getStatusLine();
			
			if(debugging) {
				System.out.println(response.getStatusLine());
				org.apache.http.Header[] header =  response.getAllHeaders();
				for(int i=0;i<header.length;i++) {
					System.out.println(header[i].getName()+" value: "+header[i].getValue());
				}
			}
			
			if(status.getStatusCode() == 503)
			{
				org.apache.http.Header[] headers = response.getAllHeaders();
				for(int i=0;i<headers.length;i++) {
					if(headers[i].getName().equals("Retry-After")) {
						String retry_time = headers[i].getValue();
						Thread.sleep(Integer.parseInt(retry_time)*1000);
						httpclient.getConnectionManager().shutdown();
						httpclient = new DefaultHttpClient();
						response = httpclient.execute(httpget);
					}
				}
			}
			
		
			
			
		}
		catch(ClientProtocolException clientProtocolException) {
			throw new ClientProtocolException(clientProtocolException);
		}
		catch(IOException ioException) {
			throw new IOException(ioException);
		}
		
		HttpEntity entity = response.getEntity();
		InputStream instream = null;
		BufferedInputStream bis = null;
		XMLStreamReader parser = null;
		
		try {
			instream = entity.getContent();
			bis = new BufferedInputStream(instream);
			
		}
		catch(IOException ioException) {
			throw new IOException(ioException);
		}
		
		try {
			parser = XMLInputFactory.newInstance().createXMLStreamReader(bis);
		} 
		catch(XMLStreamException xmlStreamException ){
			throw new XMLStreamException(xmlStreamException);
		}
		StAXOMBuilder builder = new StAXOMBuilder(parser);
		OMElement documentElement =  builder.getDocumentElement();
		return documentElement;
	}

	/**
	 * Returns an {@link Identify} object containing information about the OAI respository.
	 * @return	A {@link Identify} object.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Identify identify() throws Exception {
		// TODO: Add compression and description handling.
		OMElement documentElement = null;
		try {
			documentElement = getReaderFromHttpGet(baseUrl, "Identify");
		}
		catch(Exception e) {
			throw new Exception(e.getMessage());
		}		
		Iterator<OMElement> it = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","Identify"));
		it.hasNext();
		Iterator<OMElement> identifyElements = it.next().getChildElements();
		Identify identify = new Identify();
		while(identifyElements.hasNext()) {
			OMElement eleme = identifyElements.next();
			
			if(eleme.getLocalName().equals("repositoryName")) {
				identify.setRepositoryName(eleme.getText());
			}
			else if(eleme.getLocalName().equals("protocolVersion")) {
				identify.setProtocolVersion(eleme.getText());
			}
			else if(eleme.getLocalName().equals("baseURL")) {
				identify.setBaseUrl(eleme.getText());
			}
			else if(eleme.getLocalName().equals("earliestDatestamp")) {
				identify.setEarliestDateStamp(eleme.getText());
			}
			else if(eleme.getLocalName().equals("deletedRecord")) {
				identify.setDeletedRecord(eleme.getText());
			}
			else if(eleme.getLocalName().equals("granularity")) {
				identify.setGranularity(eleme.getText());
			}
			else if(eleme.getLocalName().equals("adminEmail")) {
				identify.addAdminEmail(eleme.getText());
			}
		}
		return identify;
	}
	
	/**
	 * Returns a List of {@link Header} type, using selective harvesting.
	 * @param from A date.
	 * @param until A date.
	 * @param set A set name supported by the respository.
	 * @return {@link List}
	 * @throws Exception
	 */
	public List<Header> listIdentifiers(String from,String until,String set) throws Exception {
		List<Header> listIdentifiers = new ArrayList<Header>();
		List<Header> tmpListIdentifiers = null;
		listIdentifiers = listIdentifiers("",from,until,set);
		if(hasIdentifierResumptionToken) {
			while(!this.identifiersResumptionToken.isEmpty()) 
			{
				tmpListIdentifiers = listIdentifiers(this.identifiersResumptionToken,null,null,null);
				for(Header h:tmpListIdentifiers) 
				{
					listIdentifiers.add(h);
				}
			}
		}
		return listIdentifiers;
	}
	
	/**
	 * Returns a List of {@link Header} type.
	 * @return A {@link List} of headers.
	 */
	public List<Header> listIdentifiers() throws Exception {
		List<Header> listIdentifiers = new ArrayList<Header>();
		List<Header> tmpListIdentifiers = null;
		listIdentifiers = listIdentifiers("",null,null,null);
		
		if(hasIdentifierResumptionToken) {
			while(!this.identifiersResumptionToken.isEmpty()) 
			{
				tmpListIdentifiers = listIdentifiers(this.identifiersResumptionToken,null,null,null);
				
				for(Header h:tmpListIdentifiers) 
				{
					listIdentifiers.add(h);
				}
			}
		}
		return listIdentifiers;
	}

	/**
	 * listIdentifiers() auxiliar method.
	 * @param resumptionToken
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Header> listIdentifiers(String resumptionToken,String from,String until,String set) throws Exception {
		// TODO: handle status attribute
		OMElement documentElement = null;
		List<Header> recordHeaderList = new ArrayList<Header>();
		try {
			if(identifiersResumptionToken.isEmpty())
			{
				if(from == null && until == null && set == null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix=oai_dc");
				else if(from!=null && until == null && set == null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix=oai_dc&from="+from);
				else if(from==null && until != null && set == null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix=oai_dc&until="+until);
				else if(from==null && until == null && set != null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix=oai_dc&set="+set);
				else if(from!=null && until != null && set == null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix=oai_dc&from="+from+"&until="+until);
				else if(from!=null && until != null && set != null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix=oai_dc&from="+from+"&until="+until+"&set"+set);
				else if(from==null && until != null && set != null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix=oai_dc&until="+until+"&set"+set);
				else if(from!=null && until == null && set != null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix=oai_dc&from="+from+"&set"+set);
			}
			else
			{
				documentElement = getReaderFromHttpGet(baseUrl, "ListIdentifiers&resumptionToken="+resumptionToken);	
			}
		}
		catch(Exception e) {
			throw new Exception(e.getMessage());
		}
		
		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			sendException(getRecordError.next());
			return null;
		}
		
		
		Iterator<OMElement> getIdentifiers = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListIdentifiers"));
		getIdentifiers.hasNext();
			
		Iterator<OMElement> getHeaders = getIdentifiers.next().getChildElements();
		while(getHeaders.hasNext()) {
			OMElement head = getHeaders.next();
			Iterator<OMElement> getHeaderContent = head.getChildElements();
			Header header = new Header();
			if(head.getLocalName().equals("header")) {
				while(getHeaderContent.hasNext()) {
					OMElement headerContent = getHeaderContent.next();
					if(headerContent.getLocalName().equals("identifier")) {
						header.setIdentifier(headerContent.getText());
					}
					else if(headerContent.getLocalName().equals("datestamp")) {
						header.setDatestamp(headerContent.getText());
					}
					else if(headerContent.getLocalName().equals("setSpec")) {
						header.addSpec(headerContent.getText());
					}
					
				}
				this.identifiersResumptionToken = "";
			}
			else if(head.getLocalName().equals("resumptionToken")) {
				hasIdentifierResumptionToken = true;
				this.identifiersResumptionToken = head.getText();
				continue;
			}
			recordHeaderList.add(header);
		}
		return recordHeaderList;
	}
	
	/**
	 * Returns a List of {@link MetadataFormat}s supported by the repository.
	 * @return A {@link List} of MetadataFormats. 
	 * @throws Exception
	 */
	public List<MetadataFormat> listMetadataFormats() throws Exception {
		return listMetadataFormats("");
	}
	
	/**
	 * Returns a List of {@link MetadataFormat}s from a given identifier.
	 * @param identifier valid OAI document identifier.
	 * @return {@link List}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<MetadataFormat> listMetadataFormats(String identifier) throws Exception {
		//TODO: descriptions needs to be handled too.
		
		OMElement documentElement = null;
		try {
			if(!identifier.isEmpty()) {
				documentElement = getReaderFromHttpGet(baseUrl, "ListMetadataFormats&identifier="+identifier);
			} 
			else {
				documentElement = getReaderFromHttpGet(baseUrl, "ListMetadataFormats");
			}
		}
		catch(Exception e) {	
			throw new Exception(e.getMessage());
		}
		
		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			sendException(getRecordError.next());
			return null;
		}
		
		List<MetadataFormat> metadataFormatList = new ArrayList<MetadataFormat>();
		
		Iterator<OMElement> listMetadataFormats = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListMetadataFormats"));		
		listMetadataFormats.hasNext();
		Iterator<OMElement> metadataFormats = listMetadataFormats.next().getChildElements();
		while(metadataFormats.hasNext()) {
			Iterator<OMElement> metaContent = metadataFormats.next().getChildElements();
			MetadataFormat metadataFormat = new MetadataFormat();
			while(metaContent.hasNext()) {
				OMElement contentElement = metaContent.next();
				if(contentElement.getLocalName().equals("metadataPrefix")) {
					metadataFormat.setMetadataPrefix(contentElement.getText());
				}
				else if(contentElement.getLocalName().equals("schema")) {
					metadataFormat.setSchema(contentElement.getText());
				}
				else if(contentElement.getLocalName().equals("metadataNamespace")) {
					metadataFormat.setMetadataNamespace(contentElement.getText());
				}
			}
			metadataFormatList.add(metadataFormat);
		}
		return metadataFormatList;
	}
	
	/**
	 * Returns a List of {@link Set} type.
	 * @return {@link List}
	 * @throws Exception
	 */
	public List<Set> listSets() throws Exception {
		List<Set> listSets = new ArrayList<Set>();
		List<Set> tmpSetList = null;
		listSets = listSets("");
		if(hasSetResumptionToken) {
			while(!this.setResumptionToken.isEmpty()) 
			{
				tmpSetList = listSets(this.setResumptionToken);
				for(Set s:tmpSetList) 
				{
					listSets.add(s);
				}
			}
		}
		return listSets;
	}
	
	/**
	 * Returns a List of {@link Set}s supported by the repository.
	 * @return {@link List}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<Set> listSets(String resumptionToken) throws Exception {
		// TODO: handle descriptions when getting set lists
		
		OMElement documentElement = null;
		List<Set> setList = new ArrayList<Set>();
		try {
			
			if(setResumptionToken.isEmpty())
			{
				documentElement = getReaderFromHttpGet(baseUrl, "ListSets");
			}
			else {
				documentElement = getReaderFromHttpGet(baseUrl, "ListSets&resumptionToken="+resumptionToken);
			}
		}
		catch(Exception e) {
			throw new Exception("error!"+e.getMessage());
		}
		
		
		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			sendException(getRecordError.next());
			return null;
		}
		
		
		Iterator<OMElement> listSets = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListSets"));
		listSets.hasNext();
		Iterator<OMElement> sets = listSets.next().getChildElements();
		while(sets.hasNext()) {
			OMElement setEl = sets.next();
			Iterator<OMElement> setContent = setEl.getChildElements();
			Set set = new Set();
			
			if(setEl.getLocalName().equals("set")) {
			
				while(setContent.hasNext()) {
						OMElement contentElement = setContent.next();
						if(contentElement.getLocalName().equals("setSpec")) {
							set.setSetSpec(contentElement.getText());
						}
						else if(contentElement.getLocalName().equals("setName")) {
							set.setSetName(contentElement.getText());
						}
				}
				this.setResumptionToken = "";
			}
			else if(setEl.getLocalName().equals("resumptionToken")) {
				hasSetResumptionToken = true;
				this.setResumptionToken = setEl.getText();
				continue;
			}
			setList.add(set);
		}
		return setList;
	}
		
	/**
	 * 	Returns a Record object harvested by his identifier 
	 * 	using 'oai_dc' as metadataPrefix 
	 * @param identifier A OAI document identifier.
	 * @return	Record 
	 */
	@SuppressWarnings("unchecked")
	public Record getRecord(String identifier) throws Exception {
		OMElement documentElement = null;
		try {
			documentElement = getReaderFromHttpGet(baseUrl, "GetRecord&identifier="+identifier+"&metadataPrefix=oai_dc");	
		}
		catch(Exception e) {
			throw new Exception(e.getMessage());
		}
		
		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			sendException(getRecordError.next());
			return null;
		}
		
		Iterator<OMElement> getRecord = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","GetRecord"));
		getRecord.hasNext();
		Iterator<OMElement> record = getRecord.next().getChildElements();
		Record nrecord = new Record();
		while(record.hasNext()) {
			Iterator<OMElement> recordContent = record.next().getChildElements();
			while(recordContent.hasNext()) {
				
				OMElement type = recordContent.next();
				if(type.getLocalName().equals("header")) {
					Header header = new Header();
					Iterator<OMElement> headerContent = type.getChildElements();
					while(headerContent.hasNext()) {
						OMElement headerElement = headerContent.next();
						extractHeader(headerElement,header);
					}
					nrecord.setHeader(header);
				}
				else if(type.getLocalName().equals("metadata")) {
					Iterator<OMElement> oai_dc = type.getChildElements();
					while(oai_dc.hasNext()) {
						Iterator<OMElement> metaContent = oai_dc.next().getChildElements();
						Metadata metadata = new Metadata();
						while(metaContent.hasNext()) {
							OMElement metaElement = metaContent.next();
							extractMetadata(metaElement,metadata);
						}
						nrecord.setMetadata(metadata);
					}
				}
				
			}
		}
		
		return nrecord;
	}
	
	/**
	 * Selective harvest of {@link Record}s from a repository 
	 * and store them in a temporal file for later use.
	 * @param from A date.
	 * @param until A date.
	 * @param set A set supported by the repository.
	 * @return {@link RecordIterator}
	 * @throws Exception
	 * 
	 */
	public RecordIterator listRecords(String from,String until,String set) throws Exception {
		List<Record> listRecords = null;
		boolean writeToDisk = true;
		RecordIterator rec;
		
		if(useHarvestFromFile)
			writeToDisk = false;	
		
		if(writeToDisk) {
			listRecords = listRecords("",from,until,set);
			int listSize = listRecords.size();
			writeOnDisk(listRecords);
			while(true) {
				if(hasResumptionToken) {
					listRecords = listRecords(this.resumptionToken,from,until,set);
				}
				else
				{
					if(listSize == 0)
						writeOnDisk(listRecords);
					break;
				}
			}
		}
		rec = new RecordIterator(getMD5Filename(baseUrl));
		return rec;
	}
	
	/**
	 * Harvest all the {@link Record}s from a repository
	 * and store them in a temporal file for later use.
	 * @return {@link RecordIterator}
	 * @throws FileNotFoundException
	 * @throws Exception
	 * 
	 */
	public RecordIterator listRecords() throws FileNotFoundException, Exception {
		List<Record> listRecords = null;
		boolean writeToDisk = true;
		RecordIterator rec;
		
		if(useHarvestFromFile)
			writeToDisk = false;	
		
		if(writeToDisk) {
			listRecords = listRecords("",null,null,null);
			writeOnDisk(listRecords);
			while(true) {
				if(hasResumptionToken) {
					listRecords = listRecords(this.resumptionToken,null,null,null);
				}
				else
				{
					writeOnDisk(listRecords);
					break;
				}
			}
		}
		rec = new RecordIterator(getMD5Filename(baseUrl));
		return rec;
	}
	
	@SuppressWarnings("unchecked")
	private List<Record> listRecords(String resumptionToken,String from,String until,String set) throws Exception {
		OMElement documentElement = null;
		this.hasResumptionToken = false;
		try {
			documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix=oai_dc");
			
			if(resumptionToken.isEmpty()) {	
				if(from==null && until == null && set == null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix=oai_dc");
				else if(from!=null && until == null && set == null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix=oai_dc&from="+from);
				else if(from==null && until != null && set == null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix=oai_dc&until="+from);
				else if(from==null && until == null && set != null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix=oai_dc&set="+set);
				else if(from!=null && until != null && set == null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix=oai_dc&from="+from+"&until="+until);
				else if(from!=null && until != null && set != null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix=oai_dc&from="+from+"&until="+until+"&set="+set);
				else if(from==null && until != null && set != null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix=oai_dc&until="+until+"&set="+set);
				else if(from!=null && until == null && set != null)
					documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&metadataPrefix=oai_dc&from="+from+"&set="+set);
			}
			else {
				documentElement = getReaderFromHttpGet(baseUrl, "ListRecords&resumptionToken="+resumptionToken);
			}	
		}
		catch(Exception e) {
			throw new Exception(e.getMessage());
		}
		
		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			sendException(getRecordError.next());
			return null;
		}
		
		Iterator<OMElement> getListRecords = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListRecords"));		
		getListRecords.hasNext();
		Iterator<OMElement> getRecords = getListRecords.next().getChildElements();
		while(getRecords.hasNext()) {
			OMElement elementType = getRecords.next();
			
			if(elementType.getLocalName().equals("record")) {
				
				Iterator<OMElement> getRecordContent = elementType.getChildElements();
				Record record = new Record();
				Header header = new Header();
				Metadata metadata = new Metadata();
				while(getRecordContent.hasNext()) {
					OMElement recordElement = getRecordContent.next();
					if(recordElement.getLocalName().equals("header")) {
						Iterator<OMElement> headerContents = recordElement.getChildElements();
						while(headerContents.hasNext()) {
							OMElement headerContent = headerContents.next();
							extractHeader(headerContent,header);
						}
						
					}
					else if(recordElement.getLocalName().equals("metadata")) {
						Iterator<OMElement> metadataContents = recordElement.getChildElements();
						metadataContents.hasNext();
						Iterator<OMElement> dcContents = metadataContents.next().getChildElements();
						while(dcContents.hasNext()) {
							OMElement dcElement = dcContents.next();
							extractMetadata(dcElement,metadata);
						}
						record.setMetadata(metadata);
					}
					
				}
				record.setMetadata(metadata);
				record.setHeader(header);
				recordList.add(record);
				
			} else if(elementType.getLocalName().equals("resumptionToken")) {
				hasResumptionToken = true;
				writeOnDisk(recordList);
				recordList.clear();
				this.resumptionToken = elementType.getText();
			}			

		}
		return recordList;
	}
	
	// Helper methods start here.
	private void extractHeader(OMElement headerElement,Header header) {
		if(headerElement.getLocalName().equals("identifier")) {
			header.setIdentifier(headerElement.getText());
			
		}
		else if(headerElement.getLocalName().equals("datestamp")) {
			header.setDatestamp(headerElement.getText());
			
		}
		else if(headerElement.getLocalName().equals("setSpec")) {
			header.addSpec(headerElement.getText());
		}
	}
	
	private void extractMetadata(OMElement metaElement,Metadata metadata) {
		if(metaElement.getLocalName().equals("title")) {
			metadata.addTitle(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("creator")) {
			metadata.addCreator(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("type")) {
			metadata.addType(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("source")) {
			metadata.addSource(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("language")) {
			metadata.addLanguage(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("identifier")) {
			metadata.addIdentifier(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("contributor")) {
			metadata.addContributor(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("subject")) {
			metadata.addSubject(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("publisher")) {
			metadata.addPublisher(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("date")) {
			metadata.addDate(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("format")) {
			metadata.addFormat(metaElement.getText());
		}
		else if(metaElement.getLocalName().equals("description")) {
			metadata.addDescription(metaElement.getText());
		}
	}
	
	private String getMD5Filename(String baseUrl) {
		String md5 = "";
		try {
			MessageDigest mdEnc = MessageDigest.getInstance("MD5");
			mdEnc.update(baseUrl.getBytes(),0,baseUrl.length());
			md5 = new BigInteger(1,mdEnc.digest()).toString(16);
		}
		catch(NoSuchAlgorithmException e) { }
		return md5;
	}
	
	private void writeOnDisk(List<Record> recordList) {
		
		String filename = getMD5Filename(baseUrl);
		
		File file = new File(filename);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		MyObjectOutputStream moos = null;
		
		
		if(file.exists()) {
			try {
				fos = new FileOutputStream(file,true);
				//oos = new ObjectOutputStream(fos);
				moos = new MyObjectOutputStream(fos);
				for(Record r:recordList){
					moos.writeObject(r);
				}
				moos.flush(); moos.close();
				
			} catch(Exception e) { 
				System.out.println("Append error "+e);
			}
		}
		else {
			try {
				if(file.createNewFile())
				{
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					for(Record r:recordList) {
						oos.writeObject(r);
					}
					oos.flush();
					oos.close();
				}
				else {
					System.out.println("Cannot create file");
				}
				
			}
			catch(Exception e) {
				System.out.println("Create error "+e);
			}
		}
	}
	
	private boolean fileExists(String filename) {
		File tempFile = new File(filename);
		return tempFile.exists();
	}
	
	private void deleteTmpFile(String filename) {
		File tempFile = new File(filename);
		if(tempFile.exists())
			tempFile.delete();
	}
	
	/**
	 * Exceptions wrapper for common OAI errors.
	 * @param element
	 * @throws Exception
	 */
	private void sendException(OMElement element) throws Exception {
		String msg = null;
		if(!element.getText().isEmpty())
			msg = ": "+element.getText();
		OMAttribute attr = element.getAttribute(new QName("code"));
		
		if(attr.getAttributeValue().equals("badArgument")) {
			throw new Exception("badArgument "+msg);
		}
		else if(attr.getAttributeValue().equals("cannotDisseminateFormat")) {
			throw new Exception("cannotDisseminateFormat "+msg);
		}
		else if(attr.getAttributeValue().equals("idDoesNotExist")) {
			throw new Exception("idDoesNotExist "+msg);
		}
	}
	
	
}
