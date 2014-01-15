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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static JdbcAgendaItemManagerUtil INSTANCE = null;
    /**
     * 
     */
    private static final String CONNECTION_FACTORY_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";
    /**
     * Initial pool size
     */
    private static final int POOL_SIZE = 100;
    /**
     * Fetch size for query.
     */
    public static final int FETCH_SIZE = 100;
    /**
     * Number of statements that will be cached.
     */
    private static final int MAX_STATEMENTS_CACHE = 10;
    /**
     * Counter for releasing resources.
     */
    private static final AtomicInteger COUNTER = new AtomicInteger();
    /**
     * 
     */
    private static final int BATCH_SIZE = 100;
    /**
     * 
     */
    private final PoolDataSource m_poolDataSource;
//    /**
//     * 
//     */
//    private List<Object> m_agendaItems = new ArrayList<Object>();

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
        truncateLeftTuples();
        truncateRightTuples();
    }

    /**
     * @return instance of {@link JdbcAgendaItemManagerUtil} class.
     */
    public static synchronized JdbcAgendaItemManagerUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JdbcAgendaItemManagerUtil();
            return INSTANCE;
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
     * @param sinkId
     * @param leftTuple
     */
    public void saveLeftTuple(final int sinkId, final Object leftTuple) {
        saveTuple(sinkId, leftTuple, Statements.INSERT_INTO_LEFT_TUPLES);
    }

    /**
     * @param sinkId
     * @param rightTuple
     */
    public void saveRightTuple(final int sinkId, final Object rightTuple) {
        saveTuple(sinkId, rightTuple, Statements.INSERT_INTO_RIGHT_TUPLES);
    }

    /**
     * @param sinkId
     * @return
     */
    public List<Object> getLeftTuples(final int sinkId) {
        return getTupleList(sinkId, Statements.SELECT_LEFT_TUPLES);
    }

    /**
     * @param sinkId
     * @return
     */
    public List<Object> getRightTuples(final int sinkId) {
        return getTupleList(sinkId, Statements.SELECT_RIGHT_TUPLES);
    }

    /**
     * @param object
     */
    public void saveAgendaItemN(Object object) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection
                    .prepareStatement(Statements.INSERT_INTO_A_I_STATEMENT);
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objOutputStream.writeObject(object);
            objOutputStream.flush();
            objOutputStream.close();

            final byte[] data = byteOutputStream.toByteArray();
            statement.setObject(1, data);
            statement.executeUpdate();
            closeEverything(connection, statement, null);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveBatchAgendaItem(Object object) {
//        m_agendaItems.add(object);
        if (COUNTER.getAndIncrement() % BATCH_SIZE == 0) {
            saveAgendaItems();
        }
    }
    /**
     * 
     * @param item
     *            to be saved.
     */
    public void saveAgendaItems() {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        try {
//            connection = getConnection();
//            statement = connection
//                    .prepareStatement(Statements.INSERT_INTO_A_I_STATEMENT);
//            for (Object object : m_agendaItems) {
//                final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
//                final ObjectOutputStream objOutputStream = new ObjectOutputStream(
//                        byteOutputStream);
//                objOutputStream.writeObject(object);
//                objOutputStream.flush();
//                objOutputStream.close();
//
//                final byte[] data = byteOutputStream.toByteArray();
//                statement.setObject(1, data);
//                statement.addBatch();
//            }
//            statement.executeBatch();
//            m_agendaItems.clear();
//            closeEverything(connection, statement, null);
//        } catch (final IOException e) {
//            e.printStackTrace();
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        }
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
                final Object object = readObject(resultSet);
                closeEverything(connection, statement, resultSet);
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
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Object readObject(ResultSet resultSet) throws IOException,
            ClassNotFoundException, SQLException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(
                resultSet.getBytes("object"));
        final ObjectInputStream objectInputStream = new ObjectInputStream(
                inputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }

    /**
     * @return
     */
    public int getTotalNumberOfRows() {
        int totalCount = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection
                    .prepareStatement(Statements.COUNT_TOTAL_NUMBER_OF_AGENDA_ITEMS);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                totalCount = resultSet.getInt(Statements.COUNT_STAR);
            }
            closeEverything(connection, statement, resultSet);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return totalCount;
    }

    /**
     * @param sinkId
     * @param resultSet
     * @param statement
     * @param connection
     * @return
     */
    public ResultSet getRightTupleCursor(int sinkId, Connection connection,
            PreparedStatement statement, ResultSet resultSet) {
        return getTupleCursor(sinkId, Statements.SELECT_RIGHT_TUPLES,
                connection, statement, resultSet);
    }

    /**
     * @param sinkId
     * @return
     */
    public ResultSet getLeftTupleCursor(int sinkId, Connection connection,
            PreparedStatement statement, ResultSet resultSet) {
        return getTupleCursor(sinkId, Statements.SELECT_LEFT_TUPLES,
                connection, statement, resultSet);
    }

    /**
     * Closes all given resources.
     * 
     * @param connection
     * @param statement
     * @param resultSet
     * @throws SQLException
     */
    public static void closeEverything(final Connection connection,
            final PreparedStatement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("Can't close resultSet!");
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Can't cloes statement!");
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Can't cloes connection!");
                e.printStackTrace();
            }
        }
    }

    /**
     * @param sinkId
     * @param selectStmt
     * @return
     */
    private List<Object> getTupleList(final int sinkId, final String selectStmt) {
        final List<Object> restults = new ArrayList<Object>();
        try {
            final Connection connection = getConnection();
            final PreparedStatement statement = connection
                    .prepareStatement(selectStmt);
            statement.setInt(1, sinkId);
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                restults.add(readObject(resultSet));
            }
            closeEverything(connection, statement, resultSet);
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return restults;
    }

    /**
     * @param sinkId
     * @param tuple
     * @param insertStmt
     */
    private void saveTuple(final int sinkId, final Object tuple,
            String insertStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objectOutputStream.writeObject(tuple);
            objectOutputStream.flush();
            objectOutputStream.close();

            final byte[] data = byteOutputStream.toByteArray();

            statement = connection.prepareStatement(insertStmt);
            statement.setInt(1, sinkId);
            statement.setObject(2, data);
            statement.executeUpdate();
            closeEverything(connection, statement, null);
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private void truncateLeftTuples() {
        executeStatement(Statements.TRUNCATE_TABLE_LEFT_TUPLES);
    }

    /**
     * 
     */
    private void truncateRightTuples() {
        executeStatement(Statements.TRUNCATE_TABLE_RIGHT_TUPLES);
    }

    /**
     * 
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
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(stmt);
            statement.execute();
            closeEverything(connection, statement, null);
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
    public Connection getConnection() throws SQLException {
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
     * @param sinkId
     * @param resultSet
     * @param statement
     * @param connection
     * @param selectRightTuples
     * @return
     */
    private ResultSet getTupleCursor(int sinkId, String selectStmt,
            Connection connection, PreparedStatement statement,
            ResultSet resultSet) {
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            statement = connection.prepareStatement(selectStmt);
            statement.setInt(1, sinkId);
            statement.setFetchSize(FETCH_SIZE);
            resultSet = statement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
