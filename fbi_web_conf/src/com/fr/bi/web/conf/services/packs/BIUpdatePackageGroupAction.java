package com.fr.bi.web.conf.services.packs;

import com.fr.base.FRContext;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;

public class BIUpdatePackageGroupAction extends
        AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "update_package_group";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String groupString = WebUtils.getHTTPRequestParameter(req, "groups");
        long userId = ServiceUtils.getCurrentUserID(req);
        updatePackageGroup(groupString, userId);

    }

    /**
     * 更新业务包分组的信息
     *
     * @param groupString 分组的信息
     * @throws Exception
     */
    public void updatePackageGroup(String groupString, long userId) throws Exception {
        if (StringUtils.isEmpty(groupString)) {
            return;
        }

        JSONObject jo = new JSONObject(groupString);
        BIConfigureManagerCenter.getPackageManager().parseGroupJSON(userId, jo);
        try {
            /**
             * Todo 保存资源
             */
//            FRContext.getCurrentEnv().writeResource(BIConfigureDataManager.getBusiPackManager().getInstance(userId));
        } catch (Exception e) {
            FRContext.getLogger().log(Level.WARNING, e.getMessage(), e);
        }
    }
}