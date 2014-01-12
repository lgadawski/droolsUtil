package com.gadawski.util.db.jdbc;

/**
 * Holds statements for jdbc.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
final class Statements {
    /**
     * 
     */
    static final String TRUNCATE_TABLE_AGENDA_ITEMS = "truncate table a_agenda_items";
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
            + "(agenda_item_id, agenda_object) values (a_agenda_items_seq.NEXTVAL, ?)";
    /**
     * 
     */
    static final String SELECT_ROW = "SELECT * from A_AGENDA_ITEMS where rownum = ?";

    /**
     * 
     */
    private Statements() {
    }
}
