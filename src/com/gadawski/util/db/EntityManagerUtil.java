package com.gadawski.util.db;

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
        System.out.println("new entityManager");
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
     * @param object
     */
    public void saveObject(final Object object) {
        beginTransaction();
        persist(object);
        commitTransaction();
    }

    /**
     * Closes entity manager.
     */
    public void close() {
        m_entityManager.close();
    }

    /**
     * Flushes entity manager.
     */
    public void flush() {
        m_entityManager.flush();
    }

    /**
     * Clears entity manager.
     */
    public void clear() {
        m_entityManager.clear();
    }

    /**
     * @param query
     * @return
     */
    public TypedQuery<Relationship> createQuery(
            CriteriaQuery<Relationship> query) {
        return m_entityManager.createQuery(query);
    }

    /**
     * @return
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return m_entityManager.getCriteriaBuilder();
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
     * Begins transaction.
     */
    private void beginTransaction() {
        m_transaction.begin();
    }

    /**
     * Commits started transaction.
     */
    private void commitTransaction() {
        m_transaction.commit();
    }
}
