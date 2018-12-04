package com.fr.swift.service.handler.indexing.rule;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.IndexingSelectRule;
import com.fr.swift.config.bean.ServerCurrentStatus;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/16
 */
@SwiftBean(name = "defaultIndexingSelectRule")
public class DefaultIndexingSelectRule implements IndexingSelectRule {
    /**
     * 获取所有索引节点当前运行状态
     * 状况最佳的去索引
     *
     * @return
     */
    @Override
    public ServerCurrentStatus select(List<ServerCurrentStatus> statuses) {
        Collections.sort(statuses);
        return statuses.get(0);
    }
}
