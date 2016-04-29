/**
 * 有总页数的分页控件
 *
 * Created by GUY on 2015/9/8.
 * @class BI.AllPagger
 * @extends BI.Widget
 */
BI.AllPagger = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AllPagger.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-all-pager",
            width: 100,
            height: 25,
            pages: 1, //必选项
            curr: 1 //初始化当前页， pages为数字时可用
        })
    },
    _init: function () {
        BI.AllPagger.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.currentPage = o.curr;
        this.editor = BI.createWidget({
            type: "bi.small_text_editor",
            validationChecker: function (v) {
                return BI.isPositiveInteger(v);
            },
            hgap: 4,
            vgap: 0,
            value: o.curr,
            errorText: BI.i18nText("BI-Please_Input_Integer"),
            width: 30,
            height: 20
        });
        this.pager = BI.createWidget({
            type: "bi.pager",
            layouts: [{
                type: "bi.horizontal",
                hgap: 1,
                vgap: 1
            }],

            dynamicShow: false,
            pages: o.pages,
            curr: o.curr,
            groups: 0,

            first: false,
            last: false,
            prev: {
                type: "bi.icon_button",
                value: "prev",
                title: BI.i18nText("BI-Previous_Page"),
                warningTitle: BI.i18nText("BI-Current_Is_First_Page"),
                height: 20,
                cls: "all-pager-prev column-pre-page-h-font"
            },
            next: {
                type: "bi.icon_button",
                value: "next",
                title: BI.i18nText("BI-Next_Page"),
                warningTitle: BI.i18nText("BI-Current_Is_Last_Page"),
                height: 20,
                cls: "all-pager-next column-next-page-h-font"
            },

            hasPrev: o.hasPrev,
            hasNext: o.hasNext,
            firstPage: o.firstPage,
            lastPage: o.lastPage
        });

        this.editor.on(BI.TextEditor.EVENT_CONFIRM, function () {
            self.pager.setValue(self.editor.getValue());
        });
        this.pager.on(BI.Pager.EVENT_CHANGE, function () {

        });
        this.pager.on(BI.Pager.EVENT_AFTER_POPULATE, function () {
            self.editor.setValue(self.pager.getCurrentPage());
            if (self.getCurrentPage() !== self.pager.getCurrentPage()) {
                self.currentPage = self.pager.getCurrentPage();
                self.fireEvent(BI.AllPagger.EVENT_CHANGE);
            }
        });

        this.allPages = BI.createWidget({
            type: "bi.label",
            text: "/" + o.pages
        });

        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [this.editor, this.allPages, this.pager]
        })
    },

    setAllPages: function (v) {
        this.allPages.setText("/" + v);
        this.pager.setAllPages(v);
    },

    setValue: function (v) {
        this.pager.setValue(v);
    },

    getCurrentPage: function () {
        return this.currentPage;
    },

    populate: function () {
        this.pager.populate();
    }
});
BI.AllPagger.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.all_pager", BI.AllPagger);