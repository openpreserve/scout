package eu.scape_project.watch.core.model;

import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.watch.core.KB;
import eu.scape_project.watch.core.common.ModelUtils;

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
@Namespace(KB.WATCH_NS)
@XmlRootElement(name = KB.SOURCE_ADAPTOR)
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
   * The source from which this adaptor fetchs information from.
   */
  @XmlElement
  @JsonProperty
  private Source source;

  /**
   * The source adaptor configuration.
   */
  @XmlElement(name = "entry")
  @XmlElementWrapper(name = "parameters")
  @JsonProperty
  private Collection<Entry> configuration;

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
   *          The source from which this adaptor fetchs information from.
   * @param configuration
   *          The source adaptor configuration
   */
  public SourceAdaptor(final String name, final String version, final Source source,
    final Map<String, String> configuration) {
    super();
    this.name = name;
    this.version = version;
    this.source = source;
    this.configuration = ModelUtils.mapToEntryList(configuration);

    this.updateId();
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getId() {
    return this.id;
  }

  public String getVersion() {
    return this.version;
  }

  public Map<String, String> getConfiguration() {
    return ModelUtils.entryListToMap(this.configuration);
  }

  public void setConfiguration(Map<String, String> configuration) {
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
    return this.source;
  }

  public void setSource(final Source source) {
    this.source = source;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((source == null) ? 0 : source.hashCode());
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
    if (this.source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!this.source.equals(other.source)) {
      return false;
    }
    if (this.version == null) {
      if (other.version != null) {
        return false;
      }
    } else if (!this.version.equals(other.version)) {
      return false;
    }
    return true;
  }

}
