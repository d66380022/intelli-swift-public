package com.fr.bi.h5.services;


import com.fr.fs.FSContext;
import com.fr.fs.base.FSManager;
import com.fr.fs.privilege.auth.FSAuthentication;
import com.fr.fs.privilege.auth.FSAuthenticationManager;
import com.fr.fs.web.service.AbstractFSAuthService;
import com.fr.general.ComparatorUtils;
import com.fr.privilege.base.PrivilegeVote;
import com.fr.stable.fun.Service;
import com.fr.web.core.ActionCMD;
import com.fr.web.core.WebActionsDispatcher;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BI数据源配置
 *
 * @author Daniel-pc
 */
public class Service4BIH5 implements Service {


    private static ActionCMD[] actions = {
            new EmbResourceService(),
            new H5InitAction()
    };

    @Override
    public String actionOP() {
        return "fr_bi_h5";
    }

    /**
     * 处理HTTP请求
     *
     * @param req       HTTP请求
     * @param res       HTTP响应
     * @param op        op参数值
     * @param sessionID 当前广义报表对象的会话ID
     * @throws Exception
     */
    @Override
    public void process(HttpServletRequest req, HttpServletResponse res,
                        String op, String sessionID) throws Exception {
        FSContext.initData();
        res.setHeader("Pragma", "No-cache");
        res.setHeader("Cache-Control", "no-cache, no-store");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setDateHeader("Expires", -10);

//        PrivilegeVote vote = getFSVote(req, res);
//        FSAuthentication authentication = FSAuthenticationManager.exAuth4FineServer(req);

//        if (!vote.isPermitted() && (authentication == null || !authentication.isRoot())) {
//            vote.action(req, res);
//        } else {
        WebActionsDispatcher.dealForActionCMD(req, res, sessionID, actions);
//        }
    }

    private PrivilegeVote getFSVote(HttpServletRequest req, HttpServletResponse res) throws Exception {
        FSAuthentication authen = FSAuthenticationManager.exAuth4FineServer(req);
        if (authen == null) {
            //b:to improve
            AbstractFSAuthService.dealCookie(req, res);
            authen = FSAuthenticationManager.exAuth4FineServer(req);
        }
        return FSManager.getFSKeeper().access(authen);
    }
}