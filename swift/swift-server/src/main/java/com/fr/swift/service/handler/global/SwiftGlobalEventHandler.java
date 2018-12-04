package com.fr.swift.service.handler.global;

import com.fr.event.EventDispatcher;
import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cluster.entity.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.Where;
import com.fr.swift.event.analyse.SegmentLocationRpcEvent;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.BaseService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.handler.SwiftServiceHandlerManager;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.service.handler.history.HistoryDataSyncManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.impl.TaskEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/27
 */
@SwiftBean
public class SwiftGlobalEventHandler extends AbstractHandler<AbstractGlobalRpcEvent> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftGlobalEventHandler.class);
    private SwiftClusterSegmentService segmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
    private SwiftMetaDataService swiftMetaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);
    private HistoryDataSyncManager historyDataSyncManager = SwiftContext.get().getBean(HistoryDataSyncManager.class);

    @Override
    public <S extends Serializable> S handle(AbstractGlobalRpcEvent event) throws Exception {
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        switch (event.subEvent()) {
            case TASK_DONE:
                Pair<TaskKey, TaskResult> pair = (Pair<TaskKey, TaskResult>) event.getContent();
                EventDispatcher.fire(TaskEvent.DONE, pair);
                break;
            case CLEAN:
                String[] sourceKeys = (String[]) event.getContent();
                try {
                    if (null != sourceKeys) {
                        factory.getProxy(BaseService.class).cleanMetaCache(sourceKeys);
                    }
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                break;
            case PUSH_SEG:
                SegmentLocationInfo info = (SegmentLocationInfo) event.getContent();
                SwiftServiceHandlerManager.getManager().
                        handle(new SegmentLocationRpcEvent(SegmentLocationInfo.UpdateType.PART, info));
                break;
            case GET_ANALYSE_REAL_TIME:
                Map<String, ClusterEntity> realtime = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
                Map<String, ClusterEntity> analyse = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
                Map<ServiceType, List<String>> result = new HashMap<ServiceType, List<String>>();
                makeResultMap(realtime, result, ServiceType.REAL_TIME);
                makeResultMap(analyse, result, ServiceType.ANALYSE);
                return (S) result;
            case DELETE:
                Pair<SourceKey, Where> content = (Pair<SourceKey, Where>) event.getContent();
                SourceKey sourceKey = content.getKey();
                Where where = content.getValue();
//                Map<String, ClusterEntity> realTimeServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);

                RealtimeService realtimeService = factory.getProxy(RealtimeService.class);
                realtimeService.delete(sourceKey, where, new ArrayList<String>());
                HistoryService historyService = factory.getProxy(HistoryService.class);
                historyService.delete(sourceKey, where, new ArrayList<String>());
//                dealDelete(sourceKey, where, realTimeServices, "realtimeDelete");
//                Map<String, ClusterEntity> historyServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
//                Iterator<Map.Entry<String, ClusterEntity>> iterator = historyServices.entrySet().iterator();
//                while (iterator.hasNext()) {
//                    if (realTimeServices.containsKey(iterator.next().getKey())) {
//                        iterator.remove();
//                    }
//                }
//                dealDelete(sourceKey, where, historyServices, "historyDelete");
                break;
            case TRUNCATE:
                String truncateSourceKey = (String) event.getContent();
                ProxySelector.getInstance().getFactory().getProxy(HistoryService.class).truncate(truncateSourceKey);
            default:
                break;
        }
        return null;
    }

//    private void dealDelete(SourceKey sourceKey, Where where, Map<String, ClusterEntity> services, String method) throws Exception {
//        if (null == services || services.isEmpty()) {
//            SwiftLoggers.getLogger().warn("Cannot find services");
//            return;
//        }
//        List<String> uploadedSegments = new ArrayList<String>();
//        for (Map.Entry<String, ClusterEntity> entry : services.entrySet()) {
//            String clusterId = entry.getKey();
//            List<SegmentKey> segmentKeys = segmentService.getOwnSegments(clusterId).get(sourceKey.getId());
//            List<String> needUploadSegs = new ArrayList<String>();
//            if (null != segmentKeys) {
//                for (SegmentKey segmentKey : segmentKeys) {
//                    if (segmentKey.getStoreType() == Types.StoreType.FINE_IO) {
//                        String segKey = segmentKey.toString();
//                        // 如果不包含就放到需要上传的list
//                        if (!uploadedSegments.contains(segKey)) {
//                            needUploadSegs.add(segKey);
//                            uploadedSegments.add(segKey);
//                        }
//                    }
//                }
//                runAsyncRpc(clusterId, entry.getValue().getServiceClass(), method, sourceKey, where, needUploadSegs);
//            }
//        }
//    }

    private void makeResultMap(Map<String, ClusterEntity> realtime, Map<ServiceType, List<String>> result, ServiceType type) {
        for (String id : realtime.keySet()) {
            if (result.get(type) == null) {
                result.put(type, new ArrayList<String>());
            }
            result.get(type).add(id);
        }
    }

}
