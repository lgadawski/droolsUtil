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
     * Entity manager.
     */
    private final EntityManager m_entityManager;
    /**
     * Transaction.
     */
    private final EntityTransaction m_transaction;

    // /**
    // * @return
    // */
    // public static EntityManagerFactory getEntityManagerFactory() {
    // EntityManagerFactory emf = Persistence
    // .createEntityManagerFactory("hsqldb-ds");
    // return emf;
    // }

    /**
     * Creates entity manager and gets transaction for session.
     */
    public EntityManagerUtil() {
        m_entityManager = m_entityManagerFactory.createEntityManager();
        m_transaction = m_entityManager.getTransaction();
    }

    /**
     * Persist object to db. Locally it has to be used with EntityTransaction.
     * 
     * @param object
     *            to be saved to db
     */
    public void persist(final Object object) {
        m_entityManager.persist(object);
    }

    /**
     * Begins transaction
     */
    public void beginTransaction() {
        m_transaction.begin();
    }

    /**
     * Commits begined transaction and closes entity manager.
     */
    public void commitTransaction() {
        m_transaction.commit();
    }

    /**
     * Closes entity manager.
     */
    public void close() {
        m_entityManager.close();
    }

    /**
     * Persists Relationship object to db.
     * 
     * @param relationship
     *            to persist
     */
    public void persistSingleRelationship(final Relationship relationship) {
        this.beginTransaction();
        this.persist(relationship);
        this.commitTransaction();
        this.close();
    }

    /**
     * Creates query to get relationships associated with given joinNode id.
     * 
     * @param joinNodeID
     *            - id of join node.
     * @return List of relationships associated with join node.
     */
    public List<Relationship> getRalationships(int joinNodeID) {
        CriteriaBuilder builder = m_entityManager.getCriteriaBuilder();
        CriteriaQuery<Relationship> query = builder
                .createQuery(Relationship.class);
        Root<Relationship> root = query.from(Relationship.class);
        query.select(root).where(
                builder.equal(root.get("joinNode_ID"), joinNodeID));

        TypedQuery<Relationship> tQuery = m_entityManager.createQuery(query);
        List<Relationship> results = new ArrayList<Relationship>();
        results = tQuery.getResultList();
        return results;
    }
}
