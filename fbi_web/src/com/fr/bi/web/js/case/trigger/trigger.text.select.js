/**
 * 选择字段trigger
 *
 * Created by GUY on 2015/9/15.
 * @class BI.SelectTextTrigger
 * @extends BI.Trigger
 */
BI.SelectTextTrigger = BI.inherit(BI.Trigger, {

    _defaultConfig: function () {
        return BI.extend(BI.SelectTextTrigger.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-text-trigger",
            height: 25
        });
    },

    _init: function () {
        BI.SelectTextTrigger.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.text_trigger",
            element: this.element,
            height: o.height - 2,
            text: o.text
        });
    },

    setValue: function (vals) {
        vals = BI.isArray(vals) ? vals : [vals];
        var result = [];
        BI.each(this.options.items, function (i, item) {
            if (vals.contains(item.value)) {
                result.push(item.text || item.value);
            }
        });
        this.trigger.setText(result.join(","));
    },

    populate: function (items) {
        this.options.items = items;
    }
});
$.shortcut("bi.select_text_trigger", BI.SelectTextTrigger);