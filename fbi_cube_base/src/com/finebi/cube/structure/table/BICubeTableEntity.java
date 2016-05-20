package com.finebi.cube.structure.table;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.*;
import com.finebi.cube.structure.table.property.BICubeTableProperty;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.*;

/**
 * This class created on 2016/3/3.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableEntity implements ICubeTableEntityService {

    protected ITableKey tableKey;
    protected ICubeResourceRetrievalService resourceRetrievalService;
    protected ICubeTableColumnManagerService columnManager;
    protected ICubeRelationManagerService relationManager;
    protected ICubeResourceLocation currentLocation;
    protected ICubeTablePropertyService tableProperty;
    protected ICubeResourceDiscovery discovery;
    private ICubeIntegerWriter removedLineWriter;

    public BICubeTableEntity(ITableKey tableKey, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        try {
            this.tableKey = tableKey;
            this.resourceRetrievalService = resourceRetrievalService;
            this.discovery = discovery;
            currentLocation = resourceRetrievalService.retrieveResource(tableKey);
            tableProperty = new BICubeTableProperty(currentLocation, discovery);
            if (tableProperty.isPropertyExist()) {
                columnManager = new BICubeTableColumnManager(tableKey, resourceRetrievalService, getAllFields(), discovery);
            }

            relationManager = new BICubeTableRelationEntityManager(this.resourceRetrievalService, this.tableKey, discovery);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }


    private void flushProperty() {
        if (tableProperty != null) {
            tableProperty.clear();
        }
        tableProperty = new BICubeTableProperty(currentLocation, discovery);

    }

    @Override
    public void recordTableStructure(List<BICubeFieldSource> fields) {
        /**
         * tableProperty缓存了上一次的map文件对象。
         * 详见BICubeTable单元测试中的testPropertyExceptionData测试用例
         */
        flushProperty();
        tableProperty.recordTableStructure(fields);
        columnManager = new BICubeTableColumnManager(tableKey, resourceRetrievalService, getAllFields(), discovery);
    }

    @Override
    public void recordTableGenerateVersion(int version) {
        tableProperty.recordTableGenerateVersion(version);
    }

    @Override
    public void recordRowCount(long rowCount) {
        tableProperty.recordRowCount(rowCount);

    }

    @Override
    public void recordLastTime() {
        tableProperty.recordLastTime();
    }

    @Override
    public void recordRemovedLine(TreeSet<Integer> removedLine) {
        Iterator<Integer> it = removedLine.iterator();
        int row = 0;
        while (it.hasNext()) {
            removedLineWriter.recordSpecificPositionValue(row++, it.next());
        }
    }

    @Override
    public void recordParentsTable(List<ITableKey> parents) {
        tableProperty.recordParentsTable(parents);
    }

    @Override
    public List<ITableKey> getParentsTable() {
        return tableProperty.getParentsTable();
    }

    @Override
    public void addDataValue(BIDataValue originalDataValue) throws BICubeColumnAbsentException {
        int columnIndex = originalDataValue.getCol();
        int rowNumber = originalDataValue.getRow();
        Object value = originalDataValue.getValue();
        BICubeFieldSource field = getAllFields().get(columnIndex);
        ICubeColumnEntityService columnService = columnManager.getColumn(BIColumnKey.covertColumnKey(field));
        columnService.addOriginalDataValue(rowNumber, value);
    }

    private List<BICubeFieldSource> getAllFields() {
        return tableProperty.getFieldInfo();
    }

    @Override
    public boolean checkRelationVersion(List<BITableSourceRelation> relations, int relation_version) {
        return false;
    }

    @Override
    public boolean checkRelationVersion(BIKey key, List<BITableSourceRelation> relations, int relation_version) {
        return false;
    }

    @Override
    public boolean checkCubeVersion() {
        return false;
    }

    @Override
    public void copyDetailValue(ICubeTableEntityService cube, long rowCount) {
    }

    @Override
    public int getTableVersion() {
        return tableProperty.getTableVersion();
    }

    @Override
    public List<BICubeFieldSource> getFieldInfo() {
        return tableProperty.getFieldInfo();
    }

    public Set<BIColumnKey> getCubeColumnInfo() {
        return columnManager.getCubeColumnInfo();
    }

    @Override
    public BICubeFieldSource getSpecificColumn(String fieldName) throws BICubeColumnAbsentException {
        Iterator<BICubeFieldSource> fieldIterator = getFieldInfo().iterator();
        while (fieldIterator.hasNext()) {
            BICubeFieldSource field = fieldIterator.next();
            if (field.getFieldName().equals(fieldName)) {
                return field;
            }
        }
        throw new BICubeColumnAbsentException();
    }

    @Override
    public int getRowCount() {
        return tableProperty.getRowCount();
    }

    @Override
    public Date getCubeLastTime() {
        return tableProperty.getCubeLastTime();
    }

    @Override
    public ICubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        return columnManager.getColumn(columnKey);
    }

    @Override
    public ICubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException {
        BICubeFieldSource field = getSpecificColumn(columnName);
        return getColumnDataGetter(convert(field));
    }

    public static BIColumnKey convert(BICubeFieldSource field) {
        return BIColumnKey.covertColumnKey(field);
    }

    @Override
    public void clear() {
        if (columnManager != null) {
            columnManager.clear();
        }
        relationManager.clear();
        tableProperty.clear();
    }

    @Override
    public ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        return relationManager.getRelationService(path);
    }

    @Override
    public void recordFieldNamesFromParent(Set<String> fieldNames) {
        tableProperty.recordFieldNamesFromParent(fieldNames);
    }

    @Override
    public Set<String> getFieldNamesFromParent() {
        return tableProperty.getFieldNamesFromParent();
    }

    @Override
    public boolean tableDataAvailable() {
        return tableProperty.isPropertyExist();
    }

    @Override
    public boolean isRowCountAvailable() {
        return tableProperty.isRowCountAvailable();
    }
}
