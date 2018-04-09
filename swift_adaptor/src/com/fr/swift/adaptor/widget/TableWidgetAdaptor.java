package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BIReportConstant.SORT;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimensionSort;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.finebi.conf.structure.result.table.BIGroupNode;
import com.finebi.conf.utils.FineTableUtils;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.adaptor.struct.node.BIGroupNodeAdaptor;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.adaptor.widget.target.CalTargetParseUtils;
import com.fr.swift.query.adapter.target.cal.TargetInfo;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.Expander;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.info.TableGroupQueryInfo;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.GroupDimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupFormulaTarget;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.NoneSort;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.GroupNodeFactory;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/21
 * 分组表
 */
public class TableWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableWidgetAdaptor.class);

    public static BIGroupNode calculate(TableWidget widget) {
        BIGroupNode resultNode = null;
        SwiftResultSet resultSet;
        try {
            TargetInfo targetInfo = CalTargetParseUtils.parseCalTarget(widget);
            resultSet = QueryRunnerProvider.getInstance().executeQuery(buildQueryInfo(widget, targetInfo.getMetrics()));
            GroupNode groupNode = GroupNodeFactory.createFromSortedList((GroupByResultSet) resultSet, targetInfo.getTargetLength());
            TargetCalculatorUtils.calculate(groupNode, targetInfo.getTargetCalculatorInfoList(), targetInfo.getTargetsForShowList());
            resultNode = new BIGroupNodeAdaptor(groupNode);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return resultNode;
    }

    static QueryInfo buildQueryInfo(TableWidget widget, List<Metric> metrics) throws Exception {
        Cursor cursor = null;
        String queryId = widget.getWidgetId();
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());

        List<Dimension> dimensions = getDimensions(widget);

        GroupTarget[] targets = getTargets(widget);
        Expander expander = null;
        String fieldId = widget.getDimensionList().isEmpty() ? null : widget.getDimensionList().get(0).getFieldId();
        fieldId = fieldId == null ?
                widget.getTargetList().isEmpty() ? null : widget.getTargetList().get(0).getFieldId()
                : fieldId;
        FineBusinessTable fineBusinessTable = FineTableUtils.getTableByFieldId(fieldId);
        DataSource baseDataSource = DataSourceFactory.getDataSource(fineBusinessTable);
        TableGroupQueryInfo tableGroupQueryInfo = new TableGroupQueryInfo(
                dimensions.toArray(new Dimension[dimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                baseDataSource.getSourceKey()
        );
        return new GroupQueryInfo(cursor, queryId, filterInfo,
                new TableGroupQueryInfo[]{tableGroupQueryInfo},
                dimensions.toArray(new Dimension[dimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                targets, expander);
    }

    private static List<Dimension> getDimensions(FineWidget widget) throws Exception {
        List<Dimension> dimensions = new ArrayList<Dimension>();
        List<FineDimension> fineDims = widget.getDimensionList();
        for (int i = 0, size = fineDims.size(); i < size; i++) {
            FineDimension fineDim = fineDims.get(i);
            dimensions.add(toDimension(fineDim, i));
        }
        return dimensions;
    }

    private static GroupTarget[] getTargets(FineWidget widget) throws Exception {
        List<FineTarget> fineTargets = widget.getTargetList();
        fineTargets = fineTargets == null ? new ArrayList<FineTarget>() : fineTargets;
        GroupTarget[] targets = new GroupTarget[fineTargets.size()];
        for (int i = 0, size = fineTargets.size(); i < size; i++) {
            targets[i] = new GroupFormulaTarget(i);
        }
        return targets;
    }

    private static Dimension toDimension(FineDimension fineDim, int index) {
        SourceKey key = new SourceKey(fineDim.getId());
        String columnName = SwiftEncryption.decryptFieldId(fineDim.getFieldId())[1];
        ColumnKey colKey = new ColumnKey(columnName);

        Group group = GroupAdaptor.adaptGroup(fineDim.getGroup());

        FilterInfo filterInfo = null;

        return new GroupDimension(index, key, colKey, group,
                fineDim.getSort() == null ? new AscSort(index) : adaptSort(fineDim.getSort(), index), filterInfo);
    }

    private static Sort adaptSort(FineDimensionSort sort, int index) {
        switch (sort.getType()) {
            case SORT.ASC:
                return new AscSort(index);
            case SORT.DESC:
                return new DescSort(index);
            case SORT.NONE:
                return new NoneSort();
            default:
                return null;
        }
    }
}