package com.fr.swift.jdbc.statement;

import com.fr.general.jsqlparser.JSQLParserException;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.bean.InsertBean;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.jdbc.parser.SqlParserFactory;
import com.fr.swift.jdbc.result.ResultSetWrapper;
import com.fr.swift.jdbc.rpc.RpcCaller;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.Crasher;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 *
 * @author pony
 * @date 2018/8/17
 */
public class SwiftStatement implements Statement {
    private RpcCaller.SelectRpcCaller caller;
    private RpcCaller.MaintenanceRpcCaller maintenanceRpcCaller;
    private SwiftDatabase schema;

    public SwiftStatement(SwiftDatabase schema, RpcCaller.SelectRpcCaller caller, RpcCaller.MaintenanceRpcCaller maintenanceRpcCaller) {
        this.schema = schema;
        this.caller = caller;
        this.maintenanceRpcCaller = maintenanceRpcCaller;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        SwiftResultSet resultSet = null;
        try {
            QueryBean queryBean = SqlParserFactory.parsQuery(sql, schema, caller);
            resultSet = caller.query(schema, queryBean.toString());
        } catch (Exception e) {
            return Crasher.crash(new SwiftJDBCNotSupportedException(sql, e));
        }
        return new ResultSetWrapper(resultSet);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        try {
            if (sql.toLowerCase().trim().startsWith("insert")) {
                InsertBean insertBean = SqlParserFactory.parsInsert(sql, schema, caller);
                if (null != insertBean.getQueryJson()) {
                    return maintenanceRpcCaller.insert(schema, insertBean.getTableName(), insertBean.getQueryJson());
                } else {
                    return maintenanceRpcCaller.insert(schema, insertBean.getTableName(), insertBean.getColumnNames(), insertBean.getDatas());
                }
            } else {
                return Crasher.crash(new SwiftJDBCNotSupportedException(sql));
            }
        } catch (JSQLParserException e) {
            return Crasher.crash(new SwiftJDBCNotSupportedException(sql, e));
        }
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    public void closeOnCompletion() throws SQLException {

    }

    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
