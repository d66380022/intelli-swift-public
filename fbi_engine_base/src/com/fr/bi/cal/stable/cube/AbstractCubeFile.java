package com.fr.bi.cal.stable.cube;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.index.BITableCubeFile;
import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.file.ColumnFile;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public abstract class AbstractCubeFile implements BITableCubeFile{
    protected ColumnFiles columns;

    @Override
    @SuppressWarnings("unchecked")
    public void addDataValue(BIDataValue v) {
        columns.getColumnFile(v.getCol()).addDataValue(v.getRow(), v.getValue());
    }


    @Override
    public Long getGroupCount(BIKey key) {
        ColumnFile<?> cf = getColumnFile(key.getKey());
        if (cf != null) {
            return cf.getGroupCount(key);
        }
        return 0L;
    }

    public ColumnFile<?> getColumnFile(BIKey key) {
        return getColumnFile(key.getKey());
    }

    protected ColumnFile<?> getColumnFile(String fieldName) {
        ColumnFiles columns = initColumns();
        return columns.getColumnFile(fieldName);
    }

    protected abstract ColumnFiles initColumns();



    @Override
    public DetailGetter createDetailGetter(BIKey key, SingleUserNIOReadManager manager) {
        return getColumnFile(key).createDetailGetter(manager);
    }

    @Override
    public GroupValueIndex getIndexByRow(BIKey key, int row, SingleUserNIOReadManager manager) {
        ColumnFile cf = getColumnFile(key);
        return cf.getIndexByRow(row, manager);
    }

}