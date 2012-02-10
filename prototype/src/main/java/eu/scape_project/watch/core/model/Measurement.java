package eu.scape_project.watch.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import eu.scape_project.watch.core.KB;

@Namespace(KB.WATCH_NS)
@XmlRootElement(name = KB.MEASUREMENT)
@XmlAccessorType(XmlAccessType.FIELD)
public class Measurement {

	private static final Logger LOG = LoggerFactory
			.getLogger(Measurement.class);

	@XmlElement
	@JsonProperty
	private PropertyValue propertyValue;

	@XmlElement
	private long timestamp;

	@XmlElement
	@JsonProperty
	private SourceAdaptor adaptor;

	public Measurement() {
		super();
	}

	public Measurement(PropertyValue pv, long t) {
		this.propertyValue = pv;
		this.timestamp = t;
	}

	@Id
	public String getId() {
		return propertyValue.getProperty().getName() + "-" + timestamp;
	}

	public PropertyValue getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(PropertyValue propertyValue) {
		this.propertyValue = propertyValue;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result + ((adaptor == null) ? 0 : adaptor.hashCode());
	// result = prime * result
	// + ((propertyValue == null) ? 0 : propertyValue.hashCode());
	// result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
	// return result;
	// }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Measurement other = (Measurement) obj;
		if (adaptor == null) {
			if (other.adaptor != null)
				return false;
		} else if (!adaptor.equals(other.adaptor))
			return false;
		if (propertyValue == null) {
			if (other.propertyValue != null)
				return false;
		} else if (!propertyValue.equals(other.propertyValue))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	/*
	 * Normally an exception in a lifecycle callback should initiate a rollback.
	 * It seems however, that Empire is not doing that.
	 */
	// @PrePersist
	// void onPrePersist() {
	// LOG.debug("Prepersisting Measurement");
	// if (adaptor == null) {
	// throw new NotNullConstraintException(
	// "Source adaptor in measurement cannot be null");
	// }
	//
	// if (propertyValue == null) {
	// throw new NotNullConstraintException(
	// "Property value in measurement canno be null");
	// }
	//
	// if (timestamp == 0L) {
	// timestamp = new Date().getTime();
	// }
	// }

}
