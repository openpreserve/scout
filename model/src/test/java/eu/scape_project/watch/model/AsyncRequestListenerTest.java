package eu.scape_project.watch.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.dao.DOListener;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.EntityType;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Plan;
import eu.scape_project.watch.domain.Property;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Source;
import eu.scape_project.watch.domain.SourceAdaptor;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.utils.JavaUtils;
import eu.scape_project.watch.utils.KBUtils;
import eu.scape_project.watch.utils.exceptions.InvalidJavaClassForDataTypeException;
import eu.scape_project.watch.utils.exceptions.UnsupportedDataTypeException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

/**
 * 
 * @author Luis Faria <lfaria@keep.pt>
 */
public class AsyncRequestListenerTest {

  /**
   * Temporary data folder.
   */
  private File dataTempDir;

  /**
   * Setup knowledge base.
   * 
   * @throws IOException
   *           Error connecting to data folder.
   */
  @Before
  public void setup() throws IOException {
    dataTempDir = JavaUtils.createTempDirectory();
    KBUtils.dbConnect(dataTempDir.getPath(), false);
  }

  /**
   * Delete temporary data folder.
   */
  @After
  public void tearDown() {
    KBUtils.dbDisconnect();
    FileUtils.deleteQuietly(dataTempDir);
  }

  /**
   * Test Entity Type CRUD listeners.
   * 
   * @throws InvalidJavaClassForDataTypeException
   * @throws UnsupportedDataTypeException
   */
  @Test
  public void testAsyncRequestCascadingDeleteListeners() throws UnsupportedDataTypeException,
    InvalidJavaClassForDataTypeException {

    @SuppressWarnings("unchecked")
    final DOListener<AsyncRequest> mockDOListener = Mockito.mock(DOListener.class);

    DAO.addDOListener(AsyncRequest.class, mockDOListener);
    // CREATE DATA
    final EntityType type = new EntityType();
    type.setName("tests");
    type.setDescription("Test entities");

    final Entity entity = new Entity(type, "entity1");
    final Property property = new Property(type, "property1", "property description");

    final PropertyValue pv = new PropertyValue(entity, property, "123");
    
    final Source source = new Source("testsource", "A test source");
    final SourceAdaptor adaptor = new SourceAdaptor("testadaptor", "0.0.1", "default", source, Arrays.asList(type),
      Arrays.asList(property), new HashMap<String, String>());

    DAO.save(type);
    DAO.save(entity);
    DAO.save(property);
    DAO.save(source);
    DAO.save(adaptor);
    DAO.PROPERTY_VALUE.save(adaptor, pv);

    // CREATE ASYNC REQUEST
    final String sparql = "?s watch:entity watch-Entity:" + entity.getName() + ". ?s watch:property watch-Property:"
      + Property.createId(type.getName(), property.getName() + ". FILTER(?s < 200)");
    final RequestTarget target = RequestTarget.PROPERTY_VALUE;
    final List<EntityType> types = Arrays.asList(type);
    final List<Property> properties = Arrays.asList(property);
    final List<Entity> entities = Arrays.asList(entity);
    final long period = 30000;

    final Question question = new Question(sparql, target, types, properties, entities, period);
    final Notification notification = new Notification("test", new HashMap<String, String>());
    final List<Notification> notifications = Arrays.asList(notification);
    final Plan plan = null;

    final Trigger trigger = new Trigger(question, notifications, plan);
    final List<Trigger> triggers = Arrays.asList(trigger);

    final AsyncRequest arequest = new AsyncRequest("test", triggers);

    // CASCADE SAVE
    DAO.save(arequest);

    Mockito.verify(mockDOListener, Mockito.times(1)).onUpdated(arequest);
    Mockito.verify(mockDOListener, Mockito.times(0)).onRemoved(arequest);

    DAO.delete(arequest);

    Mockito.verify(mockDOListener, Mockito.times(1)).onUpdated(arequest);
    Mockito.verify(mockDOListener, Mockito.times(1)).onRemoved(arequest);

    DAO.removeDOListener(AsyncRequest.class, mockDOListener);

  }
}
