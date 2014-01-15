package com.gadawski.util.db.jdbc;

/**
 * Holds useful statements for jdbc.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public final class Statements {
    /**
     * 
     */
    static final String TRUNCATE_TABLE_AGENDA_ITEMS = "truncate table a_agenda_items";
    /**
     * 
     */
    static final String TRUNCATE_TABLE_LEFT_TUPLES = "truncate table a_left_tuples";
    /**
     * 
     */
    static final String TRUNCATE_TABLE_RIGHT_TUPLES = "truncate table a_right_tuples";
    /**
     * 
     */
    static final String DELETE_FIRST_ROW = "delete from a_agenda_items where rownum = 1";
    /**
     * 
     */
    static final String COUNT_TOTAL_NUMBER_OF_AGENDA_ITEMS = "select count(*) from a_agenda_items";
    /**
     * 
     */
    static final String COUNT_STAR = "count(*)";
    /**
     * 
     */
    static final String INSERT_INTO_A_I_STATEMENT = "INSERT into A_AGENDA_ITEMS "
            + "(agenda_item_id, object) values (a_agenda_items_seq.NEXTVAL,?)";
    /**
     * 
     */
    static final String INSERT_INTO_LEFT_TUPLES = "INSERT into A_LEFT_TUPLES "
            + "(tuple_id, sink_id, object) values (a_left_tuples_seq.NEXTVAL, ?, ?)";
    /**
     * 
     */
    public static final String INSERT_INTO_RIGHT_TUPLES = "INSERT into A_RIGHT_TUPLES "
            + "(tuple_id, sink_id, object) values (a_right_tuples_seq.NEXTVAL, ?, ?)";
    /**
     * 
     */
    static final String SELECT_ROW = "SELECT * from A_AGENDA_ITEMS where rownum = ?";
    /**
     * 
     */
    public static final String SELECT_LEFT_TUPLES = "SELECT * FROM A_LEFT_TUPLES WHERE sink_id = ?";
    /**
     * 
     */
    public static final String SELECT_RIGHT_TUPLES = "SELECT * FROM A_RIGHT_TUPLES WHERE sink_id = ?";

    /**
     * 
     */
    private Statements() {
    }
}
