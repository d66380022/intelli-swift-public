/**
 * @class BI.ChartTypeShow
 * @extend BI.Widget
 * 选择图表类型组
 */
BI.ChartTypeShow = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ChartTypeShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-type"
        })
    },

    _init: function () {
        BI.ChartTypeShow.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tableCombo = BI.createWidget({
            type: "bi.table_type_combo_show",
            width: 40,
            height: 34,
            items: [BICst.TABLE_TYPE]
        });
        this.tableCombo.on(BI.TableTypeComboShow.EVENT_CHANGE, function (v) {
            self.setValue(v);
            self.fireEvent(BI.ChartTypeShow.EVENT_CHANGE, arguments);
        });
        this.chartGroup = BI.createWidget({
            type: "bi.button_group",
            scrollable: false,
            items: BI.createItems(BICst.CHART_TYPE, {
                type: "bi.icon_button",
                iconHeight: 25,
                iconWidth: 25,
                width: 40,
                height: 34,
                extraCls: "chart-type-icon"
            }),
            layouts: [{
                type: "bi.horizontal",
                scrollx: false,
                hgap: 3
            }]
        });
        this.chartGroup.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self.setValue(v);
            self.fireEvent(BI.ChartTypeShow.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.horizontal",
            element: this.element,
            scrollx: false,
            scrollable: false,
            items: [this.tableCombo, this.chartGroup],
            vgap: 3,
            hgap: 3
        })
    },

    getValue: function () {
        if (this.tableCombo.isSelected()) {
            return this.tableCombo.getValue();
        } else {
            return this.chartGroup.getValue()[0];
        }
    },

    setValue: function (v) {
        this.tableCombo.setValue(v);
        this.chartGroup.setValue(v);
    }
});
BI.ChartTypeShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_type_show", BI.ChartTypeShow);
