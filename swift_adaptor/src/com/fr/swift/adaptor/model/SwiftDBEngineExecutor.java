package com.fr.swift.adaptor.model;

import com.finebi.base.common.resource.FineResourceItem;
import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.service.engine.table.FineTableEngineExecutor;
import com.finebi.conf.internalimp.table.FineDBBusinessTable;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.finebi.conf.utils.FineConnectionUtils;
import com.fr.base.FRContext;
import com.fr.data.core.DataCoreUtils;
import com.fr.data.core.db.TableProcedure;
import com.fr.data.impl.Connection;
import com.fr.general.ComparatorUtils;
import com.fr.stable.ArrayUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.struct.SwiftDetailCell;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftRealDetailResult;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.segment.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.PrimitiveDetailColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.swift.source.db.TableDBSource;
import com.fr.third.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-2 11:22:19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
@Service("fineDataModel")
public class SwiftDBEngineExecutor implements FineTableEngineExecutor {

    public List<TableProcedure> getAllTables(Connection connection, String connectionName, String schema) {
        TableProcedure[] tps = new TableProcedure[0];
        TableProcedure[] views = new TableProcedure[0];
        try {
            if (schema != null && !schema.isEmpty()) {
                String[] schemas = DataCoreUtils.getDatabaseSchema(connection);
                if (StringUtils.isNotEmpty(schema) || schemas.length == 0) {
                    TableProcedure[] sqlTables = DataCoreUtils.getTables(connection, TableProcedure.TABLE, schema, true);
                    tps = ArrayUtils.addAll(tps, sqlTables);
                    views = ArrayUtils.addAll(views, FRContext.getCurrentEnv().getTableProcedure(connection, TableProcedure.VIEW, schema));
                }
            } else {
                tps = FRContext.getCurrentEnv().getTableProcedure(connection, TableProcedure.TABLE, null);
                views = FRContext.getCurrentEnv().getTableProcedure(connection, TableProcedure.VIEW, null);
            }
            List<TableProcedure> result = duplicateRemove(tps, views);
            return result;
        } catch (Exception e) {
            return new ArrayList<TableProcedure>();
        }
    }

    private List<TableProcedure> duplicateRemove(TableProcedure[] tps, TableProcedure[] views) {
        List<TableProcedure> procedureList = new ArrayList<TableProcedure>();
        for (TableProcedure procedure : views) {
            if (!existInTables(procedure, tps)) {
                procedureList.add(procedure);
            }
        }
        for (TableProcedure procedure : tps) {
            procedureList.add(procedure);

        }
        return procedureList;
    }

    private boolean existInTables(TableProcedure procedure, TableProcedure[] sqlTables) {
        for (TableProcedure table : sqlTables) {
            if (ComparatorUtils.equals(procedure.getSchema(), table.getSchema()) && ComparatorUtils.equals(procedure.getName(), table.getName())) {
                return true;
            }
        }
        return false;
    }


    

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

    @Override
    public BIDetailTableResult getPreviewData(FineBusinessTable table, int rowCount) throws Exception {
        FineDBBusinessTable dbTable = (FineDBBusinessTable) table;
        FineConnection connection = FineConnectionUtils.getConnectionByName(dbTable.getConnName());
        ConnectionManager.getInstance().registerConnectionInfo(dbTable.getConnName(),
                new SwiftConnectionInfo(connection.getSchema(), connection.getConnection()));
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createDBSourcePreviewTransfer(dbTable.getConnName(), dbTable.getTableName(), rowCount);
        SwiftResultSet swiftResultSet = transfer.createResultSet();
        BIDetailTableResult detailTableResult = new SwiftDetailTableResult(swiftResultSet);
        return detailTableResult;

    }

    @Override
    public BIDetailTableResult getRealData(FineBusinessTable table, int rowCount) throws Exception {
        FineDBBusinessTable dbTable = (FineDBBusinessTable) table;
        DataSource dataSource = new TableDBSource(dbTable.getTableName(), dbTable.getConnName());
        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        List<List<BIDetailCell>> dataList = new ArrayList<List<BIDetailCell>>();
        for (Segment segment : segments) {
            List<PrimitiveDetailColumn> columnList = new ArrayList<>();
            int count = segment.getRowCount();
            for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
                String columnName = swiftMetaData.getColumnName(i);
                ColumnKey columnKey = new ColumnKey(columnName);
                columnList.add(segment.getColumn(columnKey).getPrimitiveDetailColumn());
            }
            for (int i = 0; i < count; i++) {
                List<BIDetailCell> cellList = new ArrayList<BIDetailCell>();
                for (int j = 0; j < swiftMetaData.getColumnCount(); j++) {
                    BIDetailCell cell = new SwiftDetailCell(columnList.get(j).get(i));
                    cellList.add(cell);
                }
                dataList.add(cellList);
            }
        }
        BIDetailTableResult realDetailResult = new SwiftRealDetailResult(dataList.iterator(), dataList.size(), swiftMetaData.getColumnCount());
        return realDetailResult;

    }

    @Override
    public List<FineBusinessField> getFieldList(FineBusinessTable table) throws Exception {
        FineDBBusinessTable dbTable = (FineDBBusinessTable) table;
        FineConnection connection = FineConnectionUtils.getConnectionByName(dbTable.getConnName());
        DataSource dataSource = DataSourceFactory.transformTableDBSource(dbTable.getConnName(), dbTable.getTableName(), connection.getSchema(), connection.getConnection());
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        return FieldFactory.transformColumns2Fields(swiftMetaData);

    }

    @Override
    public boolean isAvailable(FineResourceItem item) {
        return false;
    }

    @Override
    public String getName(FineResourceItem item) {
        return null;
    }
}
