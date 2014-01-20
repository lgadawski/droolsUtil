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
    public static final String LEFT_TUPLE_ID = "left_tuple_id";
    /**
     * 
     */
    public static final String RIGHT_TUPLE_ID = "right_tuple_id";
    /**
     * 
     */
    public static final String SELECT_LEFT_TUPLES = "SELECT * FROM LEFT_TUPLES WHERE sink_id = ?";
    /**
     * 
     */
    public static final String SELECT_RIGHT_TUPLES = "SELECT * FROM RIGHT_TUPLES WHERE sink_id = ?";
    /**
     * 
     */
    public static final String SELECT_LEFT_TUPLE_ID = "SELECT * FROM LEFT_TUPLES WHERE left_tuple_id = ?";
    /**
     * 
     */
    public static final String SELECT_RIGHT_TUPLE_ID = "SELECT * FROM RIGHT_TUPLES WHERE right_tuple_id = ?";
    /**
     * 
     */
    static final String SELECT_FIRST_ROW = "SELECT * from AGENDA_ITEMS where rownum = ?";
    /**
     * 
     */
    public static final String SELECT_FIRST_ROW_P = "SELECT * from AGENDA_ITEMS limit ?";
    /**
     * 
     */
    public static final String SELECT_FACT_HANDLE_BY_ID = "SELECT * FROM FACT_HANDLES where fact_handle_id = ?";
    /**
     * 
     */
    static final String TRUNCATE_TABLE_AGENDA_ITEMS = "truncate table agenda_items cascade";
    /**
     * 
     */
    static final String TRUNCATE_TABLE_LEFT_TUPLES = "truncate table left_tuples cascade";
    /**
     * 
     */
    static final String TRUNCATE_TABLE_RIGHT_TUPLES = "truncate table right_tuples cascade";
    /**
     * 
     */
    static final String TRUNCATE_TABLE_FACT_HANDLES = "truncate table fact_handles cascade";
    /**
     * 
     */
    static final String DELETE_FIRST_ROW = "delete from agenda_items where rownum = 1";
    /**
     * 
     */
    static final String DELETE_FIRST_ROW_P = "delete from agenda_items where agenda_item_id = (SELECT agenda_item_id from AGENDA_ITEMS limit 1)";
    /**
     * 
     */
    public static final String DELETE_RIGHT_TUPLE = "delete from right_tuples where right_tuple_id = ? AND sink_id = ?";
    /**
     * 
     */
    public static final String DELETE_LEFT_TUPLE = "delete from left_tuples where left_tuple_id = ? AND sink_id = ?";
    /**
     * 
     */
    public static final String DELETE_FACT_HANDLE = "delete from fact_handles where fact_handle_id = ?";
    /**
     * 
     */
    static final String COUNT_TOTAL_NUMBER_OF_AGENDA_ITEMS = "select count(*) from agenda_items";
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
    static final String INSERT_INTO_A_I_STATEMENT = "INSERT into AGENDA_ITEMS "
            + "(agenda_item_id, object) values (agenda_items_seq.NEXTVAL,?)";
    /**
     * 
     */
    static final String INSERT_INTO_LEFT_TUPLES = "INSERT into LEFT_TUPLES "
            + "(left_tuple_id, sink_id, object) values (left_tuples_seq.NEXTVAL, ?, ?)";
    /**
     * 
     */
    public static final String INSERT_INTO_RIGHT_TUPLES = "INSERT into A_RIGHT_TUPLES "
            + "(left_tuple_id, sink_id, object) values (right_tuples_seq.NEXTVAL, ?, ?)";
    /**
     * 
     */
    public static final String INSERT_INTO_A_I_STATEMENT_P = "INSERT into AGENDA_ITEMS "
            + "(agenda_item_id, left_tuple_id, object) values (nextval('agenda_item_id_seq'), ?, ?)";
    /**
     * 
     */
    static final String INSERT_INTO_LEFT_TUPLES_P = "INSERT into LEFT_TUPLES "
            + "(left_tuple_id, parent_tuple_id, fact_handle_id, sink_id, object) values "
            + "(nextval('left_tuple_id_seq'), ?, ?, ?, ?)";
    /**
     * 
     */
    static final String INSERT_INTO_RIGHT_TUPLES_P = "INSERT into RIGHT_TUPLES "
            + "(right_tuple_id, fact_handle_id, sink_id, object) values "
            + "(nextval('right_tuple_id_seq'), ?, ?, ?)";
    /**
     * Special insert statement combined with update. If row doesn't exists in
     * table insert is performed, otherwise nothing happens. params: _
     * ?(object), ?(fact_handle_id), ?(fact_handle_id), ?(object), ?(fact_handle_id).
     * TODO to be correcte!
     */
    static final String INSERT_INTO_FACT_HANDLES_P = 
            "UPDATE fact_handles SET object = ? WHERE fact_handle_id = ?;"
            + "INSERT into fact_handles (fact_handle_id, object)  SELECT ?, ?"
            + "WHERE NOT EXISTS (SELECT 1 FROM fact_handles WHERE fact_handle_id = ?);";

    /**
     * 
     */
    private Statements() {
    }
}
