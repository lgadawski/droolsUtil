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
    public static final String SELECT_LEFT_TUPLES = "SELECT * FROM LEFT_TUPLES WHERE sink_id = ?";
    /**
     * 
     */
    public static final String SELECT_RIGHT_TUPLES = "SELECT * FROM RIGHT_TUPLES WHERE sink_id = ?";
    /**
     * 
     */
    static final String LEFT_TUPLE_ID = "left_tuple_id";
    /**
     * 
     */
    static final String RIGHT_TUPLE_ID = "right_tuple_id";
    /**
     * 
     */
    static final String COUNT = "count";
    /**
     * 
     */
    static final String OBJECT = "object";
    /**
     * 
     */
    static final String SELECT_LEFT_TUPLE_ID = "SELECT * FROM LEFT_TUPLES WHERE left_tuple_id = ?";
    /**
     * 
     */
    static final String SELECT_RIGHT_TUPLE_ID = "SELECT * FROM RIGHT_TUPLES WHERE right_tuple_id = ?";
    /**
     * 
     */
    static final String SELECT_FACT_HANDLE_BY_ID = "SELECT * FROM FACT_HANDLES where fact_handle_id = ?";
    /**
     * 
     */
    static final String SELECT_LAST_ROW_AGENDA_ITEM = "SELECT * FROM agenda_items "
            + "order by agenda_item_id desc limit ?";
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
    static final String DELETE_AGENDA_ITEM_BY_LT_ID = "delete from agenda_items where left_tuple_id = ?";
    /**
     * 
     */
    static final String DELETE_FIRST_AGENDA_ITEM = "delete from agenda_items where agenda_item_id = "
            + "(SELECT agenda_item_id from AGENDA_ITEMS order by agenda_item_id desc limit 1)";
    /**
     * 
     */
    static final String DELETE_RIGHT_TUPLE = "delete from right_tuples "
            + "where right_tuple_id = ?";
    /**
     * 
     */
    static final String DELETE_CHILD_LEFT_TUPLES = "delete from left_tuples "
            + "where parent_right_tuple_id = ?";
    /**
     * 
     */
    static final String DELETE_LEFT_TUPLE = "delete from left_tuples "
            + "where left_tuple_id = ?";
    /**
     * 
     */
    static final String DELETE_FACT_HANDLE = "delete from fact_handles "
            + "where fact_handle_id = ?";
    /**
     * 
     */
    static final String COUNT_TOTAL_NUMBER_OF_AGENDA_ITEMS = "select count(*) from agenda_items";
    /**
     * 
     */
    static final String INSERT_INTO_A_I_STATEMENT = "INSERT into AGENDA_ITEMS "
            + "(agenda_item_id, left_tuple_id, object) values (nextval('agenda_item_id_seq'), ?, ?)";
    /**
     * object, fact_handle_id, sink_id
     * 
     */
    static final String UPDATE_LEFT_TUPLE = "UPDATE left_tuples SET object = ? "
            + " WHERE (parent_tuple_id = ? AND "
            + " parent_right_tuple_id = ? AND "
            + " fact_handle_id = ? AND "
            + " sink_id = ?);";
    /**
     * This statement has to be combined with INSERT_INTO_LEFT_TUPLES_P
     * statement. parent_tuple_id, fact_handle_id, sink_id, object,
     * fact_handle_id, sink_id
     */
    static final String INSERT_INTO_LEFT_TUPLES = " INSERT into LEFT_TUPLES "
            + " (left_tuple_id, parent_tuple_id, fact_handle_id, parent_right_tuple_id, sink_id, object) "
            + " (SELECT nextval('left_tuple_id_seq'), ?, ?, ?, ?, ? WHERE NOT EXISTS "
            + " (SELECT 1 FROM left_tuples WHERE (parent_tuple_id = ? AND parent_right_tuple_id = ? AND "
            + " fact_handle_id = ? AND sink_id = ?)));";
    /**
     * 
     */
    static final String UPDATE_RIGHT_TUPLE = "update right_tuples SET object = ? "
            + " where (fact_handle_id = ? and" + " sink_id = ? ); ";
    /**
     * 
     */
    static final String INSERT_INTO_RIGHT_TUPLES = " insert into right_tuples "
            + " (right_tuple_id, fact_handle_id, sink_id, object) "
            + " (SELECT nextval('right_tuple_id_seq'), ?, ?, ? where not exists "
            + " (select 1 from right_tuples where (fact_handle_id = ? and sink_id = ?)))";

    /**
     * 
     */
    static final String UPDATE_FACT_HANDLE = "UPDATE fact_handles SET object = ? "
            + " WHERE fact_handle_id = ?;";
    /**
     * Special insert statement combined with update. If row doesn't exists in
     * table insert is performed, otherwise nothing happens. params: _
     * ?(object), ?(fact_handle_id), ?(fact_handle_id), ?(object),
     * ?(fact_handle_id). TODO to be corrected!
     */
    static final String INSERT_INTO_FACT_HANDLES = " INSERT into fact_handles (fact_handle_id, object)  "
            + " SELECT ?, ? WHERE NOT EXISTS "
            + " (SELECT 1 FROM fact_handles WHERE fact_handle_id = ?);";

    /**
     * 
     */
    private Statements() {
    }
}
