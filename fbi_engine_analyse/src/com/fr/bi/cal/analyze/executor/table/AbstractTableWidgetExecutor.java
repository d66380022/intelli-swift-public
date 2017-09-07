package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.base.Style;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCache;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCacheKey;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetDataCacheManager;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;
import com.fr.bi.cal.analyze.cal.sssecret.PageIteratorGroup;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.export.utils.GeneratorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;

public abstract class AbstractTableWidgetExecutor<T> extends BIAbstractExecutor<T> {

    protected BISummaryTarget[] usedSumTarget;

    protected BISummaryTarget[] allSumTarget;

    protected BIDimension[] allDimensions;

    protected TableWidget widget;

    protected AbstractTableWidgetExecutor(TableWidget widget, Paging paging, BISession session) {

        super(widget, paging, session);
        this.widget = widget;
        usedSumTarget = widget.getViewTargets();
        allSumTarget = widget.getTargets();
        allDimensions = widget.getDimensions();
    }

    public BISummaryTarget[] createTarget4Calculate() {

        ArrayList<BITarget> list = new ArrayList<BITarget>();
        Collections.addAll(list, usedSumTarget);
        if (widget.getTargetSort() != null) {
            String name = widget.getTargetSort().getName();
            boolean inUsedSumTarget = false;
            for (BISummaryTarget anUsedSumTarget : usedSumTarget) {
                if (ComparatorUtils.equals(anUsedSumTarget.getValue(), name)) {
                    inUsedSumTarget = true;
                }
            }
            if (!inUsedSumTarget) {
                for (BISummaryTarget anAllSumTarget : allSumTarget) {
                    if (ComparatorUtils.equals(anAllSumTarget.getValue(), name)) {
                        list.add(anAllSumTarget);
                    }
                }
            }
        }
        for (String key : widget.getTargetFilterMap().keySet()) {
            boolean inUsedSumTarget = false;
            for (BISummaryTarget anUsedSumTarget : usedSumTarget) {
                if (ComparatorUtils.equals(anUsedSumTarget.getValue(), key)) {
                    inUsedSumTarget = true;
                }
            }
            if (!inUsedSumTarget) {
                for (BISummaryTarget anAllSumTarget : allSumTarget) {
                    if (ComparatorUtils.equals(anAllSumTarget.getValue(), key)) {
                        list.add(anAllSumTarget);
                    }
                }
            }

        }
        return list.toArray(new BISummaryTarget[list.size()]);
    }

    /**
     * 获取点击节点代表的gvi
     * 点击的节点可能是,分组表,交叉表,复杂表中的点,之前是放在tableWidget里面进行处理,但是这样就要处理不同的executor,这样的结构
     * 并不太好,所以现在放到各自的executor里面进行处理
     *
     * @param clicked
     * @param targetKey
     * @return
     */
    abstract public GroupValueIndex getClickGvi(Map<String, JSONArray> clicked, BusinessTable targetKey);


