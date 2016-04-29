BranchTreeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BranchTreeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-branch-tree bi-mvc-layout"
        })
    },

    _init: function () {
        BranchTreeView.superclass._init.apply(this, arguments);
    },

    _createBranchTree: function () {
        var tree = BI.createWidget({
            type: "bi.branch_tree",
            items: [{
                el: {
                    text: "且",
                    value: "且1",
                    cls: "layout-bg7"
                },
                children: [{
                    type: "bi.label",
                    height: 30,
                    textAlign: "left",
                    text: "这里是一段文字1",
                    value: "这里是一段文字1"
                }, {
                    el: {
                        text: "或",
                        value: "或2",
                        cls: "layout-bg7"
                    },
                    children: [{
                        type: "bi.label",
                        height: 30,
                        textAlign: "left",
                        text: "这里是一段文字1435",
                        value: "这里是一段文字1435"
                    }, {
                        type: "bi.label",
                        height: 30,
                        textAlign: "left",
                        text: "这里是一段文字1xx",
                        value: "这里是一段文字1xx"
                    }, {
                        el: {
                            text: "且",
                            value: "且3",
                            cls: "layout-bg7"
                        },
                        children: [{
                            type: "bi.label",
                            height: 30,
                            textAlign: "left",
                            text: "可以理解为一个条件",
                            value: "可以理解为一个条件"
                        }, {
                            type: "bi.label",
                            height: 30,
                            textAlign: "left",
                            text: "可以理解为一个条件v",
                            value: "可以理解为一个条件v"
                        }]
                    }]
                }, {
                    type: "bi.label",
                    height: 30,
                    textAlign: "left",
                    text: "这里是一段文字1xa",
                    value: "这里是一段文字1xa"
                }]
            }]
        });
        return tree;
    },

    _createBranchMapTree: function () {
        var tree = BI.createWidget({
            type: "bi.branch_tree",
            el: {
                type: "bi.button_map"
            },
            items: [{
                text: "且",
                value: "且1",
                cls: "layout-bg7",
                children: [{
                    type: "bi.label",
                    height: 30,
                    textAlign: "left",
                    text: "这里是一段文字1",
                    value: "这里是一段文字1"
                }, {
                    text: "或",
                    value: "或2",
                    cls: "layout-bg7",
                    children: [{
                        type: "bi.label",
                        height: 30,
                        textAlign: "left",
                        text: "这里是一段文字1435",
                        value: "这里是一段文字1435"
                    }, {
                        type: "bi.label",
                        height: 30,
                        textAlign: "left",
                        text: "这里是一段文字1xx",
                        value: "这里是一段文字1xx"
                    }, {
                        text: "且",
                        value: "且3",
                        cls: "layout-bg7",
                        children: [{
                            type: "bi.label",
                            height: 30,
                            textAlign: "left",
                            text: "可以理解为一个条件",
                            value: "可以理解为一个条件"
                        }, {
                            type: "bi.label",
                            height: 30,
                            textAlign: "left",
                            text: "可以理解为一个条件v",
                            value: "可以理解为一个条件v"
                        }]
                    }]
                }, {
                    type: "bi.label",
                    height: 30,
                    textAlign: "left",
                    text: "这里是一段文字1xa",
                    value: "这里是一段文字1xa"
                }]
            }]
        });
        return tree;
    },

    _render: function (vessel) {

        var tree = this._createBranchTree();
        var mapTree = this._createBranchMapTree();

        function getItems() {
            return [{
                text: "且",
                value: "且",
                cls: "layout-bg7",
                children: [{
                    type: "bi.label",
                    height: 30,
                    textAlign: "left",
                    text: "这里是一段文字",
                    value: "这里是一段文字"
                }, {
                    text: "或",
                    value: "或2",
                    cls: "layout-bg7",
                    children: [{
                        type: "bi.label",
                        height: 30,
                        textAlign: "left",
                        text: "这里是一段文字",
                        value: "这里是一段文字"
                    }, {
                        type: "bi.label",
                        height: 30,
                        textAlign: "left",
                        text: "这里是一段文字" + BI.UUID(),
                        value: "这里是一段文字" + BI.UUID()
                    }, {
                        text: "且",
                        value: "且3",
                        cls: "layout-bg7",
                        children: [{
                            type: "bi.label",
                            height: 30,
                            textAlign: "left",
                            text: "可以理解为一个条件",
                            value: "可以理解为一个条件"
                        }]
                    }]
                }, {
                    type: "bi.label",
                    height: 30,
                    textAlign: "left",
                    text: "这里是一段文字1xa",
                    value: "这里是一段文字1xa"
                }]
            }];
        }

        BI.createWidget({
            type: "bi.center",
            element: vessel,
            items: [{
                type: "bi.vtape",
                items: [{
                    el: tree
                }, {
                    height: 30,
                    el: {
                        type: "bi.button",
                        height: 30,
                        text: "getValue",
                        handler: function () {
                            BI.Msg.alert("", tree.getValue());
                        }
                    }
                }]
            }, {
                type: "bi.vtape",
                items: [{
                    el: mapTree
                }, {
                    height: 30,
                    el: {
                        type: "bi.button",
                        height: 30,
                        text: "populate",
                        handler: function () {
                            mapTree.populate(getItems());
                        }
                    }
                }, {
                    height: 30,
                    el: {
                        type: "bi.button",
                        height: 30,
                        text: "getValue",
                        handler: function () {
                            BI.Msg.alert("", mapTree.getValue());
                        }
                    }
                }]
            }]
        })
    }
});

BranchTreeModel = BI.inherit(BI.Model, {
    _init: function () {
        BranchTreeModel.superclass._init.apply(this, arguments);
    }
});