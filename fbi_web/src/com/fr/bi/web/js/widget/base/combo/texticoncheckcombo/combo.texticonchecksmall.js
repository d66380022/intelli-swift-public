/**
 * @class BI.SmallTextIconCheckCombo
 * @extend BI.Widget
 * combo : text + icon, popup : check + text
 */
BI.SmallTextIconCheckCombo = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.SmallTextIconCheckCombo.superclass._defaultConfig.apply(this, arguments), {
            width: 100,
            height: 22,
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE
        })
    },

    _init: function () {
        BI.SmallTextIconCheckCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.small_select_text_trigger",
            items: o.items,
            height: o.height
        });
        this.popup = BI.createWidget({
            type: "bi.text_icon_check_combo_popup",
            chooseType: o.chooseType,
            items: o.items
        });
        this.popup.on(BI.TextIconCheckComboPopup.EVENT_CHANGE, function () {
            self.setValue(self.popup.getValue());
            self.SmallTextIconCheckCombo.hideView();
            self.fireEvent(BI.SmallTextIconCheckCombo.EVENT_CHANGE);
        });
        this.popup.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.SmallTextIconCheckCombo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            adjustLength: 2,
            el: this.trigger,
            popup: {
                el: this.popup,
                maxWidth: "",
                maxHeight: 300
            }
        });
    },

    setValue: function (v) {
        this.SmallTextIconCheckCombo.setValue(v);
    },

    setEnable: function (v) {
        BI.SmallTextIconCheckCombo.superclass.setEnable.apply(this, arguments);
        this.SmallTextIconCheckCombo.setEnable(v);
    },

    getValue: function () {
        return this.SmallTextIconCheckCombo.getValue();
    },

    populate: function (items) {
        this.options.items = items;
        this.SmallTextIconCheckCombo.populate(items);
    }
});
BI.SmallTextIconCheckCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.small_text_icon_check_combo", BI.SmallTextIconCheckCombo);