/**
 *
 * 表格
 *
 * Created by GUY on 2015/9/22.
 * @class BI.PageTableCell
 * @extends BI.Single
 */
BI.PageTableCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.PageTableCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-page-table-cell",
            text: ""
        })
    },

    _init: function () {
        BI.PageTableCell.superclass._init.apply(this, arguments);
        var label = BI.createWidget({
            type: "bi.label",
            element: this.element,
            textAlign: "left",
            whiteSpace: "nowrap",
            height: this.options.height,
            text: this.options.text,
            value: this.options.value,
            lgap: 5
        });
    }
});

$.shortcut("bi.page_table_cell", BI.PageTableCell);