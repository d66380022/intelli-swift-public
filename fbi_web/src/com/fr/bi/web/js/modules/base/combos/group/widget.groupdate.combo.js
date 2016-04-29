/**
 * @class BI.GroupDateCombo
 * @extend BI.Widget
 */
BI.GroupDateCombo = BI.inherit(BI.Widget, {

    _defaultItems:function(){
        return [
            [{
                text: BI.i18nText("BI-Date"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.DATE
            }, {
                text: BI.i18nText("BI-Year_Fen"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.YEAR
            }, {
                text: BI.i18nText("BI-Quarter"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.QUARTER
            }, {
                text: BI.i18nText("BI-Month_Fen"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.MONTH
            }, {
                text: BI.i18nText("BI-Week_XingQi"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.WEEK
            }],
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.STATISTICS_GROUP_DATE_COMBO.DELETE
            }]
        ];
    },

    _defaultConfig: function(){
        return BI.extend(BI.GroupDateCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-group-date-combo"
        })
    },

    _init: function(){
        BI.GroupDateCombo.superclass._init.apply(this, arguments);

        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items:this._defaultItems()
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.GroupDateCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function(){
            var selectedValue = self._createValueByGroup(o.dimension.group);
            this.setValue([selectedValue]);
        });
    },

    _assertGroup:function(val){
        val || (val = {});
        val.type || (val.type = BICst.GROUP.M);
        return val;
    },

    _createValueByGroup: function(group){
        group = this._assertGroup(group);

        var groupValue = {};

        switch(group.type){
            case BICst.GROUP.YMD:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.DATE;
                break;
            case BICst.GROUP.Y:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.YEAR;
                break;
            case BICst.GROUP.S:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.QUARTER;
                break;
            case BICst.GROUP.M:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.MONTH;
                break;
            case BICst.GROUP.W:
                groupValue.value = BICst.STATISTICS_GROUP_DATE_COMBO.WEEK;
                break;
        }
        return groupValue;
    },

    getValue:function(){
        this.combo.getValue();
    }
});

BI.GroupDateCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.group_date_combo", BI.GroupDateCombo);