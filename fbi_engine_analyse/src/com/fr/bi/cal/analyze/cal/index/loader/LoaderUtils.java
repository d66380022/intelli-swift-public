package com.fr.bi.cal.analyze.cal.index.loader;import com.fr.bi.cal.analyze.cal.result.NodeAllExpander;import com.fr.bi.cal.analyze.report.report.widget.BISummaryWidget;import com.fr.bi.field.target.calculator.cal.CalCalculator;import com.fr.bi.field.target.key.cal.BICalculatorTargetKey;import com.fr.bi.field.target.target.BISummaryTarget;import com.fr.bi.cal.analyze.session.BISession;import com.fr.bi.conf.report.widget.field.dimension.BIDimension;import com.fr.bi.stable.report.result.BITargetKey;import com.fr.bi.stable.report.result.DimensionCalculator;import com.fr.bi.stable.report.result.TargetCalculator;import java.util.ArrayList;import java.util.List;/** * Created by Hiram on 2015/1/8. */public class LoaderUtils {    static void fillRowDimension(BISummaryWidget widget, DimensionCalculator[] row, BIDimension[] rowDimension, int rowLength, BISummaryTarget bdt) {        for (int j = 0; j < rowLength; j++) {            BIDimension dimension = rowDimension[j];            if (dimension != null) {                row[j] = widget.createDimCalculator(dimension, bdt);            }        }    }    public static void classifyTarget(BISummaryTarget[] usedTargets, List calculateTargets, List noneCalculateTargets, BISession session) {        for (int i = 0; i < usedTargets.length; i++) {            BISummaryTarget target = usedTargets[i];            if (target == null) {                continue;            }            TargetCalculator calculator = target.createSummaryCalculator();            if (calculator instanceof CalCalculator) {                calculateTargets.add(calculator);            } else {                noneCalculateTargets.add(calculator);            }        }    }    public static void setAllExpander(List<MergerInfo> mergerInfoList) {        for (int i = 0; i < mergerInfoList.size(); i++) {            mergerInfoList.get(i).getRootDimensionGroup().setExpander(new NodeAllExpander());        }    }    public static List<BICalculatorTargetKey> getCalculatorTargets(BISummaryTarget[] usedTargets, BISession session) {        List<BICalculatorTargetKey> list = new ArrayList<BICalculatorTargetKey>();        for (int i = 0; i < usedTargets.length; i++) {            BITargetKey key = usedTargets[i].createSummaryCalculator().createTargetKey();            if (key instanceof BICalculatorTargetKey) {                list.add((BICalculatorTargetKey) key);            }        }        return list;    }}