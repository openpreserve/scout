package eu.scape_project.watch.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KB {

  private static final Logger LOGGER = LoggerFactory.getLogger(KB.class);

  private static KB UNIQUE_INSTANCE;

  private EntityManagerFactory emf;

  private EntityManager em;

  public static synchronized KB getInstance() {
    if (KB.UNIQUE_INSTANCE == null) {
      KB.UNIQUE_INSTANCE = new KB();
      KB.UNIQUE_INSTANCE.createEntityManagerFactory();
      KB.LOGGER.info("KB manager created");
    }

    return KB.UNIQUE_INSTANCE;
  }

  public EntityManager getEntityManager() {
    if (this.em == null) {
      em = this.emf.createEntityManager();
    }

    return this.em;
  }

  public void createEntityManagerFactory() {
//    InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("empire.configuration");
//
//    Properties props = new Properties();
//    try {
//      props.load(stream);
//    } catch (IOException e) {
//      LOGGER.error("Could not read properties file, Empire won't run properly: {}", e.getMessage());
//    }
//
//    Empire.init(new FourStoreEmpireModule());
//    PersistenceProvider provider = Empire.get().persistenceProvider();
//    this.emf = provider.createEntityManagerFactory((String) props.getProperty("0.name"), props);
  }

  public void close() {
    if (this.em != null) {
      this.em.close();
      this.em = null;
    }

    if (this.emf != null) {
      this.emf.close();
    }
  }

  public void persist(Object o) {
    if (o != null) {
      EntityManager em = this.getEntityManager();
      em.getTransaction().begin();
      em.persist(o);
      em.getTransaction().commit();
    } else {
      LOGGER.warn("Cannot persist a null object reference, skipping");
    }
  }

  public void remove(Object o) {
    if (o != null) {
      EntityManager em = this.getEntityManager();
      em.getTransaction().begin();
      em.remove(o);
      em.getTransaction().commit();
    } else {
      LOGGER.warn("Cannot remove a null object reference, skipping");
    }
  }

  /**
   * Returns an entity of the given class with the given property value pair if
   * one exists, or null otherwise. The method queries the knowledgebase for a
   * tripple with the given property (from our ontology) with the passed value.
   * Afterwards it parses the result to obtain the id of the entity and queries
   * again to find the whole entity.
   * 
   * @param clazz
   *          the type of the object to look for
   * @param property
   *          the property that has to be matched (shall match the annotation of
   *          the field on the corresponding entity without the prefix)
   * @param value
   *          the value that the propery shall have
   * @return the object or null
   */
  public <T > T findByProperty(Class<T> clazz, String property, Object value) {
    EntityManager em = this.getEntityManager();
    Query query = em.createQuery(String.format("SELECT * WHERE {?s kb:%s ??param}", property));
    query.setParameter("param", value);

//    MapBindingSet bindings = null;
//    try {
//      bindings = (MapBindingSet) query.getSingleResult();
//
//    } catch (NoResultException e) {
//      LOGGER.warn("No result found. Your query didn't return a match: {}", e.getMessage());
//    }
//    if (bindings == null || bindings.size() == 0) {
//      return null;
//    }
//
//    String id = bindings.getValue("s").stringValue();
//    return em.find(clazz, URI.create(id));
    return null;
  }

  /**
   * Returns a collection of entities that match the given criteria. Works
   * similar as the {@link KB#findByProperty(Class, String, Object)} method,
   * however it returns all matching entities or an empty list if none are
   * found.
   * 
   * @param clazz
   *          the type of the entities.
   * @param property
   *          the property that has to be matched against.
   * @param value
   *          the value of the property.
   * @return a list of objects of the given type that match the property value
   *         pair or an empty list.
   */
  public <T> List<T> findAllByProperty(Class<T> clazz, String property, Object value) {
    EntityManager em = this.getEntityManager();
    Query query = em.createQuery(String.format("SELECT * WHERE {?s kb:%s ??param}", property));
    query.setParameter("param", value);

    List<T> collection = new ArrayList<T>();
//    List<BindingSet> results = (List<BindingSet>) query.getResultList();
//
//    for (BindingSet bs : results) {
//      String id = bs.getValue("s").stringValue();
//      T obj = em.find(clazz, URI.create(id));
//
//      if (obj != null) {
//        collection.add(obj);
//      }
//    }

    return collection;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("Singletons cannot be cloned");
  };

  private KB() {

  }
}
