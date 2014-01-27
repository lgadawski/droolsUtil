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
        return getObjectByParamaterId(1, Statements.SELECT_LAST_ROW_AGENDA_ITEM);
    }

    /**
     * @param sinkId
     * @param leftTuple
     * @param handleId
     * @param parentId
     * @return
     */
    public int saveLeftTupleAndFactHandle(final Integer parentId,
            final Integer handleId, final Integer parentRightTupleId,
            final int sinkId, final Object leftTuple, final Object factHandle) {
        return saveLeftTupleParam(parentId, handleId, parentRightTupleId,
                sinkId, leftTuple, factHandle,
                Statements.UPSERT_INTO_LEFT_TUPLES,
                Statements.UPSERT_INTO_FACT_HANDLES);
        // return saveLeftTupleParam(parentId, handleId, parentRightTupleId,
        // sinkId, leftTuple, factHandle, Statements.UPDATE_LEFT_TUPLE,
        // Statements.INSERT_INTO_LEFT_TUPLES,
        // Statements.UPDATE_FACT_HANDLE,
        // Statements.INSERT_INTO_FACT_HANDLES);
    }

    /**
     * @param sinkId
     * @param rightTuple
     * @param handleId
     * @return
     */
    public int saveRightTupleAndFactHandle(final Integer handleId,
            final int sinkId, final Object rightTuple, final Object factHandle) {
        return saveOrUpdateRightTuple(handleId, sinkId, rightTuple, factHandle,
                Statements.UPDATE_RIGHT_TUPLE,
                Statements.INSERT_INTO_RIGHT_TUPLES,
                Statements.UPDATE_FACT_HANDLE,
                Statements.INSERT_INTO_FACT_HANDLES, Statements.RIGHT_TUPLE_ID);
    }

    /**
     * Saves fact handle.
     */
    public void saveFactHandle(final int handleId, final Object handle) {
        saveOrUpdateFactHandle(handleId, handle, Statements.UPDATE_FACT_HANDLE,
                Statements.INSERT_INTO_FACT_HANDLES);
    }

    /**
     * Saves agenda item.
     */
    public void saveAgendaItem(final Integer tupleId, final Object object) {
        saveObjectWithId(tupleId, object, Statements.INSERT_INTO_A_I_STATEMENT);
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
    public void removeRightTuple(final long tupleId) {
        removeObjectById((int) tupleId, Statements.DELETE_RIGHT_TUPLE);
    }

    /**
     * Removes all left tuples where child_righ_tuple_id == chilldRightTupleId.
     * 
     * @param childRightTupleId
     *            - fk in leftTuples table.
     */
    public void removeRightTupleChilds(final Integer childRightTupleId) {
        removeObjectById(childRightTupleId, Statements.DELETE_CHILD_LEFT_TUPLES);
    }

    /**
     * @param handleId
     */
    public void removeFactHandle(final int handleId) {
        removeObjectById(handleId, Statements.DELETE_FACT_HANDLE);
    }

    /**
     * @param tupleId
     */
    public void removeAgendaItemByLeftTupleId(final long tupleId) {
        removeObjectById((int) tupleId, Statements.DELETE_AGENDA_ITEM_BY_LT_ID);
    }

    /**
     * @param tupleId
     * @param sinkId
     */
    public void removeLeftTuple(final long tupleId) {
        removeObjectById((int) tupleId, Statements.DELETE_LEFT_TUPLE);
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
    public Object getFactHandle(final Integer handleId) {
        return getObjectByParamaterId(handleId,
                Statements.SELECT_FACT_HANDLE_BY_ID);
    }

    /**
     * @param tupleId
     * @return
     */
    public Object getRightTuple(final Integer tupleId) {
        return getObjectByParamaterId(tupleId, Statements.SELECT_RIGHT_TUPLE_ID);
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
                resultSet.getBytes(Statements.OBJECT));
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
    public Integer readRightTupleId(final ResultSet resultSet)
            throws SQLException {
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
     * 
     */
    private void deleteFirstAgendaItem() {
        executeStatement(Statements.DELETE_FIRST_AGENDA_ITEM);
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
     * Initializes connection to db and executes statement.
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
            statement.setPoolable(true);
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
    private void removeObjectById(final int id, final String sqlStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sqlStmt);
            statement.setPoolable(true);
            statement.setLong(1, id);
            statement.execute();
            closeEverything(connection, statement, null);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param id
     * @return
     */
    private Object getObjectByParamaterId(final int id, final String sqlStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sqlStmt);
            statement.setPoolable(true);
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
     * Done in one transaction.
     * 
     * @return
     */
    private int saveLeftTupleParam(final Integer parentId,
            final Integer handleId, final Integer parentRightTupleId,
            final int sinkId, final Object tuple, final Object factHandle,
            final String upsertLTStmt, final String upsertFHStmt) {
        Connection connection = null;
        PreparedStatement upsertLT = null, upsertFH = null;
        int generatedKey = -1;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            final byte[] dataLT = getData(tuple);
            final byte[] dataFH = getData(factHandle);

            upsertFH = connection.prepareStatement(upsertFHStmt);
            upsertFH.setPoolable(true);
            upsertFH.setObject(1, dataFH);
            upsertFH.setObject(2, handleId);
            upsertFH.setObject(3, handleId);
            upsertFH.setObject(4, dataFH);
            upsertFH.executeUpdate();

            upsertLT = connection.prepareStatement(upsertLTStmt,
                    Statement.RETURN_GENERATED_KEYS);
            upsertLT.setPoolable(true);
            upsertLT.setObject(1, dataLT);
            upsertLT.setObject(2, parentId);
            upsertLT.setObject(3, parentRightTupleId);
            upsertLT.setObject(4, handleId);
            upsertLT.setObject(5, sinkId);
            upsertLT.setObject(6, parentId);
            upsertLT.setObject(7, handleId);
            upsertLT.setObject(8, parentRightTupleId);
            upsertLT.setObject(9, sinkId);
            upsertLT.setObject(10, dataLT);
            upsertLT.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);

            final ResultSet keys = upsertLT.getGeneratedKeys();
            if (keys.next()) {
                generatedKey = (int) keys.getLong(Statements.LEFT_TUPLE_ID);
            }

            upsertFH.close();
            closeEverything(connection, upsertLT, keys);
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return generatedKey;
    }

    /**
     * Done in one transaction.
     * 
     * @return
     */
    private int saveLeftTupleParam(final Integer parentId,
            final Integer handleId, final Integer parentRightTupleId,
            final int sinkId, final Object tuple, final Object factHandle,
            final String updateLTStmt, final String insertLTStmt,
            final String updateFHStmt, final String insertFHStmt) {
        Connection connection = null;
        PreparedStatement insertLT = null, updateLT = null, updateFH = null, insertFH = null;
        int generatedKey = -1;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            final byte[] dataLT = getData(tuple);
            final byte[] dataFH = getData(factHandle);

            updateFH = connection.prepareStatement(updateFHStmt);
            setParamsForFactHandleUpdate(handleId, dataFH, updateFH);
            updateFH.executeUpdate();

            insertFH = connection.prepareStatement(insertFHStmt);
            setParamsForFactHandleInsert(handleId, dataFH, insertFH);
            insertFH.executeUpdate();

            updateLT = connection.prepareStatement(updateLTStmt);
            setParamsForLTUpdate(parentId, handleId, parentRightTupleId,
                    sinkId, updateLT, dataLT);
            updateLT.executeUpdate();

            insertLT = connection.prepareStatement(insertLTStmt,
                    Statement.RETURN_GENERATED_KEYS);
            setParamsForLTInsert(parentId, handleId, parentRightTupleId,
                    sinkId, insertLT, dataLT);
            insertLT.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);

            final ResultSet keys = insertLT.getGeneratedKeys();
            if (keys.next()) {
                generatedKey = (int) keys.getLong(Statements.LEFT_TUPLE_ID);
            }

            updateFH.close();
            updateLT.close();
            insertFH.close();
            closeEverything(connection, insertLT, keys);
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return generatedKey;
    }

    private void setParamsForFactHandleInsert(final Integer handleId,
            final byte[] dataFH, final PreparedStatement insertFH)
            throws SQLException {
        insertFH.setObject(1, handleId);
        insertFH.setObject(2, dataFH);
        insertFH.setObject(3, handleId);
    }

    private void setParamsForFactHandleUpdate(final Integer handleId,
            final byte[] data, final PreparedStatement updateFH)
            throws SQLException {
        updateFH.setObject(1, data);
        updateFH.setObject(2, handleId);
    }

    private void setParamsForLTInsert(final Integer parentId,
            final Integer handleId, final Integer parentRightTupleId,
            final int sinkId, final PreparedStatement insertLT,
            final byte[] data) throws SQLException {
        // parent_tuple_id, fact_handle_id,
        // sink_id, object, fact_handle_id, sink_id
        insertLT.setObject(1, parentId);
        insertLT.setObject(2, handleId);
        insertLT.setObject(3, parentRightTupleId);
        insertLT.setObject(4, sinkId);
        insertLT.setObject(5, data);
        insertLT.setObject(6, parentId);
        insertLT.setObject(7, parentRightTupleId);
        insertLT.setObject(8, handleId);
        insertLT.setObject(9, sinkId);
    }

    private void setParamsForLTUpdate(final Integer parentId,
            final Integer handleId, final Integer parentRightTupleId,
            final int sinkId, final PreparedStatement updateLT,
            final byte[] data) throws SQLException {
        updateLT.setObject(1, data);
        updateLT.setObject(2, parentId);
        updateLT.setObject(3, parentRightTupleId);
        updateLT.setObject(4, handleId);
        updateLT.setObject(5, sinkId);
    }

    /**
     * @param sinkId
     * @param tuple
     * @param handleId
     * @param insertRTStmt
     * @return
     */
    private int saveOrUpdateRightTuple(final Integer handleId,
            final int sinkId, final Object tuple, final Object factHandle,
            final String updateRTStmt, final String insertRTStmt,
            final String updateFHStmt, final String insertFHStmt,
            final String idColumnName) {
        Connection connection = null;
        PreparedStatement updateRT = null, insertRT = null, updateFH = null, insertFH = null;
        int generatedKey = -1;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            final byte[] dataRT = getData(tuple);
            final byte[] dataFH = getData(factHandle);

            updateFH = connection.prepareStatement(updateFHStmt);
            setParamsForFactHandleUpdate(handleId, dataFH, updateFH);
            updateFH.executeUpdate();

            insertFH = connection.prepareStatement(insertFHStmt);
            setParamsForFactHandleInsert(handleId, dataFH, insertFH);
            insertFH.executeUpdate();

            updateRT = connection.prepareStatement(updateRTStmt);
            setParamsForRTUpdate(handleId, sinkId, updateRT, dataRT);
            updateRT.executeUpdate();

            insertRT = connection.prepareStatement(insertRTStmt,
                    Statement.RETURN_GENERATED_KEYS);
            setParamsForRTInsert(handleId, sinkId, insertRT, dataRT);
            insertRT.executeUpdate();

            final ResultSet keys = insertRT.getGeneratedKeys();
            if (keys.next()) {
                generatedKey = (int) keys.getLong(idColumnName);
            }
            connection.commit();
            connection.setAutoCommit(true);

            updateFH.close();
            updateRT.close();
            insertFH.close();
            closeEverything(connection, insertRT, null);
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return generatedKey;
    }

    private void setParamsForRTInsert(final Integer handleId, final int sinkId,
            final PreparedStatement insertRT, final byte[] data)
            throws SQLException {
        insertRT.setObject(1, handleId);
        insertRT.setInt(2, sinkId);
        insertRT.setObject(3, data);
        insertRT.setObject(4, handleId);
        insertRT.setInt(5, sinkId);
    }

    private void setParamsForRTUpdate(final Integer handleId, final int sinkId,
            final PreparedStatement updateRT, final byte[] data)
            throws SQLException {
        updateRT.setObject(1, data);
        updateRT.setObject(2, handleId);
        updateRT.setInt(3, sinkId);
    }

    /**
     * Saves object with given sql stmt, (id, object)
     * 
     * @param id
     * @param object
     * @param sqlStmt
     */
    private void saveObjectWithId(final Integer id, final Object object,
            final String sqlStmt) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();

            final byte[] data = getData(object);
            
            statement = connection.prepareStatement(sqlStmt);
            statement.setPoolable(true);
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
     * @param updateStmt
     * @param insertStmt
     */
    private void saveOrUpdateFactHandle(final int handleId,
            final Object object, final String updateStmt,
            final String insertStmt) {
        Connection connection = null;
        PreparedStatement update = null, insert = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            update = connection.prepareStatement(updateStmt);
            insert = connection.prepareStatement(insertStmt);

            final byte[] data = getData(object);

            update.setObject(1, data);
            update.setObject(2, handleId);
            update.executeUpdate();

            insert.setObject(1, handleId);
            insert.setObject(2, data);
            insert.setObject(3, handleId);
            insert.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);

            update.close();
            closeEverything(connection, insert, null);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets byte array from object.
     * 
     * @param object
     * @return
     * @throws IOException
     */
    private byte[] getData(final Object object) throws IOException {
        final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray();
    }
}
