package com.gadawski.util.db.jdbc;

/**
 * Config props for JDBC.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
final class JdbcConfigProps {
    /**
     * 
     */
    static final String DRIVER_PACKAGE = "oracle.jdbc.driver.OracleDriver";
    /**
     * 
     */
    static final String CONNECTION_URL = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
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
    private JdbcConfigProps() {

    }
}
