package com.gadawski.util.db.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility class that uses JDBC connections to persist data.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class JdbcEntityManagerUtil {
    /**
     * Instance.
     */
    private static final JdbcEntityManagerUtil INSTANCE = null;
    /**
     * 
     */
    private Connection connection = null;

    /**
     * Private constructor to block creating objects.
     */
    private JdbcEntityManagerUtil() {

    }

    /**
     * @return instance of {@link JdbcEntityManagerUtil} class.
     */
    public static synchronized JdbcEntityManagerUtil getInstance() {
        if (INSTANCE == null) {
            return new JdbcEntityManagerUtil();
        }
        return INSTANCE;
    }

    /**
     * Gets agenda items object from table for rownum = 1.
     * 
     * @return
     */
    public Object getNextAgendaItemObject() {
        return getObject(1);
    }

    /**
     * @param item
     */
    public void removeNextAgendaItem() {
        deleteFirstObject();
    }

    /**
     * 
     * @param object
     *            to be saved.
     */
    public void saveObject(final Object object) {
        initilizeConnection();
        try {
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objOutputStream.writeObject(object);
            objOutputStream.flush();
            objOutputStream.close();

            final byte[] data = byteOutputStream.toByteArray();

            final PreparedStatement statement = connection
                    .prepareStatement(Statements.INSERT_STATEMENT);
            statement.setObject(1, null); // set agenda_item_id null to be
                                          // generated from seq
            statement.setObject(1, data);
            statement.executeUpdate();
            connection.commit();
            // closeConnection();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param rowNum
     * @return
     */
    public Object getObject(final int rowNum) {
        initilizeConnection();
        try {
            final PreparedStatement statement = connection
                    .prepareStatement(Statements.SELECT_ROW + rowNum);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(
                        resultSet.getBytes("agenda_object"));
                final ObjectInputStream objectInputStream = new ObjectInputStream(
                        inputStream);

                final Object object = objectInputStream.readObject();
                objectInputStream.close();
                // closeConnection();
                return object;
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        closeConnection();
        return null;
    }

    /**
     * @param item
     */
    public void remove(final Object object) {
        // TODO Auto-generated method stub

    }

    /**
     * @return
     */
    public int getTotalNumberOfRows() {
        initilizeConnection();
        PreparedStatement statement;
        try {
            statement = connection
                    .prepareStatement(Statements.COUNT_TOTAL_NUMBER_OF_AGENDA_ITEMS);
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt(Statements.COUNT_STAR);
            }
            connection.commit();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        // closeConnection();
        return 0;
    }

    /**
     * @param agendaItemsEntityName
     */
    public void truncateAgendaItems() {
        executeStatement(Statements.TRUNCATE_TABLE_AGENDA_ITEMS);
    }

    /**
     * Initilizes connection to db and executes statement.
     * 
     * @param stmt
     *            statement to be execute.
     */
    private void executeStatement(final String stmt) {
        initilizeConnection();
        try {
            final PreparedStatement statement = connection
                    .prepareStatement(stmt);
            statement.execute();
            connection.commit();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        // closeConnection();
    }

    /**
     * 
     */
    private void deleteFirstObject() {
        executeStatement(Statements.DELETE_FIRST_ROW);
    }

    /**
     * Initializes connection.
     */
    private void initilizeConnection() {
        try {
            Class.forName(JdbcConfigProps.DRIVER_PACKAGE);
        } catch (final ClassNotFoundException e) {
            System.out.println("Oracle JDBC driver not found.");
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(
                    JdbcConfigProps.CONNECTION_URL, JdbcConfigProps.USER_NAME,
                    JdbcConfigProps.PASSWORD);
        } catch (final SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private void closeConnection() {
        try {
            connection.close();
        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
