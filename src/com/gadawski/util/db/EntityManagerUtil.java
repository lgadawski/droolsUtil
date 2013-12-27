package com.gadawski.util.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
     * Only one instance of entity manager factory.
     */
    private static EntityManagerFactory m_entityManagerFactory = Persistence
            .createEntityManagerFactory("hsqldb-ds");
    /**
     * Instance of {@link EntityManagerUtil}.
     */
    private static EntityManagerUtil INSTANCE;
    /**
     * Entity manager.
     */
    private final EntityManager m_entityManager;
    /**
     * Transaction.
     */
    private final EntityTransaction m_transaction;

    /**
     * Creates entity manager and gets transaction for session.
     */
    private EntityManagerUtil() {
        m_entityManager = m_entityManagerFactory.createEntityManager();
        m_transaction = m_entityManager.getTransaction();
    }

    /**
     * @return instance of {@link EntityManagerUtil} object.
     */
    public static EntityManagerUtil getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        } else {
            return new EntityManagerUtil();
        }
    }

    /**
     * Persists Relationship object to db.
     * 
     * @param relationship
     *            to persist
     */
    public void saveRelationship(final Relationship relationship) {
        saveObject(relationship);
        this.close();
    }

    /**
     * @param object
     */
    public void saveObject(final Object object) {
        this.beginTransaction();
        this.persist(object);
        this.commitTransaction();
    }

    /**
     * Closes entity manager.
     */
    public void close() {
        m_entityManager.close();
    }

    /**
     * Creates query to get relationships associated with given joinNode id.
     * 
     * @param joinNodeID
     *            - id of join node.
     * @return List of relationships associated with join node.
     */
    public List<Relationship> getRalationships(final int joinNodeID) {
        final CriteriaBuilder builder = m_entityManager.getCriteriaBuilder();
        final CriteriaQuery<Relationship> query = builder
                .createQuery(Relationship.class);
        final Root<Relationship> root = query.from(Relationship.class);
        query.select(root).where(
                builder.equal(root.get("joinNode_ID"), joinNodeID));

        final TypedQuery<Relationship> tQuery = m_entityManager
                .createQuery(query);
        List<Relationship> results = new ArrayList<Relationship>();
        results = tQuery.getResultList();
        return results;
    }

//    /**
//     * Flushes entitymanager.
//     */
//    private void flush() {
//        m_entityManager.flush();
//    }

    /**
     * Persist object to db. Locally it has to be used with EntityTransaction.
     * 
     * @param object
     *            to be saved to db
     */
    private void persist(final Object object) {
        m_entityManager.persist(object);
    }

    /**
     * Begins transaction
     */
    private void beginTransaction() {
        m_transaction.begin();
    }

    /**
     * Commits begined transaction and closes entity manager.
     */
    private void commitTransaction() {
        m_transaction.commit();
    }
}
