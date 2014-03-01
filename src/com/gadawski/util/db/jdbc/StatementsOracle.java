package com.gadawski.util.db.jdbc;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public class StatementsOracle {
    /**
     * 
     */
    static final String INSERT_INTO_A_I_STATEMENT = "INSERT into AGENDA_ITEMS "
            + "(agenda_item_id, object) values (agenda_items_seq.NEXTVAL, ?)";
    /**
     * 
     */
    static final String INSERT_INTO_LEFT_TUPLES = "INSERT into LEFT_TUPLES "
            + "(left_tuple_id, sink_id, object) values (left_tuples_seq.NEXTVAL, ?, ?)";
    /**
     * 
     */
    static final String INSERT_INTO_RIGHT_TUPLES = "INSERT into A_RIGHT_TUPLES "
            + "(left_tuple_id, sink_id, object) values (right_tuples_seq.NEXTVAL, ?, ?)";
    /**
     * 
     */
    static final String DELETE_FIRST_ROW = "delete from agenda_items where rownum = 1";
    /**
     * 
     */
    final String SELECT_FIRST_ROW = "SELECT * from AGENDA_ITEMS where rownum = ?";
    
    /**
     * 
     */
    private StatementsOracle() {

    }
}
