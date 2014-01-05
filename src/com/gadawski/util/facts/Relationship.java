package com.gadawski.util.facts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

/**
 * Relationship class is responsible for storing information about tuple.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
@Entity
@Table(name = "A_RELATIONSHIPS")
// @NamedQueries({
// @NamedQuery(name = "Relatanshiops.findByJoinNodeId", query =
// "SELECT (*) FROM RELATIONSHIPS WHERE JOINNODE_ID = :joinNodeId") })
// @Cacheable(value = true)
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Relationship implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Counts number of objects in tuple.
     */
    @Transient
    private int m_numberOfObjectsInTuple = 0;
    /**
     * Entity ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rel_seq")
    @SequenceGenerator(name = "rel_seq", sequenceName = "rel_seq", allocationSize = 500)
    @Column(name = "Relationship_ID", unique = true, updatable = false, nullable = false)
    private long relationshipID;
    /**
     * Customer ID.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    // (cascade=CascadeType.PERSIST)
    @JoinColumn(name = "Customer_ID")
    private Customer customer;
    /**
     * Car ID.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    // (cascade=CascadeType.PERSIST)
    @JoinColumn(name = "Car_ID")
    private Car car;
    /**
     * House ID.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    // (cascade=CascadeType.PERSIST)
    @JoinColumn(name = "House_ID")
    private House house;
    /**
     * 
     */
    // TODO get string name of fields
    // consider using idx here, many inserts less queries
    @Column(name = "JoinNode_ID")
    @Index(name = "joinNodeIdIdx")
    private long joinNode_ID;

    /**
     * For persistence purpose.
     */
    public Relationship() {
    }

    public Relationship(final int joinNodeID, final Object object) {
        this.joinNode_ID = (long) joinNodeID;
        setObject(object);
    }

    /**
     * Set object in relationship.
     * 
     * @param object
     */
    public void setObject(final Object object) {
        if (object instanceof Car) {
            setCar((Car) object);
        } else if (object instanceof Customer) {
            setCustomer((Customer) object);
        } else if (object instanceof House) {
            setHouse((House) object);
        }
    }

    /**
     * @return the joinNodeID
     */
    public Long getJoinNode() {
        return joinNode_ID;
    }

    /**
     * @param joinNodeID
     *            the joinNodeID to set
     */
    public void setJoinNode(final Long joinNodeID) {
        this.joinNode_ID = joinNodeID;
    }

    /**
     * @return the customerID
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customerID
     *            the customerID to set
     */
    public void setCustomer(final Customer customerID) {
        this.customer = customerID;
        incrementNumberOfObjects();
    }

    /**
     * @return the carID
     */
    public Car getCar() {
        return car;
    }

    /**
     * @param carID
     *            the carID to set
     */
    public void setCar(final Car carID) {
        this.car = carID;
        incrementNumberOfObjects();
    }

    /**
     * @return the houseID
     */
    public House getHouse() {
        return house;
    }

    /**
     * @param houseID
     *            the houseID to set
     */
    public void setHouse(final House houseID) {
        this.house = houseID;
        incrementNumberOfObjects();
    }

    /**
     * 
     * @return list of objects in relationship.
     */
    public List<Object> getObjects() {
        final List<Object> objects = new ArrayList<Object>();
        if (customer != null) {
            objects.add(customer);
        }
        if (house != null) {
            objects.add(house);
        }
        if (car != null) {
            objects.add(car);
        }
        return objects;
    }

    /**
     * Gets number of object types in relationships.
     * 
     * @return number of object types in relationship.
     */
    public int getNoObjectsInTuple() {
        int counter = 0;
        if (customer != null) {
            ++counter;
        }
        if (car != null) {
            ++counter;
        }
        if (house != null) {
            ++counter;
        }
        return counter;
        // return m_numberOfObjectsInTuple;
    }

    /**
     * @return the relationshipID
     */
    public Long getRelationshipID() {
        return relationshipID;
    }

    /**
     * @param relationshipID
     *            the relationshipID to set
     */
    public void setRelationshipID(Long relationshipID) {
        this.relationshipID = relationshipID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (joinNode_ID ^ (joinNode_ID >>> 32));
        result = prime * result
                + (int) (relationshipID ^ (relationshipID >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Relationship other = (Relationship) obj;
        if (joinNode_ID != other.joinNode_ID)
            return false;
        if (relationshipID != other.relationshipID)
            return false;
        return true;
    }

    /**
     * Increments number of objects in tuple counter.
     */
    private void incrementNumberOfObjects() {
        m_numberOfObjectsInTuple++;
    }
}
