package com.fr.swift.jdbc.rpc;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.jdbc.result.SwiftPaginationResultSet;
import com.fr.swift.jdbc.rpc.invoke.ClientProxy;
import com.fr.swift.jdbc.rpc.nio.RpcConnector;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcCaller implements TableService {

    protected String address;

    public static SelectRpcCaller connectSelectService(String address) {
        return new SelectRpcCaller(address);
    }

    public static SelectRpcCaller connectSelectService(String host, int port) {
        return new SelectRpcCaller(host, port);
    }

    public static MaintenanceRpcCaller connectMaintenanceService(String address) {
        return new MaintenanceRpcCaller(address);
    }

    public static MaintenanceRpcCaller connectMaintenanceService(String host, int port) {
        return new MaintenanceRpcCaller(host, port);
    }

    private SwiftMetaData getMetaData(SwiftDatabase schema, String tableName) {
        ClientProxy proxy = new ClientProxy(new RpcConnector(address));
        try {
            proxy.start();
            return proxy.getProxy(TableService.class).detectiveMetaData(schema, tableName);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        } finally {
            proxy.stop();
        }
    }

    @Override
    public SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) {
        return getMetaData(schema, tableName);
    }

    @Override
    public boolean isTableExists(SwiftDatabase schema, String tableName) {
        return null != getMetaData(schema, tableName);
    }

    public static class SelectRpcCaller extends RpcCaller implements SelectService, TableService {
        private SelectRpcCaller(String address) {
            this.address = address;
        }

        private SelectRpcCaller(String host, int port) {
            this.address = host + ":" + port;
        }

        @Override
        public SwiftResultSet query(SwiftDatabase database, String queryJson) {
            ClientProxy proxy = new ClientProxy(new RpcConnector(address));
            try {
                proxy.start();
                SwiftResultSet resultSet = proxy.getProxy(SelectService.class).query(database, queryJson);
                return new SwiftPaginationResultSet((SerializableDetailResultSet) resultSet, this, database);
            } catch (SQLException e) {
                SwiftLoggers.getLogger().error(e);
                return null;
            } finally {
                proxy.stop();
            }
        }
    }

    public static class MaintenanceRpcCaller extends RpcCaller implements DataMaintenanceService, TableService {
        private MaintenanceRpcCaller(String address) {
            this.address = address;
        }

        private MaintenanceRpcCaller(String host, int port) {
            this.address = host + ":" + port;
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws SQLException {
            ClientProxy proxy = new ClientProxy(new RpcConnector(address));
            try {
                proxy.start();
                return proxy.getProxy(DataMaintenanceService.class).insert(schema, tableName, fields, rows);
            } finally {
                proxy.stop();
            }
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws SQLException {
            return insert(schema, tableName, Collections.<String>emptyList(), rows);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, String queryJson) throws SQLException {
            ClientProxy proxy = new ClientProxy(new RpcConnector(address));
            try {
                proxy.start();
                return proxy.getProxy(DataMaintenanceService.class).insert(schema, tableName, queryJson);
            } finally {
                proxy.stop();
            }
        }

        @Override
        public int delete(SwiftDatabase schema, String tableName, Where where) {
            ClientProxy proxy = new ClientProxy(new RpcConnector(address));
            try {
                proxy.start();
                return proxy.getProxy(DataMaintenanceService.class).delete(schema, tableName, where);
            } finally {
                proxy.stop();
            }
        }

        @Override
        public int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) {
            ClientProxy proxy = new ClientProxy(new RpcConnector(address));
            try {
                proxy.start();
                return proxy.getProxy(DataMaintenanceService.class).update(schema, tableName, resultSet, where);
            } finally {
                proxy.stop();
            }
        }

        @Override
        public int createTable(SwiftDatabase schema, String tableName, List<Column> columns) {
            ClientProxy proxy = new ClientProxy(new RpcConnector(address));
            try {
                proxy.start();
                return proxy.getProxy(DataMaintenanceService.class).createTable(schema, tableName, columns);
            } finally {
                proxy.stop();
            }
        }
    }
}
