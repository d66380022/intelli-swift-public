package com.fr.bi.field.filtervalue.string.nonevaluefilter;

import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.utils.TableIndexUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.stable.StringUtils;

import java.util.List;


public class StringNotNullFilterValue extends StringNoneValueFilterValue {
    private static final long serialVersionUID = -3139710272568206877L;
    private static String XML_TAG = "StringNotNullFilterValue";


    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, Table target, ICubeDataLoader loader, long userId) {
        ICubeTableService ti = loader.getTableIndex(dimension.getField().getTableBelongTo());
        List<BITableSourceRelation> relations = dimension.getRelationList();
        ICubeColumnIndexReader getter = ti.loadGroup(dimension.createKey(), relations);
        GroupValueIndex res = getter.getGroupIndex(new String[]{StringUtils.EMPTY})[0];

        GroupValueIndex nullres;
        if (!relations.isEmpty()) {
            ICubeTableService eti = loader.getTableIndex(target);
            nullres = TableIndexUtils.createLinkNullGVI(ti, eti, relations);
        } else {
            nullres = ti.getNullGroupValueIndex(dimension.createKey());
        }

        if (res == null) {
            res = nullres;
        } else {
            res = res.OR(nullres);
        }
        if (res == null) {
            return loader.getTableIndex(target).getAllShowIndex();
        } else {
            return res.NOT(loader.getTableIndex(target).getRowCount()).AND(loader.getTableIndex(dimension.getField().getTableBelongTo()).getAllShowIndex());
        }
    }
    
    @Override
	public boolean isMatchValue(String value){
    	return StringUtils.isNotEmpty(value);
    }
}