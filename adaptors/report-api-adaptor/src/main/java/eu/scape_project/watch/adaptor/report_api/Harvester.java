package eu.scape_project.watch.adaptor.report_api;

import info.lc.xmlns.premis_v2.EventComplexType;
import info.lc.xmlns.premis_v2.LinkingObjectIdentifierComplexType;
import info.lc.xmlns.premis_v2.PremisComplexType;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ORG.oclc.oai.harvester2.verb.Identify;
import ORG.oclc.oai.harvester2.verb.ListRecords;
import eu.scape_project.repository.agentdetails.AgentDetails;

public class Harvester {

	/**
	 * A default logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Harvester.class);

	private String baseURL;

	public Harvester(String baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 * Get the oai:protocolVersion value from the Identify response
	 * 
	 * @return the oai:repositoryName value
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws NoSuchFieldException
	 */
	public String getRepositoryName() throws IOException,
			ParserConfigurationException, SAXException, TransformerException {
		LOG.debug("getRepositoryName()");

		Identify identify = new Identify(this.baseURL);
		return identify
				.getSingleString("/oai20:OAI-PMH/oai20:Identify/oai20:repositoryName");
	}

	public Iterator<RecordType> getRecordIterator(final String from,
			final String until, final String set, final String metadataPrefix)
			throws IOException, ParserConfigurationException, SAXException,
			TransformerException, JAXBException {
		LOG.debug(
				"getRecordIterator(from={}, until={}, set={}, metadataPrefix={})",
				new Object[] { from, until, set, metadataPrefix });

		return new RecordIterator(this.baseURL, from, until, set,
				metadataPrefix);

	}

	public String getPremisLinkingObjectSipID(EventComplexType premisEvent) {
		LOG.debug("getPremisLinkingObjectSipID(premisEvent={})", premisEvent);

		String sipID = null;

		if (premisEvent.getLinkingObjectIdentifier() != null) {

			for (LinkingObjectIdentifierComplexType linkingObject : premisEvent
					.getLinkingObjectIdentifier()) {

				if ("SIP ID".equals(linkingObject
						.getLinkingObjectIdentifierType())) {

					sipID = linkingObject.getLinkingObjectIdentifierValue();
				}
			}
		}

		return sipID;
	}

	class RecordIterator implements Iterator<RecordType> {

		private final Logger LOG = LoggerFactory
				.getLogger(RecordIterator.class);

		private ListRecords listRecords = null;
		private Iterator<RecordType> innerIterator;

		public RecordIterator(String baseURL, String from, String until,
				String set, String metadataPrefix) throws IOException,
				ParserConfigurationException, SAXException,
				TransformerException, JAXBException {
			LOG.debug(
					"RecordIterator(from={}, until={}, set={}, metadataPrefix={})",
					new Object[] { from, until, set, metadataPrefix });

			this.listRecords = new ListRecords(baseURL, from, until, set,
					metadataPrefix);

			LOG.debug("ListRecords output is " + this.listRecords.toString());

			this.innerIterator = getInnerIterator();
		}

		@Override
		public boolean hasNext() {
			LOG.debug("hasNext()");
			boolean hasNext = this.innerIterator.hasNext();

			try {

				if (!hasNext
						&& StringUtils.isNotBlank(listRecords
								.getResumptionToken())) {

					this.listRecords = new ListRecords(baseURL,
							this.listRecords.getResumptionToken());

					LOG.debug("ListRecords output is "
							+ this.listRecords.toString());

					this.innerIterator = getInnerIterator();

					hasNext = this.innerIterator.hasNext();
				}

			} catch (TransformerException e) {
				LOG.error(
						"Couldn't determine if there's more records available - "
								+ e.getMessage(), e);
				hasNext = false;
			} catch (NoSuchFieldException e) {
				LOG.error(
						"Couldn't determine if there's more records available - "
								+ e.getMessage(), e);
				hasNext = false;
			} catch (JAXBException e) {
				LOG.error(
						"Couldn't determine if there's more records available - "
								+ e.getMessage(), e);
				hasNext = false;
			} catch (IOException e) {
				LOG.error(
						"Couldn't determine if there's more records available - "
								+ e.getMessage(), e);
				hasNext = false;
			} catch (ParserConfigurationException e) {
				LOG.error(
						"Couldn't determine if there's more records available - "
								+ e.getMessage(), e);
				hasNext = false;
			} catch (SAXException e) {
				LOG.error(
						"Couldn't determine if there's more records available - "
								+ e.getMessage(), e);
				hasNext = false;
			}

			return hasNext;
		}

		@Override
		public RecordType next() {
			LOG.debug("next()");
			return this.innerIterator.next();
		}

		@Override
		public void remove() {
			LOG.debug("remove()");
			throw new UnsupportedOperationException();
		}

		private Iterator<RecordType> getInnerIterator() throws JAXBException {
			LOG.debug("getInnerIterator()");

			// JAXBElement<OAIPMHtype> oaipmhElement = unmarshal(
			// this.listRecords.getDocument(), OAIPMHtype.class,
			// ElementType.class, OaiDcType.class,
			// PremisComplexType.class, AgentDetails.class);

			JAXBElement<OAIPMHtype> oaipmhElement = unmarshal(
					this.listRecords.getDocument(), OAIPMHtype.class,
					PremisComplexType.class, AgentDetails.class);

			List<RecordType> records = oaipmhElement.getValue()
					.getListRecords().getRecord();

			LOG.debug("OAI-PMH>ListRecords contains " + records.size()
					+ " records");

			return records.iterator();
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> JAXBElement<T> unmarshal(Node node, Class<?>... classes)
			throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(classes);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		return (JAXBElement<T>) unmarshaller.unmarshal(node);
	}

}
