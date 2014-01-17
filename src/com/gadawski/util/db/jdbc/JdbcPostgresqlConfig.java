package com.gadawski.util.db.jdbc;

/**
 * Config props for JDBC.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
final class JdbcPostgresqlConfig {
    /**
     * 
     */
    static final String DRIVER_PACKAGE = "org.postgresql.Driver";
    /**
     * 
     */
    static final String CONNECTION_URL = "jdbc:postgresql://127.0.0.1:5432:postgres";
    /**
     * 
     */
    static final String USER_NAME = "gadon";
    /**
     * 
     */
    static final String PASSWORD = "abelrm";

    /**
     * 
     */
    private JdbcPostgresqlConfig() {

    }
}