    /**
     * 获取点击的指标
     *
     * @param clicked
     * @return
     */
    protected String getClickTarget(Map<String, JSONArray> clicked) {

        for (String target : clicked.keySet()) {
            try {
                widget.getBITargetByID(target);
                return target;
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 获取目标的gvi
     *
     * @param target
     * @param n
     * @return
     */
    protected GroupValueIndex getTargetIndex(String target, Node n) {

        if (n != null && target != null) {
            for (BISummaryTarget t : allSumTarget) {
                if (ComparatorUtils.equals(t.getName(), target)) {
                    return n.getTargetIndex(t.createTargetGettingKey());
                }
            }
        }
        return null;
    }

    protected GroupValueIndex getLinkNodeFilter(Node n, String target, java.util.List<Object> data) {

        if (n != null) {
            if (data.size() == 0) {
                return getTargetIndex(target, n);
            }
            Node parent = getClickNode(n, data);
            return getTargetIndex(target, parent);
        }
        return null;
    }

    protected Node getClickNode(Node n, List<Object> data) {

        if (n != null) {
            Node parent = n;
            for (Object cv : data) {
                Node child = null;
                for (Node pn : parent.getChilds()) {
                    if (pn.getShowValue().equals(cv)) {
                        child = pn;
                        break;
                    }
                }
                parent = child;
            }
            return parent;
        }
        return null;
    }

    /**
     * 把以前放在BIEngineExecutor中的接口移到这边来,因为只可能需要表格才可能停在某一行
     *
     * @param stopRow
     * @param dimensions
     * @return
     * @throws Exception
     */
    protected Node getStopOnRowNode(Object[] stopRow, BIDimension[] dimensions) throws Exception {

        int rowLength = dimensions.length;
        int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;
        if (columnLen == 0) {
            return null;
        }
        int calPage = paging.getOperator();
        CubeIndexLoader cubeIndexLoader = CubeIndexLoader.getInstance(session.getUserId());
        Node n = cubeIndexLoader.getStopWhenGetRowNode(stopRow, widget, createTarget4Calculate(), dimensions,
                                                       allDimensions, allSumTarget, calPage, session, CrossExpander.ALL_EXPANDER.getYExpander());
        return n;
    }

    /**
     * dim[]中是否包含did
     *
     * @param did
     * @param dimensions
     * @return
     */
    private boolean dimensionsContain(String did, BIDimension[] dimensions) {

        if (dimensions == null || dimensions.length == 0) {
            return false;
        }
        for (BIDimension d : dimensions) {
            if (d.getId().equals(did)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是点击区域
     *
     * @param dids
     * @param dimensions
     * @return
     */
    protected boolean isClickRegion(List<String> dids, BIDimension[] dimensions) {

        if (dids == null || dids.size() == 0) {
            return false;
        }
        for (String s : dids) {
            if (!dimensionsContain(s, dimensions)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是点击区域 复杂表
     *
     * @param rowsDid
     * @param rowDimension
     * @param colsDid
     * @param colDimension
     * @return
     */
    protected boolean isClickRegion(List<String> rowsDid, BIDimension[] rowDimension, List<String> colsDid, BIDimension[] colDimension) {

        if (rowsDid == null || colsDid == null) {
            return false;
        }
        for (String s : rowsDid) {
            if (!dimensionsContain(s, rowDimension)) {
                return false;
            }
        }
        for (String s : colsDid) {
            if (!dimensionsContain(s, colDimension)) {
                return false;
            }
        }
        return true;
    }


    protected int[] getStartIndex(Operator op, NodeDimensionIterator iterator, int size){
        if (iterator == null){
            int[] indexes =  new int[size];
            Arrays.fill(indexes, -1);
            return indexes;
        }
        NodeDimensionIterator clonedIter = iterator.createClonedIterator();
        op.moveIterator(clonedIter);
        return clonedIter.getStartIndex();
    }

    protected WidgetCache<JSONObject> getWidgetCache(WidgetCacheKey key){
        if (isUseWidgetDataCache()){
            return WidgetDataCacheManager.getInstance().get(key);
        }
        return null;
    }

    protected void updateCache(WidgetCacheKey key, WidgetCache widgetCache){
        WidgetDataCacheManager.getInstance().put(key, widgetCache);
    }

    //isRealData,并且配置文件开关开启的情况才计算缓存
    protected boolean isUseWidgetDataCache(){
        return widget.isRealData() && PerformancePlugManager.getInstance().isExtremeConcurrency();
    }

    protected PageIteratorGroup getPageIterator() {
        return CubeIndexLoader.getInstance(session.getUserId()).needCreateNewIterator(paging.getOperator()) ? null : session.getPageIteratorGroup(true ,widget.getWidgetId());
    }

    /**
     * 获取点击值
     *
     * @param clicked
     * @param target
     * @param isHor   是否是
     * @return
     * @throws Exception
     */
    protected List<Object> getLinkRowData(Map<String, JSONArray> clicked, String target, boolean isHor) throws Exception {

        List r = new ArrayList<Object>();
        try {
            if (clicked != null) {
                JSONArray keyJson = clicked.get(target);
                Object rowData[] = new Object[keyJson.length()];
                int j = 0;
                if (isHor) {
                    for (int i = 0; i < keyJson.length(); i++) {
                        // 每个维度根据指标来选出点击的值得gvi
                        JSONObject object = keyJson.getJSONObject(i);
                        String click = object.getJSONArray("value").getString(0);
                        r.add(click);
                    }
                } else {
                    for (int i = keyJson.length() - 1; i >= 0; i--) {
                        // 每个维度根据指标来选出点击的值得gvi
                        JSONObject object = keyJson.getJSONObject(i);
                        String click = object.getJSONArray("value").getString(0);
                        String did = object.getString("dId");
                        rowData[j++] = click;
                        r.add(click);
                    }
                }

            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).info(e.getMessage(), e);
        }
        return r;
    }

    /**
     * 去掉没有用到的维度，现在联动以单个维度为联动的时候不跟上一个维度有关系。
     * 所以之前的维度是没有用的，只有再click里面的维度才是有用的。
     *
     * @param clicked
     * @param target
     * @return
     */
    protected BIDimension[] getUserDimension(Map<String, JSONArray> clicked, String target) {

        List<BIDimension> ret = new ArrayList<BIDimension>();
        try {

            JSONArray keyJson = clicked.get(target);
            for (int i = keyJson.length() - 1; i >= 0; i--) {
                // 每个维度根据指标来选出点击的值得gvi
                JSONObject object = keyJson.getJSONObject(i);
                String did = object.getString("dId");
                ret.add(widget.getDimensionBydId(did));
            }
        } catch (Exception e) {

        }
        return ret.toArray(new BIDimension[ret.size()]);
    }

    /**
     * 获取点击的行和列的值
     *
     * @param clicked
     * @param target
     * @param row
     * @param col
     * @param rowsId
     * @param colsId
     */
    protected void getLinkRowAndColData(Map<String, JSONArray> clicked, String target, List row, List col, List<String> rowsId, List<String> colsId) {

        try {
            if (clicked != null) {
                Map<String, String> m = new HashMap<String, String>();
                JSONArray keyJson = clicked.get(target);
                for (int i = keyJson.length() - 1; i >= 0; i--) {
                    // 每个维度根据指标来选出点击的值得gvi
                    JSONObject object = keyJson.getJSONObject(i);
                    String click = object.getJSONArray("value").getString(0);
                    String did = object.getString("dId");
                    m.put(did, click);
                }
                // 传过来的顺序不一定就是正确的为确保万一还是采用这样的做法.
                for (BIDimension dimension : widget.getViewDimensions()) {
                    String did = dimension.getId();
                    String c = m.get(did);
                    // 点击某一个汇总值得时候不一定有
                    if (c != null) {
                        row.add(c);
                        if (rowsId != null) {
                            rowsId.add(did);
                        }
                    }
                }
                for (BIDimension dimension : widget.getViewTopDimensions()) {
                    String did = dimension.getId();
                    String c = m.get(did);
                    if (c != null) {
                        col.add(c);
                        if (colsId != null) {
                            colsId.add(did);
                        }
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).info(e.getMessage(), e);
        }
    }
}