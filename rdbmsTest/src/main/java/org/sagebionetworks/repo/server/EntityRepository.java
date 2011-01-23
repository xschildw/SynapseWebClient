package org.sagebionetworks.repo.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDODataStoreException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * Example persistence manager class
 * <p>
 *
 * Code lifted from the <a
 * href="http://code.google.com/p/maven-gae-plugin/source/browse/trunk/gae-archetype-jsp/src/main/resources/archetype-resources/src/main/java/server/MessageRepository.java?r=738http://code.google.com/p/maven-gae-plugin/source/browse/trunk/gae-archetype-jsp/src/main/resources/archetype-resources/src/main/java/model/Message.java?r=738">maven-gae-plugin</a>
 * template and modified a tiny bit to serve as an example
 * @param <T>
 */
public class EntityRepository<T> {

    //    static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");
    static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("sage-mysql");

    private Class<T> theModelClass;

    /**
     * @param theModelClass
     */
    public EntityRepository(Class<T> theModelClass) {
      this.theModelClass = theModelClass;
    }

    /**
     * Return all entities stored in the repository
     *
     * @return collection of all entities stored in the repository
     */
    public List<T> getAll() {
        PersistenceManager pm = pmfInstance.getPersistenceManager();
        try {
            List<T> entities = new ArrayList<T>();
            Extent<T> extent = pm.getExtent(theModelClass, false);
            for (T entity : extent) {
                entities.add(entity);
            }

            extent.closeAll();

            return entities;
        }
        finally {
            pm.close();
        }
    }

    /**
     * @param entity
     */
    public void create(T entity) {
        PersistenceManager pm = pmfInstance.getPersistenceManager();
        try {
            pm.makePersistent(entity);
        }
        finally {
            pm.close();
        }
    }

    /**
     * @param id
     * @return the entity corresponding to the id, null otherwise
     */
    public T getById(Long id) {
        PersistenceManager pm = pmfInstance.getPersistenceManager();
        try {
            T entity = pm.getObjectById(theModelClass, id);
            return entity;
        }
        catch(JDODataStoreException ex) {
            return null;
        }
        finally {
            pm.close();
        }
    }

    /**
     * @param id
     * @return true if found and deleted, false otherwise
     */
    public boolean deleteById(Long id) {
        PersistenceManager pm = pmfInstance.getPersistenceManager();
        try {
            pm.deletePersistent(pm.getObjectById(theModelClass, id));
            return true;
        }
        catch(JDODataStoreException ex) {
            return false;
        }
        finally {
            pm.close();
        }
    }

    /**
     * Return all entities stored in the repository within a particular range<p>
     *
     * TODO this is a dummy class, therefore I have not bothered to implement this yet
     *
     * @param offset
     * @param limit
     * @return collection of all entities stored in the repository
     */
    public List<T> getRange(Integer offset, Integer limit) {
        // TODO implement me!
        return getAll();
    }

    /**
     * Return the count of all entities stored in the repository<p>
     *
     * TODO this is a dummy class, therefore I have not bothered to implement this yet
     *
     * @return the count
     */
    public Integer getCount() {
        // TODO implement me!
        return 42;
    }

}
