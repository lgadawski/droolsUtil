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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.ds.PGPoolingDataSource;

/**
 * Utility class that uses JDBC connections to persist data.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class JdbcManagerUtil {
    /**
     * Instance.
     */
    private static JdbcManagerUtil INSTANCE = null;
    /**
     * Initial pool size
     */
    private static final int POOL_SIZE = 10000;
    /**
     * Fetch size for query.
     */
    public static final int FETCH_SIZE = 10000;
    /**
     * 
     */
    private final PGPoolingDataSource m_poolDataSource;

    /**
     * Private constructor to block creating objects.
     */
    private JdbcManagerUtil() {
        m_poolDataSource = new PGPoolingDataSource();
        setConnectionProps();
        truncateTables();
    }

    /**
     * @return instance of {@link JdbcManagerUtil} class.
     */
    public static synchronized JdbcManagerUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (JdbcManagerUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JdbcManagerUtil();
                }
            }
            return INSTANCE;
        }
        return INSTANCE;
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
     * Gets agenda items object from table for rownum = 1.
     * 
     * @return
     */
    public Object getNextAgendaItemObject() {
        return getObjectByStatement(Statements.SELECT_LAST_ROW_AGENDA_ITEM);
    }

    /**
     * @param sinkId
     * @param leftTuple
     * @param handleId
     * @param parentId
     * @return
     */
    public int saveLeftTuple(final Integer parentId, final Integer handleId,
            final Integer parentRightTupleId, final int sinkId,
            final Object leftTuple) {
        return saveLeftTupleParam(parentId, handleId, parentRightTupleId,
                sinkId, leftTuple, Statements.UPDATE_LEFT_TUPLE_P,
                Statements.INSERT_INTO_LEFT_TUPLES_P);
    }

    /**
     * @param sinkId
     * @param rightTuple
     * @param handleId
     * @return
     */
    public int saveRightTuple(final Integer handleId, final int sinkId,
            final Object rightTuple) {
        return saveTuple(handleId, sinkId, rightTuple,
                Statements.INSERT_INTO_RIGHT_TUPLES_P);
    }

    /**
     * @param tupleId
     * @param rightTuple
     */
    public void updateRightTuple(Integer tupleId, Object rightTuple) {
        updateObjectById(tupleId, rightTuple, Statements.UPDATE_RIGHT_TUPLE);
    }

    /**
     * Removes AgendaItem from top.
     * 
     * @param item
     */
    public void removeNextAgendaItem() {
        deleteFirstAgendaItem();
    }

    /**
     * @param tupleId
     * @param sinkId
     */
    public void removeRightTuple(final long tupleId, final int sinkId) {
        removeTuple(tupleId, sinkId, Statements.DELETE_RIGHT_TUPLE);
    }

    /**
     * Removes all left tuples where child_righ_tuple_id == chilldRightTupleId.
     * 
     * @param childRightTupleId
     *            - fk in leftTuples table.
     */
    public void removeRightTupleChilds(Integer childRightTupleId) {
        removeObjectById(childRightTupleId, Statements.DELETE_CHILD_LEFT_TUPLES);
    }

    /**
     * @param handleId
     */
    public void removeFactHandle(int handleId) {
        removeObjectById(handleId, Statements.DELETE_FACT_HANDLE);
    }

    /**
     * @param tupleId
     */
    public void removeAgendaItemByLeftTupleId(Integer tupleId) {
        removeObjectById(tupleId, Statements.DELETE_AGENDA_ITEM_BY_LT_ID);
    }

    /**
     * @param tupleId
     * @param sinkId
     */
    public void removeLeftTuple(final long tupleId, final int sinkId) {
        removeTuple(tupleId, sinkId, Statements.DELETE_LEFT_TUPLE);
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
     * @param handleId
     * @return
     */
    public Object getFactHandle(Integer handleId) {
        return getObjectByParamaterId(handleId,
                Statements.SELECT_FACT_HANDLE_BY_ID);
    }

    /**
     * @param tupleId
     * @return
     */
    public Object getRightTuple(Integer tupleId) {
        return getObjectByParamaterId(tupleId, Statements.SELECT_RIGHT_TUPLE_ID);
    }

    /**
     * Saves fact handle.
     */
    public void saveFactHandle(int handleId, Object handle) {
        saveOrUpdateFactHandle(handleId, handle,
                Statements.INSERT_INTO_FACT_HANDLES_P);
    }

    /**
     * Saves agenda item.
     */
    public void saveAgendaItem(final Integer tupleId, final Object object) {
        saveObjectWithId(tupleId, object,
                Statements.INSERT_INTO_A_I_STATEMENT_P);
    }

    /**
     * @param id
     * @return
     */
    public Object getObjectByParamaterId(final int id, String sqlStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sqlStmt);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
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
        closeEverything(connection, statement, resultSet);
        return null;
    }

    /**
     * @param object
     * @param selectLastRowAgendaItem
     * @return
     */
    private Object getObjectByStatement(String sqlStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sqlStmt);
            resultSet = statement.executeQuery();
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
        closeEverything(connection, statement, resultSet);
        return null;
    }

    /**
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Object readObject(final ResultSet resultSet) throws IOException,
            ClassNotFoundException, SQLException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(
                resultSet.getBytes("object"));
        final ObjectInputStream objectInputStream = new ObjectInputStream(
                inputStream);
        final Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }

    /**
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public Integer readLeftTupleId(final ResultSet resultSet)
            throws SQLException {
        return resultSet.getInt(Statements.LEFT_TUPLE_ID);
    }

    /**
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public Integer readRightTupleId(ResultSet resultSet) throws SQLException {
        return resultSet.getInt(Statements.RIGHT_TUPLE_ID);
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
                // totalCount = resultSet.getInt(Statements.COUNT_STAR);
                totalCount = resultSet.getInt(Statements.COUNT);
            }
            closeEverything(connection, statement, resultSet);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return totalCount;
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
            final PreparedStatement statement, final ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (final SQLException e) {
                System.out.println("Can't close resultSet!");
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (final SQLException e) {
                System.out.println("Can't cloes statement!");
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (final SQLException e) {
                System.out.println("Can't cloes connection!");
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     */
    public void truncateAgendaItems() {
        executeStatement(Statements.TRUNCATE_TABLE_AGENDA_ITEMS);
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
    private void truncateFactHandles() {
        executeStatement(Statements.TRUNCATE_TABLE_FACT_HANDLES);
    }

    /**
     * Truncate LEFT_TUPLES, RIGHT_TUPLES, FACT_HANDLES, AGENDA_ITEM tables.
     */
    private void truncateTables() {
        truncateLeftTuples();
        truncateRightTuples();
        truncateFactHandles();
        truncateAgendaItems();
    }

    /**
     * 
     */
    private void deleteFirstAgendaItem() {
        executeStatement(Statements.DELETE_FIRST_ROW_P);
    }

    /**
     * 
     */
    private void setConnectionProps() {
        m_poolDataSource.setDataSourceName("gadons source");
        m_poolDataSource.setServerName("localhost");
        m_poolDataSource.setDatabaseName("postgres");
        m_poolDataSource.setUser(JdbcPostgresqlConfig.USER_NAME);
        m_poolDataSource.setPassword(JdbcPostgresqlConfig.PASSWORD);
        m_poolDataSource.setMaxConnections(POOL_SIZE);
    }

    /**
     * @param sinkId
     * @param selectStmt
     * @return
     */
    private List<Object> getTupleList(final int sinkId, final String selectStmt) {
        final List<Object> restults = new ArrayList<Object>();
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection
                    .prepareStatement(selectStmt);
            statement.setInt(1, sinkId);
            ResultSet resultSet = statement.executeQuery();
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
     * @param tupleId
     * @param rightTuple
     * @param updateRightTuple
     */
    private void updateObjectById(Integer tupleId, Object object, String sqlStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();

            final byte[] data = byteOutputStream.toByteArray();

            statement = connection.prepareStatement(sqlStmt);
            statement.setObject(1, data);
            statement.setObject(2, tupleId);
            statement.executeUpdate();
            closeEverything(connection, statement, null);
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param parentId
     * @param sinkId
     * @param tuple
     * @param handleId
     * @param insertStmt
     * @return
     */
    private int saveLeftTupleParam(final Integer parentId,
            final Integer handleId, final Integer parentRightTupleId,
            final int sinkId, final Object tuple, final String updateStmt,
            final String insertStmt) {
        Connection connection = null;
        PreparedStatement insert = null, update = null;
        int generatedKey = -1;
        try {
            connection = getConnection();
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objectOutputStream.writeObject(tuple);
            objectOutputStream.flush();
            objectOutputStream.close();

            final byte[] data = byteOutputStream.toByteArray();
            // object, parent_tuple_id, parent_right_tuple_id,
            // sink_id,
            update = connection.prepareStatement(updateStmt);
            update.setObject(1, data);
            update.setObject(2, parentId);
            update.setObject(3, parentRightTupleId);
            update.setObject(4, handleId);
            update.setObject(5, sinkId);
            update.executeUpdate();

            insert = connection.prepareStatement(insertStmt,
                    Statement.RETURN_GENERATED_KEYS);
            // parent_tuple_id, fact_handle_id,
            // sink_id, object, fact_handle_id, sink_id
            insert.setObject(1, parentId);
            insert.setObject(2, handleId);
            insert.setObject(3, parentRightTupleId);
            insert.setObject(4, sinkId);
            insert.setObject(5, data);
            insert.setObject(6, parentId);
            insert.setObject(7, parentRightTupleId);
            insert.setObject(8, handleId);
            insert.setObject(9, sinkId);
            insert.executeUpdate();

            final ResultSet keys = insert.getGeneratedKeys();
            if (keys.next()) {
                generatedKey = (int) keys.getLong(Statements.LEFT_TUPLE_ID);
            }
            closeEverything(connection, insert, keys);
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return generatedKey;
    }

    /**
     * @param sinkId
     * @param tuple
     * @param handleId
     * @param insertStmt
     * @return
     */
    private int saveTuple(Integer handleId, final int sinkId,
            final Object tuple, final String insertStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        int generatedKey = -1;
        try {
            connection = getConnection();
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objectOutputStream.writeObject(tuple);
            objectOutputStream.flush();
            objectOutputStream.close();

            final byte[] data = byteOutputStream.toByteArray();

            statement = connection.prepareStatement(insertStmt,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setObject(1, handleId);
            statement.setInt(2, sinkId);
            statement.setObject(3, data);
            statement.executeUpdate();

            final ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                generatedKey = (int) keys.getLong(Statements.RIGHT_TUPLE_ID);
            }
            closeEverything(connection, statement, null);
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return generatedKey;
    }

    /**
     * Saves object with given sql stmt, (id, object)
     * 
     * @param id
     * @param object
     * @param sqlStmt
     */
    private void saveObjectWithId(final Integer id, final Object object,
            String sqlStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sqlStmt);
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objOutputStream.writeObject(object);
            objOutputStream.flush();
            objOutputStream.close();

            final byte[] data = byteOutputStream.toByteArray();
            statement.setObject(1, id);
            statement.setObject(2, data);
            statement.executeUpdate();
            closeEverything(connection, statement, null);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform 'upsert'.
     * 
     * @param handleId
     * @param object
     * @param sqlStmt
     */
    private void saveOrUpdateFactHandle(int handleId, Object object,
            String sqlStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sqlStmt);
            final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objOutputStream = new ObjectOutputStream(
                    byteOutputStream);
            objOutputStream.writeObject(object);
            objOutputStream.flush();
            objOutputStream.close();

            final byte[] data = byteOutputStream.toByteArray();

            statement.setObject(1, data);
            statement.setObject(2, handleId);
            statement.setObject(3, handleId);
            statement.setObject(4, data);
            statement.setObject(5, handleId);
            statement.executeUpdate();
            closeEverything(connection, statement, null);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param tupleId
     * @param sinkId
     * @param stmt
     */
    private void removeTuple(final long tupleId, final int sinkId,
            final String stmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(stmt);
            statement.setLong(1, tupleId);
            statement.setInt(2, sinkId);
            statement.execute();
            closeEverything(connection, statement, null);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs delete operation by given query.
     * 
     * @param id
     * @param sqlStmt
     */
    private void removeObjectById(int id, String sqlStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sqlStmt);
            statement.setLong(1, id);
            statement.execute();
            closeEverything(connection, statement, null);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }
}
