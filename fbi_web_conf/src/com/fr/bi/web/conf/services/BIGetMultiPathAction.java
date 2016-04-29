/**
 *
 */
package com.fr.bi.web.conf.services;

import com.fr.bi.conf.base.relation.relation.IRelationContainer;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.fr.bi.stable.exception.BITableUnreachableException;
import com.fr.bi.stable.relation.BISimpleRelation;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author Guy
 */
public class BIGetMultiPathAction extends AbstractBIConfigureAction {

    /* (non-Javadoc)
     * @see com.fr.web.core.AcceptCMD#getCMD()
     */
    @Override
    public String getCMD() {
        return "get_multi_path";
    }

    /**
     * ����
     *
     * @param req ����
     * @param res ����
     * @throws Exception
     */
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject multiPathJo = getMultiPath(userId);
        JSONObject jo = new JSONObject();
        JSONObject translations = BIConfigureManagerCenter.getAliasManager().getTransManager(userId).createJSON();
        jo.put("cubeEnd", BIConfigureManagerCenter.getLogManager().getCubeEnd(userId));
        jo.put("translations", translations);
        jo.put("relations", multiPathJo.optJSONArray("relations"));
        jo.put("disabledRelations", multiPathJo.optJSONArray("disabledRelations"));
        jo.put("availableRelations", multiPathJo.optJSONArray("availableRelations"));
        jo.put("noneRelations", multiPathJo.optJSONArray("noneRelations"));
        WebUtils.printAsJSON(res, jo);
    }


    private Set<Table> getRelatedTables(long userId) {
        Set<Table> relatedTables = new HashSet<Table>();
        Iterator<Map.Entry<Table, IRelationContainer>> primaryIt = BIConfigureManagerCenter.getTableRelationManager().getAllTable2PrimaryRelation(userId).entrySet().iterator();
        while (primaryIt.hasNext()) {
            Map.Entry entry = primaryIt.next();
            relatedTables.add((Table) entry.getKey());
        }

        Iterator<Map.Entry<Table, IRelationContainer>> foreignIt = BIConfigureManagerCenter.getTableRelationManager().getAllTable2ForeignRelation(userId).entrySet().iterator();
        while (foreignIt.hasNext()) {
            Map.Entry entry = foreignIt.next();
            relatedTables.add((Table) entry.getKey());
        }
        return relatedTables;
    }

    private Set<BITableRelationPath> getAllPath(long userId, Table foreignTable, Table primaryTable) throws BITableUnreachableException, BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return BIConfigureManagerCenter.getTableRelationManager().getAllPath(userId, foreignTable, primaryTable);
    }

    private Set<BITableRelationPath> getAllAvailablePath(long userId, Table foreignTable, Table primaryTable) throws BITableUnreachableException,
            BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return BIConfigureManagerCenter.getTableRelationManager().getAllAvailablePath(userId, foreignTable, primaryTable);
    }


    private Set<BITableRelationPath> getDisabledPath(long userId, Table foreignTable, Table primaryTable) throws BITableUnreachableException, BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        Set<BITableRelationPath> allPath = getAllPath(userId, foreignTable, primaryTable);
        Set<BITableRelationPath> allAvailablePath = getAllAvailablePath(userId, foreignTable, primaryTable);
        allPath.removeAll(allAvailablePath);
        return allPath;
    }

    private JSONObject getMultiPath(long userId) throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray multiJa = new JSONArray();
        Set<BITableRelationPath> availableMultiSet = new HashSet<BITableRelationPath>();
        Set<BITableRelationPath> disabledMultiSet = new HashSet<BITableRelationPath>();
        Set<BITableRelationPath> noneMultiSet = new HashSet<BITableRelationPath>();
        Set<Table> relatedTables = getRelatedTables(userId);
        Iterator it = relatedTables.iterator();
        while (it.hasNext()) {
            Table foreignTable = (Table) it.next();
            Iterator primaryTableIt = relatedTables.iterator();
            while (primaryTableIt.hasNext()) {
                Table primaryTable = (Table) primaryTableIt.next();
                Set<BITableRelationPath> allPath = getAllPath(userId, foreignTable, primaryTable);
                Set<BITableRelationPath> multiPathItem = new HashSet<BITableRelationPath>();
                if (allPath.size() > 1) {
                    multiPathItem.addAll(allPath);
                    Set<BITableRelationPath> allAvailablePath = getAllAvailablePath(userId, foreignTable, primaryTable);
                    availableMultiSet.addAll(allAvailablePath);
                }
                Set<BITableRelationPath> disablePathSet = getDisabledPath(userId, foreignTable, primaryTable);
                Iterator noneIt = disablePathSet.iterator();
                while (noneIt.hasNext()) {
                    BITableRelationPath nonePath = (BITableRelationPath) noneIt.next();
                    if (!allPath.contains(nonePath)) {
                        noneMultiSet.add(nonePath);
                    }
                }
                multiPathItem.addAll(disablePathSet);
                disabledMultiSet.addAll(disablePathSet);
                if (!multiPathItem.isEmpty()) {
                    multiJa.put(path2relations(multiPathItem));
                }

            }
        }
        jo.put("relations", multiJa);
        jo.put("disabledRelations", path2relations(disabledMultiSet));
        jo.put("availableRelations", path2relations(availableMultiSet));
        jo.put("noneRelations", path2relations(noneMultiSet));
        return jo;
    }


    private JSONArray path2relations(Set<BITableRelationPath> multiPathSet) throws Exception {
        JSONArray multiPathJa = new JSONArray();
        Iterator it = multiPathSet.iterator();
        while (it.hasNext()) {
            JSONArray multiRelationJa = new JSONArray();
            BITableRelationPath path = (BITableRelationPath) it.next();
            List relationList = path.getAllRelations();
            for (int i = 0; i < relationList.size(); i++) {
                BISimpleRelation relation = ((BITableRelation) relationList.get(i)).getSimpleRelation();
                multiRelationJa.put(relation.createJSON());
            }
            multiPathJa.put(multiRelationJa);
        }
        return multiPathJa;
    }


}
