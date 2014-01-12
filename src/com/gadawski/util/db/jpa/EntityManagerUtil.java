package com.gadawski.util.db.jpa;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.gadawski.util.facts.Relationship;
import com.gadawski.util.facts.RightRelationship;

/**
 * Gives access to EntityManagerFactory which controls connection to the db,
 * also gives access to EntityManager instances.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class EntityManagerUtil {
    /**
     * "truncate table "
     */
    public static final String TRUNCATE_TABLE = "truncate table ";
    /**
     * Name of persistence unit.
     */
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
     * Limit indicates after how many operations perform commit. Should be same
     * as hibernate.batch_size in persistence.xml.
     */
    public static final int BATCH_SIZE = 3000;
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
        m_entityManager.setFlushMode(FlushModeType.AUTO);
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
        if ((EntityManagerUtil.COUNTER.getAndIncrement() % BATCH_SIZE) == 0) {
            commitTransaction();
            clear();
            beginTransaction();
        }
    }

    /**
     * Remove the entity instance.
     * 
     * @param object
     *            to remove.
     */
    public void remove(final Object object) {
        beginTransaction();
        m_entityManager.remove(object);
        commitTransaction();
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
        if (m_transaction.isActive()) {
            m_transaction.commit();
        }
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
        checkIfEMisOpen();
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
     * Clear the persistence context, causing all managed entities to become
     * detached. Changes made to entities that have not been flushed to the
     * database will not be persisted.
     */
    public void clear() {
        m_entityManager.clear();
    }

    /**
     * @return {@link EntityManager}
     */
    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    /**
     * Truncates given table.
     * 
     * @param tableName
     *            table name to truncate.
     */
    public void truncateTable(final String tableName) {
        checkIfEMisOpen();
        beginTransaction();
        m_entityManager.createNativeQuery(TRUNCATE_TABLE + tableName)
                .executeUpdate();
        commitTransaction();
    }

    /**
     * Creates query to db that counts total number of rows in table.
     * 
     * @param entityName
     */
    public int getTotalNumberOfRows(final String entityName) {
        checkIfEMisOpen();
        final String queryName = "SELECT count(*) FROM " + entityName;
        final Query query = m_entityManager.createQuery(queryName);
        // TODO refine this!
        return Integer.valueOf(query.getSingleResult().toString());
    }

    /**
     * Creates new {@link EntityManagerFactory} for persistence unit. Creating
     * EMF is very expensive, so use this method very carefully.
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
        try {
            m_entityManager.persist(object);
        } catch (final IllegalArgumentException e) {
            System.out.println("Not persistable object!");
        }
    }

    /**
     * @return - transaction for entity manager.
     */
    private EntityTransaction getTransaction() {
        return m_entityManager.getTransaction();
    }

    /**
     * Checks if EntityManager is open, otherwise creates new EM and initlizes
     * new transaction.
     */
    private void checkIfEMisOpen() {
        if (!isOpen()) {
            createEMandInitilizeTransaction();
        }
    }

    /**
     * Unwraps hibernate's session from {@link EntityManager} and opens
     * {@link Session}.
     * 
     * @return {@link Session} based on {@link EntityManager}.
     */
    public Session openSession() {
        final Session session = m_entityManager.unwrap(Session.class);
        final SessionFactory sessionFactory = session.getSessionFactory();
        return sessionFactory.openSession();
    }

    /**
     * Create an instance of TypedQuery for executing a Java Persistence query
     * language named query. The select list of the query must contain only a
     * single item, which must be assignable to the type specified by the
     * resultClass argument.
     * 
     * @param sqlString
     * @param resultClass
     * @return the new query instance.
     */
    public Query createNamedQuery(final String sqlString,
            final Class<Relationship> resultClass) {
        return m_entityManager.createNamedQuery(sqlString, resultClass);
    }

    /**
     * Create an instance of TypedQuery for executing a Java Persistence query
     * language named query. The select list of the query must contain only a
     * single item, which must be assignable to the type specified by the
     * resultClass argument.
     * 
     * @param sqlString
     * @param resultClass
     * @return the new query instance.
     */
    public Query createNamedQueryForRightRelationships(final String sqlString,
            final Class<RightRelationship> resultClass) {
        return m_entityManager.createNamedQuery(sqlString, resultClass);
    }
}
