package com.gadawski.util.db.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * Utility class that uses JDBC connections to persist data.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class JdbcAgendaItemManagerUtil {
    /**
     * Instance.
     */
    private static final JdbcAgendaItemManagerUtil INSTANCE = null;
    /**
     * 
     */
    private static final String CONNECTION_FACTORY_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";
    /**
     * Initial pool size
     */
    private static final int POOL_SIZE = 10;
    /**
     * Number of statements that will be cached.
     */
    private static final int MAX_STATEMENTS_CACHE = 10;
    /**
     * 
     */
    private final PoolDataSource m_poolDataSource;

    /**
     * Private constructor to block creating objects.
     */
    private JdbcAgendaItemManagerUtil() {
        m_poolDataSource = PoolDataSourceFactory.getPoolDataSource();
        try {
            setConnectionProps();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        truncateAgendaItems();
    }

    /**
     * @return instance of {@link JdbcAgendaItemManagerUtil} class.
     */
    public static synchronized JdbcAgendaItemManagerUtil getInstance() {
        if (INSTANCE == null) {
            return new JdbcAgendaItemManagerUtil();
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
     * Removes AgendaItem from top.
     * 
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
        try {
            final Connection connection = getConnection();
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objOutputStream.writeObject(object);
            objOutputStream.flush();
            objOutputStream.close();

            final byte[] data = byteOutputStream.toByteArray();

            final PreparedStatement statement = connection
                    .prepareStatement(Statements.INSERT_INTO_A_I_STATEMENT);
            statement.setObject(1, null); // set agenda_item_id null to be
                                          // generated from seq
            statement.setObject(1, data);
            statement.executeUpdate();
            statement.close();
            closeConnection(connection);
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
        Connection connection = null;
        try {
            connection = getConnection();
            final PreparedStatement statement = connection
                    .prepareStatement(Statements.SELECT_ROW);
            statement.setInt(1, rowNum);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(
                        resultSet.getBytes("agenda_object"));
                final ObjectInputStream objectInputStream = new ObjectInputStream(
                        inputStream);

                final Object object = objectInputStream.readObject();
                objectInputStream.close();
                statement.close();
                closeConnection(connection);
                return object;
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return
     */
    public int getTotalNumberOfRows() {
        int totalCount = 0;
        try {
            final Connection connection = getConnection();
            final PreparedStatement statement = connection
                    .prepareStatement(Statements.COUNT_TOTAL_NUMBER_OF_AGENDA_ITEMS);
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                totalCount = resultSet.getInt(Statements.COUNT_STAR);
            }
            resultSet.close();
            statement.close();
            closeConnection(connection);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return totalCount;
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
        try {
            final Connection connection = getConnection();
            final PreparedStatement statement = connection
                    .prepareStatement(stmt);
            statement.execute();
            statement.close();
            closeConnection(connection);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private void deleteFirstObject() {
        executeStatement(Statements.DELETE_FIRST_ROW);
    }

    /**
     * Get database connection from the datasource.
     * 
     * @return {@link Connection} from DataSource.
     * @throws SQLException
     *             - if connection cannot be obtained from pool.
     */
    private Connection getConnection() throws SQLException {
        return m_poolDataSource.getConnection();
    }

    /**
     * Sets all {@link OracleDataSource} properties from {@link JdbcConfigProps}
     * 
     * 
     * @throws SQLException
     * 
     */
    private void setConnectionProps() throws SQLException {
        m_poolDataSource
                .setConnectionFactoryClassName(CONNECTION_FACTORY_CLASS_NAME);
        m_poolDataSource.setURL(JdbcConfigProps.CONNECTION_URL);
        m_poolDataSource.setUser(JdbcConfigProps.USER_NAME);
        m_poolDataSource.setPassword(JdbcConfigProps.PASSWORD);
        m_poolDataSource.setInitialPoolSize(POOL_SIZE);
        m_poolDataSource.setMaxStatements(MAX_STATEMENTS_CACHE);
    }

    /**
     * Closes given connection.
     * 
     * @throws SQLException
     *             - if connection cannot be closed.
     * 
     */
    private void closeConnection(final Connection connection)
            throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

}
