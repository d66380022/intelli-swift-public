package com.fr.bi.cal.analyze.cal.sssecret;import com.fr.bi.cal.analyze.cal.result.NodeExpander;/** * Created by Hiram on 2015/1/6. */public class GatherRootDimensionGroup implements IRootDimensionGroup {    private GatherNodeDimensionIterator iterator;    @Override    public void setExpander(NodeExpander expander) {    }    @Override    public NodeDimensionIterator moveToShrinkStartValue(Object[] value) {        return null;    }    @Override    public NodeDimensionIterator moveLast() {        return null;    }    @Override    public NodeDimensionIterator moveNext() {        return iterator;    }    @Override    public NodeDimensionIterator moveToStart() {        return null;    }    @Override    public void clearCache() {    }    private class GatherNodeDimensionIterator implements NodeDimensionIterator {        NodeDimensionIterator[] iterators = null;        @Override        public void moveNext() {        }        @Override        public GroupConnectionValue next() {            return null;        }        @Override        public void PageEnd() {        }        @Override        public boolean hasPrevious() {            return false;        }        @Override        public boolean hasNext() {            return false;        }        @Override        public int getPageIndex() {            return 0;        }    }}