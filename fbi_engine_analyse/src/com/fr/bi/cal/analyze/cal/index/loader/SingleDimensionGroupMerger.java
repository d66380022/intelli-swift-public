package com.fr.bi.cal.analyze.cal.index.loader;import com.fr.bi.cal.analyze.cal.sssecret.NoneDimensionGroup;import java.util.ArrayList;import java.util.List;/** * Created by Hiram on 2015/1/7. */public class SingleDimensionGroupMerger {    List<NoneDimensionGroup> rootList = new ArrayList<NoneDimensionGroup>();    public void addRoot(NoneDimensionGroup root) {        rootList.add(root);    }}