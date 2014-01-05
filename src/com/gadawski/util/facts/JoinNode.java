package com.gadawski.util.facts;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author l.gadawski@gmail.com
 * 
 */
//@Entity(name = "JOINNODES")
public class JoinNode implements Serializable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Entity ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "JoinNodeID", unique = true, updatable = false, nullable = false)
    private long joinNodeID;

    /**
     * For persistance purposes.
     */
    public JoinNode() {
    }

    /**
     * @return the joinNodeID
     */
    public Long getJoinNodeID() {
        return joinNodeID;
    }

    /**
     * @param joinNodeID
     *            the joinNodeID to set
     */
    public void setJoinNodeID(Long joinNodeID) {
        this.joinNodeID = joinNodeID;
    }
}
