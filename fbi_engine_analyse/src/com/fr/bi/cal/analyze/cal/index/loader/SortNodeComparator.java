package com.fr.bi.cal.analyze.cal.index.loader;import com.fr.general.ComparatorUtils;import java.util.Comparator;/** * Created by Hiram on 2015/1/15. */public class SortNodeComparator implements Comparator {    @Override    public int compare(Object o1, Object o2) {        return ComparatorUtils.compare(o1, o2);    }}