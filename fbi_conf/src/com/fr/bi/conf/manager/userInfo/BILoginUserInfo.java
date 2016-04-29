package com.fr.bi.conf.manager.userInfo;

import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.FinalInt;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.BICoreService;
import com.fr.bi.common.BICoreWrapper;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.exception.BIAmountLimitUnmetException;
import com.fr.bi.stable.connection.ConnectionRowGetter;
import com.fr.bi.stable.connection.DirectTableConnectionFactory;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.exception.BITableUnreachableException;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import javax.activation.UnsupportedDataTypeException;
import java.util.List;


public class BILoginUserInfo implements XMLable, JSONTransform, BICoreService {
    public static final String XML_TAG = "BILoginUserInfo";
    /**
     *
     */
    private static final long serialVersionUID = -8906322997053607202L;
    private String user_name_column;
    private Table key;
    private transient BIKey columnIndex;

    @Override
    public BICore fetchObjectCore() {
        try {
            return new BILoginUserInfoCore().fetchObjectCore();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return BIBasicCore.EMPTY_CORE;
    }

    public Object getFieldValue(String userName, BIField ck, ICubeDataLoader loader) {
        try {
            BITableRelationPath firstPath = BIConfigureManagerCenter.getTableRelationManager().getFirstPath(loader.getUserId(), ck.getTableBelongTo(), getTableKey());
            List<BITableRelation> relations;
            relations = firstPath.getAllRelations();
            BIKey userNameIndex = getUserNameColumnIndex(loader);
            if (userNameIndex != null) {
                final ConnectionRowGetter getter = DirectTableConnectionFactory.createConnectionRow(BIConfUtils.convert2TableSourceRelation(relations, new BIUser(loader.getUserId())), loader);
                ICubeTableService ti = loader.getTableIndex(getTableKey());
                GroupValueIndex gvi = ti.getIndexes(userNameIndex, new String[]{userName})[0];
                final FinalInt o = new FinalInt();
                if (gvi != null) {
                    //只取一个值
                    gvi.BrokenableTraversal(new BrokenTraversalAction() {
                        @Override
                        public boolean actionPerformed(int rowIndex) {
                            o.i = getter.getConnectedRow(rowIndex);
                            return true;
                        }
                    });
                    if (o.i != -1) {
                        ICubeTableService cti = loader.getTableIndex(ck);
                        return cti.getRow(cti.getColumnIndex(ck.getFieldName()), o.i);
                    }
                }
            }
        } catch (BITableUnreachableException e) {
            //TODO 这个异常不应该就这么捕获什么都不做。
        }
        return null;
    }

    /**
     * 计算hash值
     *
     * @return hash值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime
                * result
                + ((user_name_column == null) ? 0 : user_name_column.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BILoginUserInfo other = (BILoginUserInfo) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(key, other.key)) {
            return false;
        }
        if (user_name_column == null) {
            if (other.user_name_column != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(user_name_column, other.user_name_column)) {
            return false;
        }
        return true;
    }

    private BIKey getUserNameColumnIndex(ICubeDataLoader loader) {
        if (columnIndex == null) {
            columnIndex = loader.getTableIndex(getTableKey()).getColumnIndex(user_name_column);
        }
        return columnIndex;
    }

    public Table getTableKey() {
        return key;
    }

    public void setTable(Table key) {
        this.key = key;
    }

    public String getUserNameColumn() {
        return this.user_name_column;
    }

    public void setUserNameColumn(String user_name_column) {
        this.user_name_column = user_name_column;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.user_name_column = reader.getAttrAsString("user_name_column", null);
            String id = reader.getAttrAsString("id", null);
            if (!StringUtils.isEmpty(id)) {
                this.key = new BITable(id);
            }
        }

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("user_name_column", user_name_column);
        if (key != null) {
            writer.attr("id", key.getID().getIdentityValue());
        }
        writer.end();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if (key != null) {
            JSONObject j = new JSONObject();
            j.put("id", key.getID());
            jo.put("table", j);

        }
        if (this.user_name_column != null) {
            jo.put("field_name", user_name_column);
        }
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        this.user_name_column = jo.optString("field_name", null);
        if (jo.optJSONObject("table") != null) {
            this.key = new BITable(jo.optJSONObject("table").optString("id"));
        }
    }

    public class BILoginUserInfoCore extends BICoreWrapper {
        public BILoginUserInfoCore(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
            super(key);
        }
    }
}