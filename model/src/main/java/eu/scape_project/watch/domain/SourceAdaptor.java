package eu.scape_project.watch.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.ModelUtils;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.binding.RdfBean;

/**
 * A software unit that is able to collect specific information from a specific
 * {@link Source} and has the capability to convert/adapt it into a
 * representation that fits the internal data model of Watch.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
@Namespace(KBUtils.WATCH_NS)
@XmlRootElement(name = KBUtils.SOURCE_ADAPTOR)
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdaptor extends RdfBean<SourceAdaptor> {

  /**
   * Create a unique Id based on the name and version.
   * 
   * @param name
   *          The {@link SourceAdaptor} name.
   * @param version
   *          The {@link SourceAdaptor} version.
   * @return The unique Id in the format 'example-v1.0.0'
   */
  public static String createId(final String name, final String version) {
    return name + "-v" + version;
  }

  /**
   * Update Id for new name or version.
   */
  private void updateId() {
    if (this.name != null && this.version != null) {
      id = createId(this.name, this.version);
    }
  }

  /**
   * A unique identifier.
   */
  @Id
  @XmlElement
  private String id;

  /**
   * A name that uniquely identifies the adaptor, regardless of the version.
   */
  @XmlElement
  private String name;

  /**
   * The version of the adaptor, preferably in the 1.0.1 format.
   */
  @XmlElement
  private String version;

  /**
   * The source from which this adaptor fetches information from.
   */
  @XmlElement
  @JsonProperty
  private Source source;

  /**
   * The entity types that are gathered.
   */
  @XmlElement
  @JsonProperty
  private List<EntityType> types;

  /**
   * The properties that are gathered.
   */
  @XmlElement
  @JsonProperty
  private List<Property> properties;

  /**
   * The source adaptor configuration.
   */
  @XmlElement(name = "entry")
  @XmlElementWrapper(name = "parameters")
  @JsonProperty
  private List<DictionaryItem> configuration = new ArrayList<DictionaryItem>();

  /**
   * Create a new empty source adaptor.
   */
  public SourceAdaptor() {
    super();
  }

  /**
   * Create a new source adaptor.
   * 
   * @param name
   *          A name that uniquely identifies the adaptor, regardless of the
   *          version.
   * @param version
   *          The version of the adaptor, preferably in the 1.0.1 format.
   * @param source
   *          The source from which this adaptor fetches information from.
   * @param types
   *          The entity types this adaptor creates with the information
   *          fetched.
   * @param properties
   *          The properties this adaptor fills with values with the information
   *          fetched.
   * @param configuration
   *          The source adaptor configuration
   */
  public SourceAdaptor(final String name, final String version, final Source source, final List<EntityType> types,
    final List<Property> properties, final Map<String, String> configuration) {
    super();
    this.name = name;
    this.version = version;
    this.source = source;
    this.types = (types != null ? types : new ArrayList<EntityType>());
    this.properties = (properties != null ? properties : new ArrayList<Property>());
    this.configuration = ModelUtils.mapToEntryList(configuration);

    this.updateId();
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public String getVersion() {
    return version;
  }

  public List<EntityType> getTypes() {
    return types;
  }

  public void setTypes(final List<EntityType> types) {
    this.types = types;
  }

  public List<Property> getProperties() {
    return properties;
  }

  public void setProperties(final List<Property> properties) {
    this.properties = properties;
  }

  /**
   * Get the configuration as a map of strings.
   * 
   * @return A map of strings with each entry as a key-value pair.
   */
  public Map<String, String> getConfiguration() {
    return ModelUtils.entryListToMap(this.configuration);
  }

  /**
   * Set the configuration as a map of strings.
   * 
   * @param configuration
   *          A map of strings with each entry as a key-value pair.
   */
  public void setConfiguration(final Map<String, String> configuration) {
    this.configuration = ModelUtils.mapToEntryList(configuration);
  }

  /**
   * Set version and update Id.
   * 
   * @param version
   *          The version of the adaptor, preferably in the 1.0.1 format.
   */
  public void setVersion(final String version) {
    this.version = version;

    this.updateId();
  }

  public Source getSource() {
    return source;
  }

  public void setSource(final Source source) {
    this.source = source;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    result = prime * result + ((types == null) ? 0 : types.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SourceAdaptor other = (SourceAdaptor) obj;
    if (configuration == null) {
      if (other.configuration != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(this.configuration, other.configuration)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (properties == null) {
      if (other.properties != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(this.properties, other.properties)) {
      return false;
    }
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    if (types == null) {
      if (other.types != null) {
        return false;
      }
    } else if (!CollectionUtils.isEqualCollection(this.properties, other.properties)) {
      return false;
    }
    if (version == null) {
      if (other.version != null) {
        return false;
      }
    } else if (!version.equals(other.version)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "SourceAdaptor [name=" + name + ", version=" + version + ", source=" + source + ", types="
      + Arrays.toString(this.types.toArray()) + ", properties=" + Arrays.toString(this.properties.toArray())
      + ", configuration=" + Arrays.toString(this.configuration.toArray()) + "]";
  }

}
