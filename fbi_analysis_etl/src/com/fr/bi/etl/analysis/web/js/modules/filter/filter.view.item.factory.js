/**
 * Created by 小灰灰 on 2016/3/16.
 */
BI.ETLFilterViewItemFactory = {
    _createItems : function (itemsArray, title){
        var items = [];
        if (BI.isWidget(title)){
            items.push(title);
        } else if (BI.isNotNull(title)){
            items.push(BI.createWidget({
                type : 'bi.label',
                cls : 'title',
                textAlign : 'left',
                text : title,
                title : title
            }));
        }
        BI.each(itemsArray, function(i, item){
            items.push(BI.createWidget({
                type : 'bi.label',
                cls : 'content',
                textAlign : 'left',
                text : item,
                title : item
            }))
        })
        return items;
    },

    _createMultiChooseItems : function (value, text){
        var v = value.type === BI.Selection.All ? value.assist : value.value;
        return this._createItems(v, BI.createWidget({
            type : 'bi.left_right_vertical_adapt',
            items : {
                left: [BI.createWidget({
                    type : 'bi.label',
                    cls : 'title',
                    text : text
                })],
                right: [BI.createWidget({
                    type : 'bi.label',
                    cls : 'title',
                    text : v.length +BI.i18nText("BI-Tiao") + BI.i18nText("BI-Data")
                })]
            }
        }));
    },

    _createNumberRangeValueText : function (value, fieldName){
        return value.min +( value.closemin ? '≤' : '<') + fieldName + (value.closemax ? '≤' : '<') + value.max;
    },

    _createNumberGroupText : function (filterValue){
        if (filterValue.type == BICst.ETL_FILTER_NUMBER_VALUE.SETTED){
            return filterValue.value;
        }
        return filterValue.value.type == BICst.ETL_FILTER_NUMBER_AVG_TYPE.ALL ? BI.i18nText("BI-ETL_Number_Avg_All") :  BI.i18nText("BI-ETL_Number_Avg_Inner");
    },

    _createNumberNTitleText : function (filterValue){
        return filterValue.type == BICst.ETL_FILTER_NUMBER_N_TYPE.ALL ?  BI.i18nText("BI-ETL_Number_N_All") + ' :' : BI.i18nText("BI-ETL_Number_N_Inner") + ' :';
    },

    _createDateRangeValueText : function (value, fieldName){
        return this._getDateText(value.start) +'≤' + fieldName + '≤' + this._getDateText(value.end);
    },

    _getDateText : function(date){
        if (BI.isNotNull(date)){
            return date.value.year + "-" + (date.value.month + 1) + "-" + date.value.day;
        }
        return '-';
    },

    createViewItems : function (value, fieldName, fieldItems){
        var type = value.filter_type, filterValue = value.filter_value;
        switch (type){
            case BICst.FILTER_TYPE.FORMULA :
                return this._createItems([BI.Utils.getTextFromFormulaValue(filterValue, fieldItems)], BI.i18nText("BI-Formula"));
            case BICst.TARGET_FILTER_STRING.BELONG_VALUE :
                return this._createMultiChooseItems(filterValue, BI.i18nText("BI-In"));
            case BICst.TARGET_FILTER_STRING.NOT_BELONG_VALUE :
                return this._createMultiChooseItems(filterValue, BI.i18nText("BI-Not_In"));
            case BICst.TARGET_FILTER_STRING.CONTAIN :
                return this._createItems([filterValue], BI.i18nText("BI-Contain"));
            case BICst.TARGET_FILTER_STRING.NOT_CONTAIN :
                return this._createItems([filterValue], BI.i18nText("BI-Not_Contain"));
            case BICst.TARGET_FILTER_STRING.BEGIN_WITH :
                return this._createItems([filterValue], BI.i18nText("BI-Begin_With"));
            case BICst.TARGET_FILTER_STRING.NOT_BEGIN_WITH :
                return this._createItems([filterValue], BI.i18nText("BI-Not_Begin_With"));
            case BICst.TARGET_FILTER_STRING.END_WITH :
                return this._createItems([filterValue], BI.i18nText("BI-End_With"));
            case BICst.TARGET_FILTER_STRING.NOT_END_WITH :
                return this._createItems([filterValue], BI.i18nText("BI-Not_End_With"));
            case BICst.TARGET_FILTER_NUMBER.CONTAINS :
            case BICst.FILTER_DATE.CONTAINS:
                return this._createMultiChooseItems(filterValue, BI.i18nText("BI-ETL_Filter_Belongs"));
            case BICst.TARGET_FILTER_NUMBER.BELONG_VALUE :
                return this._createItems([this._createNumberRangeValueText(filterValue, fieldName)], BI.i18nText("BI-ETL_Number_IN"));
            case BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE :
                return this._createItems([this._createNumberRangeValueText(filterValue, fieldName)], BI.i18nText("BI-Not") + BI.i18nText("BI-ETL_Number_IN"));
            case BICst.TARGET_FILTER_NUMBER.EQUAL_TO :
                return this._createItems([fieldName + ' = ' + filterValue]);
            case  BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO :
                return this._createItems([fieldName + '≠'+ filterValue]);
            case BICst.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE :
                return this._createItems([fieldName + (filterValue.close ? ' ≥' : ' >') + this._createNumberGroupText(filterValue)]);
            case BICst.TARGET_FILTER_NUMBER.SMALL_OR_EQUAL_CAL_LINE :
                return this._createItems([fieldName + (filterValue.close ? ' ≤' : ' <') + this._createNumberGroupText(filterValue)]);
            case  BICst.TARGET_FILTER_NUMBER.TOP_N :
                return this._createItems([BI.i18nText("BI-ETL_Top_N", filterValue.value)], this._createNumberNTitleText(filterValue));
            case  BICst.TARGET_FILTER_NUMBER.BOTTOM_N :
                return this._createItems([BI.i18nText("BI-ETL_Bottom_N", filterValue.value)], this._createNumberNTitleText(filterValue));
            case BICst.FILTER_DATE.BELONG_DATE_RANGE :
                return this._createItems([this._createDateRangeValueText(filterValue, fieldName)], BI.i18nText("BI-ETL_Date_In_Range"));
            case BICst.FILTER_DATE.MORE_THAN :
                return this._createItems([fieldName + '≥' + this._getDateText(filterValue)], BI.i18nText("BI-More_Than") + ' :');
            case BICst.FILTER_DATE.LESS_THAN :
                return this._createItems([fieldName + '≤' +this._getDateText(filterValue)], BI.i18nText("BI-Less_Than") + ' :');
            case BICst.FILTER_DATE.EQUAL_TO :
                return this._createItems([fieldName + ' = ' +this._getDateText(filterValue)]);
            case BICst.FILTER_DATE.NOT_EQUAL_TO :
                return this._createItems([fieldName + '≠' +this._getDateText(filterValue)]);
            default :
                return[];
        }
    }
}
