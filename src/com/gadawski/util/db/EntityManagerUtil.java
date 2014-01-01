package com.gadawski.util.db;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.gadawski.util.facts.Relationship;

/**
 * Gives access to EntityManagerFactory which controls connection to the db,
 * also gives access to EntityManager instances.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class EntityManagerUtil {
    private static final String PERSISTENCE_UNIT_NAME = "hsqldb-ds";
    /**
     * Instance of {@link EntityManagerUtil}.
     */
    private static EntityManagerUtil INSTANCE = null;
    /**
     * Only one instance of entity manager factory.
     */
    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;
    /**
     * Counter for releasing resources.
     */
    private static final AtomicInteger COUNTER = new AtomicInteger();
    /**
     * Entity manager.
     */
    private EntityManager m_entityManager;
    /**
     * Transaction.
     */
    private EntityTransaction m_transaction;

    /**
     * Creates entity manager and gets transaction for session.
     */
    private EntityManagerUtil() {
        createEMandInitilizeTransaction();
    }

    /**
     * @return instance of {@link EntityManagerUtil} object.
     */
    public static synchronized EntityManagerUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EntityManagerUtil();
        }
        return INSTANCE;
    }

    /**
     * Creates new entity manager and initializes transaction.
     */
    public void createEMandInitilizeTransaction() {
        createEntityManager();
        m_transaction = getTransaction();
    }

    /**
     * Creates entity manager.
     */
    public void createEntityManager() {
        createEntityManagerFactory();
        m_entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    /**
     * Persists Relationship object to db.
     * 
     * @param relationship
     *            to persist
     */
    public void saveRelationship(final Relationship relationship) {
        saveObject(relationship);
    }

    /**
     * Special implementation of saving object to db.
     * 
     * @param object
     */
    public void saveObject(final Object object) {
        persist(object);
        if ((EntityManagerUtil.COUNTER.getAndIncrement() % 10000) == 0) {
            commitTransaction();
            beginTransaction();
        }
    }

    /**
     * Closes entityManager and enitytManagerFactory.
     */
    public void close() {
        if (m_entityManager.isOpen()) {
            m_entityManager.close();
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    /**
     * Flushes entity manager.
     */
    public void flush() {
        m_entityManager.flush();
    }

    /**
     * @param query
     * @return typedQuery for given query.
     */
    public TypedQuery<Relationship> createQuery(
            final CriteriaQuery<Relationship> query) {
        return m_entityManager.createQuery(query);
    }

    /**
     * @return criteria builder based on entityManager.
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return m_entityManager.getCriteriaBuilder();
    }

    /**
     * Begins transaction.
     */
    public void beginTransaction() {
        if (!m_transaction.isActive()) {
            m_transaction.begin();
        }
    }

    /**
     * Commits started transaction.
     */
    public void commitTransaction() {
        m_transaction.commit();
    }

    /**
     * @return - true if entity manager is opened, otherwise - false.
     */
    public boolean isOpen() {
        return m_entityManager.isOpen();
    }

    /**
     * Truncates all tables.
     */
    // TODO get sqlexception, has to be fixed!
    @Deprecated
    public void cleanup() {
        if (!isOpen()) {
            createEMandInitilizeTransaction();
        }
        beginTransaction();
        m_entityManager.createNativeQuery("truncate table customers")
                .executeUpdate();
        m_entityManager.createNativeQuery("truncate table cars")
                .executeUpdate();
        m_entityManager.createNativeQuery("truncate table houses")
                .executeUpdate();
        m_entityManager.createNativeQuery("truncate table relationships")
                .executeUpdate();
        commitTransaction();
    }

    /**
     * Creates new {@link EntityManagerFactory} for persistence unit.
     */
    private static void createEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence
                .createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Persist object to db. Locally it has to be used with EntityTransaction.
     * 
     * @param object
     *            to be saved to db.
     */
    private void persist(final Object object) {
        m_entityManager.persist(object);
    }

    /**
     * @return - transaction for entity manager.
     */
    private EntityTransaction getTransaction() {
        return m_entityManager.getTransaction();
    }

    /**
     * @return em
     */
    public EntityManager getEntityManager() {
        return m_entityManager;
    }
}
