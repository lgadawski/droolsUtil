package com.gadawski.util.facts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Relationship class is responsible for storing information about tuple.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
@Entity
@Table(name = "RELATIONSHIPS")
public class Relationship implements Serializable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;
//    /**
//     * Number of object types in relationships.
//     */
//    private static final int NO_OBJECT_TYPES = 3;
    /**
     * Entity ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Relationship_ID", unique = true, updatable = false, nullable = false)
    private Long relationshipID;
    /**
     * Customer ID.
     */
    @ManyToOne
    @JoinColumn(name = "Customer_ID")
    private Customer customer;
    /**
     * Car ID.
     */
    @ManyToOne
    @JoinColumn(name = "Car_ID")
    private Car car;
    /**
     * House ID.
     */
    @ManyToOne
    @JoinColumn(name = "House_ID")
    private House house;
    /**
     * 
     */
    // TODO get string name of fields
    @Column(name = "JoinNode_ID", nullable = false)
    private Long joinNode_ID;

    /**
     * For persistence purpose.
     */
    public Relationship() {
    }

    public Relationship(final int joinNodeID, final Object object) {
        this.joinNode_ID = (long) joinNodeID;
        this.customer = (Customer) object;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((car == null) ? 0 : car.hashCode());
        result = prime * result
                + ((customer == null) ? 0 : customer.hashCode());
        result = prime * result + ((house == null) ? 0 : house.hashCode());
        result = prime * result
                + ((joinNode_ID == null) ? 0 : joinNode_ID.hashCode());
        result = prime * result
                + ((relationshipID == null) ? 0 : relationshipID.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Relationship other = (Relationship) obj;
        if (car == null) {
            if (other.car != null)
                return false;
        } else if (!car.equals(other.car))
            return false;
        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        if (house == null) {
            if (other.house != null)
                return false;
        } else if (!house.equals(other.house))
            return false;
        if (joinNode_ID == null) {
            if (other.joinNode_ID != null)
                return false;
        } else if (!joinNode_ID.equals(other.joinNode_ID))
            return false;
        if (relationshipID == null) {
            if (other.relationshipID != null)
                return false;
        } else if (!relationshipID.equals(other.relationshipID))
            return false;
        return true;
    }

    /**
     * 
     * @return list of objects in ralationship.
     */
    public List<Object> getObjects() {
        List<Object> objects = new ArrayList<Object>();
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
     * @return number of object types in relationship.
     */
    public int getNoObjectsInTuple() {
        return getObjects().size();
    }

}