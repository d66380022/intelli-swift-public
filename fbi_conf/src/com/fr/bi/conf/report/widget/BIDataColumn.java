package com.fr.bi.conf.report.widget;

import com.fr.bi.conf.base.pack.data.BIBusinessField;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.bi.stable.data.BIField;

import java.io.Serializable;

/**
 * 用于分析的原始字段， _src, 业务包字段的子类
 * Created by GUY on 2015/3/30.
 */
public class BIDataColumn extends BIBusinessField implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1869519458089727759L;


    public BIDataColumn() {
        super();
    }

    public BIDataColumn(BIBasicField fieldKey) {
        super(fieldKey.getTableBelongTo().getID().getIdentityValue(), fieldKey.getFieldName(), fieldKey.getFieldType(), fieldKey.getFieldSize());

    }


    public BIField createColumnKey() {
        return new BIField(this);
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof BIDataColumn

                && super.equals(obj);
    }


}