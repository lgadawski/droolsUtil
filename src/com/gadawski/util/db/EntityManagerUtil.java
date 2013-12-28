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
 * @author l.gadawski
 * 
 */
public class EntityManagerUtil {
    /**
     * Instance of {@link EntityManagerUtil}.
     */
    private static EntityManagerUtil INSTANCE = null;
    /**
     * Only one instance of entity manager factory.
     */
    private static EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("hsqldb-ds");
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
        m_entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        m_transaction = m_entityManager.getTransaction();
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
     * Flushes and clears entity manager.
     */
    public void flushAndClear() {
        m_entityManager.flush();
        m_entityManager.clear();
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
     * Persist object to db. Locally it has to be used with EntityTransaction.
     * 
     * @param object
     *            to be saved to db.
     */
    private void persist(final Object object) {
        m_entityManager.persist(object);
    }
}
