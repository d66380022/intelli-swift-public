BI.TextIconComboPopup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TextIconComboPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi.text-icon-popup",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE
        });
    },

    _init: function () {
        BI.TextIconComboPopup.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.popup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: BI.createItems(o.items, {
                type: "bi.single_select_item",
                height: 30
            }),
            chooseType: o.chooseType,
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.popup.on(BI.Controller.EVENT_CHANGE, function (type, val, obj) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            if (type === BI.Events.CLICK) {
                self.fireEvent(BI.TextIconComboPopup.EVENT_CHANGE, val, obj);
            }
        })
    },

    populate: function(items){
        items = BI.createItems(items, {
            type: "bi.single_select_item",
            height: 30
        });
        this.popup.populate(items);
    },

    getValue: function () {
        return this.popup.getValue();
    },

    setValue: function (v) {
        this.popup.setValue(v);
    }

});
BI.TextIconComboPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.text_icon_combo_popup", BI.TextIconComboPopup);