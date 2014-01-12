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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

/**
 * Relationship class is responsible for storing information about tuple.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
@Entity
@Table(name = "A_RELATIONSHIPS")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries({ @NamedQuery(name = "Relationship.findRelationshipByJoinNodeId", query = "FROM Relationship WHERE joinNode_ID = :nodeId") })
// @Cacheable(value = true)
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Relationship implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Named query name defined for {@link Relationship}, selects relationships
     * where joinNode_Id = :nodeId
     */
    public static final String FIND_RELS_BY_JOINNODE_ID = "Relationship.findRelationshipByJoinNodeId";
    /**
     * Parameter name used for queries.
     */
    public static final String NODE_ID_TXT = "nodeId";
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
    @JoinColumn(name = "Customer_ID")
    @Index(name = "customer_id_idx")
    private Customer customer;
    /**
     * Car ID.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Car_ID")
    @Index(name = "car_id_idx")
    private Car car;
    /**
     * House ID.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "House_ID")
    @Index(name = "car_id_idx")
    private House house;
    /**
     * 
     */
    @Column(name = "joinNode_ID")
    @Index(name = "joinNodeIdIdx")
    private long joinNode_ID;

    /**
     * For persistence purpose.
     */
    public Relationship() {
    }

    public Relationship(final long joinNodeID, final Object object) {
        this.joinNode_ID = joinNodeID;
        setObject(object);
    }

    /**
     * Set object in relationship.
     * 
     * @param object
     */
    public void setObject(final Object object) {
        if (object instanceof Car) {
            car = (Car) object;
        } else if (object instanceof Customer) {
            customer = (Customer) object;
        } else if (object instanceof House) {
            house = (House) object;
        }
    }

    /**
     * @return the joinNodeID
     */
    public long getJoinNodeId() {
        return joinNode_ID;
    }

    /**
     * @param joinNodeID
     *            the joinNodeID to set
     */
    public void setJoinNodeId(final long joinNodeID) {
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
    }

    /**
     * @return the relationshipID
     */
    public long getRelationshipID() {
        return relationshipID;
    }

    /**
     * @param relationshipID
     *            the relationshipID to set
     */
    public void setRelationshipID(final Long relationshipID) {
        this.relationshipID = relationshipID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
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
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Relationship other = (Relationship) obj;
        if (joinNode_ID != other.joinNode_ID)
            return false;
        if (relationshipID != other.relationshipID)
            return false;
        return true;
    }
}
