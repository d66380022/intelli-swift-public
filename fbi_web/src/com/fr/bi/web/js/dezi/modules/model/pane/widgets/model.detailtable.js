BIDezi.DetailTableModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.DetailTableModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds:{},
            type: BICst.Widget.DETAIL,
            dimensions: {},
            view: {},
            page: 0,
            settings: BICst.DEFAULT_CHART_SETTING
        })
    },

    change: function (changed) {
        if(BI.has(changed, "detail")){
            this.set(this.get("detail"));
        }
    },

    refresh: function () {
        this.tmp({
            detail: {
                name: this.get("name"),
                dimensions: this.get("dimensions"),
                view: this.get("view"),
                type: this.get("type"),
                page: this.get("page"),
                settings: this.get("settings")
            }
        }, {
            silent: true
        });
    },

    local: function () {
        return false;
    },

    _init: function () {
        BIDezi.DetailTableModel.superclass._init.apply(this, arguments);
    }
});