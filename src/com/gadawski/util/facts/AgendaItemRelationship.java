package com.gadawski.util.facts;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author l.gadawski@gmail.com
 *
 */
@Entity
@Table(name = "A_AGENDA_ITEMS_REL")
public class AgendaItemRelationship extends Relationship {
    /**
     * @param relationship
     */
    public AgendaItemRelationship(Relationship relationship) {
//        super(relationship);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
