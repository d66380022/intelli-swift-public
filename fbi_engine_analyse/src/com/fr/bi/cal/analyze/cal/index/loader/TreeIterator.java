package com.fr.bi.cal.analyze.cal.index.loader;import com.fr.bi.cal.analyze.cal.result.Node;import com.fr.bi.stable.report.result.LightNode;import java.util.ArrayList;import java.util.List;/** * Created by Hiram on 2015/1/27. */public class TreeIterator {    private final static LightNode BACKSPACE = new Node(null, null);    private LightNode root;    private LightNode currentNode;    private List<Integer> index = new ArrayList<Integer>();    public TreeIterator(LightNode tree) {        this.root = tree;        this.currentNode = root;//		addNewDeepIndex();    }    private void addNewDeepIndex() {        index.add(0);    }    public LightNode next() {        if (currentNode == null) {            return null;        }        if (currentNode.getChildLength() != 0) {            addNewDeepIndex();            LightNode next = currentNode.getChild(0);            currentNode = next;            return next;        } else {            LightNode next = getSiblingNode();            while (next == BACKSPACE) {                next = getSiblingNode();            }            return next;        }    }    public int getCurrentDeep() {        return index.size() - 1;    }    private boolean isRoot(LightNode node) {        return node == root;    }    private LightNode getSiblingNode() {        LightNode parent = currentNode.getParent();        if (parent == null) {            currentNode = null;            return null;        }        if (parent.getChildLength() > getCurrentIndex() + 1) {            int nextIndex = getCurrentIndex() + 1;            LightNode next = parent.getChild(nextIndex);            currentNode = next;            setLastIndex(nextIndex);            return next;        } else {            currentNode = parent;            removeLastIndex();            return BACKSPACE;        }    }    private void setLastIndex(int nextIndex) {        index.set(index.size() - 1, nextIndex);    }    private void removeLastIndex() {        index.remove(index.size() - 1);    }    private int getCurrentIndex() {        return index.get(index.size() - 1);    }}