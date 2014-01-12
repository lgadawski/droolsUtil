package com.gadawski.util.facts;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Concrete class represents objects stored in right tuple memory.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
@Entity
@Table(name = "A_RIGHT_RELATIONSHIPS")
@NamedQueries({ @NamedQuery(name = "RightRelationship.findRelationshipByJoinNodeId", query = "FROM RightRelationship WHERE joinNode_ID = :nodeId") })
public class RightRelationship extends Relationship {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Named query name defined for {@link Relationship}, selects relationships
     * where joinNode_Id = :nodeId
     */
    public static final String FIND_RELS_BY_JOINNODE_ID = "RightRelationship.findRelationshipByJoinNodeId";

    /**
     * For persistence purpose only.
     */
    public RightRelationship() {
    }

    /**
     * @param sinkId
     * @param object
     */
    public RightRelationship(final int sinkId, final Object object) {
        super(sinkId, object);
    }

    /**
     * Returns null if {@link RightRelationship} doesn't have fact handle.
     * 
     * @return object that hold fact handle.
     */
    public Object getObject() {
        Object object = null;
        if ((object = getCar()) != null) {
            return object;
        }
        if ((object = getCustomer()) != null) {
            return object;
        }
        if ((object = getHouse()) != null) {
            return object;
        }
        return object;
    }
}
