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
    static final String DELETE_FIRST_ROW_P = "delete from a_agenda_items where agenda_item_id = (SELECT agenda_item_id from A_AGENDA_ITEMS limit 1)";
    /**
     * 
     */
    public static final String DELETE_RIGHT_TUPLE = "delete from a_right_tuples where tuple_id = ? AND sink_id = ?";
    /**
     * 
     */
    public static final String DELETE_LEFT_TUPLE = "delete from a_left_tuples where tuple_id = ? AND sink_id = ?";
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
    public static final String COUNT = "count";
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
    static final String INSERT_INTO_A_I_STATEMENT_P = "INSERT into A_AGENDA_ITEMS "
            + "(agenda_item_id, object) values (nextval('agenda_item_id_seq'), ?)";
    /**
     * 
     */
    static final String INSERT_INTO_LEFT_TUPLES_P = "INSERT into A_LEFT_TUPLES "
            + "(tuple_id, parent_id, sink_id, object) values (nextval('left_tuple_id_seq'), ?, ?, ?)";
    /**
     * 
     */
    public static final String INSERT_INTO_RIGHT_TUPLES_P = "INSERT into A_RIGHT_TUPLES "
            + "(tuple_id, sink_id, object) values (nextval('right_tuple_id_seq'), ?, ?)";
    /**
     * 
     */
    static final String SELECT_FIRST_ROW = "SELECT * from A_AGENDA_ITEMS where rownum = ?";
    /**
     * 
     */
    static final String SELECT_FIRST_ROW_P = "SELECT * from A_AGENDA_ITEMS limit ?";
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
    public static final String SELECT_LEFT_TUPLE_ID = "SELECT * FROM A_LEFT_TUPLES WHERE tuple_id = ?";
    /**
     * 
     */
    public static final String SELECT_RIGHT_TUPLE_ID = "SELECT * FROM A_RIGHT_TUPLES WHERE tuple_id = ?";
    /**
     * 
     */
    private Statements() {
    }
}
